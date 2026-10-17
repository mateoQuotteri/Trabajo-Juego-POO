package RPG.modelo;

import RPG.modelo.enemigos.Enemigo;
import RPG.modelo.personajes.Personaje;

// Una batalla: la party contra UN enemigo (recomendación del profe).
// Lleva el orden de turnos de la ronda y a quién le toca.
public class Batalla {

    private Party party;
    private Enemigo enemigo;
    private int piso;

    private Combatiente[] ordenDeTurnos; // personajes vivos + el enemigo
    private int turnoActual;             // posición en ordenDeTurnos
    private int numeroRonda;

    public Batalla(Party party, Enemigo enemigo, int piso) {
        this.party = party;
        this.enemigo = enemigo;
        this.piso = piso;
        this.numeroRonda = 0;
        party.prepararParaBatalla();
        empezarRonda();
    }

    // Al empezar cada ronda se arma el orden de nuevo, porque puede haber
    // muerto alguien.
    private void empezarRonda() {
        numeroRonda++;
        Personaje[] vivos = party.getMiembrosVivos();
        ordenDeTurnos = new Combatiente[vivos.length + 1];
        for (int i = 0; i < vivos.length; i++) {
            ordenDeTurnos[i] = vivos[i];
        }
        ordenDeTurnos[vivos.length] = enemigo;
        ordenarPorVelocidad();
        turnoActual = 0;
    }

    // Selection sort de mayor a menor velocidad.
    // Si empatan, se decide al azar (documento).
    private void ordenarPorVelocidad() {
        for (int i = 0; i < ordenDeTurnos.length - 1; i++) {
            int mayor = i;
            for (int j = i + 1; j < ordenDeTurnos.length; j++) {
                int velJ = ordenDeTurnos[j].getVelocidad();
                int velMayor = ordenDeTurnos[mayor].getVelocidad();
                if (velJ > velMayor || (velJ == velMayor && Math.random() < 0.5)) {
                    mayor = j;
                }
            }
            Combatiente aux = ordenDeTurnos[i];
            ordenDeTurnos[i] = ordenDeTurnos[mayor];
            ordenDeTurnos[mayor] = aux;
        }
    }

    // A quién le toca. Saltea a los que murieron en esta ronda y, si la
    // ronda terminó, arranca otra.
    public Combatiente getCombatienteActual() {
        while (true) {
            if (turnoActual >= ordenDeTurnos.length) {
                empezarRonda();
            }
            if (ordenDeTurnos[turnoActual].estaVivo()) {
                return ordenDeTurnos[turnoActual];
            }
            turnoActual++;
        }
    }

    public boolean esTurnoDelEnemigo() {
        return getCombatienteActual() == enemigo;
    }

    // Devuelve el personaje que tiene el turno, o null si le toca al enemigo.
    public Personaje getPersonajeEnTurno() {
        Combatiente actual = getCombatienteActual();
        Personaje[] miembros = party.getMiembros();
        for (int i = 0; i < miembros.length; i++) {
            if (miembros[i] == actual) {
                return miembros[i];
            }
        }
        return null;
    }

    public void pasarAlSiguienteTurno() {
        turnoActual++;
    }

    public boolean estaTerminada() {
        return !enemigo.estaVivo() || party.estaDerrotada();
    }

    public boolean ganoLaParty() {
        return !enemigo.estaVivo();
    }

    public String getOrdenComoTexto() {
        String texto = "";
        for (int i = 0; i < ordenDeTurnos.length; i++) {
            if (i > 0) {
                texto = texto + " -> ";
            }
            texto = texto + ordenDeTurnos[i].getNombre();
        }
        return texto;
    }

    public Party getParty() {
        return party;
    }

    public Enemigo getEnemigo() {
        return enemigo;
    }

    public int getPiso() {
        return piso;
    }

    public int getNumeroRonda() {
        return numeroRonda;
    }
}
