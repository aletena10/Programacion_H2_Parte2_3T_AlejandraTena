package cine;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

// Clase principal para gestionar el menú y las operaciones del cine
public class Main {
    // Scanner para leer la entrada del usuario
    private static final Scanner scanner = new Scanner(System.in);
    // Instancia de DatabaseConnection para conectar con la base de datos
    private static final DatabaseConnection db = new DatabaseConnection();

    // Mapa de abreviaturas a géneros completos
    private static final Map<String, String> ABREVIATURAS_GENEROS = new HashMap<>();
    static {
        ABREVIATURAS_GENEROS.put("COM", "Comedia");
        ABREVIATURAS_GENEROS.put("DRA", "Drama");
        ABREVIATURAS_GENEROS.put("SUS", "Suspense");
        ABREVIATURAS_GENEROS.put("SOC", "Social");
    }

    // Método principal que inicia la aplicación
    public static void main(String[] args) {
        boolean salir = false;

        // Bucle que muestra el menú hasta que el usuario elige salir
        while (!salir) {
            mostrarMenu();
            try {
                int opcion = Integer.parseInt(scanner.nextLine());
                switch (opcion) {
                    case 1:
                        verPeliculas();
                        break;
                    case 2:
                        anadirPelicula();
                        break;
                    case 3:
                        eliminarPelicula();
                        break;
                    case 4:
                        modificarPelicula();
                        break;
                    case 5:
                        System.out.println("Saliendo de la aplicación...");
                        salir = true;
                        break;
                    default:
                        System.out.println("Opción no válida. Por favor, selecciona una opción entre 1 y 5.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Error: Ingresa un número válido.");
            }
        }
        scanner.close();
    }

    // Muestra el menú de opciones
    private static void mostrarMenu() {
        System.out.println("\n=== Menú Cine ===");
        System.out.println("1 - Ver películas");
        System.out.println("2 - Añadir película");
        System.out.println("3 - Eliminar película");
        System.out.println("4 - Modificar película");
        System.out.println("5 - Salir");
        System.out.print("Selecciona una opción: ");
    }

    // Muestra la lista de películas de la base de datos
    private static void verPeliculas() {
        try {
            List<Pelicula> peliculas = db.getPeliculas();
            if (peliculas.isEmpty()) {
                System.out.println("No hay películas disponibles.");
                return;
            }

            System.out.println("\n=== Lista de Películas ===");
            System.out.println("ID        Título                        Director                 Año   Duración   Género");
            System.out.println("----------------------------------------------------------------------------------------------------");

            for (Pelicula p : peliculas) {
                String id = p.getIdPelicula() + "         ".substring(p.getIdPelicula().length());
                String titulo = p.getTitulo() + "                             ".substring(p.getTitulo().length());
                String director = p.getDirector() + "                        ".substring(p.getDirector().length());
                String anio = p.getAnio() + "      ".substring(String.valueOf(p.getAnio()).length());
                String duracion = p.getDuracionMinutos() + "          ".substring(String.valueOf(p.getDuracionMinutos()).length());
                String genero = p.getGenero(); // Ahora muestra el id_genero (ej. 'COM')
                System.out.println(id + titulo + director + anio + duracion + genero);
            }
        } catch (SQLException e) {
            System.err.println("Error al consultar las películas: " + e.getMessage());
        }
    }

    // Solicita datos y añade una nueva película
    private static void anadirPelicula() {
        try {
            System.out.println("\n=== Añadir Película ===");
            System.out.print("ID de la película: ");
            String idPelicula = scanner.nextLine().trim();
            if (idPelicula.isEmpty()) {
                System.out.println("Error: El ID no puede estar vacío.");
                return;
            }

            System.out.print("Título: ");
            String titulo = scanner.nextLine().trim();
            if (titulo.isEmpty()) {
                System.out.println("Error: El título no puede estar vacío.");
                return;
            }

            System.out.print("Director: ");
            String director = scanner.nextLine().trim();
            if (director.isEmpty()) {
                System.out.println("Error: El director no puede estar vacío.");
                return;
            }

            System.out.print("Año: ");
            String anioStr = scanner.nextLine().trim();
            int anio;
            try {
                anio = Integer.parseInt(anioStr);
                if (anio < 1888 || anio > 2025) {
                    System.out.println("Error: Año inválido (debe ser entre 1888 y 2025).");
                    return;
                }
            } catch (NumberFormatException e) {
                System.out.println("Error: Ingresa un año válido.");
                return;
            }

            System.out.print("Duración (minutos): ");
            String duracionStr = scanner.nextLine().trim();
            int duracion;
            try {
                duracion = Integer.parseInt(duracionStr);
                if (duracion <= 0) {
                    System.out.println("Error: La duración debe ser mayor a 0.");
                    return;
                }
            } catch (NumberFormatException e) {
                System.out.println("Error: Ingresa una duración válida.");
                return;
            }

            // Mostrar géneros disponibles y validar entrada
            List<String> generos = db.getGeneros();
            System.out.println("Géneros disponibles: " + generos);
            System.out.println("(Escribe el género completo o usa abreviaturas: COM=Comedia, DRA=Drama, SUS=Suspense, SOC=Social)");
            System.out.print("Género: ");
            String generoInput = scanner.nextLine().trim().toUpperCase(); // Convertir a mayúsculas para comparar abreviaturas
            if (generoInput.isEmpty()) {
                System.out.println("Error: El género no puede estar vacío.");
                return;
            }

            // Buscar género normalizado usando abreviaturas o nombre completo
            String generoNormalizado = ABREVIATURAS_GENEROS.get(generoInput);
            if (generoNormalizado == null) {
                // Si no es abreviatura, buscar coincidencia exacta (insensible a mayúsculas/minúsculas)
                for (String g : generos) {
                    if (g.toLowerCase().startsWith(generoInput.toLowerCase())) {
                        generoNormalizado = g; // Usar el género tal como está en la base de datos
                        break;
                    }
                }
            }

            if (generoNormalizado == null) {
                System.out.println("Error: Género no válido. Debe ser uno de: " + generos + " o sus abreviaturas (COM, DRA, SUS, SOC).");
                return;
            }

            // Depuración: Mostrar el género antes y después de la llamada
            System.out.println("Genero normalizado a enviar: " + generoNormalizado);
            String generoParaDB = generoNormalizado; // Variable adicional para depuración
            System.out.println("Genero pasado a anadirPelicula: " + generoParaDB);

            // Añadir película usando el género normalizado
            boolean exito = db.anadirPelicula(idPelicula, titulo, director, anio, duracion, generoParaDB);
            if (exito) {
                System.out.println("Película añadida correctamente.");
            } else {
                System.out.println("Error: Ya existe una película con el ID " + idPelicula + ".");
            }
        } catch (SQLException e) {
            System.err.println("Error al añadir la película: " + e.getMessage());
        }
    }

    // Solicita ID y elimina una película
    private static void eliminarPelicula() {
        try {
            System.out.println("\n=== Eliminar Película ===");
            System.out.print("ID de la película a eliminar: ");
            String idPelicula = scanner.nextLine().trim();
            if (idPelicula.isEmpty()) {
                System.out.println("Error: El ID no puede estar vacío.");
                return;
            }

            boolean exito = db.eliminarPelicula(idPelicula);
            if (exito) {
                System.out.println("Película eliminada correctamente.");
            } else {
                System.out.println("Error: No existe una película con el ID " + idPelicula + ".");
            }
        } catch (SQLException e) {
            System.err.println("Error al eliminar la película: " + e.getMessage());
        }
    }

    // Solicita ID y modifica título y duración de una película
    private static void modificarPelicula() {
        try {
            System.out.println("\n=== Modificar Película ===");
            System.out.print("ID de la película a modificar: ");
            String idPelicula = scanner.nextLine().trim();
            if (idPelicula.isEmpty()) {
                System.out.println("Error: El ID no puede estar vacío.");
                return;
            }

            if (!db.existePelicula(idPelicula)) {
                System.out.println("Error: No existe una película con el ID " + idPelicula + ".");
                return;
            }

            System.out.print("Nuevo título: ");
            String nuevoTitulo = scanner.nextLine().trim();
            if (nuevoTitulo.isEmpty()) {
                System.out.println("Error: El título no puede estar vacío.");
                return;
            }

            System.out.print("Nueva duración (minutos): ");
            String duracionStr = scanner.nextLine().trim();
            int nuevaDuracion;
            try {
                nuevaDuracion = Integer.parseInt(duracionStr);
                if (nuevaDuracion <= 0) {
                    System.out.println("Error: La duración debe ser mayor a 0.");
                    return;
                }
            } catch (NumberFormatException e) {
                System.out.println("Error: Ingresa una duración válida.");
                return;
            }

            boolean exito = db.modificarPelicula(idPelicula, nuevoTitulo, nuevaDuracion);
            if (exito) {
                System.out.println("Película modificada correctamente.");
            }
        } catch (SQLException e) {
            System.err.println("Error al modificar la película: " + e.getMessage());
        }
    }
}
