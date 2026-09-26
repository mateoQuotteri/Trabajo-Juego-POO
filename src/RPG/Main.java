package RPG;

import RPG.Personajes.*;
import RPG.Personajes.ImplPersonaje.Arquero;
import RPG.Personajes.ImplPersonaje.Curador;
import RPG.Personajes.ImplPersonaje.Guerrero;
import RPG.Personajes.ImplPersonaje.Mago;
import RPG.Personajes.ImplEnemigo.Goblin;
import RPG.Personajes.ImplEnemigo.Jefe;
import RPG.Personajes.ImplEnemigo.JefeFinal;
import RPG.Personajes.ImplEnemigo.Slime;
import java.util.Scanner;

// Punto de entrada del juego. Migración del código viejo (que usaba
// Cazador/Sacerdote y el reparto de atributos Fuerza/Destreza/Inteligencia/
// Espiritu/Suerte por consola, con RepartidorAtributos y
// RepartidorHabilidades) al diseño confirmado por el grupo en
// Diseno-RPG-POO.docx y Diseno-RPG-POO2.docx: Guerrero/Mago/Arquero/Curador
// con Vida/Maná/Ataque físico/Ataque mágico/Defensa/Velocidad, habilidades
// limitadas solo por maná (sin puntos ni cooldown de turnos).
//
// RepartidorAtributos.java y RepartidorHabilidades.java ya no existen: el
// diseño nuevo no reparte nada por consola al crear o subir de nivel, así
// que no hacen falta (ver checklist-tpo.txt).
//
// Esto sigue siendo una prueba manual, no el motor de combate por turnos
// real (todavía falta ordenar por Velocidad y armar el loop de rondas: ver
// checklist-tpo.txt, FALTA - FASE B).
public class Main {

