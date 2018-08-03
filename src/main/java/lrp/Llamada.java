package lrp;

public class Llamada {
	
	public static int DURACION_MINIMA = 5;
	public static int DURACION_MAXIMA = 10;
	
	int tiempo;
	protected Enum<EstadoLlamada> estado;
	long numero;
	
	Llamada(int tiempo) {
		this.tiempo = tiempo;
		this.estado = EstadoLlamada.LLAMANDO;
	}

	public long getTiempo() {
		return tiempo;
	}

	public void finalizar() {
		this.estado = EstadoLlamada.ATENDIDA;
	}
	
	public void atender() {
		this.estado = EstadoLlamada.ATENDIENDO;
	}
	
	public void rechazar() {
		this.estado = EstadoLlamada.RECHAZADA;
	}

	public Enum<EstadoLlamada> getEstado() {
		return this.estado;
	}

	public void setNumero(long numero) {
		this.numero = numero;
	}
	
	public long getNumero() {
		return this.numero;
	}

}
