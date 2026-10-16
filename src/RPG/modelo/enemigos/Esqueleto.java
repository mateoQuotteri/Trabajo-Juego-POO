package RPG.modelo.enemigos;

import RPG.modelo.personajes.Personaje;

// Enemigo de PRUEBA (no está en la tabla oficial del documento). Se movió
// acá desde RPG.Personajes para que esté junto a los demás enemigos.
// No aparece en la Torre (GeneradorDeEnemigos no lo crea): falta que el
// grupo decida si se suma a los pisos 6-9 o se borra.
// 50% ataca / 20% aturde / 20% envenena / 10% Toque de Muerte (bloquea
// toda curación del objetivo durante 2 turnos).
public class Esqueleto extends Enemigo {

    private static final String[] ACCIONES = {
            "atacar", "atacar", "atacar", "atacar", "atacar", // 50%
            "aturdir", "aturdir",                             // 20%
            "envenenar", "envenenar",                         // 20%
            "especial"                                        // 10%
    };

    private static final int DURACION_IMPEDIMENTO = 2;

    // vida, ataque, defensa, velocidad, nivel, experiencia que otorga
    public Esqueleto(String nombre) {
        super(nombre, 80, 55, 8, 15, 1, 40);
    }

    @Override
    public String actuar(Personaje objetivo) {
        String accion = elegirAccionPonderada(ACCIONES);
        if (accion.equals("especial")) {
            return usarToqueDeMuerte(objetivo);
        }
        return ejecutarAccionSobre(accion, objetivo);
    }

    private String usarToqueDeMuerte(Personaje objetivo) {
        int vidaAntes = objetivo.getVidaActual();
        objetivo.recibirDanio(calcularDanioConMultiplicador(objetivo, 1.0));
        objetivo.aplicarImpedimentoDeCuracion(DURACION_IMPEDIMENTO);
        return getNombre() + " usa Toque de Muerte sobre " + objetivo.getNombre() + ": "
                + (vidaAntes - objetivo.getVidaActual()) + " de daño y no puede curarse por "
                + DURACION_IMPEDIMENTO + " turnos.";
    }
}
