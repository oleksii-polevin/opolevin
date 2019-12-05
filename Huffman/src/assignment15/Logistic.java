package assignment15;

public class Logistic {

    /* contains input file and output file */
    private String[] files = new String[2];

    /**
     * @param args The array with filenames and commands
     * @return Array with input and output files
     */
    String[] getFiles(String[] args) {

        /* default case */
        if (args.length == 0) {
            files[0] = "test.txt";
            files[1] = "test.par";

            /* compressing case with command */
        } else if (args[0].equals("-a")) {
            if (args.length == 3) {
                files[0] = args[1];//for compress
                files[1] = args[2];//par
            } else if (args.length == 2) {
                files[0] = args[1];
                files[1] = args[1].substring(0, args[1].indexOf('.')) + ".par";
            } else if (args.length == 1) {
                files[0] = "test.txt";
                files[1] = "test.par";
            }

            /* decompressing case with command */
        } else if (args[0].equals("-u")) {
            if (args.length == 3) {
                files[0] = args[1];//par
                files[1] = args[2];//user name
            } else if (args.length == 2) {
                files[0] = args[1];
                files[1] = args[1].substring(0, args[1].indexOf('.')) + ".uar";
            } else if (args.length == 1) {
                files[0] = "test.par";
                files[1] = "test.txt";
            }

            /* default decompressing case */
        } else if (args[0].contains(".par")) {
            files[0] = args[0];//par
            if (args.length == 1) {
                files[1] = args[0].substring(0, args[0].indexOf('.')) + ".uar"; // assign to default case
            } else files[1] = args[1]; // use user's filename

            /* default compressing case  */
        } else if (!args[0].contains(".par")) {
            files[0] = args[0];// for compress
            if (args.length == 1) {
                files[1] = args[0].substring(0, args[0].indexOf('.')) + ".par";// assign to default case
            } else files[1] = args[1];// use user's filename
        }
        return files;
    }
}
