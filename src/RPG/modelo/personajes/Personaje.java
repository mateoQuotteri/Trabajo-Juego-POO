package RPG.modelo.personajes;

import RPG.modelo.Combatiente;
import RPG.modelo.habilidades.Habilidad;

// Clase abstracta para los 4 personajes de la party.
// Suma a Combatiente: maná, ataque físico/mágico, nivel, experiencia y
// las 2 habilidades (básica y poderosa) que le pasa cada subclase.
public abstract class Personaje extends Combatiente {

    private int manaMaximo;
    private int manaActual;
    private int ataqueFisico;
    private int ataqueMagico;
    private int nivel;
    private int experiencia;

    private Habilidad habilidadBasica;
    private Habilidad habilidadPoderosa;

    private boolean defendiendo;

    public Personaje(String nombre, int vidaMaxima, int manaMaximo, int ataqueFisico, int ataqueMagico,
                     int defensa, int velocidad, Habilidad habilidadBasica, Habilidad habilidadPoderosa) {
        super(nombre, vidaMaxima, defensa, velocidad);
        this.manaMaximo = manaMaximo;
        this.manaActual = 0;
        this.ataqueFisico = ataqueFisico;
        this.ataqueMagico = ataqueMagico;
        this.nivel = 1;
        this.experiencia = 0;
        this.habilidadBasica = habilidadBasica;
        this.habilidadPoderosa = habilidadPoderosa;
        this.defendiendo = false;
    }

    // Cada clase devuelve su nombre ("Guerrero", "Mago"...) para mostrarlo.
    public abstract String getNombreClase();

    // ---------- Acciones ----------

    // Ataque básico: gratis y siempre físico.
    public String atacar(Combatiente objetivo) {
        int danio = ataqueFisico - objetivo.getDefensa();
        if (danio < 1) {
            danio = 1;
        }
        objetivo.recibirDanio(danio);
        return getNombre() + " ataca a " + objetivo.getNombre() + " (vida restante " + objetivo.getVidaActual() + ")";
    }

    public boolean tieneManaPara(Habilidad habilidad) {
        return manaActual >= habilidad.getCostoMana();
    }

    // Descuenta el maná y ejecuta la habilidad. Antes de llamarlo, el
    // controlador ya chequeó tieneManaPara().
    public String usarHabilidad(Habilidad habilidad, Combatiente objetivo) {
        manaActual = manaActual - habilidad.getCostoMana();
        return habilidad.ejecutar(this, objetivo);
    }

    // Solo el Guerrero puede Defender (lo pisa en su clase). Es intencional:
    // es el tanque. Los demás atacan, usan una habilidad o un ítem.
    public boolean puedeDefender() {
        return false;
    }

    public String defender() {
        defendiendo = true;
        return getNombre() + " se defiende";
    }

    // Defender reduce a la mitad el daño hasta el próximo turno propio.
    @Override
    public void recibirDanio(int danio) {
        if (defendiendo) {
            danio = danio / 2;
        }
        super.recibirDanio(danio);
    }

    // ---------- Turnos ----------

    // Al empezar su turno, se termina la defensa del turno anterior.
    public void iniciarTurno() {
        defendiendo = false;
    }

    // Documento: si no usó habilidad en su turno, recupera +10 de maná.
    public void recuperarMana() {
        manaActual = manaActual + 10;
        if (manaActual > manaMaximo) {
            manaActual = manaMaximo;
        }
    }

    // Documento: cada batalla arranca con el maná en 0.
    public void prepararParaBatalla() {
        manaActual = 0;
        defendiendo = false;
        limpiarEstados();
    }

    // ---------- Experiencia y nivel ----------

    // TODO: documento 1.5 "Progresión".
    //  - Nivel máximo 20.
    //  - Experiencia necesaria para subir = nivel actual x 100.
    //  - Al subir, TODAS las estadísticas suben +3% compuesto
    //    (vida máxima, maná máximo, ataques, defensa y velocidad).
    // Pista: sumar la experiencia; mientras alcance para subir, restar lo
    // necesario y llamar a un método privado subirNivel() que use los
    // setters protegidos de Combatiente (setVidaMaxima, setDefensa...).
    public void ganarExperiencia(int cantidad) {
    }

    // ---------- Getters ----------

    @Override
    public int getAtaque(boolean esMagico) {
        if (esMagico) {
            return ataqueMagico;
        }
        return ataqueFisico;
    }



    public Habilidad getHabilidadBasica() {
        return habilidadBasica;
    }

    public Habilidad getHabilidadPoderosa() {
        return habilidadPoderosa;
    }

    public int getManaMaximo() {
        return manaMaximo;
    }

    public int getManaActual() {
        return manaActual;
    }

    public int getAtaqueFisico() {
        return ataqueFisico;
    }

    public int getAtaqueMagico() {
        return ataqueMagico;
    }

    public int getNivel() {
        return nivel;
    }

    public int getExperiencia() {
        return experiencia;
    }
}
