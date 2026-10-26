package RPG.modelo.items;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Inventario {
    private static final int CAPACIDAD_MAXIMA = 6;
    private final List<Item> items;

    public Inventario() {
        this.items = new ArrayList<>();
    }

    public boolean agregarItem(Item item) {
        if (item == null || items.size() >= CAPACIDAD_MAXIMA) {
            return false;
        }
        items.add(item);
        return true;
    }

    public Item removerItem(int indice) {
        if (indice >= 0 && indice < items.size()) {
            return items.remove(indice);
        }
        return null;
    }

    public Item obtenerItem(int indice) {
        if (indice >= 0 && indice < items.size()) {
            return items.get(indice);
        }
        return null;
    }

    public Item removerItem(Item item) {
        int indice = items.indexOf(item);
        return indice < 0 ? null : items.remove(indice);
    }

    public int getCapacidadMaxima() {
        return CAPACIDAD_MAXIMA;
    }

    public List<Item> getItems() {
        return Collections.unmodifiableList(items);
    }
}