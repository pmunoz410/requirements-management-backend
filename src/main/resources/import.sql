-- ===============================
-- USUARIOS BASE DEL SISTEMA
-- ===============================
INSERT INTO users (first_name, last_name, username, email, password, phone, rol, active, created_at) VALUES ('Super', 'Admin1', 'superadmin1', 'superadmin1@empresa.local','$2a$10$VrFNZQiXNPChIJCZU2qbR.fTrEdFXKmENNiC1Fh77uCJv8hAL6N02', '999999999', 'SUPER_ADMIN', true, NOW());
INSERT INTO users (first_name, last_name, username, email, password, phone, rol, active, created_at) VALUES ('Super', 'Admin2', 'superadmin2', 'superadmin2@empresa.local', '$2a$10$I0vZoL1f/w9umdkkZhIUme8GX5.HvZU6vcLLSUkjMuj6ORYIG.2zW', '999999998', 'SUPER_ADMIN', true, NOW());
INSERT INTO users (first_name, last_name, username, email, password, phone, rol, active, created_at) VALUES ('Juan', 'Perez', 'admin1', 'juan.perez@empresa.local', '$2a$10$Sf3v37mxnrnR3T0i.2NgFeZ3GPDBsXKZ0lk0aZse8rFjO5Bm4DDFS', '988888888', 'ADMIN', true, NOW());
INSERT INTO users (first_name, last_name, username, email, password, phone, rol, active, created_at) VALUES ('Maria', 'Gomez', 'admin2', 'maria.gomez@empresa.local', '$2a$10$FTfNHM3zDNtyZCU5guyMquOrRnJiTWCRUwOw3eYxefRzwfV6IWtdi', '988888887', 'ADMIN', true, NOW());
INSERT INTO users (first_name, last_name, username, email, password, phone, rol, active, created_at) VALUES ('Luis', 'Ramirez', 'admin3', 'luis.ramirez@empresa.local', '$2a$10$40xzk2pC/Jsla2D0.zC7auCgd1paJtvhF4G5oosgwxV3o.CCfZsfm', '988888886', 'ADMIN', true, NOW());
INSERT INTO users (first_name, last_name, username, email, password, phone, rol, active, created_at) VALUES ('Carlos', 'Lopez', 'user1', 'carlos.lopez@empresa.local', '$2a$10$NVAZ.LMxexa7vW/2ZXvqXu5XbhlcSHvGuqNuQ2wDGCcgzVrh7A1Ae', '977777777', 'USUARIO', true, NOW());
INSERT INTO users (first_name, last_name, username, email, password, phone, rol, active, created_at) VALUES ('Ana', 'Torres', 'user2', 'ana.torres@empresa.local', '$2a$10$81ecq2wa8cm5S3MvlkxPvuSpDeJAHykTU1xILSsQ.RbscRLa0WZ5G', '977777776', 'USUARIO', true, NOW());
INSERT INTO users (first_name, last_name, username, email, password, phone, rol, active, created_at) VALUES ('Pedro', 'Castillo', 'user3', 'pedro.castillo@empresa.local', '$2a$10$MrgQkf/ab0aLH99NGbL0jOiw26EDE1ZVNYtUVG5Q/XKID0nnS7/ee', '977777775', 'USUARIO', true, NOW());
INSERT INTO users (first_name, last_name, username, email, password, phone, rol, active, created_at) VALUES ('Lucia', 'Fernandez', 'user4', 'lucia.fernandez@empresa.local', '$2a$10$YR4.f3GGWEdrCdk60WQVs.jUvPNJMajG91ogAvvEWSqLql/3dSftG', '977777774', 'USUARIO', true, NOW());
INSERT INTO users (first_name, last_name, username, email, password, phone, rol, active, created_at) VALUES ('Diego', 'Mendoza', 'user5', 'diego.mendoza@empresa.local', '$2a$10$CzmrZPoattwRWxbKwHIby.ntrnLljUOd//HQOCtgEkOjYuZoQxvLe', '977777773', 'USUARIO', true, NOW());
INSERT INTO users (first_name, last_name, username, email, password, phone, rol, active, created_at) VALUES ('Sofia', 'Rojas', 'user6', 'sofia.rojas@empresa.local', '$2a$10$bb5eJ1ORB./9vsMMPJJ7bOk.jfdW.XYwO.bbrIm1h4WI6ZRhrS.FK', '977777772', 'USUARIO', true, NOW());
INSERT INTO users (first_name, last_name, username, email, password, phone, rol, active, created_at) VALUES ('Miguel', 'Vargas', 'user7', 'miguel.vargas@empresa.local', '$2a$10$JHXAhK2na6Ss9Sdhcf73CODCsomrV8c7W8072opvU9vxyL5mYubwK', '977777771', 'USUARIO', true, NOW());

