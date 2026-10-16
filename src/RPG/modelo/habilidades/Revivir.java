package RPG.modelo.habilidades;

import RPG.modelo.Combatiente;
import RPG.modelo.personajes.Personaje;

// Habilidad poderosa del Curador (definición final del grupo, 25/09):
// - Sobre OTRO aliado (vivo o caído): le devuelve el 25% de SU vida máxima.
// - Sobre SÍ MISMA: no cura, la vuelve inmortal durante 2 turnos.
// OJO: el documento entregado (Fase A) todavía dice "Sanación Mayor (cura
// 45 de vida)". Hay que actualizar el documento/diagramas para que
// coincidan con el código (requisito de la cátedra).
public class Revivir extends Habilidad {

    private static final double PORCENTAJE_VIDA = 0.25;
    private static final int TURNOS_INMORTALIDAD = 2;

    public Revivir() {
        super("Revivir", 30);
    }

    @Override
    public boolean esDeApoyo() {
        return true;
    }

    // A diferencia del resto, se puede apuntar a un aliado caído (esa es
    // justamente la idea de "revivir"). Antes el motor de consola solo
    // ofrecía aliados vivos como objetivo, así que nunca se podía revivir
    // a nadie en una batalla real: quedó corregido.
    @Override
    public boolean puedeApuntarA(Combatiente objetivo) {
        return objetivo != null;
    }

    @Override
    public String ejecutar(Personaje usuario, Combatiente objetivo) {
        if (objetivo == usuario) {
            usuario.volverseInmortal(TURNOS_INMORTALIDAD);
            return usuario.getNombre() + " usa Revivir sobre sí misma: es inmortal durante "
                    + TURNOS_INMORTALIDAD + " turnos.";
        }
        boolean estabaCaido = !objetivo.estaVivo();
        int vidaAntes = objetivo.getVidaActual();
        objetivo.recibirCuracion((int) (objetivo.getVidaMaxima() * PORCENTAJE_VIDA));
        int curado = objetivo.getVidaActual() - vidaAntes;
        if (curado == 0) {
            return usuario.getNombre() + " usa Revivir sobre " + objetivo.getNombre() + ", pero no tiene efecto.";
        }
        String accion = estabaCaido ? " vuelve a la pelea con " : " recupera vida y queda en ";
        return usuario.getNombre() + " usa Revivir: " + objetivo.getNombre() + accion
                + objetivo.getVidaActual() + "/" + objetivo.getVidaMaxima() + " de vida.";
    }
}
