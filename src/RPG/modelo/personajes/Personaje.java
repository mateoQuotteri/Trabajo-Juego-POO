package RPG.modelo.personajes;

import RPG.modelo.Combatiente;
import RPG.modelo.habilidades.Habilidad;

// Clase abstracta para los 4 personajes jugables (Guerrero, Mago, Arquero,
// Curador). Extiende Combatiente (nombre, vida, defensa, velocidad) y agrega
// lo propio de un personaje de la party: maná, ataque físico y mágico,
// nivel y experiencia, y sus 2 habilidades (objetos Habilidad que cada
// subclase le pasa al constructor).
//
// Todo según el documento entregado (Fase A), que es la fuente de verdad.
public abstract class Personaje extends Combatiente {

    // ---------- Reglas del documento ----------
    private static final int NIVEL_MAXIMO = 20;
    private static final double CRECIMIENTO_POR_NIVEL = 1.03; // +3% compuesto
    private static final int EXPERIENCIA_POR_NIVEL = 100;      // necesaria = nivel actual x 100
    private static final int MANA_POR_TURNO = 10;
    private static final int DURACION_VENENO = 3;
    private static final double PORCENTAJE_VIDA_POR_VENENO = 0.05;

    // ---------- Estadísticas ----------
    private int manaMaximo;
    private int manaActual;
    private int ataqueFisico;
    private int ataqueMagico;
    private int nivel;
    private int experiencia;

    // ---------- Habilidades ----------
    private Habilidad habilidadBasica;
    private Habilidad habilidadPoderosa;

    // ---------- Estados (se limpian al empezar cada batalla) ----------
    private boolean defendiendo;
    private boolean aturdido;
    private int turnosVenenoRestantes;

    public Personaje(String nombre, int vidaMaxima, int manaMaximo, int ataqueFisico, int ataqueMagico,
                     int defensa, int velocidad, Habilidad habilidadBasica, Habilidad habilidadPoderosa) {
        super(nombre, vidaMaxima, defensa, velocidad);
        this.manaMaximo = manaMaximo;
        this.ataqueFisico = ataqueFisico;
        this.ataqueMagico = ataqueMagico;
        this.nivel = 1;
        this.experiencia = 0;
        this.habilidadBasica = habilidadBasica;
        this.habilidadPoderosa = habilidadPoderosa;
        prepararParaNuevaBatalla();
    }

    // ======================================================================
    // ACCIONES DEL TURNO
    // ======================================================================

    // Ataque básico: gratis, siempre físico. Devuelve el mensaje para la Vista.
    public String atacar(Combatiente objetivo) {
        int danio = Math.max(1, ataqueFisico - objetivo.getDefensa());
        int vidaAntes = objetivo.getVidaActual();
        objetivo.recibirDanio(danio);
        return getNombre() + " ataca a " + objetivo.getNombre() + ": "
                + (vidaAntes - objetivo.getVidaActual()) + " de daño (vida restante " + objetivo.getVidaActual() + ").";
    }

    // Diagrama de secuencia 2: Personaje.tieneManaPara(habilidad).
    public boolean tieneManaPara(Habilidad habilidad) {
        return manaActual >= habilidad.getCostoMana();
    }

    // Usa una de sus habilidades. Valida y descuenta el maná en UN solo
    // lugar (GRASP Experto: el Personaje es el dueño de su maná).
    public String usarHabilidad(Habilidad habilidad, Combatiente objetivo) {
        if (!tieneManaPara(habilidad)) {
            return getNombre() + " no tiene maná suficiente para " + habilidad.getNombre() + ".";
        }
        manaActual -= habilidad.getCostoMana();
        return habilidad.ejecutar(this, objetivo);
    }

    // Defender: por defecto ningún personaje puede; el Guerrero pisa este
    // método y devuelve true (polimorfismo en vez de "instanceof Guerrero").
    public boolean puedeDefender() {
        return false;
    }

    // Documento: "reduce a la mitad el daño que recibe ese turno". Dura
    // hasta que vuelva a empezar su propio turno (ver iniciarTurno()).
    public String defender() {
        if (!puedeDefender()) {
            return getNombre() + " no puede defenderse.";
        }
        defendiendo = true;
        return getNombre() + " se pone en guardia: recibe la mitad de daño hasta su próximo turno.";
    }

    // ======================================================================
    // CICLO DEL TURNO (lo llama el controlador)
    // ======================================================================

    // Al empezar su turno se termina la guardia de Defender del turno anterior.
    public void iniciarTurno() {
        defendiendo = false;
    }

