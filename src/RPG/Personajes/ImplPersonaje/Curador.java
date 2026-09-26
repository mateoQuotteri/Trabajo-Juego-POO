package RPG.Personajes.ImplPersonaje;

import RPG.Personajes.Combatiente;
import RPG.Personajes.Personaje;

// Ataque bajo, pero maná alto (va a estar curando constantemente). Reemplaza
// a la vieja clase "Sacerdote" del sistema descartado.
//
// La habilidad poderosa (Revivir) quedó definida por el grupo el
// 25/09/2026, resolviendo la diferencia que había entre los dos documentos
// de diseño: revive al objetivo con 25% de SU vida máxima; pero si el
// Curador se la aplica a sí misma, en cambio le da inmortalidad durante 2
// turnos (no pierde vida por ningún golpe mientras dure).
public class Curador extends Personaje {

    private static final String NOMBRE_HABILIDAD_BASICA = "Sanación";
    private static final int COSTO_HABILIDAD_BASICA = 10;
    private static final int CURACION_HABILIDAD_BASICA = 20;

    private static final String NOMBRE_HABILIDAD_PODEROSA = "Revivir";
    private static final int COSTO_HABILIDAD_PODEROSA = 30;
    private static final double PORCENTAJE_VIDA_AL_REVIVIR = 0.25; // 25% de la vida máxima del objetivo
    private static final int DURACION_INMORTALIDAD = 2; // turnos, si se la aplica a sí misma

    // nombre, vidaMaxima, manaMaximo, ataqueFisico, ataqueMagico, defensa,
    // velocidad. Valores de Nivel 1 según la tabla del documento.
    public Curador(String nombre) {
        super(nombre, 35, 40, 15, 20, 22, 28);
    }

    // Sanación: cura 20 de vida. A diferencia de las demás clases, el
    // "objetivo" acá es un aliado (un Personaje con vida para recuperar),
    // no un enemigo — por eso se usa recibirCuracion() en vez de
    // recibirDanio(). Es el mismo método usarHabilidadBasica(Combatiente)
    // que las otras clases, solo que el efecto que produce es distinto
    // (polimorfismo: quien maneja el turno no necesita saber esta
    // diferencia, solo llama "usá tu habilidad básica").
    @Override
    public void usarHabilidadBasica(Combatiente objetivo) {
        if (!puedeUsarHabilidad(COSTO_HABILIDAD_BASICA)) {
            return;
        }
        gastarMana(COSTO_HABILIDAD_BASICA);
        objetivo.recibirCuracion(CURACION_HABILIDAD_BASICA);
    }

    // Revivir: si el objetivo es OTRO personaje, recupera 25% de su propia
    // vida máxima (sirve tanto para revivir a un aliado caído como para
    // curar de golpe a uno que sigue en pie). Si el Curador se la aplica a
    // SÍ MISMA (objetivo == this, comparación de identidad: es el mismo
    // objeto), en cambio no cura nada: le da inmortalidad por 2 turnos.
    @Override
    public void usarHabilidadPoderosa(Combatiente objetivo) {
        if (!puedeUsarHabilidad(COSTO_HABILIDAD_PODEROSA)) {
            return;
        }
        gastarMana(COSTO_HABILIDAD_PODEROSA);

        if (objetivo == this) {
            activarInmortalidad(DURACION_INMORTALIDAD);
        } else {
            int vidaRecuperada = (int) (objetivo.getVidaMaxima() * PORCENTAJE_VIDA_AL_REVIVIR);
            objetivo.recibirCuracion(vidaRecuperada);
        }
    }

    @Override
    public String getNombreHabilidadBasica() {
        return NOMBRE_HABILIDAD_BASICA;
    }

    @Override
    public int getCostoHabilidadBasica() {
        return COSTO_HABILIDAD_BASICA;
    }

    @Override
    public String getNombreHabilidadPoderosa() {
        return NOMBRE_HABILIDAD_PODEROSA;
    }

    @Override
    public int getCostoHabilidadPoderosa() {
        return COSTO_HABILIDAD_PODEROSA;
    }
}
