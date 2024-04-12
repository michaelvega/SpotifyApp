package com.example.spotifyapp;

import android.os.Bundle;

public class account extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account);

        initializeDrawer();

    }
}
