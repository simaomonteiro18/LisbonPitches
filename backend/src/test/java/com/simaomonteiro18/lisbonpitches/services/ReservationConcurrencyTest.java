package com.simaomonteiro18.lisbonpitches.services;

import com.simaomonteiro18.lisbonpitches.entities.Pitch;
import com.simaomonteiro18.lisbonpitches.entities.Reservation;
import com.simaomonteiro18.lisbonpitches.entities.User;
import com.simaomonteiro18.lisbonpitches.exceptions.InvalidTimeException;
import com.simaomonteiro18.lisbonpitches.exceptions.ReservationConflictException;
import com.simaomonteiro18.lisbonpitches.repositories.PitchRepository;
import com.simaomonteiro18.lisbonpitches.repositories.ReservationRepository;
import com.simaomonteiro18.lisbonpitches.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;


import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ReservationConcurrencyTest {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PitchRepository pitchRepository;

    ExecutorService executorService = Executors.newFixedThreadPool(10);

    @Test
    public void onlyOneReservationSucceedsWhenManyRequestTheSameHourAtOnce() throws Exception {

        List<User> users = userRepository.findAll();
        List<Pitch> pitches = pitchRepository.findAll();

        Long userId = users.get(0).getId();
        Pitch inFoot = pitches.stream()
                .filter(p -> p.getName().equals("InFoot"))
                .findFirst()
                .orElseThrow();
        Long pitchId = inFoot.getId();
        LocalDateTime start = LocalDate.now().plusDays(7).atTime(20, 0);
        LocalDateTime end = start.plusHours(1);

        CountDownLatch countDownLatch = new CountDownLatch(1);

        List<Future<Reservation>> futureList = new ArrayList<>();

        Callable<Reservation> task = () -> {
            countDownLatch.await();
            return reservationService.createReservation(userId, pitchId, start, end);
        };

        for (int i = 0; i < 10; i++) {
            futureList.add(executorService.submit(task));
        }

        countDownLatch.countDown();

        int successes = 0;
        for (Future<Reservation> future : futureList) {
            try {
                future.get();
                successes++;
            } catch (ExecutionException e) {
                assertTrue(e.getCause() instanceof ReservationConflictException || e.getCause() instanceof InvalidTimeException);
            }
        }

        executorService.shutdown();

        assertEquals(1, successes);
        assertEquals(1, reservationRepository.findOverlappingReservations(inFoot, start, end).size());

    }

}