    public static void main(String[] args) {

        Party party = new Party();
        Batalla batalla = new Batalla("Piso 1 de la Torre", party);

        // Un personaje de cada clase (nombre de un integrante del grupo).
        // Los valores de Nivel 1 ya salen del constructor de cada clase.
        Guerrero mateo = new Guerrero("Mateo");
        Mago jose = new Mago("Jose");
        Arquero conrado = new Arquero("Conrado");
        Curador evelyn = new Curador("Evelyn");

        party.agregarMiembro(mateo);
        party.agregarMiembro(jose);
        party.agregarMiembro(conrado);
        party.agregarMiembro(evelyn);

        System.out.println("=== Estadísticas de Nivel 1 ===");
        for (Personaje personaje : party.getPersonajes()) {
            mostrarEstadisticas(personaje);
        }

        // Se crea un enemigo de prueba (Esqueleto, todavía placeholder: ver
        // Enemigo.java) y se agrega a la batalla.
        Esqueleto esqueleto = new Esqueleto("Esqueleto");
        batalla.agregarEnemigo(esqueleto);

        System.out.println();
        System.out.println("=== Prueba: ataque básico ===");
        System.out.println(esqueleto.getNombre() + " arranca con " + esqueleto.getVidaActual() + " de vida.");
        mateo.atacar(esqueleto);
        System.out.println(mateo.getNombre() + " (ataque básico) le pegó al esqueleto. Vida restante: " + esqueleto.getVidaActual());

        // Prueba de maná por turno: como Jose (Mago) todavía no usó ninguna
        // habilidad, cada vez que "le toca actuar" su maná sube +10.
        System.out.println();
        System.out.println("=== Prueba: maná por turno (sin usar habilidad) ===");
        System.out.println(jose.getNombre() + " arranca con " + jose.getManaActual() + "/" + jose.getManaMaximo() + " de maná.");
        jose.crecerManaPorTurno();
        jose.crecerManaPorTurno();
        System.out.println("Después de 2 turnos sin usar habilidad: " + jose.getManaActual() + "/" + jose.getManaMaximo() + " de maná.");

        // Ahora que ya tiene maná (20, y Chispa cuesta 10), Jose usa su
        // habilidad básica contra el esqueleto.
        System.out.println();
        System.out.println("=== Prueba: habilidad básica (Chispa) ===");
        jose.usarHabilidadBasica(esqueleto);
        System.out.println(jose.getNombre() + " usó " + jose.getNombreHabilidadBasica()
                + " (costo " + jose.getCostoHabilidadBasica() + " maná). Maná restante: " + jose.getManaActual()
                + ". Vida restante del esqueleto: " + esqueleto.getVidaActual());

        // Defender del Guerrero: reduce a la mitad el próximo golpe que
        // reciba Mateo. Se compara un golpe SIN defender contra uno CON
        // defender para que se note la diferencia.
        System.out.println();
        System.out.println("=== Prueba: Defender del Guerrero ===");
        int vidaAntesSinDefender = mateo.getVidaActual();
        esqueleto.atacar(mateo);
        System.out.println("Esqueleto ataca a Mateo SIN defender. Vida antes: " + vidaAntesSinDefender
                + ", vida después: " + mateo.getVidaActual()
                + " (perdió " + (vidaAntesSinDefender - mateo.getVidaActual()) + ").");

        int vidaAntesConDefender = mateo.getVidaActual();
        mateo.defender();
        esqueleto.atacar(mateo);
        System.out.println("Mateo se defiende y el esqueleto lo ataca de nuevo. Vida antes: " + vidaAntesConDefender
                + ", vida después: " + mateo.getVidaActual()
                + " (perdió " + (vidaAntesConDefender - mateo.getVidaActual()) + ", la mitad del golpe anterior).");

        // Curación del Curador: Evelyn le da maná manualmente (simulación,
        // todavía no hay motor de turnos) y cura a Mateo, que quedó
        // lastimado por los dos golpes del esqueleto de arriba.
        System.out.println();
        System.out.println("=== Prueba: habilidad de curación (Sanación) ===");
        evelyn.crecerManaPorTurno(); // le da 10 de maná, justo lo que cuesta Sanación
        int vidaMateoAntes = mateo.getVidaActual();
        evelyn.usarHabilidadBasica(mateo);
        System.out.println(evelyn.getNombre() + " curó a " + mateo.getNombre() + " con " + evelyn.getNombreHabilidadBasica()
                + ". Vida de Mateo antes: " + vidaMateoAntes + ", después: " + mateo.getVidaActual());

        // Revivir (habilidad poderosa del Curador) tiene dos efectos según
        // el objetivo: sobre otro personaje, lo revive/cura con 25% de SU
        // vida máxima; sobre sí misma, le da inmortalidad por 2 turnos.
        // Junta el maná que le falta (le quedaban 0 después de Sanación,
        // Revivir cuesta 30) y prueba los dos casos.
        System.out.println();
        System.out.println("=== Prueba: habilidad poderosa (Revivir) sobre un aliado caído ===");
        for (int i = 0; i < 3; i++) {
            evelyn.crecerManaPorTurno(); // 3 turnos x 10 = 30, justo el costo de Revivir
        }
        // Se "mata" a Conrado a mano para simular un aliado caído (todavía
        // no hay motor de combate que lo deje sin vida de forma natural).
        conrado.recibirDanio(conrado.getVidaActual());
        System.out.println(conrado.getNombre() + " cayó (vida: " + conrado.getVidaActual() + "). " + conrado.getNombre()
                + " tiene " + conrado.getVidaMaxima() + " de vida máxima, así que Revivir debería devolverle "
                + (int) (conrado.getVidaMaxima() * 0.25) + ".");
        evelyn.usarHabilidadPoderosa(conrado);
        System.out.println(evelyn.getNombre() + " usó Revivir sobre " + conrado.getNombre()
                + ". Vida de " + conrado.getNombre() + " después: " + conrado.getVidaActual());

        System.out.println();
        System.out.println("=== Prueba: habilidad poderosa (Revivir) sobre sí misma -> inmortalidad ===");
        for (int i = 0; i < 3; i++) {
            evelyn.crecerManaPorTurno(); // otros 30 de maná para la segunda Revivir
        }
        evelyn.usarHabilidadPoderosa(evelyn);
        System.out.println(evelyn.getNombre() + " se aplicó Revivir a sí misma. ¿Es inmortal ahora? " + evelyn.esInmortal());
        int vidaEvelynAntes = evelyn.getVidaActual();
        esqueleto.atacar(evelyn);
        System.out.println("El esqueleto la atacó mientras era inmortal. Vida antes: " + vidaEvelynAntes
                + ", después: " + evelyn.getVidaActual() + " (no debería haber cambiado).");
        evelyn.reducirDuracionInmortalidad();
        evelyn.reducirDuracionInmortalidad();
        System.out.println("Pasaron los 2 turnos de inmortalidad. ¿Sigue siendo inmortal? " + evelyn.esInmortal());

        // ---------- Motor de combate por turnos ----------
        // Se arma una party y una batalla NUEVAS (las de arriba ya quedaron
        // tocadas por las pruebas manuales) y se juega una batalla completa
        // ronda por ronda con MotorDeCombate, preguntando por consola qué
        // hace cada personaje. Los enemigos deciden solos (por ahora,
        // siempre atacan: ver MotorDeCombate.jugarTurnoDeEnemigo).
        System.out.println();
        System.out.println("=== Motor de combate por turnos ===");

        Scanner scanner = new Scanner(System.in);

        Party party2 = new Party();
        party2.agregarMiembro(new Guerrero("Mateo"));
        party2.agregarMiembro(new Mago("Jose"));
        party2.agregarMiembro(new Arquero("Conrado"));
        party2.agregarMiembro(new Curador("Evelyn"));

        // Se usan enemigos de la jerarquía real (Goblin, en vez de Esqueleto)
        // para que la batalla de prueba también ejercite actuar() y el
        // aturdimiento (Goblin: 70% ataca, 30% aturde -- ver
        // RPG.Personajes.ImplEnemigo.Goblin).
        Batalla batalla2 = new Batalla("Piso 1 de la Torre", party2);
        batalla2.agregarEnemigo(new Goblin("Goblin 1"));
        batalla2.agregarEnemigo(new Goblin("Goblin 2"));

        MotorDeCombate motor = new MotorDeCombate(batalla2, scanner);
        motor.jugarBatalla();

        // ---------- Prueba: jerarquía de enemigos completa ----------
        // Una segunda batalla, contra el Jefe del Piso 5 ("Rey Slime"), para
        // ejercitar también el envenenar (Jefe: 50% ataca, 30% aturde, 20%
        // envenena -- ver RPG.Personajes.ImplEnemigo.Jefe).
        System.out.println();
        System.out.println("=== Prueba: jerarquía de enemigos (Slime, Goblin, Jefe, Jefe Final) ===");

        Slime slimeDePrueba = new Slime("Slime de prueba");
        Goblin goblinDePrueba = new Goblin("Goblin de prueba");
        Jefe jefeDePrueba = new Jefe();
        JefeFinal jefeFinalDePrueba = new JefeFinal();
        System.out.println(mostrarEstadisticasEnemigo(slimeDePrueba));
        System.out.println(mostrarEstadisticasEnemigo(goblinDePrueba));
        System.out.println(mostrarEstadisticasEnemigo(jefeDePrueba));
        System.out.println(mostrarEstadisticasEnemigo(jefeFinalDePrueba));

        Party party3 = new Party();
        party3.agregarMiembro(new Guerrero("Mateo"));
        party3.agregarMiembro(new Mago("Jose"));
        party3.agregarMiembro(new Arquero("Conrado"));
        party3.agregarMiembro(new Curador("Evelyn"));

        Batalla batalla3 = new Batalla("Piso 5 de la Torre", party3);
        batalla3.agregarEnemigo(jefeDePrueba);

        MotorDeCombate motorJefe = new MotorDeCombate(batalla3, scanner);
        motorJefe.jugarBatalla();
    }

