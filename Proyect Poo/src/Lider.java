import java.io.Serializable;

public class Lider extends Usuario implements Serializable {
    private static final long serialVersionUID = 1L;
    private Club clubAdministrado;
    private String carnet;
    private int edad;

    public Lider(String nombre, String correo, String contrasena, Club clubAdministrado) {
        this(nombre, correo, contrasena, clubAdministrado, "", 0);
    }

    public Lider(String nombre, String correo, String contrasena, Club clubAdministrado, String carnet, int edad) {
        super(nombre, correo, contrasena);
        this.clubAdministrado = clubAdministrado;
        this.carnet = carnet;
        this.edad = edad;
    }

    public Club getClubAdministrado() {
        return clubAdministrado;
    }

    public void setClubAdministrado(Club clubAdministrado) {
        this.clubAdministrado = clubAdministrado;
    }

    public String getCarnet() {
        return carnet;
    }

    public void setCarnet(String carnet) {
        this.carnet = carnet;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    public void agregarAnuncio(Anuncio anuncio) {
        clubAdministrado.agregarAnuncio(anuncio);
    }

    public void cambiarhorario(String nuevoHorario) {
        clubAdministrado.setHorario(nuevoHorario);
    }
}
