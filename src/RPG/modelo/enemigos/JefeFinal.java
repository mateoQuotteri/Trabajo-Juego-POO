package RPG.modelo.enemigos;

import RPG.modelo.habilidades.Habilidad;
import RPG.modelo.habilidades.HabilidadAtaque;
import RPG.modelo.habilidades.HabilidadAturdir;
import RPG.modelo.habilidades.HabilidadEnvenenar;

// Jefe final del piso 10: "Guardián Ancestral".
// Documento: 40% ataca / 30% aturde / 30% envenena.
public class JefeFinal extends Enemigo {

    public JefeFinal() {
        super("Guardián Ancestral", 120, 45, 30, 35, 10, 500);

        Habilidad ataque = new HabilidadAtaque("Ataque", 0, 1.0, false);
        Habilidad aturdir = new HabilidadAturdir();
        Habilidad envenenar = new HabilidadEnvenenar();
        setAcciones(new Habilidad[]{
                ataque, ataque, ataque, ataque,         // 40%
                aturdir, aturdir, aturdir,              // 30%
                envenenar, envenenar, envenenar         // 30%
        });
    }
}
