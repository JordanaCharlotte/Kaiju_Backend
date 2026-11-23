package cl.duoc.msusuarios.dto;

import cl.duoc.msusuarios.model.Rol;
import cl.duoc.msusuarios.model.Usuario;

// Lo que se devuelve a la app: sin la contraseña
public record UsuarioResponse(Long id, String nombre, String email, Rol rol) {

    public static UsuarioResponse desde(Usuario usuario) {
        return new UsuarioResponse(usuario.getId(), usuario.getNombre(), usuario.getEmail(), usuario.getRol());
    }
}
