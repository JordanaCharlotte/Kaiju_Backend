package cl.duoc.msmovimientos.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

// Crea el cliente HTTP que usamos para hablar con ms-productos
@Configuration
public class ClienteConfig {

    @Bean
    public RestClient productosRestClient(@Value("${productos.url}") String urlProductos) {
        return RestClient.builder()
                .baseUrl(urlProductos)
                .build();
    }
}
