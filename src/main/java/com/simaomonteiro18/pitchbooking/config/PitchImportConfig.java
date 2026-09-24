package com.simaomonteiro18.pitchbooking.config;

import com.simaomonteiro18.pitchbooking.entities.Pitch;
import com.simaomonteiro18.pitchbooking.entities.enums.PitchAccess;
import com.simaomonteiro18.pitchbooking.entities.enums.PitchType;
import com.simaomonteiro18.pitchbooking.repositories.PitchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * Le pitches-import.csv (classpath) e regista os campos publicos que ainda
 * nao existem na base de dados (comparacao pelo nome). Corre em qualquer
 * profile para que os campos importados fiquem visiveis tanto em dev/test
 * como em producao.
 *
 * Para adicionar mais campos em massa basta acrescentar linhas ao csv, nao e
 * preciso mexer neste ficheiro.
 */
@Configuration
public class PitchImportConfig implements CommandLineRunner {

    @Autowired
    private PitchRepository pitchRepository;

    @Override
    public void run(String... args) throws Exception {
        ClassPathResource resource = new ClassPathResource("pitches-import.csv");

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
            reader.readLine();

            String linha;
            while ((linha = reader.readLine()) != null) {
                if (linha.isBlank()) {
                    continue;
                }

                String[] campos = linha.split(";", -1);
                String name = campos[0].trim();

                if (pitchRepository.existsByName(name)) {
                    continue;
                }

                PitchType pitchType = PitchType.valueOf(campos[5].trim().toUpperCase());

                Pitch pitch = new Pitch(name, campos[1].trim(), null, PitchAccess.PUBLIC, pitchType);
                pitch.setAddress(campos[2].trim());
                pitch.setLatitude(Double.parseDouble(campos[3].trim()));
                pitch.setLongitude(Double.parseDouble(campos[4].trim()));

                String imageUrl = campos.length > 6 ? campos[6].trim() : "";
                if (!imageUrl.isEmpty()) {
                    pitch.setImageUrl(imageUrl);
                }

                boolean featured = campos.length > 7 && Boolean.parseBoolean(campos[7].trim());
                pitch.setFeatured(featured);

                pitchRepository.save(pitch);
            }
        }
    }

}
