# Verificación de cumplimiento de requerimientos

Fecha de verificación: 2026-04-08
Repositorio: `proyectoIS`

## Método aplicado

Se revisó la estructura del backend (Spring) y frontend (Angular), controladores, modelos y servicios disponibles. Además, se intentó ejecutar pruebas/compilación para validar consistencia técnica mínima.

## Resultado ejecutivo

Estado global: **CUMPLIMIENTO PARCIAL / INCOMPLETO**.

Hallazgos críticos:
1. Hay archivos vacíos en puntos clave (`SecurityConfig`, `AppRoutingModule`, `AppComponent`).
2. Existen referencias a clases no presentes (DTOs, servicios y repositorios), por lo que el sistema no es compilable tal como está.
3. El build backend no pudo resolverse por dependencia padre no descargable en el entorno (HTTP 403 a Maven Central).
4. Aunque hay endpoints y modelos alineados con varios módulos solicitados, faltan piezas para considerar cumplimiento total.

---

## Matriz de cumplimiento por módulos solicitados

### 1) Módulo Adquisiciones (Compras)
**Requerido**
- Registro de entrada desde facturas.
- Gestión de proveedores.
- Vinculación con partidas presupuestarias.

**Evidencia encontrada**
- Controlador de adquisiciones con creación de adquisición y subida de factura (`/adquisiciones`, `/{id}/factura`).
- Endpoints para proveedores (`/adquisiciones/proveedores`).
- Endpoints para partidas y validación de saldo (`/adquisiciones/partidas`, `/partidas/validar`).
- Modelo `Adquisicion` contiene `numeroFactura`, `archivoFactura`, `partidaPresupuestaria`.

**Estado**: **PARCIALMENTE CUMPLE**
- Funcionalidad modelada y expuesta en API, pero no es verificable end-to-end por faltantes estructurales (DTOs/servicios auxiliares y compilación general).

### 2) Módulo Inventario Central
**Requerido**
- Catálogo maestro de bienes.
- Control de existencias en almacén.
- Etiquetado de activos (QR / RFID).

**Evidencia encontrada**
- Endpoints `/inventario/activos` para listar, obtener, crear y actualizar activos.
- Endpoint para QR: `/inventario/activos/{id}/qr`.
- Modelo `Activo` incluye `codigoIdentificacion`, `ubicacionFisica`, `codigoQR`.

**Brechas**
- No se identificó lógica explícita de RFID.
- No se identificó manejo explícito de “existencias en almacén” con kardex/stock por almacén.

**Estado**: **PARCIALMENTE CUMPLE**

### 3) Módulo Asignaciones y Resguardos
**Requerido**
- Control de préstamos.
- Traslados entre departamentos.
- Firmas de responsabilidad del empleado.

**Evidencia encontrada**
- Endpoints de asignaciones y devolución (`POST /asignaciones`, `PUT /{id}/devolver`).
- Endpoint de firma digital (`POST /asignaciones/{id}/firma`).
- Historial por activo (`GET /asignaciones/activo/{activoId}/historial`).
- Modelo `Asignacion` contiene `firmaDigital`, `documentoRespaldo`, estado de asignación.

**Brechas**
- No se encontró endpoint/flujo explícito para traslado interdepartamental (aunque existe estado `TRANSFERIDA`).

**Estado**: **PARCIALMENTE CUMPLE**

### 4) Módulo Bajas y Enajenación
**Requerido**
- Gestión de activos obsoletos, dañados o vendidos.
- Flujo de aprobación jerárquico.

**Evidencia encontrada**
- Endpoints para solicitar, aprobar/rechazar por nivel y ejecutar baja.
- Servicio `BajaService` crea flujo de 3 niveles y valida estado previo.
- Modelo `Baja` incluye tipo/motivo/estado, valor de venta y comprobante.

**Estado**: **MAYORMENTE CUMPLE (CONDICIONAL)**
- El diseño funcional está presente; queda condicionado a resolver faltantes de compilación e integración.

### 5) Módulo Reportes
**Requerido**
- Reporte de bienes invertidos en la empresa.
- Reporte de bienes asignados a un empleado.

