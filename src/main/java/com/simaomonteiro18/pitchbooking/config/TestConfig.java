package com.simaomonteiro18.pitchbooking.config;

import com.simaomonteiro18.pitchbooking.entities.Invitation;
import com.simaomonteiro18.pitchbooking.entities.Pitch;
import com.simaomonteiro18.pitchbooking.entities.Reservation;
import com.simaomonteiro18.pitchbooking.entities.User;
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

        Pitch p1 = new Pitch("Campo nº2 do Complexo Desportivo Real de Massamá", "Massamá", 30.0, PitchType.SEVEN);
        Pitch p2 = new Pitch("Pavilhão Linces de Mafra", "Mafra", 20.0, PitchType.FUTSAL);
        Pitch p3 = new Pitch("Campo nº1 do Complexo Desportivo do Jamor", "Cruz Quebrada", 60.0, PitchType.ELEVEN);
        Pitch p4 = new Pitch("InFoot", "Mem-Martins", 25.0, PitchType.FIVE);

        Reservation r1 = new Reservation(u1, p1, Instant.now(), LocalDateTime.parse("2026-08-26T20:00:00"), LocalDateTime.parse("2026-08-26T22:00:00"));
        Reservation r2 = new Reservation(u4, p4, Instant.now().minus(30, ChronoUnit.MINUTES), LocalDateTime.parse("2026-08-30T16:00:00"), LocalDateTime.parse("2026-08-30T17:00:00"));
        Reservation r3 = new Reservation(u4, p3, Instant.now().minus(20, ChronoUnit.MINUTES), LocalDateTime.parse("2026-09-01T16:00:00"), LocalDateTime.parse("2026-09-01T19:00:00"));
        Reservation r4 = new Reservation(u2, p2, Instant.now().minus(45, ChronoUnit.MINUTES), LocalDateTime.parse("2026-08-28T16:00:00"), LocalDateTime.parse("2026-08-28T18:00:00"));
        Reservation r5 = new Reservation(u3, p1, Instant.now().minus(1, ChronoUnit.HOURS), LocalDateTime.parse("2026-08-27T21:00:00"), LocalDateTime.parse("2026-08-27T23:00:00"));

        Invitation i1 = new Invitation(u4, r1);
        Invitation i2 = new Invitation(u1, r2);
        Invitation i3 = new Invitation(u3, r3);
        Invitation i4 = new Invitation(u2, r1);
        Invitation i5 = new Invitation(u4, r4);
        Invitation i6 = new Invitation(u3, r1);

        i1.accept();
        i2.reject();
        i3.accept();
        i4.accept();
        i5.reject();
        i6.accept();
        
        userRepository.saveAll(Arrays.asList(u1, u2, u3, u4));

        pitchRepository.saveAll(Arrays.asList(p1, p2, p3, p4));

        reservationRepository.saveAll(Arrays.asList(r1, r2, r3, r4, r5));

        invitationRepository.saveAll(Arrays.asList(i1, i2, i3, i4, i5, i6));

    }

}
