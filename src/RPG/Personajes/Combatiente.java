package RPG.Personajes;

// Clase abstracta que representa a CUALQUIER entidad que participa de una
// batalla, ya sea un personaje de la party o un enemigo. Se creó porque las
// habilidades necesitan un objetivo común: el Guerrero, el Mago y el Arquero
// atacan a un Enemigo, pero el Curador "ataca" (cura) a un compañero de
// party, que es un Personaje. Sin esta clase en común, cada habilidad
// necesitaría un tipo de objetivo distinto y no se podría aprovechar
// polimorfismo real (ver Diseno-RPG-POO.docx, sección GRASP/MVC: "todas las
// habilidades funcionan igual por dentro: alguien las usa, apuntan a un
// objetivo, gastan maná y producen un efecto").
//
// Acá vive todo lo que Personaje y Enemigo tienen en común: nombre, vida,
// defensa y velocidad (la estadística que decide el orden de turnos). Cada
// subclase agrega lo que le es propio (maná y ataque mágico para Personaje;
// comportamiento de IA para Enemigo).
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

    // Aplica daño a este combatiente. Es GRASP Information Expert: el propio
    // Combatiente es quien tiene el dato (su vida) así que es quien decide
    // cómo se modifica y si llegó a 0. Nunca baja de 0 (no hay vida negativa).
    public void recibirDanio(int danio) {
        vidaActual -= danio;
        if (vidaActual < 0) {
            vidaActual = 0;
        }
    }

    // Cura vida sin pasarse del máximo. La usa la habilidad de Sanación del
    // Curador, pero cualquier Combatiente (Personaje o Enemigo) la puede
    // recibir si en el futuro hiciera falta.
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

    // Usado al subir de nivel (ver Personaje.subirNivel()): la vida máxima
    // crece y la vida actual se ajusta en la misma proporción para que un
    // personaje que subió de nivel a mitad de una pelea no quede "lleno"
    // artificialmente ni "vacío" de golpe.
    protected void setVidaMaxima(int nuevaVidaMaxima) {
        this.vidaMaxima = nuevaVidaMaxima;
    }

    protected void setDefensa(int nuevaDefensa) {
        this.defensa = nuevaDefensa;
    }

    protected void setVelocidad(int nuevaVelocidad) {
        this.velocidad = nuevaVelocidad;
    }

    protected void setVidaActual(int nuevaVidaActual) {
        this.vidaActual = nuevaVidaActual;
    }
}
