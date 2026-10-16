package RPG.modelo.enemigos;

import RPG.modelo.Combatiente;
import RPG.modelo.personajes.Personaje;

// Clase abstracta para los enemigos. Extiende Combatiente igual que
// Personaje y agrega lo propio de un enemigo: ataque, nivel, la
// experiencia que da al morir y su IA por lista ponderada (actuar()).
//
// CAMBIOS 16/10:
// - actuar() ahora DEVUELVE el mensaje en vez de imprimirlo (el modelo no
//   habla con la consola; eso es trabajo de la Vista -> MVC).
// - experienciaQueOtorga: el documento dice que cada tipo de enemigo da
//   una experiencia FIJA. Faltaba el atributo.
// - aplicarEscaladoDePiso(): regla del documento "+10% por piso".
public abstract class Enemigo extends Combatiente {

    private static final double ESCALADO_POR_PISO = 0.10;

    private int ataque;
    private int nivel;
    private int experienciaQueOtorga;

    public Enemigo(String nombre, int vidaMaxima, int ataque, int defensa, int velocidad,
                   int nivel, int experienciaQueOtorga) {
        super(nombre, vidaMaxima, defensa, velocidad);
        this.ataque = ataque;
        this.nivel = nivel;
        this.experienciaQueOtorga = experienciaQueOtorga;
    }

    // Cada subclase decide qué hace en su turno con su propia lista
    // ponderada (polimorfismo: el controlador solo dice "actuá").
    public abstract String actuar(Personaje objetivo);

    // Los jefes dan recompensas garantizadas (documento). Por defecto, no.
    public boolean esJefe() {
        return false;
    }

    // Ataque básico: daño = max(1, ataque - defensa del objetivo).
    public String atacar(Combatiente objetivo) {
        int vidaAntes = objetivo.getVidaActual();
        objetivo.recibirDanio(Math.max(1, ataque - objetivo.getDefensa()));
        return getNombre() + " ataca a " + objetivo.getNombre() + ": "
                + (vidaAntes - objetivo.getVidaActual()) + " de daño (vida restante " + objetivo.getVidaActual() + ").";
    }

    // Misma fórmula pero multiplicando el ataque (ataques especiales).
    protected int calcularDanioConMultiplicador(Combatiente objetivo, double multiplicador) {
        int danio = (int) Math.round(ataque * multiplicador) - objetivo.getDefensa();
        return Math.max(1, danio);
    }

    // Lista ponderada: cada acción aparece repetida tantas veces como su
    // peso (ej: 7 "atacar" + 3 "aturdir" = 70%/30%) y se elige un índice
    // al azar.
    protected String elegirAccionPonderada(String[] acciones) {
        int indice = (int) (Math.random() * acciones.length);
        return acciones[indice];
    }

    // Las 3 acciones "genéricas" que comparten varios enemigos.
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

    // Escala vida/ataque/defensa/velocidad base según el piso (piso 1 =
    // valores base, piso 2 = +10%, piso 3 = +20%...). Se llama UNA sola vez,
    // justo después de crear el enemigo (lo hace GeneradorDeEnemigos).
    public void aplicarEscaladoDePiso(int piso) {
        double factor = 1 + ESCALADO_POR_PISO * (piso - 1);
        setVidaMaxima((int) Math.round(getVidaMaxima() * factor));
        setVidaActual(getVidaMaxima());
        setDefensa((int) Math.round(getDefensa() * factor));
        setVelocidad((int) Math.round(getVelocidad() * factor));
        ataque = (int) Math.round(ataque * factor);
        nivel = piso;
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
