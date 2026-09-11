package RPG;

import RPG.Personajes.Personaje;

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

}
