package com.js.photocompress;

/**
 * Class which helps to identify a specific compression factor needed if you want to compress JPG's
 * to have a certain file size.
 */
public class CompressQuality {

    public static final int SIMPLE_DARK = 1010;
    public static final int DETAIL_LIGHT = 5817;

    final int[] qualities = {
            95, 90, 85, 80, 75, 70, 65, 60, 55, 50,
            45, 40, 35, 30, 25, 20, 15, 10, 5};
    final double[] darkComprRatio = {
            0.7018, 0.4241, 0.3136, 0.2511, 0.2051,
            0.1761, 0.1493, 0.1266, 0.1096, 0.0967,
            0.0831, 0.0698, 0.0581, 0.0456, 0.036,
            0.0283, 0.0238, 0.0217, 0.0199};
    final double[] lightComprRatio = {
            1.0053, 0.6941, 0.5499, 0.4604, 0.3951,
            0.3516, 0.3201, 0.2924, 0.2702, 0.254,
            0.2367, 0.2168, 0.2007, 0.1811, 0.1599,
            0.1374, 0.1125, 0.0833, 0.0484
    };
    final double[] meanComprRatio = {
            0.85355, 0.5591, 0.43175, 0.35575, 0.3001,
            0.26385, 0.2347, 0.2095, 0.1899, 0.17535,
            0.1599, 0.1433, 0.1294, 0.11335, 0.09795,
            0.08285, 0.06815, 0.0525, 0.03415
    };

    /**
     * Function which returns the best starting point for image compression if you want to compress
     * to a certain value
     *
     * @param fileSize         image size usually obtained by calling file.length() (in bytes)
     * @param compressedKBSize the final image size after compression you are aiming for (in kilobytes)
     * @param imageType        if you only take images in the sun with many details, like landscape pictures,
     *                         use the flag [CompressQuality.DETAIL_LIGHT]. For dark pictures with little details
     *                         use the flag [CompressQuality.SIMPLE_DARK]. Can be omitted if you want to use the
     *                         standard implementation which is the mean between SIMPLE_DARK and DETAIL_LIGHT
     * @return the suggested image quality (5-95), so the compressed image is as close as possible to the size
     * specified in @compressedKBSize. Returns -1 if file size is already smaller than specified @compressedKBSize
     * or if required quality would be less than 5 to reach such a small compressed file size
     */
    public int get(long fileSize, int compressedKBSize, int imageType) {
        double wantedRatio = compressedKBSize / ((double) fileSize / 1000);
        double[] comprRatios = getRatioArray(imageType);
        for (int i = 0; i < comprRatios.length; i++) {
            if (comprRatios[i] < wantedRatio) {
                if (Math.abs(comprRatios[i] - wantedRatio) > Math.abs(comprRatios[i - 1] - wantedRatio)) {
                    return qualities[i - 1];
                }
                return qualities[i];
            }
        }
        return -1;
    }

    /**
     * Function which returns the best starting point for image compression if you want to compress
     * to a certain value
     *
     * @param fileSize         image size usually obtained by calling file.length()
     * @param compressedKBSize the final image size after compression you are aiming for
     * @return the suggested image quality (5-95), so the compressed image is as close as possible to the size
     * specified in @compressedKBSize. Returns -1 if file size is already smaller than specified @compressedKBSize
     * or if required quality would be less than 5 to reach such a small compressed file size
     */
    public int get(long fileSize, int compressedKBSize) {
        double wantedRatio = compressedKBSize / ((double) fileSize / 1000);
        double[] comprRatios = getRatioArray(1);
        for (int i = 0; i < comprRatios.length; i++) {
            if (comprRatios[i] < wantedRatio) {
                if (Math.abs(comprRatios[i] - wantedRatio) > Math.abs(comprRatios[i - 1] - wantedRatio)) {
                    return qualities[i - 1];
                }
                return qualities[i];
            }
        }
        return -1;
    }

    private double[] getRatioArray(int type) {
        switch (type) {
            case DETAIL_LIGHT:
                return lightComprRatio;
            case SIMPLE_DARK:
                return darkComprRatio;
            default:
                return meanComprRatio;
        }
    }
}
