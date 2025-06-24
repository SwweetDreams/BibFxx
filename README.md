# BiblFx - Sistema de Gestión de Biblioteca

BiblFx es un sistema completo de gestión de biblioteca desarrollado en JavaFX y Spring Boot, que permite administrar usuarios, libros y préstamos de manera eficiente y segura.

## Características Principales

### 🔐 Seguridad y Autenticación
- Sistema de login con encriptación de contraseñas (BCrypt)
- Roles de usuario: Administrador, Bibliotecario y Lector
- Validación de campos en formularios
- Control de acceso basado en roles

### 📚 Gestión de Libros
- CRUD completo de libros
- Categorización por géneros literarios
- Control de stock (cantidad total y disponible)
- Búsqueda y filtrado avanzado
- Paginación de resultados

### 👥 Gestión de Usuarios
- CRUD completo de usuarios
- Gestión de roles y permisos
- Activación/desactivación de cuentas
- Validación de datos de entrada

### 📖 Gestión de Préstamos
- Creación y seguimiento de préstamos
- Control de fechas de devolución
- Estados de préstamo (Activo, Devuelto, Cancelado)
- Devolución y cancelación de préstamos
- Validación de disponibilidad de libros

### 📊 Reportes y Estadísticas
- Dashboard con estadísticas en tiempo real
- Reportes de préstamos activos y vencidos
- Reportes de libros disponibles y con stock bajo
- Reportes de usuarios por rol

### 🎨 Interfaz de Usuario
- Interfaz moderna y responsiva con JavaFX
- Diseño atractivo con CSS personalizado
- Formularios intuitivos con validación
- Navegación por pestañas
- Paginación en todas las tablas

## Tecnologías Utilizadas

- **Java 17+**
- **JavaFX** - Interfaz gráfica
- **Spring Boot 3.x** - Framework de aplicación
- **Spring Data JPA** - Persistencia de datos
- **H2 Database** - Base de datos en memoria/persistente
- **BCrypt** - Encriptación de contraseñas
- **Maven** - Gestión de dependencias

## Requisitos del Sistema

- Java 17 o superior
- Maven 3.6+
- Mínimo 2GB RAM
- Resolución mínima 1024x768

## Instalación y Configuración

### 1. Clonar el repositorio
```bash
git clone <url-del-repositorio>
cd BiblFx
```

### 2. Compilar el proyecto
```bash
mvn clean compile
```

### 3. Ejecutar la aplicación
```bash
mvn javafx:run
```

O alternativamente:
```bash
java -jar target/biblfx-1.0.0.jar
```

## Configuración de la Base de Datos

El sistema utiliza H2 Database con las siguientes configuraciones:

- **Modo**: Persistente (archivo local)
- **Archivo**: `./data/biblfx.mv.db`
- **Usuario**: `sa`
- **Contraseña**: (vacía)
- **URL**: `jdbc:h2:file:./data/biblfx`

### Configuración en `application.properties`:
```properties
spring.datasource.url=jdbc:h2:file:./data/biblfx
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.h2.console.enabled=true
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true
```

## Datos de Prueba

El sistema incluye datos de prueba que se cargan automáticamente:

### Usuarios de Prueba:
- **admin** / **123456** - Administrador del Sistema
- **bibliotecario1** / **123456** - María González
- **bibliotecario2** / **123456** - Carlos Rodríguez
- **lector1** / **123456** - Ana Martínez
- **lector2** / **123456** - Luis Pérez
- **lector3** / **123456** - Carmen López

### Libros de Prueba:
- 20 libros de diferentes categorías
- Variedad de autores y editoriales
- Diferentes cantidades de stock

### Préstamos de Prueba:
- 5 préstamos con diferentes estados
- Fechas variadas para testing

## Estructura del Proyecto

