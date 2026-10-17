package RPG.modelo;

import RPG.modelo.personajes.Personaje;

// El grupo del jugador: mínimo 2 y máximo 4 personajes.
// La Party es quien sabe cosas sobre sus miembros (GRASP Experto):
// cuántos siguen vivos, elegir uno al azar, si perdieron todos.
public class Party {

    private Personaje[] miembros;
    private int cantidad;

    public Party() {
        miembros = new Personaje[4];
        cantidad = 0;
    }

    // Devuelve false si la party ya tiene 4.
    public boolean agregarMiembro(Personaje personaje) {
        if (cantidad >= miembros.length) {
            return false;
        }
        miembros[cantidad] = personaje;
        cantidad++;
        return true;
    }

    public boolean tieneMinimoDeMiembros() {
        return cantidad >= 2;
    }

    // Devuelve solo los lugares ocupados (sin los null).
    public Personaje[] getMiembros() {
        Personaje[] resultado = new Personaje[cantidad];
        for (int i = 0; i < cantidad; i++) {
            resultado[i] = miembros[i];
        }
        return resultado;
    }

    public Personaje[] getMiembrosVivos() {
        int vivos = 0;
        for (int i = 0; i < cantidad; i++) {
            if (miembros[i].estaVivo()) {
                vivos++;
            }
        }
        Personaje[] resultado = new Personaje[vivos];
        int indice = 0;
        for (int i = 0; i < cantidad; i++) {
            if (miembros[i].estaVivo()) {
                resultado[indice] = miembros[i];
                indice++;
            }
        }
        return resultado;
    }

    // El enemigo ataca a un personaje vivo elegido al azar (documento).
    public Personaje elegirVivoAlAzar() {
        Personaje[] vivos = getMiembrosVivos();
        if (vivos.length == 0) {
            return null;
        }
        return vivos[(int) (Math.random() * vivos.length)];
    }

    // Game over: todos con vida 0.
    public boolean estaDerrotada() {
        return getMiembrosVivos().length == 0;
    }

    public void prepararParaBatalla() {
        for (int i = 0; i < cantidad; i++) {
            miembros[i].prepararParaBatalla();
        }
    }
}
