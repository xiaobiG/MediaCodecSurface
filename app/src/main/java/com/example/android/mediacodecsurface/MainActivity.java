package com.example.android.mediacodecsurface;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();

    private VideoPicker videoPicker;
    SurfaceView surfaceView;
    TextView tvVideoPath;
    Button btnVideoChoose;
    private VideoDecoderThread videoDecoderThread;
    private Surface surface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        videoPicker = new VideoPicker(this);
        videoDecoderThread = new VideoDecoderThread();

        surfaceView = findViewById(R.id.surface);
        surfaceView.getHolder().addCallback(surfaceCallback);
        btnVideoChoose = findViewById(R.id.btn_choose_video);
        tvVideoPath = findViewById(R.id.tv_video_path);
        btnVideoChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                videoPicker.pick(new VideoPicker.VideoPickCallback() {
                    @Override
                    public void onPick(VideoPicker.VideoInfo videoInfo) {
                        tvVideoPath.setText(videoInfo.videoPath);
                    }
                });
            }
        });

        findViewById(R.id.btn_start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                start();
            }
        });
    }


    private void start() {
        if (videoDecoderThread != null) {
            videoDecoderThread.close();

            videoDecoderThread = new VideoDecoderThread();
            videoDecoderThread.init(surface, tvVideoPath.getText().toString());
            videoDecoderThread.start();
        }
    }

    private final SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
            Log.d(TAG, "surfaceCreated: ");
            surface = surfaceHolder.getSurface();
        }

        @Override
        public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {
            Log.d(TAG, "surfaceChanged: ");
        }

        @Override
        public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {
            Log.d(TAG, "surfaceDestroyed: ");
            surface = null;
            if (videoDecoderThread != null) {
                videoDecoderThread.close();
            }
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        videoPicker.handlePermissionResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        videoPicker.handleActivityResult(requestCode, resultCode, data);
    }
}