package RPG.vista;

import RPG.controlador.ControladorDeBatalla;
import RPG.dto.CombatienteDTO;
import RPG.dto.EstadoBatallaDTO;
import RPG.dto.EstadoInventarioDTO;
import RPG.dto.ItemDTO;
import RPG.dto.PersonajeDTO;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

// PANTALLA 2: Batalla.
// Solo muestra el estado y le avisa al controlador qué botón se tocó.
// No decide nada del juego (MVC).
public class PantallaBatalla extends JPanel {

    private VentanaPrincipal ventana;
    private ControladorDeBatalla controlador;

    private JLabel etiquetaPiso;
    private JLabel etiquetaEnemigo;
    private JProgressBar barraEnemigo;

    private JLabel[] nombresParty = new JLabel[4];
    private JProgressBar[] barrasVida = new JProgressBar[4];
    private JProgressBar[] barrasMana = new JProgressBar[4];

    private JLabel etiquetaTurno;
    private JButton botonAtacar;
    private JButton botonBasica;
    private JButton botonPoderosa;
    private JButton botonDefender;
    private JButton botonItem;
    private JComboBox<String> selectorAliado;
    private JButton botonResumen;
    private JTextArea log;

    public PantallaBatalla(VentanaPrincipal ventana, ControladorDeBatalla controlador) {
        this.ventana = ventana;
        this.controlador = controlador;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // ----- Arriba: piso y enemigo -----
        JPanel arriba = new JPanel(new GridLayout(3, 1));
        etiquetaPiso = new JLabel("Piso", JLabel.CENTER);
        etiquetaPiso.setFont(new Font("SansSerif", Font.BOLD, 18));
        etiquetaEnemigo = new JLabel("Enemigo", JLabel.CENTER);
        barraEnemigo = new JProgressBar();
        barraEnemigo.setStringPainted(true);
        barraEnemigo.setForeground(new Color(180, 40, 40));
        arriba.add(etiquetaPiso);
        arriba.add(etiquetaEnemigo);
        arriba.add(barraEnemigo);
        add(arriba, BorderLayout.NORTH);

        // ----- Centro: la party (nombre, vida, maná) -----
        JPanel centro = new JPanel(new GridLayout(4, 3, 10, 5));
        for (int i = 0; i < 4; i++) {
            nombresParty[i] = new JLabel();
            barrasVida[i] = new JProgressBar();
            barrasVida[i].setStringPainted(true);
            barrasVida[i].setForeground(new Color(40, 150, 60));
            barrasMana[i] = new JProgressBar();
            barrasMana[i].setStringPainted(true);
            barrasMana[i].setForeground(new Color(50, 100, 200));
            centro.add(nombresParty[i]);
            centro.add(barrasVida[i]);
            centro.add(barrasMana[i]);
        }
        add(centro, BorderLayout.CENTER);

        // ----- Abajo: botones y log -----
        JPanel abajo = new JPanel(new BorderLayout(5, 5));
        JPanel botones = new JPanel();
        etiquetaTurno = new JLabel("Turno de ...");
        botonAtacar = new JButton("Atacar");
        botonBasica = new JButton("Habilidad básica");
        botonPoderosa = new JButton("Habilidad poderosa");
        botonDefender = new JButton("Defender");
        botonItem = new JButton("Usar ítem");
        selectorAliado = new JComboBox<String>();
        botonResumen = new JButton("Ver resumen");
        botones.add(etiquetaTurno);
        botones.add(botonAtacar);
        botones.add(botonBasica);
        botones.add(botonPoderosa);
        botones.add(botonDefender);
        botones.add(botonItem);
        botones.add(new JLabel("Curar a:"));
        botones.add(selectorAliado);
        botones.add(botonResumen);

        log = new JTextArea(8, 40);
        log.setEditable(false);
        abajo.add(botones, BorderLayout.NORTH);
        abajo.add(new JScrollPane(log), BorderLayout.CENTER);
        add(abajo, BorderLayout.SOUTH);

        // ----- Qué hace cada botón: le avisa al controlador -----
        botonAtacar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mostrar(PantallaBatalla.this.controlador.atacar());
            }
        });
        botonBasica.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mostrar(PantallaBatalla.this.controlador.usarHabilidad(false, aliadoElegido()));
            }
        });
        botonPoderosa.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mostrar(PantallaBatalla.this.controlador.usarHabilidad(true, aliadoElegido()));
            }
        });
        botonDefender.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mostrar(PantallaBatalla.this.controlador.defender());
            }
        });
        botonItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                elegirItemParaUsar();
            }
        });
        botonResumen.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                PantallaBatalla.this.ventana.irAResumen();
            }
        });
    }

    // La llama la ventana al entrar a esta pantalla.
    public void empezar() {
        log.setText("");
        selectorAliado.removeAllItems();
        EstadoInventarioDTO estado = controlador.getEstadoInventarioDTO();
        for (PersonajeDTO personaje : estado.getPersonajes()) {
            selectorAliado.addItem(personaje.getNombre());
        }
        mostrar(controlador.empezarBatalla());
    }

    private int aliadoElegido() {
        return selectorAliado.getSelectedIndex();
    }

    private void elegirItemParaUsar() {
        EstadoBatallaDTO estado = controlador.getEstadoBatallaDTO();
        List<ItemDTO> items = estado.getItems();
        if (items.isEmpty()) {
            mostrar("No hay ítems disponibles.\n");
            return;
        }

        Object[] opcionesItem = new Object[items.size()];
        for (int i = 0; i < items.size(); i++) {
            ItemDTO item = items.get(i);
            opcionesItem[i] = (i + 1) + ". " + item.getNombre() + " — " + item.getDescripcion();
        }
        Object seleccionItem = JOptionPane.showInputDialog(
                this,
                "Elegí el ítem que querés usar:",
                "Usar ítem",
                JOptionPane.QUESTION_MESSAGE,
                null,
                opcionesItem,
                opcionesItem[0]);
        if (seleccionItem == null) {
            return;
        }
        int indiceItem = -1;
        for (int i = 0; i < opcionesItem.length; i++) {
            if (opcionesItem[i].equals(seleccionItem)) {
                indiceItem = i;
                break;
            }
        }
        if (indiceItem < 0) {
            return;
        }

        List<PersonajeDTO> vivos = new ArrayList<PersonajeDTO>();
        for (PersonajeDTO personaje : estado.getPersonajes()) {
            if (personaje.estaVivo()) {
                vivos.add(personaje);
            }
        }
        if (vivos.isEmpty()) {
            mostrar("No hay personajes vivos para recibir el ítem.\n");
            return;
        }
        Object[] opcionesObjetivo = new Object[vivos.size()];
        for (int i = 0; i < vivos.size(); i++) {
            PersonajeDTO personaje = vivos.get(i);
            opcionesObjetivo[i] = personaje.getNombre() + " (Vida "
                    + personaje.getVidaActual() + "/" + personaje.getVidaMaxima()
                    + ", Maná " + personaje.getManaActual() + "/" + personaje.getManaMaximo() + ")";
        }
        Object seleccionObjetivo = JOptionPane.showInputDialog(
                this,
                "Elegí el personaje objetivo:",
                "Usar " + items.get(indiceItem).getNombre(),
                JOptionPane.QUESTION_MESSAGE,
                null,
                opcionesObjetivo,
                opcionesObjetivo[0]);
        if (seleccionObjetivo == null) {
            return;
        }
        int indiceObjetivo = -1;
        for (int i = 0; i < opcionesObjetivo.length; i++) {
            if (opcionesObjetivo[i].equals(seleccionObjetivo)) {
                indiceObjetivo = i;
                break;
            }
        }
        if (indiceObjetivo < 0) {
            return;
        }
        mostrar(controlador.usarItemEnBatalla(items.get(indiceItem).getIndice(),
                vivos.get(indiceObjetivo).getIndice()));
    }

    // Agrega el texto al log y vuelve a dibujar todo.
    private void mostrar(String texto) {
        log.append(texto);
        actualizar();
    }

    private void actualizar() {
        EstadoBatallaDTO estado = controlador.getEstadoBatallaDTO();
        CombatienteDTO enemigo = estado.getEnemigo();

        etiquetaPiso.setText("Piso " + estado.getPiso() + " - Ronda " + estado.getNumeroRonda()
                + "   (orden: " + estado.getOrdenDeTurnos() + ")");
        etiquetaEnemigo.setText(enemigo.getNombre());
        barraEnemigo.setMaximum(enemigo.getVidaMaxima());
        barraEnemigo.setValue(enemigo.getVidaActual());
        barraEnemigo.setString(enemigo.getVidaActual() + " / " + enemigo.getVidaMaxima());

        PersonajeDTO enTurno = null;
        List<PersonajeDTO> miembros = estado.getPersonajes();
        for (PersonajeDTO personaje : miembros) {
            if (personaje.getIndice() == estado.getIndicePersonajeEnTurno()) {
                enTurno = personaje;
                break;
            }
        }
        for (int i = 0; i < miembros.size(); i++) {
            PersonajeDTO p = miembros.get(i);
            String marca = "";
            if (p == enTurno) {
                marca = "> ";
            }
            String escudo = p.getTurnosEscudoRestantes() > 0
                    ? " [Escudo " + p.getTurnosEscudoRestantes() + "]" : "";
            nombresParty[i].setText(marca + p.getNombre() + " (" + p.getClase() + ")" + escudo);
            barrasVida[i].setMaximum(p.getVidaMaxima());
            barrasVida[i].setValue(p.getVidaActual());
            barrasVida[i].setString("Vida " + p.getVidaActual() + " / " + p.getVidaMaxima());
            barrasMana[i].setMaximum(p.getManaMaximo());
            barrasMana[i].setValue(p.getManaActual());
            barrasMana[i].setString("Maná " + p.getManaActual() + " / " + p.getManaMaximo());
        }

        boolean terminada = estado.estaTerminada() || enTurno == null;
        botonResumen.setEnabled(terminada);
        botonAtacar.setEnabled(!terminada);
        botonDefender.setEnabled(!terminada && enTurno != null && enTurno.puedeDefender());
        botonItem.setEnabled(!terminada && !estado.getItems().isEmpty());
        if (terminada) {
            etiquetaTurno.setText("Batalla terminada");
            botonBasica.setEnabled(false);
            botonPoderosa.setEnabled(false);
        } else {
            etiquetaTurno.setText("Turno de " + enTurno.getNombre());
            botonBasica.setText(enTurno.getHabilidadBasica());
            botonPoderosa.setText(enTurno.getHabilidadPoderosa());
            botonBasica.setEnabled(enTurno.isHabilidadBasicaDisponible());
            botonPoderosa.setEnabled(enTurno.isHabilidadPoderosaDisponible());
        }
    }
}
