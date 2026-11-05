-- ==============================================================
-- Schema Initialization for Medical Management System
-- Database: Microsoft SQL Server
-- ==============================================================

-- 1. Table: roles
IF OBJECT_ID(N'dbo.roles', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.roles (
        id INT IDENTITY(1,1) NOT NULL,
        name NVARCHAR(50) NOT NULL,
        CONSTRAINT PK_roles PRIMARY KEY (id),
        CONSTRAINT UQ_roles_name UNIQUE (name)
    );
END;

-- 2. Table: users
IF OBJECT_ID(N'dbo.users', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.users (
        id INT IDENTITY(1,1) NOT NULL,
        fullname NVARCHAR(255) NULL,
        phone NVARCHAR(20) NULL,
        password NVARCHAR(255) NULL,
        birth_date DATE NULL,
        address NVARCHAR(255) NULL,
        avatar NVARCHAR(255) NULL,
        role_id INT NULL,
        created_at DATETIME NULL DEFAULT GETDATE(),
        email NVARCHAR(100) NULL,
        CONSTRAINT PK_users PRIMARY KEY (id),
        CONSTRAINT FK_users_roles FOREIGN KEY (role_id) REFERENCES dbo.roles (id)
    );
END;

-- 3. Table: doctors
IF OBJECT_ID(N'dbo.doctors', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.doctors (
        id INT IDENTITY(1,1) NOT NULL,
        user_id INT NOT NULL,
        specialization NVARCHAR(255) NOT NULL,
        experience_years INT NOT NULL DEFAULT 0,
        CONSTRAINT PK_doctors PRIMARY KEY (id),
        CONSTRAINT UQ_doctors_user_id UNIQUE (user_id),
        CONSTRAINT FK_doctors_users FOREIGN KEY (user_id) REFERENCES dbo.users (id) ON DELETE CASCADE
    );
END;

-- 4. Table: services
IF OBJECT_ID(N'dbo.services', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.services (
        id INT IDENTITY(1,1) NOT NULL,
        name NVARCHAR(255) NOT NULL,
        description NVARCHAR(MAX) NULL,
        price DECIMAL(18, 2) NOT NULL DEFAULT 0.00,
        image NVARCHAR(255) NULL,
        CONSTRAINT PK_services PRIMARY KEY (id)
    );
END;

-- 5. Table: treatment_routes
IF OBJECT_ID(N'dbo.treatment_routes', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.treatment_routes (
        id INT IDENTITY(1,1) NOT NULL,
        title NVARCHAR(255) NOT NULL,
        description NVARCHAR(MAX) NULL,
        created_at DATETIME NULL DEFAULT GETDATE(),
        CONSTRAINT PK_treatment_routes PRIMARY KEY (id)
    );
END;

-- 6. Table: treatment_stages
IF OBJECT_ID(N'dbo.treatment_stages', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.treatment_stages (
        id INT IDENTITY(1,1) NOT NULL,
        treatment_route_id INT NOT NULL,
        stage_name NVARCHAR(255) NOT NULL,
        sequence_order INT NOT NULL DEFAULT 1,
        status NVARCHAR(50) NULL DEFAULT 'PENDING',
        appointment_date DATETIME NULL,
        note NVARCHAR(MAX) NULL,
        created_at DATETIME NULL DEFAULT GETDATE(),
        updated_at DATETIME NULL DEFAULT GETDATE(),
        cost DECIMAL(18, 2) NULL DEFAULT 0.00,
        delay INT NULL DEFAULT 0,
        CONSTRAINT PK_treatment_stages PRIMARY KEY (id),
        CONSTRAINT FK_treatment_stages_routes FOREIGN KEY (treatment_route_id) REFERENCES dbo.treatment_routes (id) ON DELETE CASCADE
    );
END;

-- 7. Table: appointments
IF OBJECT_ID(N'dbo.appointments', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.appointments (
        id INT IDENTITY(1,1) NOT NULL,
        user_id INT NOT NULL,
        doctor_id INT NOT NULL,
        service_id INT NOT NULL,
        appointment_date DATETIME NOT NULL,
        status NVARCHAR(50) NULL DEFAULT 'PENDING',
        note NVARCHAR(MAX) NULL,
        created_at DATETIME NULL DEFAULT GETDATE(),
        stage_name NVARCHAR(255) NULL,
        CONSTRAINT PK_appointments PRIMARY KEY (id),
        CONSTRAINT FK_appointments_users FOREIGN KEY (user_id) REFERENCES dbo.users (id),
        CONSTRAINT FK_appointments_doctors FOREIGN KEY (doctor_id) REFERENCES dbo.doctors (id),
        CONSTRAINT FK_appointments_services FOREIGN KEY (service_id) REFERENCES dbo.services (id)
    );
END;

-- 8. Table: medical_records
IF OBJECT_ID(N'dbo.medical_records', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.medical_records (
        id INT IDENTITY(1,1) NOT NULL,
        user_id INT NOT NULL,
        doctor_id INT NOT NULL,
        service_id INT NOT NULL,
        treatment_route_id INT NULL,
        diagnosis NVARCHAR(MAX) NOT NULL,
        created_at DATETIME NULL DEFAULT GETDATE(),
        CONSTRAINT PK_medical_records PRIMARY KEY (id),
        CONSTRAINT FK_medical_records_users FOREIGN KEY (user_id) REFERENCES dbo.users (id),
        CONSTRAINT FK_medical_records_doctors FOREIGN KEY (doctor_id) REFERENCES dbo.doctors (id),
        CONSTRAINT FK_medical_records_services FOREIGN KEY (service_id) REFERENCES dbo.services (id),
        CONSTRAINT FK_medical_records_routes FOREIGN KEY (treatment_route_id) REFERENCES dbo.treatment_routes (id) ON DELETE SET NULL
    );
END;

-- 9. Table: payments
IF OBJECT_ID(N'dbo.payments', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.payments (
        id INT IDENTITY(1,1) NOT NULL,
        user_id INT NOT NULL,
        amount DECIMAL(18, 2) NOT NULL DEFAULT 0.00,
        method NVARCHAR(50) NOT NULL,
        status NVARCHAR(50) NULL DEFAULT 'PENDING',
        created_at DATETIME NULL DEFAULT GETDATE(),
        treatment_stage_id INT NULL,
        CONSTRAINT PK_payments PRIMARY KEY (id),
        CONSTRAINT FK_payments_users FOREIGN KEY (user_id) REFERENCES dbo.users (id),
        CONSTRAINT FK_payments_stages FOREIGN KEY (treatment_stage_id) REFERENCES dbo.treatment_stages (id) ON DELETE SET NULL
    );
END;
