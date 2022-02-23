package com.js.photocompress;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class PhotoEntity {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String photoPath;
    public int originalSize;
    public double quality_100;
    public double quality_95;
    public double quality_90;
    public double quality_85;
    public double quality_80;
    public double quality_75;
    public double quality_70;
    public double quality_65;
    public double quality_60;
    public double quality_55;
    public double quality_50;
    public double quality_45;
    public double quality_40;
    public double quality_35;
    public double quality_30;
    public double quality_25;
    public double quality_20;
    public double quality_15;
    public double quality_10;
    public double quality_5;


    public PhotoEntity(String photoPath, int originalSize, double quality_100, double quality_95,
                       double quality_90, double quality_85, double quality_80, double quality_75, double quality_70,
                       double quality_65, double quality_60, double quality_55, double quality_50, double quality_45,
                       double quality_40, double quality_35, double quality_30, double quality_25, double quality_20,
                       double quality_15, double quality_10, double quality_5) {
        this.photoPath = photoPath;
        this.originalSize = originalSize;
        this.quality_100 = quality_100;
        this.quality_95 = quality_95;
        this.quality_90 = quality_90;
        this.quality_85 = quality_85;
        this.quality_80 = quality_80;
        this.quality_75 = quality_75;
        this.quality_70 = quality_70;
        this.quality_65 = quality_65;
        this.quality_60 = quality_60;
        this.quality_55 = quality_55;
        this.quality_50 = quality_50;
        this.quality_45 = quality_45;
        this.quality_40 = quality_40;
        this.quality_35 = quality_35;
        this.quality_30 = quality_30;
        this.quality_25 = quality_25;
        this.quality_20 = quality_20;
        this.quality_15 = quality_15;
        this.quality_10 = quality_10;
        this.quality_5 = quality_5;
    }

    public void setQuality_5(double quality_5) {
        this.quality_5 = quality_5;
    }

    public void setQuality_10(double quality_10) {
        this.quality_10 = quality_10;
    }

    public void setQuality_15(double quality_15) {
        this.quality_15 = quality_15;
    }

    public void setQuality_20(double quality_20) {
        this.quality_20 = quality_20;
    }

    public void setQuality_25(double quality_25) {
        this.quality_25 = quality_25;
    }

    public void setQuality_30(double quality_30) {
        this.quality_30 = quality_30;
    }

    public void setQuality_35(double quality_35) {
        this.quality_35 = quality_35;
    }

    public void setQuality_40(double quality_40) {
        this.quality_40 = quality_40;
    }

    public void setQuality_45(double quality_45) {
        this.quality_45 = quality_45;
    }

    public void setQuality_50(double quality_50) {
        this.quality_50 = quality_50;
    }

    public void setQuality_55(double quality_55) {
        this.quality_55 = quality_55;
    }

    public void setQuality_60(double quality_60) {
        this.quality_60 = quality_60;
    }

    public void setQuality_65(double quality_65) {
        this.quality_65 = quality_65;
    }

    public void setQuality_70(double quality_70) {
        this.quality_70 = quality_70;
    }

    public void setQuality_75(double quality_75) {
        this.quality_75 = quality_75;
    }

    public void setQuality_80(double quality_80) {
        this.quality_80 = quality_80;
    }

    public void setQuality_85(double quality_85) {
        this.quality_85 = quality_85;
    }

    public void setQuality_90(double quality_90) {
        this.quality_90 = quality_90;
    }

    public void setQuality_95(double quality_95) {
        this.quality_95 = quality_95;
    }

    public void setQuality_100(double quality_100) {
        this.quality_100 = quality_100;
    }

    @NonNull
    @Override
    public String toString() {
        return "PhotoEntity{" +
                "id=" + id +
                ", photoPath='" + photoPath + '}';
    }
}
