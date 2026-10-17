package RPG.modelo.habilidades;

import RPG.modelo.Combatiente;

// El objetivo queda envenenado 3 turnos. La usan los jefes.
public class HabilidadEnvenenar extends Habilidad {

    public HabilidadEnvenenar() {
        super("Envenenar", 0);
    }

    @Override
    public String ejecutar(Combatiente usuario, Combatiente objetivo) {
        objetivo.envenenar();
        return usuario.getNombre() + " envenena a " + objetivo.getNombre();
    }
}
