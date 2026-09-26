package RPG;

import RPG.Personajes.Personaje;

// Sin cambios respecto de lo que ya tenían: sigue siendo un array fijo de 4
// personajes. No dependía del sistema de atributos viejo, así que no hacía
// falta tocarlo con la migración al diseño nuevo.
public class Party {
    private Personaje[] personajes;

    public Party(){
        personajes = new Personaje[4];
    }

    public void agregarMiembro(Personaje personaje) {

        for (int i = 0; i < personajes.length; i++) {

            if (personajes[i] == null) {
                personajes[i] = personaje;
                break;
            }
        }
    }

    // Agregado (no estaba antes): hace falta para que Main pueda recorrer
    // la party e imprimir las estadísticas de cada integrante sin tener que
    // guardar cada personaje en una variable aparte.
    public Personaje[] getPersonajes() {
        return personajes;
    }

}
