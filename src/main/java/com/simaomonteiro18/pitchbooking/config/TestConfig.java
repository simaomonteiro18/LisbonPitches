package com.simaomonteiro18.pitchbooking.config;

import com.simaomonteiro18.pitchbooking.entities.Invitation;
import com.simaomonteiro18.pitchbooking.entities.Pitch;
import com.simaomonteiro18.pitchbooking.entities.Reservation;
import com.simaomonteiro18.pitchbooking.entities.User;
import com.simaomonteiro18.pitchbooking.entities.enums.PitchAccess;
import com.simaomonteiro18.pitchbooking.entities.enums.PitchType;
import com.simaomonteiro18.pitchbooking.repositories.InvitationRepository;
import com.simaomonteiro18.pitchbooking.repositories.PitchRepository;
import com.simaomonteiro18.pitchbooking.repositories.ReservationRepository;
import com.simaomonteiro18.pitchbooking.repositories.UserRepository;
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

        Pitch p1 = new Pitch("Campo nº2 do Complexo Desportivo Real de Massamá", "Massamá", BigDecimal.valueOf(30.0), PitchAccess.PRIVATE, PitchType.SEVEN);
        Pitch p2 = new Pitch("Pavilhão Linces de Mafra", "Mafra", BigDecimal.valueOf(20.0), PitchAccess.PRIVATE, PitchType.FUTSAL);
        Pitch p3 = new Pitch("Campo nº1 do Complexo Desportivo do Jamor", "Cruz Quebrada", BigDecimal.valueOf(60.0), PitchAccess.PRIVATE, PitchType.ELEVEN);
        Pitch p4 = new Pitch("InFoot", "Mem-Martins", BigDecimal.valueOf(51.0), PitchAccess.PRIVATE, PitchType.FIVE);
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
        p4.setLatitude(38.781961899519814);
        p4.setLongitude(-9.350213670258649);
        p4.setImageUrl("https://infoot.pt/wp-content/uploads/2018/12/foto-campos-2.jpg");

        p5.setAddress("Massamá, Sintra");
        p5.setLatitude(38.75753901531081);
        p5.setLongitude(-9.27677864288426);

        Reservation r1 = new Reservation(u1, p1, Instant.now(), LocalDateTime.parse("2026-08-26T20:00:00"), LocalDateTime.parse("2026-08-26T22:00:00"));
        Reservation r2 = new Reservation(u4, p4, Instant.now().minus(30, ChronoUnit.MINUTES), LocalDateTime.parse("2026-08-30T16:00:00"), LocalDateTime.parse("2026-08-30T17:00:00"));
        Reservation r3 = new Reservation(u4, p3, Instant.now().minus(20, ChronoUnit.MINUTES), LocalDateTime.parse("2026-09-01T16:00:00"), LocalDateTime.parse("2026-09-01T19:00:00"));
        Reservation r4 = new Reservation(u2, p2, Instant.now().minus(45, ChronoUnit.MINUTES), LocalDateTime.parse("2026-08-28T16:00:00"), LocalDateTime.parse("2026-08-28T18:00:00"));
        Reservation r5 = new Reservation(u3, p1, Instant.now().minus(1, ChronoUnit.HOURS), LocalDateTime.parse("2026-08-27T21:00:00"), LocalDateTime.parse("2026-08-27T23:00:00"));

        Invitation i1 = new Invitation(u4, r1, u4);
        Invitation i2 = new Invitation(u1, r2, u1);
        Invitation i3 = new Invitation(u3, r3, u3);
        Invitation i4 = new Invitation(u2, r1, u2);
        Invitation i5 = new Invitation(u4, r4, u4);
        Invitation i6 = new Invitation(u3, r1, u3);

        i1.accept();
        i2.reject();
        i3.accept();
        i4.accept();
        i5.reject();
        i6.accept();
        
        userRepository.saveAll(Arrays.asList(u1, u2, u3, u4));

        pitchRepository.saveAll(Arrays.asList(p1, p2, p3, p4, p5));

        reservationRepository.saveAll(Arrays.asList(r1, r2, r3, r4, r5));

        invitationRepository.saveAll(Arrays.asList(i1, i2, i3, i4, i5, i6));

    }

}
