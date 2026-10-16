package RPG.modelo.habilidades;

import RPG.modelo.Combatiente;
import RPG.modelo.personajes.Personaje;

// Habilidad de apoyo que cura una cantidad FIJA de vida a un aliado vivo.
// Con una sola clase cubrimos las 2 habilidades del Curador del documento:
//   Sanación        -> cura 20, cuesta 10 de maná
//   Sanación Mayor  -> cura 45, cuesta 30 de maná
// (Mismo criterio que AtaqueConMultiplicador: cambian los datos, no el código.)
public class Curacion extends Habilidad {

    private int cantidad;

    public Curacion(String nombre, int costoMana, int cantidad) {
        super(nombre, costoMana);
        this.cantidad = cantidad;
    }

    @Override
    public boolean esDeApoyo() {
        return true;
    }

    // Solo sobre aliados vivos (puedeApuntarA heredado de Habilidad): el
    // documento no tiene ninguna habilidad que reviva.
    @Override
    public String ejecutar(Personaje usuario, Combatiente objetivo) {
        int vidaAntes = objetivo.getVidaActual();
        objetivo.recibirCuracion(cantidad);
        int curado = objetivo.getVidaActual() - vidaAntes;
        return usuario.getNombre() + " usa " + getNombre() + " sobre " + objetivo.getNombre()
                + ": recupera " + curado + " de vida (" + objetivo.getVidaActual() + "/" + objetivo.getVidaMaxima() + ").";
    }
}
