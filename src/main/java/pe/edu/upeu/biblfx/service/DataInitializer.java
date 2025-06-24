package pe.edu.upeu.biblfx.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pe.edu.upeu.biblfx.model.*;
import pe.edu.upeu.biblfx.repository.LibroRepository;
import pe.edu.upeu.biblfx.repository.PrestamoRepository;
import pe.edu.upeu.biblfx.repository.UsuarioRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private LibroRepository libroRepository;

    @Autowired
    private PrestamoRepository prestamoRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Solo inicializar si no hay usuarios en la base de datos
        if (usuarioRepository.count() == 0) {
            System.out.println("🚀 Inicializando base de datos con datos de prueba...");
            initializeUsers();
            initializeBooks();
            initializeLoans();
            System.out.println("✅ Base de datos inicializada exitosamente!");
        } else {
            System.out.println("✅ Base de datos ya contiene datos existentes.");
        }
    }

    private void initializeUsers() {
        String encodedPassword = passwordEncoder.encode("password123");
        
        List<Usuario> usuarios = Arrays.asList(
            new Usuario("admin", encodedPassword, "Administrador del Sistema", "admin@biblioteca.com"),
            new Usuario("bibliotecario1", encodedPassword, "María González", "maria@biblioteca.com"),
            new Usuario("bibliotecario2", encodedPassword, "Carlos Rodríguez", "carlos@biblioteca.com"),
            new Usuario("lector1", encodedPassword, "Ana Martínez", "ana@email.com"),
            new Usuario("lector2", encodedPassword, "Luis Pérez", "luis@email.com"),
            new Usuario("lector3", encodedPassword, "Carmen López", "carmen@email.com"),
            new Usuario("lector4", encodedPassword, "Roberto Silva", "roberto@email.com"),
            new Usuario("lector5", encodedPassword, "Patricia Ruiz", "patricia@email.com")
        );

        // Asignar roles
        usuarios.get(0).setRol(RolUsuario.ADMINISTRADOR);
        usuarios.get(1).setRol(RolUsuario.BIBLIOTECARIO);
        usuarios.get(2).setRol(RolUsuario.BIBLIOTECARIO);
        usuarios.get(3).setRol(RolUsuario.LECTOR);
        usuarios.get(4).setRol(RolUsuario.LECTOR);
        usuarios.get(5).setRol(RolUsuario.LECTOR);
        usuarios.get(6).setRol(RolUsuario.LECTOR);
        usuarios.get(7).setRol(RolUsuario.LECTOR);

        usuarioRepository.saveAll(usuarios);
        System.out.println("👥 " + usuarios.size() + " usuarios de prueba creados");
    }

    private void initializeBooks() {
        List<Libro> libros = Arrays.asList(
            // Literatura Clásica
            new Libro("El Quijote", "Miguel de Cervantes", "978-84-376-0494-7"),
            new Libro("La Celestina", "Fernando de Rojas", "978-84-376-0494-9"),
            new Libro("Don Juan Tenorio", "José Zorrilla", "978-84-376-0494-8"),
            new Libro("Los miserables", "Victor Hugo", "978-84-376-0495-0"),
            new Libro("Madame Bovary", "Gustave Flaubert", "978-84-376-0495-1"),
            new Libro("El retrato de Dorian Gray", "Oscar Wilde", "978-84-376-0495-2"),
            new Libro("Orgullo y prejuicio", "Jane Austen", "978-84-376-0495-3"),
            new Libro("Crimen y castigo", "Fiódor Dostoyevski", "978-84-376-0495-4"),
            
            // Literatura Contemporánea
            new Libro("Cien años de soledad", "Gabriel García Márquez", "978-84-397-2077-5"),
            new Libro("Pedro Páramo", "Juan Rulfo", "978-84-397-2077-6"),
            new Libro("El Aleph", "Jorge Luis Borges", "978-84-397-2077-7"),
            new Libro("Rayuela", "Julio Cortázar", "978-84-397-2077-8"),
            new Libro("1984", "George Orwell", "978-84-376-0495-5"),
            new Libro("Fahrenheit 451", "Ray Bradbury", "978-84-376-0495-6"),
            
            // Ficción y Fantasía
            new Libro("El señor de los anillos", "J.R.R. Tolkien", "978-84-397-2077-9"),
            new Libro("Harry Potter y la piedra filosofal", "J.K. Rowling", "978-84-397-2078-0"),
            new Libro("El hobbit", "J.R.R. Tolkien", "978-84-397-2078-5"),
            new Libro("El código Da Vinci", "Dan Brown", "978-84-397-2078-1"),
            new Libro("Los pilares de la tierra", "Ken Follett", "978-84-397-2078-2"),
            new Libro("El nombre del viento", "Patrick Rothfuss", "978-84-397-2078-3"),
            
            // Ciencia Ficción
            new Libro("Dune", "Frank Herbert", "978-84-397-2078-4"),
            
            // Historia y Filosofía
            new Libro("Los pilares de la tierra", "Ken Follett", "978-84-397-2078-6"),
            new Libro("El arte de la guerra", "Sun Tzu", "978-84-376-0495-8"),
            new Libro("Filosofía para principiantes", "Rius", "978-84-397-2078-7"),
            
            // Arte y Cultura
            new Libro("Historia del arte", "Ernst Gombrich", "978-84-376-0495-7"),
            
            // Cocina
            new Libro("Cocina peruana", "Gastón Acurio", "978-84-397-2078-8"),
            new Libro("El arte de la cocina francesa", "Julia Child", "978-84-397-2078-9"),
            new Libro("Cocina mediterránea", "Jamie Oliver", "978-84-397-2079-0")
        );

        // Configurar detalles de los libros
        configureBookDetails(libros);

        libroRepository.saveAll(libros);
        System.out.println("📚 " + libros.size() + " libros de prueba creados");
    }

    private void configureBookDetails(List<Libro> libros) {
        // Literatura Clásica
        configureBook(libros.get(0), "Cátedra", 1605, CategoriaLibro.LITERATURA, 5, 5, "Obra maestra de la literatura española");
        configureBook(libros.get(1), "Cátedra", 1499, CategoriaLibro.LITERATURA, 2, 2, "Tragicomedia de Calisto y Melibea");
        configureBook(libros.get(2), "Cátedra", 1844, CategoriaLibro.LITERATURA, 4, 4, "Drama romántico español");
        configureBook(libros.get(3), "Cátedra", 1862, CategoriaLibro.LITERATURA, 3, 3, "Novela histórica francesa");
        configureBook(libros.get(4), "Cátedra", 1857, CategoriaLibro.LITERATURA, 2, 2, "Novela realista francesa");
        configureBook(libros.get(5), "Cátedra", 1890, CategoriaLibro.LITERATURA, 3, 3, "Novela gótica británica");
        configureBook(libros.get(6), "Cátedra", 1813, CategoriaLibro.LITERATURA, 4, 4, "Novela romántica británica");
        configureBook(libros.get(7), "Cátedra", 1866, CategoriaLibro.LITERATURA, 2, 2, "Novela psicológica rusa");

        // Literatura Contemporánea
        configureBook(libros.get(8), "Alfaguara", 1967, CategoriaLibro.LITERATURA, 3, 3, "Novela del realismo mágico");
        configureBook(libros.get(9), "Alfaguara", 1955, CategoriaLibro.LITERATURA, 3, 3, "Novela mexicana del siglo XX");
        configureBook(libros.get(10), "Alfaguara", 1949, CategoriaLibro.LITERATURA, 4, 4, "Colección de cuentos fantásticos");
        configureBook(libros.get(11), "Alfaguara", 1963, CategoriaLibro.LITERATURA, 2, 2, "Novela experimental argentina");
        configureBook(libros.get(12), "Cátedra", 1949, CategoriaLibro.LITERATURA, 4, 4, "Novela distópica");
        configureBook(libros.get(13), "Cátedra", 1953, CategoriaLibro.LITERATURA, 3, 3, "Novela distópica sobre la censura");

        // Ficción y Fantasía
        configureBook(libros.get(14), "Minotauro", 1954, CategoriaLibro.GENERAL, 5, 5, "Trilogía épica de fantasía");
        configureBook(libros.get(15), "Salamandra", 1997, CategoriaLibro.GENERAL, 6, 6, "Primera novela de la saga de Harry Potter");
        configureBook(libros.get(16), "Minotauro", 1937, CategoriaLibro.GENERAL, 4, 4, "Precuela de El señor de los anillos");
        configureBook(libros.get(17), "Umbriel", 2003, CategoriaLibro.GENERAL, 4, 4, "Thriller de misterio");
        configureBook(libros.get(18), "Plaza & Janés", 1989, CategoriaLibro.HISTORIA, 3, 3, "Novela histórica medieval");
        configureBook(libros.get(19), "Plaza & Janés", 2007, CategoriaLibro.GENERAL, 2, 2, "Primera parte de la Crónica del asesino de reyes");

        // Ciencia Ficción
        configureBook(libros.get(20), "Plaza & Janés", 1965, CategoriaLibro.CIENCIA, 3, 3, "Clásico de la ciencia ficción");

        // Historia y Filosofía
        configureBook(libros.get(21), "Plaza & Janés", 1989, CategoriaLibro.HISTORIA, 3, 3, "Novela histórica medieval");
        configureBook(libros.get(22), "Cátedra", -500, CategoriaLibro.HISTORIA, 4, 4, "Tratado militar chino clásico");
        configureBook(libros.get(23), "DeBolsillo", 1985, CategoriaLibro.FILOSOFIA, 3, 3, "Introducción amena a la filosofía");

        // Arte y Cultura
        configureBook(libros.get(24), "Cátedra", 1950, CategoriaLibro.ARTE, 2, 2, "Compendio de la historia del arte occidental");

        // Cocina
        configureBook(libros.get(25), "Planeta", 2010, CategoriaLibro.COCINA, 3, 3, "Recetas de la gastronomía peruana");
        configureBook(libros.get(26), "Debolsillo", 1961, CategoriaLibro.COCINA, 2, 2, "Clásico de la cocina francesa");
        configureBook(libros.get(27), "Debolsillo", 2005, CategoriaLibro.COCINA, 3, 3, "Recetas mediterráneas saludables");
    }

    private void configureBook(Libro libro, String editorial, int anio, CategoriaLibro categoria, int total, int disponible, String descripcion) {
        libro.setEditorial(editorial);
        libro.setAnioPublicacion(anio);
        libro.setCategoria(categoria);
        libro.setCantidadTotal(total);
        libro.setCantidadDisponible(disponible);
        libro.setDescripcion(descripcion);
    }

    private void initializeLoans() {
        // Obtener usuarios y libros para crear préstamos
        Usuario lector1 = usuarioRepository.findByUsername("lector1").orElse(null);
        Usuario lector2 = usuarioRepository.findByUsername("lector2").orElse(null);
        Usuario lector3 = usuarioRepository.findByUsername("lector3").orElse(null);
        Usuario lector4 = usuarioRepository.findByUsername("lector4").orElse(null);
        Usuario lector5 = usuarioRepository.findByUsername("lector5").orElse(null);

        List<Libro> libros = libroRepository.findAll();
        if (libros.isEmpty()) return;

        List<Prestamo> prestamos = Arrays.asList(
            // Préstamos activos
            new Prestamo(lector1, libros.get(0), LocalDateTime.now().plusDays(7)),
            new Prestamo(lector2, libros.get(8), LocalDateTime.now().plusDays(14)),
            new Prestamo(lector3, libros.get(14), LocalDateTime.now().plusDays(10)),
            new Prestamo(lector4, libros.get(15), LocalDateTime.now().plusDays(5)),
            
            // Préstamos devueltos
            new Prestamo(lector1, libros.get(1), LocalDateTime.now().minusDays(2)),
            new Prestamo(lector2, libros.get(9), LocalDateTime.now().minusDays(5)),
            new Prestamo(lector3, libros.get(16), LocalDateTime.now().minusDays(8)),
            
            // Préstamos vencidos
            new Prestamo(lector4, libros.get(2), LocalDateTime.now().minusDays(3)),
            new Prestamo(lector5, libros.get(10), LocalDateTime.now().minusDays(1))
        );

        // Configurar estados y detalles de los préstamos
        configureLoanDetails(prestamos);

        prestamoRepository.saveAll(prestamos);
        System.out.println("📖 " + prestamos.size() + " préstamos de prueba creados");
    }

    private void configureLoanDetails(List<Prestamo> prestamos) {
        // Préstamos activos
        prestamos.get(0).setObservaciones("Préstamo para investigación académica");
        prestamos.get(1).setObservaciones("Lectura personal");
        prestamos.get(2).setObservaciones("Préstamo para proyecto escolar");
        prestamos.get(3).setObservaciones("Lectura recreativa");

        // Préstamos devueltos
        prestamos.get(4).setEstado(EstadoPrestamo.DEVUELTO);
        prestamos.get(4).setFechaDevolucionReal(LocalDateTime.now().minusDays(1));
        prestamos.get(4).setObservaciones("Devolución a tiempo");

        prestamos.get(5).setEstado(EstadoPrestamo.DEVUELTO);
        prestamos.get(5).setFechaDevolucionReal(LocalDateTime.now().minusDays(2));
        prestamos.get(5).setObservaciones("Devolución tardía");

        prestamos.get(6).setEstado(EstadoPrestamo.DEVUELTO);
        prestamos.get(6).setFechaDevolucionReal(LocalDateTime.now().minusDays(3));
        prestamos.get(6).setObservaciones("Devolución a tiempo");

        // Préstamos vencidos
        prestamos.get(7).setObservaciones("Préstamo vencido - pendiente de devolución");
        prestamos.get(8).setObservaciones("Préstamo vencido - pendiente de devolución");
    }
} 