package com.simaomonteiro18.pitchbooking.config;

import com.simaomonteiro18.pitchbooking.entities.Pitch;
import com.simaomonteiro18.pitchbooking.entities.User;
import com.simaomonteiro18.pitchbooking.entities.enums.PitchType;
import com.simaomonteiro18.pitchbooking.repositories.PitchRepository;
import com.simaomonteiro18.pitchbooking.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;

@Configuration
@Profile("prod")
public class ProductionSeedConfig implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PitchRepository pitchRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (pitchRepository.count() == 0) {

            User user = new User("Demo User", passwordEncoder.encode("demo"), "demo@pitchbooking.com", "912345678", "Sintra");
            Pitch p1 = new Pitch("Campo nº2 do Complexo Desportivo Real de Massamá", "Massamá", 30.0, PitchType.SEVEN);
            Pitch p2 = new Pitch("Pavilhão Linces de Mafra", "Mafra", 20.0, PitchType.FUTSAL);
            Pitch p3 = new Pitch("Campo nº1 do Complexo Desportivo do Jamor", "Cruz Quebrada", 60.0, PitchType.ELEVEN);
            Pitch p4 = new Pitch("InFoot", "Mem-Martins", 25.0, PitchType.FIVE);

            userRepository.saveAll(List.of(user));

            pitchRepository.saveAll(Arrays.asList(p1, p2, p3, p4));

        }
    }

}
