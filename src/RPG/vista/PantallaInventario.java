package RPG.vista;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// Pantalla de Inventario: 6 espacios (los ítems equipados no ocupan lugar).
// SIGUE SIENDO MAQUETA: muestra ítems fijos de ejemplo porque todavía no
// existen las clases Inventario / Item del modelo (próximo paso).
// Se abre desde el Resumen de batalla (entre piso y piso) y vuelve ahí.
public class PantallaInventario extends JPanel {

    private static final String[] ITEMS_DE_EJEMPLO = {
            "Poción de vida (+30 vida)",
            "Poción de maná (+20 maná)",
            "Pergamino de turno extra",
            "", "", ""
    };

    public PantallaInventario(final VentanaPrincipal ventana) {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(32, 32, 42));

        JLabel titulo = new JLabel("Inventario (6 espacios) - MAQUETA", SwingConstants.CENTER);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 22));
        titulo.setForeground(Color.WHITE);
        titulo.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        add(titulo, BorderLayout.NORTH);

        JPanel grillaSlots = new JPanel(new GridLayout(2, 3, 15, 15));
        grillaSlots.setBorder(BorderFactory.createEmptyBorder(10, 60, 10, 60));
        grillaSlots.setOpaque(false);

        for (String item : ITEMS_DE_EJEMPLO) {
            boolean vacio = item.isEmpty();
            JButton slot = new JButton(vacio ? "(vacío)" : item);
            slot.setEnabled(!vacio);
            grillaSlots.add(slot);
        }
        add(grillaSlots, BorderLayout.CENTER);

        JLabel nota = new JLabel("<html><center>Los ítems equipados (ej. Espada de hierro del Guerrero) "
                + "no ocupan espacio acá.</center></html>", SwingConstants.CENTER);
        nota.setForeground(Color.LIGHT_GRAY);

        JButton botonVolver = new JButton("Volver al resumen");
        botonVolver.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ventana.mostrarPantalla(VentanaPrincipal.RESUMEN);
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
