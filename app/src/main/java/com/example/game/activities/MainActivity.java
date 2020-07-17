package com.example.game.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.game.R;
import com.example.game.databinding.ActivityMainBinding;
import com.example.game.fragments.EventFeedFragment;
import com.example.game.fragments.ProfileFragment;
import com.example.game.fragments.SearchFragment;
import com.example.game.helpers.NavigationUtil;
import com.example.game.models.Community;
import com.example.game.models.Event;
import com.example.game.models.Subscription;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.parse.FindCallback;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private ActivityMainBinding binding;
    private FloatingActionButton fab;
    private Toolbar toolbar;
    private BottomNavigationView bottomNavigationView;
    private Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final FragmentManager fragmentManager = getSupportFragmentManager();
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        bottomNavigationView = binding.bottomNavigation;
        toolbar = binding.toolbar;

        bottomNavigationView.setOnNavigationItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_event:
                        fragment = EventFeedFragment.newInstance();
                        Toast.makeText(MainActivity.this, R.string.home, Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.action_search:
                        fragment = SearchFragment.newInstance();
                        Toast.makeText(MainActivity.this, R.string.search, Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        fragment = ProfileFragment.newInstance();
                        Toast.makeText(MainActivity.this, R.string.profile, Toast.LENGTH_SHORT).show();
                }
                fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
            }
        });

        // Set default selection
        bottomNavigationView.setSelectedItemId(R.id.action_event);

        //set a listener on the menu items
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.logout:
                        logout();
                        break;
                    case R.id.create_event:
                        Toast.makeText(MainActivity.this, R.string.create_event, Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        Toast.makeText(MainActivity.this, R.string.create_community, Toast.LENGTH_SHORT).show();
                        break;
                }
                return true;
            }
        });
    }

    private void logout() {
        ParseUser.getCurrentUser().logOutInBackground(new LogOutCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, getString(R.string.error_logout) + e);
                    Snackbar.make(toolbar, R.string.error_logout_message, BaseTransientBottomBar.LENGTH_SHORT);
                } else {
                    NavigationUtil.goToActivity(MainActivity.this, LoginActivity.class);
                }
            }
        });
    }

    public void getEvents() {
        ParseQuery<Event> qEvents = ParseQuery.getQuery(Event.class);
        qEvents.include(Event.KEY_CREATOR);
        qEvents.include(Event.KEY_COMMUNITY);
        qEvents.findInBackground(new FindCallback<Event>() {
            @Override
            public void done(List<Event> objects, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error querying events: " + e);
                } else {
                    Log.i(TAG, "Results:" + objects.size());
                }
            }
        });
    }

    public void getCommunities() {
        ParseQuery<Subscription> q = ParseQuery.getQuery(Subscription.class);
        q.whereEqualTo(Subscription.KEY_USER, ParseUser.getCurrentUser());
        q.include(Subscription.KEY_COMMUNITY);
        q.findInBackground(new FindCallback<Subscription>() {
            @Override
            public void done(List<Subscription> objects, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error querying events: " + e);
                } else {
                    Log.i(TAG, "Results:" + objects.get(0).getCommunity());
                }
            }
        });
    }

    public void createCommunity(String name) {
        Community community = new Community();
        community.setCreator(ParseUser.getCurrentUser());
        community.setName(name);
        community.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "error making a community" + e);
                } else {
                    Log.i(TAG, "Successful created a community");
                }
            }
        });
    }
}
