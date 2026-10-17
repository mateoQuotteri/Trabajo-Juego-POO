package RPG.vista;

import RPG.controlador.ControladorDeBatalla;
import RPG.modelo.personajes.Personaje;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

// PANTALLA 4: Inventario + resumen de la party.
// Arriba: las estadísticas de cada héroe (tabla hecha con un GridLayout).
// Abajo: los 6 espacios del inventario.
public class PantallaInventario extends JPanel {

    private static final String[] COLUMNAS = {
            "Nombre", "Clase", "Nivel", "Vida", "Maná", "Atq. Fís.", "Atq. Mág.", "Defensa", "Velocidad", "Exp."
    };

    private ControladorDeBatalla controlador;
    private JPanel tablaParty;

    public PantallaInventario(final VentanaPrincipal ventana, ControladorDeBatalla controlador) {
        this.controlador = controlador;
        setLayout(new BorderLayout(10, 15));
        setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        // ----- Estadísticas de la party (se llena en actualizar()) -----
        tablaParty = new JPanel(new GridLayout(5, COLUMNAS.length, 5, 5));
        tablaParty.setBorder(BorderFactory.createTitledBorder("Party"));
        add(tablaParty, BorderLayout.NORTH);

        // ----- Inventario de 6 espacios -----
        // TODO: todavía no existen las clases del inventario. Documento:
        //  - Inventario de 6 espacios, solo consumibles (Poción de vida,
        //    Poción de maná, Pergamino de experiencia, Pergamino de turno extra).
        //  - Equipables: 2 por clase, NO ocupan espacio en el inventario.
        // Cuando existan, estos botones deberían mostrar los ítems reales.
        JPanel espacios = new JPanel(new GridLayout(2, 3, 10, 10));
        espacios.setBorder(BorderFactory.createTitledBorder("Inventario (6 espacios)"));
        for (int i = 0; i < 6; i++) {
            JButton espacio = new JButton("(vacío)");
            espacio.setEnabled(false);
            espacios.add(espacio);
        }
        add(espacios, BorderLayout.CENTER);

        JButton botonVolver = new JButton("Volver al resumen");
        botonVolver.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ventana.irAResumen();
            }
        });
        JPanel abajo = new JPanel();
        abajo.add(botonVolver);
        add(abajo, BorderLayout.SOUTH);
    }

    // La llama la ventana al entrar a esta pantalla: vuelve a armar la
    // tabla con los datos actuales de cada personaje.
    public void actualizar() {
        tablaParty.removeAll();
        for (int i = 0; i < COLUMNAS.length; i++) {
            tablaParty.add(new JLabel(COLUMNAS[i]));
        }
        Personaje[] miembros = controlador.getParty().getMiembros();
        for (int i = 0; i < miembros.length; i++) {
            Personaje p = miembros[i];
            tablaParty.add(new JLabel(p.getNombre()));
            tablaParty.add(new JLabel(p.getNombreClase()));
            tablaParty.add(new JLabel("" + p.getNivel()));
            tablaParty.add(new JLabel(p.getVidaActual() + "/" + p.getVidaMaxima()));
            tablaParty.add(new JLabel("" + p.getManaMaximo()));
            tablaParty.add(new JLabel("" + p.getAtaqueFisico()));
            tablaParty.add(new JLabel("" + p.getAtaqueMagico()));
            tablaParty.add(new JLabel("" + p.getDefensa()));
            tablaParty.add(new JLabel("" + p.getVelocidad()));
            tablaParty.add(new JLabel("" + p.getExperiencia()));
        }
        tablaParty.revalidate(); // avisa a Swing que cambió el contenido
        tablaParty.repaint();
    }
}
