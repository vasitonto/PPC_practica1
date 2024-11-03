package servidor;
import java.io.*;
import java.net.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class GestorPeticion extends Thread
{
	Socket s;
	Pattern lineaMet = Pattern.compile("(\\w+)\\s(\\S+)\\s(HTTP\\/1\\.1)"); //([A-Z]+\\s)(\\S+\\s)(HTTP\\/1\\.1)\\s\\n
	
	
	public GestorPeticion (Socket s )
	{
		this.s = s;
	}
	public void run()
	{
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

