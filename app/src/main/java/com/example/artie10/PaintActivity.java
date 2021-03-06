package com.example.artie10;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.MediaRecorder;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.ToggleButton;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

/**
 * @author Öykü, Lara, Yaren, Sarper, Berk, Onur, Enis
 * @version 1.0
 * @date 5/05/2020
 * This class creates paint screen and option to record that screen
 */
public class PaintActivity extends AppCompatActivity {

    //variables
    private PaintView paintView;
    ImageButton pencil;
    ToggleButton togglebutton2;
    private String videoURI = "";
    private MediaProjectionManager mediaProjectionManager;
    private MediaProjection mediaProjection;
    private MediaProjectionCallBack mediaProjectionCallBack;
    private MediaRecorder mediaRecorder;
    private VirtualDisplay virtualDisplay;
    private int mScreenDensity;
    private static  int DISPLAY_WIDTH = 720;
    private static  int DISPLAY_HEIGHT = 1280;

    //constants
    private static final int REQUEST_CODE = 1000;
    private static final int REQUEST_PERMISSION = 1001;

    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();

    static {
        ORIENTATIONS.append( Surface.ROTATION_0,0 );
        ORIENTATIONS.append( Surface.ROTATION_90,90 );
        ORIENTATIONS.append( Surface.ROTATION_180,180 );
        ORIENTATIONS.append( Surface.ROTATION_270,270 );
    }

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_paint );

        pencil = (ImageButton) findViewById(R.id.pencil);
        pencil.setOnClickListener(
                v -> returnAR()
        );

        togglebutton2 = findViewById(R.id.toggleButton2);

        ActivityCompat.requestPermissions(this, new String[]{
                WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, PERMISSION_GRANTED);

        paintView = ( PaintView ) findViewById( R.id.paintView );
        DisplayMetrics metrics2 = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics2);
        mScreenDensity = metrics2.densityDpi;
        paintView.init(metrics2);

        DISPLAY_HEIGHT = metrics2.heightPixels;
        DISPLAY_WIDTH = metrics2.widthPixels;

        mediaRecorder = new MediaRecorder();
        mediaProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);

        togglebutton2.setOnClickListener(
                v -> {
                    if (ContextCompat.checkSelfPermission(PaintActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            + ContextCompat.checkSelfPermission(PaintActivity.this, Manifest.permission.RECORD_AUDIO)
                            != PackageManager.PERMISSION_GRANTED) {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(PaintActivity.this, WRITE_EXTERNAL_STORAGE) ||
                                ActivityCompat.shouldShowRequestPermissionRationale(PaintActivity.this, Manifest.permission.RECORD_AUDIO)) {
                            togglebutton2.setChecked(false);

                        } else {
                            ActivityCompat.requestPermissions(PaintActivity.this,
                                    new String[]{
                                            WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO},
                                    REQUEST_PERMISSION);
                        }
                    } else {
                        toggleScreenShare(v);
                    }
                });
    }

    public void returnAR(){
        finish();
    }


    /**
     * This method closes the video recording screen
     */
    private class MediaProjectionCallBack extends MediaProjection.Callback {
        @Override
        public void onStop() {
            if( togglebutton2.isChecked() ){
                togglebutton2.setChecked( false );
                mediaRecorder.stop();
                mediaRecorder.reset();
            }
            mediaProjection = null;
            stopRecordScreen();
            super.onStop();
        }
    }

    /**
     * This method stops the recording activity
     */
    private void stopRecordScreen() {
        if( virtualDisplay == null )
            return;

        virtualDisplay.release();
        destroyMediaProjection();
    }

    /**
     * This method helps stop recording
     */
    private void destroyMediaProjection() {
        if( mediaProjection != null ){
            mediaProjection.unregisterCallback( mediaProjectionCallBack );
            mediaProjection.stop();
            mediaProjection = null;
        }

    }

    /**
     * This method controls the toggle button "record"
     * @param v
     */
    private void toggleScreenShare( View v ){
        if( ( (ToggleButton) v ).isChecked() )
        {
            initRecorder();
            recordScreen();
        }
        else{
            mediaRecorder.stop();
            mediaRecorder.reset();
            stopRecordScreen();

            Intent intent = new Intent (PaintActivity.this, UploadVideo.class );
            Bundle bundle= new Bundle();
            bundle.putString("transferInfo", videoURI);
            intent.putExtras(bundle);
            startActivity( intent );
        }

    }

    /**
     * This method sets the recording video size
     */
    private void initRecorder(){
        try{
            mediaRecorder.setAudioSource( MediaRecorder.AudioSource.MIC );
            mediaRecorder.setVideoSource( MediaRecorder.VideoSource.SURFACE );
            mediaRecorder.setOutputFormat( MediaRecorder.OutputFormat.THREE_GPP );

            videoURI = Environment.getExternalStoragePublicDirectory( Environment.DIRECTORY_DOWNLOADS )
                    + new StringBuilder( "/EDMTRecord_" ).append( new SimpleDateFormat("dd-MM-yyyy-hh_mm_ss" )
                    .format(new Date())).append( " .mp4" ).toString();

            mediaRecorder.setOutputFile( videoURI );
            mediaRecorder.setVideoSize( DISPLAY_WIDTH, DISPLAY_HEIGHT );
            mediaRecorder.setVideoEncoder( MediaRecorder.VideoEncoder.H264 );
            mediaRecorder.setAudioEncoder( MediaRecorder.AudioEncoder.AMR_NB );
            mediaRecorder.setVideoEncodingBitRate( 512 * 1000 );
            mediaRecorder.setVideoFrameRate( 30 );

            int rotation = getWindowManager().getDefaultDisplay().getRotation();
            int orientation = ORIENTATIONS.get( rotation * 90);
            mediaRecorder.setOrientationHint( orientation );
            mediaRecorder.prepare();

        } catch ( IOException e ) {
            e.printStackTrace();
        }

    }

    /**
     * This method helps recording the video
     */
    private void recordScreen(){

        if( mediaProjection == null ){

            startActivityForResult( mediaProjectionManager.createScreenCaptureIntent(), REQUEST_CODE );
            return;
        }
        virtualDisplay = createVirtualDisplay();
        mediaRecorder.start();
    }

    /**
     * This method creates virtual display of video
     * @return
     */
    private VirtualDisplay createVirtualDisplay() {
        return mediaProjection.createVirtualDisplay( "ARScreen", DISPLAY_WIDTH, DISPLAY_HEIGHT, mScreenDensity,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                mediaRecorder.getSurface(), null, null );
    }

    @Override
    protected void onActivityResult( int requestCode, int resultCode, @Nullable Intent data ) {
        super.onActivityResult( requestCode, resultCode, data );
        if( requestCode!= REQUEST_CODE ){
            Toast.makeText( this, "Unk error", Toast.LENGTH_SHORT ).show();
            return;
        }

        if( resultCode != RESULT_OK ){
            Toast.makeText( this, "Permission denied", Toast.LENGTH_SHORT ).show();
            togglebutton2.setChecked( false );
            return;
        }

        mediaProjectionCallBack = new MediaProjectionCallBack();
        mediaProjection = mediaProjectionManager.getMediaProjection( resultCode, data );
        mediaProjection.registerCallback( mediaProjectionCallBack, null );
        virtualDisplay = createVirtualDisplay();
        mediaRecorder.start();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults ) {
        super.onRequestPermissionsResult( requestCode, permissions, grantResults );
        switch ( requestCode ){
            case REQUEST_PERMISSION:
            {
                if( ( grantResults.length > 0) && (grantResults[0] + grantResults[1] == PERMISSION_GRANTED ) ){
                    toggleScreenShare( togglebutton2 );
                }
                else{
                    togglebutton2.setChecked( false );
                }
                return;
            }
        }
    }
    //methods for video recording ends here
}