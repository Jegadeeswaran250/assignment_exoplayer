package com.example.exoplayerassessement.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.OptIn;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.fragment.app.Fragment;
import androidx.media3.common.AudioAttributes;
import androidx.media3.common.MediaItem;
import androidx.media3.common.MediaMetadata;
import androidx.media3.common.MimeTypes;
import androidx.media3.common.PlaybackException;
import androidx.media3.common.PlaybackParameters;
import androidx.media3.common.Player;
import androidx.media3.common.Timeline;
import androidx.media3.common.Tracks;
import androidx.media3.common.VideoSize;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;

import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.exoplayerassessement.R;

public class PlayerFragment extends Fragment {
    private String contentType="";
    protected PlayerView playerView;
    protected @Nullable ExoPlayer player;
    protected  boolean isAds=true;
    private Handler handler;
    private long remainingTime;
    private final long countdownTime = 30000; // 30 seconds countdown (in milliseconds)
    private long startTime;
    protected long currentPosition;
    private TextView tv_ads;
    public  String mainContent_dash_url="dash_url_string";
    public  String mainContent_hls_url="hls_url_string";
    public  String adContent_mp4_url="ad_URL_string";
    String className = "Demo_App";
    private Handler handlerforevents = new Handler();
    private Runnable runnable;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootview= inflater.inflate(R.layout.fragment_player, container, false);

        requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        Bundle bundle = getArguments();
        if (bundle != null) {
            contentType = bundle.getString("contentType");
        }

        tv_ads=rootview.findViewById(R.id.tv_ad);

        playerView = rootview.findViewById(R.id.player_view);
        playerView.requestFocus();

        WindowInsetsControllerCompat insetsControllerCompat = new WindowInsetsControllerCompat(
                requireActivity().getWindow(), requireActivity().getWindow().getDecorView());

        insetsControllerCompat.setSystemBarsBehavior(
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
        insetsControllerCompat.hide(WindowInsetsCompat.Type.systemBars());

        handler = new Handler(Looper.getMainLooper());

        //Method call for Ads handler
        initiateHandlerForAdsProgressLog();

        return rootview;
    }

   //Initializing player with MediaItem
    public void initializePlayer(){
        ExoPlayer.Builder playerBuilder = new ExoPlayer.Builder(requireContext());
        player=playerBuilder.build();
        player.setMediaItem(getMediaItem(contentType));
        player.addListener(new PlayerEventListener());
        player.setAudioAttributes(AudioAttributes.DEFAULT, /* handleAudioFocus= */ true);
        player.setPlayWhenReady(true);
        playerView.setPlayer(player);
        player.prepare();
        player.play();
        Log.d(className,"Main content Started");
    }
    public MediaItem getMediaItem(String contentType){
        MediaItem mediaItem;

        String DASH_CONTENT = "Dash";
        if(contentType.equalsIgnoreCase(DASH_CONTENT)){
            mediaItem =new MediaItem.Builder()
                    .setUri(mainContent_dash_url)
                    .setMimeType(MimeTypes.APPLICATION_MPD)
                    .build();
        }
        else{
            mediaItem= new MediaItem.Builder()
                    .setUri(mainContent_hls_url)
                    .setMimeType(MimeTypes.APPLICATION_M3U8)
                    .build();
        }


        return mediaItem;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(player!=null){
            player.release();
            player=null;
            playerView.setPlayer(/* player= */ null);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
            initializePlayer();
            if (playerView != null) {
                playerView.onResume();
            }

    }

    @Override
    public void onStop() {
        super.onStop();
        requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);

        new WindowInsetsControllerCompat(requireActivity().getWindow(), requireActivity().getWindow().getDecorView()).show(WindowInsetsCompat.Type.systemBars());
        handler.removeCallbacksAndMessages(null);
    }

    //PlayerEvent Listener
    private class PlayerEventListener implements Player.Listener{

