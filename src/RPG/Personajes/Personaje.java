package RPG.Personajes;

// Clase abstracta para los 4 personajes jugables (Guerrero, Mago, Arquero,
// Curador — ver RPG.Personajes.ImplPersonaje). Extiende Combatiente (nombre,
// vida, defensa, velocidad) y agrega todo lo que es propio de un personaje
// de la party: maná, ataque físico, ataque mágico, nivel/experiencia y las
// habilidades.
//
// Basado en Diseno-RPG-POO.docx / Diseno-RPG-POO2.docx (el grupo confirmó
// que ambos documentos describen el mismo diseño; se reemplaza acá el
// sistema viejo de atributos Fuerza/Destreza/Inteligencia/Espiritu/Suerte).
public abstract class Personaje extends Combatiente {

    // ---------- Estadísticas propias del personaje ----------
    private int manaMaximo;
    private int manaActual;
    private int ataqueFisico;
    private int ataqueMagico;
    private int nivel;
    private int experiencia;

    // Se pone en true cuando el personaje usa Defender ese turno (por ahora
    // solo el Guerrero lo hace, ver Guerrero.defender()) y reduce a la mitad
    // el próximo daño que reciba. Se resetea solo después de usarse una vez.
    private boolean defendiendo;

    // Cuántos turnos de inmortalidad le quedan (por ahora solo lo activa el
    // Curador sobre sí misma con Revivir, ver Curador.java). Mientras sea
    // mayor a 0, el personaje no pierde vida por ningún daño que reciba.
    private int turnosInmortalRestantes;

    // Efectos de estado que puede aplicar un enemigo (documento, sección
    // "Efectos de estado"): Aturdimiento (pierde 1 turno, no se acumula) y
    // Veneno (5% de vida máxima al final de su turno, durante 3 turnos).
    private boolean aturdido;
    private int turnosVenenoRestantes;

    // Debuffs de los ataques especiales de los enemigos comunes (no forman
    // parte de los 3 efectos de estado "oficiales" del documento; los pidió
    // el grupo además de esos 3, ver checklist-tpo.txt). Escupitajo del
    // Slime reduce Velocidad; Golpe Debilitante del Goblin reduce Defensa.
    // Los dos siguen el mismo mecanismo: una "penalización" que se resta al
    // valor base mientras dure, y una duración en turnos propios del
    // personaje que se cuenta igual que el Veneno.
    private int penalizacionVelocidad;
    private int turnosVelocidadReducidaRestantes;
    private int penalizacionDefensa;
    private int turnosDefensaReducidaRestantes;

    // Toque de Muerte del Esqueleto (otro pedido del grupo, además de los 3
    // efectos "oficiales" -- ver checklist-tpo.txt): mientras esté activo,
    // el personaje no puede recibir curación de NINGÚN tipo (ítems como
    // pociones, cuando existan, y habilidades como Sanación/Revivir del
    // Curador), durante 2 turnos.
    private int turnosImpedidoDeCurarseRestantes;

    // nombre, vidaMaxima, manaMaximo, ataqueFisico, ataqueMagico, defensa,
    // velocidad: los 7 valores de Nivel 1 que trae cada subclase desde la
    // tabla "Las 4 clases" del documento de diseño.
    public Personaje(String nombre, int vidaMaxima, int manaMaximo, int ataqueFisico,
                      int ataqueMagico, int defensa, int velocidad) {
        super(nombre, vidaMaxima, defensa, velocidad);
        this.manaMaximo = manaMaximo;
        this.manaActual = 0; // el maná arranca en 0 al empezar la batalla (documento, sección "Cómo funciona el maná")
        this.ataqueFisico = ataqueFisico;
        this.ataqueMagico = ataqueMagico;
        this.nivel = 1;
        this.experiencia = 0;
        this.defendiendo = false;
        this.turnosInmortalRestantes = 0;
        this.aturdido = false;
        this.turnosVenenoRestantes = 0;
        this.penalizacionVelocidad = 0;
        this.turnosVelocidadReducidaRestantes = 0;
        this.penalizacionDefensa = 0;
        this.turnosDefensaReducidaRestantes = 0;
        this.turnosImpedidoDeCurarseRestantes = 0;
    }

