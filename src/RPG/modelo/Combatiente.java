package RPG.modelo;

// Clase abstracta para todo el que pelea: Personaje y Enemigo heredan de acá.
// Agregamos un metodo que se llama perderVida para tener 2 vias de poder generar un daño a un personaje (recibirDanio y perderVida)
// que nos permita diferenciar si el daño viene por un ataque o por un envenamiento por ejemplo.
public abstract class Combatiente {

    private String nombre;
    private int vidaMaxima;
    private int vidaActual;
    private int defensa;
    private int velocidad;


    private boolean aturdido;
    private int turnosVenenoRestantes;
    private int turnosEscudoRestantes;
    private boolean escudoRecienAplicado;

    public Combatiente(String nombre, int vidaMaxima, int defensa, int velocidad) {
        this.nombre = nombre;
        this.vidaMaxima = vidaMaxima;
        this.vidaActual = vidaMaxima;
        this.defensa = defensa;
        this.velocidad = velocidad;
        this.aturdido = false;
        this.turnosVenenoRestantes = 0;
        this.turnosEscudoRestantes = 0;
        this.escudoRecienAplicado = false;
    }

    // Personaje tiene ataque físico y mágico; Enemigo uno solo.
    // Cada subclase decide qué devolver.
    public abstract int getAtaque(boolean esMagico);

    public void recibirDanio(int danio) {
        perderVida(reducirPorEscudo(danio));
    }

    private void perderVida(int cantidadDeVidaParaPerder){
        vidaActual = vidaActual - cantidadDeVidaParaPerder;
        if (vidaActual < 0) {
            vidaActual = 0;
        }
    }

    public void recibirCuracion(int cantidad) {
        vidaActual = vidaActual + cantidad;
        if (vidaActual > vidaMaxima) {
            vidaActual = vidaMaxima;
        }
    }

    public boolean estaVivo() {
        return vidaActual > 0;
    }
    
    public void aplicarVeneno() {
        turnosVenenoRestantes = 3;
    }

    public String procesarVenenoAlFinDeTurno() {
        if (!estaVivo() || turnosVenenoRestantes <= 0) {
            return "";
        }

        int danioVeneno = reducirPorEscudo(Math.max(1, (int) Math.round(vidaMaxima * 0.05)));
        perderVida(danioVeneno);
        turnosVenenoRestantes--;
        return nombre + " sufre " + danioVeneno + " de daño por veneno.\n";
    }

    private int reducirPorEscudo(int danio) {
        return turnosEscudoRestantes > 0 ? danio / 2 : danio;
    }

    public void limpiarAturdimiento() {
        quitarAturdimiento();
    }

    // ---------- Estados ----------

    public void aturdir() {
        aturdido = true;
    }

    public boolean estaAturdido() {
        return aturdido;
    }

    public void quitarAturdimiento() {
        aturdido = false;
    }

    public void envenenar() {
        aplicarVeneno();
    }

    public boolean estaEnvenenado() {
        return turnosVenenoRestantes > 0;
    }

    public void aplicarEscudo(int turnos) {
        if (turnos <= 0) {
            throw new IllegalArgumentException("La duración del escudo debe ser positiva.");
        }
        turnosEscudoRestantes = turnos;
        escudoRecienAplicado = false;
    }

    public void omitirProximoDescuentoEscudo() {
        if (turnosEscudoRestantes > 0) {
            escudoRecienAplicado = true;
        }
    }

    public int getTurnosEscudoRestantes() {
        return turnosEscudoRestantes;
    }

    public void procesarEscudoAlFinDeTurno() {
        if (turnosEscudoRestantes <= 0) {
            return;
        }
        if (escudoRecienAplicado) {
            escudoRecienAplicado = false;
        } else {
            turnosEscudoRestantes--;
        }
    }
    protected void limpiarEstados() {
        aturdido = false;
        turnosVenenoRestantes = 0;
        turnosEscudoRestantes = 0;
        escudoRecienAplicado = false;
    }

    // ---------- Getters y setters ----------

    public String getNombre() {
        return nombre;
    }

    public int getVidaMaxima() {
        return vidaMaxima;
    }

    public int getVidaActual() {
        return vidaActual;
    }

    public int getDefensa() {
        return defensa;
    }

    public int getVelocidad() {
        return velocidad;
    }

    // protected: solo las subclases los usan (subir de nivel, escalar enemigos)
    protected void setVidaMaxima(int vidaMaxima) {
        this.vidaMaxima = vidaMaxima;
    }

    protected void setVidaActual(int vidaActual) {
        this.vidaActual = vidaActual;
    }

    protected void setDefensa(int defensa) {
        this.defensa = defensa;
    }

    protected void setVelocidad(int velocidad) {
        this.velocidad = velocidad;
    }
}
