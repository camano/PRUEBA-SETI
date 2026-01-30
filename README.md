
# SETI - API Reactiva para Franquicias

## 📌 Descripción del Proyecto
SETI es una aplicación **reactiva** desarrollada con **Spring Boot WebFlux** que permite gestionar franquicias, sucursales y productos. Cada franquicia puede tener múltiples sucursales y cada sucursal puede contener múltiples productos.

El proyecto implementa:
- Arquitectura **hexagonal** (Clean Architecture / Ports & Adapters)
- Persistencia **reactiva** con **R2DBC** y **PostgreSQL**
- Documentación de APIs con **Swagger/OpenAPI**
- Logging reactivo con `doOnNext`, `doOnError` y `doOnSubscribe`

---

## Aclaraciones ramas

- main rama con la arquitectura sin plugin 
- plugin es la arquitectura con el plugin 

## 🗂️ Estructura del proyecto
Se esta utilizando la arquitectura https://bancolombia.github.io/scaffold-clean-architecture/docs/intro/


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

## Consulta SQL 

Esta es el schema para que pueda funcionar 

CREATE DATABASE seti_db;

CREATE TABLE IF NOT EXISTS franchises (
id VARCHAR(20) PRIMARY KEY,
name VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS branches (
id VARCHAR(20) PRIMARY KEY,
franchise_id VARCHAR(20) NOT NULL,
name VARCHAR(100) NOT NULL,
CONSTRAINT fk_franchise
FOREIGN KEY (franchise_id)
REFERENCES franchises(id)
);

CREATE TABLE IF NOT EXISTS products (
id VARCHAR(20) PRIMARY KEY,
branch_id VARCHAR(20) NOT NULL,
name VARCHAR(100) NOT NULL,
stock INT NOT NULL,
CONSTRAINT fk_branch
FOREIGN KEY (branch_id)
REFERENCES branches(id)
);
CREATE TABLE IF NOT EXISTS sequences (
name VARCHAR(50) PRIMARY KEY,
value BIGINT NOT NULL
);

INSERT INTO sequences (name, value)
VALUES ('franchise', 0),('branch', 0),('product', 0)
ON CONFLICT (name) DO NOTHING;







