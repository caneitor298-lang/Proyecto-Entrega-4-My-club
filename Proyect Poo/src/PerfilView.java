import java.awt.*;
import javax.swing.*;

public class PerfilView extends JPanel {

    public PerfilView(Estudiante estudiante,
                      UsuarioController controlador,
                      DatosAplicacion datos,
                      Runnable volver) {

        setLayout(new BorderLayout());
        setBackground(new Color(245, 247, 250));

       
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 218, 230), 1),
                BorderFactory.createEmptyBorder(24, 28, 24, 28)
        ));

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.anchor = GridBagConstraints.WEST;

        int fila = 0;

        // Título
        JLabel titulo = new JLabel("Mi perfil");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titulo.setForeground(new Color(20, 35, 70));
        c.gridy = fila++;
        c.insets = new Insets(0, 0, 18, 0);
        form.add(titulo, c);


        // Campos
        JTextField nombre = crearCampoTexto(estudiante.getNombre());
        JTextField carrera = crearCampoTexto(estudiante.getCarrera());
        JPasswordField actual = crearCampoPassword();
        JPasswordField nueva = crearCampoPassword();
        JPasswordField confirmar = crearCampoPassword();

        fila = agregarCampo(form, c, fila, "Nombre completo", nombre);
        fila = agregarCampo(form, c, fila, "Carrera", carrera);

        // Separador
        JSeparator sep = new JSeparator();
        c.gridy = fila++;
        c.insets = new Insets(6, 0, 12, 0);
        form.add(sep, c);

        JLabel secPass = new JLabel("Cambiar contraseña");
        secPass.setFont(new Font("Segoe UI", Font.BOLD, 15));
        secPass.setForeground(new Color(20, 35, 70));
        c.gridy = fila++;
        c.insets = new Insets(0, 0, 12, 0);
        form.add(secPass, c);


        fila = agregarCampo(form, c, fila, "Contraseña actual", actual);
        fila = agregarCampo(form, c, fila, "Nueva contraseña", nueva);
        fila = agregarCampo(form, c, fila, "Confirmar nueva contraseña", confirmar);

        
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        botones.setOpaque(false);

        JButton guardar = new JButton("Guardar cambios");
        guardar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        guardar.setBackground(new Color(52, 105, 200));
        guardar.setForeground(Color.WHITE);
        guardar.setFocusPainted(false);
        guardar.setBorder(BorderFactory.createEmptyBorder(9, 16, 9, 16));
        guardar.setPreferredSize(new Dimension(150, 36));
        guardar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        guardar.setOpaque(true);

        JButton cancelar = new JButton("Volver");
        cancelar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        cancelar.setBackground(Color.WHITE);
        cancelar.setForeground(new Color(52, 105, 200));
        cancelar.setFocusPainted(false);
        cancelar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(52, 105, 200), 1),
                BorderFactory.createEmptyBorder(8, 14, 8, 14)
        ));
        cancelar.setPreferredSize(new Dimension(100, 36));
        cancelar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        cancelar.setOpaque(true);

        botones.add(guardar);
        botones.add(cancelar);

        c.gridy = fila++;
        c.insets = new Insets(12, 0, 0, 0);
        form.add(botones, c);

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
                JOptionPane.showMessageDialog(this, "Perfil actualizado correctamente.",
                        "Éxito", JOptionPane.INFORMATION_MESSAGE);
                volver.run();
            } catch (RuntimeException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        cancelar.addActionListener(e -> volver.run());

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));
        wrapper.add(form, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(wrapper);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(new Color(245, 247, 250));
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        add(scroll, BorderLayout.CENTER);
    }

    private int agregarCampo(JPanel form, GridBagConstraints c, int fila, String etiqueta, JComponent campo) {
        JLabel lbl = new JLabel(etiqueta);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lbl.setForeground(new Color(90, 100, 120));
        c.gridy = fila++;
        c.insets = new Insets(0, 0, 3, 0);
        form.add(lbl, c);

        c.gridy = fila++;
        c.insets = new Insets(0, 0, 10, 0);
        form.add(campo, c);
        return fila;
    }

    private JTextField crearCampoTexto(String valor) {
        JTextField tf = new JTextField(valor);
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        // Altura fija, ancho flexible (se ajusta a la ventana)
        tf.setPreferredSize(new Dimension(100, 34));
        tf.setMinimumSize(new Dimension(80, 34));
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 218, 230), 1),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        return tf;
    }

    private JPasswordField crearCampoPassword() {
        JPasswordField pf = new JPasswordField();
        pf.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        pf.setPreferredSize(new Dimension(100, 34));
        pf.setMinimumSize(new Dimension(80, 34));
        pf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 218, 230), 1),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        return pf;
    }
}