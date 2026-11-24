package RPG.dto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class EstadoBatallaDTO {

    private final int piso;
    private final int numeroRonda;
    private final String ordenDeTurnos;
    private final CombatienteDTO enemigo;
    private final List<PersonajeDTO> personajes;
    private final List<ItemDTO> items;
    private final int indicePersonajeEnTurno;
    private final boolean terminada;

    public EstadoBatallaDTO(int piso, int numeroRonda, String ordenDeTurnos,
                            CombatienteDTO enemigo, List<PersonajeDTO> personajes,
                            List<ItemDTO> items, int indicePersonajeEnTurno, boolean terminada) {
        this.piso = piso;
        this.numeroRonda = numeroRonda;
        this.ordenDeTurnos = ordenDeTurnos;
        this.enemigo = enemigo;
        this.personajes = Collections.unmodifiableList(new ArrayList<PersonajeDTO>(personajes));
        this.items = Collections.unmodifiableList(new ArrayList<ItemDTO>(items));
        this.indicePersonajeEnTurno = indicePersonajeEnTurno;
        this.terminada = terminada;
    }

    public int getPiso() {
        return piso;
    }

    public int getNumeroRonda() {
        return numeroRonda;
    }

    public String getOrdenDeTurnos() {
        return ordenDeTurnos;
    }

    public CombatienteDTO getEnemigo() {
        return enemigo;
    }

    public List<PersonajeDTO> getPersonajes() {
        return personajes;
    }

    public List<ItemDTO> getItems() {
        return items;
    }

    public int getIndicePersonajeEnTurno() {
        return indicePersonajeEnTurno;
    }

    public boolean estaTerminada() {
        return terminada;
    }
}
