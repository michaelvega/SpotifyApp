package com.example.spotifyapp;
import com.google.android.material.navigation.NavigationView;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import com.google.android.material.navigation.NavigationView;


import android.content.Intent;
import android.os.Bundle;

import android.view.MenuItem;

public abstract class BaseActivity extends AppCompatActivity {

    protected DrawerLayout drawer;
    protected Toolbar toolbar;
    protected NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void initializeDrawer() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.login_item && !(this instanceof login)) {
                Intent intent = new Intent(this, login.class);
                startActivity(intent);
            } else if (itemId == R.id.home_item && !(this instanceof MainActivity)) {
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            } else if (itemId == R.id.portal_item && !(this instanceof portal)) {
                Intent intent = new Intent(this, portal.class);
                startActivity(intent);
            }
            if ((itemId == R.id.login_item && !(this instanceof login)) ||
                    (itemId == R.id.home_item && !(this instanceof MainActivity))
                    || (itemId == R.id.portal_item && !(this instanceof portal)) ) {
                drawer.closeDrawer(GravityCompat.START);
            }
            return true;
        });
    }
}