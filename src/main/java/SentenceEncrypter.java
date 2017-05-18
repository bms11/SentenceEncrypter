import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class takes in an English sentence of variable length and a list of distinct symmetric keys. The cipher will encrypt each word, then will encrypt the union of 2 words for the entire 
 * length of the String. Then every other word in the sentence is encrypted until there is only 1 encrypted String. 
 * 
 * @author bms11
 * 
 */

public class SentenceEncrypter {
	
	/**
	 * @unencryptedSentence - Sentence to be encrypted.
	 */
	private String unencryptedSentence;
	
	/**
	 * @encryptedSentence - Result when we encrypt <b>unencryptedSentence</b>.
	 */
	private String encryptedSentence;
	
	/**
	 * @key - Keys to be used for each layer of encryption.
	 */
	private ArrayList<String> keys;
	
	/**
	 * @decryptionOrder - Binary Tree root used to keep track of the order of keys when encrypting so that the order is known when decrypting.
	 */
	private BinaryKeyNode decryptionTree;
	
	/**
	 * @currKey - Keeps track of our index in the <b>keys</b> list. This will be incremented during encryption so we don't reuse keys.
	 */
	private int currKey;
	
	/**
	 * Constructor - Takes in an unencrypted sentence and a list of distinct keys.
	 * 
	 * @param sentence - String to be encrypted
	 * @param keys - List of keys used to encrypt the sentence
	 */
	public SentenceEncrypter(String unencryptedSentence, ArrayList<String> keys){
		// We are assuming we are supplied enough keys of sufficient size for each layer of encryption, otherwise we would throw exceptions
		this.keys = keys;
		
		currKey = 0;
		decryptionTree = new BinaryKeyNode();
		this.unencryptedSentence = unencryptedSentence;
		encryptedSentence = encryptSentence(unencryptedSentence);
	}
	
	/**
	 * 
	 * This class represents the key orderings used during encryption. Its purpose is to provide a base case during decryption when a null node is reached.
	 * This class resembles a binary tree because of the structure of recursion used during encryption.
	 * 
	 * @author bms11
	 *
	 */
	private class BinaryKeyNode {
		/**
		 * @key - Our key used at a particular node
		 */
		String key;
		
		/**
		 * @left - left subtree of keys
		 * @right - right subtree of keys
		 */
		BinaryKeyNode left, right;
		
		private BinaryKeyNode(){
			left = null;
			right = null;
			this.key = null;
		}
	
		private BinaryKeyNode(String key){
			left = null;
			right = null;
			this.key = key;
		}
	}
	

	
	/**
	 * This is the main engine of the StringEncrypter class. This method first parses the <b>sentence</b> delimited by spaces into an ArrayList. Each individual word
	 * will be an element in the ArrayList. The method recursiveEncrypt will then encrypt every word together until there is a single fully encrypted String.
	 *
	 * Why a List? Every word must be visited, and in a specific order.
	 * 
	 * Why an ArrayList and not a LinkedList? An ArrayList's get method is O(1) while a LinkedList's is O(n). We aren't concerned with addition/deletion, so a LinkedList doesn't really 
	 * benefit us here.
	 * 
	 * @param sentence - Sentence to be encrypted into a single String
	 * @return An encrypted version of the <b>sentence</b> param
	 */
	public String encryptSentence(String sentence){
		if(sentence == null){
			throw new NullPointerException();
		}
		
		// Use regex to parse words and create a fixed-size ArrayList of individual words
		List<String> words = new ArrayList<String>(Arrays.asList(sentence.split("\\s+")));

		if(words.isEmpty()){
			return "";
		}
		
		String encryptedStr = recursiveEncrypt(words, 0, words.size() - 1, decryptionTree);
		
		// Reset our current key just in case we need to re-encrypt
		currKey = 0;
		
		return encryptedStr;
	}
	
	/**
	 * <b>encryptSentence</b> helper method
	 * 
	 * Each word in the List will be recursively encrypted by using a divide and conquer technique that splits the ArrayList in two until a base case of 1 word is met. 
	 * The base case will return an encrypted word. The ArrayList will then encrypt 2 concatenated encrypted words until there is a single encrypted String.
	 * 
	 * @param words - List of individual words in the <b>unencryptedSentence</b> field
	 * @param start - starting point of a partition of <b>words</b>
	 * @param end - ending point of a partition of <b>words</b>
	 * @param root - Root of our decryptionTree to be filled with keys when recursing
	 * @return An encrypted sentence/String
	 */
	private String recursiveEncrypt(List<String> words, int start, int end, BinaryKeyNode root) {
		
		// Base case is when start >= end. Encrypt the word at index 'start' and return.
		if(start >= end){
			root.key = keys.get(currKey++);
			return encryptWord(words.get(start), root.key);
		} else {
			// Used to divide the current partition in two
			int mid = (start + end) / 2;
			
			// Add our current key to our structure
			root.key = keys.get(currKey++);
			root.left = new BinaryKeyNode();
			root.right = new BinaryKeyNode();
			
			// recursively split the Arraylist in two until the base case is met.
			String word1 = recursiveEncrypt(words, start, mid, root.left),
			 	   word2 = recursiveEncrypt(words, mid + 1, end, root.right);

			// Return the encrypted concat of both encrypted words.
			return encryptWord(word1 + "split" + word2, root.key);
		}
	}
	
