package tacs.eventos.controller;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import tacs.eventos.service.EstadisticaService;

class EstadisticasControllerTest {

    @Mock
    private EstadisticaService estadisticaService;

    @InjectMocks
    private EstadisticasController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCantidadEventos() throws Exception {
        when(estadisticaService.cantidadEventos()).thenReturn(10);

        int result = controller.cantidadEventos();

        assertEquals(10, result);
        verify(estadisticaService).cantidadEventos();
    }

    @Test
    void testCantidadInscripciones() {
        when(estadisticaService.cantidadInscribiciones()).thenReturn(20);

        int result = controller.cantidadInscripciones();

        assertEquals(20, result);
        verify(estadisticaService).cantidadInscribiciones();
    }

    @Test
    void testTasaConversionWL() {
        String eventoId = "123";
        when(estadisticaService.calcularTasaConversionWL(eventoId)).thenReturn(50);

        int result = controller.tasaConversionWL(eventoId);

        assertEquals(50, result);
        verify(estadisticaService).calcularTasaConversionWL(eventoId);
    }
}
