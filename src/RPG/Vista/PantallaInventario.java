package RPG.Vista;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// Pantalla de Inventario (documento, sección "Ítems e inventario"): 6
// espacios para consumibles y equipables sin poner (los ítems ya
// equipados NO ocupan espacio acá).
//
// Maqueta con datos de ejemplo -- todavía no existe la clase Inventario/
// Item real (ver checklist-tpo.txt, FALTA - FASE B), así que por ahora
// los 6 espacios muestran nombres fijos de ejemplo, no un inventario real
// conectado a la party.
public class PantallaInventario extends JPanel {

    private static final String[] ITEMS_DE_EJEMPLO = {
            "Poción de vida (+30 vida)",
            "Poción de maná (+20 maná)",
            "Espada de hierro (+8 ataque físico)",
            "", "", ""
    };

    public PantallaInventario(final VentanaPrincipal ventana) {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(32, 32, 42));

        JLabel titulo = new JLabel("Inventario (6 espacios)", SwingConstants.CENTER);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 22));
        titulo.setForeground(Color.WHITE);
        titulo.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        add(titulo, BorderLayout.NORTH);

        JPanel grillaSlots = new JPanel(new GridLayout(2, 3, 15, 15));
        grillaSlots.setBorder(BorderFactory.createEmptyBorder(10, 60, 10, 60));
        grillaSlots.setOpaque(false);

        for (String itemDeEjemplo : ITEMS_DE_EJEMPLO) {
            boolean vacio = itemDeEjemplo.isEmpty();
            JButton slot = new JButton(vacio ? "(vacío)" : itemDeEjemplo);
            slot.setEnabled(!vacio);
            slot.setFont(slot.getFont().deriveFont(13f));
            grillaSlots.add(slot);
        }
        add(grillaSlots, BorderLayout.CENTER);

        JLabel nota = new JLabel("<html><center>Los ítems equipados no ocupan espacio acá "
                + "(ejemplo Guerrero: Espada de hierro +8 ataque físico, Escudo de madera +8 defensa).</center></html>",
                SwingConstants.CENTER);
        nota.setForeground(Color.LIGHT_GRAY);

        JButton botonVolver = new JButton("Volver a la Torre");
        botonVolver.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ventana.mostrarPantalla(VentanaPrincipal.TORRE);
            }
        });

        JPanel panelInferior = new JPanel(new BorderLayout());
        panelInferior.setOpaque(false);
        panelInferior.add(nota, BorderLayout.CENTER);
        JPanel panelBoton = new JPanel();
        panelBoton.setOpaque(false);
        panelBoton.add(botonVolver);
        panelInferior.add(panelBoton, BorderLayout.SOUTH);

        add(panelInferior, BorderLayout.SOUTH);
    }
}
