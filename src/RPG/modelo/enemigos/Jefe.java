package RPG.modelo.enemigos;

import RPG.modelo.personajes.Personaje;

// Jefe del Piso 5: "Rey Slime". Documento: 50% ataca / 30% aturde / 20% envenena.
// Stats del documento: Vida 70, Ataque 30, Defensa 20, Velocidad 25.
// No se escala por piso: la tabla ya lo define como "Jefe (Piso 5)".
public class Jefe extends Enemigo {

    private static final String[] ACCIONES = {
            "atacar", "atacar", "atacar", "atacar", "atacar", // 50%
            "aturdir", "aturdir", "aturdir",                  // 30%
            "envenenar", "envenenar"                          // 20%
    };

    // vida, ataque, defensa, velocidad, experiencia que otorga
    public Jefe() {
        super("Rey Slime", 70, 30, 20, 25, 250);
        setNivel(5);
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
