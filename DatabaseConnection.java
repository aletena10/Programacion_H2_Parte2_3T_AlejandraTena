package cine;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// Clase para gestionar la conexión a la base de datos MySQL y las consultas
public class DatabaseConnection {
    // Constantes para la URL, usuario y contraseña de la base de datos
    private static final String URL = "jdbc:mysql://localhost:3306/cine_AlejandraTenaMuñoz";
    private static final String USER = "root";
    private static final String PASSWORD = "curso"; 

    // Obtiene una conexión a la base de datos
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // Obtiene todas las películas con sus géneros desde la base de datos
    public List<Pelicula> getPeliculas() throws SQLException {
        List<Pelicula> peliculas = new ArrayList<>();
        String query = "SELECT p.id_pelicula, p.titulo, p.director, p.anio, p.duracion_minutos, " +
                      "g.id_genero AS id_genero " + // Cambiado a id_genero en lugar de nombre_genero
                      "FROM peliculas p JOIN generos g ON p.id_genero = g.id_genero";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Pelicula pelicula = new Pelicula(
                    rs.getString("id_pelicula"),
                    rs.getString("titulo"),
                    rs.getString("director"),
                    rs.getInt("anio"),
                    rs.getInt("duracion_minutos"),
                    rs.getString("id_genero") // Cambiado a id_genero
                );
                peliculas.add(pelicula);
            }
        }
        return peliculas;
    }

    // Verifica si una película existe por su ID
    public boolean existePelicula(String idPelicula) throws SQLException {
        String query = "SELECT COUNT(*) FROM peliculas WHERE id_pelicula = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, idPelicula);
            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        }
    }

    // Obtiene el id_genero por su nombre, insensible a mayúsculas/minúsculas
    public String getIdGenero(String nombreGenero) throws SQLException {
        System.out.println("Recibido en getIdGenero: " + nombreGenero); // Depuración
        String query = "SELECT id_genero FROM generos WHERE LOWER(nombre) = LOWER(?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, nombreGenero);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("id_genero");
                }
                throw new SQLException("Género no encontrado: " + nombreGenero);
            }
        }
    }

    // Obtiene todos los géneros disponibles (sus nombres)
    public List<String> getGeneros() throws SQLException {
        List<String> generos = new ArrayList<>();
        String query = "SELECT nombre FROM generos";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                generos.add(rs.getString("nombre"));
            }
        }
        return generos;
    }

    // Añade una nueva película a la base de datos
    public boolean anadirPelicula(String idPelicula, String titulo, String director, int anio, int duracionMinutos, String nombreGenero) throws SQLException {
        if (existePelicula(idPelicula)) {
            return false; // Película ya existe
        }
        String idGenero = getIdGenero(nombreGenero); // Obtener el id_genero correspondiente
        String query = "INSERT INTO peliculas (id_pelicula, titulo, director, anio, duracion_minutos, id_genero) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, idPelicula);
            stmt.setString(2, titulo);
            stmt.setString(3, director);
            stmt.setInt(4, anio);
            stmt.setInt(5, duracionMinutos);
            stmt.setString(6, idGenero);
            stmt.executeUpdate();
            return true;
        }
    }

    // Elimina una película por su ID
    public boolean eliminarPelicula(String idPelicula) throws SQLException {
        if (!existePelicula(idPelicula)) {
            return false; // Película no existe
        }
        String query = "DELETE FROM peliculas WHERE id_pelicula = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, idPelicula);
            stmt.executeUpdate();
            return true;
        }
    }

    // Modifica el título y duración de una película por su ID
    public boolean modificarPelicula(String idPelicula, String nuevoTitulo, int nuevaDuracion) throws SQLException {
        if (!existePelicula(idPelicula)) {
            return false; // Película no existe
        }
        String query = "UPDATE peliculas SET titulo = ?, duracion_minutos = ? WHERE id_pelicula = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, nuevoTitulo);
            stmt.setInt(2, nuevaDuracion);
            stmt.setString(3, idPelicula);
            stmt.executeUpdate();
            return true;
        }
    }
}
