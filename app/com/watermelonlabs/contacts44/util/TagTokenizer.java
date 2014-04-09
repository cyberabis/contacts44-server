package com.watermelonlabs.contacts44.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TagTokenizer {

	public static List<String> tokenize(String tags){
		
		//String input = "Input text, with words, punctuation, etc. Well, it's rather short.";
		List<String> tagList = new ArrayList<String>();
		Pattern p = Pattern.compile("[\\w']+");
		Matcher m = p.matcher(tags);

		while ( m.find() ) {
		    tagList.add(tags.substring(m.start(), m.end()));
		}
		return tagList;
	}
}
