package cl.duoc.msproductos.exception;

// Se lanza cuando los datos son válidos pero rompen una regla del inventario (responde 409)
public class ReglaNegocioException extends RuntimeException {
    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }
}