```
src/
├── main/
│   ├── java/pe/edu/upeu/biblfx/
│   │   ├── BiblFxApplication.java          # Clase principal de Spring Boot
│   │   ├── Application.java                # Clase principal de JavaFX
│   │   ├── controller/                     # Controladores JavaFX
│   │   │   ├── LoginController.java
│   │   │   ├── MainMenuController.java
│   │   │   ├── UsuarioCrudController.java
│   │   │   ├── LibroCrudController.java
│   │   │   └── PrestamoCrudController.java
│   │   ├── model/                          # Entidades JPA
│   │   │   ├── Usuario.java
│   │   │   ├── Libro.java
│   │   │   ├── Prestamo.java
│   │   │   └── enums/
│   │   ├── repository/                     # Repositorios Spring Data
│   │   │   ├── UsuarioRepository.java
│   │   │   ├── LibroRepository.java
│   │   │   └── PrestamoRepository.java
│   │   └── service/                        # Servicios de negocio
│   │       ├── UsuarioService.java
│   │       ├── LibroService.java
│   │       ├── PrestamoService.java
│   │       └── EncriptacionService.java
│   └── resources/
│       ├── fxml/                           # Vistas FXML
│       │   ├── LoginView.fxml
│       │   ├── MainMenuView.fxml
│       │   ├── UsuarioFormView.fxml
│       │   ├── LibroFormView.fxml
│       │   └── PrestamoFormView.fxml
│       ├── css/                            # Estilos CSS
│       │   └── styles.css
│       ├── data.sql                        # Datos de inicialización
│       └── application.properties          # Configuración
```

## Funcionalidades por Rol

### 👑 Administrador
- Acceso completo a todas las funcionalidades
- Gestión de usuarios (crear, editar, eliminar)
- Gestión de libros (crear, editar, eliminar)
- Gestión de préstamos (crear, editar, cancelar, devolver)
- Visualización de reportes y estadísticas

### 📚 Bibliotecario
- Gestión de libros (crear, editar, eliminar)
- Gestión de préstamos (crear, editar, cancelar, devolver)
- Visualización de reportes y estadísticas
- No puede gestionar usuarios

### 👤 Lector
- Consulta de libros disponibles
- Visualización de sus préstamos activos
- No puede realizar operaciones de gestión

## Características Técnicas

### Seguridad
- Encriptación de contraseñas con BCrypt
- Validación de entrada en todos los formularios
- Control de acceso basado en roles
- Sanitización de datos

### Persistencia
- ORM JPA con Hibernate
- Base de datos H2 persistente
- Transacciones automáticas
- Validación a nivel de entidad

### Interfaz de Usuario
- JavaFX con FXML
- CSS personalizado para estilos
- Validación en tiempo real
- Mensajes de error informativos
- Paginación en tablas

### Arquitectura
- Patrón MVC
- Separación de capas (Controller, Service, Repository)
- Inyección de dependencias con Spring
- Configuración basada en anotaciones

## Solución de Problemas

### Error de conexión a la base de datos
- Verificar que el directorio `./data/` existe
- Comprobar permisos de escritura
- Revisar configuración en `application.properties`

### Error de compilación
- Verificar versión de Java (17+)
- Ejecutar `mvn clean compile`
- Revisar dependencias en `pom.xml`

### Error de ejecución
- Verificar que JavaFX esté configurado correctamente
- Comprobar variables de entorno JAVA_HOME
- Revisar logs de la aplicación

## Contribución

1. Fork el proyecto
2. Crear una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abrir un Pull Request

## Licencia

Este proyecto está bajo la Licencia MIT. Ver el archivo `LICENSE` para más detalles.

## Contacto

- **Desarrollador**: [Tu Nombre]
- **Email**: [tu-email@ejemplo.com]
- **Proyecto**: [https://github.com/tuusuario/BiblFx](https://github.com/tuusuario/BiblFx)

---

**BiblFx** - Sistema de Gestión de Biblioteca Moderno y Eficiente 🏛️📚 