package lrp;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;

public class Dispatcher {
	
	public static int CANTIDAD_OPERADORES = 8;
	public static int CANTIDAD_SUPERVISORES = 1;
	public static int CANTIDAD_DIRECTORES = 1;
	public static int CANTIDAD_TOTAL_ATENDEDORES = CANTIDAD_OPERADORES + CANTIDAD_SUPERVISORES + CANTIDAD_DIRECTORES;
	ExecutorService atencionesThread;
	PriorityBlockingQueue<Atendedor> atendedoresLibres = new PriorityBlockingQueue<>(CANTIDAD_TOTAL_ATENDEDORES);
	private boolean callcenterAbierto;
	private long contadorLlamadas = 1;
	private static Dispatcher instancia;
	
	private Dispatcher() throws InterruptedException {
		abrirCallcenter();
	}
	
	public static synchronized Dispatcher getDispatcher() throws InterruptedException {
		if(instancia == null)
			instancia = new Dispatcher();
		if(!instancia.callcenterAbierto)
			instancia.abrirCallcenter();
		return instancia;
	}
	
	private void abrirCallcenter() throws InterruptedException {
		crearEmpleados();
		this.atencionesThread = Executors.newFixedThreadPool(CANTIDAD_TOTAL_ATENDEDORES);
		this.callcenterAbierto = true;
	}

	private void crearEmpleados() {
		for(int i = 0; i < CANTIDAD_OPERADORES; i++)
			atendedoresLibres.put(Empleado.crearOperador());
		atendedoresLibres.put(Empleado.crearSupervisor());
		atendedoresLibres.put(Empleado.crearDirector());
	}
	
	public synchronized void dispatchCall(Llamada llamada) {
		if(callcenterAbierto) {
			llamada.setNumero(this.contadorLlamadas++);
			LoggerFactory.getLogger().info("Despachando llamada " + llamada.getNumero());
			Empleado atendedor = (Empleado) getAtendedor();
			if(atendedor != null) {
				atenderLlamada(llamada, atendedor);
			} else {
				rechazarPorOperadoresOcupados(llamada);
			}
		} else {
			rechazarPorCallcenterCerrado(llamada);
		}
	}

	private void rechazarPorCallcenterCerrado(Llamada llamada) {
		LoggerFactory.getLogger().info("Callcenter fuera de horario de atención.");
		llamada.rechazar();
	}

	private void rechazarPorOperadoresOcupados(Llamada llamada) {
		LoggerFactory.getLogger().info("Todos los operadores ocupados, intente más tarde.");
		llamada.rechazar();
	}

	private void atenderLlamada(Llamada llamada, Empleado atendedor) {
		LoggerFactory.getLogger().info("Dispatcher seleccionó atendedor " + atendedor.getPrioridad());
		atencionesThread.execute(Atencion.crearNuevaAtencion(atendedor, llamada, this));
	}

	private Atendedor getAtendedor() {
		return this.atendedoresLibres.poll();
	}
	
	protected synchronized void desocuparAtendedor(Atendedor atendedor) {
		this.atendedoresLibres.offer(atendedor);
	}
	
	public synchronized void cerrarCallcenter() throws InterruptedException {
		if(!this.callcenterAbierto)
			return;
		LoggerFactory.getLogger().info("Cerrando callcenter");
		this.callcenterAbierto = false;
		this.atencionesThread.shutdown();
		this.atencionesThread.awaitTermination(10, TimeUnit.SECONDS);
		instancia = null;
		LoggerFactory.getLogger().info("Callcenter cerrado");
	}

}
