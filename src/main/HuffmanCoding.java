package main;

import java.io.File;
import java.util.Scanner;

import map.HashTableSC;
import map.Map;
import sortedList.SortedArrayList;
import sortedList.SortedList;

/**
 * Class that takes a text file with only one line and compresses it using
 * the Huffman coding method. We first take the text file an extract it's string,
 * then we proceed to count how many times each character appears in the string,
 * we store this frequency distribution in a map. Then using that map we create a
 * binary tree, where the lower the frequency for that character, the deeper it
 * will be in the tree. After creating the binary tree, we then traverse to each leaf, 
 * when traversing to a specific leaf, when we move left we add '0' and when we move
 * right we add '1' to its code. We store the corresponding character and it's code
 * in a map which we use to replace every character in the original string with 
 * its huffman code equivalent.
 * 
 * @author Abdiel Cortés
 *
 */
public class HuffmanCoding {
	
	// string containing the path to the txt file containing the string to be encoded
	private final String FILE_PATH; 
	
	/**
	 * Constructor that uses the parameter to assign the filePath of the txt file to be encoded.
	 * 
	 * @param filePath file path of the txt file to be encoded
	 */
	public HuffmanCoding(String filePath) {
		this.FILE_PATH = filePath;
	}
	
	/**
	 * Constructor that sets the file path to a default value of "inputData/stringData.txt" .
	 */
	public HuffmanCoding() {
		this("inputData/stringData.txt");
	}
	
	/**
	 * Runs every method required to encode the text and prints the results.
	 * This should be the only public method, but for testing purposes we
	 * need the other classes to be public.
	 */
	public void run() {
		String text = this.load_data(this.FILE_PATH);
		
		Map<Character, Integer> fd = this.compute_fd(text); 
		
		BTNode<Integer, String> root = this.huffman_tree(fd);
		
		Map<Character, String> table = this.huffman_code(root);
		
		String encoded = this.encode(table, text);
		
		this.process_results(fd, table, text, encoded);
	}

