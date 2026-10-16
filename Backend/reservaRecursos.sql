-- ============================================================
-- Sistema de Reserva de Recursos (EIF206) - base de datos MySQL
-- Genera el esquema y migra los datos que estaban en data.xml
-- Ejecutar completo en MySQL Workbench (rayo o Ctrl+Shift+Enter)
-- ============================================================

DROP DATABASE IF EXISTS ReservaRecursos;
CREATE DATABASE ReservaRecursos CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE ReservaRecursos;

CREATE TABLE Administrador (
    id    VARCHAR(20) NOT NULL,
    clave VARCHAR(50) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE Funcionario (
    id       VARCHAR(20)  NOT NULL,
    clave    VARCHAR(50)  NOT NULL,
    nombre   VARCHAR(80)  NOT NULL,
    telefono VARCHAR(20),
    PRIMARY KEY (id)
);

CREATE TABLE CategoriaRecurso (
    id          VARCHAR(10) NOT NULL,
    descripcion VARCHAR(80) NOT NULL,
    PRIMARY KEY (id)
);

-- Borrar una categoria borra en cascada sus recursos
-- (igual que reservationSystem.logic.Service.eliminarCategoria con XML)
CREATE TABLE Recurso (
    id          VARCHAR(10) NOT NULL,
    descripcion VARCHAR(80) NOT NULL,
    idCategoria VARCHAR(10) NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (idCategoria) REFERENCES CategoriaRecurso(id) ON DELETE CASCADE
);

-- estado es VARCHAR (no ENUM): el data.xml trae ACTIVA, CANCELADA y FINALIZADA
CREATE TABLE Reserva (
    id            VARCHAR(10)  NOT NULL,
    actividad     VARCHAR(100) NOT NULL,
    idFuncionario VARCHAR(20)  NOT NULL,
    fecha         DATE         NOT NULL,
    horaInicio    TIME         NOT NULL,
    horaFin       TIME         NOT NULL,
    estado        VARCHAR(15)  NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (idFuncionario) REFERENCES Funcionario(id)
);

-- Un detalle por categoria solicitada. Si luego se borra el recurso o la
-- categoria, el detalle se conserva con NULL (historial de reservas canceladas).
CREATE TABLE DetalleReserva (
    id                   INT         NOT NULL AUTO_INCREMENT,
    idReserva            VARCHAR(10) NOT NULL,
    idCategoriaSolicitada VARCHAR(10),
    idRecursoAsignado    VARCHAR(10),
    PRIMARY KEY (id),
    FOREIGN KEY (idReserva) REFERENCES Reserva(id) ON DELETE CASCADE,
    FOREIGN KEY (idCategoriaSolicitada) REFERENCES CategoriaRecurso(id) ON DELETE SET NULL,
    FOREIGN KEY (idRecursoAsignado) REFERENCES Recurso(id) ON DELETE SET NULL
);

-- ---------- Datos migrados desde data.xml ----------

INSERT INTO Administrador (id, clave) VALUES ('admin', 'admin');

INSERT INTO Funcionario (id, clave, nombre, telefono) VALUES ('f003', 'f003', 'Carlos Jiménez', '8777-1122');
INSERT INTO Funcionario (id, clave, nombre, telefono) VALUES ('f004', 'f004', 'Ana Solano', '8777-3344');
INSERT INTO Funcionario (id, clave, nombre, telefono) VALUES ('f005', 'f005', 'Luis Vargas', '8666-5566');
INSERT INTO Funcionario (id, clave, nombre, telefono) VALUES ('f006', 'f006', 'Paola Castro', '8666-7788');
INSERT INTO Funcionario (id, clave, nombre, telefono) VALUES ('f002', 'f002', 'María Rodríguez', '8688-9234');
INSERT INTO Funcionario (id, clave, nombre, telefono) VALUES ('f001', 'f001', 'Juan Pérez', '4704-4739');

INSERT INTO CategoriaRecurso (id, descripcion) VALUES ('CAT001', 'Proyectores');
INSERT INTO CategoriaRecurso (id, descripcion) VALUES ('CAT002', 'Salas de reunión');
INSERT INTO CategoriaRecurso (id, descripcion) VALUES ('CAT003', 'Equipo de cómputo');
INSERT INTO CategoriaRecurso (id, descripcion) VALUES ('CAT004', 'Impresoras y escáneres');
INSERT INTO CategoriaRecurso (id, descripcion) VALUES ('CAT005', 'Equipo audiovisual');
INSERT INTO CategoriaRecurso (id, descripcion) VALUES ('CAT006', 'Redes y conectividad');

INSERT INTO Recurso (id, descripcion, idCategoria) VALUES ('REC001', 'Proyector Epson 1', 'CAT001');
INSERT INTO Recurso (id, descripcion, idCategoria) VALUES ('REC002', 'Proyector Epson 2', 'CAT001');
INSERT INTO Recurso (id, descripcion, idCategoria) VALUES ('REC003', 'Proyector BenQ 1', 'CAT001');
INSERT INTO Recurso (id, descripcion, idCategoria) VALUES ('REC004', 'Sala A', 'CAT002');
INSERT INTO Recurso (id, descripcion, idCategoria) VALUES ('REC005', 'Sala B', 'CAT002');
INSERT INTO Recurso (id, descripcion, idCategoria) VALUES ('REC006', 'Sala C - Auditorio', 'CAT002');
INSERT INTO Recurso (id, descripcion, idCategoria) VALUES ('REC007', 'Laptop Dell Latitude 1', 'CAT003');
INSERT INTO Recurso (id, descripcion, idCategoria) VALUES ('REC008', 'Laptop Dell Latitude 2', 'CAT003');
INSERT INTO Recurso (id, descripcion, idCategoria) VALUES ('REC009', 'Tablet Samsung 1', 'CAT003');
INSERT INTO Recurso (id, descripcion, idCategoria) VALUES ('REC010', 'Impresora HP LaserJet 1', 'CAT004');
INSERT INTO Recurso (id, descripcion, idCategoria) VALUES ('REC011', 'Impresora Epson EcoTank 1', 'CAT004');
INSERT INTO Recurso (id, descripcion, idCategoria) VALUES ('REC012', 'Escáner Canon DR-C225', 'CAT004');
INSERT INTO Recurso (id, descripcion, idCategoria) VALUES ('REC013', 'Micrófono inalámbrico 1', 'CAT005');
INSERT INTO Recurso (id, descripcion, idCategoria) VALUES ('REC014', 'Parlante portátil JBL', 'CAT005');
INSERT INTO Recurso (id, descripcion, idCategoria) VALUES ('REC015', 'Cámara web Logitech 1', 'CAT005');
INSERT INTO Recurso (id, descripcion, idCategoria) VALUES ('REC016', 'Router WiFi portátil 1', 'CAT006');
INSERT INTO Recurso (id, descripcion, idCategoria) VALUES ('REC017', 'Módem 4G/5G portátil', 'CAT006');
INSERT INTO Recurso (id, descripcion, idCategoria) VALUES ('REC018', 'Sala de 10 personas', 'CAT002');

INSERT INTO Reserva (id, actividad, idFuncionario, fecha, horaInicio, horaFin, estado) VALUES ('R1', 'Reunion de planificación', 'f001', '2026-09-23', '08:00:00', '10:00:00', 'ACTIVA');
INSERT INTO Reserva (id, actividad, idFuncionario, fecha, horaInicio, horaFin, estado) VALUES ('R2', 'Capacitación de personal', 'f002', '2026-09-24', '09:00:00', '12:00:00', 'ACTIVA');
INSERT INTO Reserva (id, actividad, idFuncionario, fecha, horaInicio, horaFin, estado) VALUES ('R3', 'Entrevista a proveedor', 'f003', '2026-09-25', '14:00:00', '15:30:00', 'ACTIVA');
INSERT INTO Reserva (id, actividad, idFuncionario, fecha, horaInicio, horaFin, estado) VALUES ('R4', 'Impresión de material para evento', 'f004', '2026-09-26', '07:30:00', '09:00:00', 'ACTIVA');
INSERT INTO Reserva (id, actividad, idFuncionario, fecha, horaInicio, horaFin, estado) VALUES ('R5', 'Presentación de resultados', 'f005', '2026-09-27', '10:00:00', '11:30:00', 'ACTIVA');
INSERT INTO Reserva (id, actividad, idFuncionario, fecha, horaInicio, horaFin, estado) VALUES ('R6', 'Mantenimiento de equipo', 'f006', '2026-09-20', '08:00:00', '09:00:00', 'FINALIZADA');
INSERT INTO Reserva (id, actividad, idFuncionario, fecha, horaInicio, horaFin, estado) VALUES ('R7', 'Taller de innovación', 'f001', '2026-09-30', '13:00:00', '17:00:00', 'ACTIVA');
INSERT INTO Reserva (id, actividad, idFuncionario, fecha, horaInicio, horaFin, estado) VALUES ('R8', 'Reunión con cliente externo', 'f002', '2026-09-18', '15:00:00', '16:00:00', 'CANCELADA');
INSERT INTO Reserva (id, actividad, idFuncionario, fecha, horaInicio, horaFin, estado) VALUES ('R9', 'Digitalización de expedientes', 'f003', '2026-09-22', '08:30:00', '10:30:00', 'ACTIVA');
INSERT INTO Reserva (id, actividad, idFuncionario, fecha, horaInicio, horaFin, estado) VALUES ('R10', 'Videoconferencia con casa matriz', 'f005', '2026-09-29', '16:00:00', '17:00:00', 'ACTIVA');

INSERT INTO DetalleReserva (idReserva, idCategoriaSolicitada, idRecursoAsignado) VALUES ('R1', 'CAT001', 'REC001');
INSERT INTO DetalleReserva (idReserva, idCategoriaSolicitada, idRecursoAsignado) VALUES ('R1', 'CAT002', 'REC004');
INSERT INTO DetalleReserva (idReserva, idCategoriaSolicitada, idRecursoAsignado) VALUES ('R2', 'CAT002', 'REC006');
INSERT INTO DetalleReserva (idReserva, idCategoriaSolicitada, idRecursoAsignado) VALUES ('R2', 'CAT005', 'REC013');
INSERT INTO DetalleReserva (idReserva, idCategoriaSolicitada, idRecursoAsignado) VALUES ('R3', 'CAT002', 'REC005');
INSERT INTO DetalleReserva (idReserva, idCategoriaSolicitada, idRecursoAsignado) VALUES ('R4', 'CAT004', 'REC010');
INSERT INTO DetalleReserva (idReserva, idCategoriaSolicitada, idRecursoAsignado) VALUES ('R5', 'CAT001', 'REC002');
INSERT INTO DetalleReserva (idReserva, idCategoriaSolicitada, idRecursoAsignado) VALUES ('R5', 'CAT002', 'REC004');
INSERT INTO DetalleReserva (idReserva, idCategoriaSolicitada, idRecursoAsignado) VALUES ('R6', 'CAT003', 'REC007');
INSERT INTO DetalleReserva (idReserva, idCategoriaSolicitada, idRecursoAsignado) VALUES ('R7', 'CAT002', 'REC006');
INSERT INTO DetalleReserva (idReserva, idCategoriaSolicitada, idRecursoAsignado) VALUES ('R7', 'CAT003', 'REC009');
INSERT INTO DetalleReserva (idReserva, idCategoriaSolicitada, idRecursoAsignado) VALUES ('R7', 'CAT006', 'REC016');
INSERT INTO DetalleReserva (idReserva, idCategoriaSolicitada, idRecursoAsignado) VALUES ('R8', 'CAT002', 'REC005');
INSERT INTO DetalleReserva (idReserva, idCategoriaSolicitada, idRecursoAsignado) VALUES ('R9', 'CAT004', 'REC012');
INSERT INTO DetalleReserva (idReserva, idCategoriaSolicitada, idRecursoAsignado) VALUES ('R10', 'CAT002', 'REC004');
INSERT INTO DetalleReserva (idReserva, idCategoriaSolicitada, idRecursoAsignado) VALUES ('R10', 'CAT005', 'REC015');
INSERT INTO DetalleReserva (idReserva, idCategoriaSolicitada, idRecursoAsignado) VALUES ('R10', 'CAT006', 'REC017');

-- Verificacion rapida
SELECT (SELECT COUNT(*) FROM Funcionario) AS funcionarios, (SELECT COUNT(*) FROM CategoriaRecurso) AS categorias, (SELECT COUNT(*) FROM Recurso) AS recursos, (SELECT COUNT(*) FROM Reserva) AS reservas, (SELECT COUNT(*) FROM DetalleReserva) AS detalles;
