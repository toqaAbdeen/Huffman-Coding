package application;

import java.io.*;

public class FileDecompressor {

	/**
	 * The decompress method takes a compressed file and a special set of rules
	 * (Huffman tree). It follows these rules to decode the compressed file and save
	 * the original file.
	 * 
	 * First, it converts the compressed file into a long string of 0s and 1s. Then,
	 * it starts at the beginning of this string and follows the rules in the
	 * Huffman tree.
	 * 
	 * Each 0 or 1 tells the method to go left or right in the tree. When it reaches
	 * the end of a branch (a leaf), it finds a letter and writes it to the new
	 * file. Then, it starts again from the beginning of the string and the tree.
	 * 
	 * 
	 * This process continues until the entire string of 0 s and 1s is used, and the
	 * original file is fully restored.
	 **/

	public static void decompress(byte[] compressedData, BinaryTree huffmanTree, String outputFile) throws IOException {
		try (FileOutputStream fos = new FileOutputStream(outputFile)) {
			StringBuilder binaryString = new StringBuilder();

			// Convert the byte array into a binary string
			for (byte b : compressedData) {
				String binary = String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0');
				binaryString.append(binary);
			}

			BinaryTree current = huffmanTree;

			// Decompress the binary string using the Huffman tree
			for (int i = 0; i < binaryString.length(); i++) {
				if (binaryString.charAt(i) == '0') {
					current = current.left;
				} else {
					current = current.right;
				}

				// When a leaf node is reached, write the decoded character
				if (current.left == null && current.right == null) {
					fos.write(current.ch);
					current = huffmanTree; // Reset to root of Huffman tree
				}
			}
		}
	}

}
