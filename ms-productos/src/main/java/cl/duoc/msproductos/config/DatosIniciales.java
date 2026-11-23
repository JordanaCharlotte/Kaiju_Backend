package cl.duoc.msproductos.config;

import cl.duoc.msproductos.model.Producto;
import cl.duoc.msproductos.repository.ProductoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

// Carga el catálogo ficticio de Kaiju solo la primera vez (cuando la tabla está vacía)
@Component
public class DatosIniciales implements CommandLineRunner {

    private final ProductoRepository repository;

    public DatosIniciales(ProductoRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... args) {
        if (repository.count() > 0) {
            return;
        }
        repository.saveAll(List.of(
                new Producto("Café Espresso", "Bebidas Calientes", 2200.0, 20, 5, "780001"),
                new Producto("Café Latte", "Bebidas Calientes", 2800.0, 12, 5, "780002"),
                new Producto("Cappuccino", "Bebidas Calientes", 3000.0, 5, 5, "780003"),
                new Producto("Té Verde", "Bebidas Calientes", 1900.0, 25, 8, "780004"),
                new Producto("Chocolate Caliente", "Bebidas Calientes", 2900.0, 9, 5, "780005"),
                new Producto("Jugo de Naranja", "Bebidas Frías", 2500.0, 10, 6, "780006"),
                new Producto("Café Helado", "Bebidas Frías", 3200.0, 3, 5, "780007"),
                new Producto("Muffin de Arándano", "Repostería", 1800.0, 0, 3, "780008"),
                new Producto("Muffin de Chocolate", "Repostería", 1800.0, 7, 3, "780009"),
                new Producto("Croissant de Mantequilla", "Repostería", 1600.0, 15, 5, "780010"),
                new Producto("Galleta con Chips", "Repostería", 900.0, 30, 10, "780011"),
                new Producto("Sándwich Jamón Queso", "Salados", 3500.0, 0, 4, "780012")
        ));
    }
}
