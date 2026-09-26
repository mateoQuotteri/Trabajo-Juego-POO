package RPG.Vista;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import RPG.Personajes.Enemigo;
import RPG.Personajes.Personaje;
import RPG.Personajes.ImplEnemigo.Goblin;
import RPG.Personajes.ImplEnemigo.Jefe;
import RPG.Personajes.ImplEnemigo.JefeFinal;
import RPG.Personajes.ImplEnemigo.Slime;
import RPG.Personajes.ImplPersonaje.Arquero;
import RPG.Personajes.ImplPersonaje.Curador;
import RPG.Personajes.ImplPersonaje.Guerrero;
import RPG.Personajes.ImplPersonaje.Mago;

// Pantalla de Batalla (documento, "mínimo 4 pantallas"). Es la MISMA
// pantalla para cualquier piso de la Torre, "con alguna diferencia" según
// el piso (pedido del grupo, 26/09/2026): si el piso es 5 o 10 aparece un
// Jefe/Jefe Final en vez de un enemigo común, y el título avisa que es
// una pelea de jefe.
//
// Maqueta navegable (ver checklist-tpo.txt): arma una party de ejemplo
// (un personaje de cada clase, mismos nombres que usa Main.java) y deja
// jugar acciones REALES (atacar/habilidad básica/poderosa/Defender, y un
// botón aparte para el turno del enemigo) sobre objetos Personaje/Enemigo
// de verdad -- no son botones que no hacen nada. Lo que NO tiene todavía
// es el motor de turnos completo (orden automático por Velocidad, ronda
// por ronda como MotorDeCombate): acá se elige a mano qué personaje actúa
// y cuándo le toca al enemigo, para poder definir el diseño de la
// pantalla primero. Conectarla a MotorDeCombate de verdad queda para más
// adelante.
public class PantallaBatalla extends JPanel {

    private final VentanaPrincipal ventana;

    private Personaje[] party;
    private Enemigo enemigoActual;

    private JLabel tituloPiso;
    private JLabel nombreEnemigoLabel;
    private JProgressBar barraVidaEnemigo;

    private JLabel[] etiquetasNombreParty;
    private JProgressBar[] barrasVidaParty;
    private JProgressBar[] barrasManaParty;

    private JComboBox<String> selectorPersonaje;
    private JButton botonAtacar;
    private JButton botonBasica;
    private JButton botonPoderosa;
    private JButton botonDefender;
    private JButton botonTurnoEnemigo;
    private JTextArea areaLog;

    public PantallaBatalla(VentanaPrincipal ventana) {
        this.ventana = ventana;
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(18, 18, 24));

        add(armarPanelEnemigo(), BorderLayout.NORTH);
        add(armarPanelParty(), BorderLayout.CENTER);
        add(armarPanelInferior(), BorderLayout.SOUTH);

