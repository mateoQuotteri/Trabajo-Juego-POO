package RPG.modelo.enemigos;

import RPG.modelo.personajes.Personaje;

// Enemigo común (pisos 1-4 y 6-9). 70% ataca / 30% Escupitajo.
// Escupitajo (pedido del grupo): x1.3 de daño y -10 de Velocidad al
// objetivo durante 2 turnos.
// OJO: el documento entregado dice "Slime: solo ataca, nunca aturde".
// El código refleja la decisión posterior del grupo; hay que actualizar
// el documento.
public class Slime extends Enemigo {

    private static final String[] ACCIONES = {
            "atacar", "atacar", "atacar", "atacar", "atacar", "atacar", "atacar", // 70%
            "especial", "especial", "especial"                                     // 30%
    };

    private static final double MULTIPLICADOR_ESCUPITAJO = 1.3;
    private static final int REDUCCION_VELOCIDAD = 10;
    private static final int DURACION = 2;

    // vida, ataque, defensa, velocidad, nivel, experiencia que otorga
    public Slime(String nombre) {
        super(nombre, 25, 12, 5, 12, 1, 30);
    }

    @Override
    public String actuar(Personaje objetivo) {
        String accion = elegirAccionPonderada(ACCIONES);
        if (accion.equals("especial")) {
            return usarEscupitajo(objetivo);
        }
        return ejecutarAccionSobre(accion, objetivo);
    }

    private String usarEscupitajo(Personaje objetivo) {
        int vidaAntes = objetivo.getVidaActual();
        objetivo.recibirDanio(calcularDanioConMultiplicador(objetivo, MULTIPLICADOR_ESCUPITAJO));
        objetivo.reducirVelocidad(REDUCCION_VELOCIDAD, DURACION);
        return getNombre() + " escupe a " + objetivo.getNombre() + ": " + (vidaAntes - objetivo.getVidaActual())
                + " de daño y -" + REDUCCION_VELOCIDAD + " de Velocidad por " + DURACION + " turnos.";
    }
}
