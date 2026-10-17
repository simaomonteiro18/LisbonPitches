package com.simaomonteiro18.lisbonpitches;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

@AutoConfigureTestRestTemplate
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ErrorHandlingIntegrationTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    @DisplayName("Teste a /error em GET /pitches/...")
    public void errorGettingPitches() {

        ResponseEntity<String> testPitchesPath = testRestTemplate.getForEntity("/pitches/abc", String.class);

        assertEquals(400, testPitchesPath.getStatusCode().value());

        String body = testPitchesPath.getBody();

        assertFalse(body.contains("trace"));

    }

}