package RPG.modelo.habilidades;

import RPG.modelo.Combatiente;
import RPG.modelo.personajes.Personaje;

// Clase abstracta que representa una habilidad (básica o poderosa) de un
// personaje. Cada personaje "tiene" dos. Coincide con el diagrama de
// secuencia 2 de la entrega ("Uso de una Habilidad Poderosa"), donde
// aparece como objeto con getCosto() y ejecutar(personaje, objetivo).
//
// Polimorfismo: el controlador le dice a cualquier habilidad "ejecutate"
// sin saber si es Tajo, Bola de Fuego o Sanación. Cada subclase decide qué
// hace:
//   AtaqueConMultiplicador -> las 6 habilidades de daño del documento
//   Curacion               -> Sanación y Sanación Mayor
public abstract class Habilidad {

    private String nombre;
    private int costoMana;

    public Habilidad(String nombre, int costoMana) {
        this.nombre = nombre;
        this.costoMana = costoMana;
    }

    // Aplica el efecto y devuelve un texto que describe qué pasó, para que
    // la Vista lo muestre (el MODELO no imprime nada por su cuenta).
    // El maná NO se descuenta acá: lo hace Personaje.usarHabilidad().
    public abstract String ejecutar(Personaje usuario, Combatiente objetivo);

    // true si el objetivo es un aliado (curar) en vez del enemigo.
    public boolean esDeApoyo() {
        return false;
    }

    // ¿Se puede usar sobre este objetivo? Solo sobre alguien vivo.
    public boolean puedeApuntarA(Combatiente objetivo) {
        return objetivo != null && objetivo.estaVivo();
    }

    // Fórmula del documento: daño = max(1, ataque - defensa del objetivo).
    // El multiplicador de cada habilidad se aplica DESPUÉS, sobre este valor.
    protected int calcularDanioBase(Personaje usuario, Combatiente objetivo, boolean esMagico) {
        int danio = usuario.getAtaque(esMagico) - objetivo.getDefensa();
        return Math.max(1, danio);
    }

    public String getNombre() {
        return nombre;
    }

    public int getCostoMana() {
        return costoMana;
    }
}
