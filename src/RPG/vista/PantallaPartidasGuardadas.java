package RPG.vista;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// Pantalla de Partidas guardadas: la abre "Cargar partida" desde el Menú.
// Muestra la lista de partidas guardadas para elegir cuál continuar.
//
// MAQUETA: el guardado en archivo todavía no existe (próximo paso), así que
// la lista sale vacía y "Cargar" está deshabilitado. Cuando exista, la
// lista se llena pidiéndole los nombres de las partidas al controlador.
public class PantallaPartidasGuardadas extends JPanel {

    private DefaultListModel<String> partidas;
    private JList<String> listaPartidas;

    public PantallaPartidasGuardadas(final VentanaPrincipal ventana) {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(28, 28, 38));
        setBorder(BorderFactory.createEmptyBorder(20, 120, 20, 120));

        JLabel titulo = new JLabel("Partidas guardadas", SwingConstants.CENTER);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 24));
        titulo.setForeground(Color.WHITE);
        add(titulo, BorderLayout.NORTH);

        partidas = new DefaultListModel<String>();
        listaPartidas = new JList<String>(partidas);
        listaPartidas.setFont(listaPartidas.getFont().deriveFont(15f));
        add(new JScrollPane(listaPartidas), BorderLayout.CENTER);

        JLabel aviso = new JLabel("Todavía no hay partidas guardadas (el guardado se implementa más adelante).",
                SwingConstants.CENTER);
        aviso.setForeground(Color.LIGHT_GRAY);

        JButton botonCargar = new JButton("Cargar seleccionada");
        botonCargar.setEnabled(false);
        JButton botonVolver = new JButton("Volver al menú");
        botonVolver.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ventana.mostrarPantalla(VentanaPrincipal.MENU);
            }
        });

        JPanel botones = new JPanel();
        botones.setOpaque(false);
        botones.add(botonCargar);
        botones.add(botonVolver);

        JPanel inferior = new JPanel(new BorderLayout());
        inferior.setOpaque(false);
        inferior.add(aviso, BorderLayout.NORTH);
        inferior.add(botones, BorderLayout.SOUTH);
        add(inferior, BorderLayout.SOUTH);
    }
}
