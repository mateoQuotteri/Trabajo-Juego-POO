package RPG.modelo;

import RPG.modelo.enemigos.Enemigo;
import RPG.modelo.enemigos.Goblin;
import RPG.modelo.enemigos.Jefe;
import RPG.modelo.enemigos.JefeFinal;
import RPG.modelo.enemigos.Slime;

// Crea el enemigo que corresponde a cada piso de la Torre (documento 1.1):
//   pisos 1-4 y 6-9 -> Slime o Goblin al azar
//   piso 5          -> Rey Slime (Jefe)
//   piso 10         -> Guardián Ancestral (Jefe Final)
// A los comunes les aplica el escalado de +10% por piso. Los jefes NO se
// escalan: en la tabla del documento figuran como "Jefe (Piso 5)" y "Jefe
// Final (Piso 10)", o sea que sus números ya son los de su piso. (Si se
// escalaran, el Guardián quedaría con 57 de defensa y sería imposible de
// ganar: probado con 1000 partidas simuladas, 0 victorias.)
// GRASP Creador: quien tiene los datos para armar el enemigo (el piso) lo crea.
public class GeneradorDeEnemigos {

    public static final int PISO_JEFE = 5;
    public static final int PISO_JEFE_FINAL = 10;

    // Lista ponderada (misma técnica que la IA): 50% Slime / 50% Goblin.
    private static final String[] ENEMIGOS_COMUNES = {"Slime", "Goblin"};

    public Enemigo crearEnemigoParaPiso(int piso) {
        if (piso == PISO_JEFE) {
            return new Jefe();
        }
        if (piso == PISO_JEFE_FINAL) {
            return new JefeFinal();
        }
        String tipo = ENEMIGOS_COMUNES[(int) (Math.random() * ENEMIGOS_COMUNES.length)];
        Enemigo enemigo;
        if (tipo.equals("Goblin")) {
            enemigo = new Goblin("Goblin");
        } else {
            enemigo = new Slime("Slime");
        }
        enemigo.aplicarEscaladoDePiso(piso);
        return enemigo;
    }
}
