package RPG.modelo.enemigos;

import RPG.modelo.personajes.Personaje;

// Enemigo común. 50% ataca / 30% aturde / 20% Golpe Debilitante.
// Golpe Debilitante (pedido del grupo): x1.3 de daño y -8 de Defensa al
// objetivo durante 2 turnos.
// OJO: el documento entregado dice "70% ataca / 30% aturde" (sin especial).
public class Goblin extends Enemigo {

    private static final String[] ACCIONES = {
            "atacar", "atacar", "atacar", "atacar", "atacar", // 50%
            "aturdir", "aturdir", "aturdir",                  // 30%
            "especial", "especial"                            // 20%
    };

    private static final double MULTIPLICADOR_GOLPE = 1.3;
    private static final int REDUCCION_DEFENSA = 8;
    private static final int DURACION = 2;

    // vida, ataque, defensa, velocidad, nivel, experiencia que otorga
    public Goblin(String nombre) {
        super(nombre, 35, 20, 12, 28, 1, 50);
    }

    @Override
    public String actuar(Personaje objetivo) {
        String accion = elegirAccionPonderada(ACCIONES);
        if (accion.equals("especial")) {
            return usarGolpeDebilitante(objetivo);
        }
        return ejecutarAccionSobre(accion, objetivo);
    }

    private String usarGolpeDebilitante(Personaje objetivo) {
        int vidaAntes = objetivo.getVidaActual();
        objetivo.recibirDanio(calcularDanioConMultiplicador(objetivo, MULTIPLICADOR_GOLPE));
        objetivo.reducirDefensa(REDUCCION_DEFENSA, DURACION);
        return getNombre() + " usa Golpe Debilitante sobre " + objetivo.getNombre() + ": "
                + (vidaAntes - objetivo.getVidaActual()) + " de daño y -" + REDUCCION_DEFENSA
                + " de Defensa por " + DURACION + " turnos.";
    }
}
