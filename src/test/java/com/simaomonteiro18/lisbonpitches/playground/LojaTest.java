package com.simaomonteiro18.lisbonpitches.playground;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LojaTest {

    @Mock Armazem armazem;

    @Test
    @DisplayName("Teste Procura de Preço")
    void preco() {
        when(armazem.procurarPreco("bola")).thenReturn(10.0);

        Loja loja = new Loja(armazem);

        double preco = loja.precoComDesconto("bola");

        assertEquals(9.0, preco);

    }

}
