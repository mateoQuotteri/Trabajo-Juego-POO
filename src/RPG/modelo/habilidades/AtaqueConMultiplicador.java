package RPG.modelo.habilidades;

import RPG.modelo.Combatiente;
import RPG.modelo.personajes.Personaje;

// Habilidad de daño "estándar": calcula el daño base (físico o mágico) y lo
// multiplica. Con UNA sola clase cubrimos 5 habilidades del documento que
// antes estaban copiadas en cada personaje:
//   Tajo (físico x1.5), Golpe Poderoso (físico x2.5),
//   Chispa (mágico x1.5), Bola de Fuego (mágico x2.5),
//   Lluvia de Flechas (físico x2.5).
// Lo único que cambia entre ellas son los datos que se pasan al constructor.
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
        return aplicarDanio(usuario, objetivo, danio, "");
    }
}
