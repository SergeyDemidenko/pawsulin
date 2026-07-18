package com.pawsulin.cucumber;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pawsulin.dto.CreateGlucoseReadingRequest;
import com.pawsulin.dto.CreateInsulinLogRequest;
import com.pawsulin.dto.CreatePetRequest;
import com.pawsulin.dto.GlucoseReadingDTO;
import com.pawsulin.dto.InsulinLogDTO;
import com.pawsulin.dto.PetDTO;
import com.pawsulin.dto.UserDTO;
import com.pawsulin.dto.auth.AuthResponse;
import com.pawsulin.dto.auth.LoginRequest;
import com.pawsulin.dto.auth.RegisterRequest;
import com.pawsulin.security.UserPrincipal;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public class PawsulinStepDefinitions {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CucumberScenarioState scenarioState;

    private String requestBody;
    private MvcResult lastResult;
    private Long currentPetId;

    @Before
    public void setUpScenario() {
        scenarioState.reset();
        SecurityContextHolder.clearContext();
        requestBody = null;
        lastResult = null;
        currentPetId = null;
    }

    @After
    public void tearDownScenario() {
        SecurityContextHolder.clearContext();
    }

    @Given("an authenticated pet owner")
    public void anAuthenticatedPetOwner() {
        UserPrincipal userPrincipal = new UserPrincipal(
                1L,
                "test@example.com",
                "password123",
                true,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_PET_OWNER")));
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userPrincipal, null, userPrincipal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Given("a valid registration request")
    public void aValidRegistrationRequest() throws Exception {
        RegisterRequest request = RegisterRequest.builder()
                .email("test@example.com")
                .password("password123")
                .firstName("John")
                .lastName("Doe")
                .build();
        requestBody = objectMapper.writeValueAsString(request);
    }

    @Given("auth service returns a registered user")
    public void authServiceReturnsARegisteredUser() {
        UserDTO userDTO = UserDTO.builder()
                .id(1L)
                .email("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .role("PET_OWNER")
                .isActive(true)
                .build();
        scenarioState.setRegisteredUser(userDTO);
    }

    @When("the client registers")
    public void theClientRegisters() throws Exception {
        lastResult = mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andReturn();
    }

    @And("the auth response should contain user email {string}")
    public void theAuthResponseShouldContainUserEmail(String email) throws Exception {
        assertEquals(email, responseBody().get("email").asText());
    }

    @Given("a valid login request")
    public void aValidLoginRequest() throws Exception {
        LoginRequest request = LoginRequest.builder()
                .email("test@example.com")
                .password("password123")
                .build();
        requestBody = objectMapper.writeValueAsString(request);
    }

    @Given("auth service returns auth tokens")
    public void authServiceReturnsAuthTokens() {
        AuthResponse authResponse = AuthResponse.builder()
                .accessToken("accessToken")
                .refreshToken("refreshToken")
                .tokenType("Bearer")
                .userId(1L)
                .email("test@example.com")
                .role("PET_OWNER")
                .build();
        scenarioState.setAuthResponse(authResponse);
    }

    @When("the client logs in")
    public void theClientLogsIn() throws Exception {
        lastResult = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andReturn();
    }

    @And("the login response should contain access token {string}")
    public void theLoginResponseShouldContainAccessToken(String accessToken) throws Exception {
        assertEquals(accessToken, responseBody().get("accessToken").asText());
    }

    @Given("a valid pet creation request")
    public void aValidPetCreationRequest() throws Exception {
        CreatePetRequest request = CreatePetRequest.builder()
                .name("Fluffy")
                .species("Cat")
                .breed("Persian")
                .ageYears(3)
                .weightKg(new BigDecimal("4.5"))
                .diabetesType("Type 1")
                .medicalNotes("Requires insulin twice daily")
                .build();
        requestBody = objectMapper.writeValueAsString(request);
    }

    @Given("pet service returns the created pet")
    public void petServiceReturnsTheCreatedPet() {
        PetDTO petDTO = PetDTO.builder()
                .id(1L)
                .userId(1L)
                .name("Fluffy")
                .species("Cat")
                .breed("Persian")
                .ageYears(3)
                .weightKg(new BigDecimal("4.5"))
                .diabetesType("Type 1")
                .medicalNotes("Requires insulin twice daily")
                .isActive(true)
                .build();
        scenarioState.setPetDTO(petDTO);
    }

    @When("the client creates a pet")
    public void theClientCreatesAPet() throws Exception {
        lastResult = mockMvc.perform(post("/api/v1/pets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andReturn();
    }

    @And("the pet response should contain name {string}")
    public void thePetResponseShouldContainName(String name) throws Exception {
        assertEquals(name, responseBody().get("name").asText());
    }

    @Given("a valid glucose reading request for pet {long}")
    public void aValidGlucoseReadingRequestForPet(Long petId) throws Exception {
        currentPetId = petId;
        CreateGlucoseReadingRequest request = CreateGlucoseReadingRequest.builder()
                .glucoseValue(new BigDecimal("125.50"))
                .readingTime(LocalDateTime.parse("2026-07-18T08:30:00"))
                .notes("Morning reading")
                .build();
        requestBody = objectMapper.writeValueAsString(request);
    }

    @Given("glucose service returns the created reading")
    public void glucoseServiceReturnsTheCreatedReading() {
        GlucoseReadingDTO readingDTO = GlucoseReadingDTO.builder()
                .id(1L)
                .petId(currentPetId)
                .userId(1L)
                .glucoseValue(new BigDecimal("125.50"))
                .glucoseLevel("NORMAL")
                .readingTime(LocalDateTime.parse("2026-07-18T08:30:00"))
                .notes("Morning reading")
                .build();
        scenarioState.setGlucoseReadingDTO(readingDTO);
    }

    @When("the client creates a glucose reading for pet {long}")
    public void theClientCreatesAGlucoseReadingForPet(Long petId) throws Exception {
        lastResult = mockMvc.perform(post("/api/v1/pets/{petId}/glucose", petId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andReturn();
    }

    @And("the glucose response should contain level {string}")
    public void theGlucoseResponseShouldContainLevel(String level) throws Exception {
        assertEquals(level, responseBody().get("glucoseLevel").asText());
    }

    @Given("a valid insulin log request for pet {long}")
    public void aValidInsulinLogRequestForPet(Long petId) throws Exception {
        currentPetId = petId;
        CreateInsulinLogRequest request = CreateInsulinLogRequest.builder()
                .insulinType("Lantus")
                .amountUnits(new BigDecimal("5.0"))
                .injectionTime(LocalDateTime.parse("2026-07-18T08:45:00"))
                .batchNumber("BATCH001")
                .expirationDate(LocalDate.parse("2027-01-01"))
                .notes("Morning dose")
                .build();
        requestBody = objectMapper.writeValueAsString(request);
    }

    @Given("insulin service returns the created log")
    public void insulinServiceReturnsTheCreatedLog() {
        InsulinLogDTO logDTO = InsulinLogDTO.builder()
                .id(1L)
                .petId(currentPetId)
                .userId(1L)
                .insulinType("Lantus")
                .amountUnits(new BigDecimal("5.0"))
                .injectionTime(LocalDateTime.parse("2026-07-18T08:45:00"))
                .batchNumber("BATCH001")
                .expirationDate(LocalDate.parse("2027-01-01"))
                .notes("Morning dose")
                .build();
        scenarioState.setInsulinLogDTO(logDTO);
    }

    @When("the client creates an insulin log for pet {long}")
    public void theClientCreatesAnInsulinLogForPet(Long petId) throws Exception {
        lastResult = mockMvc.perform(post("/api/v1/pets/{petId}/insulin", petId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andReturn();
    }

    @And("the insulin response should contain type {string}")
    public void theInsulinResponseShouldContainType(String insulinType) throws Exception {
        assertEquals(insulinType, responseBody().get("insulinType").asText());
    }

    @Then("the response status should be {int}")
    public void theResponseStatusShouldBe(int status) {
        assertEquals(status, lastResult.getResponse().getStatus());
    }

    private JsonNode responseBody() throws Exception {
        return objectMapper.readTree(lastResult.getResponse().getContentAsString());
    }
}
