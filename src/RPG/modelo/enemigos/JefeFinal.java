package RPG.modelo.enemigos;

import RPG.modelo.personajes.Personaje;

// Jefe final del Piso 10: "Guardián Ancestral". Documento: 40% ataca /
// 30% aturde / 30% envenena. Derrotarlo = ganar el juego.
// Stats del documento: Vida 120, Ataque 45, Defensa 30, Velocidad 35.
// No se escala por piso: la tabla ya lo define como "Jefe Final (Piso 10)".
public class JefeFinal extends Enemigo {

    private static final String[] ACCIONES = {
            "atacar", "atacar", "atacar", "atacar",    // 40%
            "aturdir", "aturdir", "aturdir",           // 30%
            "envenenar", "envenenar", "envenenar"      // 30%
    };

    // vida, ataque, defensa, velocidad, experiencia que otorga
    public JefeFinal() {
        super("Guardián Ancestral", 120, 45, 30, 35, 500);
        setNivel(10);
    }

    @Override
    public String actuar(Personaje objetivo) {
        return ejecutarAccionSobre(elegirAccionPonderada(ACCIONES), objetivo);
    }

    @Override
    public boolean esJefe() {
        return true;
    }
}
