package RPG.modelo.enemigos;

import RPG.modelo.habilidades.Habilidad;
import RPG.modelo.habilidades.HabilidadAtaque;
import RPG.modelo.habilidades.HabilidadAturdir;

// Documento: "70% ataca / 30% intenta aturdir".
public class Goblin extends Enemigo {

    public Goblin() {
        super("Goblin", 35, 20, 12, 28, 1, 50);

        Habilidad ataque = new HabilidadAtaque("Ataque", 0, 1.0, false);
        Habilidad aturdir = new HabilidadAturdir();
        setAcciones(new Habilidad[]{
                ataque, ataque, ataque, ataque, ataque, ataque, ataque, // 70%
                aturdir, aturdir, aturdir                               // 30%
        });
    }
}
