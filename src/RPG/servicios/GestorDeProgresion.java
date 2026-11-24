package RPG.servicios;

import RPG.modelo.Party;
import RPG.modelo.personajes.Personaje;

public class GestorDeProgresion {

    public Resultado distribuirExperiencia(Party party, int experienciaGanada) {
        String log = "";
        String resumen = "";
        Personaje[] miembrosVivos = party.getMiembrosVivos();
        for (Personaje personaje : miembrosVivos) {
            int nivelAnterior = personaje.getNivel();
            personaje.ganarExperiencia(experienciaGanada);
            String lineaExperiencia = personaje.getNombre() + " obtiene "
                    + experienciaGanada + " puntos de experiencia.";
            log = log + lineaExperiencia + "\n";
            resumen = resumen + lineaExperiencia + "\n";
            if (personaje.getNivel() > nivelAnterior) {
                String lineaNivel = personaje.getNombre() + " sube al nivel " + personaje.getNivel() + ".";
                log = log + lineaNivel + "\n";
                resumen = resumen + lineaNivel + "\n";
            }
        }
        return new Resultado(log, resumen);
    }

    public static final class Resultado {
        private final String log;
        private final String resumen;

        private Resultado(String log, String resumen) {
            this.log = log;
            this.resumen = resumen;
        }

        public String getLog() {
            return log;
        }

        public String getResumen() {
            return resumen;
        }
    }
}
