package RPG.modelo.habilidades;

import RPG.modelo.Combatiente;

// Clase abstracta para las habilidades. La usan los personajes (Tajo, Chispa,
// Sanación...) y también los enemigos (atacar, aturdir, envenenar).
// usuario y objetivo son Combatiente, así sirve para los dos bandos
// (pedido de la devolución).
public abstract class Habilidad {

    private String nombre;
    private int costoMana;

    public Habilidad(String nombre, int costoMana) {
        this.nombre = nombre;
        this.costoMana = costoMana;
    }

    // Cada subclase hace su efecto y devuelve un texto para el log.
    public abstract String ejecutar(Combatiente usuario, Combatiente objetivo);

    // true si se usa sobre un aliado (curar). Por defecto, no.
    public boolean esDeApoyo() {
        return false;
    }

    public String getNombre() {
        return nombre;
    }

    public int getCostoMana() {
        return costoMana;
    }
}
