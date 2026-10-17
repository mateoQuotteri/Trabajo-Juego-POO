package RPG.modelo.enemigos;

import RPG.modelo.habilidades.Habilidad;
import RPG.modelo.habilidades.HabilidadAtaque;
import RPG.modelo.habilidades.HabilidadAturdir;
import RPG.modelo.habilidades.HabilidadEnvenenar;

// Jefe del piso 5: "Rey Slime". Documento: 50% ataca / 30% aturde / 20% envenena.
public class Jefe extends Enemigo {

    public Jefe() {
        super("Rey Slime", 70, 30, 20, 25, 5, 250);

        Habilidad ataque = new HabilidadAtaque("Ataque", 0, 1.0, false);
        Habilidad aturdir = new HabilidadAturdir();
        Habilidad envenenar = new HabilidadEnvenenar();
        setAcciones(new Habilidad[]{
                ataque, ataque, ataque, ataque, ataque, // 50%
                aturdir, aturdir, aturdir,              // 30%
                envenenar, envenenar                    // 20%
        });
    }
}
