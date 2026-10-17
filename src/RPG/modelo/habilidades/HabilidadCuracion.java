package RPG.modelo.habilidades;

import RPG.modelo.Combatiente;

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
        objetivo.recibirCuracion(cantidad);
        return usuario.getNombre() + " usa " + getNombre() + " sobre " + objetivo.getNombre()
                + " (vida " + objetivo.getVidaActual() + "/" + objetivo.getVidaMaxima() + ")";
    }
}
