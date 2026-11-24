package RPG.dto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class EstadoInventarioDTO {

    private final int oro;
    private final List<PersonajeDTO> personajes;
    private final List<ItemDTO> items;

    public EstadoInventarioDTO(int oro, List<PersonajeDTO> personajes, List<ItemDTO> items) {
        this.oro = oro;
        this.personajes = Collections.unmodifiableList(new ArrayList<PersonajeDTO>(personajes));
        this.items = Collections.unmodifiableList(new ArrayList<ItemDTO>(items));
    }

    public int getOro() {
        return oro;
    }

    public List<PersonajeDTO> getPersonajes() {
        return personajes;
    }

    public List<ItemDTO> getItems() {
        return items;
    }
}
