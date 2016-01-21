package com.reviewsearch.query;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class SentimentAnalyzer {
	
	static List<String> posWords = new ArrayList<String>();
	static List<String> negWords = new ArrayList<String>();
	
	public static void initialize() throws Exception{
		
		BufferedReader negReader = new BufferedReader(new FileReader(new File(
				"E:/yelpdata/sentiment/negative-words.txt")));
		BufferedReader posReader = new BufferedReader(new FileReader(new File(
				"E:/yelpdata/sentiment/positive-words.txt")));

		// currently read word
		String word;

		// add words to comparison list
		while ((word = negReader.readLine()) != null) {
			negWords.add(word);
		}
		while ((word = posReader.readLine()) != null) {
			posWords.add(word);
		}
		negReader.close();
		posReader.close();
	}
	
	
	public static int getSentimentScore(String input) {
		// normalize!
		input = input.toLowerCase();
		input = input.trim();
		// remove all non alpha-numeric non whitespace chars
		input = input.replaceAll("[^a-zA-Z0-9\\s]", "");

		int negCounter = 0;
		int posCounter = 0;

		// so what we got?
		String[] words = input.split(" ");

		// check if the current word appears in our reference lists...
		for (int i = 0; i < words.length; i++) {
			if (posWords.contains(words[i])) {
				posCounter++;
			}
			if (negWords.contains(words[i])) {
				negCounter++;
			}
		}

		// positive matches MINUS negative matches
		int result = (posCounter - negCounter);

		// negative?
		if (result < 0) {
			return -1;
			// or positive?
		} else if (result > 0) {
			return 1;
		}

		// neutral to the rescue!
		return 0;
	}
	

}
