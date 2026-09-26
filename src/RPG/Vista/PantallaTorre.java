package RPG.Vista;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// Pantalla de los Pisos de la Torre (documento, sección "Estructura del
// juego: la Torre"): elegir a qué piso subir. Piso 5 y Piso 10 son los
// jefes ("Rey Slime" y "Guardián Ancestral", ver RPG.Personajes.
// ImplEnemigo). Elegir un piso arma y muestra la Pantalla de Batalla para
// ese piso -- es la MISMA pantalla para todos los pisos, "con alguna
// diferencia" según cuál se elija (ver PantallaBatalla.
// armarBatallaParaPiso).
public class PantallaTorre extends JPanel {

    public PantallaTorre(final VentanaPrincipal ventana) {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(38, 33, 28));

        JLabel titulo = new JLabel("Elegí un piso de la Torre", SwingConstants.CENTER);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 22));
        titulo.setForeground(Color.WHITE);
        titulo.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        add(titulo, BorderLayout.NORTH);

        JPanel grillaPisos = new JPanel(new GridLayout(5, 2, 10, 10));
        grillaPisos.setOpaque(false);
        grillaPisos.setBorder(BorderFactory.createEmptyBorder(10, 60, 10, 60));

        for (int piso = 1; piso <= 10; piso++) {
            final int pisoElegido = piso;
            String etiqueta = "Piso " + piso;
            if (piso == 5) {
                etiqueta = "Piso 5 - Rey Slime (Jefe)";
            } else if (piso == 10) {
                etiqueta = "Piso 10 - Guardián Ancestral (Jefe Final)";
            }
            JButton botonPiso = new JButton(etiqueta);
            if (piso == 5 || piso == 10) {
                botonPiso.setFont(botonPiso.getFont().deriveFont(Font.BOLD));
                botonPiso.setForeground(new Color(160, 20, 20));
            }
            botonPiso.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    ventana.empezarBatallaEnPiso(pisoElegido);
                }
            });
            grillaPisos.add(botonPiso);
        }
        add(grillaPisos, BorderLayout.CENTER);

        JPanel panelInferior = new JPanel();
        panelInferior.setOpaque(false);
        JButton botonInventario = new JButton("Inventario");
        JButton botonMenu = new JButton("Volver al Menú");

        botonInventario.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ventana.mostrarPantalla(VentanaPrincipal.INVENTARIO);
            }
        });
        botonMenu.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ventana.mostrarPantalla(VentanaPrincipal.MENU);
            }
        });

        panelInferior.add(botonInventario);
        panelInferior.add(botonMenu);
        add(panelInferior, BorderLayout.SOUTH);
    }
}
