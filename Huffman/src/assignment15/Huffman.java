package assignment15;

import java.io.*;
import java.util.HashMap;
import java.util.PriorityQueue;

public class Huffman {
    /*
    number of bits, also equals to number of bytes used
    for writing uncompressed size of file in bytes
   */
    private final static int BYTE = 8;
    /* length of array which holds all symbols from file */
    private final static int SYMBOLS = 258;
    /* index of file size in array */
    private final static int FILE_SIZE_INDEX = 257;
    /* index of frequency table size in array */
    private final static int TABLE_SIZE_INDEX = 256;
    /* twelve bytes offset (table and file sizes) */
    private final static int OFFSET = 12;
    /* first four bytes for table size */
    private final static int TABLE_SIZE = 4;
    /* marks non leaf nodes, value had been chosen arbitrary */
    private final static int MARKER = 100000;


    /**
     * Calculates frequency of each symbol from file if file need to be compressed
     * or takes prepared table from head of file
     *
     * @param filename Name of file
     * @return frequency table
     */
    private int[] frequency(String filename) {

         /* this array has capacity == 258 because it necessary to decompressing:
         cell 256 reserved for length of decompress map and cell 257 for number of bytes */
        int[] freqTable = new int[SYMBOLS];

        try {
            FileInputStream inputStream = new FileInputStream(filename);
            int symbol;
            /* for compress */
            if (!filename.contains(".par")) {
                while ((symbol = inputStream.read()) != -1) {
                    freqTable[symbol]++; // adding frequency of particular symbol
                }
                /* for decompress */
            } else {
                int counter = 0;
                int table = 0; // frequency table length
                int file; // decompressed file length in bytes
                /* holds frequency table written on top of compressed file */
                StringBuilder result = new StringBuilder();
                while ((symbol = inputStream.read()) != -1) {
                    result.append((char) symbol);
                    counter++;
                    if (counter == OFFSET) {
                        table = Integer.parseInt(result.substring(0, TABLE_SIZE));// parsing table length
                        file = Integer.parseInt(result.substring(TABLE_SIZE));// parsing file length
                        freqTable[TABLE_SIZE_INDEX] = table;
                        freqTable[FILE_SIZE_INDEX] = file;
                        result = new StringBuilder();
                    }
                    if (counter >= table + OFFSET)
                        break;
                }
                /* building frequency table: each even cell = symbol; each odd = it frequency */
                String[] frequencyTable = result.toString().split(" ");
                for (int i = 0; i < frequencyTable.length - 1; i += 2) {
                    freqTable[Integer.parseInt(frequencyTable[i])] = Integer.parseInt(frequencyTable[i + 1]);
                }
            }
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return freqTable;
    }

    /**
     * Building Huffman tree
     *
     * @param freqTable The table of frequency of each symbol from file
     * @return Huffman tree
     */
    private Node buildTree(int[] freqTable) {

        PriorityQueue<Node> huffmanTree = new PriorityQueue<>();

        /* adding 'leaves' to the queue */
        for (int i = 0; i < freqTable.length - 2; i++) {
            if (freqTable[i] != 0) {
                huffmanTree.add(new Node(i, freqTable[i], null, null));
            }
        }
        /* avoiding null pointer exceptions for files with only one symbol */
        if (huffmanTree.size() == 1)
            huffmanTree.add(new Node(MARKER + 1, 1, null, null));

        /* building huffman tree */
        while (huffmanTree.size() > 1) {
            Node right = huffmanTree.poll(); // least frequency node
            Node left = huffmanTree.poll(); // next frequency
            Node combinedNode = new Node(MARKER, right.frequency + left.frequency, right, left);
            huffmanTree.add(combinedNode);
        }
        return huffmanTree.poll(); //root node
    }

    /**
     * Building map for compressing information
     *
     * @param node The Huffmann node
     * @return Hash map where each integer from text associated with its binary code
     */
    private HashMap<Integer, String> compressMap(Node node) {

        HashMap<Integer, String> compressMap = new HashMap<>();
        String binary = "";
        buildCompressMap(node, binary, compressMap);
        return compressMap;
    }

    /**
     * Using recursion visiting all nodes of huffman tree and writing codes of each symbol to hash map
     *
     * @param node        The Huffmann tree
     * @param binary      String for coding particular symbol
     * @param compressMap hash map where symbols from text associated with their codes
     */
    private void buildCompressMap(Node node, String binary, HashMap<Integer, String> compressMap) {
        if (node.integer == MARKER) {
            buildCompressMap(node.rightChildNode, binary + '1', compressMap);
            buildCompressMap(node.leftChildNode, binary + '0', compressMap);
        } else {
            compressMap.put(node.integer, binary);
        }
    }

    /**
     * Building head of file which contains information about table and file lengths
     *
     * @param freqTable The frequency table
     * @return String used for decoding file
     */
    private String buildHeadOfCompressFile(int[] freqTable) {

        int fileSize = 0; // holds the size of file
        StringBuilder tableLength = new StringBuilder(); // table size
        StringBuilder fileLength = new StringBuilder(); // file size
        StringBuilder table = new StringBuilder(); // frequency table itself

        /* calculating the size of file which equals to sum of frequencies of each symbol
         * also adding frequency table to string in order to write it to file */
        for (int i = 0; i < freqTable.length - 2; i++) {
            if (freqTable[i] != 0) {
                fileSize += freqTable[i];
                table.append(i).append(" ").append(freqTable[i]).append(" ");
            }
        }
        int tableSize = table.length();
        tableLength.append(tableSize);
        while (tableLength.length() < TABLE_SIZE) // aligning table size to 4 bytes
            tableLength.insert(0, '0');
        fileLength.append(fileSize);
        while (fileLength.length() < BYTE) // aligning file size to 8 bytes
            fileLength.insert(0, '0');

        return tableLength.toString() + fileLength.toString() + table.toString();
    }

    /**
     * Compressing given file
     *
     * @param fileIn  File for compressing
     * @param fileOut Compressed file
     */
    void compress(String fileIn, String fileOut) {

        long startTime = System.nanoTime();
        int[] freqTable = frequency(fileIn);
        int inFileSize; // input file size
        String head = buildHeadOfCompressFile(freqTable);
        int outFileSize = head.length(); // starting calculations size of compressed file
        HashMap<Integer, String> compressMap = compressMap(buildTree(freqTable));
        try {
            BufferedInputStream input = new BufferedInputStream(new FileInputStream(fileIn));
            BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(fileOut));
            inFileSize = input.available();
            output.write(head.getBytes());
            StringBuilder binaryLine = new StringBuilder();
            int symbol;
            while ((symbol = input.read()) != -1) {
                binaryLine.append(compressMap.get(symbol));
                if (binaryLine.length() % BYTE == 0) {
                    byte[] fragment = compressFragment(binaryLine);
                    outFileSize += fragment.length; // continue calculation size of compressed file
                    output.write(fragment);
                    binaryLine = new StringBuilder();
                }
            }
            /* writes the last piece of file */
            while (binaryLine.length() % BYTE != 0) {
                binaryLine.append('0');
            }
            output.write(compressFragment(binaryLine));
            input.close();
            output.close();
            long endTime = System.nanoTime();
            /* printing results */
            double effectiveness = ((double) inFileSize - outFileSize) / inFileSize * 100;//%
            System.out.println("Compressing time:          " + (endTime - startTime) / 1000_000 + " milliseconds");
            System.out.println("Uncompressed file size:    " + fileIn + " = " + inFileSize + " bytes");
            System.out.println("Compressed file size:      " + fileOut + " = " + outFileSize + " bytes");
            System.out.println("Compressing effectiveness: " + (int) effectiveness + "%");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Compressing part of file
     *
     * @param binaryLine The line with codes of symbols
     * @return compressed byte array
     */
    private byte[] compressFragment(StringBuilder binaryLine) {
        byte[] bytes = new byte[binaryLine.length() / BYTE];
        for (int i = 0; i < bytes.length; i++) {
            String bits = binaryLine.substring(0, BYTE);
            bytes[i] = (byte) Integer.parseInt(bits, 2);
            binaryLine = new StringBuilder(binaryLine.substring(BYTE));
        }
        return bytes;
    }

    /**
     * Decompressing given file
     *
     * @param fileIn  File for decompress
     * @param fileOut Decompressed file
     */
    void deCompress(String fileIn, String fileOut) {

        long startTime = System.nanoTime();
        int[] freqTable = frequency(fileIn);
        Node node = buildTree(freqTable);
        int offset = OFFSET + freqTable[TABLE_SIZE_INDEX];
        int outFileSize = freqTable[FILE_SIZE_INDEX];
        int inFileSize;// input file size
        try {
            FileInputStream inputStream = new FileInputStream(fileIn);
            FileOutputStream outputStream = new FileOutputStream(fileOut);
            inFileSize = inputStream.available(); // compressed file size
            int fileLength = inFileSize - offset; // length of byte array with compressed file
            byte[] compressedInfo = new byte[fileLength]; // array with inputted file
            int counter = 0;
            int symbol;
            while ((symbol = inputStream.read()) != -1) {

                if (offset <= 0) {
                    compressedInfo[counter++] = (byte) symbol;
                }
                offset--;
            }
            outputStream.write(decompress(compressedInfo, outFileSize, node));
            inputStream.close();
            outputStream.close();
            long endTime = System.nanoTime();
            double effectiveness = ((double) outFileSize - inFileSize) / outFileSize * 100;//%
            /* printing results */
            System.out.println("Decompressing time:          " + (endTime - startTime) / 1000_000 + " milliseconds");
            System.out.println("Compressed file size:        " + fileIn + " = " + inFileSize + " bytes");
            System.out.println("Decompressed file size:      " + fileOut + " = " + outFileSize + " bytes");
            System.out.println("Decompressing effectiveness: " + (int) effectiveness + "%");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method creates array with unarchived file
     *
     * @param bytes         array with compressed file
     * @param fileOutLength the length of decompressed file
     * @param node          The root of Huffmann tree
     * @return array with decompressed text
     */
    private byte[] decompress(byte[] bytes, int fileOutLength, Node node) {
        byte[] decompressed = new byte[fileOutLength];
        int counter = 0;
        Node root = node;
        for (byte b : bytes) {
            for (int k = 7; k >= 0; k--) {
                /* writing decoded symbol to array */
                if (node.integer != MARKER) {
                    decompressed[counter++] = (byte) node.integer;
                    node = root;
                    if (counter == fileOutLength)
                        break;
                }
                /* going on huffman tree from root towards leaf according to value of bit */
                int bit = ((b >> k) & 1);
                if (bit == 1) {
                    node = node.rightChildNode;
                } else {
                    node = node.leftChildNode;
                }
            }
        }
        return decompressed;
    }

    /**
     * Class - constructor for building Huffmann tree
     */
    class Node implements Comparable<Node> {
        int integer;
        int frequency;
        Node leftChildNode;
        Node rightChildNode;

        /**
         * Constructor of nodes for Huffmann tree
         *
         * @param integer        The symbol from file
         * @param frequency      Its frequency
         * @param leftChildNode  Left child node
         * @param rightChildNode Right child node
         */
        private Node(int integer, int frequency, Node leftChildNode, Node rightChildNode) {
            this.integer = integer;
            this.frequency = frequency;
            this.leftChildNode = leftChildNode;
            this.rightChildNode = rightChildNode;
        }

        /*method for comparison frequencies of nodes */
        @Override
        public int compareTo(Node x) {
            return Integer.compare(this.frequency, x.frequency);
        }
    }
}
