package RPG.vista;

import RPG.modelo.Partida;
import RPG.modelo.ResultadoDeBatalla;
import RPG.modelo.personajes.Personaje;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// Pantalla de Resumen de batalla. Aparece al terminar cada pelea y muestra:
//   - el resultado (victoria / derrota / juego ganado)
//   - la experiencia ganada y quién subió de nivel
//   - el ESTADO DE LA PARTY (nivel, vida, experiencia de cada uno)
// Con esto cubre 2 de las pantallas mínimas de la cátedra en una sola:
// "estado de personaje/party" y "resultados/resumen".
//
// Botones según el caso:
//   ganó y quedan pisos -> Siguiente piso / Inventario / Guardar / Menú
//   ganó el piso 10 o perdió -> solo Volver al menú
public class PantallaResumenBatalla extends JPanel {

    private VentanaPrincipal ventana;

    private JLabel etiquetaTitulo;
    private JLabel etiquetaDetalle;
    private JTextArea areaParty;
    private JButton botonSiguiente;
    private JButton botonInventario;
    private JButton botonGuardar;

    public PantallaResumenBatalla(VentanaPrincipal ventana) {
        this.ventana = ventana;
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(30, 30, 40));
        setBorder(BorderFactory.createEmptyBorder(20, 80, 20, 80));

        JPanel superior = new JPanel();
        superior.setOpaque(false);
        superior.setLayout(new BoxLayout(superior, BoxLayout.Y_AXIS));
        etiquetaTitulo = new JLabel("Resumen");
        etiquetaTitulo.setFont(new Font("SansSerif", Font.BOLD, 26));
        etiquetaTitulo.setForeground(Color.WHITE);
        etiquetaTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        etiquetaDetalle = new JLabel(" ");
        etiquetaDetalle.setFont(new Font("SansSerif", Font.PLAIN, 14));
        etiquetaDetalle.setForeground(Color.LIGHT_GRAY);
        etiquetaDetalle.setAlignmentX(Component.CENTER_ALIGNMENT);
        superior.add(etiquetaTitulo);
        superior.add(Box.createVerticalStrut(8));
        superior.add(etiquetaDetalle);
        add(superior, BorderLayout.NORTH);

        areaParty = new JTextArea();
        areaParty.setEditable(false);
        areaParty.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane scroll = new JScrollPane(areaParty);
        scroll.setBorder(BorderFactory.createTitledBorder("Estado de la party"));
        add(scroll, BorderLayout.CENTER);

        botonSiguiente = new JButton("Siguiente piso");
        botonInventario = new JButton("Inventario");
        botonGuardar = new JButton("Guardar partida");
        JButton botonMenu = new JButton("Volver al menú");

        botonSiguiente.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                PantallaResumenBatalla.this.ventana.jugarPisoActual();
            }
        });
        botonInventario.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                PantallaResumenBatalla.this.ventana.mostrarPantalla(VentanaPrincipal.INVENTARIO);
            }
        });
        botonGuardar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(PantallaResumenBatalla.this,
                        "El guardado en archivo se implementa más adelante.",
                        "No disponible todavía", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        botonMenu.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                PantallaResumenBatalla.this.ventana.mostrarPantalla(VentanaPrincipal.MENU);
            }
        });

        JPanel botones = new JPanel();
        botones.setOpaque(false);
        botones.add(botonSiguiente);
        botones.add(botonInventario);
        botones.add(botonGuardar);
        botones.add(botonMenu);
        add(botones, BorderLayout.SOUTH);
    }

    // La llama VentanaPrincipal con el resultado que armó el controlador.
    public void mostrar(ResultadoDeBatalla resultado, Partida partida) {
        if (resultado == null) {
            return;
        }
        if (!resultado.ganoLaParty()) {
            etiquetaTitulo.setText("GAME OVER");
            etiquetaTitulo.setForeground(new Color(230, 90, 90));
            etiquetaDetalle.setText("La party cayó en el piso " + resultado.getPiso() + " contra "
                    + resultado.getNombreEnemigo() + " (" + resultado.getRondasJugadas() + " rondas).");
        } else if (partida.estaGanada()) {
            etiquetaTitulo.setText("¡VICTORIA! Completaron la Torre");
            etiquetaTitulo.setForeground(new Color(255, 210, 90));
            etiquetaDetalle.setText(resultado.getNombreEnemigo() + " derrotado en " + resultado.getRondasJugadas()
                    + " rondas. +" + resultado.getExperienciaGanada() + " de experiencia.");
        } else {
            etiquetaTitulo.setText("Piso " + resultado.getPiso() + " superado");
            etiquetaTitulo.setForeground(new Color(120, 220, 120));
            etiquetaDetalle.setText(resultado.getNombreEnemigo() + " derrotado en " + resultado.getRondasJugadas()
                    + " rondas. +" + resultado.getExperienciaGanada() + " de experiencia para cada personaje vivo.");
        }

        String texto = "";
        if (!resultado.getSubidasDeNivel().isEmpty()) {
            texto += resultado.getSubidasDeNivel() + "\n\n";
        }
        for (Personaje p : partida.getParty().getMiembros()) {
            texto += String.format("%-10s %-9s Nv %2d   Vida %3d/%-3d   Exp %3d/%-4d %s%n",
                    p.getNombre(), p.getNombreClase(), p.getNivel(), p.getVidaActual(), p.getVidaMaxima(),
                    p.getExperiencia(), p.getExperienciaParaSiguienteNivel(), p.estaVivo() ? "" : "[CAÍDO]");
        }
        areaParty.setText(texto);

        boolean sigue = !partida.estaTerminada();
        botonSiguiente.setVisible(sigue);
        botonInventario.setVisible(sigue);
        botonGuardar.setVisible(sigue);
        if (sigue) {
            botonSiguiente.setText("Siguiente: piso " + partida.getPisoActual());
        }
    }
}
