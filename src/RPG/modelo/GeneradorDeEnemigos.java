package RPG.modelo;

import RPG.modelo.enemigos.Enemigo;
import RPG.modelo.enemigos.Goblin;
import RPG.modelo.enemigos.Jefe;
import RPG.modelo.enemigos.JefeFinal;
import RPG.modelo.enemigos.Slime;

// Crea el enemigo que corresponde a cada piso de la Torre.
// GRASP Creador: quien necesita los enemigos y tiene los datos para
// armarlos (el número de piso) es quien los crea. Antes esta decisión
// estaba metida en PantallaBatalla (una pantalla decidiendo reglas del
// juego = rompía MVC) y alternaba Slime/Goblin por piso par/impar en vez
// de elegir al azar como dice el documento.
public class GeneradorDeEnemigos {

    public static final int PISO_JEFE = 5;
    public static final int PISO_JEFE_FINAL = 10;

    // Lista ponderada (misma técnica que la IA de los enemigos): por ahora
    // 50% Slime / 50% Goblin. Cambiar las repeticiones cambia la chance.
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
        enemigo.aplicarEscaladoDePiso(piso); // +10% por piso (documento)
        return enemigo;
    }
}
