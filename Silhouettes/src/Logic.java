

import java.util.ArrayList;

/**
 * File Logic.java
 * ---------------
 * This class counts the number of silhouettes in the picture
 * by using deep breadthSearch algorithm
 * Auxiliary methods (garbage finder, silhouette divider, and background finder)
 * are also present in this class.
 */
class Logic implements Constants {

    /* counter for each breadthSearch loop (used for throwing garbage from counting) */
    private int totalPixels;

    /**
     * This method returns number of silhouettes
     *
     * @param pixels The integer array with luminosities of each pixel
     * @return The number of silhouettes
     */
    int silhouettesNumber(int[][] pixels) {

        return findSilhouettes(getSilhouettes(pixels));
    }

    /**
     * Reads top line of pixel array which almost 100% don't contains any silhouette
     * if silhouette crossing top line this method returns luminosity of most frequent pixel
     *
     * @param pixels The array with luminosities
     * @return luminosity of the background
     */
    private int backgroundLuminosity(int[][] pixels) {

        /* assign first pixel as candidate to background */
        int background = pixels[0][0];

        /* create alternative background because some pictures
        may contain silhouette on the top of the picture */
        int alternativeBackground = 0;

        /* creating two numbers for comparison: first represent number of pixels similar to pixel[0][0] */
        int first = 0;

        /* represent number of pixels different from pixel[0][0] */
        int second = 0;

        /* sorting pixels into two groups: the biggest one will be background */
        for (int[] pixel : pixels) {
            if (pixel[0] == background)
                first++;
            else {
                second++;
                alternativeBackground += pixel[0]; // correction for different luminosities
            }
        }
        /* deciding where is background */
        if (first > second)
            return background;

        return alternativeBackground / second; // average value
    }

    /**
     * This method rewrite image into boolean array wherein true cells represents silhouettes
     *
     * @param pixels The array with luminosities
     * @return array with separated silhouettes
     */
    private boolean[][] getSilhouettes(int[][] pixels) {

        /* initialising boolean array for silhouettes */
        boolean[][] silhouette = new boolean[pixels.length][pixels[0].length];

        /* found  brightness of background */
        int background = backgroundLuminosity(pixels);

        /* marking silhouette pixels as true */
        for (int row = 1; row < pixels.length - 1; row++) {
            for (int col = 1; col < pixels[0].length - 1; col++) {
                if (pixels[row][col] < background - DIFFERENCE || pixels[row][col] > background + DIFFERENCE)
                    silhouette[row][col] = true;
            }
        }
        return silhouetteDivider(silhouette);
    }

    /**
     * Reads array until all silhouettes will be found
     *
     * @param silhouette boolean array with silhouettes
     * @return the number of silhouettes
     */
    private int findSilhouettes(boolean[][] silhouette) {

        /* array list which holds coordinates of not visited vertexes of single silhouette */
        ArrayList<String> coordinates = new ArrayList<>();

        /* this list holds number of pixels in each silhouette */
        ArrayList<Integer> silhouetteCapacity = new ArrayList<>();

        /* silhouette counter */
        int count = 0;

        /* loop for finding first vertex of silhouette */
        for (int i = 1; i < silhouette.length - 1; i++) {
            for (int j = 1; j < silhouette[0].length - 1; j++) {
                if (silhouette[i][j]) {
                    totalPixels = 0;

                    /* adding first vertex into list of search */
                    coordinates.add(i + " " + j);
                    breadthSearch(coordinates, silhouette, i, j);

                    /* throwing away very small garbage */
                    if (totalPixels > THRESHOLD) {
                        count++;
                        silhouetteCapacity.add(totalPixels);
                    }
                }
            }
        }
        /* final calculations */
        count = count - garbageFinder(silhouetteCapacity);
        return count;
    }

    /**
     * The breadthSearch algorithm:
     * this method works until can find adjacent point which value is true and makes it false
     * after each ending of this algorithm the number of silhouettes will be increased by one
     *
     * @param silhouette Array with silhouettes
     * @param x          The X coordinate of first pixel of silhouette which value is true
     * @param y          The Y coordinate of first pixel of silhouette which value is true
     */
    private void breadthSearch(ArrayList<String> coordinates, boolean[][] silhouette, int x, int y) {

        /* mark first vertex as visited */
        silhouette[x][y] = false;

        /*starting breadth search from the first vertex */
        while (!coordinates.isEmpty()) {

            /* taking next vertex from the queue */
            String[] s = coordinates.get(0).split(" ");
            x = Integer.parseInt(s[0]);
            y = Integer.parseInt(s[1]);
            coordinates.remove(0);

            /* finding adjacent vertexes and adding them into queue; counting pixels in silhouette */
            for (int i = x + 1; i >= x - 1; i--) {
                for (int j = y - 1; j <= y + 1; j++) {
                    if (silhouette[i][j]) {
                        totalPixels++;

                        /* mark visited */
                        silhouette[i][j] = false;

                        /* adding to queue */
                        if (!coordinates.contains(i + " " + j))
                            coordinates.add(i + " " + j);
                    }
                }
            }
        }
    }

    /**
     * This method finds the biggest silhouette and uses it in comparison:
     * each silhouette which less than 1/10 from biggest to be counted as a garbage
     *
     * @param silhouetteCapacity list with silhouette sizes
     * @return number of garbage
     */
    private int garbageFinder(ArrayList<Integer> silhouetteCapacity) {

        /* holds maximal size of silhouette */
        int max = 0;

        /* finding maximal size */
        for (Integer integer : silhouetteCapacity) {
            if (max < integer)
                max = integer;
        }
        /* number of garbage silhouettes */
        int garbage = 0;

        /* finding number of garbage */
        for (Integer integer : silhouetteCapacity) {
            if (integer < max / DIVIDER)
                garbage++;
        }
        return garbage;
    }

    /**
     * This method separate silhouettes stuck together by one or two pixels on both directions
     *
     * @param silhouette The array with silhouettes
     * @return Array with corrections
     */
    private boolean[][] silhouetteDivider(boolean[][] silhouette) {

        int row = 1;
        while (row < silhouette.length - 2) {
            int col = 1;
            while (col < silhouette[0].length - 2) {

                /* one pixel on X axis */
                if (silhouette[row][col] && !silhouette[row][col - 1] && !silhouette[row][col + 1]) {
                    silhouette[row][col] = false;

                    /* one pixel on Y axis */
                } else if (silhouette[row][col] && !silhouette[row - 1][col] && !silhouette[row + 1][col]) {
                    silhouette[row][col] = false;

                    /* two pixels on X axis */
                } else if (silhouette[row][col] && silhouette[row][col + 1]
                        && !silhouette[row][col - 1] && !silhouette[row][col + 2]) {
                    silhouette[row][col] = false;
                    silhouette[row][col + 1] = false;

                    /* two pixels on Y axis */
                } else if (silhouette[row][col] && silhouette[row + 1][col]
                        && !silhouette[row - 1][col] && !silhouette[row + 2][col]) {
                    silhouette[row][col] = false;
                    silhouette[row + 1][col] = false;
                }
                col++;
            }
            row++;
        }
        return silhouette;
    }
}