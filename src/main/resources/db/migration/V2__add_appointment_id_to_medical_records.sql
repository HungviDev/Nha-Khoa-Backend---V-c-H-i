-- ==============================================================
-- Migration: Add appointment_id to medical_records
-- Database: Microsoft SQL Server
-- ==============================================================

IF NOT EXISTS (
    SELECT 1 FROM sys.columns 
    WHERE object_id = OBJECT_ID(N'dbo.medical_records') 
    AND name = 'appointment_id'
)
BEGIN
    ALTER TABLE dbo.medical_records
    ADD appointment_id INT NULL;
END;

IF NOT EXISTS (
    SELECT 1 FROM sys.foreign_keys 
    WHERE object_id = OBJECT_ID(N'dbo.FK_medical_records_appointments')
)
BEGIN
    ALTER TABLE dbo.medical_records
    ADD CONSTRAINT FK_medical_records_appointments
    FOREIGN KEY (appointment_id) REFERENCES dbo.appointments(id)
    ON DELETE SET NULL;
END;

IF NOT EXISTS (
    SELECT 1 FROM sys.indexes 
    WHERE name = 'UQ_medical_records_appointment_id' 
    AND object_id = OBJECT_ID(N'dbo.medical_records')
)
BEGIN
    CREATE UNIQUE INDEX UQ_medical_records_appointment_id 
    ON dbo.medical_records(appointment_id) 
    WHERE appointment_id IS NOT NULL;
END;