    // ---------- Ataque básico (gratis, siempre disponible) ----------
    // daño = ataque físico del atacante - defensa del objetivo, mínimo 1
    // (documento, sección "Cómo se calcula el daño"). El ataque básico
    // siempre es físico: las habilidades son las que pueden ser mágicas.
    public void atacar(Combatiente objetivo) {
        int danio = calcularDanioFisico(objetivo);
        objetivo.recibirDanio(danio);
    }

    // ---------- Habilidades: básica y poderosa ----------
    // Cada clase decide su propio costo de maná, su propio multiplicador y
    // si el objetivo recibe daño o curación (caso del Curador). Por eso son
    // abstractas acá: el código que maneja el turno solo necesita saber que
    // "todo Personaje tiene una habilidad básica y una poderosa", sin
    // importarle de qué clase concreta se trata (polimorfismo -- ver
    // Diseno-RPG-POO.docx, sección GRASP/MVC).
    public abstract void usarHabilidadBasica(Combatiente objetivo);
    public abstract void usarHabilidadPoderosa(Combatiente objetivo);

    // Nombres y costos, para que Main (o más adelante la pantalla de
    // batalla) pueda mostrarlos sin tener que preguntarle a cada subclase
    // "¿vos qué clase sos?".
    public abstract String getNombreHabilidadBasica();
    public abstract int getCostoHabilidadBasica();
    public abstract String getNombreHabilidadPoderosa();
    public abstract int getCostoHabilidadPoderosa();

    // Devuelve true si hay maná suficiente para pagar ese costo. La usan las
    // subclases antes de ejecutar el efecto de la habilidad, y más adelante
    // la van a usar las pantallas para saber qué opciones mostrar
    // habilitadas.
    public boolean puedeUsarHabilidad(int costo) {
        return manaActual >= costo;
    }

    // Resta el costo de maná. Se llama SOLO si puedeUsarHabilidad(costo) dio
    // true (cada subclase lo valida en su propia habilidad antes de restar).
    protected void gastarMana(int costo) {
        manaActual -= costo;
    }

    // ---------- Maná por turno ----------
    // Si el personaje NO usó una habilidad en su turno, el maná le sube +10
    // (documento: "cada vez que le toca actuar a un personaje, si no usó una
    // habilidad ese turno, su maná sube +10"). Todavía no hay un motor de
    // turnos que llame esto automáticamente (ver checklist-tpo.txt); por
    // ahora se llama a mano, como se prueba en Main.
    public void crecerManaPorTurno() {
        manaActual += 10;
        if (manaActual > manaMaximo) {
            manaActual = manaMaximo;
        }
    }

    // ---------- Defender (por ahora solo la usa Guerrero) ----------
    // Reduce a la mitad el PRÓXIMO daño que reciba este personaje, y después
    // se desactiva sola (dura "ese turno", no se acumula ronda tras ronda).
    protected void activarDefensa() {
        defendiendo = true;
    }

    // Se pisa el recibirDanio() heredado de Combatiente para aplicar el
    // descuento de Defender (y, si corresponde, la inmortalidad) antes de
    // restar la vida. Es el mismo mecanismo de siempre (daño = ataque -
    // defensa, ya calculado por quien ataca); acá solo se lo modifica según
    // el estado del personaje.
    @Override
    public void recibirDanio(int danio) {
        if (esInmortal()) {
            return; // inmortal: no pierde vida sin importar el daño que reciba
        }
        int danioFinal = danio;
        if (defendiendo) {
            danioFinal = danio / 2;
            defendiendo = false; // Defender se consume con el primer golpe que recibe
        }
        super.recibirDanio(danioFinal);
    }

    // ---------- Inmortalidad (por ahora solo la otorga Revivir del Curador) ----------
    protected void activarInmortalidad(int turnos) {
        turnosInmortalRestantes = turnos;
    }

    public boolean esInmortal() {
        return turnosInmortalRestantes > 0;
    }

    // Va a llamarla el motor de turnos (todavía no existe) una vez por
    // ronda, para que la inmortalidad se vaya agotando con el tiempo en vez
    // de durar para siempre. El resto de los efectos de estado con duración
    // (Aturdimiento, Veneno, Escudo — ver checklist-tpo.txt, FALTA - FASE B)
    // van a necesitar un mecanismo parecido cuando se implementen.
    public void reducirDuracionInmortalidad() {
        if (turnosInmortalRestantes > 0) {
            turnosInmortalRestantes--;
        }
    }

