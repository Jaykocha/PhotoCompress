package com.js.photocompress;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements PhotoCompressInterface {

    private static String imagePath;
    private final ActivityResultLauncher<String[]> requestPhotoPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(),
                    mappedResults -> {
                        Collection<Boolean> cameraPermissions = mappedResults.values();
                        if (cameraPermissions.size() == 2 && !cameraPermissions.contains(false)) {
                            invokeCamera(createImagePath());
                        }
                    });
    private Button button;
    private ProgressBar loadingIndi;
    private final ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            this::handleResult);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //main data folder
        File f = new File(Environment.getExternalStoragePublicDirectory("Compressed").getPath());
        if (!f.exists()) {
            boolean folderCreated = f.mkdirs();
            if (folderCreated) {
                Toast.makeText(this, "Folder created", Toast.LENGTH_LONG).show();
            }
        }

        button = findViewById(R.id.button);
        loadingIndi = findViewById(R.id.loadingIndi);
        button.setOnClickListener(view -> invokeCamera(createImagePath()));
    }

    private void handleResult(ActivityResult result) {
        if (result.getResultCode() == Activity.RESULT_OK) {
            // There are no request codes
            showLoading();
            if (imagePath != null) {
                PhotoCompressor photoCompressor = new PhotoCompressor(MainActivity.this);
                photoCompressor.execute(imagePath);
            } else {
                Toast.makeText(this, "imagePath is null", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void invokeCamera(String imagePath) {

        if (!hasCameraPermission() || !hasStoragePermission()) {
            String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
            requestPhotoPermissionLauncher.launch(permissions);
            return;
        }

        Uri pictureUri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", new File(imagePath));
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, pictureUri);
        MainActivity.imagePath = imagePath;
        intent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        someActivityResultLauncher.launch(intent);
    }

    @Override
    public void beforeCompressing(String name, int size) {
        Log.v("LOGGER", "Quality: " + size);
    }

    @Override
    public void whileCompressing(String path, int quality, int size) {
        Log.v("LOGGER", path + ", Quality: " + quality + ", Size: " + size + "kB");
    }

    @Override
    public void onDoneCompressing() {
        showButtons();
    }

    private String createImagePath() {
        Date currentTime = Calendar.getInstance().getTime();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
        String path = Environment.getExternalStoragePublicDirectory("Compressed").getPath() + File.separator + format.format(currentTime) + ".jpg";
        Log.v("PhotoCompCallback", path);
        return path;
    }

    private void showButtons() {
        button.setVisibility(View.VISIBLE);
        loadingIndi.setVisibility(View.GONE);
    }

    private void showLoading() {
        button.setVisibility(View.GONE);
        loadingIndi.setVisibility(View.VISIBLE);
    }

    private boolean hasCameraPermission() {
        return ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    private boolean hasStoragePermission() {
        return ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }
}