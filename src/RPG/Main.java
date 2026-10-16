package RPG;

import RPG.vista.VentanaPrincipal;
import javax.swing.SwingUtilities;

// Punto de entrada del juego: abre la ventana de Swing.
// (Para probar la lógica por consola, correr RPG.consola.JuegoConsola.)
//
// SwingUtilities.invokeLater: Swing pide que las ventanas se creen en su
// propio hilo de eventos. Es la forma "oficial" de arrancar una app Swing.
public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                VentanaPrincipal ventana = new VentanaPrincipal();
                ventana.setVisible(true);
            }
        });
    }
}