    // Igual que mostrarEstadisticas(Personaje), pero para un Enemigo: sirve
    // para chequear a ojo que los 4 tipos de enemigo (Slime/Goblin/Jefe/Jefe
    // Final) tienen los valores de la tabla del documento de diseño.
    private static String mostrarEstadisticasEnemigo(Enemigo enemigo) {
        return enemigo.getNombre() + " (" + enemigo.getClass().getSimpleName() + ", Nivel " + enemigo.getNivel() + "): "
                + "Vida=" + enemigo.getVidaActual() + "/" + enemigo.getVidaMaxima()
                + ", Ataque=" + enemigo.getAtaque()
                + ", Defensa=" + enemigo.getDefensa()
                + ", Velocidad=" + enemigo.getVelocidad();
    }

    // Imprime las estadísticas actuales de un personaje, para chequear a
    // ojo que coinciden con la tabla del documento (o para ver cómo
    // cambiaron después de una prueba).
    private static void mostrarEstadisticas(Personaje personaje){
        System.out.println(personaje.getNombre() + " (" + personaje.getClass().getSimpleName() + "): "
                + "Vida=" + personaje.getVidaActual() + "/" + personaje.getVidaMaxima()
                + ", Mana=" + personaje.getManaActual() + "/" + personaje.getManaMaximo()
                + ", AtaqueFisico=" + personaje.getAtaqueFisico()
                + ", AtaqueMagico=" + personaje.getAtaqueMagico()
                + ", Defensa=" + personaje.getDefensa()
                + ", Velocidad=" + personaje.getVelocidad()
                + " | " + personaje.getNombreHabilidadBasica() + " (" + personaje.getCostoHabilidadBasica() + " maná)"
                + " / " + personaje.getNombreHabilidadPoderosa() + " (" + personaje.getCostoHabilidadPoderosa() + " maná)");
    }
}
