package RPG.Personajes.ImplPersonaje;

import RPG.Personajes.Enemigo;
import RPG.Personajes.Personaje;

// Personaje cuerpo a cuerpo: es la clase que más aprovecha la Fuerza, tanto
// en el ataque básico (heredado de Personaje) como en su habilidad especial.
public class Guerrero extends Personaje {

    // ---------- Habilidades del Guerrero (ver checklist-tpo.txt) ----------
    // Posiciones 0 y 2: dos ataques (Habilidad 1 y 3). Posición 1: pasiva de
    // defensa (siempre activa, sin cooldown real). Posición 3: la Ulti
    // (Grito de Guerra). Topes de nivel: 9 para las 3 menores, 6 para la
    // Ulti (igual que el esquema general, ver Personaje).
    private static final String[] NOMBRES_HABILIDADES = {
            "Golpe Fuerte", "Piel de Hierro (pasiva)", "Golpe Devastador", "Grito de Guerra (Ulti)"
    };
    private static final int[] NIVELES_MAXIMOS_HABILIDADES = {9, 9, 9, 6};
    // La pasiva (índice 1) no tiene cooldown de verdad (siempre está activa
    // mientras esté en Nivel 1 o más); se deja en 0 para indicar "no aplica".
    private static final int[] COOLDOWNS_HABILIDADES_EN_TURNOS = {2, 0, 4, 7};

    // Índices dentro de esos arrays (para no usar números mágicos abajo).
    private static final int INDICE_GOLPE_FUERTE = 0;
    private static final int INDICE_PIEL_DE_HIERRO = 1;
    private static final int INDICE_GOLPE_DEVASTADOR = 2;
    private static final int INDICE_GRITO_DE_GUERRA = 3;

    // ---------- Constantes de las fórmulas de daño/efecto ----------
    // Golpe Fuerte (Habilidad 1): en Nivel 1 pega el doble del ataque
    // básico; ese "doble" (el daño base) crece un 15% por cada nivel de la
    // habilidad, y por separado se le suma +8 de daño por cada punto de
    // Fuerza que tenga el personaje (ya sea por puntos repartidos o por
    // ítems, porque ambos suman directo al mismo valor de Fuerza).
    private static final int MULTIPLICADOR_BASE_GOLPE_FUERTE = 2;
    private static final double CRECIMIENTO_PORCENTUAL_POR_NIVEL = 0.15; // 15% por nivel, ambos ataques
    private static final int BONO_FUERZA_GOLPE_FUERTE = 8;

    // Golpe Devastador (Habilidad 3): en Nivel 1 pega el doble de lo que
    // pega Golpe Fuerte en su Nivel 1 (o sea, 4 veces el ataque básico), con
    // el mismo crecimiento de 15% por nivel, y +10 de daño por cada punto
    // de Fuerza (en vez de +8).
    private static final int MULTIPLICADOR_BASE_GOLPE_DEVASTADOR = MULTIPLICADOR_BASE_GOLPE_FUERTE * 2;
    private static final int BONO_FUERZA_GOLPE_DEVASTADOR = 10;

    // Piel de Hierro (Habilidad 2, pasiva): suma el mismo bono a la defensa
    // física y a la defensa mágica. Nivel 1 = +5, y sube +5 por nivel hasta
    // +45 en Nivel 9 ((45-5)/(9-1) = 5 por nivel).
    private static final int BONO_DEFENSA_BASE_PIEL_DE_HIERRO = 5;
    private static final int BONO_DEFENSA_POR_NIVEL_PIEL_DE_HIERRO = 5;

    // Grito de Guerra (Ulti): aumenta la velocidad de toda la party viva
    // durante 2 turnos. El % de aumento no lo dio Conrado ("te dejo la
    // decisión a vos"): elegí +20% en Nivel 1 y +4% por nivel hasta +40%
    // en Nivel 6, para que coincida con la misma escala que ya usa la Ulti
    // del Sacerdote (Revivir), y así las 4 Ultis del juego crecen parejo.
    private static final int PORCENTAJE_VELOCIDAD_BASE_GRITO_DE_GUERRA = 20;
    private static final int PORCENTAJE_VELOCIDAD_POR_NIVEL_GRITO_DE_GUERRA = 4;
    private static final int DURACION_GRITO_DE_GUERRA_EN_TURNOS = 2;

    // nombre, vida, mana, ataque, defensa, velocidad. Los atributos
    // (Fuerza, Destreza, Inteligencia, Espiritu, Suerte) no se pasan acá:
    // los inicializa la clase base al azar, y después se completan con el
    // reparto de puntos. Es el más lento de las 4 clases: velocidad 15.
    public Guerrero(String nombre){
        super(nombre, 200, 50, 25, 50, 15);
        // Reemplaza las habilidades genéricas de Personaje por las reales
        // del Guerrero (nombres, topes de nivel y cooldowns de arriba).
        configurarHabilidades(NOMBRES_HABILIDADES, NIVELES_MAXIMOS_HABILIDADES, COOLDOWNS_HABILIDADES_EN_TURNOS);
    }

    // ---------- Efectividad de cada atributo para esta clase ----------
    // La Fuerza rinde al máximo en un Guerrero (multiplicador 1.0).
    @Override
    public double multiplicadorFuerza() {
        return 1.0;
    }

