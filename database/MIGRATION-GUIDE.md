# Guía para Ejecutar la Migración en Railway

## Opción 1: Usando Railway CLI (Recomendado)

### Paso 1: Instalar Railway CLI
```bash
# Windows (PowerShell)
iwr https://railway.app/install.ps1 | iex

# Verificar instalación
railway --version
```

### Paso 2: Login en Railway
```bash
railway login
```

### Paso 3: Vincularse al proyecto
```bash
# En el directorio del proyecto
railway link
# Selecciona tu proyecto de la lista
```

### Paso 4: Ejecutar el script de migración
```bash
# Conectarse a PostgreSQL y ejecutar el script
railway run psql $DATABASE_URL -f database/migration-stock-fields.sql
```

---

## Opción 2: Usando la Web UI de Railway

### Paso 1: Obtener credenciales de la base de datos
1. Ve a tu proyecto en Railway: https://railway.app
2. Haz clic en el servicio **Postgres**
3. Ve a la pestaña **Variables**
4. Copia las siguientes variables:
   - `PGHOST`
   - `PGPORT`
   - `PGDATABASE`
   - `PGUSER`
   - `PGPASSWORD`

### Paso 2: Usar el Query Tool de Railway
1. En el servicio Postgres, ve a la pestaña **Data**
2. Haz clic en **Query**
3. Copia y pega el contenido de `database/migration-stock-fields.sql`
4. Ejecuta el script

---

## Opción 3: Usando pgAdmin o DBeaver

### Paso 1: Obtener credenciales (ver Opción 2, Paso 1)

### Paso 2: Crear conexión
- Host: [PGHOST value]
- Port: [PGPORT value]
- Database: [PGDATABASE value]
- Username: [PGUSER value]
- Password: [PGPASSWORD value]

### Paso 3: Ejecutar el script
1. Abre `database/migration-stock-fields.sql`
2. Ejecuta el script completo

---

## Opción 4: Temporal - Recrear tablas (⚠️ BORRA DATOS)

**Solo usar si no hay datos importantes en producción**

### Modificar application-prod.properties:
```properties
# Cambiar esta línea temporalmente:
spring.jpa.hibernate.ddl-auto=create  # Era 'update'
```

### Pasos:
1. Commit y push el cambio
2. Espera a que Railway redepliegue
3. La aplicación creará las tablas desde cero
4. **Revertir inmediatamente** a `ddl-auto=update`
5. Commit y push de nuevo

---

## Verificación Post-Migración

Después de ejecutar la migración, verifica con:

```sql
-- Ver estructura de la tabla
\d componentes_electronicos

-- Ver datos de ejemplo
SELECT id, nombre, stock_actual, stock_minimo, precio_unitario, ubicacion 
FROM componentes_electronicos 
LIMIT 5;
```

Deberías ver las nuevas columnas: `stock_actual`, `stock_minimo`, `precio_unitario`, `ubicacion`

---

## Rollback (si algo sale mal)

Si necesitas revertir:

```sql
-- Crear columnas antiguas
ALTER TABLE componentes_electronicos 
ADD COLUMN stock INTEGER,
ADD COLUMN precio DECIMAL(19,2);

-- Copiar datos de vuelta
UPDATE componentes_electronicos 
SET stock = stock_actual,
    precio = precio_unitario;

-- Hacer NOT NULL
ALTER TABLE componentes_electronicos 
ALTER COLUMN stock SET NOT NULL,
ALTER COLUMN precio SET NOT NULL;

-- Eliminar nuevas columnas
ALTER TABLE componentes_electronicos 
DROP COLUMN stock_actual,
DROP COLUMN stock_minimo,
DROP COLUMN precio_unitario,
DROP COLUMN ubicacion;
```
