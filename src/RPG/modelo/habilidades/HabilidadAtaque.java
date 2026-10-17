package RPG.modelo.habilidades;

import RPG.modelo.Combatiente;

// Habilidad de daño. Fórmula del documento:
//   daño = max(1, ataque del usuario - defensa del objetivo) x multiplicador
// Sirve para Tajo, Golpe Poderoso, Chispa, Bola de Fuego, Disparo Certero,
// Lluvia de Flechas y el ataque de los enemigos (multiplicador 1).
public class HabilidadAtaque extends Habilidad {

    private double multiplicador;
    private boolean esMagico;

    public HabilidadAtaque(String nombre, int costoMana, double multiplicador, boolean esMagico) {
        super(nombre, costoMana);
        this.multiplicador = multiplicador;
        this.esMagico = esMagico;
    }

    @Override
    public String ejecutar(Combatiente usuario, Combatiente objetivo) {
        int danioBase = usuario.getAtaque(esMagico) - objetivo.getDefensa();
        if (danioBase < 1) {
            danioBase = 1;
        }
        int danio = (int) (danioBase * multiplicador);
        objetivo.recibirDanio(danio);
        return usuario.getNombre() + " usa " + getNombre() + " sobre " + objetivo.getNombre()
                + " (vida restante " + objetivo.getVidaActual() + ")";
    }
}