-- ============================
-- ORGANIZATIONAL AREAS
-- ============================
INSERT INTO organizational_areas (name, active, created_at, created_by_id) VALUES ('Sistemas', true, NOW(), 1);
INSERT INTO organizational_areas (name, active, created_at, created_by_id) VALUES ('Finanzas', true, NOW(), 1);
INSERT INTO organizational_areas (name, active, created_at, created_by_id) VALUES ('Recursos Humanos', true, NOW(), 2);
INSERT INTO organizational_areas (name, active, created_at, created_by_id) VALUES ('Operaciones', true, NOW(), 2);
INSERT INTO organizational_areas (name, active, created_at, created_by_id) VALUES ('Marketing', true, NOW(), 2);

-- ============================
-- USERS ↔ ORGANIZATIONAL AREAS
-- ============================

-- SISTEMAS (area_id = 1) - admin1 (Juan Perez)
INSERT INTO area_administrators (user_id, area_id) VALUES (3, 1);

-- FINANZAS (area_id = 2) - admin1 (Juan Perez)
INSERT INTO area_administrators (user_id, area_id) VALUES (3, 2);

-- RRHH (area_id = 3) - admin2 (Maria Gomez)
INSERT INTO area_administrators (user_id, area_id) VALUES (4, 3);

-- OPERACIONES (area_id = 4) - admin2 (Maria Gomez)
INSERT INTO area_administrators (user_id, area_id) VALUES (4, 4);

-- MARKETING (area_id = 5) - admin3 (Luis Ramirez)
INSERT INTO area_administrators (user_id, area_id) VALUES (5, 5);

-- ============================
-- WORKFLOWS
-- ============================

-- SISTEMAS (area_id = 1) - admin1 (Juan Perez)
INSERT INTO workflows (name, area_id, admin_id, active, created_at) VALUES ('Workflow Sistemas A', 1, 3, true, NOW());

-- FINANZAS (area_id = 2) - admin1 (Juan Perez)
INSERT INTO workflows (name, area_id, admin_id, active, created_at) VALUES ('Workflow Finanzas A', 2, 3, true, NOW());

-- RRHH (area_id = 3) - admin2 (Maria Gomez)
INSERT INTO workflows (name, area_id, admin_id, active, created_at) VALUES ('Workflow RRHH A', 3, 4, true, NOW());

-- OPERACIONES (area_id = 4) - admin2 (Maria Gomez)
INSERT INTO workflows (name, area_id, admin_id, active, created_at) VALUES ('Workflow Operaciones A', 4, 4, true, NOW());

-- MARKETING (area_id = 5) - admin3 (Luis Ramirez)
INSERT INTO workflows (name, area_id, admin_id, active, created_at) VALUES ('Workflow Marketing A', 5, 5, true, NOW());

-- ============================
-- USERS ↔ WORKFLOWS
-- ============================

-- Workflow Sistemas A (id = 1) - admin1 (Juan Perez)
INSERT INTO workflow_users (user_id, workflow_id) VALUES (6, 1);
INSERT INTO workflow_users (user_id, workflow_id) VALUES (7, 1);

-- Workflow Finanzas A (id = 2) - admin1 (Juan Perez)
INSERT INTO workflow_users (user_id, workflow_id) VALUES (8, 2);

-- Workflow RRHH A (id = 3) - admin2 (Maria Gomez)
INSERT INTO workflow_users (user_id, workflow_id) VALUES (9, 3);

-- Workflow Operaciones A (id = 4) - admin2 (Maria Gomez)
INSERT INTO workflow_users (user_id, workflow_id) VALUES (10, 4);

-- Workflow Marketing A (id = 5) - admin3 (Luis Ramirez)
INSERT INTO workflow_users (user_id, workflow_id) VALUES (11, 5);
INSERT INTO workflow_users (user_id, workflow_id) VALUES (12, 5);

-- ============================
-- REQUIREMENTS
-- ============================

-- Req 1 | Workflow: Sistemas A | Admin: admin1 (id 3) | Asignado a: user1 (id 6)
INSERT INTO requirements (title, description, status, priority, classification, workflow_id, assignee_id, active, created_at) VALUES ('Error login', 'El login falla con credenciales válidas', 'EN_DESARROLLO', 'ALTA', 'ERROR', 1, 6, true, NOW());

-- Req 2 | Workflow: Finanzas A | Admin: admin1 (id 3) | Asignado a: user2 (id 7)
INSERT INTO requirements (title, description, status, priority, classification, workflow_id, assignee_id, active, created_at) VALUES ('Mejora dashboard', 'Agregar KPIs financieros', 'EN_PRUEBAS', 'MEDIA', 'MEJORA', 2, 7, true, NOW());

