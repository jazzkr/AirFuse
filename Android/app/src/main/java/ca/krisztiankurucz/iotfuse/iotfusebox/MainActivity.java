package ca.krisztiankurucz.iotfuse.iotfusebox;

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

import java.util.ArrayList;
import java.util.HashMap;

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
    //public static FuseObject fuse_1;
    //public static FuseObject fuse_2;
    //public static FuseObject fuse_3;


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
                .replace(R.id.content_frame, fragment)
                .commit();

        setTitle("Overview");

        // Load fuse information for later
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
                    .replace(R.id.content_frame, fragment)
                    .commit();
            setTitle("Overview");

        } else if (id == R.id.nav_fuse_1) {
            // Create fuse fragment
            Fragment fragment = new FuseFragment();
            Bundle args = new Bundle();
            //args.putInt(PlanetFragment.ARG_PLANET_NUMBER, position);
            args.putString("graph_title", "Fuse 1 Consumption Graph");
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
            args.putString("graph_title", "Fuse 2 Consumption Graph");
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
            args.putString("graph_title", "Fuse 3 Consumption Graph");
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
                    .replace(R.id.content_frame, fragment)
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
