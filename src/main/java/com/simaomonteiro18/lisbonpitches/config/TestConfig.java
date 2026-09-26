package com.simaomonteiro18.lisbonpitches.config;

import com.simaomonteiro18.lisbonpitches.entities.Invitation;
import com.simaomonteiro18.lisbonpitches.entities.Pitch;
import com.simaomonteiro18.lisbonpitches.entities.Reservation;
import com.simaomonteiro18.lisbonpitches.entities.User;
import com.simaomonteiro18.lisbonpitches.entities.enums.PitchAccess;
import com.simaomonteiro18.lisbonpitches.entities.enums.PitchType;
import com.simaomonteiro18.lisbonpitches.repositories.InvitationRepository;
import com.simaomonteiro18.lisbonpitches.repositories.PitchRepository;
import com.simaomonteiro18.lisbonpitches.repositories.ReservationRepository;
import com.simaomonteiro18.lisbonpitches.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;

@Configuration
@Profile("test")
public class TestConfig implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PitchRepository pitchRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private InvitationRepository invitationRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {

        User u1 = new User("Simão Monteiro", passwordEncoder.encode("simao"), "simao@gmail.com", "921233459", "Sintra");
        User u2 = new User("David Monteiro", passwordEncoder.encode("david"), "david@gmail.com", "967361438", "Agualva-Cacém");
        User u3 = new User("Michelle Santos", passwordEncoder.encode("michelle"), "michelle@gmail.com", "924309573", "Massamá");
        User u4 = new User("Gonçalo Vaqueiro", passwordEncoder.encode("goncalo"), "goncalo@gmail.com", "925637323", "São João das Lampas");

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

        Reservation r2 = new Reservation(u4, p4, Instant.now().minus(30, ChronoUnit.MINUTES), LocalDateTime.parse("2026-08-30T16:00:00"), LocalDateTime.parse("2026-08-30T17:00:00"));

        Invitation i2 = new Invitation(u1, r2, u1);

        i2.reject();

        userRepository.saveAll(Arrays.asList(u1, u2, u3, u4));

        pitchRepository.saveAll(Arrays.asList(p4, p5));

        reservationRepository.saveAll(Arrays.asList(r2));

        invitationRepository.saveAll(Arrays.asList(i2));

    }

}
