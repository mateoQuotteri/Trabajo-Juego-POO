package RPG.persistencia;

import RPG.modelo.Party;
import RPG.modelo.items.Arma;
import RPG.modelo.items.Armadura;
import RPG.modelo.items.Escudo;
import RPG.modelo.items.Item;
import RPG.modelo.items.PocionMana;
import RPG.modelo.items.PocionVida;
import RPG.modelo.personajes.Arquero;
import RPG.modelo.personajes.Curador;
import RPG.modelo.personajes.Guerrero;
import RPG.modelo.personajes.Mago;
import RPG.modelo.personajes.Personaje;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class PersistenciaPartida {

    private static final Path ARCHIVO = Paths.get("partida.json");
    private static final int VERSION = 2;

    private PersistenciaPartida() {
    }

    public static void guardar(Party party, int pisoSiguiente) throws IOException {
        guardar(ARCHIVO, party, pisoSiguiente);
    }

    public static void guardar(Path archivo, Party party, int pisoSiguiente) throws IOException {
        if (archivo == null || party == null) {
            throw new IllegalArgumentException("El archivo y la party son obligatorios.");
        }
        Personaje[] personajes = party.getMiembros();
        if (pisoSiguiente < 1 || pisoSiguiente > 10
                || personajes.length < 2 || personajes.length > 4) {
            throw new IllegalArgumentException("El progreso de la partida no es válido.");
        }

        String json = serializar(party, personajes, pisoSiguiente);
        Path rutaAbsoluta = archivo.toAbsolutePath();
        Path directorio = rutaAbsoluta.getParent();
        Path temporal = Files.createTempFile(directorio, "partida-", ".tmp");
        try {
            Files.write(temporal, json.getBytes(StandardCharsets.UTF_8));
            try {
                Files.move(temporal, rutaAbsoluta, StandardCopyOption.REPLACE_EXISTING,
                        StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException ex) {
                Files.move(temporal, rutaAbsoluta, StandardCopyOption.REPLACE_EXISTING);
            }
        } finally {
            Files.deleteIfExists(temporal);
        }
    }

    public static PartidaCargada cargar() throws IOException {
        return cargar(ARCHIVO);
    }

    public static PartidaCargada cargar(Path archivo) throws IOException {
        if (archivo == null) {
            throw new IllegalArgumentException("El archivo de partida es obligatorio.");
        }
        if (!Files.isRegularFile(archivo)) {
            throw new IOException("No existe una partida guardada en " + archivo + ".");
        }

        String json = new String(Files.readAllBytes(archivo), StandardCharsets.UTF_8);
        try {
            return deserializar(json);
        } catch (IllegalArgumentException ex) {
            throw new IOException("El archivo de partida no es válido: " + ex.getMessage(), ex);
        }
    }

    private static String serializar(Party party, Personaje[] personajes, int pisoSiguiente) {
        StringBuilder json = new StringBuilder();
        json.append("{\n  \"version\": ").append(VERSION)
                .append(",\n  \"pisoSiguiente\": ").append(pisoSiguiente)
                .append(",\n  \"oro\": ").append(party.getOro())
                .append(",\n  \"personajes\": [");
        for (int i = 0; i < personajes.length; i++) {
            Personaje personaje = personajes[i];
            if (i > 0) {
                json.append(',');
            }
            json.append("\n    {\"clase\": ");
            agregarCadena(json, personaje.getNombreClase());
            json.append(", \"nombre\": ");
            agregarCadena(json, personaje.getNombre());
            json.append(", \"nivel\": ").append(personaje.getNivel())
                    .append(", \"experiencia\": ").append(personaje.getExperiencia())
                    .append(", \"vidaActual\": ").append(personaje.getVidaActual())
                    .append(", \"manaActual\": ").append(personaje.getManaActual())
                    .append(", \"arma\": ");
            agregarCadena(json, personaje.getArmaEquipada() == null ? "" : "Arma");
            json.append(", \"armadura\": ");
            agregarCadena(json, personaje.getArmaduraEquipada() == null ? "" : "Armadura");
            json.append('}');
        }
        json.append("\n  ],\n  \"inventario\": [");
        List<Item> items = party.getInventario().getItems();
        for (int i = 0; i < items.size(); i++) {
            if (i > 0) {
                json.append(", ");
            }
            agregarCadena(json, tipoItem(items.get(i)));
        }
        return json.append("]\n}\n").toString();
    }

    private static void agregarCadena(StringBuilder json, String valor) {
        json.append('"');
        for (int i = 0; i < valor.length(); i++) {
            char caracter = valor.charAt(i);
            switch (caracter) {
                case '"':
                    json.append("\\\"");
                    break;
                case '\\':
                    json.append("\\\\");
                    break;
                case '\b':
                    json.append("\\b");
                    break;
                case '\f':
                    json.append("\\f");
                    break;
                case '\n':
                    json.append("\\n");
                    break;
                case '\r':
                    json.append("\\r");
                    break;
                case '\t':
                    json.append("\\t");
                    break;
                default:
                    if (caracter < 0x20) {
                        json.append(String.format("\\u%04x", (int) caracter));
                    } else {
                        json.append(caracter);
                    }
            }
        }
        json.append('"');
    }

    private static PartidaCargada deserializar(String json) {
        Object raiz = new AnalizadorJson(json).analizar();
        Map<?, ?> datos = comoObjeto(raiz, "partida");
        int version = leerEntero(datos, "version");
        if (version < 1 || version > VERSION) {
            throw new IllegalArgumentException("La versión del archivo no es compatible.");
        }

        int pisoSiguiente = leerEntero(datos, "pisoSiguiente");
        int oro = leerEntero(datos, "oro");
        if (pisoSiguiente < 1 || pisoSiguiente > 10 || oro < 0) {
            throw new IllegalArgumentException("El piso o el oro guardado no son válidos.");
        }

        List<?> personajesGuardados = comoLista(datos.get("personajes"), "personajes");
        if (personajesGuardados.size() < 2 || personajesGuardados.size() > 4) {
            throw new IllegalArgumentException("La partida debe tener entre 2 y 4 personajes.");
        }
        Party party = new Party();
        for (Object personajeGuardado : personajesGuardados) {
            Map<?, ?> datosPersonaje = comoObjeto(personajeGuardado, "personaje");
            String clase = leerCadena(datosPersonaje, "clase");
            String nombre = leerCadena(datosPersonaje, "nombre");
            if (nombre.trim().isEmpty()) {
                throw new IllegalArgumentException("El nombre de un personaje está vacío.");
            }
            Personaje personaje = crearPersonaje(clase, nombre);
            personaje.restaurarProgreso(
                    leerEntero(datosPersonaje, "nivel"),
                    leerEntero(datosPersonaje, "experiencia"),
                    leerEntero(datosPersonaje, "vidaActual"),
                    leerEntero(datosPersonaje, "manaActual"));
            if (version >= 2) {
                String arma = leerCadena(datosPersonaje, "arma");
                String armadura = leerCadena(datosPersonaje, "armadura");
                if (!arma.isEmpty()) {
                    personaje.equiparArma(crearArma(arma));
                }
                if (!armadura.isEmpty()) {
                    personaje.equiparArmadura(crearArmadura(armadura));
                }
            }
            if (!party.agregarMiembro(personaje)) {
                throw new IllegalArgumentException("La party guardada tiene demasiados miembros.");
            }
        }
        party.agregarOro(oro);

        List<?> itemsGuardados = comoLista(datos.get("inventario"), "inventario");
        if (itemsGuardados.size() > party.getInventario().getCapacidadMaxima()) {
            throw new IllegalArgumentException("El inventario guardado supera su capacidad.");
        }
        for (Object itemGuardado : itemsGuardados) {
            if (!(itemGuardado instanceof String)
                    || !party.getInventario().agregarItem(crearItem((String) itemGuardado))) {
                throw new IllegalArgumentException("El inventario guardado contiene un ítem no válido.");
            }
        }
        return new PartidaCargada(party, pisoSiguiente);
    }

    private static Personaje crearPersonaje(String clase, String nombre) {
        if ("Guerrero".equals(clase)) {
            return new Guerrero(nombre);
        }
        if ("Mago".equals(clase)) {
            return new Mago(nombre);
        }
        if ("Arquero".equals(clase)) {
            return new Arquero(nombre);
        }
        if ("Curador".equals(clase)) {
            return new Curador(nombre);
        }
        throw new IllegalArgumentException("Clase de personaje desconocida: " + clase);
    }

    private static String tipoItem(Item item) {
        if (item instanceof Arma) {
            return "Arma";
        }
        if (item instanceof Armadura) {
            return "Armadura";
        }
        if (item instanceof PocionVida) {
            return "PocionVida";
        }
        if (item instanceof PocionMana) {
            return "PocionMana";
        }
        if (item instanceof Escudo) {
            return "Escudo";
        }
        throw new IllegalArgumentException("No se puede guardar el ítem: " + item.getNombre());
    }

    private static Item crearItem(String tipo) {
        if ("Arma".equals(tipo)) {
            return new Arma();
        }
        if ("Armadura".equals(tipo)) {
            return new Armadura();
        }
        if ("PocionVida".equals(tipo)) {
            return new PocionVida();
        }
        if ("PocionMana".equals(tipo)) {
            return new PocionMana();
        }
        if ("Escudo".equals(tipo)) {
            return new Escudo();
        }
        throw new IllegalArgumentException("Tipo de ítem desconocido: " + tipo);
    }

    private static Arma crearArma(String tipo) {
        if ("Arma".equals(tipo)) {
            return new Arma();
        }
        throw new IllegalArgumentException("Tipo de arma desconocido: " + tipo);
    }

    private static Armadura crearArmadura(String tipo) {
        if ("Armadura".equals(tipo)) {
            return new Armadura();
        }
        throw new IllegalArgumentException("Tipo de armadura desconocido: " + tipo);
    }

    private static Map<?, ?> comoObjeto(Object valor, String nombre) {
        if (!(valor instanceof Map)) {
            throw new IllegalArgumentException("Se esperaba un objeto para " + nombre + ".");
        }
        return (Map<?, ?>) valor;
    }

    private static List<?> comoLista(Object valor, String nombre) {
        if (!(valor instanceof List)) {
            throw new IllegalArgumentException("Se esperaba una lista para " + nombre + ".");
        }
        return (List<?>) valor;
    }

    private static int leerEntero(Map<?, ?> objeto, String clave) {
        Object valor = objeto.get(clave);
        if (!(valor instanceof Integer)) {
            throw new IllegalArgumentException("El campo " + clave + " debe ser un entero.");
        }
        return (Integer) valor;
    }

    private static String leerCadena(Map<?, ?> objeto, String clave) {
        Object valor = objeto.get(clave);
        if (!(valor instanceof String)) {
            throw new IllegalArgumentException("El campo " + clave + " debe ser texto.");
        }
        return (String) valor;
    }

    public static final class PartidaCargada {
        private final Party party;
        private final int pisoSiguiente;

        private PartidaCargada(Party party, int pisoSiguiente) {
            this.party = party;
            this.pisoSiguiente = pisoSiguiente;
        }

        public Party getParty() {
            return party;
        }

        public int getPisoSiguiente() {
            return pisoSiguiente;
        }
    }

    private static final class AnalizadorJson {
        private final String texto;
        private int posicion;

        private AnalizadorJson(String texto) {
            this.texto = texto;
        }

        private Object analizar() {
            Object valor = leerValor();
            saltarEspacios();
            if (posicion != texto.length()) {
                error("Hay contenido adicional después del objeto.");
            }
            return valor;
        }

        private Object leerValor() {
            saltarEspacios();
            if (posicion >= texto.length()) {
                return error("El archivo está vacío o incompleto.");
            }
            char caracter = texto.charAt(posicion);
            if (caracter == '{') {
                return leerObjeto();
            }
            if (caracter == '[') {
                return leerLista();
            }
            if (caracter == '"') {
                return leerCadena();
            }
            if (caracter == '-' || Character.isDigit(caracter)) {
                return leerEntero();
            }
            return error("El archivo contiene un valor JSON no admitido.");
        }

        private Map<String, Object> leerObjeto() {
            avanzar('{');
            Map<String, Object> objeto = new LinkedHashMap<String, Object>();
            saltarEspacios();
            if (aceptar('}')) {
                return objeto;
            }
            while (true) {
                saltarEspacios();
                if (posicion >= texto.length() || texto.charAt(posicion) != '"') {
                    return error("Se esperaba una clave de objeto.");
                }
                String clave = leerCadena();
                saltarEspacios();
                avanzar(':');
                if (objeto.containsKey(clave)) {
                    return error("La clave " + clave + " está repetida.");
                }
                objeto.put(clave, leerValor());
                saltarEspacios();
                if (aceptar('}')) {
                    return objeto;
                }
                avanzar(',');
            }
        }

        private List<Object> leerLista() {
            avanzar('[');
            List<Object> lista = new ArrayList<Object>();
            saltarEspacios();
            if (aceptar(']')) {
                return lista;
            }
            while (true) {
                lista.add(leerValor());
                saltarEspacios();
                if (aceptar(']')) {
                    return lista;
                }
                avanzar(',');
            }
        }

        private String leerCadena() {
            avanzar('"');
            StringBuilder resultado = new StringBuilder();
            while (posicion < texto.length()) {
                char caracter = texto.charAt(posicion++);
                if (caracter == '"') {
                    return resultado.toString();
                }
                if (caracter < 0x20) {
                    return error("Una cadena contiene un carácter de control sin escapar.");
                }
                if (caracter != '\\') {
                    resultado.append(caracter);
                    continue;
                }
                if (posicion >= texto.length()) {
                    return error("La cadena termina con una secuencia de escape incompleta.");
                }
                char escape = texto.charAt(posicion++);
                switch (escape) {
                    case '"':
                    case '\\':
                    case '/':
                        resultado.append(escape);
                        break;
                    case 'b':
                        resultado.append('\b');
                        break;
                    case 'f':
                        resultado.append('\f');
                        break;
                    case 'n':
                        resultado.append('\n');
                        break;
                    case 'r':
                        resultado.append('\r');
                        break;
                    case 't':
                        resultado.append('\t');
                        break;
                    case 'u':
                        resultado.append(leerUnicode());
                        break;
                    default:
                        return error("La cadena contiene una secuencia de escape no válida.");
                }
            }
            return error("La cadena no está cerrada.");
        }

        private char leerUnicode() {
            if (posicion + 4 > texto.length()) {
                return error("La secuencia Unicode está incompleta.");
            }
            int valor = 0;
            for (int i = 0; i < 4; i++) {
                int digito = Character.digit(texto.charAt(posicion++), 16);
                if (digito < 0) {
                    return error("La secuencia Unicode no es válida.");
                }
                valor = valor * 16 + digito;
            }
            return (char) valor;
        }

        private Integer leerEntero() {
            int inicio = posicion;
            if (aceptar('-') && posicion >= texto.length()) {
                return error("El número está incompleto.");
            }
            if (aceptar('0')) {
                if (posicion < texto.length() && Character.isDigit(texto.charAt(posicion))) {
                    return error("El número tiene ceros iniciales.");
                }
            } else {
                if (posicion >= texto.length() || texto.charAt(posicion) < '1'
                        || texto.charAt(posicion) > '9') {
                    return error("El número no es válido.");
                }
                while (posicion < texto.length() && Character.isDigit(texto.charAt(posicion))) {
                    posicion++;
                }
            }
            try {
                return Integer.valueOf(texto.substring(inicio, posicion));
            } catch (NumberFormatException ex) {
                return error("El número está fuera del rango permitido.");
            }
        }

        private void avanzar(char esperado) {
            saltarEspacios();
            if (!aceptar(esperado)) {
                error("Se esperaba '" + esperado + "'.");
            }
        }

        private boolean aceptar(char esperado) {
            if (posicion < texto.length() && texto.charAt(posicion) == esperado) {
                posicion++;
                return true;
            }
            return false;
        }

        private void saltarEspacios() {
            while (posicion < texto.length() && Character.isWhitespace(texto.charAt(posicion))) {
                posicion++;
            }
        }

        private <T> T error(String mensaje) {
            throw new IllegalArgumentException(mensaje + " (posición " + posicion + ").");
        }
    }
}
