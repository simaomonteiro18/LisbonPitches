package com.simaomonteiro18.lisbonpitches.playground;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CalculatorTest {

    Calculator calculator = new Calculator();

    @Test
    @DisplayName("Teste de Soma")
    void verifySum() {

        assertEquals(2, calculator.sum(1, 1));

    }

    @Test
    @DisplayName("Teste de Subtração")
    void verifySubtraction() {

        assertEquals(3, calculator.subtract(4, 1));

    }

    @Test
    @DisplayName("Teste de Resultado = 0")
    void verifyZero() {

        assertEquals(0, calculator.sum(-1, 1));

    }

}
