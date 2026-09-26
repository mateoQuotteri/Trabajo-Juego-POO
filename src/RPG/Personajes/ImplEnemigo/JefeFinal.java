package RPG.Personajes.ImplEnemigo;

import RPG.Personajes.Enemigo;
import RPG.Personajes.Personaje;

// Jefe final del Piso 10 de la Torre ("Guardián Ancestral", nombre tomado de
// la propuesta gráfica "EL ASCENSO A LA TORRE"; los valores de stats de esa
// imagen NO se usaron por el mismo motivo que en Jefe.java). Único, sin
// nombre por parámetro. IA: 40% ataca, 30% aturde, 30% envenena -- el más
// peligroso de los 4, usa aturdir/envenenar más seguido que el Jefe del
// Piso 5.
//
// BALANCEADO 26/09/2026 (mismo pedido que Jefe.java, pero acá el grupo
// quiere que la posibilidad de perder sea MÁS ALTA que contra el Rey
// Slime: es el jefe final). Ver checklist-tpo.txt, sección "BALANCE DE
// LOS JEFES": con esta configuración la party pierde aproximadamente la
// mitad de las veces (más que el ~33% del Rey Slime).
public class JefeFinal extends Enemigo {

    private static final String[] ACCIONES = {
            "atacar", "atacar", "atacar", "atacar", // 4/10 = 40%
            "aturdir", "aturdir", "aturdir", // 3/10 = 30%
            "envenenar", "envenenar", "envenenar" // 3/10 = 30%
    };

    public JefeFinal() {
        super("Guardián Ancestral", 220, 56, 25, 32, 10); // vida, ataque, defensa, velocidad, nivel
    }

    @Override
    public void actuar(Personaje objetivo) {
        String accion = elegirAccionPonderada(ACCIONES);
        ejecutarAccionSobre(accion, objetivo);
    }
}
