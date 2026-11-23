# Kaiju Backend

Backend de **Kaiju Inventory**, la app Android para la gestión y trazabilidad del inventario de Kaiju.
Evaluación Parcial 3 de Desarrollo de Aplicaciones Móviles (DSY1105) · Duoc UC San Bernardo.

**Integrantes:** Jordana Vásquez y Davis Monsalvez (Grupo 12)

## Repositorios del proyecto

| Repositorio | Contenido |
|---|---|
| [KaijuInventory_Grupo12](https://github.com/JordanaCharlotte/KaijuInventory_Grupo12) | App Android (Kotlin + Jetpack Compose) |
| [Kaiju_Backend](https://github.com/JordanaCharlotte/Kaiju_Backend) | Microservicios Spring Boot (este repositorio) |

## Arquitectura

El backend está dividido en **tres microservicios**. Cada uno tiene una sola responsabilidad y su propia base de datos H2.

```
App Android
   │  HTTP (JSON)
   ├──────────────► ms-usuarios    (puerto 8083)  ── H2: usuarios-db
   ├──────────────► ms-productos   (puerto 8081)  ── H2: productos-db
   │                     ▲
   │                     │ GET producto / PATCH stock
   └──────────────► ms-movimientos (puerto 8082)  ── H2: movimientos-db
```

- **ms-usuarios:** inicio de sesión y usuarios, con contraseñas cifradas con BCrypt.
- **ms-productos:** CRUD del catálogo de productos y control del stock.
- **ms-movimientos:** registra entradas, salidas y ventas (trazabilidad). Al registrar un movimiento llama a `ms-productos` para validar y actualizar el stock.

Cada microservicio sigue la misma estructura en capas:

```
controller/   Endpoints REST (reciben y responden JSON)
service/      Lógica de negocio y reglas del inventario
repository/   Acceso a la base de datos con Spring Data JPA
model/        Entidades que se guardan en la base de datos
dto/          Objetos que se reciben o envían
exception/    Errores propios y manejador global (respuestas JSON claras)
```

## Tecnologías

- Java 21 · Spring Boot 4.2 · Maven
- Spring Web, Spring Data JPA, Validation
- Spring Security Crypto (solo BCrypt, para cifrar contraseñas)
- Base de datos H2 guardada en archivo (carpeta `data/`)
- JUnit 5 y Mockito para las pruebas unitarias

## Cómo ejecutarlo

Requisitos: un JDK 21 o superior (sirve el que trae Android Studio).

Abrir **una terminal por microservicio**. `ms-productos` debe estar encendido antes de registrar movimientos, porque `ms-movimientos` depende de él.

```powershell
# Terminal 1
cd ms-productos
$env:JAVA_HOME="C:\Program Files\Android\Android Studio\jbr"
.\mvnw.cmd spring-boot:run

# Terminal 2
cd ms-movimientos
$env:JAVA_HOME="C:\Program Files\Android\Android Studio\jbr"
.\mvnw.cmd spring-boot:run

# Terminal 3
cd ms-usuarios
$env:JAVA_HOME="C:\Program Files\Android\Android Studio\jbr"
.\mvnw.cmd spring-boot:run
```

Para apagar un microservicio se presiona **Ctrl + C** en su terminal.

La primera vez que se ejecutan, `ms-productos` carga 12 productos ficticios de Kaiju y `ms-usuarios` crea tres usuarios de prueba (uno por perfil del caso):

| Correo | Contraseña | Rol |
|---|---|---|
| admin@kaiju.cl | admin123 | ADMINISTRADOR |
| vendedor@kaiju.cl | vendedor123 | VENDEDOR |
| inventario@kaiju.cl | inventario123 | ENCARGADO_INVENTARIO |

Desde el emulador de Android, el PC se alcanza con la IP `10.0.2.2` (por ejemplo `http://10.0.2.2:8081/api/productos`). Desde un celular real se usa la IP del PC en la red Wi-Fi.

## Endpoints

### ms-usuarios (`http://localhost:8083`)

| Método | Ruta | Descripción | Respuesta |
|---|---|---|---|
| POST | `/api/auth/login` | Inicia sesión; devuelve el usuario sin la contraseña | 200 / 400 / 401 |
| GET | `/api/usuarios` | Lista los usuarios (sin contraseñas) | 200 |
| POST | `/api/usuarios` | Crea un usuario (contraseña mínima de 6 caracteres) | 201 / 400 / 409 |

```json
{
  "email": "admin@kaiju.cl",
  "password": "admin123"
}
```

### ms-productos (`http://localhost:8081`)

| Método | Ruta | Descripción | Respuesta |
|---|---|---|---|
| GET | `/api/productos` | Lista todos los productos | 200 |
| GET | `/api/productos/{id}` | Busca un producto | 200 / 404 |
| GET | `/api/productos/stock-bajo` | Productos en su stock mínimo o bajo él | 200 |
| POST | `/api/productos` | Crea un producto | 201 / 400 / 409 |
| PUT | `/api/productos/{id}` | Actualiza un producto | 200 / 400 / 404 / 409 |
| DELETE | `/api/productos/{id}` | Elimina un producto | 204 / 404 |
| PATCH | `/api/productos/{id}/stock` | Suma o resta stock | 200 / 404 / 409 |

Ejemplo de cuerpo para crear un producto:

```json
{
  "nombre": "Queque de Vainilla",
  "categoria": "Repostería",
  "precio": 1500,
  "stock": 10,
  "stockMinimo": 3,
  "codigoBarra": "780013"
}
```

### ms-movimientos (`http://localhost:8082`)

| Método | Ruta | Descripción | Respuesta |
|---|---|---|---|
| GET | `/api/movimientos` | Historial completo (más reciente primero) | 200 |
| GET | `/api/movimientos/{id}` | Busca un movimiento | 200 / 404 |
| GET | `/api/movimientos/producto/{productoId}` | Historial de un producto | 200 |
| POST | `/api/movimientos` | Registra una entrada, salida o venta | 201 / 400 / 404 / 409 / 503 |

Ejemplo de cuerpo para registrar una venta (`tipo` puede ser `ENTRADA`, `SALIDA` o `VENTA`):

```json
{
  "productoId": 3,
  "tipo": "VENTA",
  "cantidad": 1
}
```

### Errores

Todos los errores responden en JSON con un mensaje en español:

```json
{
  "estado": 409,
  "mensaje": "Stock insuficiente (disponible: 4)"
}
```

| Código | Cuándo ocurre |
|---|---|
| 401 | Correo o contraseña incorrectos (mismo mensaje para ambos casos, así no se revela qué correos existen) |
| 400 | Datos inválidos (por ejemplo, precio 0 o nombre vacío). Incluye un mensaje por campo en `errores` |
| 404 | El producto o movimiento no existe |
| 409 | Se rompe una regla del inventario: código de barra repetido o stock insuficiente |
| 503 | `ms-movimientos` no puede comunicarse con `ms-productos` |

## Reglas de negocio

- El nombre, la categoría, el precio (mayor a 0), el stock y el stock mínimo (no negativos) son obligatorios.
- El código de barra es opcional, pero si se ingresa debe tener entre 6 y 20 dígitos y no puede repetirse.
- Una salida o venta no puede dejar el stock en negativo.
- Las contraseñas nunca se guardan ni se devuelven: solo se guarda su hash BCrypt.
- El correo no puede repetirse y se guarda en minúsculas.
- Cada movimiento guarda la fecha y el nombre del producto, para mantener el historial aunque el producto cambie.

## Base de datos (consola H2)

Cada microservicio tiene una consola web para ver y modificar los datos:

| Microservicio | Consola | JDBC URL |
|---|---|---|
| ms-productos | http://localhost:8081/h2-console | `jdbc:h2:file:./data/productos-db` |
| ms-movimientos | http://localhost:8082/h2-console | `jdbc:h2:file:./data/movimientos-db` |
| ms-usuarios | http://localhost:8083/h2-console | `jdbc:h2:file:./data/usuarios-db` |

Usuario: `sa` · Contraseña: (vacía)

## Pruebas unitarias

Las pruebas revisan la lógica de los servicios. Usan Mockito para reemplazar la base de datos y la comunicación con el otro microservicio, así se prueban de forma aislada. Durante las pruebas se usa una base H2 en memoria, separada de la real.

```powershell
cd ms-productos
.\mvnw.cmd test
```

| Microservicio | Qué se prueba |
|---|---|
| ms-productos | Buscar por id (existente e inexistente), código de barra repetido, código vacío, sumar stock y no permitir stock negativo |
| ms-usuarios | Login correcto, correo con mayúsculas o espacios, contraseña incorrecta, correo inexistente, contraseña guardada cifrada y correo repetido |
| ms-movimientos | Una entrada suma stock, una venta lo resta y una venta mayor al stock disponible se rechaza sin guardar nada |

## API externa

La app consume directamente la API pública de **Open Food Facts**, sin pasar por estos microservicios, para buscar datos de un producto por su código de barras:

```
GET https://world.openfoodfacts.org/api/v2/product/{codigo}.json
```

Todos los datos del proyecto son ficticios, como exige el caso.
