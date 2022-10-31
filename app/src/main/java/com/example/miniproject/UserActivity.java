package com.example.miniproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;

public class UserActivity extends AppCompatActivity {

    TextView tv;

    Fragment fragment = null;
    FragmentTransaction fragmentTransaction;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId()) {
                case R.id.ongoingFragment:
                    fragment = new OngoingFragment();
                    switchFragment(fragment);
                    return true;
                case R.id.pastFragment:
                    fragment = new upcomingFragment();
                    switchFragment(fragment);
                    return true;
                case R.id.upcomingFragment:
                    fragment = new PastFragment();
                    switchFragment(fragment);
                    return true;
                case R.id.Logout:
                    logoutUser();
                    return true;

            }
            return false;
        }
    };


    private void switchFragment(Fragment fragment) {

        FrameLayout fl = (FrameLayout) findViewById(R.id.nav_fragment);
        fl.removeAllViews();


        fragmentTransaction = getSupportFragmentManager().beginTransaction();

        fragmentTransaction.replace(R.id.nav_fragment, fragment);
        //fragmentTransaction.remove(fragment);
        //fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void logoutUser(){
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this,LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        FloatingActionButton addBtn = (FloatingActionButton) findViewById(R.id.floating_action_button);

        if(!Config.user.getStatus().equals("admin")) {
            addBtn.setVisibility(View.GONE);
        }

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Config.user.getStatus().equals("admin")) {
                    startActivity(new Intent(UserActivity.this, CreateTournamentActivity.class));
                }
            }
        });


        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.bottom_navigatin_view);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        //navigation.setSelectedItemId(R.id.navigation_dashboard);


    }


}