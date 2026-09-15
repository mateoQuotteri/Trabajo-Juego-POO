package RPG.Personajes.ImplPersonaje;

import RPG.Personajes.Enemigo;
import RPG.Personajes.Personaje;

// Personaje mágico de soporte (curador): es la clase que más aprovecha el
// Espiritu en su habilidad especial. Igual que el Mago, es frágil en
// combate cuerpo a cuerpo.
public class Sacerdote extends Personaje {

    // ---------- Habilidades del Sacerdote (ver checklist-tpo.txt) ----------
    // Posición 0, 1 y 2: habilidades menores (tope Nivel 9). Posición 3: la
    // Ulti (tope Nivel 6, sube la mitad de veces que una menor). El cooldown
    // está en turnos porque el juego es por turnos; todavía no se usa en
    // combate real (no existe el motor de turnos todavía), pero ya queda
    // definido acá para no perderlo.
    private static final String[] NOMBRES_HABILIDADES = {
            "Curación", "Buff de Ataque", "Buff de Defensa", "Revivir (Ulti)"
    };
    private static final int[] NIVELES_MAXIMOS_HABILIDADES = {9, 9, 9, 6};
    private static final int[] COOLDOWNS_HABILIDADES_EN_TURNOS = {2, 4, 4, 6};

    // Índices dentro de esos arrays, para no usar "números mágicos" al leer
    // el nivel de una habilidad puntual (ver los getPorcentaje... de abajo).
    private static final int INDICE_CURACION = 0;
    private static final int INDICE_BUFF_ATAQUE = 1;
    private static final int INDICE_BUFF_DEFENSA = 2;
    private static final int INDICE_ULTI_REVIVIR = 3;

    // nombre, vida, mana, ataque, defensa (los atributos los pone la clase
    // base, al azar, y se completan con el reparto de puntos).
    public Sacerdote(String nombre){
        // Velocidad intermedia, algo menor que el Mago: 18.
        super(nombre, 120, 300, 15, 20, 18);
        // Reemplaza las habilidades genéricas de Personaje por las reales
        // del Sacerdote (nombres, topes de nivel y cooldowns de arriba).
        configurarHabilidades(NOMBRES_HABILIDADES, NIVELES_MAXIMOS_HABILIDADES, COOLDOWNS_HABILIDADES_EN_TURNOS);
    }

    // ---------- Porcentajes de efecto de cada habilidad, según su nivel ----------
    // Ojo: esto solo calcula el % que le correspondería a la habilidad según
    // su nivel actual. Todavía no se aplica el efecto de verdad (curar la
    // vida de un objetivo, buffear a toda la party, revivir a un
    // compañero caído), porque eso necesita acceso a la Party y al estado
    // de la batalla, y se va a implementar junto con el motor de combate.

    // Curación: 8% de la vida máxima en Nivel 1, +2% por nivel de la
    // habilidad, hasta 24% en Nivel 9.
    public int getPorcentajeCuracion(){
        int nivelHabilidad = getNivelHabilidad(INDICE_CURACION);
        return 8 + (nivelHabilidad - 1) * 2;
    }

    // Buff de Ataque: +10% de ataque en Nivel 1, +2% por nivel, hasta +26%
    // en Nivel 9. Afecta a todos los personajes vivos de la party (no solo
    // a quien usa la habilidad).
    public int getPorcentajeBuffAtaque(){
        int nivelHabilidad = getNivelHabilidad(INDICE_BUFF_ATAQUE);
        return 10 + (nivelHabilidad - 1) * 2;
    }

    // Buff de Defensa: misma fórmula que el de Ataque, pero sobre la
    // defensa de toda la party viva.
    public int getPorcentajeBuffDefensa(){
        int nivelHabilidad = getNivelHabilidad(INDICE_BUFF_DEFENSA);
        return 10 + (nivelHabilidad - 1) * 2;
    }

    // Ulti - Revivir: 20% de vida restaurada en Nivel 1, +4% por nivel,
    // hasta el tope de 40% en Nivel 6 (el tope máximo pedido en el diseño).
    public int getPorcentajeRevivir(){
        int nivelHabilidad = getNivelHabilidad(INDICE_ULTI_REVIVIR);
        return 20 + (nivelHabilidad - 1) * 4;
    }

    // ---------- Efectividad de cada atributo para esta clase ----------
    // La Fuerza y la Destreza (combate físico) le rinden poco: es un
    // personaje frágil cuerpo a cuerpo.
    @Override
    public double multiplicadorFuerza() {
        return 0.25;
    }

    @Override
    public double multiplicadorDestreza() {
        return 0.25;
    }

    // La Inteligencia (magia ofensiva) le rinde a medias, porque comparte
    // algo de esa afinidad mágica con el Mago.
    @Override
    public double multiplicadorInteligencia() {
        return 0.5;
    }

    // El Espiritu es su atributo principal (multiplicador 1.0): de ahí sale
    // el "daño" (o efecto) de su habilidad especial.
    @Override
    public double multiplicadorEspiritu() {
        return 1.0;
    }

    // Habilidad especial del Sacerdote: basada en su Espiritu (con el
    // multiplicador de esta clase, que es 1.0) y su nivel. Por ahora usa
    // recibirDanio() igual que las demás clases (todavía no hay curación
    // implementada; ver checklist-tpo.txt).
    @Override
    public void usarHabilidad(Enemigo objetivo){
        int danio = bonoDe(getEspiritu(), multiplicadorEspiritu()) * getNivel();
        objetivo.recibirDanio(danio);
    }
}
