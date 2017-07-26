package ca.krisztiankurucz.iotfuse.iotfusebox;

import android.net.Uri;
import android.preference.PreferenceFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.Fragment;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import ca.krisztiankurucz.iotfuse.iotfusebox.dummy.DummyContent;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        OverviewFragment.OnFragmentInteractionListener,
        NotificationFragment.OnFragmentInteractionListener,
        FuseFragment.OnFragmentInteractionListener,
        HistoryFragment.OnFragmentInteractionListener,
        ActionsFragment.OnFragmentInteractionListener,
        SettingsFragment.OnFragmentInteractionListener,
        HistoryTestFragment.OnListFragmentInteractionListener
{


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

            // Highlight the selected item, update the title, and close the drawer
            //mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
            //mDrawerList = (ListViewCompat) findViewById(R.id.);
            //mDrawerList.setItemChecked(position, true);
            //mDrawerLayout.closeDrawer(mDrawerList);
        } else if (id == R.id.nav_fuse_1) {
            // Create fuse fragment
            Fragment fragment = new FuseFragment();
            Bundle args = new Bundle();
            //args.putInt(PlanetFragment.ARG_PLANET_NUMBER, position);
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
            // Create actiosn fragment
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
            PreferenceFragmentCompat fragment = new SettingsFragment();
            Bundle args = new Bundle();
            //args.putInt(PlanetFragment.ARG_PLANET_NUMBER, position);
            fragment.setArguments(args);

            // Insert the fragment by replacing any existing fragment
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.content, fragment)
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
    public void onHistoryFragmentInteraction(Uri uri) {

    }

    @Override
    public void onActionsFragmentInteraction(Uri uri) {

    }

    @Override
    public void onSettingsFragmentInteraction(Uri uri) {

    }

    @Override
    public void onHistoryListFragmentInteraction(DummyContent.DummyItem item) {

    }
}
