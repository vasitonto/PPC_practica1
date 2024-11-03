package cliente;
import java.io.*;
import java.net.*;
import java.util.concurrent.atomic.AtomicInteger;

class Client {
private final static int PORT = 80;
	
	/**
	 * @param args
	 */
	public static void main( String args[] ){
		Socket socCli;
		BufferedReader userReader = new BufferedReader(new InputStreamReader(System.in));;
		BufferedReader sIn;
		DataOutputStream sOut;
		HeaderFactory genCabeceras = new HeaderFactory();
		boolean cont = true;
		boolean esCuerpo = false;
//		int contentRead = 0;
		int contentLen = 0;
		String cookies = "";
		char[] cuerpo = new char[0];
		
		try {
			//inicializo el socket y los buffers IO
			socCli = new Socket ("localhost", PORT);
			sIn = new BufferedReader(new InputStreamReader(socCli.getInputStream()));
			sOut = new DataOutputStream(socCli.getOutputStream());
			
			while (cont) {
				System.out.print("Introduce la dirección del recurso, o escribe \"exit\" para salir: ");
				String recurso = userReader.readLine();
				System.out.println(recurso);
				if (recurso.equals("exit"))cont = false;
				String packet = genCabeceras.generaPeticion("GET", recurso, cookies);//genero un paquete sin contenido
				System.out.print("se enviará:\n" + packet);
				sOut.writeBytes(packet); // Envío datos sin contenido
				cookies = "";
				
				String textoDevuelto;
				while((textoDevuelto = sIn.readLine()) != null && !textoDevuelto.isEmpty()){//.length() != 0) {
					System.out.println(textoDevuelto); // Recibo la respuesta
					if (textoDevuelto.contains("Content-length")) { //almaceno content-length para la funcion read
						contentLen = Integer.valueOf(textoDevuelto.split(": ")[1]);
						cuerpo = new char[contentLen + 10];
					}
					
					if (textoDevuelto.contains("Set-Cookie")) { 
						// por cada cabecera que contenga set-cookie la añadimos al string cookie
						cookies += genCabeceras.procesaCookie(textoDevuelto);
					}
				}

				// ahora leo el cuerpo del mensaje gracias a la cabecera Content-Length
				sIn.read(cuerpo, 0, contentLen+10);
				System.out.print(new String(cuerpo) + "\r\n");

//			
			}
			
			sIn.close();
			sOut.close();
			socCli.close();
		} catch (IOException e) { e.printStackTrace (); }
	}
}
