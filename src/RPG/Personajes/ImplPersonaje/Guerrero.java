package RPG.Personajes.ImplPersonaje;

import RPG.Personajes.Combatiente;
import RPG.Personajes.Personaje;

// Tanque: vida y defensa altas, daño puramente físico (ataqueMagico = 0),
// poco maná. Es el único que puede Defender.
public class Guerrero extends Personaje {

    private static final String NOMBRE_HABILIDAD_BASICA = "Tajo";
    private static final int COSTO_HABILIDAD_BASICA = 10;
    private static final double MULTIPLICADOR_HABILIDAD_BASICA = 1.5;

    private static final String NOMBRE_HABILIDAD_PODEROSA = "Golpe Poderoso";
    private static final int COSTO_HABILIDAD_PODEROSA = 20;
    private static final double MULTIPLICADOR_HABILIDAD_PODEROSA = 2.5;

    // nombre, vidaMaxima, manaMaximo, ataqueFisico, ataqueMagico, defensa,
    // velocidad. Valores de Nivel 1 según la tabla "Las 4 clases" del
    // documento de diseño.
    public Guerrero(String nombre) {
        super(nombre, 50, 20, 40, 0, 40, 20);
    }

    // Tajo: daño físico x1.5 (documento, tabla de habilidades).
    @Override
    public void usarHabilidadBasica(Combatiente objetivo) {
        if (!puedeUsarHabilidad(COSTO_HABILIDAD_BASICA)) {
            return;
        }
        gastarMana(COSTO_HABILIDAD_BASICA);
        int danio = (int) (calcularDanioFisico(objetivo) * MULTIPLICADOR_HABILIDAD_BASICA);
        objetivo.recibirDanio(danio);
    }

    // Golpe Poderoso: daño físico x2.5.
    @Override
    public void usarHabilidadPoderosa(Combatiente objetivo) {
        if (!puedeUsarHabilidad(COSTO_HABILIDAD_PODEROSA)) {
            return;
        }
        gastarMana(COSTO_HABILIDAD_PODEROSA);
        int danio = (int) (calcularDanioFisico(objetivo) * MULTIPLICADOR_HABILIDAD_PODEROSA);
        objetivo.recibirDanio(danio);
    }

    // Defender: reduce a la mitad el próximo daño que reciba el Guerrero
    // (documento: "El Guerrero además puede Defender"). No gasta maná ni
    // usa el objetivo (se defiende a sí mismo), por eso no encaja en
    // usarHabilidadBasica/Poderosa y es un método propio del Guerrero.
    public void defender() {
        activarDefensa();
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
