package com.simaomonteiro18.lisbonpitches.config;

import com.simaomonteiro18.lisbonpitches.entities.OpeningHours;
import com.simaomonteiro18.lisbonpitches.entities.Pitch;
import com.simaomonteiro18.lisbonpitches.entities.enums.DayType;
import com.simaomonteiro18.lisbonpitches.entities.enums.PitchAccess;
import com.simaomonteiro18.lisbonpitches.entities.enums.PitchType;
import com.simaomonteiro18.lisbonpitches.repositories.OpeningHoursRepository;
import com.simaomonteiro18.lisbonpitches.repositories.PitchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.Arrays;

@Configuration
@Order(1)
@Profile("prod")
public class ProductionSeedConfig implements CommandLineRunner {

    @Autowired
    private PitchRepository pitchRepository;

    @Autowired
    private OpeningHoursRepository openingHoursRepository;

    @Override
    public void run(String... args) throws Exception {
        if (pitchRepository.count() == 0) {

            Pitch p4 = new Pitch("InFoot", "Mem-Martins", BigDecimal.valueOf(50.0), PitchAccess.PRIVATE, PitchType.FIVE);
            Pitch p5 = new Pitch("Polidesportivo do Parque 2 de Abril", "Massamá", null, PitchAccess.PUBLIC, PitchType.FIVE);

            p4.setAddress("Mem-Martins, Sintra");
            p4.setLatitude(38.78195225049205);
            p4.setLongitude(-9.350182383227262);
            p4.setImageUrl("/campos/infoot.jpg");
            p4.setContactPhone("966 629 383");
            p4.setContactEmail("geral@infoot.pt");
            p4.setReservable(true);

            p5.setAddress("Massamá, Sintra");
            p5.setLatitude(38.75753901531081);
            p5.setLongitude(-9.27677864288426);

            OpeningHours oh1 = new OpeningHours();
            oh1.setPitch(p4);
            oh1.setDayType(DayType.WEEKDAY);
            oh1.setOpenTime(LocalTime.of(10, 0));
            oh1.setCloseTime(LocalTime.of(0, 0));

            OpeningHours oh2 = new OpeningHours();
            oh2.setPitch(p4);
            oh2.setDayType(DayType.WEEKEND);
            oh2.setOpenTime(LocalTime.of(19, 0));
            oh2.setCloseTime(LocalTime.of(0, 0));

            pitchRepository.saveAll(Arrays.asList(p4, p5));

            openingHoursRepository.saveAll(Arrays.asList(oh1, oh2));

        }
    }

}
