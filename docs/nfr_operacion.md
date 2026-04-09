# NFRs operativos y automatización

## Objetivo
Definir mecanismos operativos para cumplir requerimientos no funcionales de seguridad, disponibilidad, rendimiento, escalabilidad y respaldo.

## 1) Respaldos automáticos (BD)
Se incluye script: `scripts/backup_db.sh`.

### Ejecución manual
```bash
./scripts/backup_db.sh
```

### Automatización sugerida (cron)
```bash
0 2 * * * cd /workspace/proyectoIS && ./scripts/backup_db.sh >> logs/backup.log 2>&1
```

## 2) SLO de latencia y disponibilidad
- **SLO disponibilidad**: 99.5% mensual.
- **SLO latencia API**: p95 < 3s para endpoints transaccionales.
- **Error budget**: 3h 39m de indisponibilidad/mes aprox.

## 3) Observabilidad
Habilitar Actuator para health/metrics.
- Health endpoint: `/api/actuator/health`
- Métricas: `/api/actuator/metrics`

## 4) Escalabilidad
- Escalado horizontal de la API detrás de balanceador.
- Base de datos con índices en campos de búsqueda de activos, asignaciones y adquisiciones.

## 5) Seguridad
- Control por roles con `@PreAuthorize`.
- Política de contraseñas y rotación de secretos en entorno productivo.
