package RPG.vista;

import RPG.controlador.ControladorDeBatalla;
import RPG.modelo.Batalla;
import RPG.modelo.Party;
import RPG.modelo.enemigos.Enemigo;
import RPG.modelo.habilidades.Habilidad;
import RPG.modelo.personajes.Personaje;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// Pantalla de Batalla (VISTA de MVC).
//
// CAMBIOS 16/10 (lo más grande de esta tanda en la Vista):
// - Ya NO tiene lógica de juego adentro. Antes esta pantalla elegía el
//   enemigo según el piso, decidía a quién curaba el Curador, hacía jugar
//   al enemigo y revisaba si alguien ganó. Todo eso pasó al
//   ControladorDeBatalla / modelo. Acá solo quedan: dibujar, y avisar al
//   controlador qué botón se tocó.
// - Respeta el ORDEN POR VELOCIDAD: antes el jugador elegía a mano qué
//   personaje actuaba y tocaba "Turno del Enemigo". Ahora la pantalla
//   muestra de quién es el turno (lo decide el modelo) y el enemigo juega
//   solo cuando le toca.
// - Muestra los estados (veneno, aturdido, defendiendo...) y los nombres
//   reales de las habilidades con su costo; los botones de habilidad se
//   bloquean si no alcanza el maná (documento: "la habilidad se muestra
//   bloqueada").
// - Si la habilidad es de apoyo (Curador), el jugador elige el aliado en
//   un selector (antes curaba "al que tuviera menos vida" sin preguntar, y
//   Revivir nunca podía apuntar a un caído).
public class PantallaBatalla extends JPanel {

    private VentanaPrincipal ventana;
    private ControladorDeBatalla controlador;

    private JLabel etiquetaTitulo;
    private JLabel etiquetaOrden;
    private JLabel etiquetaEnemigo;
    private JProgressBar barraVidaEnemigo;

    private JLabel[] etiquetasParty;
    private JProgressBar[] barrasVida;
    private JProgressBar[] barrasMana;

    private JLabel etiquetaTurno;
    private JButton botonAtacar;
    private JButton botonBasica;
    private JButton botonPoderosa;
    private JButton botonDefender;
    private JComboBox<String> selectorAliado;
    private JTextArea areaLog;

    public PantallaBatalla(VentanaPrincipal ventana, ControladorDeBatalla controlador) {
        this.ventana = ventana;
        this.controlador = controlador;
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(18, 18, 24));

