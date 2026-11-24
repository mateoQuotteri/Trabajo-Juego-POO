package RPG.servicios;

import RPG.modelo.Batalla;
import RPG.modelo.Combatiente;
import RPG.modelo.personajes.Personaje;

public class GestorDeTurnos {

    public String terminarTurno(Batalla batalla) {
        Combatiente actual = batalla.getCombatienteActual();
        String log = procesarFinDeTurno(actual);
        batalla.pasarAlSiguienteTurno();
        return log + resolverTurnosAutomaticos(batalla);
    }

    public String resolverTurnosAutomaticos(Batalla batalla) {
        String log = "";
        while (!batalla.estaTerminada()) {
            Combatiente actual = batalla.getCombatienteActual();
            if (actual.estaAturdido()) {
                log = log + actual.getNombre() + " pierde su turno por estar aturdido.\n";
                actual.quitarAturdimiento();
                log = log + procesarFinDeTurno(actual);
                batalla.pasarAlSiguienteTurno();
                continue;
            }

            if (actual == batalla.getEnemigo()) {
                Personaje objetivo = batalla.getParty().elegirVivoAlAzar();
                log = log + batalla.getEnemigo().actuar(objetivo) + "\n";
                log = log + procesarFinDeTurno(actual);
                batalla.pasarAlSiguienteTurno();
                continue;
            }

            ((Personaje) actual).iniciarTurno();
            return log;
        }
        return log;
    }

    private String procesarFinDeTurno(Combatiente combatiente) {
        String log = combatiente.procesarVenenoAlFinDeTurno();
        combatiente.procesarEscudoAlFinDeTurno();
        return log;
    }
}
