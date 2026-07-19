package com.pawsulin.config;

import com.pawsulin.entity.GlucoseReading;
import com.pawsulin.entity.InsulinLog;
import com.pawsulin.entity.Pet;
import com.pawsulin.entity.User;
import com.pawsulin.repository.GlucoseReadingRepository;
import com.pawsulin.repository.InsulinLogRepository;
import com.pawsulin.repository.PetRepository;
import com.pawsulin.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;
import java.util.Random;

@Component
@Slf4j
@Profile("seed")
public class DataSeeder implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private InsulinLogRepository insulinLogRepository;

    @Autowired
    private GlucoseReadingRepository glucoseReadingRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        log.info("--- Data Seeding Started ---");
        seedData();
        log.info("--- Data Seeding Completed ---");
    }

    public void seedData() {
        String email = "sd@s.d";
        Optional<User> userOpt = userRepository.findByEmail(email);
        User user;
        if (userOpt.isEmpty()) {
            user = User.builder()
                    .email(email)
                    .password(passwordEncoder.encode("12345678"))
                    .firstName("S")
                    .lastName("D")
                    .role(User.UserRole.PET_OWNER)
                    .isActive(true)
                    .build();
            user = userRepository.save(user);
            log.info("Created user: {}", email);
        } else {
            user = userOpt.get();
            log.info("User {} already exists", email);
        }

        String petName = "pupochka";
        Pet pet = petRepository.findByUserId(user.getId()).stream()
                .filter(p -> p.getName().equalsIgnoreCase(petName))
                .findFirst()
                .orElse(null);

        if (pet == null) {
            pet = Pet.builder()
                    .user(user)
                    .name(petName)
                    .species("Dog")
                    .breed("Jack Russell Terrier")
                    .ageYears(4)
                    .weightKg(new BigDecimal("8.5"))
                    .diabetesType("Type 1")
                    .isActive(true)
                    .build();
            pet = petRepository.save(pet);
            log.info("Created pet: {} for user {}", petName, email);
        } else {
            log.info("Pet {} already exists for user {}", petName, email);
        }

        // Check if logs exist
        if (insulinLogRepository.findByPetIdAndIsActiveTrue(pet.getId(), PageRequest.of(0, 1)).isEmpty()) {
            generateInsulinLogs(user, pet);
        } else {
            log.info("Insulin logs already exist for pet {}", petName);
        }

        // Check if glucose readings exist
        if (glucoseReadingRepository.findByPetIdAndIsActiveTrue(pet.getId(), PageRequest.of(0, 1)).isEmpty()) {
            generateGlucoseReadings(user, pet);
        } else {
            log.info("Glucose readings already exist for pet {}", petName);
        }
    }

    private void generateInsulinLogs(User user, Pet pet) {
        Random random = new Random();
        LocalDateTime now = LocalDateTime.now();
        
        // Generate logs for the last 5 days
        for (int i = 4; i >= 0; i--) {
            LocalDateTime day = now.minusDays(i);
            
            // Morning injection (around 08:00)
            LocalDateTime morning = day.with(LocalTime.of(8, 0)).plusMinutes(random.nextInt(40) - 20);
            BigDecimal morningAmount = new BigDecimal(2.0 + random.nextDouble()).setScale(1, RoundingMode.HALF_UP);
            createLog(user, pet, morning, morningAmount, "Morning injection");
            
            // Evening injection (around 20:00)
            LocalDateTime evening = day.with(LocalTime.of(20, 0)).plusMinutes(random.nextInt(40) - 20);
            BigDecimal eveningAmount = new BigDecimal(1.5 + random.nextDouble()).setScale(1, RoundingMode.HALF_UP);
            createLog(user, pet, evening, eveningAmount, "Evening injection");
        }
        log.info("Generated insulin logs for pet {}", pet.getName());
    }

    private void generateGlucoseReadings(User user, Pet pet) {
        Random random = new Random();
        LocalDateTime now = LocalDateTime.now();

        // Generate readings for the last 5 days
        for (int i = 4; i >= 0; i--) {
            LocalDateTime day = now.minusDays(i);

            // Morning reading (around 07:45, before injection)
            LocalDateTime morning = day.with(LocalTime.of(7, 45)).plusMinutes(random.nextInt(30) - 15);
            BigDecimal morningValue = new BigDecimal(12.0 + random.nextDouble() * 8).setScale(1, RoundingMode.HALF_UP);
            createGlucoseReading(user, pet, morning, morningValue, "Pre-morning injection reading");

            // Mid-day reading (around 14:00, nadir)
            LocalDateTime midDay = day.with(LocalTime.of(14, 0)).plusMinutes(random.nextInt(60) - 30);
            BigDecimal midDayValue = new BigDecimal(4.5 + random.nextDouble() * 5).setScale(1, RoundingMode.HALF_UP);
            createGlucoseReading(user, pet, midDay, midDayValue, "Mid-day nadir check");

            // Evening reading (around 19:45, before injection)
            LocalDateTime evening = day.with(LocalTime.of(19, 45)).plusMinutes(random.nextInt(30) - 15);
            BigDecimal eveningValue = new BigDecimal(10.0 + random.nextDouble() * 7).setScale(1, RoundingMode.HALF_UP);
            createGlucoseReading(user, pet, evening, eveningValue, "Pre-evening injection reading");
        }
        log.info("Generated glucose readings for pet {}", pet.getName());
    }

    private void createLog(User user, Pet pet, LocalDateTime time, BigDecimal amount, String note) {
        InsulinLog logEntry = InsulinLog.builder()
                .user(user)
                .pet(pet)
                .insulinType("Caninsulin")
                .amountUnits(amount)
                .injectionTime(time)
                .isActive(true)
                .notes(note)
                .build();
        insulinLogRepository.save(logEntry);
    }

    private void createGlucoseReading(User user, Pet pet, LocalDateTime time, BigDecimal value, String note) {
        GlucoseReading.GlucoseLevel level = determineGlucoseLevel(value);
        GlucoseReading reading = GlucoseReading.builder()
                .user(user)
                .pet(pet)
                .glucoseValue(value)
                .glucoseLevel(level)
                .readingTime(time)
                .isActive(true)
                .notes(note)
                .build();
        glucoseReadingRepository.save(reading);
    }

    private GlucoseReading.GlucoseLevel determineGlucoseLevel(BigDecimal value) {
        double val = value.doubleValue();
        if (val < 4.0 || val > 16.0) {
            return GlucoseReading.GlucoseLevel.CRITICAL;
        } else if (val < 5.5) {
            return GlucoseReading.GlucoseLevel.LOW;
        } else if (val < 9.0) {
            return GlucoseReading.GlucoseLevel.NORMAL;
        } else {
            return GlucoseReading.GlucoseLevel.HIGH;
        }
    }
}
