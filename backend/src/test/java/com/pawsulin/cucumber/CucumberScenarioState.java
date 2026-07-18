package com.pawsulin.cucumber;

import com.pawsulin.dto.GlucoseReadingDTO;
import com.pawsulin.dto.InsulinLogDTO;
import com.pawsulin.dto.PetDTO;
import com.pawsulin.dto.UserDTO;
import com.pawsulin.dto.auth.AuthResponse;
public class CucumberScenarioState {
    private UserDTO registeredUser;
    private AuthResponse authResponse;
    private PetDTO petDTO;
    private GlucoseReadingDTO glucoseReadingDTO;
    private InsulinLogDTO insulinLogDTO;

    public UserDTO getRegisteredUser() {
        return registeredUser;
    }

    public void setRegisteredUser(UserDTO registeredUser) {
        this.registeredUser = registeredUser;
    }

    public AuthResponse getAuthResponse() {
        return authResponse;
    }

    public void setAuthResponse(AuthResponse authResponse) {
        this.authResponse = authResponse;
    }

    public PetDTO getPetDTO() {
        return petDTO;
    }

    public void setPetDTO(PetDTO petDTO) {
        this.petDTO = petDTO;
    }

    public GlucoseReadingDTO getGlucoseReadingDTO() {
        return glucoseReadingDTO;
    }

    public void setGlucoseReadingDTO(GlucoseReadingDTO glucoseReadingDTO) {
        this.glucoseReadingDTO = glucoseReadingDTO;
    }

    public InsulinLogDTO getInsulinLogDTO() {
        return insulinLogDTO;
    }

    public void setInsulinLogDTO(InsulinLogDTO insulinLogDTO) {
        this.insulinLogDTO = insulinLogDTO;
    }

    public void reset() {
        registeredUser = null;
        authResponse = null;
        petDTO = null;
        glucoseReadingDTO = null;
        insulinLogDTO = null;
    }
}
