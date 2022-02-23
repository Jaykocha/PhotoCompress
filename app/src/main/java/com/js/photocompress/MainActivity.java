package com.js.photocompress;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
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

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

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
    private Button button2;
    private ProgressBar loadingIndi;
    private final ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            this::handleResult);
    private PhotoEntity photoData;

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
        button2 = findViewById(R.id.button2);
        loadingIndi = findViewById(R.id.loadingIndi);
        button.setOnClickListener(view -> invokeCamera(createImagePath()));
        button2.setOnClickListener(v -> {
            showLoading();
            AsyncTask.execute(() -> {
                exportToCSV();
                runOnUiThread(() -> {
                    showButtons();
                    Toast.makeText(MainActivity.this,
                            "CSV created successfully and put in to folder 'Compressed'",
                            Toast.LENGTH_LONG).show();
                });
            });
        });
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

    private void showLoading() {
        button.setVisibility(View.GONE);
        loadingIndi.setVisibility(View.VISIBLE);
        button2.setVisibility(View.GONE);
    }

    private void exportToCSV() {
        File exportDir = new File(Environment.getExternalStoragePublicDirectory("Compressed"), "");
        File file = new File(exportDir, "export" + ".csv");
        try {
            boolean fileCreated = file.createNewFile();
            CSVWriter csvWrite = new CSVWriter(new FileWriter(file));
            csvWrite.writeNext(getColumnHeader());
            List<PhotoEntity> list = Database.getInstance(this).pathDao().getAllData();
            for (int i = 0; i < list.size(); i++) {
                csvWrite.writeNext(getStringArray(list.get(i)));
            }
            csvWrite.close();
        } catch (Exception sqlEx) {
            Log.e("MainActivity", sqlEx.getMessage(), sqlEx);
        }
    }

    String[] getColumnHeader() {
        return new String[]{
                "Name", "Original", "Quality 100", "Quality 95", "Quality 90", "Quality 85", "Quality 80",
                "Quality 75", "Quality 70", "Quality 65", "Quality 60", "Quality 55", "Quality 50",
                "Quality 45", "Quality 40", "Quality 35", "Quality 30", "Quality 25", "Quality 20",
                "Quality 15", "Quality 10", "Quality 5"
        };
    }

    String[] getStringArray(PhotoEntity entity) {
        return new String[]{
                entity.photoPath,
                String.valueOf(entity.originalSize),
                String.valueOf(entity.quality_100),
                String.valueOf(entity.quality_95),
                String.valueOf(entity.quality_90),
                String.valueOf(entity.quality_85),
                String.valueOf(entity.quality_80),
                String.valueOf(entity.quality_75),
                String.valueOf(entity.quality_70),
                String.valueOf(entity.quality_65),
                String.valueOf(entity.quality_60),
                String.valueOf(entity.quality_55),
                String.valueOf(entity.quality_50),
                String.valueOf(entity.quality_45),
                String.valueOf(entity.quality_40),
                String.valueOf(entity.quality_35),
                String.valueOf(entity.quality_30),
                String.valueOf(entity.quality_25),
                String.valueOf(entity.quality_20),
                String.valueOf(entity.quality_15),
                String.valueOf(entity.quality_10),
                String.valueOf(entity.quality_5),
        };
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

    private boolean hasCameraPermission() {
        return ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    private boolean hasStoragePermission() {
        return ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private String createImagePath() {
        Date currentTime = Calendar.getInstance().getTime();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
        String path = Environment.getExternalStoragePublicDirectory("Compressed").getPath() + File.separator + format.format(currentTime) + ".jpg";
        Log.v("PhotoCompCallback", path);
        return path;
    }

    @Override
    public void onDoneCompressing() {
        AsyncTask.execute(() -> Database.getInstance(MainActivity.this).pathDao().insertPhotoEntity(photoData));
        showButtons();
    }

    private void showButtons() {
        button.setVisibility(View.VISIBLE);
        button2.setVisibility(View.VISIBLE);
        loadingIndi.setVisibility(View.GONE);
    }

    @Override
    public void whileCompressing(String name, int quality, int size) {
        mapQualityToUpdateFunction(photoData, quality, size / 1000);
        Log.v("PhotoCompCallback", name + " Quality: " + quality + ", Size: " + size / 1000 + "kB");
    }

    @Override
    public void beforeCompressing(String name, int size) {
        Log.v("PhotoCompCallback", "[BEFORE] " + name + ": " + size / 1000 + "KB");
        photoData = new PhotoEntity(name, size / 1000, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0);
    }

    private void mapQualityToUpdateFunction(PhotoEntity entity, int quality, int size) {
        DecimalFormatSymbols formatOptions = new DecimalFormatSymbols();
        formatOptions.setDecimalSeparator('.');
        DecimalFormat df = new DecimalFormat("0.000", formatOptions);
        double originalSize = entity.originalSize;
        double sizeRatio = size / originalSize;
        String roundedRatio = df.format(sizeRatio);
        double sizeRatioToOriginal = Double.parseDouble(roundedRatio);
        if (quality == 5) {
            entity.setQuality_5(sizeRatioToOriginal);
        } else if (quality == 10) {
            entity.setQuality_10(sizeRatioToOriginal);
        } else if (quality == 15) {
            entity.setQuality_15(sizeRatioToOriginal);
        } else if (quality == 20) {
            entity.setQuality_20(sizeRatioToOriginal);
        } else if (quality == 25) {
            entity.setQuality_25(sizeRatioToOriginal);
        } else if (quality == 30) {
            entity.setQuality_30(sizeRatioToOriginal);
        } else if (quality == 35) {
            entity.setQuality_35(sizeRatioToOriginal);
        } else if (quality == 40) {
            entity.setQuality_40(sizeRatioToOriginal);
        } else if (quality == 45) {
            entity.setQuality_45(sizeRatioToOriginal);
        } else if (quality == 50) {
            entity.setQuality_50(sizeRatioToOriginal);
        } else if (quality == 55) {
            entity.setQuality_55(sizeRatioToOriginal);
        } else if (quality == 60) {
            entity.setQuality_60(sizeRatioToOriginal);
        } else if (quality == 65) {
            entity.setQuality_65(sizeRatioToOriginal);
        } else if (quality == 70) {
            entity.setQuality_70(sizeRatioToOriginal);
        } else if (quality == 75) {
            entity.setQuality_75(sizeRatioToOriginal);
        } else if (quality == 80) {
            entity.setQuality_80(sizeRatioToOriginal);
        } else if (quality == 85) {
            entity.setQuality_85(sizeRatioToOriginal);
        } else if (quality == 90) {
            entity.setQuality_90(sizeRatioToOriginal);
        } else if (quality == 95) {
            entity.setQuality_95(sizeRatioToOriginal);
        } else if (quality == 100) {
            entity.setQuality_100(sizeRatioToOriginal);
        }
    }
}