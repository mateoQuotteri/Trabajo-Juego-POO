package RPG.modelo.enemigos;

import RPG.modelo.habilidades.Habilidad;
import RPG.modelo.habilidades.HabilidadAtaque;

// Documento: "Solo ataca, nunca aturde".
public class Slime extends Enemigo {

    // nombre, vida, ataque, defensa, velocidad, nivel, experiencia que da
    // (la experiencia no está en el documento: valor a definir)
    public Slime() {
        super("Slime", 25, 12, 5, 12, 1, 30);

        Habilidad ataque = new HabilidadAtaque("Ataque", 0, 1.0, false);
        setAcciones(new Habilidad[]{
                ataque, ataque, ataque, ataque, ataque,
                ataque, ataque, ataque, ataque, ataque   // 100% ataca
        });
    }
}
