package com.rahimian.player;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements View.OnClickListener , SeekBar.OnSeekBarChangeListener {
    //UI COMPONENTS ARE:
    private Button play ;
    private TextView name ;
    private RadioGroup videoOrMusic;
    private SeekBar playSeek , volumeSeek ;
    private VideoView videoView ;
    private ImageView imgmusic, voloco,stop, loop;
    private ConstraintLayout ctrlMedia;
    //----------------------------------
    enum state {video,music};
    state appstate=state.music;
    private AudioManager audioManager ;//create AudioManager
    private MediaPlayer mediaPlayer;//create  MediaPlayer
    private Timer timer;
    private boolean isloop=false;
    private Uri uri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //INITIALISE UI COMPONENTS
        ctrlMedia = findViewById(R.id.ctrllayout);

        imgmusic = findViewById(R.id.imgmusic);
        imgmusic.setTranslationY(-2000);imgmusic.animate().translationYBy(2000).setDuration(100);
        name         = findViewById(R.id.name);
        videoView    = findViewById(R.id.videoview);
        uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.video);
        videoView.setVideoURI(uri);
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (isloop){
                    onClick(play);
                }else {
                    onClick(stop); }
            }
        });
        videoOrMusic = findViewById(R.id.videoOrMusic);
        play         = findViewById(R.id.play);
        play.setOnClickListener(this);
        loop        = findViewById(R.id.loop);
        loop.setOnClickListener(this);
        stop         = findViewById(R.id.stop);
        stop.setOnClickListener(this);
        //------------------------------------
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);//make audio manager to control the volume
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        final int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        //MediaPlayer
        mediaPlayer = MediaPlayer.create(this,R.raw.music);//make media player with address in R.raw.*
//        timer = new Timer();
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (!mediaPlayer.isLooping()){
                    onClick(stop);
                }
            }
        });
        //volume seek
        volumeSeek   = findViewById(R.id.seekvolume);
        volumeSeek.setMax(maxVolume);
        volumeSeek.setProgress(currentVolume);
        voloco       = findViewById(R.id.voloco);
        if (volumeSeek.getProgress()>=10){ voloco.setImageResource(R.drawable.ic_volume_up_black_24dp); }
        if(volumeSeek.getProgress()==0){ voloco.setImageResource(R.drawable.ic_volume_mute_black_24dp); }
        volumeSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {//volume seek
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {//volume seek
                if (fromUser){//volume seek
                    // change volume icon
                    if (progress>=10){ voloco.setImageResource(R.drawable.ic_volume_up_black_24dp); }
                    if (progress >0 && progress<10){ voloco.setImageResource(R.drawable.ic_volume_down_black_24dp); }
                    if(progress==0){ voloco.setImageResource(R.drawable.ic_volume_mute_black_24dp); }
                    // set volume
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,progress,0);
                }
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) { } @Override public void onStopTrackingTouch(SeekBar seekBar) { }});
        //play seek
        playSeek    = findViewById(R.id.seekplay);
        playSeek.setOnSeekBarChangeListener(this);
        //videoOrMusic group
        videoOrMusic.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                LinearLayout.LayoutParams params  = (LinearLayout.LayoutParams) ctrlMedia.getLayoutParams();
                if (name.getText().toString().equals(" MUSIC is playing ")){onClick(stop);}
                if (name.getText().toString().equals(" VIDEO is playing ")){onClick(stop);}

                playSeek.setProgress(0);
                if (checkedId==R.id.ismusic)//<MUSIC>
                { imgmusic.animate().translationYBy(2000).setDuration(100);
                    appstate = state.music;name.setText(" MUSIC PLAYER ");
                    params.weight= 50;
                }//</MUSIC>
                if (checkedId==R.id.isvideo)//<VIDEO>
                { imgmusic.animate().translationYBy(-2000).setDuration(500);
                    appstate = state.video;name.setText(" VIDEO PLAYER ");
                    params.weight= 25;
                }//</VIDEO>
            }});

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.play://playBT
                stop.setAlpha(.5f);
                    if (appstate == state.music) {//MUSIC state
                        if ( !mediaPlayer.isPlaying()) {
                            //playing
                            play.setBackgroundResource(R.drawable.pause);
                            playSeek.setMax(mediaPlayer.getDuration());
                            mediaPlayer.start();
                            name.setText(" MUSIC is playing ");
                            timer = new Timer();
                            timer.scheduleAtFixedRate(new TimerTask() {
                                @Override
                                public void run() {
                                    playSeek.setProgress(mediaPlayer.getCurrentPosition());
                                }
                            }, 0, 100);
                        }else {
                            //pausing
                            play.setBackgroundResource(R.drawable.play);
                            name.setText(" MUSIC is paused ");
                            mediaPlayer.pause();
                            timer.cancel();
                        }
                    }
                    if (appstate == state.video) {//VIDEO state
                        if ( !videoView.isPlaying()) {
                            //playing
                            play.setBackgroundResource(R.drawable.pause);
                            playSeek.setMax(videoView.getDuration());
                            name.setText(" VIDEO is playing ");
                            /////
                            videoView.start();
                            timer = new Timer();
                            timer.scheduleAtFixedRate(new TimerTask() {
                                @Override
                                public void run() {
                                    playSeek.setProgress(videoView.getCurrentPosition());
                                }
                            }, 0, 100);
                        }else {
                            //pausing
                            play.setBackgroundResource(R.drawable.play);
                            name.setText(" VIDEO is paused ");
                            videoView.pause();
                            timer.cancel();
                        }
                    }
                break;
            case R.id.loop:
                if (appstate==state.music){//MUSIC state
                    if(!mediaPlayer.isLooping()){
                        loop.setAlpha(1f);
                        mediaPlayer.setLooping(true);
                    }else {
                        loop.setAlpha(.5f);
                        mediaPlayer.setLooping(false);
                    }
                }
                if (appstate==state.video){//VIDEO state
                    if (!isloop){ loop.setAlpha(1f);isloop = true ; }else {loop.setAlpha(.5f);isloop = false ;}
                }

                break;
            case R.id.stop://stopBTN
                if (appstate==state.music){//MUSIC state
                    name.setText(" MUSIC PLAYER ");
                    timer.cancel();
                    mediaPlayer.stop();mediaPlayer = MediaPlayer.create(this,R.raw.music);//again addressing media player in R.raw.*
                }
                if (appstate==state.video){//VIDEO state
                    name.setText(" VIDEO PLAYER ");
                    videoView.seekTo(0);
                    timer.cancel();
                    videoView.stopPlayback();uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.video);videoView.setVideoURI(uri);//again
                }
                //other

                playSeek.setProgress(0);
                play.setBackgroundResource(R.drawable.play);
                stop.setAlpha(1f);
                break;
        }
    }
    //playseek in changes
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser){
            if (appstate==state.music){//MUSIC state
                    mediaPlayer.seekTo(progress);
            }
            if (appstate==state.video){//VIDEO state
                    videoView.seekTo(progress);
            }
        }
    }
    @Override public void onStartTrackingTouch(SeekBar seekBar) { } @Override public void onStopTrackingTouch(SeekBar seekBar) { }
}