	/**
	 * This method encrypts a String by using the one-time pad technique; i.e. XOR'ing the string with the key on each character.
	 * 
	 * One-time pad is a good method because it's currently known to be unbreakable.
	 * 
	 * @param word - String to be encrypted
	 * @param key - key to use to obfuscate our word
	 * @return The encrypted version of <b>word</b>
	 */
	public String encryptWord(String word, String key) {
		if(word == null){
			throw new NullPointerException();
		}
		
		StringBuilder encryptedWord = new StringBuilder();
		
		// XOR each character in 'word' with our key and build the result
		for(int idx = 0; idx < word.length(); idx++){
		    encryptedWord.append((char)(word.charAt(idx) ^ key.charAt(idx % key.length())));
		}

		return encryptedWord.toString();
		
	}
	
	/**
	 * 
	 * This method is a wrapper for <b>encryptWord(...)</b> because they perform the same function -- Just here to reduce any risk of confusion.
	 * 
	 * This method decrypts a String by using the one-time pad technique; i.e. XOR'ing the string with the key on each character.
	 * 
	 * One-time pad is a good method because it's currently known to be unbreakable.
	 * 
	 * 
	 * @param word - word to be decrypted
	 * @param key - key to XOR with <b>word</b> to decrypt the word
	 * @return
	 */
	public String decryptWord(String word, String key) {
		return encryptWord(word, key);
	}
	
	/**
	 * This method recursively decrypts each layer of encryption by recursively tracing the binary tree <b>decryptionTree</b> which keeps track of which keys were used on specific layers
	 * 
	 * @param sentence - sentence to decrypt
	 * @return The fully unencrypted sentence
	 */
	public String decryptSentence(String sentence){
		if(sentence == null){
			throw new NullPointerException();
		} 
		
		if(sentence.equals("")){
			return "";
		} else {
			// Using StringBuilder because pesky Strings are immutable. 
			StringBuilder result = recursiveDecrypt(sentence, decryptionTree, new StringBuilder());
			
			// Remove the extra whitespace at the end and convert back to a String.
			return result.deleteCharAt(result.length() - 1).toString();
		}
	}
	
	/**
	 * decryptSentence helper method
	 * 
	 * This method recursively decrypts each layer of encryption by recursively tracing the binary tree <b>decryptionTree</b> which keeps track of which keys were used on specific layers
	 * 
	 * The base case is when a null node is reached in the tree i.e. a fully decrypted word is found. The decrypted word is then added to <b>sentenceBuilder</b>.
	 * 
	 * @param sentence - the encrypted sentence to be decrypted
	 * @param root - The binary tree representing the orderings of keys used
	 * @param sentenceBuilder - A StringBuilder object that appends the unencrypted word when the base case is reached
	 * @return The fully unencrypted sentence in StringBuilder form
	 */
	private StringBuilder recursiveDecrypt(String sentence, BinaryKeyNode root, StringBuilder sentenceBuilder){
		// We've reached the bottom of our tree, meaning we've reached the last layer of encryption. Decrypt the word and append it.
		if(root.left == null && root.right == null){
			sentenceBuilder.append(decryptWord(sentence, root.key) + " ");
			return sentenceBuilder;
		} else {
			// Decrypt the sentence to reveal the separator
			sentence = decryptWord(sentence, root.key);
			
			// Split the 2 words delimited by our chosen separator "split"
			List<String> words = new ArrayList<String>(Arrays.asList(sentence.split("split")));

			// Recurse left first, then right to preserve the order of the sentence
			recursiveDecrypt(words.get(0), root.left, sentenceBuilder);
			recursiveDecrypt(words.get(1), root.right, sentenceBuilder);
			
			return sentenceBuilder;
		}
	}
	
	/**
	 * 
	 * @return the encrypted version of the sentence field
	 */
	public String getEncryptedSentence() {
		return encryptedSentence;
	}
	
	/**
	 * 
	 * @return the unencrypted version of the sentence (the sentence supplied to the constructor for this instance)
	 */
	public String getUnencryptedSentence() {
		return unencryptedSentence;
	}
	

}
