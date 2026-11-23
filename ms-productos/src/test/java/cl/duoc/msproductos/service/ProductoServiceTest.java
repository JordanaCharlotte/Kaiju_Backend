package cl.duoc.msproductos.service;

import cl.duoc.msproductos.exception.RecursoNoEncontradoException;
import cl.duoc.msproductos.exception.ReglaNegocioException;
import cl.duoc.msproductos.model.Producto;
import cl.duoc.msproductos.repository.ProductoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// Pruebas unitarias del servicio: el repositorio es falso (Mockito), así no se usa la base de datos
@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

    @Mock
    private ProductoRepository repository;

    @InjectMocks
    private ProductoService service;

    private Producto cappuccino;

    @BeforeEach
    void setUp() {
        cappuccino = new Producto("Cappuccino", "Bebidas Calientes", 3000.0, 5, 5, "780003");
        cappuccino.setId(3L);
    }

    @Test
    void buscarPorId_existente_devuelveElProducto() {
        when(repository.findById(3L)).thenReturn(Optional.of(cappuccino));

        Producto resultado = service.buscarPorId(3L);

        assertEquals("Cappuccino", resultado.getNombre());
    }

    @Test
    void buscarPorId_inexistente_lanzaRecursoNoEncontrado() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> service.buscarPorId(99L));
    }

    @Test
    void crear_conCodigoRepetido_lanzaErrorYNoGuarda() {
        when(repository.findByCodigoBarra("780003")).thenReturn(Optional.of(cappuccino));
        Producto nuevo = new Producto("Otro café", "Bebidas Calientes", 2000.0, 10, 2, "780003");

        ReglaNegocioException error = assertThrows(ReglaNegocioException.class, () -> service.crear(nuevo));

        assertEquals("El código 780003 ya pertenece a otro producto", error.getMessage());
        verify(repository, never()).save(any());
    }

    @Test
    void crear_conCodigoVacio_loGuardaComoNull() {
        Producto nuevo = new Producto("Queque", "Repostería", 1500.0, 8, 2, "   ");
        when(repository.save(any(Producto.class))).thenAnswer(invocacion -> invocacion.getArgument(0));

        Producto guardado = service.crear(nuevo);

        assertNull(guardado.getCodigoBarra());
    }

    @Test
    void ajustarStock_entrada_sumaElStock() {
        when(repository.findById(3L)).thenReturn(Optional.of(cappuccino));
        when(repository.save(any(Producto.class))).thenAnswer(invocacion -> invocacion.getArgument(0));

        Producto resultado = service.ajustarStock(3L, 10);

        assertEquals(15, resultado.getStock());
    }

    @Test
    void ajustarStock_ventaMayorAlStock_lanzaErrorYNoGuarda() {
        when(repository.findById(3L)).thenReturn(Optional.of(cappuccino));

        ReglaNegocioException error = assertThrows(ReglaNegocioException.class, () -> service.ajustarStock(3L, -6));

        assertEquals("Stock insuficiente (disponible: 5)", error.getMessage());
        verify(repository, never()).save(any());
    }
}