    // La Destreza le sirve a medias (por ejemplo, para maniobrar el arma).
    @Override
    public double multiplicadorDestreza() {
        return 0.5;
    }

    // La magia (Inteligencia/Espiritu) casi no le aporta nada.
    @Override
    public double multiplicadorInteligencia() {
        return 0.25;
    }

    @Override
    public double multiplicadorEspiritu() {
        return 0.25;
    }

    // Habilidad especial "vieja" del Guerrero (queda de la primera versión,
    // antes de definir las 4 habilidades reales de abajo). Todavía no se
    // reemplazó porque usarHabilidad() no forma parte de lo que se pidió
    // ahora (ver checklist-tpo.txt): un golpe basado en su Fuerza y nivel.
    @Override
    public void usarHabilidad(Enemigo objetivo){
        int danio = bonoDe(getFuerza(), multiplicadorFuerza()) * getNivel();
        objetivo.recibirDanio(danio);
    }

    // ---------- Cálculo de daño/efecto de cada habilidad, según su nivel ----------
    // Igual que en Sacerdote: esto solo calcula cuánto pegaría o cuánto
    // bonificaría cada habilidad según su nivel actual. Todavía no se
    // aplica de verdad en combate (no hay motor de turnos ni acceso a la
    // Party desde acá), salvo que ya se puede probar el número con
    // getVida()/recibirDanio() de un enemigo puntual, igual que atacar().

    // Multiplicador acumulado de crecimiento por nivel de habilidad (15%
    // compuesto por cada nivel arriba del 1). Se calcula con un bucle en
    // vez de Math.pow() para no depender de nada más que lo ya visto.
    private double crecimientoCompuestoPorNivel(int nivelHabilidad){
        double multiplicador = 1.0;
        for (int i = 1; i < nivelHabilidad; i++) {
            multiplicador *= (1 + CRECIMIENTO_PORCENTUAL_POR_NIVEL);
        }
        return multiplicador;
    }

    // Golpe Fuerte (Habilidad 1): doble del ataque básico en Nivel 1, con
    // ese "doble" creciendo 15% por nivel, más +8 de daño por cada punto
    // de Fuerza actual del personaje (puntos repartidos + ítems, porque
    // ambos ya suman directo a getFuerza()).
    public int calcularDanioGolpeFuerte(){
        int nivelHabilidad = getNivelHabilidad(INDICE_GOLPE_FUERTE);
        double danioBase = MULTIPLICADOR_BASE_GOLPE_FUERTE * calcularDanioAtaqueBasico()
                * crecimientoCompuestoPorNivel(nivelHabilidad);
        int bonoFuerzaExtra = getFuerza() * BONO_FUERZA_GOLPE_FUERTE;
        return (int) danioBase + bonoFuerzaExtra;
    }

    // Golpe Devastador (Habilidad 3): el doble de lo que pega Golpe Fuerte
    // en Nivel 1 (4 veces el ataque básico), mismo crecimiento de 15% por
    // nivel, y +10 de daño por cada punto de Fuerza (en vez de +8).
    public int calcularDanioGolpeDevastador(){
        int nivelHabilidad = getNivelHabilidad(INDICE_GOLPE_DEVASTADOR);
        double danioBase = MULTIPLICADOR_BASE_GOLPE_DEVASTADOR * calcularDanioAtaqueBasico()
                * crecimientoCompuestoPorNivel(nivelHabilidad);
        int bonoFuerzaExtra = getFuerza() * BONO_FUERZA_GOLPE_DEVASTADOR;
        return (int) danioBase + bonoFuerzaExtra;
    }

    // Piel de Hierro (Habilidad 2, pasiva): mismo bono para defensa física
    // y defensa mágica. +5 en Nivel 1, +5 por nivel, hasta +45 en Nivel 9.
    // Todavía no se suma de verdad a la defensa del personaje (ver nota de
    // arriba); son dos getters separados porque conceptualmente son dos
    // estadísticas distintas, aunque hoy compartan la misma fórmula.
    public int getBonoDefensaFisicaPielDeHierro(){
        return calcularBonoPielDeHierro();
    }

    public int getBonoDefensaMagicaPielDeHierro(){
        return calcularBonoPielDeHierro();
    }

    private int calcularBonoPielDeHierro(){
        int nivelHabilidad = getNivelHabilidad(INDICE_PIEL_DE_HIERRO);
        return BONO_DEFENSA_BASE_PIEL_DE_HIERRO + (nivelHabilidad - 1) * BONO_DEFENSA_POR_NIVEL_PIEL_DE_HIERRO;
    }

    // Grito de Guerra (Ulti): % de aumento de velocidad para toda la party
    // viva, durante 2 turnos. +20% en Nivel 1, +4% por nivel, hasta +40%
    // en Nivel 6 (ver la nota de la constante más arriba).
    public int getPorcentajeVelocidadGritoDeGuerra(){
        int nivelHabilidad = getNivelHabilidad(INDICE_GRITO_DE_GUERRA);
        return PORCENTAJE_VELOCIDAD_BASE_GRITO_DE_GUERRA
                + (nivelHabilidad - 1) * PORCENTAJE_VELOCIDAD_POR_NIVEL_GRITO_DE_GUERRA;
    }

    public int getDuracionGritoDeGuerraEnTurnos(){
        return DURACION_GRITO_DE_GUERRA_EN_TURNOS;
    }
}
