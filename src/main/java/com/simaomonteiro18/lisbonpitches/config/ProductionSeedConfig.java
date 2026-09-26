package com.simaomonteiro18.lisbonpitches.config;

import com.simaomonteiro18.lisbonpitches.entities.Pitch;
import com.simaomonteiro18.lisbonpitches.entities.User;
import com.simaomonteiro18.lisbonpitches.entities.enums.PitchAccess;
import com.simaomonteiro18.lisbonpitches.entities.enums.PitchType;
import com.simaomonteiro18.lisbonpitches.repositories.PitchRepository;
import com.simaomonteiro18.lisbonpitches.repositories.UserRepository;
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

            User user = new User("Demo User", passwordEncoder.encode("demo"), "demo@lisbonpitches.com", "912345678", "Sintra");
            Pitch p4 = new Pitch("InFoot", "Mem-Martins", BigDecimal.valueOf(50.0), PitchAccess.PRIVATE, PitchType.FIVE);
            Pitch p5 = new Pitch("Polidesportivo do Parque 2 de Abril", "Massamá", null, PitchAccess.PUBLIC, PitchType.FIVE);

            p4.setAddress("Mem-Martins, Sintra");
            p4.setLatitude(38.78195225049205);
            p4.setLongitude(-9.350182383227262);
            p4.setImageUrl("https://www.aircourts.com/uploads/courts/courts_1639_1540486381.JPG");
            p4.setContactPhone("966 629 383");
            p4.setContactEmail("geral@infoot.pt");
            p4.setReservable(true);

            p5.setAddress("Massamá, Sintra");
            p5.setLatitude(38.75753901531081);
            p5.setLongitude(-9.27677864288426);

            userRepository.saveAll(List.of(user));

            pitchRepository.saveAll(Arrays.asList(p4, p5));

        }
    }

}
