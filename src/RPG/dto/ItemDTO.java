package RPG.dto;

public final class ItemDTO {

    private final int indice;
    private final String nombre;
    private final String descripcion;
    private final boolean equipable;

    public ItemDTO(int indice, String nombre, String descripcion, boolean equipable) {
        this.indice = indice;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.equipable = equipable;
    }

    public int getIndice() {
        return indice;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public boolean esEquipable() {
        return equipable;
    }
}
