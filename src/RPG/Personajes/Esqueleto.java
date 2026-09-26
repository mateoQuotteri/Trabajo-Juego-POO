package RPG.Personajes;

// Enemigo de prueba (no es parte de la tabla oficial de enemigos del
// documento de diseño, que define Slime/Goblin/Jefe/Jefe Final -- ver
// RPG.Personajes.ImplEnemigo), pero el grupo pidió que igual tenga
// comportamiento real en vez de solo atacar: además de su ataque común,
// tiene una chance de Aturdir o de Envenenar al objetivo (los 2 efectos
// "oficiales" del documento), y un ataque especial propio, Toque de
// Muerte, que impide que el objetivo reciba curación (de ítem o de
// habilidad) durante 2 turnos.
public class Esqueleto extends Enemigo {

    // 50% atacar / 20% aturdir / 20% envenenar / 10% especial (Toque de
    // Muerte se deja más raro que las otras 2 acciones porque su efecto es
    // más fuerte -- bloquea toda curación, no solo resta vida).
    private static final String[] ACCIONES = {
            "atacar", "atacar", "atacar", "atacar", "atacar", // 5/10 = 50%
            "aturdir", "aturdir", // 2/10 = 20%
            "envenenar", "envenenar", // 2/10 = 20%
            "especial" // 1/10 = 10%
    };

    private static final int DURACION_IMPEDIMENTO_CURACION = 2;

    public Esqueleto(String nombre) {
        super(nombre, 80, 55, 8, 15, 1); // vida, ataque, defensa, velocidad, nivel
    }

    @Override
    public void actuar(Personaje objetivo) {
        String accion = elegirAccionPonderada(ACCIONES);
        if (accion.equals("especial")) {
            usarToqueDeMuerte(objetivo);
        } else {
            ejecutarAccionSobre(accion, objetivo);
        }
    }

    // Ataque especial del Esqueleto: mismo daño que su ataque común (no
    // pega más fuerte, a diferencia de Escupitajo/Golpe Debilitante --
    // acá el pedido del grupo fue solo el efecto de bloquear la curación,
    // sin pedir daño extra) y además impide que el objetivo se cure
    // durante 2 turnos, sea por poción o por habilidad (ver Personaje.
    // aplicarImpedimentoDeCuracion/recibirCuracion).
    private void usarToqueDeMuerte(Personaje objetivo) {
        int danio = calcularDanioConMultiplicador(objetivo, 1.0);
        objetivo.recibirDanio(danio);
        objetivo.aplicarImpedimentoDeCuracion(DURACION_IMPEDIMENTO_CURACION);
        System.out.println(getNombre() + " usa Toque de Muerte sobre " + objetivo.getNombre()
                + ". Vida restante: " + objetivo.getVidaActual()
                + ". No va a poder curarse durante " + DURACION_IMPEDIMENTO_CURACION + " turnos.");
    }
}