        armarBatallaParaPiso(1); // para que la pantalla no arranque vacía
    }

    // ---------- Armado de la UI (una sola vez, en el constructor) ----------
    private JPanel armarPanelEnemigo() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        tituloPiso = new JLabel("Piso 1");
        tituloPiso.setFont(new Font("SansSerif", Font.BOLD, 20));
        tituloPiso.setForeground(Color.WHITE);
        tituloPiso.setAlignmentX(Component.CENTER_ALIGNMENT);

        nombreEnemigoLabel = new JLabel("Enemigo");
        nombreEnemigoLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        nombreEnemigoLabel.setForeground(new Color(220, 120, 120));
        nombreEnemigoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        barraVidaEnemigo = new JProgressBar();
        barraVidaEnemigo.setStringPainted(true);
        barraVidaEnemigo.setForeground(new Color(180, 40, 40));
        barraVidaEnemigo.setMaximumSize(new Dimension(420, 22));
        barraVidaEnemigo.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(tituloPiso);
        panel.add(Box.createVerticalStrut(5));
        panel.add(nombreEnemigoLabel);
        panel.add(Box.createVerticalStrut(5));
        panel.add(barraVidaEnemigo);
        return panel;
    }

    private JPanel armarPanelParty() {
        JPanel panel = new JPanel(new GridLayout(4, 1, 5, 5));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 60, 10, 60));

        etiquetasNombreParty = new JLabel[4];
        barrasVidaParty = new JProgressBar[4];
        barrasManaParty = new JProgressBar[4];

        for (int i = 0; i < 4; i++) {
            JPanel fila = new JPanel(new GridLayout(1, 3, 10, 0));
            fila.setOpaque(false);

            etiquetasNombreParty[i] = new JLabel();
            etiquetasNombreParty[i].setForeground(Color.WHITE);

            barrasVidaParty[i] = new JProgressBar();
            barrasVidaParty[i].setStringPainted(true);
            barrasVidaParty[i].setForeground(new Color(40, 160, 60));

            barrasManaParty[i] = new JProgressBar();
            barrasManaParty[i].setStringPainted(true);
            barrasManaParty[i].setForeground(new Color(50, 100, 200));

            fila.add(etiquetasNombreParty[i]);
            fila.add(barrasVidaParty[i]);
            fila.add(barrasManaParty[i]);
            panel.add(fila);
        }
        return panel;
    }

    private JPanel armarPanelInferior() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setOpaque(false);

        // 2 filas fijas (BoxLayout Y_AXIS) en vez de un único FlowLayout:
        // así los botones de abajo (Turno del Enemigo / Volver a la Torre)
        // siempre quedan visibles en su propia fila, sin depender de que
        // el ancho de la ventana alcance para que FlowLayout los "envuelva"
        // a una segunda línea.
        JPanel panelAcciones = new JPanel();
        panelAcciones.setOpaque(false);
        panelAcciones.setLayout(new BoxLayout(panelAcciones, BoxLayout.Y_AXIS));

        JPanel filaPersonajeYHabilidades = new JPanel();
        filaPersonajeYHabilidades.setOpaque(false);
        JPanel filaTurnoYVolver = new JPanel();
        filaTurnoYVolver.setOpaque(false);

        selectorPersonaje = new JComboBox<String>();
        selectorPersonaje.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                actualizarBotonDefender();
            }
        });

        botonAtacar = new JButton("Atacar");
        botonBasica = new JButton("Habilidad Básica");
        botonPoderosa = new JButton("Habilidad Poderosa");
        botonDefender = new JButton("Defender");
        botonTurnoEnemigo = new JButton("Turno del Enemigo");
        JButton botonVolver = new JButton("Volver a la Torre");

        botonAtacar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { accionAtacar(); }
        });
        botonBasica.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { accionHabilidad(true); }
        });
        botonPoderosa.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { accionHabilidad(false); }
        });
        botonDefender.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { accionDefender(); }
        });
        botonTurnoEnemigo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { accionTurnoEnemigo(); }
        });
        botonVolver.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { ventana.mostrarPantalla(VentanaPrincipal.TORRE); }
        });

        filaPersonajeYHabilidades.add(new JLabel("Personaje:"));
        filaPersonajeYHabilidades.add(selectorPersonaje);
        filaPersonajeYHabilidades.add(botonAtacar);
        filaPersonajeYHabilidades.add(botonBasica);
        filaPersonajeYHabilidades.add(botonPoderosa);
        filaPersonajeYHabilidades.add(botonDefender);

        filaTurnoYVolver.add(botonTurnoEnemigo);
        filaTurnoYVolver.add(botonVolver);

        panelAcciones.add(filaPersonajeYHabilidades);
        panelAcciones.add(filaTurnoYVolver);

        areaLog = new JTextArea(8, 40);
        areaLog.setEditable(false);
        areaLog.setLineWrap(true);
        areaLog.setWrapStyleWord(true);
        JScrollPane scrollLog = new JScrollPane(areaLog);

        panel.add(panelAcciones, BorderLayout.NORTH);
        panel.add(scrollLog, BorderLayout.CENTER);
        return panel;
    }

    // ---------- Armar una batalla nueva para un piso (lo llama VentanaPrincipal) ----------
    public void armarBatallaParaPiso(int piso) {
        party = new Personaje[] {
                new Guerrero("Mateo"),
                new Mago("Jose"),
                new Arquero("Conrado"),
                new Curador("Evelyn")
        };

        if (piso == 5) {
            enemigoActual = new Jefe();
        } else if (piso == 10) {
            enemigoActual = new JefeFinal();
        } else if (piso % 2 == 0) {
            enemigoActual = new Goblin("Goblin");
        } else {
            enemigoActual = new Slime("Slime");
        }

        selectorPersonaje.removeAllItems();
        for (Personaje p : party) {
            selectorPersonaje.addItem(p.getNombre() + " (" + p.getClass().getSimpleName() + ")");
        }

        String tituloTexto = "Piso " + piso;
        if (piso == 5 || piso == 10) {
            tituloTexto += " -- ¡BATALLA DE JEFE!";
        }
        tituloPiso.setText(tituloTexto);

        areaLog.setText("");
        agregarLog("Aparece " + enemigoActual.getNombre() + ".");

        habilitarBotonesDeAccion(true);
        actualizarVistas();
    }

    // ---------- Acciones de los botones ----------
    private void accionAtacar() {
        Personaje actor = obtenerPersonajeSeleccionado();
        if (!puedeActuar(actor)) {
            return;
        }
        actor.atacar(enemigoActual);
        agregarLog(actor.getNombre() + " atacó a " + enemigoActual.getNombre()
                + ". Vida restante: " + enemigoActual.getVidaActual());
        actualizarVistas();
        revisarFinDeBatalla();
    }

    // esBasica: true = habilidad básica, false = habilidad poderosa (mismo
    // botón "tipo" de acción, solo cambia el costo/nombre -- polimorfismo,
    // igual que en MotorDeCombate.jugarTurnoDePersonaje).
    private void accionHabilidad(boolean esBasica) {
        Personaje actor = obtenerPersonajeSeleccionado();
        if (!puedeActuar(actor)) {
            return;
        }
        int costo = esBasica ? actor.getCostoHabilidadBasica() : actor.getCostoHabilidadPoderosa();
        String nombreHabilidad = esBasica ? actor.getNombreHabilidadBasica() : actor.getNombreHabilidadPoderosa();
        if (!actor.puedeUsarHabilidad(costo)) {
            agregarLog(actor.getNombre() + " no tiene maná suficiente para " + nombreHabilidad + ".");
            return;
        }

        // El Curador cura/revive a un aliado (al que tenga menos vida); el
        // resto ataca al enemigo actual (mismo criterio que Curador.java /
        // MotorDeCombate.elegirAliadoObjetivo).
        if (actor instanceof Curador) {
            Personaje objetivo = elegirAliadoConMenosVida();
            if (esBasica) {
                actor.usarHabilidadBasica(objetivo);
            } else {
                actor.usarHabilidadPoderosa(objetivo);
            }
            agregarLog(actor.getNombre() + " usó " + nombreHabilidad + " sobre " + objetivo.getNombre() + ".");
        } else {
            if (esBasica) {
                actor.usarHabilidadBasica(enemigoActual);
            } else {
                actor.usarHabilidadPoderosa(enemigoActual);
            }
            agregarLog(actor.getNombre() + " usó " + nombreHabilidad + " sobre " + enemigoActual.getNombre()
                    + ". Vida restante: " + enemigoActual.getVidaActual());
        }
        actualizarVistas();
        revisarFinDeBatalla();
    }

    private void accionDefender() {
        Personaje actor = obtenerPersonajeSeleccionado();
        if (!puedeActuar(actor) || !(actor instanceof Guerrero)) {
            return;
        }
        ((Guerrero) actor).defender();
        agregarLog(actor.getNombre() + " se puso en guardia.");
    }

    private void accionTurnoEnemigo() {
        if (enemigoActual == null || !enemigoActual.estaVivo()) {
            return;
        }
        Personaje objetivo = elegirAliadoVivoAlAzar();
        if (objetivo == null) {
            return;
        }
        int vidaAntes = objetivo.getVidaActual();
        boolean aturdidoAntes = objetivo.estaAturdido();
        boolean envenenadoAntes = objetivo.estaEnvenenado();

        enemigoActual.actuar(objetivo); // también narra por consola, ver Enemigo.ejecutarAccionSobre

        StringBuilder mensaje = new StringBuilder(enemigoActual.getNombre() + " actúa sobre " + objetivo.getNombre() + ".");
        if (objetivo.getVidaActual() < vidaAntes) {
            mensaje.append(" Pierde ").append(vidaAntes - objetivo.getVidaActual()).append(" de vida.");
        }
        if (!aturdidoAntes && objetivo.estaAturdido()) {
            mensaje.append(" Queda aturdido.");
        }
        if (!envenenadoAntes && objetivo.estaEnvenenado()) {
            mensaje.append(" Queda envenenado.");
        }
        agregarLog(mensaje.toString());
        actualizarVistas();
        revisarFinDeBatalla();
    }

    // ---------- Helpers ----------
    private boolean puedeActuar(Personaje actor) {
        if (actor == null || !actor.estaVivo()) {
            agregarLog("Ese personaje no puede actuar.");
            return false;
        }
        if (enemigoActual == null || !enemigoActual.estaVivo()) {
            return false;
        }
        return true;
    }

    private Personaje obtenerPersonajeSeleccionado() {
        int indice = selectorPersonaje.getSelectedIndex();
        if (indice < 0 || party == null || indice >= party.length) {
            return null;
        }
        return party[indice];
    }

    // Igual criterio que usa el Curador en una batalla real (ver
    // Curador.java / MotorDeCombate.elegirAliadoObjetivo): entre los
    // vivos, el que tenga menos vida actual.
    private Personaje elegirAliadoConMenosVida() {
        Personaje elegido = null;
        for (Personaje p : party) {
            if (p.estaVivo() && (elegido == null || p.getVidaActual() < elegido.getVidaActual())) {
                elegido = p;
            }
        }
        return elegido;
    }

    // Mismo patrón que MotorDeCombate.elegirPersonajeVivoAlAzar().
    private Personaje elegirAliadoVivoAlAzar() {
        int cantidadVivos = 0;
        for (Personaje p : party) {
            if (p.estaVivo()) {
                cantidadVivos++;
            }
        }
        if (cantidadVivos == 0) {
            return null;
        }
        int elegido = (int) (Math.random() * cantidadVivos);
        int contados = 0;
        for (Personaje p : party) {
            if (p.estaVivo()) {
                if (contados == elegido) {
                    return p;
                }
                contados++;
            }
        }
        return null;
    }

    private void actualizarBotonDefender() {
        Personaje actor = obtenerPersonajeSeleccionado();
        botonDefender.setEnabled(actor instanceof Guerrero && actor.estaVivo()
                && enemigoActual != null && enemigoActual.estaVivo());
    }

    private void habilitarBotonesDeAccion(boolean habilitados) {
        botonAtacar.setEnabled(habilitados);
        botonBasica.setEnabled(habilitados);
        botonPoderosa.setEnabled(habilitados);
        botonTurnoEnemigo.setEnabled(habilitados);
        actualizarBotonDefender();
    }

    private void revisarFinDeBatalla() {
        if (!enemigoActual.estaVivo()) {
            agregarLog("¡" + enemigoActual.getNombre() + " derrotado! Victoria.");
            habilitarBotonesDeAccion(false);
        } else if (!partyTieneVivos()) {
            agregarLog("Toda la party cayó. Game over.");
            habilitarBotonesDeAccion(false);
        }
    }

    private boolean partyTieneVivos() {
        for (Personaje p : party) {
            if (p.estaVivo()) {
                return true;
            }
        }
        return false;
    }

    private void agregarLog(String linea) {
        areaLog.append(linea + "\n");
        areaLog.setCaretPosition(areaLog.getDocument().getLength());
    }

    private void actualizarVistas() {
        barraVidaEnemigo.setMaximum(enemigoActual.getVidaMaxima());
        barraVidaEnemigo.setValue(enemigoActual.getVidaActual());
        barraVidaEnemigo.setString("Vida: " + enemigoActual.getVidaActual() + "/" + enemigoActual.getVidaMaxima());
        nombreEnemigoLabel.setText(enemigoActual.getNombre() + " (Nivel " + enemigoActual.getNivel() + ")");

        for (int i = 0; i < party.length; i++) {
            Personaje p = party[i];
            String estado = p.estaVivo() ? "" : " [caído]";
            etiquetasNombreParty[i].setText(p.getNombre() + " (" + p.getClass().getSimpleName() + ")" + estado);

            barrasVidaParty[i].setMaximum(p.getVidaMaxima());
            barrasVidaParty[i].setValue(p.getVidaActual());
            barrasVidaParty[i].setString("Vida: " + p.getVidaActual() + "/" + p.getVidaMaxima());

            barrasManaParty[i].setMaximum(p.getManaMaximo());
            barrasManaParty[i].setValue(p.getManaActual());
            barrasManaParty[i].setString("Maná: " + p.getManaActual() + "/" + p.getManaMaximo());
        }
    }
}
