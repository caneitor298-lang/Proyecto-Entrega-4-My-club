import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

public class SolicitudesController {
    private final DatosAplicacion datos;

    public SolicitudesController(DatosAplicacion datos) {
        this.datos = datos;
    }

    /** Este es el método que debe llamar el botón Enviar del formulario del compañero. */
    public SolicitudLider enviarSolicitud(String carnet, String nombre, String correo,
            String carrera, String contrasena, String nombreClub, String descripcion,
            String categoria, String ubicacion, String horario) {
        return crearSolicitud(carnet, nombre, 0, correo, carrera, contrasena,
                nombreClub, descripcion, categoria, ubicacion, horario);
    }

    /** Versión usada por el formulario integrado: conserva también la edad del solicitante. */
    public SolicitudLider enviarSolicitud(String carnet, String nombre, int edad, String correo,
            String carrera, String contrasena, String nombreClub, String descripcion,
            String categoria, String ubicacion, String horario) {
        if (edad < 1 || edad > 120) {
            throw new IllegalArgumentException("Ingresa una edad válida, entre 1 y 120 años.");
        }
        return crearSolicitud(carnet, nombre, edad, correo, carrera, contrasena,
                nombreClub, descripcion, categoria, ubicacion, horario);
    }

    private SolicitudLider crearSolicitud(String carnet, String nombre, int edad, String correo,
            String carrera, String contrasena, String nombreClub, String descripcion,
            String categoria, String ubicacion, String horario) {
        carnet = validarTexto(carnet, "El carné", 20);
        if (!carnet.matches("[0-9]+")) {
            throw new IllegalArgumentException("El carné debe contener solo números.");
        }
        nombre = validarTexto(nombre, "El nombre", 150);
        correo = validarTexto(correo, "El correo", 150).toLowerCase(Locale.ROOT);
        if (!correo.matches("[A-Za-z0-9._%+\\-]+@uvg\\.edu\\.gt")) {
            throw new IllegalArgumentException("Usa un correo válido de @uvg.edu.gt.");
        }
        carrera = validarTexto(carrera, "La carrera", 150);
        if (contrasena == null || contrasena.length() < 8 || contrasena.trim().isEmpty()) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 8 caracteres.");
        }
        nombreClub = validarTexto(nombreClub, "El nombre del club", 100);
        descripcion = validarTexto(descripcion, "La descripción", 2000);
        categoria = validarTexto(categoria, "La categoría", 50);
        if (!Arrays.asList("Deportes", "Tecnología", "Arte", "Académicos", "Entretenimiento").contains(categoria)) {
            throw new IllegalArgumentException("Selecciona una categoría del catálogo de clubes.");
        }
        ubicacion = validarTexto(ubicacion, "La ubicación", 150);
        horario = validarTexto(horario, "El horario", 150);
        validarDisponibilidad(correo, carnet, nombreClub);
        for (SolicitudLider solicitud : datos.getSolicitudesLider()) {
            if (solicitud.getEstado() == EstadoSolicitud.PENDIENTE
                    && (solicitud.getCorreo().equalsIgnoreCase(correo)
                    || solicitud.getCarnet().equals(carnet)
                    || normalizar(solicitud.getNombreClub()).equals(normalizar(nombreClub)))) {
                throw new IllegalArgumentException("Ya existe una solicitud pendiente con ese correo, carné o club.");
            }
        }
        SolicitudLider solicitud = new SolicitudLider(carnet, nombre, edad, correo, carrera,
                contrasena, nombreClub, descripcion, categoria, ubicacion, horario);
        datos.getSolicitudesLider().add(solicitud);
        try {
            datos.guardar();
        } catch (RuntimeException ex) {
            datos.getSolicitudesLider().remove(solicitud);
            throw ex;
        }
        return solicitud;
    }

    /** estado == null devuelve el historial completo. */
    public ArrayList<SolicitudLider> obtenerSolicitudes(Usuario usuario, EstadoSolicitud estado) {
        validarAdministrador(usuario);
        ArrayList<SolicitudLider> resultado = new ArrayList<>();
        for (SolicitudLider solicitud : datos.getSolicitudesLider()) {
            if (estado == null || solicitud.getEstado() == estado) resultado.add(solicitud);
        }
        return resultado;
    }

    public Lider aceptarSolicitud(Usuario usuario, SolicitudLider solicitud) {
        Administrador administrador = validarAdministrador(usuario);
        validarPendiente(solicitud);
        // Otro registro pudo ocupar el correo o el nombre del club durante la espera.
        validarDisponibilidad(solicitud.getCorreo(), solicitud.getCarnet(), solicitud.getNombreClub());
        String contrasena = solicitud.getContrasena();
        Club club = new Club(solicitud.getNombreClub(), solicitud.getDescripcion(),
                solicitud.getCategoria(), solicitud.getUbicacion(), solicitud.getHorario());
        Lider lider = new Lider(solicitud.getNombre(), solicitud.getCorreo(), contrasena,
                club, solicitud.getCarnet(), solicitud.getEdad(), solicitud.getCarrera());
        datos.getClubes().add(club);
        datos.getUsuarios().add(lider);
        solicitud.resolver(EstadoSolicitud.ACEPTADA, administrador, "");
        try {
            datos.guardar();
        } catch (RuntimeException ex) {
            datos.getClubes().remove(club);
            datos.getUsuarios().remove(lider);
            solicitud.restaurarPendiente(contrasena);
            throw ex;
        }
        return lider;
    }

    public void denegarSolicitud(Usuario usuario, SolicitudLider solicitud, String motivo) {
        Administrador administrador = validarAdministrador(usuario);
        validarPendiente(solicitud);
        motivo = validarTexto(motivo, "El motivo de denegación", 500);
        String contrasena = solicitud.getContrasena();
        solicitud.resolver(EstadoSolicitud.DENEGADA, administrador, motivo);
        try {
            datos.guardar();
        } catch (RuntimeException ex) {
            solicitud.restaurarPendiente(contrasena);
            throw ex;
        }
    }

    private Administrador validarAdministrador(Usuario usuario) {
        if (!(usuario instanceof Administrador) || !datos.getUsuarios().contains(usuario)) {
            throw new IllegalArgumentException("Solo un administrador registrado puede revisar solicitudes.");
        }
        return (Administrador) usuario;
    }

    private void validarPendiente(SolicitudLider solicitud) {
        if (solicitud == null || !datos.getSolicitudesLider().contains(solicitud)) {
            throw new IllegalArgumentException("Selecciona una solicitud registrada.");
        }
        if (solicitud.getEstado() != EstadoSolicitud.PENDIENTE) {
            throw new IllegalArgumentException("Esta solicitud ya fue revisada.");
        }
    }

    private void validarDisponibilidad(String correo, String carnet, String nombreClub) {
        for (Usuario usuario : datos.getUsuarios()) {
            if (usuario.getCorreo().equalsIgnoreCase(correo)) {
                throw new IllegalArgumentException("Ya existe una cuenta con ese correo.");
            }
            if (usuario instanceof Lider && carnet.equals(((Lider) usuario).getCarnet())) {
                throw new IllegalArgumentException("Ya existe un líder con ese carné.");
            }
        }
        for (Club club : datos.getClubes()) {
            if (normalizar(club.getNombre()).equals(normalizar(nombreClub))) {
                throw new IllegalArgumentException("Ya existe un club con ese nombre.");
            }
        }
    }

    private String validarTexto(String texto, String nombre, int maximo) {
        if (texto == null || texto.trim().isEmpty()) {
            throw new IllegalArgumentException(nombre + " es obligatorio.");
        }
        if (texto.trim().length() > maximo) {
            throw new IllegalArgumentException(nombre + " admite hasta " + maximo + " caracteres.");
        }
        return texto.trim();
    }

    private String normalizar(String texto) {
        return Normalizer.normalize(texto.trim(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "").toLowerCase(Locale.ROOT);
    }
}
