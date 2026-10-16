package RPG.modelo.habilidades;

import RPG.modelo.Combatiente;
import RPG.modelo.personajes.Personaje;

// Habilidad básica del Arquero. Según la decisión del grupo (checklist,
// punto 5): daño físico normal, pero un 40% de las veces sale crítico y
// pega el doble.
// OJO: el documento entregado (Fase A) dice "Disparo Certero (daño físico
// x1.5)". Hay que decidir cuál de las dos versiones queda y alinear código
// y documento (ver CAMBIOS-16-10 en docs/).
public class DisparoCertero extends Habilidad {

    private static final double PROBABILIDAD_CRITICO = 0.40;

    public DisparoCertero() {
        super("Disparo Certero", 10);
    }

    @Override
    public String ejecutar(Personaje usuario, Combatiente objetivo) {
        int danio = calcularDanioBase(usuario, objetivo, false);
        String detalle = "";
        if (Math.random() < PROBABILIDAD_CRITICO) {
            danio = danio * 2;
            detalle = " (¡CRÍTICO!)";
        }
        return aplicarDanio(usuario, objetivo, danio, detalle);
    }
}
