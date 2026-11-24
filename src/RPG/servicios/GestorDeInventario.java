package RPG.servicios;

import RPG.modelo.Batalla;
import RPG.modelo.Party;
import RPG.modelo.items.Arma;
import RPG.modelo.items.Armadura;
import RPG.modelo.items.Consumible;
import RPG.modelo.items.Escudo;
import RPG.modelo.items.Item;
import RPG.modelo.personajes.Personaje;

public class GestorDeInventario {

    public Resultado usarFueraDeBatalla(Party party, Batalla batalla, Item item, Personaje objetivo) {
        if (item instanceof Arma || item instanceof Armadura) {
            return equipar(party, batalla, item, objetivo);
        }

        String error = validarConsumible(party, item, objetivo, false);
        if (error != null) {
            return new Resultado(false, error);
        }

        Item consumido = party.getInventario().removerItem(item);
        if (consumido == null) {
            return new Resultado(false, "Ese ítem ya no está en el inventario.\n");
        }
        consumido.usar(objetivo);
        return new Resultado(true, objetivo.getNombre() + " usó " + consumido.getNombre() + ".\n");
    }

    public Resultado usarEnBatalla(Party party, Batalla batalla, Personaje actor,
                                  Item item, Personaje objetivo) {
        if (batalla == null || batalla.estaTerminada()) {
            return new Resultado(false, "No hay una batalla en curso.\n");
        }
        if (actor == null) {
            return new Resultado(false, "No es el turno de un personaje.\n");
        }

        String error = validarConsumible(party, item, objetivo, true);
        if (error != null) {
            return new Resultado(false, error);
        }

        Item consumido = party.getInventario().removerItem(item);
        if (consumido == null) {
            return new Resultado(false, "Ese ítem ya no está en el inventario.\n");
        }
        consumido.usar(objetivo);
        if (consumido instanceof Escudo && actor == objetivo) {
            objetivo.omitirProximoDescuentoEscudo();
        }
        return new Resultado(true, objetivo.getNombre() + " usó " + consumido.getNombre() + ".\n");
    }

    private Resultado equipar(Party party, Batalla batalla, Item item, Personaje objetivo) {
        if (batalla != null && !batalla.estaTerminada()) {
            return new Resultado(false, "No se puede cambiar el equipo durante una batalla.\n");
        }
        if (objetivo == null) {
            return new Resultado(false, "Seleccioná un personaje para equipar el ítem.\n");
        }
        if (!perteneceALaParty(party, objetivo)) {
            return new Resultado(false, "El personaje no pertenece a la party.\n");
        }
        if (!party.getInventario().getItems().contains(item)) {
            return new Resultado(false, "Ese ítem ya no está en el inventario.\n");
        }

        Item equipado = party.getInventario().removerItem(item);
        Item reemplazado;
        if (equipado instanceof Arma) {
            reemplazado = objetivo.equiparArma((Arma) equipado);
        } else {
            reemplazado = objetivo.equiparArmadura((Armadura) equipado);
        }
        if (reemplazado != null && !party.getInventario().agregarItem(reemplazado)) {
            throw new IllegalStateException("No se pudo devolver el equipo reemplazado al inventario.");
        }
        return new Resultado(true, objetivo.getNombre() + " equipó " + equipado.getNombre()
                + (reemplazado == null ? "." : " y guardó " + reemplazado.getNombre() + " en el inventario.")
                + "\n");
    }

    private String validarConsumible(Party party, Item item, Personaje objetivo, boolean duranteBatalla) {
        if (item == null || objetivo == null) {
            return "Seleccioná un ítem y un personaje objetivo.\n";
        }
        if (!(item instanceof Consumible)) {
            return "Este ítem no se puede usar.\n";
        }
        if (item instanceof Escudo && !duranteBatalla) {
            return "El escudo solo se puede usar durante una batalla.\n";
        }
        if (!objetivo.estaVivo()) {
            return "No se puede usar un ítem sobre alguien caído.\n";
        }
        if (!perteneceALaParty(party, objetivo)) {
            return "El objetivo no pertenece a la party.\n";
        }
        if (!party.getInventario().getItems().contains(item)) {
            return "Ese ítem ya no está en el inventario.\n";
        }
        if (!((Consumible) item).tieneEfectoEn(objetivo)) {
            return "Ese ítem no tendría efecto sobre " + objetivo.getNombre() + ".\n";
        }
        return null;
    }

    private boolean perteneceALaParty(Party party, Personaje objetivo) {
        for (Personaje miembro : party.getMiembros()) {
            if (miembro == objetivo) {
                return true;
            }
        }
        return false;
    }

    public static final class Resultado {
        private final boolean exitoso;
        private final String mensaje;

        private Resultado(boolean exitoso, String mensaje) {
            this.exitoso = exitoso;
            this.mensaje = mensaje;
        }

        public boolean fueExitoso() {
            return exitoso;
        }

        public String getMensaje() {
            return mensaje;
        }
    }
}
