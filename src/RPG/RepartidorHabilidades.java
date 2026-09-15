package RPG;

import RPG.Personajes.Personaje;
import java.util.Scanner;

// Se encarga de preguntarle al jugador, por consola, en qué habilidad poner
// cada punto de habilidad que gana un personaje al subir de nivel. Es una
// clase aparte (no vive dentro de Personaje), por la misma razón que
// RepartidorAtributos: no mezclar la lógica del personaje con la entrada y
// salida por consola (una clase, una responsabilidad).
//
// No necesita saber los nombres ni la cantidad exacta de habilidades de cada
// clase: usa únicamente los getters genéricos de Personaje (nombre, nivel,
// nivel máximo y cooldown de la habilidad en la posición "i"), así que sirve
// igual para el Sacerdote (que ya tiene sus 4 habilidades definidas) que
// para las demás clases (que todavía tienen nombres genéricos "a definir").
public class RepartidorHabilidades {

    // Scanner compartido con el resto del juego (mismo patrón que
    // RepartidorAtributos): se recibe por afuera en vez de crear uno acá.
    private Scanner scanner;

    public RepartidorHabilidades(Scanner scanner){
        this.scanner = scanner;
    }

    // Gasta, de a uno, todos los puntos de habilidad que tenga el personaje
    // en ese momento (normalmente 1, recién ganado en subirNivel()). Si no
    // tiene puntos para gastar (por ejemplo, ya está en el nivel máximo de
    // personaje), no hace nada.
    public void repartirHabilidad(Personaje personaje){
        if (personaje.getPuntosDeHabilidad() <= 0) {
            return;
        }

        System.out.println();
        System.out.println("=== Puntos de habilidad para " + personaje.getNombre() + " ===");

        // Puede tener más de 1 punto acumulado (si no se gastó en niveles
        // anteriores), así que se repite hasta gastarlos todos.
        while (personaje.getPuntosDeHabilidad() > 0) {
            mostrarHabilidades(personaje);
            int indiceElegido = pedirIndiceHabilidad(personaje);
            boolean subio = personaje.subirNivelHabilidad(indiceElegido);

            if (subio) {
                System.out.println(personaje.getNombreHabilidad(indiceElegido) + " subió a Nivel "
                        + personaje.getNivelHabilidad(indiceElegido) + ".");
            } else {
                // Esto pasa si la habilidad elegida ya llegó a su nivel
                // máximo: no se gasta el punto y se vuelve a preguntar.
                System.out.println("Esa habilidad ya está en su nivel máximo. Elegí otra.");
            }
        }
    }

    // Imprime las 4 habilidades del personaje con su nivel actual, su tope
    // de nivel y su cooldown (en turnos), más los puntos que le quedan por
    // gastar.
    private void mostrarHabilidades(Personaje personaje){
        System.out.println("Puntos de habilidad disponibles: " + personaje.getPuntosDeHabilidad());

        for (int i = 0; i < personaje.getCantidadHabilidades(); i++) {
            System.out.println("  " + (i + 1) + ") " + personaje.getNombreHabilidad(i)
                    + " - Nivel " + personaje.getNivelHabilidad(i) + "/" + personaje.getNivelMaximoHabilidad(i)
                    + " (recarga: " + personaje.getCooldownHabilidad(i) + " turnos)");
        }
    }

    // Pide por consola un número de habilidad (1 a la cantidad total) y lo
    // valida, repitiendo la pregunta mientras la respuesta sea inválida.
    // Devuelve el índice interno (0 a cantidad-1) ya validado.
    private int pedirIndiceHabilidad(Personaje personaje){
        int cantidad = personaje.getCantidadHabilidades();
        int opcion = -1;

        while (opcion < 1 || opcion > cantidad) {
            System.out.print("¿En qué habilidad querés poner el punto? (1 a " + cantidad + "): ");
            opcion = scanner.nextInt();

            if (opcion < 1 || opcion > cantidad) {
                System.out.println("Opción inválida. Tiene que ser un número entre 1 y " + cantidad + ".");
            }
        }

        return opcion - 1;
    }
}
