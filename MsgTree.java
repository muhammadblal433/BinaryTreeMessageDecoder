import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.Stack;

/**
 * This class implements a binary tree structure to decode messages encoded
 * using a binary-tree-based encoding scheme. It provides functionality to
 * construct the tree from an encoded string, decode messages, and calculate
 * statistics about the encoded data.
 * 
 * The class uses an iterative approach to construct the binary tree, avoiding
 * recursion. It uses a stack to manage parent-child relationships while
 * building the tree. Internal nodes (`^`) represent structural branches, while
 * leaf nodes contain characters used in the message. The resulting tree allows
 * traversal for decoding messages and generating encoded statistics.
 * 
 * Key features: 
 * - Tree Construction: Implements an iterative solution to build
 * the binary tree from a pre-order traversal string (`encodingString`). 
 * - Message Decoding: Traverses the binary tree to decode a compressed message
 * (`encodingString`). 
 * - Statistics Calculation: Provides statistics about the
 * encoded and decoded messages, including average bits per character, total
 * characters, and space savings.
 * 
 * The class includes the following fields: 
 * - payloadChar: Represents the character stored at a node (leaf nodes store characters, while internal nodes
 * are marked with `^`). 
 * - left and right: Represent the left and right child nodes of the binary tree. 
 * - encodeString: Stores the binary tree's structure as a string. 
 * - encodingString: Contains the compressed binary message to decode. 
 * - MsgTreeFile: The file path for the `.arch` file being processed. 
 * - wrongFileType: A boolean to track whether the provided file has the correct extension (`.arch`).
 * 
 * Overall, this class combines tree construction, traversal, and data decoding
 * to efficiently reconstruct encoded messages and calculate meaningful statistics.
 * 
 * @author Muhammad Blal
 */

public class MsgTree {

	/**
	 * Character represented by this node in the tree. Internal nodes are marked
	 * with '^'.
	 */
	public char payloadChar;

	/**
	 * Left child of the current node, representing a '0' in the binary encoding.
	 */

	public MsgTree left;

	/**
	 * Right child of the current node, representing a '1' in the binary encoding.
	 */

	public MsgTree right;

	/**
	 * Encoding string used to construct the tree.
	 */

	private static String encodeString;

	/**
	 * Encoded binary message to be decoded.
	 */

	private static String encodingString;

	/**
	 * File path to the encoded message file.
	 */

	private static String MsgTreeFile;

	/**
	 * Initialize a boolean to track if the file is the correct type, '.arch'
	 */

	private static boolean wrongFileType = false;

	/**
	 * MsgTree constructor that initializes the payloadChar with a given character
	 * 
	 * @param payLoadChar The character to set as the payload
	 */
	public MsgTree(char payloadChar) {
		this.payloadChar = payloadChar;
	}

