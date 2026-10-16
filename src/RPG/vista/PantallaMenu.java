package RPG.vista;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// Pantalla de Inicio / Menú (acordada con el profe):
//   Iniciar partida -> arranca en el piso 1
//   Cargar partida  -> abre la pantalla de Partidas guardadas
//   Cerrar juego
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

        JLabel subtitulo = new JLabel("TPO Paradigma Orientado a Objetos - UADE - Grupo 16");
        subtitulo.setFont(new Font("SansSerif", Font.PLAIN, 13));
        subtitulo.setForeground(Color.LIGHT_GRAY);
        subtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton botonIniciar = new JButton("Iniciar partida");
        JButton botonCargar = new JButton("Cargar partida");
        JButton botonCerrar = new JButton("Cerrar juego");

        JButton[] botones = {botonIniciar, botonCargar, botonCerrar};
        for (JButton boton : botones) {
            boton.setAlignmentX(Component.CENTER_ALIGNMENT);
            boton.setMaximumSize(new Dimension(220, 35));
            boton.setFont(boton.getFont().deriveFont(14f));
        }

        botonIniciar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ventana.iniciarPartida();
            }
        });
        botonCargar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ventana.mostrarPantalla(VentanaPrincipal.PARTIDAS_GUARDADAS);
            }
        });
        botonCerrar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        columna.add(titulo);
        columna.add(Box.createVerticalStrut(5));
        columna.add(subtitulo);
        columna.add(Box.createVerticalStrut(40));
        columna.add(botonIniciar);
        columna.add(Box.createVerticalStrut(10));
        columna.add(botonCargar);
        columna.add(Box.createVerticalStrut(10));
        columna.add(botonCerrar);

        add(columna);
    }
}
