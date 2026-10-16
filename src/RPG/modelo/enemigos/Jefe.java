package RPG.modelo.enemigos;

import RPG.modelo.personajes.Personaje;

// Jefe del Piso 5: "Rey Slime". 50% ataca / 30% aturde / 20% envenena.
// Stats rebalanceadas por el grupo el 26/09 (simulación de 1000 batallas:
// la party pierde ~1 de cada 3). NO se le aplica el escalado por piso:
// sus números ya están pensados para el piso 5.
// OJO: el documento entregado todavía tiene los valores viejos (70/30/20/25).
// PENDIENTE (pedido del grupo): 2 ataques especiales propios.
public class Jefe extends Enemigo {

    private static final String[] ACCIONES = {
            "atacar", "atacar", "atacar", "atacar", "atacar", // 50%
            "aturdir", "aturdir", "aturdir",                  // 30%
            "envenenar", "envenenar"                          // 20%
    };

    // vida, ataque, defensa, velocidad, nivel, experiencia que otorga
    public Jefe() {
        super("Rey Slime", 200, 55, 25, 30, 5, 250);
    }

    @Override
    public String actuar(Personaje objetivo) {
        return ejecutarAccionSobre(elegirAccionPonderada(ACCIONES), objetivo);
    }

    @Override
    public boolean esJefe() {
        return true;
    }
}
