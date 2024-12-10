package application;

import java.io.*;
import java.util.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.BufferedInputStream;

public class FileCompressor {
    private BinaryTree huffmanTree;

    public BinaryTree getHuffmanTree() {
        return huffmanTree;
    }

    public void setHuffmanTree(BinaryTree huffmanTree) {
        this.huffmanTree = huffmanTree;
    }

    // Compress the file and write the header
    public void compress(String inputFile, String compressedFile, String headerFile) throws IOException {
        int[] frequencyTable = new int[256];
        try (BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(inputFile))) {
            int byteRead;
            while ((byteRead = inputStream.read()) != -1) {
                frequencyTable[byteRead]++;
            }
        }

        // Build the Huffman Tree using the frequency table
        huffmanTree = buildHuffmanTree(frequencyTable);

        // Generate Huffman codes
        StringBuilder codeBuilder = new StringBuilder();
        generateHuffmanCodes(huffmanTree, codeBuilder);

        // Compress the file
        compressFile(inputFile, compressedFile);

        // Write the header to the header file
        writeHeader(headerFile);
    }

    public static BinaryTree buildHuffmanTree(int[] frequencyTable) {
        Heap heap = new Heap(256);
        for (int i = 0; i < frequencyTable.length; i++) {
            if (frequencyTable[i] > 0) {
                heap.addElement(new Node(frequencyTable[i], (char) i));
            }
        }

        while (heap.getSize() > 1) {
            Node left = heap.deleteElement();
            Node right = heap.deleteElement();
            Node parent = new Node(left.getFreq() + right.getFreq(), '\0');
            parent.setLeft(left);
            parent.setRight(right);
            heap.addElement(parent);
        }

        return convertToBinaryTree(heap.deleteElement(), frequencyTable);
    }

    private static BinaryTree convertToBinaryTree(Node root, int[] frequencyTable) {
        if (root == null) return null;

        BinaryTree tree = new BinaryTree(root.getCh(), "", frequencyTable[root.getCh()]);
        tree.left = convertToBinaryTree(root.getLeft(), frequencyTable);
        tree.right = convertToBinaryTree(root.getRight(), frequencyTable);
        return tree;
    }

    // Generate Huffman codes
    private void generateHuffmanCodes(BinaryTree tree, StringBuilder code) {
        if (tree == null) return;

        if (tree.left == null && tree.right == null) {
            tree.huffCode = code.toString();
            return;
        }

        generateHuffmanCodes(tree.left, code.append('0'));
        code.deleteCharAt(code.length() - 1);
        generateHuffmanCodes(tree.right, code.append('1'));
        code.deleteCharAt(code.length() - 1);
    }

    private void compressFile(String inputFile, String compressedFile) throws IOException {
        try (BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(inputFile));
             BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(compressedFile))) {
            int byteRead;
            int bitBuffer = 0;
            int bitCount = 0;

            while ((byteRead = inputStream.read()) != -1) {
                String code = findHuffmanCode(huffmanTree, (char) byteRead, new StringBuilder());
                if (code == null) {
                    throw new IllegalArgumentException("Byte '" + byteRead + "' is missing in the Huffman tree.");
                }

                for (char bit : code.toCharArray()) {
                    bitBuffer = (bitBuffer << 1) | (bit - '0');
                    bitCount++;

                    if (bitCount == 8) {
                        outputStream.write(bitBuffer);
                        bitBuffer = 0;
                        bitCount = 0;
                    }
                }
            }

            if (bitCount > 0) {
                bitBuffer <<= (8 - bitCount);
                outputStream.write(bitBuffer);
            }
        }
    }

    public byte[] compress(String inputFilePath, BinaryTree huffmanTree) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        
        try (BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(inputFilePath))) {
            int byteRead;
            int bitBuffer = 0;
            int bitCount = 0;

            while ((byteRead = inputStream.read()) != -1) {
                String code = findHuffmanCode(huffmanTree, (char) byteRead, new StringBuilder());
                if (code == null) {
                    throw new IllegalArgumentException("Byte '" + byteRead + "' is missing in the Huffman tree.");
                }

                for (char bit : code.toCharArray()) {
                    bitBuffer = (bitBuffer << 1) | (bit - '0');
                    bitCount++;

                    if (bitCount == 8) {
                        byteArrayOutputStream.write(bitBuffer);
                        bitBuffer = 0;
                        bitCount = 0;
                    }
                }
            }

            if (bitCount > 0) {
                bitBuffer <<= (8 - bitCount);
                byteArrayOutputStream.write(bitBuffer);
            }
        }
        
        return byteArrayOutputStream.toByteArray();
    }

    private String findHuffmanCode(BinaryTree tree, char target, StringBuilder path) {
        if (tree == null) return null;

        if (tree.ch == target) return path.toString();

        path.append('0');
        String leftPath = findHuffmanCode(tree.left, target, path);
        if (leftPath != null) return leftPath;
        path.deleteCharAt(path.length() - 1);

        path.append('1');
        String rightPath = findHuffmanCode(tree.right, target, path);
        if (rightPath != null) return rightPath;
        path.deleteCharAt(path.length() - 1);

        return null;
    }

    private void writeHeader(String headerFile) throws IOException {
        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(headerFile))) {
            dos.writeUTF("Huffman Representation");
            dos.writeUTF("huffman");
            dos.writeUTF(".huff");
            dos.writeLong(huffmanTree.size()); // Assuming the header size is the size of the Huffman tree
            ObjectOutputStream oos = new ObjectOutputStream(dos);
            oos.writeObject(huffmanTree);
        }
    }

    public String displayHuffmanTree() {
        if (huffmanTree != null) {
            return huffmanTree.inorderTraversal();
        }
        return "Huffman Tree is empty!";
    }
}