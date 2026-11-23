package cl.duoc.msmovimientos.repository;

import cl.duoc.msmovimientos.model.Movimiento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MovimientoRepository extends JpaRepository<Movimiento, Long> {

    // El más reciente primero
    List<Movimiento> findAllByOrderByFechaDesc();

    List<Movimiento> findByProductoIdOrderByFechaDesc(Long productoId);
}
