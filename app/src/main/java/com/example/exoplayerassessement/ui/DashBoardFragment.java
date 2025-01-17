package com.example.exoplayerassessement.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.exoplayerassessement.Network.NetworkUtils;
import com.example.exoplayerassessement.R;

public class DashBoardFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView= inflater.inflate(R.layout.fragment_dash_board, container, false);

        //
        rootView.findViewById(R.id.btn_playdash).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(NetworkUtils.isNetworkAvailable(requireContext())){
                    MainActivity activity = (MainActivity) getActivity();
                    assert activity != null;
                    activity.playDashContent();
                }
                else{
                    Toast.makeText(requireContext(), "No network connection!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //
        rootView.findViewById(R.id.btn_playhls).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(NetworkUtils.isNetworkAvailable(requireContext())) {
                    MainActivity activity = (MainActivity) getActivity();
                    assert activity != null;
                    activity.playHlsContent();
                }
                else{
                    Toast.makeText(requireContext(), "No network connection!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    return  rootView;
    }
}