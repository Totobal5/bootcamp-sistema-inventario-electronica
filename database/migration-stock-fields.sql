-- Migration: Update ComponenteElectronico stock fields
-- Date: 2025-12-06
-- Description: Replace 'stock' and 'precio' with detailed inventory fields

-- Step 1: Add new columns (nullable initially)
ALTER TABLE componentes_electronicos 
ADD COLUMN IF NOT EXISTS stock_actual INTEGER,
ADD COLUMN IF NOT EXISTS stock_minimo INTEGER,
ADD COLUMN IF NOT EXISTS precio_unitario DECIMAL(19,2),
ADD COLUMN IF NOT EXISTS ubicacion VARCHAR(100);

-- Step 2: Migrate data from old columns to new ones
UPDATE componentes_electronicos 
SET stock_actual = stock,
    stock_minimo = 0,
    precio_unitario = COALESCE(precio, 0),
    ubicacion = 'Sin ubicación'
WHERE stock_actual IS NULL;

-- Step 3: Make new columns NOT NULL
ALTER TABLE componentes_electronicos 
ALTER COLUMN stock_actual SET NOT NULL,
ALTER COLUMN stock_minimo SET NOT NULL,
ALTER COLUMN precio_unitario SET NOT NULL;

-- Step 4: Drop old columns
ALTER TABLE componentes_electronicos 
DROP COLUMN IF EXISTS stock,
DROP COLUMN IF EXISTS precio;

-- Verification query (optional - comment out after verification)
-- SELECT id, nombre, stock_actual, stock_minimo, precio_unitario, ubicacion 
-- FROM componentes_electronicos 
-- LIMIT 10;
