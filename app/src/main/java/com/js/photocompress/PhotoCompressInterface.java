package com.js.photocompress;

public interface PhotoCompressInterface {
    void onDoneCompressing();

    void whileCompressing(String name, int quality, int size);

    void beforeCompressing(String name, int size);
}