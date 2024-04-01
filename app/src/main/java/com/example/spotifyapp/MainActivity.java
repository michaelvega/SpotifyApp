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


public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawer = findViewById(R.id.drawer_view);
        NavigationView navigationView = findViewById(R.id.nav_view);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        // Setup drawer view
        navigationView.setNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.login_item) {// Handle navigation
                Intent intent = new Intent(MainActivity.this, login.class);
                startActivity(intent);

                drawer.closeDrawer(GravityCompat.START);
                return true;
            } else if (item.getItemId() == R.id.home_item) {
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
            return false;
        });



    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            drawer.openDrawer(GravityCompat.START);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}