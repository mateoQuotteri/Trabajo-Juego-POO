package RPG.Vista;

import javax.swing.*;
import java.awt.*;

// Ventana principal de la aplicación: un JFrame que arma las 4 pantallas
// mínimas (Menú, Pisos de la Torre, Batalla, Inventario -- documento,
// sección "Pantallas") y las va mostrando de a una con CardLayout, según
// lo que el jugador elija en cada pantalla. Es la única clase que conoce
// la existencia de las 4 pantallas juntas; cada pantalla (JPanel) solo
// conoce esta ventana para pedirle "mostrame la pantalla X" (documento,
// MVC: "las pantallas no deciden nada, solo muestran información y
// avisan qué eligió el jugador").
//
// Maqueta navegable (ver checklist-tpo.txt, 26/09/2026): arma las 4
// pantallas con personajes/enemigos de ejemplo (los mismos objetos reales
// que usa Main.java, no texto inventado) para definir el diseño visual y
// la navegación. Todavía no reemplaza la consola (Main.java +
// MotorDeCombate, que sigue intacta); conectar esta interfaz a la lógica
// real completa (motor de turnos con orden por Velocidad, Torre real con
// progreso guardado, inventario real) queda para más adelante.
public class VentanaPrincipal extends JFrame {

    public static final String MENU = "Menu";
    public static final String TORRE = "Torre";
    public static final String BATALLA = "Batalla";
    public static final String INVENTARIO = "Inventario";

    private final CardLayout cardLayout;
    private final JPanel contenedor;
    private final PantallaBatalla pantallaBatalla;

    public VentanaPrincipal() {
        super("RPG - El Ascenso a la Torre");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 650);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        contenedor = new JPanel(cardLayout);

        PantallaMenu pantallaMenu = new PantallaMenu(this);
        PantallaTorre pantallaTorre = new PantallaTorre(this);
        pantallaBatalla = new PantallaBatalla(this);
        PantallaInventario pantallaInventario = new PantallaInventario(this);

        contenedor.add(pantallaMenu, MENU);
        contenedor.add(pantallaTorre, TORRE);
        contenedor.add(pantallaBatalla, BATALLA);
        contenedor.add(pantallaInventario, INVENTARIO);

        add(contenedor);
        mostrarPantalla(MENU);
    }

    public void mostrarPantalla(String nombre) {
        cardLayout.show(contenedor, nombre);
    }

    // Lo llama PantallaTorre cuando el jugador elige un piso: le arma a
    // PantallaBatalla los enemigos que corresponden a ese piso ANTES de
    // mostrarla (el escalado real de stats por piso, ver checklist-tpo.txt,
    // sigue pendiente -- por ahora cada piso común alterna Slime/Goblin, y
    // los pisos 5 y 10 son los jefes).
    public void empezarBatallaEnPiso(int piso) {
        pantallaBatalla.armarBatallaParaPiso(piso);
        mostrarPantalla(BATALLA);
    }

    public static void main(String[] args) {
        VentanaPrincipal ventana = new VentanaPrincipal();
        ventana.setVisible(true);
    }
}
