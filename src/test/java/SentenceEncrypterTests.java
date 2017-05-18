import static org.junit.Assert.*;
import java.util.Random;

import java.util.ArrayList;

import org.junit.Test;

public class SentenceEncrypterTests {
	
	ArrayList<String> keys;
	SentenceEncrypter encrypter;
	
	// Using random Strings. Could use traditional byte arrays, but this is just as effective.
	public void generateKeys(){
		keys = new ArrayList<String>();
		
		StringBuilder key = new StringBuilder();
		Random r = new Random();
		// These keys MUST be as long the sentence itself for guaranteed safety. The amount of keys must also be equal to or greater than
		// nlog_2(n) , where n is the amount of words in the sentence.
		
		// The characters used to generate our keys
		String keyspace = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ!@#$%^&*()_+-={}|:{}<>/;";
		
		// Make 1000 distinct keys, each of size 1000
		for(int numKeys = 0; numKeys < 1000; numKeys++){
			for(int keySize = 0; keySize < 1000; keySize++){
				key.append(keyspace.charAt(r.nextInt(keyspace.length())));
			}
			keys.add(key.toString());
			key = new StringBuilder();
		}
	}
	
	/**
	 * Tests our encryptWord and decryptWord methods.
	 */
	@Test
	public void encryptWordTest(){
		generateKeys();
		
		// Constructor arguments won't be used in this test case, but filling them out anyway
		encrypter = new SentenceEncrypter("", keys);
		
		// Cases where the same key is used
		String word = "word";
		String encryptedWord = encrypter.encryptWord(word, "1234");
		String decryptedWord = encrypter.decryptWord(encryptedWord, "1234");
		assertEquals(decryptedWord, word);
		
		word = "California";
		encryptedWord = encrypter.encryptWord(word, keys.get(4));
		decryptedWord = encrypter.decryptWord(encryptedWord, keys.get(4));
		assertEquals(decryptedWord, word);
		
		// Case where we try to decrypt using a key that is slightly off eg. "1234" is the actual key but someone guesses "1243"
		word = "word";
		encryptedWord = encrypter.encryptWord(word, "1234");
		decryptedWord = encrypter.decryptWord(encryptedWord, "1243");
		
		assertNotEquals(decryptedWord, word);
		
		// Show we can handle an empty string without crashing 
		word = "";
		encryptedWord = encrypter.encryptWord(word, keys.get(0));
		decryptedWord = encrypter.decryptWord(encryptedWord, keys.get(0));
		
		assertEquals(decryptedWord, word);
		
		// Show that no 2 distinct keys produce the same encryption on the same word
		word = "distinct";
		encryptedWord = encrypter.encryptWord(word, keys.get(0));
		String encryptedWord2 = encrypter.encryptWord(word, keys.get(1));
		assertNotEquals(encryptedWord, encryptedWord2);
		
	}
	
	/**
	 * Tests our encryptSentence and decryptSentence methods
	 * Encrypts a sentence, then tests that the String is the same as the unencrypted version after decrypting it.
	 */
	@Test
	public void encryptSentenceTest(){
		generateKeys();
		
		// sentence of even length
		encrypter = new SentenceEncrypter("How are you doing?", keys);
		assertEquals("How are you doing?", encrypter.decryptSentence(encrypter.getEncryptedSentence()));
		
		// sentence of odd length
		encrypter = new SentenceEncrypter("How are you?", keys);
		assertEquals("How are you?", encrypter.decryptSentence(encrypter.getEncryptedSentence()));
		
		// Show we can handle an empty string without crashing
		encrypter = new SentenceEncrypter("", keys);
		assertEquals("", encrypter.decryptSentence(encrypter.getEncryptedSentence()));
		
		// Show we can handle a sentence with 1 word.
		encrypter = new SentenceEncrypter("Yes.", keys);
		assertEquals("Yes.", encrypter.decryptSentence(encrypter.getEncryptedSentence()));
		
		// Show we can handle a sentence with 2 words.
		encrypter = new SentenceEncrypter("No way.", keys);
		assertEquals("No way.", encrypter.decryptSentence(encrypter.getEncryptedSentence()));
		
		// Show we can handle a longer sentence
		String gburgAddress = "Four score and seven years ago our fathers brought forth on this continent, a new nation, conceived in Liberty, "
							+ "and dedicated to the proposition that all men are created equal.";
		encrypter = new SentenceEncrypter(gburgAddress, keys);
		assertEquals(gburgAddress, encrypter.decryptSentence(encrypter.getEncryptedSentence()));
		
		// Show we can handle the fate of the universe itself
		String wikipediaQuote = "Computer science is the study of the theory, experimentation, and engineering that form the basis for the design and use of computers. "
				+ "It is the scientific and practical approach to computation and its applications and the systematic study of the feasibility, structure, expression, and "
				+ "mechanization of the methodical procedures (or algorithms) that underlie the acquisition, representation, processing, storage, communication of, and access to "
				+ "information. An alternate, more succinct definition of computer science is the study of automating algorithmic processes that scale. A computer scientist "
				+ "specializes in the theory of computation and the design of computational systems. Good stuff. ! ! !";
		
		encrypter = new SentenceEncrypter(wikipediaQuote, keys);
		assertEquals(wikipediaQuote, encrypter.decryptSentence(encrypter.getEncryptedSentence()));
	}

}
