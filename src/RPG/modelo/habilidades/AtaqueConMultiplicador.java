package RPG.modelo.habilidades;

import RPG.modelo.Combatiente;
import RPG.modelo.personajes.Personaje;

// Habilidad de daño: calcula el daño base (físico o mágico) y lo multiplica.
// Con UNA sola clase cubrimos las 6 habilidades de daño del documento:
//   Tajo (físico x1.5)            Golpe Poderoso (físico x2.5)
//   Chispa (mágico x1.5)          Bola de Fuego (mágico x2.5)
//   Disparo Certero (físico x1.5) Lluvia de Flechas (físico x2.5)
// Lo único que cambia entre ellas son los datos del constructor.
public class AtaqueConMultiplicador extends Habilidad {

    private double multiplicador;
    private boolean esMagico;

    public AtaqueConMultiplicador(String nombre, int costoMana, double multiplicador, boolean esMagico) {
        super(nombre, costoMana);
        this.multiplicador = multiplicador;
        this.esMagico = esMagico;
    }

    @Override
    public String ejecutar(Personaje usuario, Combatiente objetivo) {
        int danio = (int) (calcularDanioBase(usuario, objetivo, esMagico) * multiplicador);
        int vidaAntes = objetivo.getVidaActual();
        objetivo.recibirDanio(danio);
        return usuario.getNombre() + " usa " + getNombre() + " sobre " + objetivo.getNombre() + ": "
                + (vidaAntes - objetivo.getVidaActual()) + " de daño (vida restante " + objetivo.getVidaActual() + ").";
    }
}
