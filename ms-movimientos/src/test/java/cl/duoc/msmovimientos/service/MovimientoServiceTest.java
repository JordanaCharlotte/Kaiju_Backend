package cl.duoc.msmovimientos.service;

import cl.duoc.msmovimientos.client.ProductoClient;
import cl.duoc.msmovimientos.dto.MovimientoRequest;
import cl.duoc.msmovimientos.dto.ProductoDto;
import cl.duoc.msmovimientos.exception.ReglaNegocioException;
import cl.duoc.msmovimientos.model.Movimiento;
import cl.duoc.msmovimientos.model.TipoMovimiento;
import cl.duoc.msmovimientos.repository.MovimientoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// Pruebas del servicio de movimientos: ms-productos y la base de datos se reemplazan por mocks
@ExtendWith(MockitoExtension.class)
class MovimientoServiceTest {

    @Mock
    private MovimientoRepository repository;

    @Mock
    private ProductoClient productoClient;

    @InjectMocks
    private MovimientoService service;

    private final ProductoDto cappuccino = new ProductoDto(3L, "Cappuccino", 5, 5);

    @Test
    void registrar_entrada_sumaStockYGuardaElMovimiento() {
        when(productoClient.obtenerProducto(3L)).thenReturn(cappuccino);
        when(repository.save(any(Movimiento.class))).thenAnswer(invocacion -> invocacion.getArgument(0));

        Movimiento resultado = service.registrar(new MovimientoRequest(3L, TipoMovimiento.ENTRADA, 10));

        verify(productoClient).ajustarStock(3L, 10);
        assertEquals("Cappuccino", resultado.getProductoNombre());
        assertNotNull(resultado.getFecha());
    }

    @Test
    void registrar_venta_restaStock() {
        when(productoClient.obtenerProducto(3L)).thenReturn(cappuccino);
        when(repository.save(any(Movimiento.class))).thenAnswer(invocacion -> invocacion.getArgument(0));

        service.registrar(new MovimientoRequest(3L, TipoMovimiento.VENTA, 2));

        verify(productoClient).ajustarStock(3L, -2);
    }

    @Test
    void registrar_ventaMayorAlStock_lanzaErrorSinTocarNada() {
        when(productoClient.obtenerProducto(3L)).thenReturn(cappuccino);

        ReglaNegocioException error = assertThrows(ReglaNegocioException.class,
                () -> service.registrar(new MovimientoRequest(3L, TipoMovimiento.VENTA, 6)));

        assertEquals("Stock insuficiente (disponible: 5)", error.getMessage());
        verify(productoClient, never()).ajustarStock(anyLong(), anyInt());
        verify(repository, never()).save(any());
    }
}
