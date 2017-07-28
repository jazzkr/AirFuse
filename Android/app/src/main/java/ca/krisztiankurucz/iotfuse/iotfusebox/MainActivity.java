package ca.krisztiankurucz.iotfuse.iotfusebox;

import android.graphics.Color;
import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import android.os.Handler;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        OverviewFragment.OnFragmentInteractionListener,
        NotificationFragment.OnFragmentInteractionListener,
        FuseFragment.OnFragmentInteractionListener,
        ActionsFragment.OnFragmentInteractionListener,
        SettingsFragment.OnFragmentInteractionListener,
        HistoryTestFragment.OnListFragmentInteractionListener
{

    public static final String fusebox_id = "14";

    //public static ArrayList<FuseObject> fuse_list = new ArrayList<>();
    public static HashMap<String,FuseObject> fuse_map = new HashMap<>();

    public static boolean first_run = true;

    public static FuseObject getFuseByName(String fname)
    {
        for (String sf: fuse_map.keySet())
        {
            FuseObject fo = fuse_map.get(sf);
            if (fo.name.equals(fname))
            {
                return fo;
            }
        }
        return null;
    }

    public static FuseObject getFuseById(int fid)
    {
        for (String sf: fuse_map.keySet())
        {
            FuseObject fo = fuse_map.get(sf);
            if (fo.id == fid)
            {
                return fo;
            }
        }
        return null;
    }

    //Fuse status update timer
    // Create the Handler object (on the main thread by default)
    Handler handler = new Handler();
    // Define the code block to be executed
    private Runnable pullFuseStatus = new Runnable() {
        @Override
        public void run() {
            System.out.println("Running fuse update on main thread!");
            for(String sf: fuse_map.keySet())
            {
                FuseObject fo = fuse_map.get(sf);
                getFuseStatus(fo);
            }
            ActionsFragment af = (ActionsFragment)getSupportFragmentManager().findFragmentByTag("ACTIONS_FRAGMENT");
            if (af != null && af.isVisible()) {
                af.refreshActionFragment(findViewById(android.R.id.content));
            }
            OverviewFragment of = (OverviewFragment)getSupportFragmentManager().findFragmentByTag("OVERVIEW_FRAGMENT");
            if (of != null && of.isVisible() && MainActivity.first_run) {
                of.updateOverviewChart();
                if (MainActivity.first_run)
                {
                    MainActivity.first_run = false;
                }
            }
            handler.postDelayed(this, 5000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Default push overview fragment
        Fragment fragment = new OverviewFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, fragment, "OVERVIEW_FRAGMENT")
                .commit();

        setTitle("Overview");

        // Load initial
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="http://django.utkarshsaini.com/AirFuse/fuse/" + fusebox_id + "/";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        System.out.println("[HTTPGET] Response is: "+ response);
                        try
                        {
                            JSONArray fuses = new JSONArray(response);
                            for (int i = 0; i < fuses.length(); i++) {
                                JSONObject f = fuses.getJSONObject(i);
                                int id = f.getInt("id");
                                String name = f.getString("name");
                                String desc = f.getString("desc");
                                double current_limit = f.getDouble("current_limit");

                                FuseObject f_obj = new FuseObject(id,name,desc,current_limit);
                                //fuse_list.add(f_obj);
                                fuse_map.put(f_obj.name, f_obj);
                                //System.out.println(f_obj.id);
                                //System.out.println(f_obj.name);
                                //System.out.println(f_obj.desc);
                                //System.out.println(f_obj.current_limit);
                            }
                            for (String f_name: fuse_map.keySet())
                            {
                                System.out.println(fuse_map.get(f_name).name);
                            }
                        } catch(Exception e)
                        {
                            e.printStackTrace();
                        }
                        //mTextView.setText("Response is: "+ response.substring(0,500));
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("That didn't work!");
                //mTextView.setText("That didn't work!");
            }
        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);

        // Start the initial runnable task by posting through the handler
        handler.post(pullFuseStatus);

        ((OverviewFragment)fragment).updateOverviewChart();
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(pullFuseStatus);
    }

    @Override
    public void onResume() {
        super.onResume();
        handler.post(pullFuseStatus);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_overview) {
            // Create overview fragment
            Fragment fragment = new OverviewFragment();
            Bundle args = new Bundle();
            //args.putInt(PlanetFragment.ARG_PLANET_NUMBER, position);
            fragment.setArguments(args);

            // Insert the fragment by replacing any existing fragment
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, fragment, "OVERVIEW_FRAGMENT")
                    .commit();
            setTitle("Overview");

        } else if (id == R.id.nav_fuse_1) {
            // Create fuse fragment
            Fragment fragment = new FuseFragment();
            Bundle args = new Bundle();
            //args.putInt(PlanetFragment.ARG_PLANET_NUMBER, position);
            args.putString("graph_title", "Fuse 1 Consumption");
            args.putString("fuse_name", "Fuse 1");
            fragment.setArguments(args);

            // Insert the fragment by replacing any existing fragment
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, fragment)
                    .commit();
            setTitle("Fuse 1");

        } else if (id == R.id.nav_fuse_2) {
            // Create fuse fragment
            Fragment fragment = new FuseFragment();
            Bundle args = new Bundle();
            //args.putInt(PlanetFragment.ARG_PLANET_NUMBER, position);
            args.putString("graph_title", "Fuse 2 Consumption");
            args.putString("fuse_name", "Fuse 2");
            fragment.setArguments(args);

            // Insert the fragment by replacing any existing fragment
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, fragment)
                    .commit();
            setTitle("Fuse 2");

        } else if (id == R.id.nav_fuse_3) {
            // Create fuse fragment
            Fragment fragment = new FuseFragment();
            Bundle args = new Bundle();
            //args.putInt(PlanetFragment.ARG_PLANET_NUMBER, position);
            args.putString("graph_title", "Fuse 3 Consumption");
            args.putString("fuse_name", "Fuse 3");
            fragment.setArguments(args);

            // Insert the fragment by replacing any existing fragment
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, fragment)
                    .commit();
            setTitle("Fuse 3");

        } else if (id == R.id.nav_history) {
            // Create history fragment
            Fragment fragment = new HistoryTestFragment();
            Bundle args = new Bundle();
            //args.putInt(PlanetFragment.ARG_PLANET_NUMBER, position);
            fragment.setArguments(args);

            // Insert the fragment by replacing any existing fragment
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, fragment)
                    .commit();
            setTitle("History");

        } else if (id == R.id.nav_control) {
            // Create actions fragment
            Fragment fragment = new ActionsFragment();
            Bundle args = new Bundle();
            //args.putInt(PlanetFragment.ARG_PLANET_NUMBER, position);
            fragment.setArguments(args);

            // Insert the fragment by replacing any existing fragment
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, fragment, "ACTIONS_FRAGMENT")
                    .commit();
            setTitle("Fuse Actions");

        } else if (id == R.id.nav_notifications) {
            // Create overview fragment
            Fragment fragment = new NotificationFragment();
            Bundle args = new Bundle();
            //args.putInt(PlanetFragment.ARG_PLANET_NUMBER, position);
            fragment.setArguments(args);

            // Insert the fragment by replacing any existing fragment
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, fragment)
                    .commit();
            setTitle("Notifications");
        } else if (id == R.id.nav_settings) {
            // Create action fragment
            Fragment fragment = new SettingsFragment();
            Bundle args = new Bundle();
            //args.putInt(PlanetFragment.ARG_PLANET_NUMBER, position);
            fragment.setArguments(args);

            // Insert the fragment by replacing any existing fragment
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, fragment)
                    .commit();
            setTitle("Settings");
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void getFuseStatus(FuseObject fuse)
    {
        final int fid = fuse.id;
        // Get fuse information
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="http://django.utkarshsaini.com/AirFuse/fuseStatus/" + Integer.toString(fuse.id);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        System.out.println("[HTTPGET] Response is: "+ response);
                        try
                        {
                            JSONArray statuses = new JSONArray(response);
                            TreeMap<Integer, String> statusMap = new TreeMap<>();
                            for (int i = 0; i < statuses.length(); i++)
                            {
                                JSONObject s = statuses.getJSONObject(i);
                                if (!s.getBoolean("seen"))
                                {
                                    setFuseSeen(fid, s.getInt("id"), s.getString("status"));
                                    //Send notification here?
                                }
                                int id = s.getInt("id");
                                String status = s.getString("status");
                                statusMap.put(id, status);
                            }
                            for (String sf: MainActivity.fuse_map.keySet())
                            {
                                FuseObject fo = MainActivity.fuse_map.get(sf);
                                if (fo.id == fid)
                                {
                                    fo.status = statusMap.lastEntry().getValue();
                                    System.out.println("Updated " + fo.name + " status to " + fo.status);
                                }

                            }
                        } catch(Exception e)
                        {
                            e.printStackTrace();
                        }
                        //mTextView.setText("Response is: "+ response.substring(0,500));
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("That didn't work!");
                //mTextView.setText("That didn't work!");
            }
        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    public void setFuseSeen(final int fuseid, final int fuseStatusId, final String fuseStatus)
    {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://django.utkarshsaini.com/AirFuse/fuseStatus/" + Integer.toString(fuseid) + "/" + Integer.toString(fuseStatusId) + "/";
        StringRequest postRequest = new StringRequest(Request.Method.PUT, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        System.out.println("Tried updating fuse seen value!");
                        System.out.println("[HTTPPOST] " + response);
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        System.out.println("That didn't work!");
                        error.printStackTrace();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("fuse", Integer.toString(fuseid));
                params.put("seen", "True");
                params.put("status", fuseStatus);
                return params;
            }
        };
        queue.add(postRequest);
    }

    @Override
    public void onOverviewFragmentInteraction(Uri uri) {

    }

    @Override
    public void onNavFragmentInteraction(Uri uri) {

    }

    @Override
    public void onFuseFragmentInteraction(Uri uri) {

    }

    @Override
    public void onActionsFragmentInteraction(Uri uri) {

    }

    @Override
    public void onSettingsFragmentInteraction(Uri uri) {

    }

    @Override
    public void onHistoryListFragmentInteraction(HistoryContent.HistoryItem item) {

    }
}
