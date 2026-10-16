package RPG.modelo;

// Lo que pasó en una batalla que ya terminó. Lo arma el controlador y lo
// muestra la Pantalla de Resumen de Batalla. Es solo un "paquete de datos":
// se crea una vez y no cambia (por eso no tiene setters).
public class ResultadoDeBatalla {

    private int piso;
    private String nombreEnemigo;
    private boolean ganoLaParty;
    private int experienciaGanada;
    private String subidasDeNivel;
    private int rondasJugadas;

    public ResultadoDeBatalla(int piso, String nombreEnemigo, boolean ganoLaParty,
                              int experienciaGanada, String subidasDeNivel, int rondasJugadas) {
        this.piso = piso;
        this.nombreEnemigo = nombreEnemigo;
        this.ganoLaParty = ganoLaParty;
        this.experienciaGanada = experienciaGanada;
        this.subidasDeNivel = subidasDeNivel;
        this.rondasJugadas = rondasJugadas;
    }

    public int getPiso() {
        return piso;
    }

    public String getNombreEnemigo() {
        return nombreEnemigo;
    }

    public boolean ganoLaParty() {
        return ganoLaParty;
    }

    public int getExperienciaGanada() {
        return experienciaGanada;
    }

    public String getSubidasDeNivel() {
        return subidasDeNivel;
    }

    public int getRondasJugadas() {
        return rondasJugadas;
    }
}