    // Documento: si atacó, se defendió o usó un ítem, recupera +10 de maná.
    public void crecerManaPorTurno() {
        manaActual = Math.min(manaMaximo, manaActual + MANA_POR_TURNO);
    }

    // Al final de su turno (haya actuado o no): el veneno le saca el 5% de
    // su vida máxima, durante 3 turnos. Devuelve el mensaje, o "" si nada.
    public String actualizarEfectosDeFinDeTurno() {
        if (turnosVenenoRestantes == 0) {
            return "";
        }
        int danioVeneno = (int) Math.round(getVidaMaxima() * PORCENTAJE_VIDA_POR_VENENO);
        int vidaAntes = getVidaActual();
        recibirDanio(danioVeneno);
        turnosVenenoRestantes--;
        return getNombre() + " pierde " + (vidaAntes - getVidaActual()) + " de vida por el veneno.";
    }

    // Documento: "al comenzar el combate, todos inician con 0 de maná".
    public void prepararParaNuevaBatalla() {
        manaActual = 0;
        defendiendo = false;
        aturdido = false;
        turnosVenenoRestantes = 0;
    }

    @Override
    public void recibirDanio(int danio) {
        int danioFinal = defendiendo ? danio / 2 : danio;
        super.recibirDanio(danioFinal);
    }

    // ======================================================================
    // ESTADOS DE ALTERACIÓN (documento, sección 1.4.4)
    // PENDIENTE: Escudo (mitad de daño durante 2 turnos). El documento lo
    // nombra pero no dice quién lo da: falta definirlo.
    // ======================================================================

    public void aturdir() {
        aturdido = true;
    }

    public boolean estaAturdido() {
        return aturdido;
    }

    // Al empezar el turno: si estaba aturdido, "gasta" el aturdimiento y
    // devuelve true (pierde exactamente 1 turno). No se acumula.
    public boolean consumirAturdimiento() {
        if (aturdido) {
            aturdido = false;
            return true;
        }
        return false;
    }

    public void envenenar() {
        turnosVenenoRestantes = DURACION_VENENO;
    }

    public boolean estaEnvenenado() {
        return turnosVenenoRestantes > 0;
    }

    public boolean estaDefendiendo() {
        return defendiendo;
    }

    // Texto corto con los estados activos, para mostrar en pantalla.
    public String describirEstados() {
        String estados = "";
        if (aturdido) estados += " [Aturdido]";
        if (estaEnvenenado()) estados += " [Veneno " + turnosVenenoRestantes + "]";
        if (defendiendo) estados += " [Defendiendo]";
        return estados;
    }

    // ======================================================================
    // EXPERIENCIA Y NIVEL
    // ======================================================================

    // Experiencia necesaria = nivel actual x 100. Devuelve cuántos niveles
    // subió (puede ser más de uno de golpe).
    public int ganarExperiencia(int cantidad) {
        if (nivel >= NIVEL_MAXIMO) {
            return 0;
        }
        experiencia += cantidad;
        int nivelesSubidos = 0;
        while (nivel < NIVEL_MAXIMO && experiencia >= getExperienciaParaSiguienteNivel()) {
            experiencia -= getExperienciaParaSiguienteNivel();
            subirNivel();
            nivelesSubidos++;
        }
        if (nivel >= NIVEL_MAXIMO) {
            experiencia = 0;
        }
        return nivelesSubidos;
    }

    public int getExperienciaParaSiguienteNivel() {
        return nivel * EXPERIENCIA_POR_NIVEL;
    }

    // +3% compuesto a todas las estadísticas (documento).
    private void subirNivel() {
        nivel++;
        setVidaMaxima(crecer(getVidaMaxima()));
        setVidaActual(getVidaMaxima()); // al subir de nivel se cura del todo (el doc no lo dice: a confirmar)
        manaMaximo = crecer(manaMaximo);
        ataqueFisico = crecer(ataqueFisico);
        ataqueMagico = crecer(ataqueMagico);
        setDefensa(crecer(getDefensa()));
        setVelocidad(crecer(getVelocidad()));
    }

    private int crecer(int valor) {
        return (int) Math.round(valor * CRECIMIENTO_POR_NIVEL);
    }

    // ======================================================================
    // GETTERS
    // ======================================================================

    // Diagrama de secuencia 2: getAtaque(tipoDeDanio).
    public int getAtaque(boolean esMagico) {
        return esMagico ? ataqueMagico : ataqueFisico;
    }

    public Habilidad getHabilidadBasica() {
        return habilidadBasica;
    }

    public Habilidad getHabilidadPoderosa() {
        return habilidadPoderosa;
    }

    // Nombre de la clase para mostrar ("Guerrero", "Mago"...).
    public abstract String getNombreClase();

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
