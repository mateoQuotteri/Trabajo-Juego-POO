package RPG.modelo;

// Clase abstracta para CUALQUIER entidad que participa de una batalla:
// un Personaje de la party o un Enemigo. Existe porque las habilidades
// necesitan un "objetivo" común (el Guerrero le pega a un Enemigo, pero el
// Curador cura a un Personaje aliado) y porque el orden de turnos mezcla a
// los dos bandos en un mismo array ordenado por Velocidad.
//
// Acá vive solo lo que Personaje y Enemigo tienen en común: nombre, vida,
// defensa y velocidad.
public abstract class Combatiente {

    private String nombre;
    private int vidaMaxima;
    private int vidaActual;
    private int defensa;
    private int velocidad;

    public Combatiente(String nombre, int vidaMaxima, int defensa, int velocidad) {
        this.nombre = nombre;
        this.vidaMaxima = vidaMaxima;
        this.vidaActual = vidaMaxima;
        this.defensa = defensa;
        this.velocidad = velocidad;
    }

    // GRASP Experto en Información: el propio Combatiente tiene el dato
    // (su vida), así que es quien decide cómo se modifica. Nunca baja de 0.
    public void recibirDanio(int danio) {
        vidaActual -= danio;
        if (vidaActual < 0) {
            vidaActual = 0;
        }
    }

    // Cura sin pasarse del máximo. Personaje la pisa (@Override) para
    // bloquearla mientras dure el Toque de Muerte del Esqueleto.
    public void recibirCuracion(int cantidad) {
        vidaActual += cantidad;
        if (vidaActual > vidaMaxima) {
            vidaActual = vidaMaxima;
        }
    }

    public boolean estaVivo() {
        return vidaActual > 0;
    }

    public String getNombre() {
        return nombre;
    }

    public int getVidaMaxima() {
        return vidaMaxima;
    }

    public int getVidaActual() {
        return vidaActual;
    }

    public int getDefensa() {
        return defensa;
    }

    public int getVelocidad() {
        return velocidad;
    }

    // Setters protegidos: solo las subclases pueden cambiar estos valores
    // (al subir de nivel o al escalar un enemigo según el piso). Desde
    // afuera nadie puede "pisar" la vida de un combatiente a mano.
    protected void setVidaMaxima(int nuevaVidaMaxima) {
        this.vidaMaxima = nuevaVidaMaxima;
    }

    protected void setVidaActual(int nuevaVidaActual) {
        this.vidaActual = nuevaVidaActual;
    }

    protected void setDefensa(int nuevaDefensa) {
        this.defensa = nuevaDefensa;
    }

    protected void setVelocidad(int nuevaVelocidad) {
        this.velocidad = nuevaVelocidad;
    }
}
