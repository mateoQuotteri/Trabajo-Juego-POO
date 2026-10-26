package RPG.vista;

import RPG.controlador.ControladorDeBatalla;
import RPG.modelo.Batalla;
import RPG.modelo.enemigos.Enemigo;
import RPG.modelo.items.Item;
import RPG.modelo.personajes.Personaje;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
        Personaje[] miembros = controlador.getParty().getMiembros();
        for (int i = 0; i < miembros.length; i++) {
            selectorAliado.addItem(miembros[i].getNombre());
        }
        mostrar(controlador.empezarBatalla());
    }

    private Personaje aliadoElegido() {
        return controlador.getParty().getMiembros()[selectorAliado.getSelectedIndex()];
    }

    private void elegirItemParaUsar() {
        List<Item> items = controlador.getParty().getInventario().getItems();
        if (items.isEmpty()) {
            mostrar("No hay ítems disponibles.\n");
            return;
        }

        Object[] opcionesItem = new Object[items.size()];
        for (int i = 0; i < items.size(); i++) {
            Item item = items.get(i);
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

        Personaje[] vivos = controlador.getParty().getMiembrosVivos();
        if (vivos.length == 0) {
            mostrar("No hay personajes vivos para recibir el ítem.\n");
            return;
        }
        Object[] opcionesObjetivo = new Object[vivos.length];
        for (int i = 0; i < vivos.length; i++) {
            Personaje personaje = vivos[i];
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

        for (int i = 0; i < opcionesObjetivo.length; i++) {
            if (opcionesObjetivo[i].equals(seleccionObjetivo)) {
                mostrar(controlador.usarItemEnBatalla(items.get(indiceItem), vivos[i]));
                return;
            }
        }
    }

    // Agrega el texto al log y vuelve a dibujar todo.
    private void mostrar(String texto) {
        log.append(texto);
        actualizar();
    }

    private void actualizar() {
        Batalla batalla = controlador.getBatalla();
        Enemigo enemigo = batalla.getEnemigo();

        etiquetaPiso.setText("Piso " + batalla.getPiso() + " - Ronda " + batalla.getNumeroRonda()
                + "   (orden: " + batalla.getOrdenComoTexto() + ")");
        etiquetaEnemigo.setText(enemigo.getNombre());
        barraEnemigo.setMaximum(enemigo.getVidaMaxima());
        barraEnemigo.setValue(enemigo.getVidaActual());
        barraEnemigo.setString(enemigo.getVidaActual() + " / " + enemigo.getVidaMaxima());

        Personaje enTurno = controlador.getPersonajeEnTurno();
        Personaje[] miembros = controlador.getParty().getMiembros();
        for (int i = 0; i < miembros.length; i++) {
            Personaje p = miembros[i];
            String marca = "";
            if (p == enTurno) {
                marca = "> ";
            }
            nombresParty[i].setText(marca + p.getNombre() + " (" + p.getNombreClase() + ")");
            barrasVida[i].setMaximum(p.getVidaMaxima());
            barrasVida[i].setValue(p.getVidaActual());
            barrasVida[i].setString("Vida " + p.getVidaActual() + " / " + p.getVidaMaxima());
            barrasMana[i].setMaximum(p.getManaMaximo());
            barrasMana[i].setValue(p.getManaActual());
            barrasMana[i].setString("Maná " + p.getManaActual() + " / " + p.getManaMaximo());
        }

        boolean terminada = (enTurno == null);
        botonResumen.setEnabled(terminada);
        botonAtacar.setEnabled(!terminada);
        botonDefender.setEnabled(!terminada && enTurno.puedeDefender());
        botonItem.setEnabled(!terminada && !controlador.getParty().getInventario().getItems().isEmpty());
        if (terminada) {
            etiquetaTurno.setText("Batalla terminada");
            botonBasica.setEnabled(false);
            botonPoderosa.setEnabled(false);
        } else {
            etiquetaTurno.setText("Turno de " + enTurno.getNombre());
            botonBasica.setText(enTurno.getHabilidadBasica().getNombre());
            botonPoderosa.setText(enTurno.getHabilidadPoderosa().getNombre());
            // Documento: si no alcanza el maná, la habilidad se muestra bloqueada.
            botonBasica.setEnabled(enTurno.tieneManaPara(enTurno.getHabilidadBasica()));
            botonPoderosa.setEnabled(enTurno.tieneManaPara(enTurno.getHabilidadPoderosa()));
        }
    }
}
