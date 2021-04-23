package com.a6raywa1cher.hackservspring;

import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestUtils {
	public static String find(String content, String regexp) {
		Pattern pattern = Pattern.compile(regexp);
		Matcher matcher = pattern.matcher(content);
		if (matcher.find()) {
			return matcher.group(1);
		}
		return null;
	}

	public static String base64Encode(String input) {
		return Base64.getEncoder().encodeToString(input.getBytes());
	}
}
