package RPG.vista;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

// PANTALLA 1: Inicio con el menú.
// Iniciar partida / Cargar partida / Cerrar juego.
public class PantallaInicio extends JPanel {

    public PantallaInicio(final VentanaPrincipal ventana) {
        setLayout(new GridBagLayout()); // centra lo que se agregue
        setBackground(new Color(28, 28, 38));

        JPanel menu = new JPanel();
        menu.setOpaque(false);
        menu.setLayout(new BoxLayout(menu, BoxLayout.Y_AXIS)); // uno abajo del otro

        JLabel titulo = new JLabel("EL ASCENSO A LA TORRE");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 30));
        titulo.setForeground(Color.WHITE);
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton botonIniciar = new JButton("Iniciar partida");
        JButton botonCargar = new JButton("Cargar partida");
        JButton botonCerrar = new JButton("Cerrar juego");
        botonIniciar.setAlignmentX(Component.CENTER_ALIGNMENT);
        botonCargar.setAlignmentX(Component.CENTER_ALIGNMENT);
        botonCerrar.setAlignmentX(Component.CENTER_ALIGNMENT);
        botonIniciar.setMaximumSize(new Dimension(200, 35));
        botonCargar.setMaximumSize(new Dimension(200, 35));
        botonCerrar.setMaximumSize(new Dimension(200, 35));

        botonIniciar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ventana.iniciarPartida();
            }
        });

        // TODO: cargar una partida guardada (documento: archivo JSON con el
        // piso, la party y el inventario).
        botonCargar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(ventana, "Todavía no está implementado.");
            }
        });

        botonCerrar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        menu.add(titulo);
        menu.add(Box.createVerticalStrut(40)); // espacio vacío
        menu.add(botonIniciar);
        menu.add(Box.createVerticalStrut(10));
        menu.add(botonCargar);
        menu.add(Box.createVerticalStrut(10));
        menu.add(botonCerrar);
        add(menu);
    }
}
