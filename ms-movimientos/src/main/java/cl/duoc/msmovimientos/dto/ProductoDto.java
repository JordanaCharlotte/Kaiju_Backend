package cl.duoc.msmovimientos.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

// Datos del producto que recibimos desde ms-productos (solo los que necesitamos;
// el resto de campos del JSON se ignora)
@JsonIgnoreProperties(ignoreUnknown = true)
public record ProductoDto(
        Long id,
        String nombre,
        Integer stock,
        Integer stockMinimo
) {
}
