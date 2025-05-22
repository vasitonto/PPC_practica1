package servidor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class GestorPeticiones extends Thread {

	private Socket s;
	
	public GestorPeticiones (Socket s )
	{
		this.s = s;
	}
	
	@Override
	public void run() {
			
		BufferedReader sIn;
		PrintWriter sOut;
		String texto;
		String respuesta;
		String cuerpoRespuesta;
		String cookies = "";
		String recurso = "";
		ServerHeaderFactory creadorCabeceras = new ServerHeaderFactory();
		HTMLResourceCreator creadorCuerpo = new HTMLResourceCreator();
		SetCookieHeaderGenerator creadorCookies = new SetCookieHeaderGenerator();
		try {
			sIn = new BufferedReader(new InputStreamReader(s.getInputStream()));
			sOut = new PrintWriter(s.getOutputStream());
			
			while(!s.isClosed()) {
				
				recurso = "";
		        cookies = "";
		        cuerpoRespuesta = "";
		        respuesta="";
		        
		        while ((texto = sIn.readLine()) != null && !texto.isEmpty()) {	
					//aqui voy a procesar la primera línea de la peticion
		        	if(texto.contains("exit")) break;
					if(texto.contains("GET") || texto.contains("POST") || texto.contains("HEAD")) {
						recurso = texto.split(" ")[1];
					}
					//else enviar cabecera de error
					
					if(texto.contains("Cookie:")) {
						cookies += creadorCookies.generarCookies(recurso, texto.substring(8));
					}
					respuesta += texto + "\r\n";
				}
		        System.out.print("Se ha recibido:\r\n" + respuesta);
				
				//compruebo si el string de cookies está vacío. Si lo está es que es la primera conexión.
				if(cookies.isEmpty()) cookies = creadorCookies.generarCookiesNuevas(recurso);
				
				//preparo la respuesta con cabeceras y un HTML que devuelve el recurso pedido
				cuerpoRespuesta = creadorCuerpo.creaHTML(recurso);
				respuesta = creadorCabeceras.generarCabeceras("text/html", cuerpoRespuesta.length(), recurso) + cookies + "\r\n\r\n" + cuerpoRespuesta + "\r\n\r\n";
				System.out.print("\nSe enviará: \n"+ respuesta);
				sOut.print(respuesta);
				sOut.flush();
			}
			sIn.close();
			sOut.close();
			s.close();
		} catch (java.net.SocketException sockex) {
			System.out.println("Conexión terminada con el usuario anterior. Esperando nueva conexión...");
		} catch (IOException e) { e.printStackTrace (); }
	}
}
