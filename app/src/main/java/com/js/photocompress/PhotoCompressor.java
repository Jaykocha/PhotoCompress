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

        Bitmap photo = BitmapFactory.decodeFile(strings[0]);

        File file = new File(strings[0]);
        int currSize;
        int targetKBSize = 700;
        int quality = new PhotoQuality().get(file.length(), targetKBSize, PhotoQuality.STANDARD);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        do {
            bytes.reset();
            photo.compress(Bitmap.CompressFormat.JPEG, quality, bytes);
            currSize = bytes.toByteArray().length / 1000;
            compressorInterface.whileCompressing(strings[0], quality, currSize);
            quality -= 2;
        } while (currSize > targetKBSize);
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
}