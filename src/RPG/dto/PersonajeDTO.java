package RPG.dto;

public final class PersonajeDTO extends CombatienteDTO {

    private final int indice;
    private final String clase;
    private final int nivel;
    private final int manaActual;
    private final int manaMaximo;
    private final int ataqueFisico;
    private final int ataqueMagico;
    private final int velocidad;
    private final int experiencia;
    private final String arma;
    private final String armadura;
    private final boolean puedeDefender;
    private final String habilidadBasica;
    private final String habilidadPoderosa;
    private final boolean habilidadBasicaDisponible;
    private final boolean habilidadPoderosaDisponible;

    public PersonajeDTO(int indice, String nombre, String clase, int nivel,
                        int vidaActual, int vidaMaxima, int manaActual, int manaMaximo,
                        int ataqueFisico, int ataqueMagico, int defensa, int velocidad,
                        int experiencia, int turnosEscudoRestantes, boolean vivo,
                        String arma, String armadura, boolean puedeDefender,
                        String habilidadBasica, String habilidadPoderosa,
                        boolean habilidadBasicaDisponible, boolean habilidadPoderosaDisponible) {
        super(nombre, vidaActual, vidaMaxima, defensa, turnosEscudoRestantes, vivo);
        this.indice = indice;
        this.clase = clase;
        this.nivel = nivel;
        this.manaActual = manaActual;
        this.manaMaximo = manaMaximo;
        this.ataqueFisico = ataqueFisico;
        this.ataqueMagico = ataqueMagico;
        this.velocidad = velocidad;
        this.experiencia = experiencia;
        this.arma = arma;
        this.armadura = armadura;
        this.puedeDefender = puedeDefender;
        this.habilidadBasica = habilidadBasica;
        this.habilidadPoderosa = habilidadPoderosa;
        this.habilidadBasicaDisponible = habilidadBasicaDisponible;
        this.habilidadPoderosaDisponible = habilidadPoderosaDisponible;
    }

    public int getIndice() {
        return indice;
    }

    public String getClase() {
        return clase;
    }

    public int getNivel() {
        return nivel;
    }

    public int getManaActual() {
        return manaActual;
    }

    public int getManaMaximo() {
        return manaMaximo;
    }

    public int getAtaqueFisico() {
        return ataqueFisico;
    }

    public int getAtaqueMagico() {
        return ataqueMagico;
    }

    public int getVelocidad() {
        return velocidad;
    }

    public int getExperiencia() {
        return experiencia;
    }

    public String getArma() {
        return arma;
    }

    public String getArmadura() {
        return armadura;
    }

    public boolean puedeDefender() {
        return puedeDefender;
    }

    public String getHabilidadBasica() {
        return habilidadBasica;
    }

    public String getHabilidadPoderosa() {
        return habilidadPoderosa;
    }

    public boolean isHabilidadBasicaDisponible() {
        return habilidadBasicaDisponible;
    }

    public boolean isHabilidadPoderosaDisponible() {
        return habilidadPoderosaDisponible;
    }
}
