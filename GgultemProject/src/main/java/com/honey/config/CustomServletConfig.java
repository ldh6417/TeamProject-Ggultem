package com.honey.config;

import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.honey.controller.fomatter.LocalDateTimeFormatter;

public class CustomServletConfig implements WebMvcConfigurer {
	@Override
	public void addFormatters(FormatterRegistry registry) {
		registry.addFormatter(new LocalDateTimeFormatter());
	}
}
