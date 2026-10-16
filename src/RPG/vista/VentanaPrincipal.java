package RPG.vista;

import RPG.controlador.ControladorDeJuego;
import javax.swing.*;
import java.awt.CardLayout;

// Ventana principal (JFrame): arma las pantallas una sola vez y las va
// mostrando con CardLayout. Cada pantalla solo avisa "el jugador tocó tal
// botón"; ninguna decide reglas del juego (MVC).
//
// Pantallas acordadas con el profe (mínimas e indispensables):
//   Menú (inicio)          -> Iniciar partida / Cargar partida / Cerrar juego
//   Partidas guardadas     -> la abre "Cargar partida"
//   Batalla
//   Resumen de batalla     -> resultado + estado de la party + seguir
//   Inventario
//
// Flujo: Menú -> Batalla (piso 1) -> Resumen -> Batalla (piso 2) -> ...
//        hasta ganar el piso 10 o perder (vuelve al Menú).
public class VentanaPrincipal extends JFrame {

    public static final String MENU = "Menu";
    public static final String PARTIDAS_GUARDADAS = "PartidasGuardadas";
    public static final String BATALLA = "Batalla";
    public static final String RESUMEN = "Resumen";
    public static final String INVENTARIO = "Inventario";

    private ControladorDeJuego controlador;
    private CardLayout cardLayout;
    private JPanel contenedor;
    private PantallaBatalla pantallaBatalla;
    private PantallaResumenBatalla pantallaResumen;

    public VentanaPrincipal() {
        super("RPG - El Ascenso a la Torre");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(960, 680);
        setLocationRelativeTo(null);

        controlador = new ControladorDeJuego();

        cardLayout = new CardLayout();
        contenedor = new JPanel(cardLayout);

        pantallaBatalla = new PantallaBatalla(this, controlador.getControladorDeBatalla());
        pantallaResumen = new PantallaResumenBatalla(this);
        contenedor.add(new PantallaMenu(this), MENU);
        contenedor.add(new PantallaPartidasGuardadas(this), PARTIDAS_GUARDADAS);
        contenedor.add(pantallaBatalla, BATALLA);
        contenedor.add(pantallaResumen, RESUMEN);
        contenedor.add(new PantallaInventario(this), INVENTARIO);

        add(contenedor);
        mostrarPantalla(MENU);
    }

    public void mostrarPantalla(String nombre) {
        cardLayout.show(contenedor, nombre);
    }

    // Menú -> "Iniciar partida"
    public void iniciarPartida() {
        controlador.nuevaPartida();
        jugarPisoActual();
    }

    // Resumen -> "Siguiente piso"
    public void jugarPisoActual() {
        pantallaBatalla.empezarBatalla(controlador.empezarBatallaDelPisoActual());
        mostrarPantalla(BATALLA);
    }

    // Batalla -> "Ver resumen" (cuando la batalla terminó)
    public void mostrarResumen() {
        pantallaResumen.mostrar(controlador.cerrarBatalla(), controlador.getPartida());
        mostrarPantalla(RESUMEN);
    }
}
