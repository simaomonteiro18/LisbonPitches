package com.simaomonteiro18.lisbonpitches.services;

import com.simaomonteiro18.lisbonpitches.repositories.PitchRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PitchServiceTest {

    @Mock
    private PitchRepository pitchRepository;

    @InjectMocks
    private PitchService pitchService;

    @Test
    @DisplayName("Teste à pesquisa com os valores nulos no fim da ordenação")
    void searchSortsNullsLast() {

        Pageable requested = PageRequest.of(2, 12, Sort.by(Sort.Order.desc("pricePerHour"), Sort.Order.asc("name")));

        when(pitchRepository.search(any(), any(), any(), any(Pageable.class))).thenReturn(Page.empty());

        pitchService.search(null, null, null, requested);

        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);

        verify(pitchRepository).search(isNull(), isNull(), isNull(), captor.capture());

        Pageable sent = captor.getValue();

        assertEquals(2, sent.getPageNumber());
        assertEquals(12, sent.getPageSize());
        assertEquals(2, sent.getSort().stream().count());
        sent.getSort().forEach(order -> assertEquals(Sort.NullHandling.NULLS_LAST, order.getNullHandling()));
        assertEquals(Sort.Direction.DESC, sent.getSort().getOrderFor("pricePerHour").getDirection());
        assertEquals(Sort.Direction.ASC, sent.getSort().getOrderFor("name").getDirection());

    }

    @Test
    @DisplayName("Teste à pesquisa sem ordenação pedida")
    void searchWithoutSortStaysUnsorted() {

        Pageable requested = PageRequest.of(0, 12);

        when(pitchRepository.search(any(), any(), any(), any(Pageable.class))).thenReturn(Page.empty());

        pitchService.search(null, null, null, requested);

        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);

        verify(pitchRepository).search(isNull(), isNull(), isNull(), captor.capture());

        Pageable sent = captor.getValue();

        assertEquals(0, sent.getPageNumber());
        assertEquals(12, sent.getPageSize());
        assertTrue(sent.getSort().isUnsorted());

    }

}
