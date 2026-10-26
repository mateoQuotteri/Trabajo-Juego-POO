package RPG.modelo.enemigos;

import RPG.modelo.Combatiente;
import RPG.modelo.habilidades.Habilidad;

// Clase abstracta para los enemigos.
// Cómo decide qué hacer (documento 1.5, "listas ponderadas"): cada subclase
// arma un array de 10 habilidades, repitiendo cada una según su
// probabilidad (ej. 7 veces Ataque + 3 veces Aturdir = 70% / 30%).
// En su turno se elige una posición al azar entre 0 y 9.
public abstract class Enemigo extends Combatiente {

    private final int vidaMaximaBase;
    private int ataque;
    private final int ataqueBase;
    private final int defensaBase;
    private final int velocidadBase;
    private int nivel;
    private int experienciaQueOtorga;
    private Habilidad[] acciones;

    public Enemigo(String nombre, int vidaMaxima, int ataque, int defensa, int velocidad,
                   int nivel, int experienciaQueOtorga) {
        super(nombre, vidaMaxima, defensa, velocidad);
        this.vidaMaximaBase = vidaMaxima;
        this.ataque = ataque;
        this.ataqueBase = ataque;
        this.defensaBase = defensa;
        this.velocidadBase = velocidad;
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

    // Piso 1 conserva las estadísticas base; cada piso posterior suma un 10%.
    public void escalarSegunPiso(int piso) {
        if (piso < 1) {
            throw new IllegalArgumentException("El piso debe ser mayor o igual a 1.");
        }

        double multiplicador = 1.0 + (piso - 1) * 0.10;
        int vidaMaxima = escalarStat(vidaMaximaBase, multiplicador);
        setVidaMaxima(vidaMaxima);
        setVidaActual(vidaMaxima);
        ataque = escalarStat(ataqueBase, multiplicador);
        setDefensa(escalarStat(defensaBase, multiplicador));
        setVelocidad(escalarStat(velocidadBase, multiplicador));
    }

    private int escalarStat(int valorBase, double multiplicador) {
        return Math.max(1, (int) Math.round(valorBase * multiplicador));
    }

    public int getNivel() {
        return nivel;
    }

    public int getExperienciaQueOtorga() {
        return experienciaQueOtorga;
    }
}
