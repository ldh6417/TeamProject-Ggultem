package com.honey.controller.fomatter;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.springframework.format.Formatter;

public class LocalDateTimeFormatter implements Formatter<LocalDateTime> {

	@Override
	public String print(LocalDateTime object, Locale locale) {
		return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(object);
	}

	@Override
	public LocalDateTime parse(String text, Locale locale) throws ParseException {
		return LocalDateTime.parse(text, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
	}
	
}
