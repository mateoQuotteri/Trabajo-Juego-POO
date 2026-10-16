package RPG.modelo.habilidades;

import RPG.modelo.Combatiente;
import RPG.modelo.personajes.Personaje;

// Habilidad básica del Curador: cura 20 de vida a un aliado VIVO.
// Antes también "revivía" a un caído (le sumaba 20 a alguien en 0), lo que
// la hacía hacer lo mismo que Revivir pero más barata. Ahora solo se puede
// usar sobre aliados vivos (puedeApuntarA heredado de Habilidad).
public class Sanacion extends Habilidad {

    private static final int CURACION = 20;

    public Sanacion() {
        super("Sanación", 10);
    }

    @Override
    public boolean esDeApoyo() {
        return true;
    }

    @Override
    public String ejecutar(Personaje usuario, Combatiente objetivo) {
        int vidaAntes = objetivo.getVidaActual();
        objetivo.recibirCuracion(CURACION);
        int curado = objetivo.getVidaActual() - vidaAntes;
        if (curado == 0 && objetivo instanceof Personaje && ((Personaje) objetivo).estaImpedidoDeCurarse()) {
            return usuario.getNombre() + " usa Sanación sobre " + objetivo.getNombre()
                    + ", pero no tiene efecto: sigue bajo Toque de Muerte.";
        }
        return usuario.getNombre() + " usa Sanación sobre " + objetivo.getNombre()
                + ": recupera " + curado + " de vida (" + objetivo.getVidaActual() + "/" + objetivo.getVidaMaxima() + ").";
    }
}
