package com.vittig.spring_digital_wallet.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    private final ModelMapper modelMapper = new ModelMapper();

    @Bean
    public ModelMapper modelMapper() {
        modelMapper.getConfiguration().setAmbiguityIgnored(true);

        return this.modelMapper;
    }
}
