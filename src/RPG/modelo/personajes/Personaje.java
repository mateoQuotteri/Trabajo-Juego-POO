package RPG.modelo.personajes;

import RPG.modelo.Combatiente;
import RPG.modelo.habilidades.Habilidad;

// Clase abstracta para los 4 personajes jugables (Guerrero, Mago, Arquero,
// Curador). Extiende Combatiente (nombre, vida, defensa, velocidad) y agrega
// lo propio de un personaje de la party: maná, ataque físico y mágico,
// nivel y experiencia, y sus 2 habilidades.
//
// CAMBIO 16/10: las habilidades ya no son métodos abstractos que cada
// subclase reescribe (usarHabilidadBasica/usarHabilidadPoderosa), sino 2
// objetos Habilidad que cada subclase le pasa al constructor. Así la lógica
// común ("¿alcanza el maná? -> se descuenta -> se ejecuta") vive en un solo
// lugar (usarHabilidad) y no copiada 8 veces.
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

    // ---------- Estados temporales (se limpian al empezar cada batalla) ----------
    private boolean defendiendo;
    private int turnosInmortalRestantes;
    private boolean aturdido;
    private int turnosVenenoRestantes;
    private int penalizacionVelocidad;
    private int turnosVelocidadReducidaRestantes;
    private int penalizacionDefensa;
    private int turnosDefensaReducidaRestantes;
    private int turnosImpedidoDeCurarseRestantes;

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

    // Usa una de SUS habilidades sobre un objetivo. El que llama (el
    // controlador) ya validó maná y objetivo, pero se vuelve a chequear el
    // maná acá por seguridad: el Personaje es el dueño de su maná (GRASP
    // Experto) y no debería poder quedar negativo nunca.
    public String usarHabilidad(Habilidad habilidad, Combatiente objetivo) {
        if (!tieneManaPara(habilidad)) {
            return getNombre() + " no tiene maná suficiente para " + habilidad.getNombre() + ".";
        }
        manaActual -= habilidad.getCostoMana();
        return habilidad.ejecutar(this, objetivo);
    }

    // Defender: por defecto ningún personaje puede. El Guerrero pisa
    // puedeDefender() y devuelve true (polimorfismo, en vez de preguntar
    // "instanceof Guerrero" en el controlador o en la pantalla).
    public boolean puedeDefender() {
        return false;
    }

    // Reduce a la mitad TODO el daño que reciba hasta que vuelva a empezar
    // su propio turno (documento: "reduce a la mitad el daño que recibe ese
    // turno"). Antes se gastaba con el primer golpe, y si nadie lo golpeaba
    // quedaba activo para siempre: corregido con iniciarTurno().
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

    // Al empezar su turno: se termina la guardia de Defender del turno anterior.
    public void iniciarTurno() {
        defendiendo = false;
    }

    // Si el personaje NO usó una habilidad este turno, recupera +10 de maná.
    public void crecerManaPorTurno() {
        manaActual = Math.min(manaMaximo, manaActual + MANA_POR_TURNO);
    }

    // Al final de su turno (haya actuado o no): aplica veneno y descuenta
    // un turno a todos los efectos con duración. Devuelve un mensaje si
    // pasó algo visible (veneno), o "" si no.
    public String actualizarEfectosDeFinDeTurno() {
        String mensaje = "";
        if (turnosVenenoRestantes > 0) {
            int danioVeneno = (int) Math.round(getVidaMaxima() * PORCENTAJE_VIDA_POR_VENENO);
            int vidaAntes = getVidaActual();
            recibirDanio(danioVeneno);
            mensaje = getNombre() + " pierde " + (vidaAntes - getVidaActual()) + " de vida por el veneno.";
            turnosVenenoRestantes--;
        }
        if (turnosInmortalRestantes > 0) {
            turnosInmortalRestantes--;
        }
        if (turnosVelocidadReducidaRestantes > 0) {
            turnosVelocidadReducidaRestantes--;
            if (turnosVelocidadReducidaRestantes == 0) {
                penalizacionVelocidad = 0;
            }
        }
        if (turnosDefensaReducidaRestantes > 0) {
            turnosDefensaReducidaRestantes--;
            if (turnosDefensaReducidaRestantes == 0) {
                penalizacionDefensa = 0;
            }
        }
        if (turnosImpedidoDeCurarseRestantes > 0) {
            turnosImpedidoDeCurarseRestantes--;
        }
        return mensaje;
    }

    // Documento: "Al comenzar el combate, todos los personajes inician con
    // 0 de maná". NUEVO: antes el maná solo arrancaba en 0 al crear el
    // personaje; si la misma party pasaba de piso, arrastraba el maná y los
    // efectos de la batalla anterior.
    public void prepararParaNuevaBatalla() {
        manaActual = 0;
        defendiendo = false;
        turnosInmortalRestantes = 0;
        aturdido = false;
        turnosVenenoRestantes = 0;
        penalizacionVelocidad = 0;
        turnosVelocidadReducidaRestantes = 0;
        penalizacionDefensa = 0;
        turnosDefensaReducidaRestantes = 0;
        turnosImpedidoDeCurarseRestantes = 0;
    }

    // ======================================================================
    // RECIBIR DAÑO / CURACIÓN (se pisan los de Combatiente)
    // ======================================================================

    @Override
    public void recibirDanio(int danio) {
        if (esInmortal()) {
            return;
        }
        int danioFinal = defendiendo ? danio / 2 : danio;
        super.recibirDanio(danioFinal);
    }

    // Mientras dure el Toque de Muerte, cualquier curación se ignora (de
    // habilidad o, más adelante, de poción: todas pasan por acá).
    @Override
    public void recibirCuracion(int cantidad) {
        if (estaImpedidoDeCurarse()) {
            return;
        }
        super.recibirCuracion(cantidad);
    }

    // ======================================================================
    // EFECTOS DE ESTADO (los aplican los enemigos o Revivir)
    // ======================================================================

    public void aturdir() {
        aturdido = true;
    }

    public boolean estaAturdido() {
        return aturdido;
    }

    // Al empezar el turno: si estaba aturdido, "gasta" el aturdimiento y
    // devuelve true (pierde este turno). No se acumula.
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

    public void volverseInmortal(int turnos) {
        turnosInmortalRestantes = turnos;
    }

    public boolean esInmortal() {
        return turnosInmortalRestantes > 0;
    }

    public boolean estaDefendiendo() {
        return defendiendo;
    }

    // Escupitajo del Slime
    public void reducirVelocidad(int cantidad, int turnos) {
        penalizacionVelocidad = cantidad;
        turnosVelocidadReducidaRestantes = turnos;
    }

    // Golpe Debilitante del Goblin
    public void reducirDefensa(int cantidad, int turnos) {
        penalizacionDefensa = cantidad;
        turnosDefensaReducidaRestantes = turnos;
    }

    // Toque de Muerte del Esqueleto
    public void aplicarImpedimentoDeCuracion(int turnos) {
        turnosImpedidoDeCurarseRestantes = turnos;
    }

    public boolean estaImpedidoDeCurarse() {
        return turnosImpedidoDeCurarseRestantes > 0;
    }

    // Se pisan para que la penalización se descuente en TODOS lados (orden
    // de turnos, fórmulas de daño) sin tocar el valor base.
    @Override
    public int getVelocidad() {
        return Math.max(0, super.getVelocidad() - penalizacionVelocidad);
    }

    @Override
    public int getDefensa() {
        return Math.max(0, super.getDefensa() - penalizacionDefensa);
    }

    // Texto corto con los estados activos, para mostrar en pantalla.
    public String describirEstados() {
        String estados = "";
        if (aturdido) estados += " [Aturdido]";
        if (estaEnvenenado()) estados += " [Veneno " + turnosVenenoRestantes + "]";
        if (defendiendo) estados += " [Defendiendo]";
        if (esInmortal()) estados += " [Inmortal " + turnosInmortalRestantes + "]";
        if (turnosVelocidadReducidaRestantes > 0) estados += " [-Vel]";
        if (turnosDefensaReducidaRestantes > 0) estados += " [-Def]";
        if (estaImpedidoDeCurarse()) estados += " [Sin curación]";
        return estados;
    }

    // ======================================================================
    // EXPERIENCIA Y NIVEL
    // ======================================================================

    // NUEVO: conecta la experiencia con subirNivel(), que antes existía
    // pero nadie llamaba. Experiencia necesaria = nivel actual x 100.
    // Devuelve cuántos niveles subió (puede ser más de uno de golpe).
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

    // +3% compuesto a todas las estadísticas. Se usa super.getDefensa()/
    // super.getVelocidad() para crecer sobre el valor BASE, aunque en ese
    // momento tenga un debuff activo.
    private void subirNivel() {
        nivel++;
        setVidaMaxima(crecer(getVidaMaxima()));
        setVidaActual(getVidaMaxima()); // al subir de nivel se cura del todo (a confirmar con el grupo)
        manaMaximo = crecer(manaMaximo);
        ataqueFisico = crecer(ataqueFisico);
        ataqueMagico = crecer(ataqueMagico);
        setDefensa(crecer(super.getDefensa()));
        setVelocidad(crecer(super.getVelocidad()));
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

    // Nombre de la clase para mostrar ("Guerrero", "Mago"...). Cada
    // subclase lo define; evita usar getClass().getSimpleName() en la Vista.
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
