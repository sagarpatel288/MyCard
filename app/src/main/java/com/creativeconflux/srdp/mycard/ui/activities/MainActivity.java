package com.creativeconflux.srdp.mycard.ui.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.FrameLayout;

import com.creativeconflux.srdp.mycard.InitialFragment;
import com.creativeconflux.srdp.mycard.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.layout_frame_container)
    FrameLayout layoutFrameContainer;

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
        ButterKnife.bind(this);
        setToolbar();
        commitFragmentTransaction(new InitialFragment(), false, null);
    }

    private void setToolbar() {
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
