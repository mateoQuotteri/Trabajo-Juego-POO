package RPG.Personajes.ImplEnemigo;

import RPG.Personajes.Enemigo;
import RPG.Personajes.Personaje;

// Jefe del Piso 5 de la Torre ("Rey Slime", nombre tomado de la propuesta
// gráfica "EL ASCENSO A LA TORRE"; los valores de stats de esa imagen NO se
// usaron porque no coinciden con la tabla confirmada en los documentos de
// diseño). Es único, por eso no recibe nombre por parámetro como los
// enemigos comunes. IA: 50% ataca, 30% aturde, 20% envenena.
//
// BALANCEADO 26/09/2026 (pedido del grupo: "bastante más difícil que los
// enemigos de pisos anteriores, más agresivo y poderoso, con una
// posibilidad NORMAL de perder"). Los valores viejos (Vida 70, Ataque 30,
// Defensa 20) hacían que la party le ganara siempre en 2-3 rondas -- ver
// checklist-tpo.txt, sección "BALANCE DE LOS JEFES", para el detalle
// completo de cómo se llegó a estos números (simulación de ~1000 batallas
// automáticas, no a ojo): con esta configuración la party pierde
// aproximadamente 1 de cada 3 veces.
public class Jefe extends Enemigo {

    private static final String[] ACCIONES = {
            "atacar", "atacar", "atacar", "atacar", "atacar", // 5/10 = 50%
            "aturdir", "aturdir", "aturdir", // 3/10 = 30%
            "envenenar", "envenenar" // 2/10 = 20%
    };

    public Jefe() {
        super("Rey Slime", 200, 55, 25, 30, 5); // vida, ataque, defensa, velocidad, nivel
    }

    @Override
    public void actuar(Personaje objetivo) {
        String accion = elegirAccionPonderada(ACCIONES);
        ejecutarAccionSobre(accion, objetivo);
    }
}
