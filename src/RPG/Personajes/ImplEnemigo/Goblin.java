package RPG.Personajes.ImplEnemigo;

import RPG.Personajes.Enemigo;
import RPG.Personajes.Personaje;

// Enemigo del Piso 1 de la Torre. Más agresivo que el Slime: además de
// atacar y aturdir, tiene su propio ataque especial, Golpe Debilitante
// (pedido del grupo además de los 3 efectos de estado "oficiales" -- ver
// checklist-tpo.txt): un poco más de daño que el ataque común, y además
// baja la Defensa del objetivo durante 2 turnos (mismo mecanismo que el
// Escupitajo del Slime, pero sobre Defensa en vez de Velocidad -- el
// Goblin ya cubre el rol de "aturdir" para controlar, así que su especial
// se diferencia bajando la resistencia del objetivo en vez de la
// velocidad).
public class Goblin extends Enemigo {

    private static final String[] ACCIONES = {
            "atacar", "atacar", "atacar", "atacar", "atacar", // 5/10 = 50%
            "aturdir", "aturdir", "aturdir", // 3/10 = 30% (igual que antes de agregar el especial)
            "especial", "especial" // 2/10 = 20%
    };

    private static final double MULTIPLICADOR_GOLPE_DEBILITANTE = 1.3; // "un poco más" que el ataque común
    private static final int REDUCCION_DEFENSA_GOLPE_DEBILITANTE = 8;
    private static final int DURACION_REDUCCION_DEFENSA = 2;

    public Goblin(String nombre) {
        super(nombre, 35, 20, 12, 28, 1); // vida, ataque, defensa, velocidad, nivel
    }

    @Override
    public void actuar(Personaje objetivo) {
        String accion = elegirAccionPonderada(ACCIONES);
        if (accion.equals("especial")) {
            usarGolpeDebilitante(objetivo);
        } else {
            ejecutarAccionSobre(accion, objetivo);
        }
    }

    // Ataque especial del Goblin: un poco más de daño que su ataque común
    // (mismo helper que usa el Escupitajo del Slime, ver Enemigo.
    // calcularDanioConMultiplicador) y además reduce la Defensa del
    // objetivo (ver Personaje.reducirDefensa).
    private void usarGolpeDebilitante(Personaje objetivo) {
        int danio = calcularDanioConMultiplicador(objetivo, MULTIPLICADOR_GOLPE_DEBILITANTE);
        objetivo.recibirDanio(danio);
        objetivo.reducirDefensa(REDUCCION_DEFENSA_GOLPE_DEBILITANTE, DURACION_REDUCCION_DEFENSA);
        System.out.println(getNombre() + " golpea a " + objetivo.getNombre() + " con Golpe Debilitante"
                + ". Vida restante: " + objetivo.getVidaActual()
                + ". Defensa reducida en " + REDUCCION_DEFENSA_GOLPE_DEBILITANTE
                + " durante " + DURACION_REDUCCION_DEFENSA + " turnos.");
    }
}
