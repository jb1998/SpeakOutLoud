package com.developer.jatin.text;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.IOException;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements
        TextToSpeech.OnInitListener{
    SurfaceView surfaceView;
    TextView textView;
    private TextToSpeech tts;
    Switch mySwitch;
    CameraSource cameraSource;
 final int RequestCameraPermissionID=1001;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode)
        {
            case RequestCameraPermissionID :
            {
                if(grantResults[0]==PackageManager.PERMISSION_GRANTED)
                {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                    {
                        return;
                    }
                    try {
                        cameraSource.start(surfaceView.getHolder());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        mySwitch=(Switch)findViewById(R.id.switch1);
        mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // do something, the isChecked will be
                // true if the switch is in the On position
                if(isChecked){
                    if (tts != null) {
                        textView.setText("Point to Text");
                        tts.stop();
                        tts.shutdown();
                    }
                }else{
                    tts_again();
                }
            }
        });
        surfaceView = (SurfaceView) findViewById(R.id.surface);
        textView = (TextView) findViewById(R.id.text);
         textView.setBackgroundColor(Color.TRANSPARENT);
        tts = new TextToSpeech(this, this);
        TextRecognizer textRecognizer = new TextRecognizer.Builder(MainActivity.this).build();
        if (!textRecognizer.isOperational()) {
            Toast.makeText(MainActivity.this,"bhbjhbjh",Toast.LENGTH_LONG).show();
        } else {
            cameraSource = new CameraSource.Builder(MainActivity.this, textRecognizer)
                    .setFacing(CameraSource.CAMERA_FACING_BACK)
                    .setRequestedPreviewSize(1280, 1024)
                    .setRequestedFps(2.0f)
                    .setAutoFocusEnabled(true)
                    .build();
            surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder surfaceHolder) {
                    try {
                        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                          ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.CAMERA},
                            RequestCameraPermissionID);
                            return;
                        }
                        cameraSource.start(surfaceView.getHolder());
                     } catch (IOException e) {
                         e.printStackTrace();
                     }
               }

               @Override
               public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

               }

               @Override
               public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
            cameraSource.stop();
               }
           });

            textRecognizer.setProcessor(new Detector.Processor<TextBlock>() {
                @Override
                public void release() {

                }

                @Override
                public void receiveDetections(Detector.Detections<TextBlock> detections) {

                    final SparseArray<TextBlock> items=detections.getDetectedItems();
                    if(items.size()!=0)
                    {
                        textView.post(new Runnable() {
                            @Override
                            public void run() {
                                StringBuilder stringBuilder= new StringBuilder();
                                for(int i=0;i<items.size();i++)
                                {
                                    TextBlock item =items.valueAt(i);
                                    stringBuilder.append(item.getValue());
                                    stringBuilder.append("\n");
                                }
                                textView.setText(stringBuilder.toString());
                                tts.speak(stringBuilder.toString(), TextToSpeech.QUEUE_FLUSH, null);
                            }

                        });

                    }
//                 Thread t = new Thread();
//                    try {
//                        t.sleep(1000);
//                        String speechmy = textView.getText().toString();
////                        tts.speak(speechmy, TextToSpeech.QUEUE_FLUSH , null);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
                }
            });
        }
    }
    void tts_again()
    {
        tts = new TextToSpeech(this, this);
    }

    @Override
    public void onDestroy() {
        // Don't forget to shutdown tts!
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }
    @Override
    public void onInit(int status) {

        if (status == TextToSpeech.SUCCESS) {

            int result = tts.setLanguage(Locale.US);

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
//                Log.e("TTS", "This Language is not supported");
                Toast.makeText(MainActivity.this,"This Language is not supported",Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(MainActivity.this,"error",Toast.LENGTH_LONG).show();
            }

        } else {
            Toast.makeText(MainActivity.this,"error",Toast.LENGTH_LONG).show();
        }

    }

}
