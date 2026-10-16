package RPG.vista;

import RPG.controlador.ControladorDeBatalla;
import javax.swing.*;
import java.awt.*;

// Ventana principal (JFrame): arma las 4 pantallas una sola vez y las va
// mostrando con CardLayout. Cada pantalla solo le pide "mostrame tal
// pantalla"; ninguna decide reglas del juego (MVC).
//
// CAMBIO 16/10: la ventana crea el ControladorDeBatalla y se lo pasa a la
// pantalla de Batalla. La pantalla ya no arma enemigos ni parties: se lo
// pide al controlador.
public class VentanaPrincipal extends JFrame {

    public static final String MENU = "Menu";
    public static final String TORRE = "Torre";
    public static final String BATALLA = "Batalla";
    public static final String INVENTARIO = "Inventario";

    private CardLayout cardLayout;
    private JPanel contenedor;
    private PantallaBatalla pantallaBatalla;

    public VentanaPrincipal() {
        super("RPG - El Ascenso a la Torre");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(960, 680);
        setLocationRelativeTo(null);

        ControladorDeBatalla controladorDeBatalla = new ControladorDeBatalla();

        cardLayout = new CardLayout();
        contenedor = new JPanel(cardLayout);

        pantallaBatalla = new PantallaBatalla(this, controladorDeBatalla);
        contenedor.add(new PantallaMenu(this), MENU);
        contenedor.add(new PantallaTorre(this), TORRE);
        contenedor.add(pantallaBatalla, BATALLA);
        contenedor.add(new PantallaInventario(this), INVENTARIO);

        add(contenedor);
        mostrarPantalla(MENU);
    }

    public void mostrarPantalla(String nombre) {
        cardLayout.show(contenedor, nombre);
    }

    // Lo llama PantallaTorre cuando el jugador elige un piso.
    public void empezarBatallaEnPiso(int piso) {
        pantallaBatalla.empezarBatalla(piso);
        mostrarPantalla(BATALLA);
    }
}
