package RPG.modelo.habilidades;

import RPG.modelo.Combatiente;
import RPG.modelo.personajes.Personaje;

// Clase abstracta que representa una habilidad (básica o poderosa) de un
// personaje. Antes cada clase (Guerrero, Mago...) tenía el código de sus
// 2 habilidades metido adentro, repetido casi igual 4 veces. Ahora cada
// habilidad es un objeto propio y el personaje solo "tiene" dos.
//
// Esto coincide con el diagrama de secuencia 2 de la entrega ("Uso de una
// Habilidad Poderosa"), donde aparece HabilidadPoderosa como objeto con
// getCosto() y ejecutar(personaje, objetivo).
//
// Polimorfismo: el controlador le dice a cualquier habilidad "ejecutate"
// sin saber si es Tajo, Bola de Fuego o Sanación. Cada subclase decide
// qué hace.
public abstract class Habilidad {

    private String nombre;
    private int costoMana;

    public Habilidad(String nombre, int costoMana) {
        this.nombre = nombre;
        this.costoMana = costoMana;
    }

    // Aplica el efecto y devuelve un texto que describe qué pasó, para que
    // la Vista (Swing o consola) lo muestre. El MODELO no imprime nada por
    // su cuenta (antes había System.out.println acá adentro, y en la
    // pantalla de Swing esos mensajes se perdían).
    // Ojo: el maná NO se descuenta acá, lo hace Personaje.usarHabilidad()
    // antes de llamar a este método (así no se repite en cada subclase).
    public abstract String ejecutar(Personaje usuario, Combatiente objetivo);

    // true si el objetivo es un aliado (curar/revivir) en vez del enemigo.
    // Reemplaza los "if (personaje instanceof Curador)" que había en
    // MotorDeCombate y en PantallaBatalla.
    public boolean esDeApoyo() {
        return false;
    }

    // ¿Se puede usar sobre este objetivo? Por defecto, solo sobre alguien
    // vivo. Revivir lo pisa para permitir apuntar a un aliado caído.
    public boolean puedeApuntarA(Combatiente objetivo) {
        return objetivo != null && objetivo.estaVivo();
    }

    // Fórmula del documento: daño = max(1, ataque - defensa del objetivo).
    // El multiplicador de cada habilidad se aplica DESPUÉS, sobre este valor.
    protected int calcularDanioBase(Personaje usuario, Combatiente objetivo, boolean esMagico) {
        int danio = usuario.getAtaque(esMagico) - objetivo.getDefensa();
        return Math.max(1, danio);
    }

    // Aplica el daño y arma el mensaje. Se mide la vida antes y después
    // porque el daño real puede ser menor al calculado (si el objetivo se
    // estaba defendiendo o es inmortal).
    protected String aplicarDanio(Personaje usuario, Combatiente objetivo, int danio, String detalleExtra) {
        int vidaAntes = objetivo.getVidaActual();
        objetivo.recibirDanio(danio);
        int danioReal = vidaAntes - objetivo.getVidaActual();
        return usuario.getNombre() + " usa " + nombre + " sobre " + objetivo.getNombre()
                + detalleExtra + ": " + danioReal + " de daño (vida restante " + objetivo.getVidaActual() + ").";
    }

    public String getNombre() {
        return nombre;
    }

    public int getCostoMana() {
        return costoMana;
    }
}