    // ---------- Efectos de estado: Aturdimiento y Veneno (los aplica un enemigo) ----------
    // Aturdimiento: el personaje pierde su próximo turno entero (no se
    // "acumula" -- volver a aturdir a alguien ya aturdido no hace nada
    // extra). Lo consulta MotorDeCombate al empezar el turno del personaje.
    public void aturdir() {
        aturdido = true;
    }

    public boolean estaAturdido() {
        return aturdido;
    }

    // Se llama al empezar el turno del personaje. Si estaba aturdido,
    // "gasta" el aturdimiento (vuelve a false) y avisa que el turno se
    // pierde; si no estaba aturdido, no hace nada y devuelve false.
    public boolean consumirAturdimiento() {
        if (aturdido) {
            aturdido = false;
            return true;
        }
        return false;
    }

    // Veneno: durante 3 turnos (contando el propio, ver
    // aplicarVenenoSiCorresponde) pierde 5% de su vida máxima al final de
    // cada turno suyo. Volver a envenenar a alguien ya envenenado reinicia
    // la duración a 3 (mismo criterio simple que usa Defender/Inmortalidad).
    private static final int DURACION_VENENO = 3;
    private static final double PORCENTAJE_VIDA_POR_VENENO = 0.05;

    public void envenenar() {
        turnosVenenoRestantes = DURACION_VENENO;
    }

    public boolean estaEnvenenado() {
        return turnosVenenoRestantes > 0;
    }

    // Se llama al final del turno del personaje (después de actuar, haya
    // podido actuar o no). Si tiene veneno activo, le resta el 5% de su
    // vida máxima y descuenta un turno de duración.
    public void aplicarVenenoSiCorresponde() {
        if (turnosVenenoRestantes > 0) {
            int danioVeneno = (int) Math.round(getVidaMaxima() * PORCENTAJE_VIDA_POR_VENENO);
            recibirDanio(danioVeneno);
            System.out.println(getNombre() + " pierde " + danioVeneno + " de vida por el veneno.");
            turnosVenenoRestantes--;
        }
    }

    // ---------- Debuffs de los ataques especiales (Escupitajo / Golpe Debilitante) ----------
    // Se pisan getVelocidad()/getDefensa() (heredados de Combatiente) para
    // que la penalización se aplique en todos lados sin tocar nada más:
    // MotorDeCombate ya usa getVelocidad() para el orden de turnos, y
    // calcularDanioFisico/Magico (acá y en Enemigo) ya usan getDefensa()
    // del objetivo. No se toca el valor base (setVelocidad/setDefensa)
    // para no arrastrar la penalización si el personaje sube de nivel a
    // mitad de un debuff (ver subirNivel(), que usa super.getVelocidad()/
    // super.getDefensa() para crecer siempre sobre el valor base real).
    public void reducirVelocidad(int cantidad, int turnos) {
        penalizacionVelocidad = cantidad;
        turnosVelocidadReducidaRestantes = turnos;
    }

    public boolean tieneVelocidadReducida() {
        return turnosVelocidadReducidaRestantes > 0;
    }

    @Override
    public int getVelocidad() {
        return Math.max(0, super.getVelocidad() - penalizacionVelocidad);
    }

    private void actualizarDuracionVelocidadReducida() {
        if (turnosVelocidadReducidaRestantes > 0) {
            turnosVelocidadReducidaRestantes--;
            if (turnosVelocidadReducidaRestantes == 0) {
                penalizacionVelocidad = 0;
            }
        }
    }

    public void reducirDefensa(int cantidad, int turnos) {
        penalizacionDefensa = cantidad;
        turnosDefensaReducidaRestantes = turnos;
    }

    public boolean tieneDefensaReducida() {
        return turnosDefensaReducidaRestantes > 0;
    }

    @Override
    public int getDefensa() {
        return Math.max(0, super.getDefensa() - penalizacionDefensa);
    }

    private void actualizarDuracionDefensaReducida() {
        if (turnosDefensaReducidaRestantes > 0) {
            turnosDefensaReducidaRestantes--;
            if (turnosDefensaReducidaRestantes == 0) {
                penalizacionDefensa = 0;
            }
        }
    }

