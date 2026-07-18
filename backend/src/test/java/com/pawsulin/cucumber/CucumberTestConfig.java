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

@Configuration
public class CucumberTestConfig {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        return objectMapper;
    }

    @Bean
    public CucumberScenarioState cucumberScenarioState() {
        return new CucumberScenarioState();
    }

    @Bean
    public MockMvc mockMvc(CucumberScenarioState scenarioState, ObjectMapper objectMapper) {
        AuthController authController = new AuthController();
        ReflectionTestUtils.setField(authController, "authService", new AuthService() {
            @Override
            public com.pawsulin.dto.UserDTO register(com.pawsulin.dto.auth.RegisterRequest request) {
                return scenarioState.getRegisteredUser();
            }

            @Override
            public com.pawsulin.dto.auth.AuthResponse login(com.pawsulin.dto.auth.LoginRequest request) {
                return scenarioState.getAuthResponse();
            }
        });

        PetController petController = new PetController();
        ReflectionTestUtils.setField(petController, "petService", new PetService() {
            @Override
            public com.pawsulin.dto.PetDTO createPet(Long userId, com.pawsulin.dto.CreatePetRequest request) {
                return scenarioState.getPetDTO();
            }
        });

        GlucoseController glucoseController = new GlucoseController();
        ReflectionTestUtils.setField(glucoseController, "glucoseService", new GlucoseService() {
            @Override
            public com.pawsulin.dto.GlucoseReadingDTO createGlucoseReading(
                    Long petId,
                    Long userId,
                    com.pawsulin.dto.CreateGlucoseReadingRequest request) {
                return scenarioState.getGlucoseReadingDTO();
            }
        });

        InsulinController insulinController = new InsulinController();
        ReflectionTestUtils.setField(insulinController, "insulinService", new InsulinService() {
            @Override
            public com.pawsulin.dto.InsulinLogDTO createInsulinLog(
                    Long petId,
                    Long userId,
                    com.pawsulin.dto.CreateInsulinLogRequest request) {
                return scenarioState.getInsulinLogDTO();
            }
        });

        return MockMvcBuilders.standaloneSetup(
                        authController,
                        petController,
                        glucoseController,
                        insulinController)
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();
    }
}