**Evidencia encontrada**
- En `ReporteService` existen métodos para:
  - `generarReporteBienesInvertidos(...)`
  - `generarReporteBienesAsignadosEmpleados(...)`
- En `ReporteController` existen endpoints para hoja de vida, centro de costo, depreciación y próximos a baja.

**Brechas**
- No se encontró endpoint expuesto directo para “bienes invertidos en la empresa”.
- No se encontró endpoint expuesto directo para “bienes asignados a un empleado” como reporte específico general (sí existe “hoja de vida por empleado”).

**Estado**: **PARCIALMENTE CUMPLE**

---

## Requerimientos no funcionales (evaluación por evidencia disponible)

- **Seguridad (roles/permisos)**: Hay uso extensivo de `@PreAuthorize` en controladores. **Parcialmente cumple**.
- **Disponibilidad 24/7**: No se observan evidencias operativas/infra (monitoring, despliegue HA). **No verificable**.
- **Rendimiento (<3s)**: No se observan pruebas de performance. **No verificable**.
- **Escalabilidad**: No se observaron pruebas/capacidad documentada. **No verificable**.
- **Usabilidad**: Hay componentes frontend, pero con archivos núcleo vacíos. **Parcial / no verificable**.
- **Respaldo automático BD**: No se encontró configuración de backups automáticos. **No evidenciado**.

---

## Reglas de negocio (validación rápida)

1. **Todo activo asociado a factura de adquisición**
   - En modelo `Activo`, relación con `Adquisicion` no está marcada `nullable=false`; no garantiza obligatoriedad a nivel esquema. **No garantizado**.

2. **Ningún activo puede asignarse si no está registrado en inventario**
   - No se pudo confirmar flujo completo de `AsignacionService` (archivo no disponible en repositorio visible). **No verificable**.

3. **Activos dados de baja no pueden volver a asignarse**
   - `BajaService` cambia estado del activo a `DADO_DE_BAJA`; falta validar restricción en flujo de asignación (servicio no disponible). **Parcial / no verificable**.

4. **Toda asignación requiere responsable registrado**
   - Modelo `Asignacion` tiene `usuarioResponsable` con `nullable=false`. **Cumple a nivel modelo**.

5. **Bajas con aprobación jerárquica**
   - `BajaService` implementa niveles y validación de aprobaciones. **Cumple en lógica observada**.

---

## Hallazgos técnicos críticos adicionales

1. **Archivos vacíos críticos**
   - `gestion-activos-backend/src/main/java/com/empresa/gestionactivos/config/SecurityConfig.java` (0 líneas).
   - `gestion-activos-frontend/src/app/app-routing.module.ts` (0 líneas).
   - `gestion-activos-frontend/src/app/app.component.ts` (0 líneas).

2. **Dependencias de código ausentes en árbol visible**
   - Se referencian DTOs, repositorios y servicios no presentes en el conjunto de archivos encontrado (ej. `ActivoDTO`, `AsignacionService`, etc.).

3. **Compilación backend no ejecutable en entorno actual**
   - `mvn test` falla por imposibilidad de resolver parent POM (`spring-boot-starter-parent:3.1.5`) debido a 403 contra Maven Central en este entorno.

---

## Conclusión

Con la evidencia actual, el código **no puede certificarse como totalmente conforme** al documento de requerimientos.

Calificación recomendada:
- **Cumplimiento funcional:** ~60-70% (diseño/estructura aparente).
- **Cumplimiento técnico verificable ejecutable:** **bajo**, debido a faltantes de código y build no resoluble en el entorno.

## Recomendaciones inmediatas

1. Completar archivos vacíos críticos (seguridad y bootstrap frontend).
2. Incorporar todas las clases DTO/servicios/repositorios faltantes en el árbol del proyecto.
3. Exponer endpoints faltantes para reportes exigidos explícitamente (bienes invertidos y bienes asignados).
4. Añadir pruebas automatizadas para reglas de negocio clave.
5. Documentar y automatizar NFRs: backups, SLO de latencia, disponibilidad y escalabilidad.
