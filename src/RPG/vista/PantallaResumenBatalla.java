package RPG.vista;

import RPG.controlador.ControladorDeBatalla;
import RPG.dto.ResumenBatallaDTO;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

// PANTALLA 3: Resumen de batalla.
// Dice si se ganó o se perdió y deja seguir: siguiente piso, inventario
// o volver al inicio.
public class PantallaResumenBatalla extends JPanel {

    private ControladorDeBatalla controlador;

    private JLabel etiquetaResultado;
    private JLabel etiquetaDetalle;
    private JLabel etiquetaExperiencia;
    private JLabel etiquetaRecompensas;
    private JButton botonSiguiente;
    private JButton botonInventario;
    private JButton botonGuardar;

    public PantallaResumenBatalla(final VentanaPrincipal ventana, ControladorDeBatalla controlador) {
        this.controlador = controlador;
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(60, 40, 40, 40));

        JPanel textos = new JPanel(new BorderLayout());
        etiquetaResultado = new JLabel("", JLabel.CENTER);
        etiquetaResultado.setFont(new Font("SansSerif", Font.BOLD, 28));
        etiquetaDetalle = new JLabel("", JLabel.CENTER);
        etiquetaExperiencia = new JLabel("", JLabel.CENTER);
        etiquetaRecompensas = new JLabel("", JLabel.CENTER);
        textos.add(etiquetaResultado, BorderLayout.NORTH);
        textos.add(etiquetaDetalle, BorderLayout.CENTER);
        JPanel detalles = new JPanel(new BorderLayout());
        detalles.add(etiquetaExperiencia, BorderLayout.NORTH);
        detalles.add(etiquetaRecompensas, BorderLayout.SOUTH);
        textos.add(detalles, BorderLayout.SOUTH);
        add(textos, BorderLayout.CENTER);

        botonSiguiente = new JButton("Siguiente piso");
        botonInventario = new JButton("Inventario");
        botonGuardar = new JButton("Guardar partida");
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
        botonGuardar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    PantallaResumenBatalla.this.controlador.guardarPartida();
                    JOptionPane.showMessageDialog(ventana, "Partida guardada. "
                            + "Al cargarla continuarás en el siguiente piso.");
                } catch (IOException | IllegalStateException ex) {
                    JOptionPane.showMessageDialog(ventana, ex.getMessage(),
                            "No se pudo guardar la partida", JOptionPane.ERROR_MESSAGE);
                }
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
        botones.add(botonGuardar);
        botones.add(botonInicio);
        add(botones, BorderLayout.SOUTH);
    }

    // La llama la ventana al entrar a esta pantalla.
    public void actualizar() {
        ResumenBatallaDTO resumen = controlador.getResumenBatallaDTO();
        etiquetaResultado.setText(resumen.getResultado());
        etiquetaDetalle.setText(resumen.getDetalle());

        botonSiguiente.setVisible(resumen.puedeContinuar());
        botonInventario.setVisible(resumen.puedeContinuar());
        botonGuardar.setVisible(resumen.puedeContinuar());
        String experiencia = resumen.getExperiencia();
        if (experiencia.isEmpty()) {
            etiquetaExperiencia.setText("");
        } else {
            etiquetaExperiencia.setText("<html>" + experiencia.replace("\n", "<br>") + "</html>");
        }
        String recompensas = resumen.getRecompensas();
        etiquetaRecompensas.setText(recompensas.isEmpty()
                ? "Oro acumulado: " + resumen.getOro()
                : "<html>" + recompensas.replace("\n", "<br>") + "</html>");
    }
}
