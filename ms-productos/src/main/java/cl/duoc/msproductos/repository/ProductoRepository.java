package cl.duoc.msproductos.repository;

import cl.duoc.msproductos.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

// JpaRepository ya trae save, findAll, findById, deleteById, etc.
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    Optional<Producto> findByCodigoBarra(String codigoBarra);

    // Productos que están en su stock mínimo o por debajo
    @Query("SELECT p FROM Producto p WHERE p.stock <= p.stockMinimo")
    List<Producto> buscarStockBajo();
}
