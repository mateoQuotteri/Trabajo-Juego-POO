package RPG.modelo.items;

import RPG.modelo.Combatiente;

public abstract class Item {
    private String nombre;
    private String descripcion;

    public Item(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public abstract void usar(Combatiente objetivo);
}