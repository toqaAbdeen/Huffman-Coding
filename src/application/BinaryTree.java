package application;

import java.io.Serializable;

public class BinaryTree implements Serializable {

	char ch;
	String huffCode;
	BinaryTree left;
	BinaryTree right;
	private boolean printed = false; // Flag to track if the character has been printed
	private Node root;
	private int frequency;

	public Node getRoot() {
		return root;
	}

	public void setRoot(Node root) {
		this.root = root;
	}

	public BinaryTree() {
		ch = '\0';
		huffCode = "";
		left = right = null;
		frequency = 0; // Initialize frequency
	}

	// Constructor with character, Huffman code, and frequency
	public BinaryTree(char ch, String huffCode, int frequency) {
		this.ch = ch;
		this.huffCode = huffCode;
		this.frequency = frequency; // Set frequency
		left = right = null;
	}

	public int size() {
		// If the tree is null, size is 0
		if (this == null) {
			return 0;
		}

		// If it's a leaf node, count 1
		int leftSize = (left == null) ? 0 : left.size(); // Count left subtree
		int rightSize = (right == null) ? 0 : right.size(); // Count right subtree

		return 1 + leftSize + rightSize; // Count the current node + left and right subtrees
	}

	// Getter and Setter methods (optional but recommended for encapsulation)
	public char getCh() {
		return ch;
	}

	public void setCh(char ch) {
		this.ch = ch;
	}

	public String getHuffCode() {
		return huffCode;
	}

	public void setHuffCode(String huffCode) {
		this.huffCode = huffCode;
	}

	public BinaryTree getLeft() {
		return left;
	}

	public void setLeft(BinaryTree left) {
		this.left = left;
	}

	public BinaryTree getRight() {
		return right;
	}

	public void setRight(BinaryTree right) {
		this.right = right;
	}

	public int getFrequency() {
		return frequency;
	}

	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}

	public String inorderTraversal() {
		StringBuilder result = new StringBuilder();
		if (this.left != null) {
			result.append(this.left.inorderTraversal()); // Traverse the left subtree
		}

		// Only print valid nodes (non-empty) and avoid printing unnecessary spaces or
		// null characters.
		if (this.ch != 0) { // Make sure the character is not null or empty.
			result.append(this.toString()).append("\n");
		}

		if (this.right != null) {
			result.append(this.right.inorderTraversal()); // Traverse the right subtree
		}

		return result.toString();
	}

	@Override
	public String toString() {
		if (ch == ' ') {
			return "Character: space -> Huffman Code: " + huffCode + " (Bits: " + huffCode.length() + "), Frequency: "
					+ frequency;
		}
		return "Character: " + ch + " -> Huffman Code: " + huffCode + " (Bits: " + huffCode.length() + "), Frequency: "
				+ frequency;
	}
}
