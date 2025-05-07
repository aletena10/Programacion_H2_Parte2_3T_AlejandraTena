package cine;

// Clase que representa una película en el cine
public class Pelicula {
    // Atributos privados de la película
    private String idPelicula;
    private String titulo;
    private String director;
    private int anio;
    private int duracionMinutos;
    private String genero;

    // Constructor para inicializar una película con todos sus atributos
    public Pelicula(String idPelicula, String titulo, String director, int anio, int duracionMinutos, String genero) {
        this.idPelicula = idPelicula;
        this.titulo = titulo;
        this.director = director;
        this.anio = anio;
        this.duracionMinutos = duracionMinutos;
        this.genero = genero;
    }

    // Obtiene el ID de la película
    public String getIdPelicula() {
        return idPelicula;
    }

    // Obtiene el título de la película
    public String getTitulo() {
        return titulo;
    }

    // Obtiene el director de la película
    public String getDirector() {
        return director;
    }

    // Obtiene el año de estreno
    public int getAnio() {
        return anio;
    }

    // Obtiene la duración en minutos
    public int getDuracionMinutos() {
        return duracionMinutos;
    }

    // Obtiene el género de la película
    public String getGenero() {
        return genero;
    }
}
