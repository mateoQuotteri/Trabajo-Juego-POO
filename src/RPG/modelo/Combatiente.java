package RPG.modelo;

// Clase abstracta para todo el que pelea: Personaje y Enemigo heredan de acá.
// (Pedido de la devolución: un tipo común para tratarlos igual en combate.)
public abstract class Combatiente {

    private String nombre;
    private int vidaMaxima;
    private int vidaActual;
    private int defensa;
    private int velocidad;

    // Estados de alteración (documento 1.4.4)
    private boolean aturdido;
    private int turnosVenenoRestantes;

    public Combatiente(String nombre, int vidaMaxima, int defensa, int velocidad) {
        this.nombre = nombre;
        this.vidaMaxima = vidaMaxima;
        this.vidaActual = vidaMaxima;
        this.defensa = defensa;
        this.velocidad = velocidad;
        this.aturdido = false;
        this.turnosVenenoRestantes = 0;
    }

    // Personaje tiene ataque físico y mágico; Enemigo uno solo.
    // Cada subclase decide qué devolver.
    public abstract int getAtaque(boolean esMagico);

    public void recibirDanio(int danio) {
        vidaActual = vidaActual - danio;
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

    // ---------- Estados ----------

    public void aturdir() {
        aturdido = true;
    }

    public boolean estaAturdido() {
        return aturdido;
    }

    // TODO: usar este método al empezar el turno del combatiente (ver
    // ControladorDeBatalla). Si estaba aturdido, pierde el turno y
    // el aturdimiento se apaga (documento: "pierde exactamente 1 turno").
    public void quitarAturdimiento() {
        aturdido = false;
    }

    public void envenenar() {
        turnosVenenoRestantes = 3;
    }

    public boolean estaEnvenenado() {
        return turnosVenenoRestantes > 0;
    }

    // TODO: veneno. Documento: "inflige 5% de la vida máxima al FINAL del
    // turno de la víctima, durante 3 turnos".
    // Pista: si turnosVenenoRestantes > 0 -> recibirDanio(5% de vidaMaxima)
    // y restar 1 a turnosVenenoRestantes. Devolver un texto para mostrar
    // en el log de la pantalla (o "" si no pasó nada).
    public String aplicarVeneno() {
        return "";
    }

    protected void limpiarEstados() {
        aturdido = false;
        turnosVenenoRestantes = 0;
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
