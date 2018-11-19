package com.pets.audio;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class actAudio extends AppCompatActivity {
    VideoView player;
    MediaController ctrl;
    MediaPlayer song;
    SeekBar seeker;
    SeekBar seeker_Volume;
    AudioManager audioMan;
    TextView txtVol;
    TextView txtTime;
    boolean playing = false;
    Timer audioTimer;
    TimerTask task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_audio);
        ConfigureVideoViewer();
        ConfigureSongSeeker();
        ConfigureVolumeSeeker();
    }

    private void ConfigureVolumeSeeker() {
        seeker_Volume = (SeekBar) findViewById(R.id.seekVolume);
        seeker_Volume.setMax(audioMan.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        seeker_Volume.setProgress(audioMan.getStreamVolume(AudioManager.STREAM_MUSIC));
        txtVol = (TextView) findViewById(R.id.txtVolume);
        txtVol.setText(String.valueOf(seeker_Volume.getProgress() * 100 / seeker_Volume.getMax()));

        seeker_Volume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                audioMan.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
                txtVol.setText(String.valueOf(progress * 100 / seeker_Volume.getMax()));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {


            }
        });
    }

    private void ConfigureVideoViewer() {
        player = (VideoView) findViewById(R.id.videoView);
        player.setVideoPath("android.resource://" + getPackageName() + "/" + R.raw.vid);
        ctrl = new MediaController(this,true);
        ctrl.setAnchorView(player);
        player.setMediaController(ctrl);

    }

    private void ConfigureSongSeeker() {
        audioMan = (AudioManager) getSystemService(AUDIO_SERVICE);
        song = MediaPlayer.create(this, R.raw.kolena);
        song.seekTo(0);

        seeker = (SeekBar) findViewById(R.id.seekSong);
        seeker.setMax(song.getDuration());
        txtTime = (TextView) findViewById(R.id.txtTime);
        txtTime.setText("0/" + formatDuration(seeker.getMax()));
        seeker.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //song.seekTo(progress);
                txtTime.setText(formatDuration(progress) + "/" + formatDuration(seeker.getMax()));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                song.seekTo(seekBar.getProgress());
                txtTime.setText(formatDuration(seekBar.getProgress()) + "/" + formatDuration(seeker.getMax()));
            }
        });
    }

    public void playAudio_Clicked(View view) {
        if (playing) {
            song.pause();
            playing = false;
            ImageView img = (ImageView) findViewById(R.id.btnPlay);
            img.setImageResource(R.drawable.play);
            audioTimer = null;
        } else {
            song.start();
            playing = true;
            ImageView img = (ImageView) findViewById(R.id.btnPlay);
            img.setImageResource(R.drawable.pause);
            task = new TimerTask() {
                @Override
                public void run() {
                    seeker.setProgress(song.getCurrentPosition());
                }
            };
            new Timer().scheduleAtFixedRate(task, 0, 100);
        }
    }

    private String formatDuration(long duration) {
        return String.format("%d:%d",TimeUnit.MILLISECONDS.toMinutes(duration),TimeUnit.MILLISECONDS.toSeconds(duration) -TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration)));
    }

    @Override
    protected void onDestroy() {
        song.release();
        song=null;
        super.onDestroy();
    }
}

