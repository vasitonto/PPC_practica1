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
	@SuppressWarnings("deprecation")
	public void run()
	{
		BufferedReader sIn;
		PrintWriter sOut;
//		String peticion = "";
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
			sOut = new PrintWriter(s.getOutputStream(), true);
			
			while(!s.isClosed()) {
				
				recurso = "";
		        cookies = "";
		        cuerpoRespuesta = "";
		        
		        while ((texto = sIn.readLine()) != null && !texto.isEmpty()) {	
					//aqui voy a procesar la primera línea de la peticion
					if(texto.contains("GET") || texto.contains("POST")) {
						recurso = texto.split(" ")[1];
					}
					
					if(texto.contains("Cookie:")) {
						cookies += creadorCookies.generarCookies(recurso, texto.substring(8));
					}
					System.out.println(texto);
				}
				
				//compruebo si el string de cookies está vacío. Si lo está es que es la primera conexión.
				if(cookies.isEmpty()) cookies = creadorCookies.generarCookiesNuevas(recurso);
				
				//preparo la respuesta con cabeceras y un HTML que devuelve el recurso pedido
				cuerpoRespuesta = creadorCuerpo.creaHTML(recurso);
				respuesta = creadorCabeceras.generarCabeceras("text/html", cuerpoRespuesta.length(), recurso) + cookies + "\r\n\r\n" + cuerpoRespuesta + "\r\n\r\n";
				System.out.print("\nAquí está la respuesta que se va a enviar: \n"+ respuesta + "\n");
				sOut.print(respuesta.toString());
				sOut.flush();
				continue;
			}
			sIn.close();
			sOut.close();
			s.close();
		//} catch (SocketException sockex) {
			//System.out.println("Conexión terminada con el usuario anterior. Esperando nueva conexión...");
		} catch (IOException e) { e.printStackTrace (); }
	}
	/*
	 * private String gestionarCookies() {
	 * 
	 * return String;
		};
	 */
		
		
}

