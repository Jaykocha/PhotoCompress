package com.js.photocompress;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.ByteArrayOutputStream;
import java.io.File;

public class PhotoCompressor extends AsyncTask<String, Void, Boolean> {

    private final PhotoCompressInterface compressorInterface;

    public PhotoCompressor(PhotoCompressInterface compressorInterface) {
        this.compressorInterface = compressorInterface;
    }

    @Override
    protected Boolean doInBackground(String... strings) {

        File file = new File(strings[0]);
        if (file.exists()) {
            compressorInterface.beforeCompressing(strings[0].substring(strings[0].lastIndexOf("/")),
                    Math.toIntExact(file.length()));
        }
        Bitmap photo = BitmapFactory.decodeFile(strings[0]);
        int currQuality = 100;

        int currSize;
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        do {
            bytes.reset();
            photo.compress(Bitmap.CompressFormat.JPEG, currQuality, bytes);
            currSize = bytes.toByteArray().length;
            compressorInterface.whileCompressing(strings[0], currQuality, currSize);
            currQuality -= 5;
        } while (currQuality >= 5);

        return file.delete();
    }

    @Override
    protected void onPostExecute(Boolean aVoid) {
        super.onPostExecute(aVoid);
        compressorInterface.onDoneCompressing();
    }
}