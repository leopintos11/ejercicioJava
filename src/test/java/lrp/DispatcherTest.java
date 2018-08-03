package lrp;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.RandomUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class DispatcherTest {
	
	static Dispatcher dispatcher;
	
	@Before
	public void crearDispatcher() throws InterruptedException {
		dispatcher = Dispatcher.getDispatcher();
	}
	
	@Test
	public void unLlamadoTest() throws InterruptedException {
		Llamada llamada = new Llamada(Llamada.DURACION_MINIMA);
		
		dispatcher.dispatchCall(llamada);
		
		Thread.sleep((Llamada.DURACION_MINIMA + 1 )*1000);
		Assert.assertEquals(EstadoLlamada.ATENDIDA, llamada.getEstado());
	}

	@Test
	public void enviarDiezLlamadasTest() throws InterruptedException {
		
		List<Llamada> llamadas = this.crearDiezLlamadasConTiempoAleatorio();
		
		for(Llamada llamada : llamadas)
			dispatcher.dispatchCall(llamada);
		
		Thread.sleep((Llamada.DURACION_MAXIMA + 1) * 1000);
		
		for(Llamada llamada : llamadas)
			Assert.assertEquals(EstadoLlamada.ATENDIDA, llamada.getEstado());
	}
	
	private List<Llamada> crearDiezLlamadasConTiempoAleatorio() {
		List<Llamada> llamadas = new ArrayList<>();
		
		for(int i = 0; i < Dispatcher.CANTIDAD_TOTAL_ATENDEDORES; i++) {
			llamadas.add(new Llamada(RandomUtils.nextInt(Llamada.DURACION_MINIMA, Llamada.DURACION_MAXIMA)));
		}
		return llamadas;
	}
	
	@Test
	public void masDeDiezLlamadasConcurrentes() throws InterruptedException {
		for(int i = 0; i < Dispatcher.CANTIDAD_TOTAL_ATENDEDORES; i++) {
			dispatcher.dispatchCall(new Llamada(Llamada.DURACION_MINIMA));
		}
		Llamada llamada11 = new Llamada(Llamada.DURACION_MINIMA);
		dispatcher.dispatchCall(llamada11);
		
		Thread.sleep((Llamada.DURACION_MINIMA + 1) * 1000);
		Assert.assertEquals(EstadoLlamada.RECHAZADA, llamada11.getEstado());
	}
	
	@Test
	public void llamadaConCallcenterCerrado() throws InterruptedException {
		Dispatcher dispatcher = Dispatcher.getDispatcher();
		dispatcher.dispatchCall(new Llamada(Llamada.DURACION_MINIMA));
		dispatcher.cerrarCallcenter();
		
		Llamada llamada = new Llamada(Llamada.DURACION_MINIMA);
		dispatcher.dispatchCall(llamada);
		Assert.assertEquals(EstadoLlamada.RECHAZADA, llamada.getEstado());
	}
	
	@Test
	public void enviarLlamadasConcurrentementePorVeinteSegundos() throws InterruptedException {
		EnviadorDeLlamadas enviador1 = new EnviadorDeLlamadas(dispatcher);
		EnviadorDeLlamadas enviador2 = new EnviadorDeLlamadas(dispatcher);
		
		Thread hilo1 = new Thread(enviador1);
		hilo1.start();
		Thread hilo2 = new Thread(enviador2);
		hilo2.start();
		
		Thread.sleep(20 * 1000);
		
		enviador1.setEjecutar(false);
		enviador2.setEjecutar(false);
		hilo1.join();
		hilo2.join();
		
		/* Espero un tiempo prudencial porque pueden haber llamadas en curso aún */
		Thread.sleep((Llamada.DURACION_MAXIMA + 1) * 1000);
		
		List<Llamada> llamadas = enviador1.getLlamadasHechas();
		llamadas.addAll(enviador2.getLlamadasHechas());
		for(Llamada llamada : llamadas) {
			Assert.assertEquals(EstadoLlamada.ATENDIDA, llamada.getEstado());
		}
	}
	
	class EnviadorDeLlamadas implements Runnable {
		
		private Dispatcher dispatcher;
		private List<Llamada> llamadasHechas;
		private boolean ejecutar;
		
		EnviadorDeLlamadas(Dispatcher dispatcher) {
			this.dispatcher = dispatcher;
			this.llamadasHechas = new ArrayList<>();
			this.ejecutar = true;
		}

		@Override
		public void run() {
			while(ejecutar) {
				Llamada llamada = new Llamada(RandomUtils.nextInt(Llamada.DURACION_MINIMA, Llamada.DURACION_MAXIMA));
				this.llamadasHechas.add(llamada);
				this.dispatcher.dispatchCall(llamada);
				try {
					Thread.sleep(RandomUtils.nextInt(3, 5) * 1000); //Espero un intervalo aleatorio arbitrario
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
		public List<Llamada> getLlamadasHechas() {
			return llamadasHechas;
		}

		public void setEjecutar(boolean ejecutar) {
			this.ejecutar = ejecutar;
		}
	}
	
	@After
	public void cerrarCallcenter() throws InterruptedException {
		dispatcher.cerrarCallcenter();
	}

}
