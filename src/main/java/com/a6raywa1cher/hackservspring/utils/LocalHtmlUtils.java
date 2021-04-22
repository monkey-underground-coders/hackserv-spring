package com.a6raywa1cher.hackservspring.utils;

import com.a6raywa1cher.hackservspring.rest.exc.TooLongStringException;
import org.springframework.web.util.HtmlUtils;

public abstract class LocalHtmlUtils {
	public static String htmlEscape(String input) {
		return input == null ? null : HtmlUtils.htmlEscape(input);
	}

	public static String htmlEscape(String input, int maxLength) {
		String out = htmlEscape(input.strip());
		if (out == null) {
			return null;
		} else if (out.length() > maxLength) {
			throw new TooLongStringException();
		} else {
			return out;
		}
	}
}
