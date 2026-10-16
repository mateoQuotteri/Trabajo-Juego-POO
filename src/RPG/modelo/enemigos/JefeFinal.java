package RPG.modelo.enemigos;

import RPG.modelo.personajes.Personaje;

// Jefe final del Piso 10: "Guardián Ancestral". 40% ataca / 30% aturde /
// 30% envenena. Stats rebalanceadas el 26/09 (la party pierde ~1 de cada 2).
// Tampoco se escala por piso. Derrotarlo = ganar el juego.
// OJO: el documento entregado todavía tiene los valores viejos (120/45/30/35).
// PENDIENTE (pedido del grupo): 2 ataques especiales propios.
public class JefeFinal extends Enemigo {

    private static final String[] ACCIONES = {
            "atacar", "atacar", "atacar", "atacar",    // 40%
            "aturdir", "aturdir", "aturdir",           // 30%
            "envenenar", "envenenar", "envenenar"      // 30%
    };

    // vida, ataque, defensa, velocidad, nivel, experiencia que otorga
    public JefeFinal() {
        super("Guardián Ancestral", 220, 56, 25, 32, 10, 500);
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
