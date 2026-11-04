public class UsuarioController {
    private final DatosAplicacion datos;
    public UsuarioController(DatosAplicacion datos) { this.datos = datos; }
    public Usuario iniciarSesion(String correo, String contrasena) {
        if (correo == null || correo.trim().isEmpty() || contrasena == null || contrasena.isEmpty()) return null;
        for (Usuario usuario : datos.getUsuarios()) {
            if (usuario.getCorreo().equalsIgnoreCase(correo.trim())
                    && usuario.getContrasena().equals(contrasena)) return usuario;
        }
        return null;
    }


    public void actualizarPerfil(Estudiante estudiante, String nombre, String carrera, String actual, String nueva, String confirmar) {
    if (estudiante == null || !datos.getUsuarios().contains(estudiante)) {
        throw new IllegalArgumentException("Estudiante no registrado.");
    }
    if (actual == null || nueva == null || confirmar == null) {
        throw new IllegalArgumentException("Revisa los campos de contraseña.");
    }

    if (nombre == null || nombre.trim().isEmpty()
            || carrera == null || carrera.trim().isEmpty()) {
        throw new IllegalArgumentException(
            "El nombre y la carrera son obligatorios."
        );
    }

    if (!nueva.isEmpty() || !confirmar.isEmpty()
            || !actual.isEmpty()) {

        if (!estudiante.getContrasena().equals(actual)) {
            throw new IllegalArgumentException(
                "La contraseña actual es incorrecta."
            );
        }

        if (nueva.length() < 8) {
            throw new IllegalArgumentException(
                "La nueva contraseña debe tener al menos 8 caracteres."
            );
        }

        if (!nueva.equals(confirmar)) {
            throw new IllegalArgumentException(
                "Las contraseñas nuevas no coinciden."
            );
        }

        estudiante.setContrasena(nueva);
    }

        estudiante.setNombre(nombre.trim());
        estudiante.setCarrera(carrera.trim());
        datos.guardar();
    }

    public Lider registrarLider(String nombre, String carnet, int edad, String nombreClub, String correo, String contrasena) {
        if (nombre == null || nombre.trim().isEmpty()
            || carnet == null || carnet.trim().isEmpty()
            || nombreClub == null || nombreClub.trim().isEmpty()
            || correo == null || correo.trim().isEmpty()
            || contrasena == null || contrasena.isEmpty()) {
        throw new IllegalArgumentException(
            "Completa todos los campos."
        );
    }
    nombre = nombre.trim();
    carnet = carnet.trim();
    nombreClub = nombreClub.trim();
    correo = correo.trim();

    if (edad < 1 || edad > 120) {
        throw new IllegalArgumentException(
            "Ingresa una edad válida."
        );
    }

    if (!correo.matches(
        "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {

        throw new IllegalArgumentException(
            "Ingresa un correo electrónico válido."
        );
    }

    if (!correo.toLowerCase().endsWith("@uvg.edu.gt")) {
        throw new IllegalArgumentException(
            "Usa tu correo institucional (@uvg.edu.gt)."
        );
    }

    if (contrasena.length() < 8) {
        throw new IllegalArgumentException(
            "La contraseña debe tener al menos 8 caracteres."
        );
    }

    for (Usuario usuario : datos.getUsuarios()) {

        if (usuario.getCorreo().equalsIgnoreCase(correo)) {
            throw new IllegalArgumentException(
                "Ya existe una cuenta con ese correo."
            );
        }
        if (usuario instanceof Lider
                && ((Lider) usuario).getCarnet()
                    .equalsIgnoreCase(carnet)) {

            throw new IllegalArgumentException(
                "Ya existe un líder con ese carnet."
            );
        }
    }
    
    for (Club club : datos.getClubes()) {

        if (club.getNombre().equalsIgnoreCase(nombreClub)) {
            throw new IllegalArgumentException(
                "Ya existe un club con ese nombre."
            );
        }
    }

    // 8. Crear el nuevo club.
    Club nuevoClub = new Club(
        nombreClub,
        "Descripción pendiente de completar.",
        "General",
        "Por definir",
        "Por definir"
    );

    // 9. Crear el nuevo líder.
    Lider nuevoLider = new Lider(
        nombre,
        correo,
        contrasena,
        nuevoClub,
        carnet,
        edad
    );
        datos.getClubes().add(nuevoClub);
        datos.getUsuarios().add(nuevoLider);
        return nuevoLider;
    }

    /** Crea una cuenta de estudiante nueva. Lanza una excepción si algún dato no es válido. */
    public Estudiante registrarEstudiante(String nombre, String correo, String carrera, String contrasena) {
        // 1. Ningún campo vacío
        if (nombre == null || nombre.trim().isEmpty()
                || correo == null || correo.trim().isEmpty()
                || carrera == null || carrera.trim().isEmpty()
                || contrasena == null || contrasena.isEmpty()) {
            throw new IllegalArgumentException("Completa todos los campos.");
        }

        nombre = nombre.trim();
        correo = correo.trim();
        carrera = carrera.trim();

        // 2. Correo institucional
        if (!correo.toLowerCase().endsWith("@uvg.edu.gt")) {
            throw new IllegalArgumentException("Usa tu correo institucional (@uvg.edu.gt).");
        }

        // 3. Correo no repetido
        for (Usuario usuario : datos.getUsuarios()) {
            if (usuario.getCorreo().equalsIgnoreCase(correo)) {
                throw new IllegalArgumentException("Ya existe una cuenta con ese correo.");
            }
        }

        // 4. Contraseña de al menos 8 caracteres
        if (contrasena.length() < 8) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 8 caracteres.");
        }

        // 5. Crear y guardar el estudiante
        Estudiante nuevo = new Estudiante(nombre, correo, carrera, contrasena);
        datos.getUsuarios().add(nuevo);
        datos.guardar();
        return nuevo;
    }
}
