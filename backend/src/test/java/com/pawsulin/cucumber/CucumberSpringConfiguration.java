package com.pawsulin.cucumber;

import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.test.context.ContextConfiguration;

@CucumberContextConfiguration
@ContextConfiguration(classes = CucumberTestConfig.class)
public class CucumberSpringConfiguration {
}