	/**
	 * Constructor building the tree iteratively from a string representation of the
	 * tree structure.
	 * 
	 * This method processes each character in the encoding string to build a binary
	 * tree. Internal nodes (`^`) are pushed onto a stack until their left and right
	 * children are assigned. Leaf nodes (non-`^`) are directly assigned as children
	 * to the current node. 
	 * 
	 * The process begins by assigning the first character of the encoding string to the 
	 * root node and pushing it onto a stack. For each subsequent character in the string:
	 * - If the node is an internal node (`^`), it is pushed onto the stack for later child to be assigned.
	 * - If the node is a leaf node (any non `^` character), it is assigned as either the left or right
	 *   child of the current node, depending on the `theLeftChild` flag.
	 * - After both children are assigned to a node, it is popped from the stack to continue
	 *   the process for the next available parent node.
	 * 
	 * @param encodingString The string representing the binary tree in pre-order format.
	 */
	public MsgTree(String encodingString) {
	    // Variable to keep track of the current position in the encoding string
	    int index = 0;

	    // Stack to hold MsgTree nodes while building the tree iteratively
	    Stack<MsgTree> stack = new Stack<>();

	    // Initialize the payloadChar for the root node and push it onto the stack
	    this.payloadChar = encodingString.charAt(index++); // Root node initialized with the first character
	    stack.push(this); // Push the root node onto the stack

	    // Initialize a MsgTree pointer to track the current node being processed
	    MsgTree currentMsgTree = this; // Start processing from the root node

	    // Flag to track whether the current assigned node is for the left or right child
	    boolean theLeftChild = true; // Start by assigning the left child

	    // Process each character in the encoding string to construct the tree
	    while (index < encodingString.length()) {
	        // Create a new node for the current character
	        MsgTree node = new MsgTree(encodingString.charAt(index++)); // Create a new MsgTree for the current character

	        if (!theLeftChild) {
	            // Assign to the right child if theLeftChild is false
	            currentMsgTree.right = node; // Assign the current node as the right child

	            // If the current node is an internal node, push it onto the stack
	            if (node.payloadChar == '^') {
	                stack.push(node); // Push internal node onto the stack
	                currentMsgTree = node; // Update the currentMsgTree to this new node
	                theLeftChild = true; // The next node to be assigned for the left child
	            } else {
	                // Otherwise, pop the stack if available
	                if (!stack.empty()) {
	                    currentMsgTree = stack.pop(); // Move to the next available parent node
	                }
	                theLeftChild = false; // Prepare for the next right child
	            }
	        } else {
	            // Assign to the left child if theLeftChild is true
	            currentMsgTree.left = node; // Assign the current node as the left child

	            // If the current node is an internal node, push it onto the stack
	            if (node.payloadChar == '^') {
	                stack.push(node); // Push internal node onto the stack
	                currentMsgTree = node; // Update the currentMsgTree to this new node
	                theLeftChild = true; // The next node to be assigned for the left child
	            } else {
	                // Otherwise, pop the stack if available
	                if (!stack.empty()) {
	                    currentMsgTree = stack.pop(); // Move to the next available parent node
	                }
	                theLeftChild = false; // Prepare for the next right child
	            }
	        }
	    }
	}

	/**
	 * Main method to prompt the user for an input file (checks that the file has a
	 * .arch extension and exist), validate it, construct the tree, decode the
	 * message, and print the results and statistics.
	 * 
	 * @param args Command-line arguments.
	 * @throws FileNotFoundException If the specified file does not exist or has an
	 *                               invalid format.
	 */
	public static void main(String[] args) throws FileNotFoundException {

		// Validate the file input provided by the user.
		try {
			MsgTreeFile = args[0]; // Read the file name.
			File file = new File(MsgTreeFile); // Create a File object.

			// Check if the file has the required '.arch' extension.
			if (!file.getAbsolutePath().endsWith(".arch")) {
				wrongFileType = true; // Mark the file type as incorrect.
				throw new FileNotFoundException(); // Throw an exception for invalid file type.
			}
		} catch (Exception e) {
			// Handle errors for invalid file type or missing file.
			if (wrongFileType) {
				throw new FileNotFoundException(
						"File '" + MsgTreeFile + "' is the wrong file type. It must have a .arch extension.");
			} else {
				throw new FileNotFoundException("File '" + MsgTreeFile + "' does not exist.");
			}
		}

		// Read and process the file contents to extract the encoding string and
		// message.
		extractEncodingData();

		// Construct the binary tree from the encoding string.
		MsgTree messageTree = new MsgTree(encodeString);

		// Print the header and character codes.
		System.out.println("character            code: \n--------------------------");
		printCodes(messageTree, ""); // Display the encoding for each character.

		// Decode the binary message and print it.
		System.out.println("-----------------------\nMessage:");
		String decodeMessage = decode(messageTree, encodingString);
		System.out.println(decodeMessage);

		// Calculate and display statistics about the encoding and decoding.
		statistics(encodingString, decodeMessage);
	}

