package RPG;

import RPG.Personajes.Enemigo;

// Sin cambios respecto de lo que ya tenían: sigue siendo un array fijo de 4
// enemigos. No dependía del sistema de atributos viejo, así que no hacía
// falta tocarlo con la migración al diseño nuevo.
public class Batalla {
    private String lugar;
    private Party party;
    private Enemigo[] enemigos;

    public Batalla(String lugar, Party party){
        this.lugar = lugar;
        this.party = party;
        this.enemigos = new Enemigo[4];
    }

    public void agregarEnemigo(Enemigo enemigo) {

        for (int i = 0; i < enemigos.length; i++) {

            if (enemigos[i] == null) {
                enemigos[i] = enemigo;
                break;
            }
        }
    }

    public Enemigo[] getEnemigos() {
        return enemigos;
    }

    // Agregados (no estaban antes): los necesita MotorDeCombate para saber
    // dónde jugarse la batalla y para llegar a los personajes de la party
    // desde una Batalla.
    public String getLugar() {
        return lugar;
    }

    public Party getParty() {
        return party;
    }

}