        @Override
        public void onPlaybackStateChanged(int playbackState) {
            Player.Listener.super.onPlaybackStateChanged(playbackState);
            if(isAds&&playbackState == Player.STATE_READY){
                isAds=false;

                remainingTime = countdownTime;

                startTime = SystemClock.elapsedRealtime();
                //Handler to get start the Ad in every 30 secs
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        updateCountdown();
                        if (remainingTime > 0) {
                            handler.postDelayed(this, 1000);
                        }
                    }
                }, 3000);
            }

            if (playbackState == Player.STATE_ENDED) {

                playerView.setUseController(true);
                assert player != null;
                player.setMediaItem(getMediaItem(contentType));
                player.seekTo(currentPosition);
                player.prepare();
                player.play();

                isAds=true;

                Log.i(className,"Main content resumed");
            }
        }

        @Override
        public void onPlayerError(@NonNull PlaybackException error) {
            Player.Listener.super.onPlayerError(error);
            Log.e(className,"Player error "+error.getErrorCodeName());
            showAlertDialog(error.getErrorCodeName());

        }

        @Override
        public void onTracksChanged(@NonNull Tracks tracks) {
            Player.Listener.super.onTracksChanged(tracks);
        }

        @Override
        public void onEvents(@NonNull Player player, @NonNull Player.Events events) {
            Player.Listener.super.onEvents(player, events);
            long seconds = Math.round(player.getCurrentPosition() / 1000.0);
            Log.i(className,"Current playing position(in secs): "+seconds);

        }

        @Override
        public void onPlayWhenReadyChanged(boolean playWhenReady, int reason) {
            Player.Listener.super.onPlayWhenReadyChanged(playWhenReady, reason);
            if(playWhenReady){
                Log.i(className,"Player Event : Content Play");
            }
            else{
                Log.i(className,"Player Event : Content Pause");
            }


        }

        @Override
        public void onVolumeChanged(float volume) {
            Player.Listener.super.onVolumeChanged(volume);
            Log.i(className,"Volume Changed: "+volume);
        }

    }

    //Method to initiate Ad playback after 30 secs
    private void updateCountdown() {
        // Calculate elapsed time
        long elapsedTime = SystemClock.elapsedRealtime() - startTime;

        // Calculate remaining time
        remainingTime = countdownTime - elapsedTime;

        if (remainingTime > 0) {
            if(remainingTime/1000<=5){
                tv_ads.setVisibility(View.VISIBLE);
                String adinSec="Ad in "+remainingTime/1000+" sec";
                tv_ads.setText(adinSec);
            }

        } else {
            Log.d(className,"Main content Paused");
            tv_ads.setVisibility(View.GONE);
            // If remaining time is less than or equal to zero, set to zero and stop
            assert player != null;
            currentPosition =player.getCurrentPosition();
            player.pause();
            Uri newUri = Uri.parse(adContent_mp4_url);
            MediaItem newMediaItem = new MediaItem.Builder()
                    .setUri(newUri)
                    .setMimeType(MimeTypes.APPLICATION_MP4)
                    .build();

            playerView.setUseController(false);
            player.setMediaItem(newMediaItem);
            player.prepare();
            player.play();
            Log.e(className,"Ad content started");
        }
    }

    //To show the playback errors in dialog
    private void showAlertDialog(String errorMsg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Playback error")
                .setMessage("Message :"+errorMsg)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        requireActivity().onBackPressed();
                    }
                })
                .setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    //Handler for Ads completion percentage
    public void initiateHandlerForAdsProgressLog(){
        runnable = new Runnable() {
            @Override
            public void run() {
                if(player!=null && player.isPlaying()){
                    if(player.getCurrentMediaItem().localConfiguration.mimeType.equalsIgnoreCase("application/mp4")){
                        double contentduration=player.getContentDuration()/ 1000.0;
                        double currentPos=player.getCurrentPosition()/1000.0;
                        double percentage = (currentPos /contentduration) * 100;
                        if(percentage>=20 && percentage <=25){
                            Log.i(className," 25% Ad completed");
                        }
                        else if(percentage>=40&& percentage <=50){
                            Log.i(className,"50% Ad completed");
                        }
                        if(percentage>=60 && percentage <=75){
                            Log.i(className,"75% Ad completed");
                        }
                        if(percentage>90){
                            Log.i(className,"100% Ad completed");
                        }

                    }

                }
                handlerforevents.postDelayed(this, 1000);
            }
        };

        handlerforevents.postDelayed(runnable, 1000);
    }
}