package RPG.modelo.personajes;

import RPG.modelo.Combatiente;
import RPG.modelo.habilidades.Habilidad;
import RPG.modelo.items.Arma;
import RPG.modelo.items.Armadura;

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
    private Arma armaEquipada;
    private Armadura armaduraEquipada;

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
        this.armaEquipada = null;
        this.armaduraEquipada = null;
    }

    // Cada clase devuelve su nombre ("Guerrero", "Mago"...) para mostrarlo.
    public abstract String getNombreClase();

    // ---------- Acciones ----------

    // Ataque básico: gratis y siempre físico.
    public String atacar(Combatiente objetivo) {
        if (objetivo == null || objetivo instanceof Personaje) {
            throw new IllegalArgumentException("Un personaje solo puede atacar a un enemigo.");
        }
        int danio = getAtaqueFisico() - objetivo.getDefensa();
        if (danio < 1) {
            danio = 1;
        }
        objetivo.recibirDanio(danio);
        return getNombre() + " ataca a " + objetivo.getNombre() + " (vida restante " + objetivo.getVidaActual() + ")";
    }

    public boolean tieneManaPara(Habilidad habilidad) {
        if (habilidad == null) {
            throw new IllegalArgumentException("La habilidad es obligatoria.");
        }
        return manaActual >= habilidad.getCostoMana();
    }

    public String usarHabilidad(Habilidad habilidad, Combatiente objetivo) {
        if (habilidad == null || objetivo == null) {
            throw new IllegalArgumentException("La habilidad y el objetivo son obligatorios.");
        }
        if (habilidad != habilidadBasica && habilidad != habilidadPoderosa) {
            throw new IllegalArgumentException("El personaje no posee esa habilidad.");
        }
        if (habilidad.esDeApoyo() && !(objetivo instanceof Personaje)) {
            throw new IllegalArgumentException("Una habilidad de apoyo solo puede usarse sobre un aliado.");
        }
        if (!habilidad.esDeApoyo() && objetivo instanceof Personaje) {
            throw new IllegalArgumentException("Una habilidad ofensiva no puede usarse sobre un aliado.");
        }
        if (!tieneManaPara(habilidad)) {
            throw new IllegalStateException("No alcanza el maná para usar " + habilidad.getNombre() + ".");
        }

        manaActual = manaActual - habilidad.getCostoMana();
        return habilidad.ejecutar(this, objetivo);
    }

    // Solo el Guerrero puede Defender (lo pisa en su clase). Es intencional:
    // es el tanque. Los demás atacan, usan una habilidad o un ítem.
    public boolean puedeDefender() {
        return false;
    }

    public String defender() {
        if (!puedeDefender()) {
            throw new IllegalStateException("Solo el Guerrero puede defenderse.");
        }
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
        recuperarMana(10);
    }

    public void recuperarMana(int cantidad) {
        if (cantidad <= 0) {
            return;
        }
        manaActual = manaActual + cantidad;
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

    public void ganarExperiencia(int cantidad) {
        if (cantidad <= 0 || nivel >= 20) {
            return;
        }

        long experienciaAcumulada = (long) experiencia + cantidad;
        while (nivel < 20 && experienciaAcumulada >= nivel * 100L) {
            experienciaAcumulada -= nivel * 100L;
            subirNivel();
        }
        experiencia = nivel == 20 ? 0 : (int) experienciaAcumulada;
    }

    private void subirNivel() {
        int vidaMaximaAnterior = getVidaMaxima();
        int vidaActualAnterior = getVidaActual();
        setVidaMaxima(escalarStat(vidaMaximaAnterior));
        setVidaActual(Math.min(getVidaMaxima(),
                vidaActualAnterior + getVidaMaxima() - vidaMaximaAnterior));
        setDefensa(escalarStat(super.getDefensa()));
        setVelocidad(escalarStat(getVelocidad()));
        manaMaximo = escalarStat(manaMaximo);
        ataqueFisico = escalarStat(ataqueFisico);
        ataqueMagico = escalarStat(ataqueMagico);
        nivel++;
    }

    private int escalarStat(int valor) {
        if (valor == 0) {
            return 0;
        }

        // Antes retornabamos esto:
        // return Math.max(1, (int) Math.round(valor * 1.03));
        // Ahora:
        // Con Math.ceil conseguimos que TODAS las estadisticas suban. Con Math.max solo algunas. Ya que ceil redondea SIEMPRE para
        // arriba.

        return (int) Math.ceil(valor * 1.03);
    }

    // ---------- Getters ----------

    @Override
    public int getAtaque(boolean esMagico) {
        if (esMagico) {
            return getAtaqueMagico();
        }
        return getAtaqueFisico();
    }

    @Override
    public int getDefensa() {
        return super.getDefensa() + (armaduraEquipada == null ? 0 : armaduraEquipada.getBonusDefensa());
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
        return ataqueFisico + (armaEquipada == null ? 0 : armaEquipada.getBonusAtaqueFisico());
    }

    public int getAtaqueMagico() {
        return ataqueMagico;
    }

    public Arma getArmaEquipada() {
        return armaEquipada;
    }

    public Armadura getArmaduraEquipada() {
        return armaduraEquipada;
    }

    public Arma equiparArma(Arma arma) {
        if (arma == null) {
            throw new IllegalArgumentException("El arma es obligatoria.");
        }
        Arma anterior = armaEquipada;
        armaEquipada = arma;
        return anterior;
    }

    public Armadura equiparArmadura(Armadura armadura) {
        if (armadura == null) {
            throw new IllegalArgumentException("La armadura es obligatoria.");
        }
        Armadura anterior = armaduraEquipada;
        armaduraEquipada = armadura;
        return anterior;
    }

    public int getNivel() {
        return nivel;
    }

    public int getExperiencia() {
        return experiencia;
    }

    public void restaurarProgreso(int nivel, int experiencia, int vidaActual, int manaActual) {
        if (nivel < 1 || nivel > 20 || experiencia < 0
                || (nivel == 20 && experiencia != 0)
                || (nivel < 20 && experiencia >= nivel * 100)
                || vidaActual < 0 || manaActual < 0) {
            throw new IllegalArgumentException("El progreso del personaje guardado no es válido.");
        }

        while (this.nivel < nivel) {
            subirNivel();
        }
        if (vidaActual > getVidaMaxima() || manaActual > manaMaximo) {
            throw new IllegalArgumentException("La vida o el maná guardado superan el máximo del personaje.");
        }
        this.experiencia = experiencia;
        setVidaActual(vidaActual);
        this.manaActual = manaActual;
        defendiendo = false;
    }
}
