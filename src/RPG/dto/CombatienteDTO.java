package RPG.dto;

public class CombatienteDTO {

    private final String nombre;
    private final int vidaActual;
    private final int vidaMaxima;
    private final int defensa;
    private final int turnosEscudoRestantes;
    private final boolean vivo;

    public CombatienteDTO(String nombre, int vidaActual, int vidaMaxima, int defensa,
                          int turnosEscudoRestantes, boolean vivo) {
        this.nombre = nombre;
        this.vidaActual = vidaActual;
        this.vidaMaxima = vidaMaxima;
        this.defensa = defensa;
        this.turnosEscudoRestantes = turnosEscudoRestantes;
        this.vivo = vivo;
    }

    public String getNombre() {
        return nombre;
    }

    public int getVidaActual() {
        return vidaActual;
    }

    public int getVidaMaxima() {
        return vidaMaxima;
    }

    public int getDefensa() {
        return defensa;
    }

    public int getTurnosEscudoRestantes() {
        return turnosEscudoRestantes;
    }

    public boolean estaVivo() {
        return vivo;
    }
}
