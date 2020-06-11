package test;

import main.HuffmanCoding;
import main.HuffmanCoding.BTNode;
import map.Map;

/**
 * Tester class for the HuffmanCoding class. Individually tests all the methods
 * from the HuffmanCoding class.
 * 
 * @author Abdiel Cortés
 *
 */
public class HuffmanCodingTester {

	public static void main(String[] args) {
		HuffmanCoding test = new HuffmanCoding();
		
		// test load_data
		String text = test.load_data("inputData/input2.txt");
		System.out.println("Input text: -" + text + "-" + "\n");
		
		// test compute_fd
		Map<Character, Integer> fd = test.compute_fd(text); 
		System.out.println("Frequency Distribution: " + fd + "\n");
		
		// prints sorted list containing the BTNodes
		// method generateFDSortedList is private so for testing we would need to change it to public
		//System.out.println("Sorted List: " + test.generateFDSortedList(fd) + "\n");
		
		// printing tree using in-order traversal
		BTNode<Integer, String> root = test.huffman_tree(fd);
		System.out.println("Printing Binary Tree in-order");
		test.printTree(root);
		System.out.println();
		
		// printing huffman code table
		Map<Character, String> table = test.huffman_code(root);
		System.out.println("Huffman code map: " + table + "\n");
		
		// printing encoded text
		String encoded = test.encode(table, text);
		System.out.println("Encoded text: " + encoded + "\n");
		
		// printing results
		System.out.println("Results:");
		test.process_results(fd, table, text, encoded);
		
		System.out.println("\nDecrypted string: ");
		System.out.println(test.decode(table, encoded));
	}
}
