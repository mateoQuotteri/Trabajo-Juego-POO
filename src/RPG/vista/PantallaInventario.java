package RPG.vista;

import RPG.controlador.ControladorDeBatalla;
import RPG.dto.EstadoInventarioDTO;
import RPG.dto.ItemDTO;
import RPG.dto.PersonajeDTO;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

// PANTALLA 4: Inventario + resumen de la party.
// Arriba: las estadísticas de cada héroe (tabla hecha con un GridLayout).
// Abajo: los 6 espacios del inventario.
public class PantallaInventario extends JPanel {

    private static final String[] COLUMNAS = {
            "Nombre", "Clase", "Nivel", "Vida", "Maná", "Atq. Fís.", "Atq. Mág.",
            "Defensa", "Velocidad", "Exp.", "Arma", "Armadura"
    };

    private ControladorDeBatalla controlador;
    private JPanel tablaParty;
    private JButton[] espaciosInventario;

    public PantallaInventario(final VentanaPrincipal ventana, ControladorDeBatalla controlador) {
        this.controlador = controlador;
        setLayout(new BorderLayout(10, 15));
        setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        // ----- Estadísticas de la party (se llena en actualizar()) -----
        tablaParty = new JPanel(new GridLayout(5, COLUMNAS.length, 5, 5));
        tablaParty.setBorder(BorderFactory.createTitledBorder("Party"));
        add(tablaParty, BorderLayout.NORTH);

        // ----- Inventario de 6 espacios -----
        JPanel espacios = new JPanel(new GridLayout(2, 3, 10, 10));
        espacios.setBorder(BorderFactory.createTitledBorder("Inventario (6 espacios)"));
        espaciosInventario = new JButton[6];
        for (int i = 0; i < espaciosInventario.length; i++) {
            final int indice = i;
            espaciosInventario[i] = new JButton("(vacío)");
            espaciosInventario[i].setEnabled(false);
            espaciosInventario[i].addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    mostrarItem(indice, ventana);
                }
            });
            espacios.add(espaciosInventario[i]);
        }
        add(espacios, BorderLayout.CENTER);

        JButton botonVolver = new JButton("Volver al resumen");
        botonVolver.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ventana.irAResumen();
            }
        });
        JPanel abajo = new JPanel();
        abajo.add(botonVolver);
        add(abajo, BorderLayout.SOUTH);
    }

    // La llama la ventana al entrar a esta pantalla: vuelve a armar la
    // tabla con los datos actuales de cada personaje.
    public void actualizar() {
        EstadoInventarioDTO estado = controlador.getEstadoInventarioDTO();
        tablaParty.removeAll();
        tablaParty.setBorder(BorderFactory.createTitledBorder("Party - Oro: " + estado.getOro()));
        for (int i = 0; i < COLUMNAS.length; i++) {
            tablaParty.add(new JLabel(COLUMNAS[i]));
        }
        for (PersonajeDTO p : estado.getPersonajes()) {
            tablaParty.add(new JLabel(p.getNombre()));
            tablaParty.add(new JLabel(p.getClase()));
            tablaParty.add(new JLabel("" + p.getNivel()));
            tablaParty.add(new JLabel(p.getVidaActual() + "/" + p.getVidaMaxima()));
            tablaParty.add(new JLabel(p.getManaActual() + "/" + p.getManaMaximo()));
            tablaParty.add(new JLabel("" + p.getAtaqueFisico()));
            tablaParty.add(new JLabel("" + p.getAtaqueMagico()));
            tablaParty.add(new JLabel("" + p.getDefensa()));
            tablaParty.add(new JLabel("" + p.getVelocidad()));
            tablaParty.add(new JLabel("" + p.getExperiencia()));
            tablaParty.add(new JLabel(p.getArma().isEmpty() ? "-" : p.getArma()));
            tablaParty.add(new JLabel(p.getArmadura().isEmpty() ? "-" : p.getArmadura()));
        }
        actualizarInventario(estado.getItems());
        tablaParty.revalidate(); // avisa a Swing que cambió el contenido
        tablaParty.repaint();
    }

    private void actualizarInventario(List<ItemDTO> items) {
        for (int i = 0; i < espaciosInventario.length; i++) {
            JButton espacio = espaciosInventario[i];
            if (i >= items.size()) {
                espacio.setText("(vacío)");
                espacio.setToolTipText(null);
                espacio.setEnabled(false);
            } else {
                ItemDTO item = items.get(i);
                espacio.setText(item.getNombre());
                espacio.setToolTipText(item.getDescripcion());
                espacio.setEnabled(true);
            }
        }
    }

    private void mostrarItem(int indice, VentanaPrincipal ventana) {
        EstadoInventarioDTO estado = controlador.getEstadoInventarioDTO();
        if (indice < 0 || indice >= estado.getItems().size()) {
            actualizarInventario(estado.getItems());
            return;
        }
        ItemDTO item = estado.getItems().get(indice);

        boolean esEquipo = item.esEquipable();
        int opcion = JOptionPane.showConfirmDialog(
                this,
                item.getDescripcion() + (esEquipo ? "\n\n¿Querés equiparlo?" : "\n\n¿Querés usar este ítem?"),
                item.getNombre(),
                JOptionPane.YES_NO_OPTION);
        if (opcion != JOptionPane.YES_OPTION) {
            return;
        }

        List<PersonajeDTO> miembros = new ArrayList<PersonajeDTO>();
        for (PersonajeDTO personaje : estado.getPersonajes()) {
            if (esEquipo || personaje.estaVivo()) {
                miembros.add(personaje);
            }
        }
        if (miembros.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay personajes disponibles.");
            return;
        }

        String[] opcionesObjetivo = new String[miembros.size()];
        for (int i = 0; i < miembros.size(); i++) {
            PersonajeDTO miembro = miembros.get(i);
            opcionesObjetivo[i] = miembro.getNombre() + " (Vida "
                    + miembro.getVidaActual() + "/" + miembro.getVidaMaxima()
                    + ", Maná " + miembro.getManaActual() + "/" + miembro.getManaMaximo() + ")";
        }
        int seleccionado = JOptionPane.showOptionDialog(
                this,
                esEquipo ? "Elegí quién va a equipar el ítem:" : "Elegí el personaje objetivo:",
                "Usar " + item.getNombre(),
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                opcionesObjetivo,
                opcionesObjetivo[0]);
        if (seleccionado < 0) {
            return;
        }

        String resultado = controlador.usarItem(item.getIndice(), miembros.get(seleccionado).getIndice());
        JOptionPane.showMessageDialog(this, resultado);
        actualizar();
    }
}
