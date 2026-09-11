package RPG.Personajes;

public abstract class Enemigo {
    private String nombre;
    private int vida;
    private int ataque;
    private int nivel;

    public Enemigo(String nombre, int vida, int ataque){
        this.nombre = nombre;
        this.vida = vida;
        this.ataque = ataque;
        this.nivel = 1;
    }

    // Este comentario solo sirve para hacer un commit
    public void recibirDanio(int danio){

        vida -= danio;
    }

    public void atacar(Personaje objetivo){
        objetivo.recibirDanio(ataque * nivel);
    }

    public int getNivel() {
        return nivel;
    }

    public int getVida() {
        return vida;
    }

    public abstract void usarHabilidad(Personaje objetivo);
}
