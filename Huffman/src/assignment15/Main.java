package assignment15;

public class Main {

    public static void main(String[] args) {

        /* Huffmann coder */
        Huffman huffman = new Huffman();

        /* supply huffman coder with names of files */
        Logistic logistic = new Logistic();

        /* contain two file names */
        String[] files = logistic.getFiles(args);

        /* according to first filename program will compress or decompress */
        if (!files[0].contains(".par")) {
            huffman.compress(files[0], files[1]);
        } else
            huffman.deCompress(files[0], files[1]);
    }

}
