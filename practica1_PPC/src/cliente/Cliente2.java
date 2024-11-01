package cliente;

import java.io.*;
import java.net.Socket;

public class Cliente2 {
	public static void main(String[] args) {
		String servidor = "localhost"; // IP del servidor
		int puerto = 9999; // Puerto del servidor
		String cookie = null; // Almacena la cookie devuelta por el servidor

		for (int i = 1; i <= 3; i++) {
			try (Socket socket = new Socket(servidor, puerto);
					PrintWriter sOut = new PrintWriter(socket.getOutputStream(), true);
					BufferedReader sIn = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

				// Construir la solicitud HTTP, añadiendo la cabecera "Cookie" solo si es la
				// segunda o tercera petición
				StringBuilder solicitudHTTP = new StringBuilder();
				solicitudHTTP.append("GET /prueba").append(i).append(".html HTTP/1.1\r\n"); // Cambia el recurso en cada
																							// iteración
				solicitudHTTP.append("Host: localhost\r\n");
				if (cookie != null) {
					solicitudHTTP.append("Cookie: ").append(cookie).append("\r\n");
				}
				solicitudHTTP.append("Connection: close\r\n\r\n");

				// Enviar la solicitud al servidor
				System.out.println("-----SOLICITUD " + i + " -----");
				System.out.println(solicitudHTTP);
				sOut.println(solicitudHTTP);

				// Leer la respuesta del servidor
				String respuesta;
				System.out.println("-----RESPUESTA " + i + " -----");
				while ((respuesta = sIn.readLine()) != null) {
					System.out.println(respuesta);

					// Si encontramos el encabezado Set-Cookie, almacenamos el nuevo valor de la
					// cookie
					if (respuesta.startsWith("Set-Cookie:")) {
						cookie = respuesta.substring(12).split(";", 2)[0]; // Guardamos solo el valor de la cookie
						System.out.println("Cookie actualizada: " + cookie);
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
