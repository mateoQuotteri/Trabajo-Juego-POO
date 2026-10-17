package RPG.modelo.habilidades;

import RPG.modelo.Combatiente;

// El objetivo pierde su próximo turno. La usan el Goblin y los jefes.
public class HabilidadAturdir extends Habilidad {

    public HabilidadAturdir() {
        super("Aturdir", 0);
    }

    @Override
    public String ejecutar(Combatiente usuario, Combatiente objetivo) {
        objetivo.aturdir();
        return usuario.getNombre() + " aturde a " + objetivo.getNombre();
    }
}
