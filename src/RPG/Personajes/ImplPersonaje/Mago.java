package RPG.Personajes.ImplPersonaje;

import RPG.Personajes.Combatiente;
import RPG.Personajes.Personaje;

// Vida y defensa bajísimas, pero el mayor ataque mágico y mucho maná.
public class Mago extends Personaje {

    private static final String NOMBRE_HABILIDAD_BASICA = "Chispa";
    private static final int COSTO_HABILIDAD_BASICA = 10;
    private static final double MULTIPLICADOR_HABILIDAD_BASICA = 1.5;

    private static final String NOMBRE_HABILIDAD_PODEROSA = "Bola de Fuego";
    private static final int COSTO_HABILIDAD_PODEROSA = 30;
    private static final double MULTIPLICADOR_HABILIDAD_PODEROSA = 2.5;

    // nombre, vidaMaxima, manaMaximo, ataqueFisico, ataqueMagico, defensa,
    // velocidad. Valores de Nivel 1 según la tabla del documento.
    public Mago(String nombre) {
        super(nombre, 25, 45, 10, 40, 15, 25);
    }

    // Chispa: daño mágico x1.5.
    @Override
    public void usarHabilidadBasica(Combatiente objetivo) {
        if (!puedeUsarHabilidad(COSTO_HABILIDAD_BASICA)) {
            return;
        }
        gastarMana(COSTO_HABILIDAD_BASICA);
        int danio = (int) (calcularDanioMagico(objetivo) * MULTIPLICADOR_HABILIDAD_BASICA);
        objetivo.recibirDanio(danio);
    }

    // Bola de Fuego: daño mágico x2.5.
    @Override
    public void usarHabilidadPoderosa(Combatiente objetivo) {
        if (!puedeUsarHabilidad(COSTO_HABILIDAD_PODEROSA)) {
            return;
        }
        gastarMana(COSTO_HABILIDAD_PODEROSA);
        int danio = (int) (calcularDanioMagico(objetivo) * MULTIPLICADOR_HABILIDAD_PODEROSA);
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
