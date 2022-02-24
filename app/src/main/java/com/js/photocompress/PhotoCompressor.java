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
        int quality = getQualitySettings(Math.toIntExact(file.length()), 700000);
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

    private int getQualitySettings(int size, int maxSize){
        double compressRatio = maxSize / (double) size;
        final double [] ratioList = new double[] {1.005252632,0.694115789,0.549894737,0.460442105,
                0.395084211,0.351610526,0.320126316,0.2924,0.270178947,0.253968421,0.236673684,
                0.216842105,0.200736842,0.181084211,0.159947368,0.1374,0.112452632,0.083305263,
                0.048368421};
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