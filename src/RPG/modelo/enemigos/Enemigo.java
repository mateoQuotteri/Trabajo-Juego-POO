package RPG.modelo.enemigos;

import RPG.modelo.Combatiente;
import RPG.modelo.personajes.Personaje;

// Clase abstracta para los enemigos. Extiende Combatiente igual que
// Personaje y agrega lo propio de un enemigo: ataque, nivel, la
// experiencia que da al morir y su IA por lista ponderada (actuar()).
//
// Documento, sección 1.5: las decisiones se toman con "listas ponderadas":
// un array con las acciones repetidas según su probabilidad (7 "atacar" +
// 3 "aturdir" = 70%/30%) y un índice al azar entre 0 y 9.
public abstract class Enemigo extends Combatiente {

    private static final double ESCALADO_POR_PISO = 0.10;

    private int ataque;
    private int nivel;
    private int experienciaQueOtorga;

    public Enemigo(String nombre, int vidaMaxima, int ataque, int defensa, int velocidad,
                   int experienciaQueOtorga) {
        super(nombre, vidaMaxima, defensa, velocidad);
        this.ataque = ataque;
        this.nivel = 1;
        this.experienciaQueOtorga = experienciaQueOtorga;
    }

    // Cada subclase decide qué hace en su turno (polimorfismo: el
    // controlador solo dice "actuá"). Devuelve el mensaje para la Vista.
    public abstract String actuar(Personaje objetivo);

    // Los jefes dan recompensas especiales (documento). Por defecto, no.
    public boolean esJefe() {
        return false;
    }

    // Ataque: daño = max(1, ataque - defensa del objetivo).
    public String atacar(Combatiente objetivo) {
        int vidaAntes = objetivo.getVidaActual();
        objetivo.recibirDanio(Math.max(1, ataque - objetivo.getDefensa()));
        return getNombre() + " ataca a " + objetivo.getNombre() + ": "
                + (vidaAntes - objetivo.getVidaActual()) + " de daño (vida restante " + objetivo.getVidaActual() + ").";
    }

    // Elige una acción de la lista ponderada.
    protected String elegirAccionPonderada(String[] acciones) {
        int indice = (int) (Math.random() * acciones.length);
        return acciones[indice];
    }

    // Ejecuta una de las 3 acciones posibles del documento.
    protected String ejecutarAccionSobre(String accion, Personaje objetivo) {
        if (accion.equals("aturdir")) {
            objetivo.aturdir();
            return getNombre() + " aturde a " + objetivo.getNombre() + ": pierde su próximo turno.";
        }
        if (accion.equals("envenenar")) {
            objetivo.envenenar();
            return getNombre() + " envenena a " + objetivo.getNombre() + ": pierde vida durante 3 turnos.";
        }
        return atacar(objetivo);
    }

    // Documento, regla de escalado: "multiplicando sus atributos base por
    // un factor relativo al número de piso (+10% por piso)". Piso 1 = x1.0,
    // piso 2 = x1.1 ... piso 9 = x1.8. Se aplica solo a los enemigos comunes
    // (ver GeneradorDeEnemigos). Se llama una sola vez, justo después de
    // crear el enemigo.
    public void aplicarEscaladoDePiso(int piso) {
        double factor = 1 + ESCALADO_POR_PISO * (piso - 1);
        setVidaMaxima((int) Math.round(getVidaMaxima() * factor));
        setVidaActual(getVidaMaxima());
        setDefensa((int) Math.round(getDefensa() * factor));
        setVelocidad((int) Math.round(getVelocidad() * factor));
        ataque = (int) Math.round(ataque * factor);
        nivel = piso;
    }

    // Para los jefes, que no se escalan pero tienen el nivel de su piso.
    protected void setNivel(int nivel) {
        this.nivel = nivel;
    }

    public int getAtaque() {
        return ataque;
    }

    public int getNivel() {
        return nivel;
    }

    public int getExperienciaQueOtorga() {
        return experienciaQueOtorga;
    }
}
