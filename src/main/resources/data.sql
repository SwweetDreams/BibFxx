-- Script de inicialización de datos para BiblFx
-- Este script se ejecuta automáticamente al iniciar la aplicación
-- Insertar usuarios de prueba
CREATE TABLE usuarios (
                          username VARCHAR(50) PRIMARY KEY,
                          password VARCHAR(255) NOT NULL,
                          nombre_completo VARCHAR(100) NOT NULL,
                          email VARCHAR(100) NOT NULL,
                          rol VARCHAR(50) NOT NULL,
                          activo BOOLEAN NOT NULL,
                          fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                        );

CREATE TABLE libros (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        titulo VARCHAR(255) NOT NULL,
                        autor VARCHAR(100) NOT NULL,
                        isbn VARCHAR(20),
                        editorial VARCHAR(100),
                        anio_publicacion INT,
                        categoria VARCHAR(50),
                        cantidad_total INT NOT NULL,
                        cantidad_disponible INT NOT NULL,
                        descripcion TEXT,
                        activo BOOLEAN NOT NULL,
                        fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                        );
CREATE TABLE prestamos (
                           id INT AUTO_INCREMENT PRIMARY KEY,
                           usuario_id VARCHAR(50) NOT NULL,
                           libro_id INT NOT NULL,
                           fecha_prestamo TIMESTAMP NOT NULL,
                           fecha_devolucion_esperada TIMESTAMP NOT NULL,
                           fecha_devolucion_real TIMESTAMP,
                           estado VARCHAR(50) NOT NULL,
                           observaciones TEXT,
                           FOREIGN KEY (usuario_id) REFERENCES usuarios(username),
                           FOREIGN KEY (libro_id) REFERENCES libros(id)
);
INSERT INTO usuarios (username, password, nombre_completo, email, rol, activo, fecha_creacion) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'Administrador del Sistema', 'admin@biblioteca.com', 'ADMINISTRADOR', true, CURRENT_TIMESTAMP),
('bibliotecario1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'María González', 'maria@biblioteca.com', 'BIBLIOTECARIO', true, CURRENT_TIMESTAMP),
('bibliotecario2', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'Carlos Rodríguez', 'carlos@biblioteca.com', 'BIBLIOTECARIO', true, CURRENT_TIMESTAMP),
('lector1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'Ana Martínez', 'ana@email.com', 'LECTOR', true, CURRENT_TIMESTAMP),
('lector2', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'Luis Pérez', 'luis@email.com', 'LECTOR', true, CURRENT_TIMESTAMP),
('lector3', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'Carmen López', 'carmen@email.com', 'LECTOR', true, CURRENT_TIMESTAMP);

-- Insertar libros de prueba
INSERT INTO libros (titulo, autor, isbn, editorial, anio_publicacion, categoria, cantidad_total, cantidad_disponible, descripcion, activo, fecha_creacion) VALUES
('El Quijote', 'Miguel de Cervantes', '978-84-376-0494-7', 'Cátedra', 1605, 'LITERATURA_CLASICA', 5, 5, 'Obra maestra de la literatura española', true, CURRENT_TIMESTAMP),
('Cien años de soledad', 'Gabriel García Márquez', '978-84-397-2077-5', 'Alfaguara', 1967, 'LITERATURA_CONTEMPORANEA', 3, 3, 'Novela del realismo mágico', true, CURRENT_TIMESTAMP),
('Don Juan Tenorio', 'José Zorrilla', '978-84-376-0494-8', 'Cátedra', 1844, 'TEATRO', 4, 4, 'Drama romántico español', true, CURRENT_TIMESTAMP),
('La Celestina', 'Fernando de Rojas', '978-84-376-0494-9', 'Cátedra', 1499, 'LITERATURA_CLASICA', 2, 2, 'Tragicomedia de Calisto y Melibea', true, CURRENT_TIMESTAMP),
('Pedro Páramo', 'Juan Rulfo', '978-84-397-2077-6', 'Alfaguara', 1955, 'LITERATURA_CONTEMPORANEA', 3, 3, 'Novela mexicana del siglo XX', true, CURRENT_TIMESTAMP),
('El Aleph', 'Jorge Luis Borges', '978-84-397-2077-7', 'Alfaguara', 1949, 'LITERATURA_CONTEMPORANEA', 4, 4, 'Colección de cuentos fantásticos', true, CURRENT_TIMESTAMP),
('Rayuela', 'Julio Cortázar', '978-84-397-2077-8', 'Alfaguara', 1963, 'LITERATURA_CONTEMPORANEA', 2, 2, 'Novela experimental argentina', true, CURRENT_TIMESTAMP),
('Los miserables', 'Victor Hugo', '978-84-376-0495-0', 'Cátedra', 1862, 'LITERATURA_CLASICA', 3, 3, 'Novela histórica francesa', true, CURRENT_TIMESTAMP),
('Madame Bovary', 'Gustave Flaubert', '978-84-376-0495-1', 'Cátedra', 1857, 'LITERATURA_CLASICA', 2, 2, 'Novela realista francesa', true, CURRENT_TIMESTAMP),
('El retrato de Dorian Gray', 'Oscar Wilde', '978-84-376-0495-2', 'Cátedra', 1890, 'LITERATURA_CLASICA', 3, 3, 'Novela gótica británica', true, CURRENT_TIMESTAMP),
('Orgullo y prejuicio', 'Jane Austen', '978-84-376-0495-3', 'Cátedra', 1813, 'LITERATURA_CLASICA', 4, 4, 'Novela romántica británica', true, CURRENT_TIMESTAMP),
('Crimen y castigo', 'Fiódor Dostoyevski', '978-84-376-0495-4', 'Cátedra', 1866, 'LITERATURA_CLASICA', 2, 2, 'Novela psicológica rusa', true, CURRENT_TIMESTAMP),
('El señor de los anillos', 'J.R.R. Tolkien', '978-84-397-2077-9', 'Minotauro', 1954, 'FICCION', 5, 5, 'Trilogía épica de fantasía', true, CURRENT_TIMESTAMP),
('Harry Potter y la piedra filosofal', 'J.K. Rowling', '978-84-397-2078-0', 'Salamandra', 1997, 'FICCION', 6, 6, 'Primera novela de la saga de Harry Potter', true, CURRENT_TIMESTAMP),
('El código Da Vinci', 'Dan Brown', '978-84-397-2078-1', 'Umbriel', 2003, 'FICCION', 4, 4, 'Thriller de misterio', true, CURRENT_TIMESTAMP),
('Los pilares de la tierra', 'Ken Follett', '978-84-397-2078-2', 'Plaza & Janés', 1989, 'FICCION', 3, 3, 'Novela histórica medieval', true, CURRENT_TIMESTAMP),
('El nombre del viento', 'Patrick Rothfuss', '978-84-397-2078-3', 'Plaza & Janés', 2007, 'FICCION', 2, 2, 'Primera parte de la Crónica del asesino de reyes', true, CURRENT_TIMESTAMP),
('Dune', 'Frank Herbert', '978-84-397-2078-4', 'Plaza & Janés', 1965, 'CIENCIA_FICCION', 3, 3, 'Clásico de la ciencia ficción', true, CURRENT_TIMESTAMP),
('1984', 'George Orwell', '978-84-376-0495-5', 'Cátedra', 1949, 'CIENCIA_FICCION', 4, 4, 'Novela distópica', true, CURRENT_TIMESTAMP),
('Fahrenheit 451', 'Ray Bradbury', '978-84-376-0495-6', 'Cátedra', 1953, 'CIENCIA_FICCION', 3, 3, 'Novela distópica sobre la censura', true, CURRENT_TIMESTAMP),
('El hobbit', 'J.R.R. Tolkien', '978-84-397-2078-5', 'Minotauro', 1937, 'FICCION', 4, 4, 'Precuela de El señor de los anillos', true, CURRENT_TIMESTAMP);

-- Insertar algunos préstamos de prueba
INSERT INTO prestamos (usuario_id, libro_id, fecha_prestamo, fecha_devolucion_esperada, fecha_devolucion_real, estado, observaciones) VALUES
(4, 1, CURRENT_TIMESTAMP - INTERVAL 5 DAY , CURRENT_TIMESTAMP + INTERVAL 2 DAY, NULL, 'ACTIVO', 'Préstamo para investigación'),
(5, 2, CURRENT_TIMESTAMP - INTERVAL 3 DAY, CURRENT_TIMESTAMP + INTERVAL 4 DAY, NULL, 'ACTIVO', 'Lectura personal'),
(6, 3, CURRENT_TIMESTAMP - INTERVAL 10 DAY, CURRENT_TIMESTAMP - INTERVAL 3 DAY, CURRENT_TIMESTAMP - INTERVAL 2 DAY, 'DEVUELTO', 'Devolución a tiempo'),
(4, 4, CURRENT_TIMESTAMP - INTERVAL 15 DAY, CURRENT_TIMESTAMP - INTERVAL 8 DAY, CURRENT_TIMESTAMP - INTERVAL 5 DAY, 'DEVUELTO', 'Devolución tardía'),
(5, 5, CURRENT_TIMESTAMP - INTERVAL 20 DAY, CURRENT_TIMESTAMP - INTERVAL 13 DAY, NULL, 'CANCELADO', 'Préstamo cancelado por el usuario');