package RPG.Vista;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// Pantalla de Inicio (documento, "mínimo 4 pantallas"). No decide nada por
// su cuenta: solo muestra los botones y le avisa a VentanaPrincipal qué
// eligió el jugador (MVC).
public class PantallaMenu extends JPanel {

    public PantallaMenu(final VentanaPrincipal ventana) {
        setLayout(new GridBagLayout());
        setBackground(new Color(28, 28, 38));

        JPanel columna = new JPanel();
        columna.setOpaque(false);
        columna.setLayout(new BoxLayout(columna, BoxLayout.Y_AXIS));

        JLabel titulo = new JLabel("EL ASCENSO A LA TORRE");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 30));
        titulo.setForeground(Color.WHITE);
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitulo = new JLabel("TPO de Programación Orientada a Objetos - UADE");
        subtitulo.setFont(new Font("SansSerif", Font.PLAIN, 13));
        subtitulo.setForeground(Color.LIGHT_GRAY);
        subtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton botonNuevaPartida = new JButton("Nueva Partida");
        JButton botonCargarPartida = new JButton("Cargar Partida");
        JButton botonSalir = new JButton("Salir");

        for (JButton boton : new JButton[] { botonNuevaPartida, botonCargarPartida, botonSalir }) {
            boton.setAlignmentX(Component.CENTER_ALIGNMENT);
            boton.setMaximumSize(new Dimension(220, 35));
            boton.setFont(boton.getFont().deriveFont(14f));
        }

        botonNuevaPartida.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ventana.mostrarPantalla(VentanaPrincipal.TORRE);
            }
        });

        botonCargarPartida.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(ventana,
                        "Cargar Partida todavía no está implementado (falta el guardado en JSON, ver checklist-tpo.txt).",
                        "No disponible todavía", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        botonSalir.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        columna.add(titulo);
        columna.add(Box.createVerticalStrut(5));
        columna.add(subtitulo);
        columna.add(Box.createVerticalStrut(40));
        columna.add(botonNuevaPartida);
        columna.add(Box.createVerticalStrut(10));
        columna.add(botonCargarPartida);
        columna.add(Box.createVerticalStrut(10));
        columna.add(botonSalir);

        add(columna);
    }
}
