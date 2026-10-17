package RPG.vista;

import RPG.controlador.ControladorDeBatalla;
import java.awt.CardLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;

// La ventana del juego. Tiene las 4 pantallas y muestra una a la vez
// (CardLayout = un mazo de cartas: se ve solo la de arriba).
//
// Las 4 pantallas acordadas con el profe:
//   1. Inicio (con el menú)
//   2. Batalla
//   3. Resumen de batalla
//   4. Inventario (con el resumen de la party y las estadísticas)
//
// Flujo: Inicio -> Batalla -> Resumen -> (Inventario) -> Batalla del
// piso siguiente -> ... -> gana el piso 10 o pierde -> Inicio.
public class VentanaPrincipal extends JFrame {

    public static final String INICIO = "Inicio";
    public static final String BATALLA = "Batalla";
    public static final String RESUMEN = "Resumen";
    public static final String INVENTARIO = "Inventario";

    private ControladorDeBatalla controlador;
    private CardLayout cartas;
    private JPanel contenedor;

    private PantallaBatalla pantallaBatalla;
    private PantallaResumenBatalla pantallaResumen;
    private PantallaInventario pantallaInventario;

    public VentanaPrincipal() {
        super("RPG - El Ascenso a la Torre");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(950, 650);
        setLocationRelativeTo(null);

        controlador = new ControladorDeBatalla();

        cartas = new CardLayout();
        contenedor = new JPanel(cartas);

        pantallaBatalla = new PantallaBatalla(this, controlador);
        pantallaResumen = new PantallaResumenBatalla(this, controlador);
        pantallaInventario = new PantallaInventario(this, controlador);

        contenedor.add(new PantallaInicio(this), INICIO);
        contenedor.add(pantallaBatalla, BATALLA);
        contenedor.add(pantallaResumen, RESUMEN);
        contenedor.add(pantallaInventario, INVENTARIO);

        add(contenedor);
        cartas.show(contenedor, INICIO);
    }

    // ---------- Navegación (la llaman los botones de cada pantalla) ----------

    public void irAInicio() {
        cartas.show(contenedor, INICIO);
    }

    public void iniciarPartida() {
        controlador.nuevaPartida();
        irABatalla();
    }

    public void irABatalla() {
        pantallaBatalla.empezar();
        cartas.show(contenedor, BATALLA);
    }

    public void irAResumen() {
        pantallaResumen.actualizar();
        cartas.show(contenedor, RESUMEN);
    }

    public void irAInventario() {
        pantallaInventario.actualizar();
        cartas.show(contenedor, INVENTARIO);
    }
}
