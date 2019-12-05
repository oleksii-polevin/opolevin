

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * File ImageReader.java
 * ---------------------
 * This class is responsible of reading image from file and rewrite it into pixel array
 */
class ImageReader {
    /**
     * Reads from file and writes luminosities of each pixel into Integer array
     *
     * @param filename The image file
     * @return The array with luminosities
     */
    int[][] getPixels(String filename) {

        File file = new File(filename);
        BufferedImage image;
        try {
            image = ImageIO.read(file);

            /* initialising array for luminosities */
            int[][] pixels = new int[image.getWidth()][image.getHeight()];

            /* rewriting image luminosities into Integer array */
            for (int i = 0; i < pixels.length; i++) {
                for (int j = 0; j < pixels[0].length; j++) {
                    Color c = new Color(image.getRGB(i, j));
                    int luminosity = getLuminosity(c);
                    pixels[i][j] = luminosity;
                }
            }
            return pixels;
        } catch (IOException e) {
            System.err.println("check filename");
            System.exit(1);
        }
        return null;
    }

    /**
     * This method reads luminosity of each pixel and converts it into integer
     * from 0 to 255
     * by using formula from assignment 6 cs-a
     *
     * @param c The color of pixel from image
     * @return luminosity
     */
    private int getLuminosity(Color c) {

        int red = c.getRed();
        int green = c.getGreen();
        int blue = c.getBlue();

        return (int) (0.3D * (double) red + 0.59D * (double) green + 0.11 * (double) blue + 0.5D);
    }
}
