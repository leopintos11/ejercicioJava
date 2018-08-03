package lrp;

public class Empleado implements Atendedor, Comparable<Empleado> {

	private Prioridad prioridad;
	
	private Empleado(Prioridad prioridad) {
		this.prioridad = prioridad;
	}
	
	public Prioridad getPrioridad() {
		return this.prioridad;
	}
	

	@Override
	public void atender(Llamada llamada) {
		logAtendiendoLlamada(llamada);
		llamada.atender();
		try {
			Thread.sleep(llamada.getTiempo() * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		llamada.finalizar();
		logFinLlamada(llamada);
	}

	private void logFinLlamada(Llamada llamada) {
		LoggerFactory.getLogger().info("Fin de llamada " + llamada.getNumero());
	}

	private void logAtendiendoLlamada(Llamada llamada) {
		LoggerFactory.getLogger().info("Atendiendo llamada " + llamada.getNumero() + " - " + llamada.getTiempo() + " segundos.");
	}

	@Override
	public int compareTo(Empleado o) {
		return this.getPrioridad().compareTo(o.getPrioridad());
	}
	
	public static Empleado crearOperador() {
		return new Empleado(Prioridad.OPERADOR);
	}
	
	public static Empleado crearSupervisor() {
		return new Empleado(Prioridad.SUPERVISOR);
	}
	
	public static Empleado crearDirector() {
		return new Empleado(Prioridad.DIRECTOR);
	}

}
