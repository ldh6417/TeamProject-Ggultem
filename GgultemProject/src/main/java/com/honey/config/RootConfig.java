package com.honey.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RootConfig {

	@Bean
	public ModelMapper getMapper() {
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.getConfiguration().setFieldMatchingEnabled(true)
								//필드명이 일치할 경우 매핑
				.setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE)
				//필드 접근제어자 : private 매핑
				.setMatchingStrategy(MatchingStrategies.LOOSE);
				// 필드이름이 비슷하면 자동으로 매핑
		return modelMapper;
	}
}
