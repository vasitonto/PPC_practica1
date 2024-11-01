package cliente;
import java.io.*;
import java.net.*;
import java.util.concurrent.atomic.AtomicInteger;

class Client {
private final static int PORT = 9999;
	
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
				if (recurso.equals("exit"))cont = false;
				String packet = genCabeceras.generaPeticion("GET", recurso, cookies);//genero un paquete sin contenido
				System.out.print("se enviará:\n" + packet);
				sOut.writeBytes(packet + "\r\n"); // Envío datos sin contenido
				
				String textoDevuelto;
//				while((textoDevuelto = sIn.readLine()) != null && !textoDevuelto.isEmpty()){//.length() != 0) {
				while((textoDevuelto = sIn.readLine()) != null){
					System.out.println(textoDevuelto); // Recibo la respuesta
					//contentRead += contentRead + textoDevuelto.length();
					if (textoDevuelto.contains("Content-length")) { 
						contentLen = Integer.valueOf(textoDevuelto.split(": ")[1]);
//						cuerpo = new char[contentLen + 600];
					}
					
					if (textoDevuelto.contains("Set-Cookie")) { // por cada cabecera que contenga set-cookie aumentamos el string cookie de la siguiente forma:
						cookies += genCabeceras.procesaCookie(textoDevuelto);
					}
				}

				// ahora leo el cuerpo del mensaje gracias a la cabecera Content-Length
//				int charLeidos = sIn.read(cuerpo, 0, contentLen+600);
//				System.out.println("el cuerpo es:\n"+ new String(cuerpo));

//			
			}
			
				sIn.close();
				sOut.close();
				socCli.close();
		} catch (IOException e) { e.printStackTrace (); }
	}
}
