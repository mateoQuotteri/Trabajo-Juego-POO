package RPG.Personajes;

// Clase base (abstracta) de todos los personajes jugables. Guerrero, Mago,
// Cazador y Sacerdote heredan de acá. Todo lo que es común a cualquier
// personaje (datos, atributos, ataque básico, recibir daño) vive en esta
// clase; lo que cambia según la clase concreta queda como método abstracto
// (multiplicadorFuerza(), etc. y usarHabilidad()), que cada subclase
// implementa a su manera (polimorfismo).
public abstract class Personaje {

    // ---------- Datos básicos del personaje ----------
    private String nombre;
    private int vida;
    private int mana;
    private int nivel;
    private int ataque;   // daño base del ataque físico, antes del bono de Fuerza
    private int defensa;  // resta daño recibido en recibirDanio()

    // ---------- Atributos ----------
    // Al crear el personaje, cada uno arranca en un valor aleatorio entre 1
    // y 4 (ver valorAleatorioAtributo()). Después, el jugador suma puntos
    // por encima de esa base: al iniciar la partida y en cada subida de
    // nivel (ver sumarAtributos() más abajo y la clase RepartidorAtributos,
    // que es quien pide esos puntos por consola).
    private int fuerza;        // daño cuerpo a cuerpo (usado en atacar())
    private int destreza;      // agilidad / puntería (usado por la habilidad del Cazador)
    private int inteligencia;  // poder mágico ofensivo (usado por la habilidad del Mago)
    private int espiritu;      // poder mágico de soporte (usado por la habilidad del Sacerdote)
    private int suerte;        // no afecta el combate: cuantos más puntos tiene, más puntos
                                // extra da el personaje para repartir en el resto de sus
                                // atributos (ver RepartidorAtributos)

    // ---------- Habilidades ----------
    // Todo personaje tiene 4 habilidades (3 "menores" + 1 Ulti). Se guardan
    // en arrays de tamaño fijo (4), en vez de una clase aparte por
    // habilidad, para no necesitar nada más que lo ya visto en clase
    // (arrays + polimorfismo de la clase concreta). Cada subclase que ya
    // tenga sus habilidades definidas (por ahora, Sacerdote) llama a
    // configurarHabilidades() en su constructor para reemplazar estos
    // valores genéricos por los suyos; las clases que todavía no las
    // definieron (Guerrero, Cazador, Mago) se quedan con estos valores
    // "a definir" sin romper nada.
    private static final int CANTIDAD_HABILIDADES = 4;
    private static final int NIVEL_MAXIMO_PERSONAJE = 30; // tope de nivel de personaje

    private String[] nombresHabilidades;      // nombre de cada una de las 4 habilidades
    private int[] nivelesHabilidades;         // nivel actual de cada habilidad (arranca en 1)
    private int[] nivelesMaximosHabilidades;  // tope de nivel de cada habilidad (9 para las
                                               // menores, 6 para la Ulti, según el diseño)
    private int[] cooldownsEnTurnos;          // recarga de cada habilidad, en turnos (todavía
                                               // no se usa: depende del motor de combate)
    private int puntosDeHabilidad;            // puntos sin gastar; se gana 1 por cada nivel
                                               // de personaje (ver subirNivel())

    // Constructor: recibe los datos "de clase" (los valores fijos que le
    // pasa cada subclase, como Guerrero o Mago) y arranca los 5 atributos
    // en un valor aleatorio. El nivel siempre arranca en 1.
    public Personaje(String nombre, int vida, int mana, int ataque, int defensa){
        this.nombre = nombre;
        this.vida = vida;
        this.mana = mana;
        this.nivel = 1;
        this.ataque = ataque;
        this.defensa = defensa;

        this.fuerza = valorAleatorioAtributo();
        this.destreza = valorAleatorioAtributo();
        this.inteligencia = valorAleatorioAtributo();
        this.espiritu = valorAleatorioAtributo();
        this.suerte = valorAleatorioAtributo();

        // Valores genéricos de habilidades, por si la subclase todavía no
        // definió las suyas (ver configurarHabilidades()).
        this.nombresHabilidades = new String[]{
                "Habilidad 1 (a definir)", "Habilidad 2 (a definir)",
                "Habilidad 3 (a definir)", "Ulti (a definir)"
        };
        this.nivelesMaximosHabilidades = new int[]{9, 9, 9, 6};
        this.cooldownsEnTurnos = new int[]{2, 4, 4, 6};
        this.nivelesHabilidades = new int[CANTIDAD_HABILIDADES];
        for (int i = 0; i < CANTIDAD_HABILIDADES; i++) {
            this.nivelesHabilidades[i] = 1; // toda habilidad arranca en Nivel 1
        }
        this.puntosDeHabilidad = 0;
    }

    // Devuelve un número entero al azar entre 1 y 4 (inclusive). Se usa para
    // la base aleatoria de cada atributo al crear el personaje.
    private int valorAleatorioAtributo(){
        return (int) (Math.random() * 4) + 1;
    }

    public int getNivel(){
        return nivel;
    }

    // ---------- Acciones de combate ----------

    // Ataque cuerpo a cuerpo básico, disponible para cualquier personaje.
    // El daño es el ataque base más un bono de Fuerza. Ese bono depende de
    // qué tan efectiva es la Fuerza para la clase concreta del personaje
    // (multiplicadorFuerza(): x1 Guerrero, x0.5 Cazador, x0.25 Mago/Sacerdote).
    public void atacar(Enemigo objetivo){
        int bonoFuerza = bonoDe(fuerza, multiplicadorFuerza());
        int danio = (ataque + bonoFuerza) * nivel;
        objetivo.recibirDanio(danio);
    }

