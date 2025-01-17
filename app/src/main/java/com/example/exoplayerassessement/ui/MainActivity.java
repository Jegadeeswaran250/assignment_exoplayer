package com.example.exoplayerassessement.ui;

import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.exoplayerassessement.Network.NetworkStateChangeReceiver;
import com.example.exoplayerassessement.R;

public class MainActivity extends AppCompatActivity {
    private DashBoardFragment fragment_Dashboard;
    private PlayerFragment playerFragment;
    private NetworkStateChangeReceiver networkChangeReceiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        networkChangeReceiver = new NetworkStateChangeReceiver();
        fragment_Dashboard=new DashBoardFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.player_activity,fragment_Dashboard).commit();
    }

    //Initiate PlayerFragment with Dash contentType
    public void playDashContent(){
        playerFragment=new PlayerFragment();
        Bundle bundle = new Bundle();
        bundle.putString("contentType", "Dash");
        playerFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.player_activity,playerFragment)
                .addToBackStack("VodPlayer")
                .commit();
    }

    //Initiate PlayerFragment with HLS contentType
    public void playHlsContent(){
        playerFragment=new PlayerFragment();
        Bundle bundle = new Bundle();
        bundle.putString("contentType", "Hls");
        playerFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.player_activity,playerFragment)
                .addToBackStack("VodPlayer")
                .commit();
    }
    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeReceiver, filter);
    }
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(networkChangeReceiver);
    }
}