package RPG.vista;

import RPG.controlador.ControladorDeBatalla;
import RPG.modelo.Batalla;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

// PANTALLA 3: Resumen de batalla.
// Dice si se ganó o se perdió y deja seguir: siguiente piso, inventario
// o volver al inicio.
public class PantallaResumenBatalla extends JPanel {

    private ControladorDeBatalla controlador;

    private JLabel etiquetaResultado;
    private JLabel etiquetaDetalle;
    private JButton botonSiguiente;
    private JButton botonInventario;

    public PantallaResumenBatalla(final VentanaPrincipal ventana, ControladorDeBatalla controlador) {
        this.controlador = controlador;
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(60, 40, 40, 40));

        JPanel textos = new JPanel(new BorderLayout());
        etiquetaResultado = new JLabel("", JLabel.CENTER);
        etiquetaResultado.setFont(new Font("SansSerif", Font.BOLD, 28));
        etiquetaDetalle = new JLabel("", JLabel.CENTER);
        textos.add(etiquetaResultado, BorderLayout.NORTH);
        textos.add(etiquetaDetalle, BorderLayout.CENTER);
        add(textos, BorderLayout.CENTER);

        // TODO: mostrar acá la experiencia ganada, quién subió de nivel y
        // las recompensas (oro / ítems) cuando estén hechas.

        botonSiguiente = new JButton("Siguiente piso");
        botonInventario = new JButton("Inventario");
        JButton botonInicio = new JButton("Volver al inicio");

        botonSiguiente.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                PantallaResumenBatalla.this.controlador.subirDePiso();
                ventana.irABatalla();
            }
        });
        botonInventario.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ventana.irAInventario();
            }
        });
        botonInicio.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ventana.irAInicio();
            }
        });

        JPanel botones = new JPanel();
        botones.add(botonSiguiente);
        botones.add(botonInventario);
        botones.add(botonInicio);
        add(botones, BorderLayout.SOUTH);
    }

    // La llama la ventana al entrar a esta pantalla.
    public void actualizar() {
        Batalla batalla = controlador.getBatalla();
        boolean puedeSeguir = false;

        if (!batalla.ganoLaParty()) {
            etiquetaResultado.setText("GAME OVER");
            etiquetaDetalle.setText("La party cayó en el piso " + batalla.getPiso());
        } else if (controlador.ganoElJuego()) {
            etiquetaResultado.setText("¡GANASTE EL JUEGO!");
            etiquetaDetalle.setText("Derrotaste al " + batalla.getEnemigo().getNombre());
        } else {
            etiquetaResultado.setText("Piso " + batalla.getPiso() + " superado");
            etiquetaDetalle.setText(batalla.getEnemigo().getNombre() + " derrotado en "
                    + batalla.getNumeroRonda() + " rondas");
            puedeSeguir = true;
        }

        botonSiguiente.setVisible(puedeSeguir);
        botonInventario.setVisible(puedeSeguir);
    }
}
