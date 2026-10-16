package RPG.modelo;

import RPG.modelo.personajes.Personaje;

// El grupo de personajes del jugador (mínimo 2, máximo 4: requisito de la
// cátedra y recomendación del profe).
//
// CAMBIOS 16/10: la Party ahora es "experta" en sus miembros (GRASP Experto
// en Información). Antes MotorDeCombate y PantallaBatalla recorrían el
// array de personajes por su cuenta, cada uno con su propia copia de "¿hay
// alguien vivo?" y "elegir uno vivo al azar". Ahora esas preguntas se le
// hacen a la Party, y coinciden con el diagrama de secuencia 1
// (getMiembrosVivos(), elegirVivoAlAzar()).
public class Party {

    public static final int MINIMO_MIEMBROS = 2;
    public static final int MAXIMO_MIEMBROS = 4;

    private Personaje[] miembros;
    private int cantidad;

    public Party() {
        miembros = new Personaje[MAXIMO_MIEMBROS];
        cantidad = 0;
    }

    // Devuelve false si la party ya está llena (antes fallaba en silencio).
    public boolean agregarMiembro(Personaje personaje) {
        if (cantidad >= MAXIMO_MIEMBROS) {
            return false;
        }
        miembros[cantidad] = personaje;
        cantidad++;
        return true;
    }

    // ¿Se puede arrancar a jugar con esta party?
    public boolean tieneMiembrosSuficientes() {
        return cantidad >= MINIMO_MIEMBROS;
    }

    // Copia del array con solo los personajes cargados (sin los null).
    // Se devuelve una copia para que nadie de afuera pueda meter o sacar
    // miembros salteándose agregarMiembro() (encapsulamiento).
    public Personaje[] getMiembros() {
        Personaje[] copia = new Personaje[cantidad];
        for (int i = 0; i < cantidad; i++) {
            copia[i] = miembros[i];
        }
        return copia;
    }

    public Personaje[] getMiembrosVivos() {
        Personaje[] vivos = new Personaje[contarVivos()];
        int indice = 0;
        for (int i = 0; i < cantidad; i++) {
            if (miembros[i].estaVivo()) {
                vivos[indice] = miembros[i];
                indice++;
            }
        }
        return vivos;
    }

    public int contarVivos() {
        int vivos = 0;
        for (int i = 0; i < cantidad; i++) {
            if (miembros[i].estaVivo()) {
                vivos++;
            }
        }
        return vivos;
    }

    // Game over cuando todos llegan a 0 de vida.
    public boolean estaDerrotada() {
        return contarVivos() == 0;
    }

    // El enemigo elige a quién atacar al azar entre los vivos (documento).
    public Personaje elegirVivoAlAzar() {
        Personaje[] vivos = getMiembrosVivos();
        if (vivos.length == 0) {
            return null;
        }
        return vivos[(int) (Math.random() * vivos.length)];
    }

    // Al empezar una batalla: maná en 0 y sin efectos de la pelea anterior.
    public void prepararParaBatalla() {
        for (int i = 0; i < cantidad; i++) {
            miembros[i].prepararParaNuevaBatalla();
        }
    }

    // Documento: "se reparte experiencia a los miembros VIVOS de la party".
    // Devuelve un texto con quién subió de nivel, para mostrarlo.
    public String repartirExperiencia(int experiencia) {
        String resumen = "";
        for (int i = 0; i < cantidad; i++) {
            if (miembros[i].estaVivo()) {
                int niveles = miembros[i].ganarExperiencia(experiencia);
                if (niveles > 0) {
                    resumen += miembros[i].getNombre() + " sube a nivel " + miembros[i].getNivel() + ". ";
                }
            }
        }
        return resumen;
    }

    public int getCantidad() {
        return cantidad;
    }
}
