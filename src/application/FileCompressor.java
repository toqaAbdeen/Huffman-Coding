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

	/**
	 * The buildHuffmanTree method creates a Huffman tree using a table that shows
	 * how often each character appears. It starts by putting each character with a
	 * non-zero frequency into a heap, which is a special structure that helps
	 * quickly find the smallest values. Then, it builds the tree by repeatedly
	 * taking the two characters with the smallest frequencies out of the heap,
	 * combining them into a new parent node with their combined frequency, and
	 * adding this parent back to the heap. This process continues until only one
	 * node is left in the heap, which becomes the root of the Huffman tree.
	 * Finally, the method converts the nodes into a BinaryTree structure using the
	 * convertToBinaryTree method, which organizes the nodes into a format suitable
	 * for encoding. This tree assigns shorter binary codes to more frequent
	 * characters.
	 **/
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
		if (root == null)
			return null;

		BinaryTree tree = new BinaryTree(root.getCh(), "", frequencyTable[root.getCh()]);
		tree.left = convertToBinaryTree(root.getLeft(), frequencyTable);
		tree.right = convertToBinaryTree(root.getRight(), frequencyTable);
		return tree;
	}

	// Generate Huffman codes
	private void generateHuffmanCodes(BinaryTree tree, StringBuilder code) {
		if (tree == null)
			return;

		if (tree.left == null && tree.right == null) {
			tree.huffCode = code.toString();
			return;
		}

		generateHuffmanCodes(tree.left, code.append('0'));
		code.deleteCharAt(code.length() - 1);
		generateHuffmanCodes(tree.right, code.append('1'));
		code.deleteCharAt(code.length() - 1);
	}

	/**
	 * The compress method compresses a file's contents into a byte array using
	 * Huffman coding, making it suitable for scenarios where compressed data needs
	 * to be processed in memory. It begins by creating a ByteArrayOutputStream to
	 * store the compressed output and a BufferedInputStream to read the file
	 * efficiently. The method reads each byte from the input file, retrieves its
	 * corresponding Huffman code, and processes the code bit by bit. Bits are
	 * accumulated in a temporary buffer (bitBuffer) until it forms a full byte,
	 * which is then written to the output stream. If any bits remain after
	 * processing all bytes, they are padded with zeros and written as the final
	 * byte.
	 **/

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
		if (tree == null)
			return null;

		if (tree.ch == target)
			return path.toString();

		path.append('0');
		String leftPath = findHuffmanCode(tree.left, target, path);
		if (leftPath != null)
			return leftPath;
		path.deleteCharAt(path.length() - 1);

		path.append('1');
		String rightPath = findHuffmanCode(tree.right, target, path);
		if (rightPath != null)
			return rightPath;
		path.deleteCharAt(path.length() - 1);

		return null;
	}

	public String displayHuffmanTree() {
		if (huffmanTree != null) {
			return huffmanTree.inorderTraversal();
		}
		return "Huffman Tree is empty!";
	}
}