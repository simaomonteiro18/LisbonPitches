package com.simaomonteiro18.pitchbooking.config;

import com.simaomonteiro18.pitchbooking.entities.Pitch;
import com.simaomonteiro18.pitchbooking.entities.User;
import com.simaomonteiro18.pitchbooking.entities.enums.PitchAccess;
import com.simaomonteiro18.pitchbooking.entities.enums.PitchType;
import com.simaomonteiro18.pitchbooking.repositories.PitchRepository;
import com.simaomonteiro18.pitchbooking.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
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
            Pitch p1 = new Pitch("Campo nº2 do Complexo Desportivo Real de Massamá", "Massamá", BigDecimal.valueOf(30.0), PitchAccess.PRIVATE, PitchType.SEVEN);
            Pitch p2 = new Pitch("Pavilhão Linces de Mafra", "Mafra", BigDecimal.valueOf(20.0), PitchAccess.PRIVATE, PitchType.FUTSAL);
            Pitch p3 = new Pitch("Campo nº1 do Complexo Desportivo do Jamor", "Cruz Quebrada", BigDecimal.valueOf(60.0), PitchAccess.PRIVATE, PitchType.ELEVEN);
            Pitch p4 = new Pitch("InFoot", "Mem-Martins", BigDecimal.valueOf(50.0), PitchAccess.PRIVATE, PitchType.FIVE);
            Pitch p5 = new Pitch("Polidesportivo do Parque 2 de Abril", "Massamá", null, PitchAccess.PUBLIC, PitchType.FIVE);

            p1.setAddress("Massamá, Sintra");
            p1.setLatitude(38.7526);
            p1.setLongitude(-9.2926);

            p2.setAddress("Mafra");
            p2.setLatitude(38.9357);
            p2.setLongitude(-9.3271);

            p3.setAddress("Cruz Quebrada-Dafundo, Oeiras");
            p3.setLatitude(38.7002);
            p3.setLongitude(-9.2335);

            p4.setAddress("Mem-Martins, Sintra");
            p4.setLatitude(38.78195225049205);
            p4.setLongitude(-9.350182383227262);
            p4.setImageUrl("https://www.aircourts.com/uploads/courts/courts_1639_1540486381.JPG");
            p4.setContactPhone("966 629 383");
            p4.setContactEmail("geral@infoot.pt");

            p5.setAddress("Massamá, Sintra");
            p5.setLatitude(38.75753901531081);
            p5.setLongitude(-9.27677864288426);

            userRepository.saveAll(List.of(user));

            pitchRepository.saveAll(Arrays.asList(p1, p2, p3, p4, p5));

        }
    }

}
