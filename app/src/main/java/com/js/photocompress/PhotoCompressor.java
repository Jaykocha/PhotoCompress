package com.js.photocompress;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class PhotoCompressor extends AsyncTask<String, Void, Boolean> {

    private final PhotoCompressInterface compressorInterface;

    public PhotoCompressor(PhotoCompressInterface compressorInterface) {
        this.compressorInterface = compressorInterface;
    }

    @Override
    protected Boolean doInBackground(String... strings) {

        File file = new File(strings[0]);
        int quality = getStartingQuality(Math.toIntExact(file.length()), 700000);
        if (file.exists()) {
            compressorInterface.beforeCompressing(strings[0].substring(strings[0].lastIndexOf("/")),
                    quality);
        }

        Bitmap photo = BitmapFactory.decodeFile(strings[0]);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.JPEG, quality, bytes);
        File f = new File(strings[0].replace(".jpg", "compressed.jpg"));
        try {
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            fo.close();
            f.renameTo(f);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    protected void onPostExecute(Boolean aVoid) {
        super.onPostExecute(aVoid);
        compressorInterface.onDoneCompressing();
    }

    private int getStartingQuality(int size, int maxSize){
        double compressRatio = maxSize / (double) size;
        final double [] ratioList = new double[] {0.77, 0.458,0.33966,0.273,0.224,0.19033,0.1596,0.134,0.11366,0.099,0.083666667,
                0.066,0.053,0.04,0.031,0.025666667,0.022666667,0.021333333, 0.020333333};
        final int [] qualityList = new int[] {95, 90, 85, 80, 75, 70, 65, 60, 55, 50, 45, 40, 35, 30, 25, 20, 15, 10, 5};
        for (int i = 0; i < ratioList.length; i++) {
            if (ratioList[i] < compressRatio) {
                if(Math.abs(ratioList[i] - compressRatio) > Math.abs(ratioList[i-1] - compressRatio)){
                    return qualityList[i-1];
                }
                return qualityList[i];
            }
        }
        return -1;
    }
}