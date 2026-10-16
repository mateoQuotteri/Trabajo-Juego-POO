package RPG.controlador;

import RPG.modelo.Partida;
import RPG.modelo.ResultadoDeBatalla;

// CONTROLADOR de la partida completa: lo que pasa ENTRE batallas.
// Iniciar partida, arrancar la batalla del piso actual y, cuando termina,
// avanzar de piso o cerrar la partida (victoria / game over).
//
// Se separó de ControladorDeBatalla para que cada controlador tenga una
// sola responsabilidad: uno maneja los turnos de una pelea, el otro el
// recorrido por la Torre. Más adelante acá van a ir guardar/cargar partida.
public class ControladorDeJuego {

    private Partida partida;
    private ControladorDeBatalla controladorDeBatalla;
    private boolean batallaYaCerrada;

    public ControladorDeJuego() {
        controladorDeBatalla = new ControladorDeBatalla();
    }

    // Menú -> "Iniciar partida"
    public void nuevaPartida() {
        partida = Partida.crearNueva();
    }

    // Arranca la batalla del piso en el que está la partida.
    public String empezarBatallaDelPisoActual() {
        batallaYaCerrada = false;
        return controladorDeBatalla.iniciarBatalla(partida.getParty(), partida.getPisoActual());
    }

    // Se llama cuando la batalla terminó: actualiza la partida (sube de
    // piso, o la da por ganada/perdida) y devuelve el resultado para la
    // pantalla de Resumen de Batalla.
    public ResultadoDeBatalla cerrarBatalla() {
        ResultadoDeBatalla resultado = controladorDeBatalla.getResultado();
        if (resultado == null) {
            return null; // la batalla todavía no terminó
        }
        if (batallaYaCerrada) {
            return resultado; // ya se registró: no subir de piso dos veces
        }
        batallaYaCerrada = true;
        if (resultado.ganoLaParty()) {
            partida.registrarVictoria();
        } else {
            partida.registrarDerrota();
        }
        return resultado;
    }

    public Partida getPartida() {
        return partida;
    }

    public ControladorDeBatalla getControladorDeBatalla() {
        return controladorDeBatalla;
    }
}
