package com.simaomonteiro18.lisbonpitches.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "app.cors.allowed-origins=https://demo.exemplo.app")
@AutoConfigureMockMvc
public class CorsConfigurationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void allowedOriginReceivesCorsHeader() throws Exception {
        mockMvc.perform(options("/pitches")
                        .header("Origin", "https://demo.exemplo.app")
                        .header("Access-Control-Request-Method", "GET"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "https://demo.exemplo.app"));
    }

    @Test
    public void notAllowedOriginIsRejected() throws Exception {
        mockMvc.perform(options("/pitches")
                        .header("Origin", "https://outro.com")
                        .header("Access-Control-Request-Method", "GET"))
                .andExpect(status().isForbidden());
    }
}
