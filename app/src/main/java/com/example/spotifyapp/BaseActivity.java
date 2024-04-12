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

    private String mAccessToken;

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
            } else if (itemId == R.id.sign_up_item && !(this instanceof SignUp)) {
                Intent intent = new Intent(this, SignUp.class);
                startActivity(intent);
            } else if (itemId == R.id.llm_item && !(this instanceof llm)) {
                Intent intent = new Intent(this, llm.class);
                startActivity(intent);
            } else if (itemId == R.id.pastwrapped_item && !(this instanceof pastwrapped)) {
                Intent intent = new Intent(this, pastwrapped.class);
                startActivity(intent);
            } else if (itemId == R.id.account_item && !(this instanceof account)) {
                Intent intent = new Intent(this, account.class);
                startActivity(intent);
            }

            if ((itemId == R.id.login_item && !(this instanceof login)) ||
                    (itemId == R.id.home_item && !(this instanceof MainActivity))
                    || (itemId == R.id.sign_up_item && !(this instanceof SignUp))
                    || (itemId == R.id.llm_item && !(this instanceof llm))
                    || (itemId == R.id.pastwrapped_item && !(this instanceof pastwrapped))
                    || (itemId == R.id.account_item && !(this instanceof account))) {
                drawer.closeDrawer(GravityCompat.START);
            }
            return true;
        });
    }

    public String getmAccessToken() {
        return mAccessToken;
    }

    public void setmAccessToken(String mAccessToken) {
        this.mAccessToken = mAccessToken;
    }
}