package application;

import java.io.*;

public class FileDecompressor {

	// Decompress the file using the Huffman tree (BinaryTree)
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

	// Build the Huffman tree from a frequency table
	public static Node buildHuffmanTree(int[] frequencyTable) {
		Heap heap = new Heap(frequencyTable.length);

		// Insert all nodes with non-zero frequency into the heap
		for (int i = 0; i < frequencyTable.length; i++) {
			if (frequencyTable[i] > 0) {
				heap.addElement(new Node(frequencyTable[i], (char) i));
			}
		}

		// Combine the two smallest nodes until only one node is left in the heap
		while (heap.getSize() > 1) {
			Node left = heap.deleteElement();
			Node right = heap.deleteElement();
			Node internalNode = new Node(left.getFreq() + right.getFreq(), '\0');
			internalNode.setLeft(left);
			internalNode.setRight(right);
			heap.addElement(internalNode);
		}

		return heap.deleteElement();
	}

	// Convert a Node-based tree to a BinaryTree structure
	private static BinaryTree convertToBinaryTree(Node root) {
		if (root == null)
			return null;

		BinaryTree tree = new BinaryTree(root.getCh(), "", root.getFreq());
		tree.left = convertToBinaryTree(root.getLeft());
		tree.right = convertToBinaryTree(root.getRight());
		return tree;
	}

	// Read the Huffman tree from the header file
	public static BinaryTree readHuffmanTree(String headerFile) throws IOException, ClassNotFoundException {
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(headerFile))) {
			return (BinaryTree) ois.readObject(); // Deserialize and return the Huffman tree
		}
	}
}
