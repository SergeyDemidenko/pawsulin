package com.pawsulin.cucumber;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pawsulin.controller.AuthController;
import com.pawsulin.controller.GlucoseController;
import com.pawsulin.controller.InsulinController;
import com.pawsulin.controller.PetController;
import com.pawsulin.service.AuthService;
import com.pawsulin.service.GlucoseService;
import com.pawsulin.service.InsulinService;
import com.pawsulin.service.PetService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.mock;

@Configuration
public class CucumberTestConfig {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        return objectMapper;
    }

    @Bean
    public AuthService authService() {
        return mock(AuthService.class);
    }

    @Bean
    public PetService petService() {
        return mock(PetService.class);
    }

    @Bean
    public GlucoseService glucoseService() {
        return mock(GlucoseService.class);
    }

    @Bean
    public InsulinService insulinService() {
        return mock(InsulinService.class);
    }

    @Bean
    public AuthController authController(AuthService authService) {
        AuthController controller = new AuthController();
        ReflectionTestUtils.setField(controller, "authService", authService);
        return controller;
    }

    @Bean
    public PetController petController(PetService petService) {
        PetController controller = new PetController();
        ReflectionTestUtils.setField(controller, "petService", petService);
        return controller;
    }

    @Bean
    public GlucoseController glucoseController(GlucoseService glucoseService) {
        GlucoseController controller = new GlucoseController();
        ReflectionTestUtils.setField(controller, "glucoseService", glucoseService);
        return controller;
    }

    @Bean
    public InsulinController insulinController(InsulinService insulinService) {
        InsulinController controller = new InsulinController();
        ReflectionTestUtils.setField(controller, "insulinService", insulinService);
        return controller;
    }

    @Bean
    public MockMvc mockMvc(
            AuthController authController,
            PetController petController,
            GlucoseController glucoseController,
            InsulinController insulinController,
            ObjectMapper objectMapper) {
        return MockMvcBuilders.standaloneSetup(
                        authController,
                        petController,
                        glucoseController,
                        insulinController)
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();
    }
}
