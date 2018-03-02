package com.creativeconflux.srdp.mycard.ui.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.FrameLayout;

import com.creativeconflux.srdp.mycard.R;
import com.creativeconflux.srdp.mycard.ui.fragments.EditFragment;

public class MainActivity extends AppCompatActivity {

    FrameLayout frameLayout;
    Toolbar toolbar;

    public Toolbar getToolbar() {
        return toolbar;
    }

    public void setToolbar(Toolbar toolbar) {
        this.toolbar = toolbar;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        frameLayout = findViewById(R.id.layout_frame_container);
        setToolbar();
        commitFragmentTransaction(new EditFragment(), false, new Bundle());
    }

    private void setToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setToolbar(toolbar);
    }

    public void commitFragmentTransaction(Fragment fragment, boolean addToBackStack, Bundle bundle) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.layout_frame_container, fragment);
        if (addToBackStack) {
            fragmentTransaction.addToBackStack(null);
        }
        fragment.setArguments(bundle);
        fragmentTransaction.commit();
    }

    public void hideToolbar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }

    public void showToolbar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().show();
        }
    }

    // FIXME: 2/24/2018 sagar : should we use separate toolbar for each fragment?
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_save, menu);
        menu.findItem(R.id.menu_item_edit).setVisible(false);
        menu.findItem(R.id.menu_item_save).setVisible(false);
        return true;
    }
}
