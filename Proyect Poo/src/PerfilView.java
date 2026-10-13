
import java.awt.*;
import javax.swing.*;

public class PerfilView extends JPanel {

    public PerfilView(Estudiante estudiante,
                      UsuarioController controlador,
                      DatosAplicacion datos,
                      Runnable volver) {

        setLayout(new GridLayout(0, 2, 10, 10));
        setBorder(BorderFactory.createEmptyBorder(25, 40, 25, 40));

        JLabel titulo = new JLabel("Mi perfil");
        titulo.setFont(Estilos.TITULO);

        JTextField nombre = new JTextField(estudiante.getNombre());
        JTextField carrera = new JTextField(estudiante.getCarrera());

        JPasswordField actual = new JPasswordField();
        JPasswordField nueva = new JPasswordField();
        JPasswordField confirmar = new JPasswordField();

        add(titulo);
        add(new JLabel(""));

        add(new JLabel("Nombre:"));
        add(nombre);

        add(new JLabel("Carrera:"));
        add(carrera);

        add(new JLabel("Contraseña actual:"));
        add(actual);

        add(new JLabel("Nueva contraseña:"));
        add(nueva);

        add(new JLabel("Confirmar contraseña:"));
        add(confirmar);

        JButton guardar = new JButton("Guardar cambios");
        JButton cancelar = new JButton("Volver");

        add(guardar);
        add(cancelar);

        guardar.addActionListener(e -> {
            try {
                controlador.actualizarPerfil(
                    estudiante,
                    nombre.getText(),
                    carrera.getText(),
                    new String(actual.getPassword()),
                    new String(nueva.getPassword()),
                    new String(confirmar.getPassword())
                );

                JOptionPane.showMessageDialog(
                    this, "Perfil actualizado correctamente."
                );

                volver.run();

            } catch (RuntimeException ex) {
                JOptionPane.showMessageDialog(
                    this, ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE
                );
            }
        });

        cancelar.addActionListener(e -> volver.run());
    }
}
