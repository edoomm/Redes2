
public class Cronometro {
    private long tiempoInicio;
    private long tiempoFinal;
    
    public Cronometro () {
        tiempoInicio = 0;
        tiempoFinal = 0;
    }
    
    public void iniciar () {
        tiempoInicio = System.currentTimeMillis();
    }
    
    public void detener () {
        tiempoFinal = System.currentTimeMillis();
    }
    
    public void reiniciar () {
        tiempoInicio = 0;
        tiempoFinal = 0;
    }
    
    public long getSegundosTranscurridos () {
        return (tiempoFinal - tiempoInicio) / 1000;
    }
    
    public long getMinutosTranscurridos () {
        return (tiempoFinal - tiempoInicio) / (1000 * 60);
    }
    
    public long getHorasTranscurridas () {
        return (tiempoFinal - tiempoInicio) / (1000 * 60 * 60);
    }
}
