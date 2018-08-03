package lrp;

public class Atencion implements Runnable {

	Empleado atendedor;
	Llamada llamada;
	Dispatcher dispatcher;
	
	private Atencion(Atendedor atendedor, Llamada llamada, Dispatcher dispatcher) {
		this.atendedor = (Empleado) atendedor;
		this.llamada = llamada;
		this.dispatcher = dispatcher;
	}
	
	public static Atencion crearNuevaAtencion(Atendedor atendedor, Llamada llamada, Dispatcher dispatcher) {
		Atencion atencion = new Atencion(atendedor, llamada, dispatcher);
		return atencion;
	}

	@Override
	public void run() {
		atendedor.atender(llamada);
		this.dispatcher.desocuparAtendedor(atendedor);
	}

}
