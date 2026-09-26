package RPG.Personajes.ImplPersonaje;

import RPG.Personajes.Combatiente;
import RPG.Personajes.Personaje;

// Velocidad muy por encima del resto (actúa primero casi siempre), daño
// físico. Reemplaza a la vieja clase "Cazador" del sistema descartado.
public class Arquero extends Personaje {

    private static final String NOMBRE_HABILIDAD_BASICA = "Disparo Certero";
    private static final int COSTO_HABILIDAD_BASICA = 10;
    private static final double PROBABILIDAD_CRITICO = 0.40; // 40% de las veces pega el doble

    private static final String NOMBRE_HABILIDAD_PODEROSA = "Lluvia de Flechas";
    private static final int COSTO_HABILIDAD_PODEROSA = 20;
    private static final double MULTIPLICADOR_HABILIDAD_PODEROSA = 2.5;

    // nombre, vidaMaxima, manaMaximo, ataqueFisico, ataqueMagico, defensa,
    // velocidad. Valores de Nivel 1 según la tabla del documento.
    public Arquero(String nombre) {
        super(nombre, 30, 20, 35, 5, 20, 45);
    }

    // Disparo Certero: el documento la describe distinto al resto de
    // habilidades básicas (no es un multiplicador fijo como Tajo o Chispa,
    // sino "40% crítico, daño doble"). Se implementa como un tiro de daño
    // físico normal que, un 40% de las veces, sale crítico y pega el doble
    // (Math.random() < 0.40, mismo mecanismo de azar que usa el documento
    // para la IA de los enemigos en la sección "Cómo deciden los enemigos
    // qué hacer").
    @Override
    public void usarHabilidadBasica(Combatiente objetivo) {
        if (!puedeUsarHabilidad(COSTO_HABILIDAD_BASICA)) {
            return;
        }
        gastarMana(COSTO_HABILIDAD_BASICA);
        int danio = calcularDanioFisico(objetivo);
        if (Math.random() < PROBABILIDAD_CRITICO) {
            danio = danio * 2;
        }
        objetivo.recibirDanio(danio);
    }

    // Lluvia de Flechas: daño físico x2.5.
    @Override
    public void usarHabilidadPoderosa(Combatiente objetivo) {
        if (!puedeUsarHabilidad(COSTO_HABILIDAD_PODEROSA)) {
            return;
        }
        gastarMana(COSTO_HABILIDAD_PODEROSA);
        int danio = (int) (calcularDanioFisico(objetivo) * MULTIPLICADOR_HABILIDAD_PODEROSA);
        objetivo.recibirDanio(danio);
    }

    @Override
    public String getNombreHabilidadBasica() {
        return NOMBRE_HABILIDAD_BASICA;
    }

    @Override
    public int getCostoHabilidadBasica() {
        return COSTO_HABILIDAD_BASICA;
    }

    @Override
    public String getNombreHabilidadPoderosa() {
        return NOMBRE_HABILIDAD_PODEROSA;
    }

    @Override
    public int getCostoHabilidadPoderosa() {
        return COSTO_HABILIDAD_PODEROSA;
    }
}
