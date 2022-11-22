package com.example.computervisionproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.widget.TextView;

import com.example.computervisionproject.camera.CameraOverlay;
import com.example.computervisionproject.camera.CameraSurfacePreview;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.IOException;

public class OcrActivity extends AppCompatActivity {

    private TextView textView;

    private static final String TAG = "FaceTrackerDemo";
    private CameraSource mCameraSource = null;
    private CameraSurfacePreview mPreview;
    private CameraOverlay mOverlay;
    private static final int RC_HANDLE_GMS = 9001;
    private static final int RC_HANDLE_CAMERA_PERM = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ocr);

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE},
                PackageManager.PERMISSION_GRANTED);

        textView = findViewById(R.id.textView);
        mPreview = findViewById(R.id.preview);
        mOverlay = findViewById(R.id.overlay);
        int rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (rc == PackageManager.PERMISSION_GRANTED) {
            createCameraSource();
        } else {
            requestCameraPermission();
        }
    }

    private void requestCameraPermission() {
        final String[] permissions = new String[]{Manifest.permission.CAMERA};
        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(this, permissions, RC_HANDLE_CAMERA_PERM);
        }
    }

    private void createCameraSource() {
        Context context = getApplicationContext();

        TextRecognizer textRecognizer = new TextRecognizer.Builder(this).build();

        textRecognizer.setProcessor(new MultiProcessor.Builder<>(new OcrActivity.TextTrackerFactory())
                .build());

        mCameraSource = new CameraSource.Builder(context, textRecognizer)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedFps(30.0f)
                .build();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startCameraSource();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPreview.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCameraSource != null) {
            mCameraSource.release();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode != RC_HANDLE_CAMERA_PERM) {
            Log.d(TAG, "Got unexpected permission result: " + requestCode);
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Camera permission granted - initialize the camera source");
            createCameraSource();
            return;
        }

        Log.e(TAG, "Permission not granted: results len = " + grantResults.length +
                " Result code = " + (grantResults.length > 0 ? grantResults[0] : "(empty)"));
        DialogInterface.OnClickListener listener = (dialog, id) -> finish();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("FaceTrackerDemo")
                .setMessage("Need Camera access permission!")
                .setPositiveButton("OK", listener)
                .show();
    }

    private void startCameraSource() {
        int code = GoogleApiAvailability.getInstance()
                .isGooglePlayServicesAvailable(getApplicationContext());
        if (code != ConnectionResult.SUCCESS) {
            GoogleApiAvailability.getInstance()
                    .getErrorDialog(this, code, RC_HANDLE_GMS).show();
        }

        if (mCameraSource != null) {
            try {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                mPreview.start(mCameraSource, mOverlay);
            } catch (IOException e) {
                Log.e(TAG, "Unable to start camera source.", e);
                mCameraSource.release();
                mCameraSource = null;
            }
        }
    }

    private class TextTrackerFactory implements MultiProcessor.Factory<TextBlock> {
        @NonNull
        @Override
        public Tracker<TextBlock> create(@NonNull TextBlock textBlock) {
            System.out.println(textBlock.getValue());
            return new TextTracker();
        }
    }

    private class TextTracker extends Tracker<TextBlock> {
        private final TextOverlayGraphics textOverlayGraphics;

        TextTracker() {
            textOverlayGraphics = new TextOverlayGraphics(mOverlay);
        }

        @Override
        public void onUpdate(@NonNull Detector.Detections<TextBlock> detections, @NonNull TextBlock textBlock) {
            super.onUpdate(detections, textBlock);
            String stringImageText = "";
            SparseArray<TextBlock> textBlockSparseArray = detections.getDetectedItems();
            for (int i = 0; i < textBlockSparseArray.size(); i++) {
                TextBlock block = textBlockSparseArray.get(textBlockSparseArray.keyAt(i));
                stringImageText += " " + block.getValue();
            }
            textView.setText(stringImageText);
            mOverlay.add(textOverlayGraphics);
            textOverlayGraphics.updateTextBlock(textBlock);
        }

        @Override
        public void onMissing(@NonNull Detector.Detections<TextBlock> detections) {
            mOverlay.remove(textOverlayGraphics);
        }

        @Override
        public void onDone() {
            mOverlay.remove(textOverlayGraphics);
        }

    }
}
