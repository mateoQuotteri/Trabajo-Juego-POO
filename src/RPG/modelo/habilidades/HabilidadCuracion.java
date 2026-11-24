package RPG.modelo.habilidades;

import RPG.modelo.Combatiente;
import RPG.modelo.personajes.Personaje;

// Cura una cantidad fija. Sanación (20) y Sanación Mayor (45) del Curador.
public class HabilidadCuracion extends Habilidad {

    private int cantidad;

    public HabilidadCuracion(String nombre, int costoMana, int cantidad) {
        super(nombre, costoMana);
        this.cantidad = cantidad;
    }

    @Override
    public boolean esDeApoyo() {
        return true;
    }

    @Override
    public String ejecutar(Combatiente usuario, Combatiente objetivo) {
        if (usuario == null || !(objetivo instanceof Personaje)) {
            throw new IllegalArgumentException("Una habilidad de curación requiere un aliado como objetivo.");
        }
        objetivo.recibirCuracion(cantidad);
        return usuario.getNombre() + " usa " + getNombre() + " sobre " + objetivo.getNombre()
                + " (vida " + objetivo.getVidaActual() + "/" + objetivo.getVidaMaxima() + ")";
    }
}
