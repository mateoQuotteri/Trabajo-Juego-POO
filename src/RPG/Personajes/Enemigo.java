package RPG.Personajes;

// Clase abstracta para los enemigos. Extiende Combatiente igual que
// Personaje (comparten nombre/vida/defensa/velocidad), y agrega lo que es
// propio de un enemigo: su ataque y su nivel.
//
// Jerarquía real de enemigos (documento de diseño, sección "Enemigos"):
// Slime / Goblin / Jefe / Jefe Final (ver RPG.Personajes.ImplEnemigo), cada
// uno con su propia Vida/Ataque/Defensa/Velocidad y su propia IA por lista
// ponderada (actuar()). Esqueleto sigue siendo el enemigo de prueba
// placeholder que ya existía, no uno de los 4 oficiales, pero también
// implementa actuar() para poder seguir usándose en las pruebas.
public abstract class Enemigo extends Combatiente {

    private int ataque;
    private int nivel;

    public Enemigo(String nombre, int vidaMaxima, int ataque, int defensa, int velocidad, int nivel) {
        super(nombre, vidaMaxima, defensa, velocidad);
        this.ataque = ataque;
        this.nivel = nivel;
    }

    // Ataque básico del enemigo contra un objetivo (normalmente un
    // Personaje). Misma fórmula que el ataque del personaje: daño = ataque -
    // defensa del objetivo, mínimo 1.
    public void atacar(Combatiente objetivo) {
        int danio = Math.max(1, ataque - objetivo.getDefensa());
        objetivo.recibirDanio(danio);
    }

    // Misma fórmula de siempre (ataque - defensa del objetivo, mínimo 1),
    // pero multiplicando el ataque primero -- la usan los ataques
    // especiales de los enemigos comunes (Escupitajo del Slime, Golpe
    // Debilitante del Goblin), que pegan "un poco más" que el ataque común.
    // Vive acá (no en cada subclase) porque la fórmula en sí es la misma
    // para cualquier especial; lo único que cambia entre uno y otro es el
    // multiplicador y qué efecto adicional aplican, y eso lo define cada
    // subclase en su propio método privado.
    protected int calcularDanioConMultiplicador(Combatiente objetivo, double multiplicador) {
        int danio = (int) Math.round(ataque * multiplicador) - objetivo.getDefensa();
        return Math.max(1, danio);
    }

    // Decide y ejecuta la acción del enemigo en su turno (documento,
    // sección "Cómo deciden los enemigos qué hacer" -- lista ponderada de
    // probabilidades por tipo de enemigo). Cada subclase concreta define su
    // propia lista de acciones posibles; acá solo viven los helpers
    // compartidos para no repetir el mecanismo de "elegir de una lista
    // ponderada" en cada una (polimorfismo: MotorDeCombate solo necesita
    // saber que "todo Enemigo sabe actuar()", sin importarle cuál es su IA).
    public abstract void actuar(Personaje objetivo);

    // Recibe un array donde cada acción aparece repetida tantas veces como
    // "peso" tiene (ej: {"atacar","atacar","atacar","atacar","atacar",
    // "atacar","atacar","aturdir","aturdir","aturdir"} = 70%/30%) y devuelve
    // una elegida al azar con esa proporción.
    protected String elegirAccionPonderada(String[] pool) {
        int indice = (int) (Math.random() * pool.length);
        return pool[indice];
    }

    // Ejecuta sobre el objetivo la acción elegida por elegirAccionPonderada.
    // Vive acá (no en cada subclase) porque las 3 acciones posibles
    // (atacar/aturdir/envenenar) son siempre las mismas, solo cambia con qué
    // probabilidad las usa cada tipo de enemigo.
    protected void ejecutarAccionSobre(String accion, Personaje objetivo) {
        switch (accion) {
            case "atacar":
                atacar(objetivo);
                System.out.println(getNombre() + " ataca a " + objetivo.getNombre()
                        + ". Vida restante: " + objetivo.getVidaActual());
                break;
            case "aturdir":
                objetivo.aturdir();
                System.out.println(getNombre() + " aturde a " + objetivo.getNombre()
                        + ". Va a perder su próximo turno.");
                break;
            case "envenenar":
                objetivo.envenenar();
                System.out.println(getNombre() + " envenena a " + objetivo.getNombre()
                        + ". Va a perder vida durante 3 turnos.");
                break;
        }
    }

    public int getAtaque() {
        return ataque;
    }

    public int getNivel() {
        return nivel;
    }
}