        add(armarPanelEnemigo(), BorderLayout.NORTH);
        add(armarPanelParty(), BorderLayout.CENTER);
        add(armarPanelInferior(), BorderLayout.SOUTH);
    }

    // ======================================================================
    // ARMADO DE LA INTERFAZ (una sola vez)
    // ======================================================================

    private JPanel armarPanelEnemigo() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 5, 20));

        etiquetaTitulo = crearEtiqueta("Piso", 20, Font.BOLD, Color.WHITE);
        etiquetaOrden = crearEtiqueta("Orden de turnos", 12, Font.PLAIN, Color.LIGHT_GRAY);
        etiquetaEnemigo = crearEtiqueta("Enemigo", 16, Font.BOLD, new Color(230, 120, 120));

        barraVidaEnemigo = new JProgressBar();
        barraVidaEnemigo.setStringPainted(true);
        barraVidaEnemigo.setForeground(new Color(180, 40, 40));
        barraVidaEnemigo.setMaximumSize(new Dimension(420, 22));
        barraVidaEnemigo.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(etiquetaTitulo);
        panel.add(Box.createVerticalStrut(4));
        panel.add(etiquetaOrden);
        panel.add(Box.createVerticalStrut(8));
        panel.add(etiquetaEnemigo);
        panel.add(Box.createVerticalStrut(4));
        panel.add(barraVidaEnemigo);
        return panel;
    }

    private JPanel armarPanelParty() {
        int filas = Party.MAXIMO_MIEMBROS;
        JPanel panel = new JPanel(new GridLayout(filas, 1, 5, 5));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(5, 40, 5, 40));

        etiquetasParty = new JLabel[filas];
        barrasVida = new JProgressBar[filas];
        barrasMana = new JProgressBar[filas];

        for (int i = 0; i < filas; i++) {
            JPanel fila = new JPanel(new GridLayout(1, 3, 10, 0));
            fila.setOpaque(false);

            etiquetasParty[i] = new JLabel();
            etiquetasParty[i].setForeground(Color.WHITE);

            barrasVida[i] = new JProgressBar();
            barrasVida[i].setStringPainted(true);
            barrasVida[i].setForeground(new Color(40, 160, 60));

            barrasMana[i] = new JProgressBar();
            barrasMana[i].setStringPainted(true);
            barrasMana[i].setForeground(new Color(50, 100, 200));

            fila.add(etiquetasParty[i]);
            fila.add(barrasVida[i]);
            fila.add(barrasMana[i]);
            panel.add(fila);
        }
        return panel;
    }

    private JPanel armarPanelInferior() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setOpaque(false);

        JPanel panelAcciones = new JPanel();
        panelAcciones.setOpaque(false);
        panelAcciones.setLayout(new BoxLayout(panelAcciones, BoxLayout.Y_AXIS));

        etiquetaTurno = crearEtiqueta("Turno de ...", 15, Font.BOLD, new Color(255, 210, 90));

        JPanel filaBotones = new JPanel();
        filaBotones.setOpaque(false);
        botonAtacar = new JButton("Atacar");
        botonBasica = new JButton("Habilidad básica");
        botonPoderosa = new JButton("Habilidad poderosa");
        botonDefender = new JButton("Defender");
        filaBotones.add(botonAtacar);
        filaBotones.add(botonBasica);
        filaBotones.add(botonPoderosa);
        filaBotones.add(botonDefender);

        JPanel filaObjetivo = new JPanel();
        filaObjetivo.setOpaque(false);
        JLabel etiquetaAliado = new JLabel("Objetivo aliado (curar/revivir):");
        etiquetaAliado.setForeground(Color.LIGHT_GRAY);
        selectorAliado = new JComboBox<String>();
        JButton botonVolver = new JButton("Volver a la Torre");
        filaObjetivo.add(etiquetaAliado);
        filaObjetivo.add(selectorAliado);
        filaObjetivo.add(Box.createHorizontalStrut(30));
        filaObjetivo.add(botonVolver);

        botonAtacar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mostrarResultado(controlador.atacar());
            }
        });
        botonBasica.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mostrarResultado(controlador.usarHabilidad(false, getAliadoSeleccionado()));
            }
        });
        botonPoderosa.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mostrarResultado(controlador.usarHabilidad(true, getAliadoSeleccionado()));
            }
        });
        botonDefender.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mostrarResultado(controlador.defender());
            }
        });
        botonVolver.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ventana.mostrarPantalla(VentanaPrincipal.TORRE);
            }
        });

        panelAcciones.add(etiquetaTurno);
        panelAcciones.add(filaBotones);
        panelAcciones.add(filaObjetivo);

        areaLog = new JTextArea(9, 40);
        areaLog.setEditable(false);
        areaLog.setLineWrap(true);
        areaLog.setWrapStyleWord(true);

        panel.add(panelAcciones, BorderLayout.NORTH);
        panel.add(new JScrollPane(areaLog), BorderLayout.CENTER);
        return panel;
    }

    private JLabel crearEtiqueta(String texto, int tamanio, int estilo, Color color) {
        JLabel etiqueta = new JLabel(texto);
        etiqueta.setFont(new Font("SansSerif", estilo, tamanio));
        etiqueta.setForeground(color);
        etiqueta.setAlignmentX(Component.CENTER_ALIGNMENT);
        return etiqueta;
    }

    // ======================================================================
    // LO QUE PIDE LA VENTANA / LOS BOTONES
    // ======================================================================

    public void empezarBatalla(int piso) {
        areaLog.setText("");
        String log = controlador.iniciarBatallaDePrueba(piso);

        selectorAliado.removeAllItems();
        for (Personaje p : controlador.getBatalla().getParty().getMiembros()) {
            selectorAliado.addItem(p.getNombre() + " (" + p.getNombreClase() + ")");
        }
        mostrarResultado(log);
    }

    // Agrega al log lo que devolvió el controlador y redibuja todo.
    private void mostrarResultado(String texto) {
        if (texto != null && !texto.isEmpty()) {
            areaLog.append(texto);
            areaLog.setCaretPosition(areaLog.getDocument().getLength());
        }
        actualizarPantalla();
    }

    private Personaje getAliadoSeleccionado() {
        int indice = selectorAliado.getSelectedIndex();
        Personaje[] miembros = controlador.getBatalla().getParty().getMiembros();
        if (indice < 0 || indice >= miembros.length) {
            return null;
        }
        return miembros[indice];
    }

    // ======================================================================
    // DIBUJAR EL ESTADO ACTUAL (solo lee el modelo, no lo modifica)
    // ======================================================================

    private void actualizarPantalla() {
        Batalla batalla = controlador.getBatalla();
        Enemigo enemigo = batalla.getEnemigo();

        String titulo = "Piso " + batalla.getPiso() + " - Ronda " + batalla.getNumeroRonda();
        if (enemigo.esJefe()) {
            titulo += "  ¡BATALLA DE JEFE!";
        }
        etiquetaTitulo.setText(titulo);
        etiquetaOrden.setText("Orden de la ronda: " + batalla.describirOrdenDeLaRonda());
        etiquetaEnemigo.setText(enemigo.getNombre() + " (nivel " + enemigo.getNivel() + ")");
        barraVidaEnemigo.setMaximum(enemigo.getVidaMaxima());
        barraVidaEnemigo.setValue(enemigo.getVidaActual());
        barraVidaEnemigo.setString("Vida: " + enemigo.getVidaActual() + "/" + enemigo.getVidaMaxima());

        Personaje enTurno = controlador.getPersonajeEnTurno();
        Personaje[] miembros = batalla.getParty().getMiembros();
        for (int i = 0; i < etiquetasParty.length; i++) {
            boolean hayMiembro = i < miembros.length;
            etiquetasParty[i].setVisible(hayMiembro);
            barrasVida[i].setVisible(hayMiembro);
            barrasMana[i].setVisible(hayMiembro);
            if (!hayMiembro) {
                continue;
            }
            Personaje p = miembros[i];
            String marca = (p == enTurno) ? "▶ " : "   ";
            String caido = p.estaVivo() ? "" : " [CAÍDO]";
            etiquetasParty[i].setText(marca + p.getNombre() + " (" + p.getNombreClase() + " nv " + p.getNivel() + ")"
                    + caido + p.describirEstados());
            barrasVida[i].setMaximum(p.getVidaMaxima());
            barrasVida[i].setValue(p.getVidaActual());
            barrasVida[i].setString("Vida: " + p.getVidaActual() + "/" + p.getVidaMaxima());
            barrasMana[i].setMaximum(p.getManaMaximo());
            barrasMana[i].setValue(p.getManaActual());
            barrasMana[i].setString("Maná: " + p.getManaActual() + "/" + p.getManaMaximo());
        }

        actualizarBotones(enTurno);
    }

    private void actualizarBotones(Personaje enTurno) {
        if (enTurno == null) {
            etiquetaTurno.setText("Batalla terminada. Volvé a la Torre para elegir otro piso.");
            botonAtacar.setEnabled(false);
            botonBasica.setEnabled(false);
            botonPoderosa.setEnabled(false);
            botonDefender.setEnabled(false);
            selectorAliado.setEnabled(false);
            return;
        }
        Habilidad basica = enTurno.getHabilidadBasica();
        Habilidad poderosa = enTurno.getHabilidadPoderosa();

        etiquetaTurno.setText("Turno de " + enTurno.getNombre() + " (" + enTurno.getNombreClase() + ")");
        botonAtacar.setEnabled(true);
        botonBasica.setText(basica.getNombre() + " (" + basica.getCostoMana() + " MP)");
        botonPoderosa.setText(poderosa.getNombre() + " (" + poderosa.getCostoMana() + " MP)");
        botonBasica.setEnabled(controlador.puedeUsarHabilidad(false));
        botonPoderosa.setEnabled(controlador.puedeUsarHabilidad(true));
        botonDefender.setEnabled(enTurno.puedeDefender());
        selectorAliado.setEnabled(basica.esDeApoyo() || poderosa.esDeApoyo());
    }
}
