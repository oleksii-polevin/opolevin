

/**
 * File Silhouettes.java
 * ---------------------
 * This program calculated number of silhouettes
 * in the given picture.
 * <p>
 * It uses breadth search algorithm
 */
public class Silhouettes implements Constants {

    public static void main(String[] args) {

        long start = System.currentTimeMillis();
        /* initialising all necessary classes */
        ImageReader reader = new ImageReader();
        Logic logic = new Logic();

        String filename;
        if (args.length > 0) {
            filename = args[0];
        } else filename = NAME;

        /*finding number of silhouettes and printing results */
        int number = logic.silhouettesNumber(reader.getPixels(filename));
        System.out.print("File: " + filename + "\n"
                + "The total number of silhouettes in the picture: " + number);
        long end = System.currentTimeMillis();
        System.out.println("\n" + "time: " + (end - start));
    }
}