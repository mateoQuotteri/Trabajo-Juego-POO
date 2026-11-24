package RPG.dto;

public final class ResumenBatallaDTO {

    private final String resultado;
    private final String detalle;
    private final boolean puedeContinuar;
    private final String experiencia;
    private final String recompensas;
    private final int oro;

    public ResumenBatallaDTO(String resultado, String detalle, boolean puedeContinuar,
                             String experiencia, String recompensas, int oro) {
        this.resultado = resultado;
        this.detalle = detalle;
        this.puedeContinuar = puedeContinuar;
        this.experiencia = experiencia;
        this.recompensas = recompensas;
        this.oro = oro;
    }

    public String getResultado() {
        return resultado;
    }

    public String getDetalle() {
        return detalle;
    }

    public boolean puedeContinuar() {
        return puedeContinuar;
    }

    public String getExperiencia() {
        return experiencia;
    }

    public String getRecompensas() {
        return recompensas;
    }

    public int getOro() {
        return oro;
    }
}
