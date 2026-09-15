package RPG;

import RPG.Personajes.Personaje;
import java.util.Scanner;

// Se encarga de pedirle al jugador, por consola, cómo repartir los puntos
// de atributo de un personaje: al crear la partida y en cada subida de
// nivel. Es una clase aparte (no vive dentro de Personaje) para no mezclar
// la lógica del personaje con la entrada/salida por consola (cada clase
// tiene una sola responsabilidad).
public class RepartidorAtributos {

    // ---------- Configuración del reparto ----------
    private static final int CANTIDAD_ATRIBUTOS = 5; // Fuerza, Destreza, Inteligencia, Espiritu, Suerte
    private static final int PUNTOS_POR_ATRIBUTO_AL_CREAR = 10; // reparto inicial (50 puntos en total)
    private static final int PUNTOS_POR_ATRIBUTO_POR_NIVEL = 5; // reparto en cada level up (25 en total)
    private static final int PUNTOS_DE_SUERTE_POR_BONUS = 3;    // cada 3 puntos de Suerte = +1 punto extra

    // Scanner compartido para leer lo que el jugador escribe por consola.
    // Se recibe por afuera (en vez de crear uno acá) para que Main use un
    // solo Scanner para todo el juego.
    private Scanner scanner;

    public RepartidorAtributos(Scanner scanner){
        this.scanner = scanner;
    }

    // Reparto inicial, al crear el personaje: 10 puntos por atributo (50 en
    // total). Se llama una vez por personaje, al armar la party.
    public void repartirAlCrear(Personaje personaje){
        repartir(personaje, PUNTOS_POR_ATRIBUTO_AL_CREAR);
    }

    // Reparto al subir de nivel: 5 puntos por atributo (25 en total), que se
    // suman a lo que el personaje ya tenía acumulado. Se llama cada vez que
    // el personaje sube de nivel (después de personaje.subirNivel()).
    public void repartirPorNivel(Personaje personaje){
        repartir(personaje, PUNTOS_POR_ATRIBUTO_POR_NIVEL);
    }

    // Lógica común a los dos repartos de arriba. Recibe cuántos puntos le
    // corresponden a CADA atributo (10 al crear, 5 por nivel) y arma el
    // total a repartir multiplicando por la cantidad de atributos.
    private void repartir(Personaje personaje, int puntosPorAtributo){
        int puntosTotales = puntosPorAtributo * CANTIDAD_ATRIBUTOS;

        System.out.println();
        System.out.println("=== Reparto de atributos para " + personaje.getNombre() + " ===");
        System.out.println("Tenés " + puntosTotales + " puntos para repartir entre Suerte, Fuerza, Destreza, Inteligencia y Espiritu.");

        int puntosRestantes = puntosTotales;

        // Suerte se reparte primero: cuantos más puntos le pongas, más
        // puntos extra te da para repartir en los demás atributos (ver
        // PUNTOS_DE_SUERTE_POR_BONUS). Por eso hay que saber su valor antes
        // de preguntar por el resto.
        int suerte = pedirPuntos("Suerte", puntosRestantes);
        puntosRestantes -= suerte;

        int puntosExtra = suerte / PUNTOS_DE_SUERTE_POR_BONUS; // división entera: sobrantes se pierden
        if (puntosExtra > 0) {
            System.out.println("Suerte=" + suerte + " te da " + puntosExtra + " puntos extra para repartir en el resto de los atributos.");
            puntosRestantes += puntosExtra;
        }

        // El resto de los atributos se reparten en orden, descontando cada
        // vez lo que ya se gastó, para que nunca se pueda pasar del total
        // disponible (ver pedirPuntos(), que valida esto en un bucle).
        int fuerza = pedirPuntos("Fuerza", puntosRestantes);
        puntosRestantes -= fuerza;

        int destreza = pedirPuntos("Destreza", puntosRestantes);
        puntosRestantes -= destreza;

        int inteligencia = pedirPuntos("Inteligencia", puntosRestantes);
        puntosRestantes -= inteligencia;

        int espiritu = pedirPuntos("Espiritu", puntosRestantes);
        puntosRestantes -= espiritu;

        // Una vez decididos los 5 valores, se suman de una sola vez a los
        // atributos que el personaje ya tenía (ver Personaje.sumarAtributos()).
        personaje.sumarAtributos(fuerza, destreza, inteligencia, espiritu, suerte);

        // Resumen de lo que se repartió en esta ronda y de los totales
        // acumulados del personaje hasta ahora.
        System.out.println(personaje.getNombre() + " sumó Fuerza=" + fuerza
                + ", Destreza=" + destreza
                + ", Inteligencia=" + inteligencia
                + ", Espiritu=" + espiritu
                + ", Suerte=" + suerte);
        System.out.println("Totales actuales -> Fuerza=" + personaje.getFuerza()
                + ", Destreza=" + personaje.getDestreza()
                + ", Inteligencia=" + personaje.getInteligencia()
                + ", Espiritu=" + personaje.getEspiritu()
                + ", Suerte=" + personaje.getSuerte());
        if (puntosRestantes > 0) {
            System.out.println("(Quedaron " + puntosRestantes + " puntos sin usar)");
        }
    }

    // Le pregunta al jugador cuántos puntos quiere poner en un atributo
    // puntual, y repite la pregunta mientras la respuesta sea inválida
    // (negativa o mayor a lo disponible). Devuelve el valor ya validado.
    private int pedirPuntos(String nombreAtributo, int puntosDisponibles){
        int puntos = -1;

        while (puntos < 0 || puntos > puntosDisponibles) {
            System.out.print("Puntos disponibles: " + puntosDisponibles + ". ¿Cuántos poner en " + nombreAtributo + "? ");
            puntos = scanner.nextInt();

            if (puntos < 0 || puntos > puntosDisponibles) {
                System.out.println("Valor inválido. Tiene que ser un número entre 0 y " + puntosDisponibles + ".");
            }
        }

        return puntos;
    }
}
