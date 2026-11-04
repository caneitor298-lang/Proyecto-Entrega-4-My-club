
import java.awt.*;
import java.util.Arrays;
import java.util.function.Consumer;
import javax.swing.*;

public class RegistroLiderView extends JPanel {
    public RegistroLiderView(UsuarioController controlador, Consumer<Usuario> alRegistrar, Runnable volver) {
        setLayout(new GridBagLayout());
        setBackground(Estilos.FONDO);
        JPanel formulario = new JPanel(new GridLayout(0, 1, 6, 5));
        formulario.setBorder(
            BorderFactory.createEmptyBorder(18, 28, 18, 28)
        );

        JLabel titulo = new JLabel(
            "Registro de líder de club",
            SwingConstants.CENTER
        );

        titulo.setFont(Estilos.TITULO);
        titulo.setForeground(Estilos.AZUL_OSCURO);
        formulario.add(titulo);

        formulario.add(new JLabel("Nombre completo"));
        JTextField nombre = new JTextField();
        formulario.add(nombre);

        formulario.add(new JLabel("Carnet"));
        JTextField carnet = new JTextField();
        formulario.add(carnet);

        formulario.add(new JLabel("Edad"));
        JTextField edad = new JTextField();
        formulario.add(edad);

        formulario.add(new JLabel("Nombre del club que administrará"));
        JTextField nombreClub = new JTextField();
        formulario.add(nombreClub);

        formulario.add(new JLabel("Correo institucional"));
        JTextField correo = new JTextField();
        formulario.add(correo);

        formulario.add(new JLabel("Contraseña (mínimo 8 caracteres)"));
        JPasswordField contrasena = new JPasswordField();
        formulario.add(contrasena);

        formulario.add(new JLabel("Confirmar contraseña"));
        JPasswordField confirmar = new JPasswordField();
        formulario.add(confirmar);

        JButton crear = new JButton("Crear cuenta de líder");
        Estilos.estilizarBoton(crear);
        formulario.add(crear);

        JButton regresar = new JButton("Volver al inicio de sesión");
        formulario.add(regresar);

        JLabel estado = new JLabel(" ");
        estado.setForeground(Color.RED);
        formulario.add(estado);

        crear.addActionListener(e -> {
            char[] clave = contrasena.getPassword();
            char[] clave2 = confirmar.getPassword();
            try {

                if (!Arrays.equals(clave, clave2)) {
                    throw new IllegalArgumentException(
                        "Las contraseñas no coinciden."
                    );
                }
                int edadIngresada;

                try {
                    edadIngresada = Integer.parseInt(
                        edad.getText().trim()
                    );
                } catch (NumberFormatException ex) {
                    throw new IllegalArgumentException(
                        "La edad debe ser un número entero."
                    );
                }

                Lider nuevo = controlador.registrarLider(
                    nombre.getText(),
                    carnet.getText(),
                    edadIngresada,
                    nombreClub.getText(),
                    correo.getText(),
                    new String(clave)
                );
                alRegistrar.accept(nuevo);

            } catch (RuntimeException ex) {

                estado.setText(ex.getMessage());

            } finally {

                Arrays.fill(clave, '\0');
                Arrays.fill(clave2, '\0');

                contrasena.setText("");
                confirmar.setText("");
            }
        });

        regresar.addActionListener(e -> volver.run());
        JScrollPane desplazamiento = new JScrollPane(formulario);
        desplazamiento.setBorder(null);
        desplazamiento.setPreferredSize(new Dimension(470, 620));

        add(desplazamiento);
    }
    
}