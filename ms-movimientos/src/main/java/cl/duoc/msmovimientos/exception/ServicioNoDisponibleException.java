package cl.duoc.msmovimientos.exception;

// El microservicio de productos no responde (responde 503)
public class ServicioNoDisponibleException extends RuntimeException {
    public ServicioNoDisponibleException(String mensaje) {
        super(mensaje);
    }
}