    // Aplica daño a este personaje, restando su defensa. Devuelve la vida
    // restante.
    public int recibirDanio(int danio){
        vida -= danio - defensa;
        return vida;
    }

    // Sube un nivel al personaje (hasta el tope de NIVEL_MAXIMO_PERSONAJE) y
    // le da 1 punto de habilidad para gastar (ver subirNivelHabilidad() y la
    // clase RepartidorHabilidades, que es quien pide ese punto por consola).
    // No reparte puntos de atributo por sí sola: eso lo sigue haciendo
    // RepartidorAtributos.repartirPorNivel(), aparte.
    // Devuelve true si efectivamente subió de nivel, o false si ya estaba
    // en el nivel máximo (para que quien la llame sepa si no pasó nada).
    public boolean subirNivel(){
        if (nivel >= NIVEL_MAXIMO_PERSONAJE) {
            return false;
        }
        nivel += 1;
        puntosDeHabilidad += 1;
        return true;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    // ---------- Getters de atributos ----------
    public int getFuerza() {
        return fuerza;
    }

    public int getDestreza() {
        return destreza;
    }

    public int getInteligencia() {
        return inteligencia;
    }

    public int getEspiritu() {
        return espiritu;
    }

    public int getSuerte() {
        return suerte;
    }

    // Suma puntos por encima de los que el personaje ya tiene (la base
    // aleatoria de la creación, más lo acumulado en niveles anteriores).
    // La usa RepartidorAtributos tanto al crear la partida como en cada
    // subida de nivel, después de que el jugador decide cuánto poner en
    // cada atributo.
    public void sumarAtributos(int fuerza, int destreza, int inteligencia, int espiritu, int suerte){
        this.fuerza += fuerza;
        this.destreza += destreza;
        this.inteligencia += inteligencia;
        this.espiritu += espiritu;
        this.suerte += suerte;
    }

    // ---------- Habilidades: configuración y reparto de puntos ----------

    // Lo llama el constructor de una subclase (ej. Sacerdote) cuando ya
    // tiene definidas sus 4 habilidades reales, para reemplazar los
    // nombres/topes/cooldowns genéricos de arriba por los suyos. Los 3
    // arrays tienen que tener tamaño 4 (CANTIDAD_HABILIDADES): posiciones
    // 0, 1 y 2 para las habilidades "menores" y posición 3 para la Ulti.
    // Reinicia los niveles de habilidad a 1 (recién configuradas).
    protected void configurarHabilidades(String[] nombres, int[] nivelesMaximos, int[] cooldownsEnTurnos){
        this.nombresHabilidades = nombres;
        this.nivelesMaximosHabilidades = nivelesMaximos;
        this.cooldownsEnTurnos = cooldownsEnTurnos;
        this.nivelesHabilidades = new int[CANTIDAD_HABILIDADES];
        for (int i = 0; i < CANTIDAD_HABILIDADES; i++) {
            this.nivelesHabilidades[i] = 1;
        }
    }

    // Gasta 1 punto de habilidad para subir de nivel la habilidad en la
    // posición "indice" (0 a 3). Devuelve false (sin gastar el punto) si el
    // índice no existe, si no quedan puntos, o si esa habilidad ya está en
    // su nivel máximo. La usa RepartidorHabilidades, después de preguntarle
    // al jugador dónde quiere poner el punto.
    public boolean subirNivelHabilidad(int indice){
        if (indice < 0 || indice >= CANTIDAD_HABILIDADES) {
            return false;
        }
        if (puntosDeHabilidad <= 0) {
            return false;
        }
        if (nivelesHabilidades[indice] >= nivelesMaximosHabilidades[indice]) {
            return false;
        }
        nivelesHabilidades[indice] += 1;
        puntosDeHabilidad -= 1;
        return true;
    }

    // ---------- Getters de habilidades ----------
    public int getPuntosDeHabilidad() {
        return puntosDeHabilidad;
    }

    public int getCantidadHabilidades() {
        return CANTIDAD_HABILIDADES;
    }

    public String getNombreHabilidad(int indice) {
        return nombresHabilidades[indice];
    }

    public int getNivelHabilidad(int indice) {
        return nivelesHabilidades[indice];
    }

    public int getNivelMaximoHabilidad(int indice) {
        return nivelesMaximosHabilidades[indice];
    }

    public int getCooldownHabilidad(int indice) {
        return cooldownsEnTurnos[indice];
    }

    public static int getNivelMaximoPersonaje() {
        return NIVEL_MAXIMO_PERSONAJE;
    }

    // Multiplica un valor de atributo por el multiplicador de efectividad
    // de la clase (ver multiplicador...() más abajo), y lo redondea a
    // entero. La usan atacar() (con Fuerza) y usarHabilidad() de cada
    // subclase (con su atributo principal).
    protected int bonoDe(int valorAtributo, double multiplicador){
        return (int) (valorAtributo * multiplicador);
    }

    // ---------- Métodos abstractos: los define cada subclase ----------
    // Qué tan efectivo es cada atributo para el daño cuerpo a cuerpo
    // (atacar(), que siempre usa Fuerza) y para las habilidades
    // (usarHabilidad(), que cada clase implementa con su propio atributo
    // principal). Por ejemplo, Guerrero devuelve 1.0 en multiplicadorFuerza()
    // porque para él la Fuerza rinde al máximo; Mago devuelve 0.25 porque
    // para un mago la fuerza física rinde poco.
    public abstract double multiplicadorFuerza();
    public abstract double multiplicadorDestreza();
    public abstract double multiplicadorInteligencia();
    public abstract double multiplicadorEspiritu();

    // Habilidad especial de la clase. Cada subclase decide qué atributo usa
    // y cómo calcula el daño (ver Guerrero/Cazador/Mago/Sacerdote).
    public abstract void usarHabilidad(Enemigo objetivo);
}
