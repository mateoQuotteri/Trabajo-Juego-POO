package RPG;

import RPG.Personajes.Enemigo;

public class Batalla {
    private String lugar;
    private Party party;
    private Enemigo[] enemigos;

    public Batalla(String lugar, Party party){
        this.lugar = lugar;
        this.party = party;
        this.enemigos = new Enemigo[4];
    }
    // comentario para hacer un commit
    public void agregarEnemigo(Enemigo enemigo) {

        for (int i = 0; i < enemigos.length; i++) {

            if (enemigos[i] == null) {
                enemigos[i] = enemigo;
                break;
            }
        }
    }

}