	/**
	 * Reads the contents of the encoded file to extract the encoding string and
	 * binary message. Handles cases where the encoding string spans multiple lines.
	 * If a line contains only binary digits ('0' or '1'), it is treated as the
	 * binary message; otherwise, it is appended to the encoding string.
	 * 
	 * @throws FileNotFoundException If the file cannot be found.
	 */
	private static void extractEncodingData() throws FileNotFoundException {
		try (Scanner sc = new Scanner(new File(MsgTreeFile))) {
			// Initialize StringBuilder for the encoding string.
			StringBuilder constructString = new StringBuilder();

			// Read and append lines to the encoding string until the binary message is
			// found.
			while (sc.hasNextLine()) {
				String currentLine = sc.nextLine(); // Read the current line.

				// Check if the line contains only binary characters ('0' and '1').
				if (currentLine.matches("[01]+")) {
					// If the line is binary, assign it to encodingString and stop reading further.
					encodingString = currentLine;
					break;
				} else {
					// Otherwise, append the line to the encoding string.
					if (constructString.length() > 0) {
						constructString.append("\n"); // Add a newline for multi-line encoding strings.
					}
					constructString.append(currentLine);
				}
			}

			// Finalize the encoding string.
			encodeString = constructString.toString();
		} catch (FileNotFoundException e) {
			throw new FileNotFoundException("The file '" + MsgTreeFile + "' does not exist.");
		}
	}

	/**
	 * Recursively prints the character node for each leaf node in the MsgTree. if
	 * the node is a leaf, it prints the character and its corresponding code. For
	 * internal nodes, it continues to traverse the left and right subtrees,
	 * appending '0' for the left and '1' for the right.
	 * 
	 * @param root The root of the binary tree.
	 * @param code The current binary code for traversal.
	 */
	public static void printCodes(MsgTree root, String code) {
		if (root == null) {
			return; // Stop recursion when reaching a null node.
		}

		char payload = root.payloadChar; // Get the character of the current node.

		if (payload != '^') { // Only print leaf nodes (characters).
			if (payload == '\n') {
				System.out.print("\\n			");
			} else {
				System.out.print(root.payloadChar + "			");
			}
			System.out.println(code); // Print the binary code for this character.
		}
		printCodes(root.left, code + "0"); // Traverse the left subtree (add '0').
		printCodes(root.right, code + "1"); // Traverse the right subtree (add '1').
	}

	/**
	 * Decodes the binary message using the tree structure by traversing it based on
	 * each bit of the message ('0' for left, '1' for right).
	 * 
	 * @param codes   The root of the binary tree.
	 * @param message The binary string representing the encoded message.
	 * @return The decoded message as a string.
	 */
	public static String decode(MsgTree codes, String message) {
		StringBuilder decodeMessage = new StringBuilder(); // Store the decoded message.
		MsgTree currentNode = codes; // Start traversal from the root of the tree.

		for (int i = 0; i < message.length(); i++) {
			// Traverse left for '0' and right for '1'.
			currentNode = (message.charAt(i) == '0') ? currentNode.left : currentNode.right;

			// Append the character when reaching a leaf node.
			if (currentNode.left == null && currentNode.right == null) {
				decodeMessage.append(currentNode.payloadChar);
				currentNode = codes; // Reset to the root for the next character.
			}
		}
		return decodeMessage.toString(); // Return the decoded message.
	}

	/**
	 * Calculates and prints statistics for the encoded and decoded messages,
	 * including average bits per character, total characters, and space savings.
	 * 
	 * @param encodingString The binary-encoded message.
	 * @param decodedString  The decoded message as a string.
	 */
	public static void statistics(String encodingString, String decodedString) {
		// Print a header for the statistics section
		System.out.println("\nSTATISTICS:");

		// Calculate the average number of bits used per character and print it
		// This is done by dividing the total number of bits (length of the encoding
		// string)
		// by the number of characters in the decoded message.
		System.out.printf("Avg bits/char:       \t%.1f%n", encodingString.length() / (double) decodedString.length());

		// Print the total number of characters in the decoded message
		System.out.println("Total characters:    \t" + decodedString.length());

		// Calculate and print the space savings achieved by encoding
		// Space savings formula: (1 - (decoded length / encoded length)) * 100
		// This assumes that the uncompressed message uses 1 character = 1 unit of
		// space.
		System.out.printf("Space Savings:       \t%.1f%%%n",
				(1d - decodedString.length() / (double) encodingString.length()) * 100);
	}

}
