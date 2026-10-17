package RPG.modelo.enemigos;

import RPG.modelo.Combatiente;
import RPG.modelo.habilidades.Habilidad;

// Clase abstracta para los enemigos.
// Cómo decide qué hacer (documento 1.5, "listas ponderadas"): cada subclase
// arma un array de 10 habilidades, repitiendo cada una según su
// probabilidad (ej. 7 veces Ataque + 3 veces Aturdir = 70% / 30%).
// En su turno se elige una posición al azar entre 0 y 9.
public abstract class Enemigo extends Combatiente {

    private int ataque;
    private int nivel;
    private int experienciaQueOtorga;
    private Habilidad[] acciones;

    public Enemigo(String nombre, int vidaMaxima, int ataque, int defensa, int velocidad,
                   int nivel, int experienciaQueOtorga) {
        super(nombre, vidaMaxima, defensa, velocidad);
        this.ataque = ataque;
        this.nivel = nivel;
        this.experienciaQueOtorga = experienciaQueOtorga;
    }

    // Lo llama cada subclase en su constructor para cargar su lista.
    protected void setAcciones(Habilidad[] acciones) {
        this.acciones = acciones;
    }

    // Turno del enemigo: elige una acción al azar de su lista y la usa.
    public String actuar(Combatiente objetivo) {
        int indice = (int) (Math.random() * acciones.length);
        return acciones[indice].ejecutar(this, objetivo);
    }

    // El enemigo tiene un solo ataque (no distingue físico o mágico).
    @Override
    public int getAtaque(boolean esMagico) {
        return ataque;
    }

    // TODO: escalado por piso (documento 1.1): las stats de los enemigos
    // comunes se multiplican +10% por piso (piso 1 = x1.0, piso 2 = x1.1...).
    // Pista: calcular el factor y usar los setters protegidos de
    // Combatiente (setVidaMaxima, setVidaActual, setDefensa, setVelocidad)
    // y el atributo ataque. Ojo con los jefes: decidir si se escalan.
    public void escalarSegunPiso(int piso) {
    }

    public void aplicarEscaladoDePiso(int piso) {

    }

    public int getNivel() {
        return nivel;
    }

    public int getExperienciaQueOtorga() {
        return experienciaQueOtorga;
    }
}
