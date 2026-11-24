package RPG.servicios;

import RPG.modelo.enemigos.Enemigo;
import RPG.modelo.enemigos.Goblin;
import RPG.modelo.enemigos.Jefe;
import RPG.modelo.enemigos.JefeFinal;
import RPG.modelo.enemigos.Slime;

public class FabricaDeEnemigos {

    public Enemigo crear(int piso) {
        if (piso < 1 || piso > 10) {
            throw new IllegalArgumentException("El piso debe estar entre 1 y 10.");
        }
        if (piso == 5) {
            return new Jefe();
        }
        if (piso == 10) {
            return new JefeFinal();
        }

        Enemigo enemigo = Math.random() < 0.5 ? new Slime() : new Goblin();
        enemigo.escalarSegunPiso(piso);
        return enemigo;
    }
}
