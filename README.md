
# SETI - API Reactiva para Franquicias

## 📌 Descripción del Proyecto
SETI es una aplicación **reactiva** desarrollada con **Spring Boot WebFlux** que permite gestionar franquicias, sucursales y productos. Cada franquicia puede tener múltiples sucursales y cada sucursal puede contener múltiples productos.

El proyecto implementa:
- Arquitectura **hexagonal** (Clean Architecture / Ports & Adapters)
- Persistencia **reactiva** con **R2DBC** y **PostgreSQL**
- Documentación de APIs con **Swagger/OpenAPI**
- Logging reactivo con `doOnNext`, `doOnError` y `doOnSubscribe`

---

## 🗂️ Estructura del proyecto
src/
├─ main/
│ ├─ java/com/prueba/seti/
│ │ ├─ domain/ # Modelos y lógica de negocio
│ │ ├─ service/ # Casos de uso (UseCases)
│ │ ├─ adapter/ # Puertos de persistencia / R2DBC
│ │ ├─ controller/ # Endpoints REST
│ │ └─ config/ # Configuraciones (Swagger, R2DBC)
│ └─ resources/
│ ├─ application.yml # Configuración de base de datos y logs
└─ test/
└─ java/com/prueba/seti/
└─ (Pruebas unitarias y de integración)


---

## 💾 Persistencia

- Base de datos: **PostgreSQL**
- Conexión reactiva vía **R2DBC**
- Tablas principales:
    - `franchises` (id VARCHAR, name VARCHAR)
    - `branches` (id VARCHAR, franchise_id VARCHAR, name VARCHAR)
    - `products` (id VARCHAR, branch_id VARCHAR, name VARCHAR, stock INT)
- Secuencias de IDs personalizados:
    - Franquicia: `f-1, f-2, ...`
    - Sucursal: `b-1, b-2, ...`
    - Producto: `p-1, p-2, ...`

> Nota: No se usa JPA; las inserciones se hacen **explícitamente con DatabaseClient** para respetar los IDs personalizados.

---
## 🔗 Requerimientos principales

1. Exponer endpoint para agregar una nueva franquicia.
2. Exponer endpoint para agregar una nueva sucursal a una franquicia.
3. Exponer endpoint para agregar un nuevo producto a una sucursal.
4. Exponer endpoint para eliminar un nuevo producto a una sucursal.
5. Exponer endpoint para modificar el stock de un producto.
6. Exponer endpoint que permita mostrar cual es el producto que más stock tiene por
   sucursal para una franquicia puntual. Debe retornar un listado de productos que
   indique a que sucursal pertenece.
7. Exponer endpoint que permita actualizar el nombre de una franquicia.
8. Exponer endpoint que permita actualizar el nombre de una sucursal.
9. Exponer endpoint que permita actualizar el nombre de un producto.

## 🔗 Endpoints principales

| Recurso | Método | URL                               | Descripción                                   |
|---------|--------|-----------------------------------|-----------------------------------------------|
| Franquicia | POST   | `/api/franchise/add`              | Crear nueva franquicia                        |
| Franquicia | PATH   | `/api/franchise/{id}/name`        | Actualizar nombre de franquicia               |
| Sucursal | POST   | `/api/franchises/add/{id}/branches` | Agregar sucursal a franquicia                 |
| Franquicia | PATH   | `/api/branch/{id}/name`           | Actualizar nombre de la sucursal              |
| Producto | POST   | `/api/product/add/{id}/products`  | Agregar producto a sucursal                   |
| Producto | PATH   | `/api/product/{id}/name`          | Actualizar nombre del producto                |
| Producto | PATH   | `/api/product/{id}/stock}`        | Actualizar stock                              |
| Producto | DELETE | `/api/product/{id}`               | Eliminar producto                             |
| Productos | GET    | `/api/product/{id}/top-stock`     | Obtener producto con mayor stock por sucursal |

---

## 📊 Documentación Swagger

- URL: [http://localhost:8080/webjars/swagger-ui/index.html](http://localhost:8080/webjars/swagger-ui/index.html)
- Permite probar requests directamente desde el navegador.

---

## Levantar Proyecto con docker compose
- En la raiz se encuentra un documento para poder ejecutarlo en local o en mi drive https://docs.google.com/document/d/1TGNTwwl-Z49umd9js_GDggfskJdnzMV8MmZ7PhM7iFY/edit?usp=sharing
- mvn clean install -- ejecuta el jar**
- docker-compose up -- se ejecuta las imagenes de la base de datos y app**
- En la raiz del proyecto encuentran la coleccion de postman