-- Req 3 | Workflow: RRHH A | Admin: admin2 (id 4) | Asignado a: user3 (id 8)
INSERT INTO requirements (title, description, status, priority, classification, workflow_id, assignee_id, active, created_at) VALUES ('Bug exportación', 'No exporta PDF en reportes', 'EN_REVISION', 'ALTA', 'ERROR', 3, 8, true, NOW());

-- Req 4 | Workflow: Operaciones A | Admin: admin2 (id 4) | Asignado a: user4 (id 9)
INSERT INTO requirements (title, description, status, priority, classification, workflow_id, assignee_id, active, created_at) VALUES ('Flujo vacaciones', 'Revisar flujo de aprobación', 'REQUIERE_CAMBIOS', 'MEDIA', 'CAMBIO', 4, 9, true, NOW());

-- Req 5 | Workflow: Marketing A | Admin: admin3 (id 5) | Asignado a: user5 (id 10)
INSERT INTO requirements (title, description, status, priority, classification, workflow_id, assignee_id, active, created_at) VALUES ('Documentación campañas', 'Documentar flujo de campañas', 'FINALIZADO', 'BAJA', 'DOCUMENTACION', 5, 10, true, NOW());

-- ============================
-- COMMENTS
-- ============================

-- REQUIREMENT 1
-- Usuario asignado comenta avance
INSERT INTO comments (content, type, user_id, requirement_id, created_at) VALUES ('Identifiqué el error en la validación del token', 'PUBLIC', 6, 1, NOW());

-- Admin responde públicamente
INSERT INTO comments (content, type, user_id, requirement_id, created_at) VALUES ('Perfecto, continúa con la corrección y avanza a pruebas', 'PUBLIC', 3, 1, NOW());

-- Admin deja nota interna
INSERT INTO comments (content, type, user_id, requirement_id, created_at) VALUES ('Impacta a todos los usuarios, prioridad alta', 'INTERNAL', 3, 1, NOW());

-- SuperAdmin refuerza decisión
INSERT INTO comments (content, type, user_id, requirement_id, created_at) VALUES ('Debe quedar listo antes del cierre semanal', 'INTERNAL', 1, 1, NOW());

-- REQUIREMENT 2
-- Usuario avisa que terminó desarrollo
INSERT INTO comments (content, type, user_id, requirement_id, created_at) VALUES ('Dashboard listo, KPIs agregados', 'PUBLIC', 7, 2, NOW());

-- Admin pasa a pruebas
INSERT INTO comments (content, type, user_id, requirement_id, created_at) VALUES ('Recibido, validando métricas en ambiente de pruebas', 'PUBLIC', 3, 2, NOW());

-- Nota interna admin
INSERT INTO comments (content, type, user_id, requirement_id, created_at) VALUES ('Revisar rendimiento con datos grandes', 'INTERNAL', 3, 2, NOW());

-- REQUIREMENT 3
-- Usuario indica que terminó pruebas
INSERT INTO comments (content, type, user_id, requirement_id, created_at) VALUES ('Exportación PDF ya funciona correctamente', 'PUBLIC', 8, 3, NOW());

-- Admin revisa
INSERT INTO comments (content, type, user_id, requirement_id, created_at) VALUES ('Estoy revisando en entorno de revisión', 'PUBLIC', 4, 3, NOW());

-- SuperAdmin comentario interno
INSERT INTO comments (content, type, user_id, requirement_id, created_at) VALUES ('Validar compatibilidad con navegadores antiguos', 'INTERNAL', 2, 3, NOW());

-- REQUIREMENT 4
-- Admin rechaza y solicita cambios (OBLIGATORIO comentario)
INSERT INTO comments (content, type, user_id, requirement_id, created_at) VALUES ('El flujo no contempla aprobación de RRHH', 'PUBLIC', 4, 4, NOW());

-- Usuario responde
INSERT INTO comments (content, type, user_id, requirement_id, created_at) VALUES ('Entendido, agrego etapa de aprobación adicional', 'PUBLIC', 9, 4, NOW());

-- Nota interna superadmin
INSERT INTO comments (content, type, user_id, requirement_id, created_at) VALUES ('Cambio impacta política interna', 'INTERNAL', 1, 4, NOW());

-- REQUIREMENT 5
-- Usuario entrega documentación
INSERT INTO comments (content, type, user_id, requirement_id, created_at) VALUES ('Documentación subida al repositorio', 'PUBLIC', 10, 5, NOW());

-- Admin cierra
INSERT INTO comments (content, type, user_id, requirement_id, created_at) VALUES ('Revisado y aprobado, se da por finalizado', 'PUBLIC', 5, 5, NOW());

-- SuperAdmin nota interna
INSERT INTO comments (content, type, user_id, requirement_id, created_at) VALUES ('Buen estándar de documentación', 'INTERNAL', 2, 5, NOW());
