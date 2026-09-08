package RPG;

public abstract class Personaje {
    private String nombre;
    private int vida;
    private int mana;
    private int nivel;
    private int ataque;
    private int defensa;

    public Personaje(String nombre, int vida, int mana, int ataque, int defensa){
        this.nombre = nombre;
        this.vida = vida;
        this.mana = mana;
        this.nivel = 1;
        this.ataque = ataque;
        this.defensa = defensa;
    }

    public int getNivel(){
        return nivel;
    }

    public void atacar(Enemigo objetivo){
        objetivo.recibirDanio(ataque * nivel);
    }

    public int recibirDanio(int danio){
        vida -= danio - defensa;
        return vida;
    }

    public void subirNivel(){
        nivel += 1;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public abstract void usarHabilidad(Enemigo objetivo);
}
