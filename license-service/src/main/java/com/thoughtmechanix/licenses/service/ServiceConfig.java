package com.thoughtmechanix.licenses.service;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ServiceConfig {
    @Value("${example.property}")
    @Getter
    private String exampleProperty;
}
