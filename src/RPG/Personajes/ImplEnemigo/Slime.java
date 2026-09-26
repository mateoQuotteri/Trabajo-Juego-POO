package RPG.Personajes.ImplEnemigo;

import RPG.Personajes.Enemigo;
import RPG.Personajes.Personaje;

// Enemigo del Piso 1 de la Torre (documento de diseño, tabla de enemigos).
// El más débil de los 4: nunca aturde ni envenena. Tiene su ataque común y
// un ataque especial propio, Escupitajo (pedido del grupo además de los 3
// efectos de estado "oficiales" -- ver checklist-tpo.txt): un poco más de
// daño que el ataque común, y además baja 10 puntos la Velocidad del
// objetivo durante 2 turnos.
public class Slime extends Enemigo {

    private static final String[] ACCIONES = {
            "atacar", "atacar", "atacar", "atacar", "atacar", "atacar", "atacar", // 7/10 = 70%
            "especial", "especial", "especial" // 3/10 = 30%
    };

    private static final double MULTIPLICADOR_ESCUPITAJO = 1.3; // "un poco más" que el ataque común
    private static final int REDUCCION_VELOCIDAD_ESCUPITAJO = 10;
    private static final int DURACION_REDUCCION_VELOCIDAD = 2;

    public Slime(String nombre) {
        super(nombre, 25, 12, 5, 12, 1); // vida, ataque, defensa, velocidad, nivel
    }

    @Override
    public void actuar(Personaje objetivo) {
        String accion = elegirAccionPonderada(ACCIONES);
        if (accion.equals("especial")) {
            usarEscupitajo(objetivo);
        } else {
            ejecutarAccionSobre("atacar", objetivo);
        }
    }

    // Ataque especial del Slime: un poco más de daño que su ataque común
    // (mismo helper que usan los otros especiales, ver Enemigo.
    // calcularDanioConMultiplicador) y además reduce la Velocidad del
    // objetivo (ver Personaje.reducirVelocidad).
    private void usarEscupitajo(Personaje objetivo) {
        int danio = calcularDanioConMultiplicador(objetivo, MULTIPLICADOR_ESCUPITAJO);
        objetivo.recibirDanio(danio);
        objetivo.reducirVelocidad(REDUCCION_VELOCIDAD_ESCUPITAJO, DURACION_REDUCCION_VELOCIDAD);
        System.out.println(getNombre() + " escupe sobre " + objetivo.getNombre()
                + ". Vida restante: " + objetivo.getVidaActual()
                + ". Velocidad reducida en " + REDUCCION_VELOCIDAD_ESCUPITAJO
                + " durante " + DURACION_REDUCCION_VELOCIDAD + " turnos.");
    }
}