    // ---------- Toque de Muerte (bloquea toda curación durante 2 turnos) ----------
    public void aplicarImpedimentoDeCuracion(int turnos) {
        turnosImpedidoDeCurarseRestantes = turnos;
    }

    public boolean estaImpedidoDeCurarse() {
        return turnosImpedidoDeCurarseRestantes > 0;
    }

    // Se pisa recibirCuracion() (heredado de Combatiente) para que, mientras
    // dure el impedimento, se ignore CUALQUIER curación -- no importa si
    // viene de una habilidad (Sanación/Revivir del Curador) o, más
    // adelante, de un ítem (Poción de vida): las dos rutas terminan
    // llamando a este mismo método (GRASP Information Expert: el propio
    // Personaje es el único que necesita saber si puede curarse o no, nadie
    // más tiene que preguntarlo antes de intentar curar).
    @Override
    public void recibirCuracion(int cantidad) {
        if (estaImpedidoDeCurarse()) {
            System.out.println(getNombre() + " no puede curarse: sigue bajo el efecto de Toque de Muerte.");
            return;
        }
        super.recibirCuracion(cantidad);
    }

    private void actualizarDuracionImpedimentoDeCuracion() {
        if (turnosImpedidoDeCurarseRestantes > 0) {
            turnosImpedidoDeCurarseRestantes--;
        }
    }

    // Punto único que llama MotorDeCombate al final del turno de este
    // personaje (haya podido actuar o no, por ejemplo si estaba aturdido):
    // hace ir bajando la duración de TODOS los efectos con duración propia
    // (Veneno, Inmortalidad, y los dos debuffs de los especiales). Antes la
    // Inmortalidad solo se probaba a mano desde Main y nunca se agotaba
    // sola en una batalla real con MotorDeCombate; queda arreglado acá de
    // paso (GRASP Information Expert: el propio Personaje es quien sabe
    // qué efectos tiene y cómo se le van agotando).
    public void actualizarEfectosDeFinDeTurno() {
        aplicarVenenoSiCorresponde();
        reducirDuracionInmortalidad();
        actualizarDuracionVelocidadReducida();
        actualizarDuracionDefensaReducida();
        actualizarDuracionImpedimentoDeCuracion();
    }

    // ---------- Cálculo de daño (física y mágico comparten la fórmula) ----------
    // Se dejan como protected para que las subclases las usen al implementar
    // sus habilidades (ej: Tajo del Guerrero = calcularDanioFisico() * 1.5).
    protected int calcularDanioFisico(Combatiente objetivo) {
        int danio = ataqueFisico - objetivo.getDefensa();
        return Math.max(1, danio);
    }

    protected int calcularDanioMagico(Combatiente objetivo) {
        int danio = ataqueMagico - objetivo.getDefensa();
        return Math.max(1, danio);
    }

    // ---------- Subir de nivel ----------
    // +3% compuesto a todas las estadísticas (documento, sección "Subir de
    // nivel"), hasta Nivel 20. Todavía no está conectado a experiencia real
    // (falta el motor de combate que reparta experiencia); por ahora es un
    // método que se puede llamar a mano para probar el cálculo.
    private static final int NIVEL_MAXIMO = 20;
    private static final double CRECIMIENTO_POR_NIVEL = 1.03; // +3% compuesto

    public void subirNivel() {
        if (nivel >= NIVEL_MAXIMO) {
            return;
        }
        nivel++;
        setVidaMaxima(crecer(getVidaMaxima()));
        setVidaActual(getVidaMaxima()); // al subir de nivel se cura del todo (regla simple, a confirmar con el grupo)
        manaMaximo = crecer(manaMaximo);
        ataqueFisico = crecer(ataqueFisico);
        if (ataqueMagico > 0) {
            ataqueMagico = crecer(ataqueMagico);
        }
        // super.getDefensa()/getVelocidad() (no getDefensa()/getVelocidad()
        // de acá) para crecer siempre sobre el valor BASE, aunque el
        // personaje esté con Velocidad o Defensa reducidas por un ataque
        // especial en el momento de subir de nivel.
        setDefensa(crecer(super.getDefensa()));
        setVelocidad(crecer(super.getVelocidad()));
    }

    private int crecer(int valor) {
        return (int) Math.round(valor * CRECIMIENTO_POR_NIVEL);
    }

    // ---------- Getters ----------
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
