-- Eliminar la base de datos si existe para evitar duplicados
DROP DATABASE IF EXISTS cine_AlejandraTenaMuñoz;

-- Crear la base de datos
CREATE DATABASE cine_AlejandraTenaMuñoz;
USE cine_AlejandraTenaMuñoz;

-- Crear tabla generos
CREATE TABLE generos (
    id_genero VARCHAR(3) PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL
);

-- Crear tabla peliculas
CREATE TABLE peliculas (
    id_pelicula VARCHAR(10) PRIMARY KEY,
    titulo VARCHAR(100) NOT NULL,
    director VARCHAR(100) NOT NULL,
    anio INT NOT NULL,
    duracion_minutos INT NOT NULL,
    id_genero VARCHAR(3),
    FOREIGN KEY (id_genero) REFERENCES generos(id_genero)
);

-- Insertar datos en generos
INSERT INTO generos (id_genero, nombre) VALUES
    ('DRA', 'Drama'),
    ('COM', 'Comedia'),
    ('SUS', 'Suspense'),
    ('SOC', 'Social');

-- Insertar datos en peliculas (películas españolas conocidas)
INSERT INTO peliculas (id_pelicula, titulo, director, anio, duracion_minutos, id_genero) VALUES
    ('PEL001', 'El Laberinto del Fauno', 'Guillermo del Toro', 2006, 118, 'DRA'),
    ('PEL002', 'Volver', 'Pedro Almodóvar', 2006, 121, 'DRA'),
    ('PEL003', 'Celda 211', 'Daniel Monzón', 2009, 113, 'SUS'),
    ('PEL004', 'Ocho Apellidos Vascos', 'Emilio Martínez-Lázaro', 2014, 98, 'COM'),
    ('PEL005', 'Campeones', 'Javier Fesser', 2018, 124, 'COM');

-- Verificar los datos
USE cine_AlejandraTenaMuñoz;
SELECT * FROM generos;
SELECT * FROM peliculas;