	/**
	 * Takes the filePath and extracts the string to be encoded from the file.
	 * 
	 * @param filePath file path of the txt file to be encoded
	 * @return string contained in the txt file located at filePath
	 */
	public String load_data(String filePath) {
		File textFile = new File(filePath); // creates file using file path string
		String inputText = null;
		
		try (Scanner scanner = new Scanner(textFile)) { // uses scanner to read file
			inputText = scanner.nextLine(); // file only has one line so by using nextLine we get the entire file
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return inputText;
	}
	
	/**
	 * Computes the frequency distribution of each character in the inputText.
	 * 
	 * @param inputText string to compute its frequency distribution
	 * @return map containing the characters as its keys and their frequencies as their values
	 */
	public Map<Character, Integer> compute_fd(String inputText) {
		// map to store the characters and their frequency
		Map<Character, Integer> map = new HashTableSC<Character, Integer>(); 
		
		for (int i = 0; i < inputText.length(); i++) { // iterate through all characters in the inputText
			Character c = inputText.charAt(i);
			if (map.containsKey(c)) { // if there is and entry corresponding to c already in the map
				map.put(c, map.get(c) + 1); // we update the entry by increasing its value by 1
			} else { // else there is no entry corresponding to c
				map.put(c, 1); // this is the first time we see c, so its only appeared 1 time
			}
		}
		
		return map;
	}
	
	/**
	 * Takes a frequency distribution map and generates a sorted list containing
	 * the nodes corresponding to every entry in the map.
	 * 
	 * @param frequencyDistribution map containing characters as keys and their frequencies as values
	 * @return sorted list containing every BTNode<frequency, character> corresponding to the entries in the map
	 */
	private SortedList<BTNode<Integer, String>> generateFDSortedList(Map<Character, Integer> frequencyDistribution) {
		SortedList<BTNode<Integer, String>> list = new SortedArrayList<BTNode<Integer, String>>(frequencyDistribution.size());
		
		for (Character key: frequencyDistribution.getKeys()) { 
			// takes every entry from the map and uses its key and value to create a BTNode and insert it into the sorted list
			list.add(new BTNode<Integer, String>(frequencyDistribution.get(key), key.toString()));
		}
		
		return list;
	}
	
	/**
	 * Takes the frequency distribution map and creates a binary tree where the lower the 
	 * frequency of a character, the deeper it will be in the tree. 
	 * 
	 * @param frequencyDistribution map containing characters as keys and their frequencies as values
	 * @return root node of the huffman tree
	 */
	public BTNode<Integer, String> huffman_tree(Map<Character, Integer> frequencyDistribution) {
		SortedList<BTNode<Integer, String>> sortedList = generateFDSortedList(frequencyDistribution);
		
		while (sortedList.size() > 1) { // iterates until only one node remains in the list, this will be our root node
			// removes the two smallest nodes in the tree
			BTNode<Integer, String> a = sortedList.removeIndex(0); 
			BTNode<Integer, String> b = sortedList.removeIndex(0);
			
			// create a new node that has a frequency equal to a + b and a symbol equal to a + b
			// who's left child is a and its right child is b
			BTNode<Integer, String> parent = new BTNode<Integer, String>(a.getFrequency() + b.getFrequency(),
					                                                     a.getSymbol() + b.getSymbol(), a, b);
			/** since the nodes are in a sorted list, we already know that a.frequency <= b.frequency
			 *	the tie breaker is performed within BTNode.compareTo, where if two nodes have the same frequency
			 *	we proceed to compare the nodes based on their symbols.
			 *	@see BTNode.compareTo(BTNode)
			 */
			
			sortedList.add(parent); // add parent node to continue building tree
		}
		
		return sortedList.removeIndex(0); // there is only one node left in our list, this is the root node of the tree
	}
	
	/**
	 * Prints the nodes of the binary tree using a recursive in-order traversal.
	 * 
	 * @param root root node of the tree to be printed
	 */
	public void printTree(BTNode<Integer, String> root) {
		if (root != null) {
			if (root.hasLeft()) {
				printTree(root.getLeft()); // prints left child first
			}
			
			System.out.println(root); // prints parent second
			
			if (root.hasRight()) {
				printTree(root.getRight()); // prints right child last
			}
		}
	}
	
	/**
	 * Takes a huffman tree and computes the huffman code for all the characters in
	 * in the original text.
	 * 
	 * @param root root node of the huffman tree
	 * @return map containing the symbols as keys and their huffman code as values
	 */
	public Map<Character, String> huffman_code(BTNode<Integer, String> root) {
		Map<Character, String> table = new HashTableSC<Character, String>(); // map to store the symbol-code pairs
		String allSymbols = root.getSymbol(); // string containing every symbol in the original text
		
		for (int i = 0; i < allSymbols.length(); i++) { // iterate through every character in allSymbols string
			Character c = allSymbols.charAt(i); 
			String huffman = createCode(c, root, ""); // use recursive method to find the huffman code for that symbol
			table.put(c, huffman); // insert the new symbol-code pair
		}
		
		return table;
	}
	
	/**
	 * Recursive method that searches for a character in a huffman tree. Returns the huffman
	 * code corresponding to the path taken to find the target character, where every time we
	 * move left in the tree we add '0' and every time we move right we add '1' to the code.
	 * 
	 * @param target character to be found in the huffman tree
	 * @param root root node of the huffman tree
	 * @param code string that is empty at first but gets filled up as we traverse through the tree
	 * @return string corresponding to the huffman code of the target character
	 */
	private String createCode(Character target, BTNode<Integer, String> root, String code) {
		if (root == null) { // when we call the function non-recursively the root cannot be null
			return "error"; // if the node is null it means that we made a mistake while creating the huffman tree
		}
		
		if (root.isLeaf()) { // if we reach a leaf, it means we reached a single character symbol
			return code;
		} 
		// since a node's symbol is the combination of its child's symbols,
		// then if the node.symbol contains the character 'target', it means
		// that one of its descendants is a leaf with a symbol == target
		else if (root.getLeft().getSymbol().contains(target.toString())) { 
			return createCode(target, root.getLeft(), code + '0'); // when we move left, we add 0
		} else {
			return createCode(target, root.getRight(), code + '1'); // when we move right, we add 1
		}
		
	}
	
	/**
	 * Uses the huffman code map and replaces every character in the inputTexts with
	 * its corresponding huffman code.
	 * 
	 * @param huffmanCode map containing characters as keys and their huffman code as values
	 * @param inputText original text
	 * @return encoded text
	 */
	public String encode(Map<Character, String> huffmanCode, String inputText) {
		String result = "";
		
		for (int i = 0; i < inputText.length(); i++) {
			// we use a map where the keys are the symbols in the inputText,
			// so we iterate through the inputText and replace every symbol
			// with its huffman code
			result += huffmanCode.get(inputText.charAt(i));
		}
		
		return result;
	}
	
	/**
	 * Takes the frequency distribution map, the huffman code map, the original text and the
	 * encoded text, and prints all the results to the console.
	 * 
	 * @param frequencyDistribution map containing characters as keys and their frequencies as values
	 * @param huffmanCode map containing characters as keys and their huffman code as values
	 * @param inputText inputText original text
	 * @param encodedText text encoded using the huffman method
	 */
	public void process_results(Map<Character, Integer> frequencyDistribution, Map<Character, String> huffmanCode, 
								String inputText, String encodedText) {

		// we create a sorted list of BTNode so that we can sort everything in the frequencyDistributin
		SortedList<BTNode<Integer, Character>> list = new SortedArrayList<BTNode<Integer, Character>>(frequencyDistribution.size());
		
		for (Character key: frequencyDistribution.getKeys()) { // we insert BTNodes corresponding to the map entries
			list.add(new BTNode<Integer, Character>(frequencyDistribution.get(key), key));
		}
		
		// we want to print in ascending order so we need to reverse the list
		Character[] symbols = new Character[list.size()]; 
		int index = symbols.length - 1;                   
		for (BTNode<Integer, Character> node: list) { 
			symbols[index--] = node.getSymbol();
		}
		
		// printing the huffman table
		System.out.println("Symbol   Frequency   Code\r\n" + "------   ---------   ----");
		for (Character c: symbols) {
			System.out.printf("%-6c   ", c); // print symbol
			System.out.printf("%-9d   ", frequencyDistribution.get(c)); // print frequency
			System.out.printf("%s%n", huffmanCode.get(c)); // print huffman symbol
		}
		System.out.println();
			
		// printing the original and encoded strings
		System.out.println("Original string:\n" + inputText);
		System.out.println("Encoded string:\n" + encodedText + "\n");
		
		int originalBytes = inputText.length(); // calculating how many bytes in the original string, 1char = 8bytes
		System.out.println("The original string requires " + originalBytes + " bytes.");
		
		// calculate how many bytes in the encoded string
		// since every binary number only takes 1bit we need to divide by 8, 8bit = 1bytes
		// if number is not an integer we add 1, because you can't have a fraction of a byte
		double encodedBytes = encodedText.length() / 8.0; 
		encodedBytes =  encodedBytes % 1 == 0 ? encodedBytes : ((int) encodedBytes) + 1; 
		System.out.println("The encoded string requires " + (int) encodedBytes + " bytes.");
		
		// calculate percentage difference between originalBytes and encodedBytes
		double percentage = (Math.abs(originalBytes - encodedBytes) / originalBytes) * 100;
		System.out.println("Difference in space required is " + Math.round(percentage) + "%.");
	}
	
	public String decode(Map<Character, String> huffmanCode, String encodedText) {
		
		// invert map
		Map<String, Character> encryptionKey = new HashTableSC<String, Character>(huffmanCode.size()*2);
		for (Character key : huffmanCode.getKeys()) {
			encryptionKey.put(huffmanCode.get(key), key);
		}
		
		String decoded = "";
		String temp = "";
		for (int i = 0; i < encodedText.length(); i++) {
			temp += encodedText.charAt(i);
			if (encryptionKey.containsKey(temp)) {
				decoded += encryptionKey.get(temp);
				temp = "";
			}
		}
		
		return decoded;
	}
	
	
	/**
	 * Binary Tree Node class, used for the construction of a huffman tree.
	 * Each node contains to fields: a frequency of type F that corresponds to the amount of
	 * times a symbol is present in the text to be encoded, and a symbol of type S that 
	 * corresponds to a character present in the text to be encoded.
	 * Edges in the tree only point in one direction, that is from parent to child.
	 * Every BTNode only has to children, one left and one right.
	 * 
	 * @author Abdiel Cortés
	 *
	 * @param <F> comparable object that reffers to the frequency of the symbol
	 * @param <S> symbol contained in the text to be enconded
	 */
	public class BTNode<F extends Comparable<F>, S extends Comparable<S>> implements Comparable<BTNode<F, S>> {
		// the frequency and the symbol need to be comparable so that we can properly sort using a sorted list
		
		private F frequency; // how many times the frequency appears in the input text
		private S symbol; // character in the input text
		private BTNode<F, S> left; // left child of node
		private BTNode<F, S> right; // right child of node

		/**
		 * Constructor that assigns every attribute to the BTNode.
		 * 
		 * @param frequency amount of times the symbol appears in the text to be encoded
		 * @param symbol character in the text to be encoded
		 * @param left left child of node
		 * @param right right child of node
		 */
		public BTNode(F frequency, S symbol, BTNode<F, S> left, BTNode<F, S> right) {
			this.frequency = frequency;
			this.symbol = symbol;
			this.left = left;
			this.right = right;
		}
		
		/**
		 * Constructor that assigns the frequency and the symbol.
		 * 
		 * @param frequency amount of times the symbol appears in the text to be encoded
		 * @param symbol character in the text to be encoded
		 */
		public BTNode(F frequency, S symbol) {
			this(frequency, symbol, null, null);
		}
		
		/**
		 * Constructor that sets all attributes to null.
		 */
		public BTNode() {
			this(null, null, null, null);
		}
		
		/**
		 * Sets all the attributes to null, used to prevent data leaks
		 * while removing a node from a tree.
		 */
		public void delete() {
			this.frequency = null;
			this.symbol = null;
			this.left = null;
			this.right = null;
		}

		/**
		 * Compares the target node with the parameter node. Compares using 
		 * the frequency, but if the frequencies are equal its compares using the symbol.
		 */
		@Override
		public int compareTo(BTNode<F, S> node) {
			if (this.getFrequency().compareTo(node.getFrequency()) == 0) {
				return this.getSymbol().compareTo(node.getSymbol());
			} else {
				return this.getFrequency().compareTo(node.getFrequency());
			}
		}
		
		/**
		 * Checks if the node has a left child.
		 *
		 * @return true if left is not null, false otherwise
		 */
		public boolean hasLeft() {
			return getLeft() != null;
		}
		
		/**
		 * Checks if the node has a right child.
		 *
		 * @return true if right is not null, false otherwise
		 */
		public boolean hasRight() {
			return getRight() != null;
		}
		
		/**
		 * Checks if the node is a leaf (node has no children).
		 * 
		 * @return true if both left and right are null
		 */
		public boolean isLeaf() {
			return !hasLeft() && !hasRight();
		}
		

		// geters for node attributes
		public F getFrequency() {return frequency;}
		
		public S getSymbol() {return symbol;}

		public BTNode<F, S> getLeft() {return left;}

		public BTNode<F, S> getRight() {return right;}

		
		// seters for node attributes
		public void setFrequency(F frequency) {this.frequency = frequency;}
		
		public void setSymbol(S symbol) {this.symbol = symbol;}

		public void setLeft(BTNode<F, S> left) {this.left = left;}

		public void setRight(BTNode<F, S> right) {this.right = right;}

		/**
		 * Creates a string representation for the BTNode, used in the debugger.
		 * 
		 * @return string representation of the target object
		 */
		@Override
		public String toString() {
			return "(" + this.frequency + ":" + this.symbol + ")";
		}
		
	}
	
}
