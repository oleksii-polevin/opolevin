

/**
 * File Constants.java
 * -------------------
 * Contains necessary constants
 */
public interface Constants {

    /* used for finding boundary between silhouettes and garbage
     * by dividing the biggest silhouette on 10, thus silhouette which size
     * less than 10% from biggest will be counted as a garbage */
    int DIVIDER = 10;

    /* the name of the file */
    String NAME = "test.jpg";

    /* difference between background and object luminosities.
     * Coefficient 20 gives good opportunity to distinguish
     * between objects and background */
    int DIFFERENCE = 20;


    /* minimal number of pixels pretending to be silhouette */
    int THRESHOLD = 50;
}
