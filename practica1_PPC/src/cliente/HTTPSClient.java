package cliente;

import java.io.*;
import java.net.*;
import java.security.*;
import javax.net.ssl.*;

public class HTTPSClient 
{
	public static final int 			port 		= 9000;
	
	public static final String			KSTORE		= "certs/clientp12.p12";
	public static final String			KS_PWD		= "practicas";
	public static final String			CERT_PWD	= "practicas";

	public static void initUserAuth (String address)
	{		
		SSLContext				ctx;
		SSLSocketFactory		fac; 
		SSLSocket				s; 
		KeyStore 				ks;
		KeyManagerFactory 		kmf;
		TrustManagerFactory 	tmf;
		
		
		
		try {
			// creo las KeyStore y TrustStore para importar los certificados
			ks = KeyStore.getInstance("PKCS12");
			ks.load(new FileInputStream(KSTORE), KS_PWD.toCharArray());
			
			kmf = KeyManagerFactory.getInstance("SunX509");
			kmf.init(ks, CERT_PWD.toCharArray());
				
			tmf = TrustManagerFactory.getInstance("SunX509");
			tmf.init(ks);

			// Creo un contexto SSL para crear el socketSSL
			ctx = SSLContext.getInstance("TLS");
			ctx.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
			fac = ctx.getSocketFactory();
			s =  (SSLSocket) fac.createSocket (address, port);
			s.startHandshake();
			lanzaCliente(s);
			
		} catch (Exception e) { e.printStackTrace(); }
	}

	static protected void lanzaCliente(Socket s)
	{
		BufferedWriter			os; 
		BufferedReader			is;
		
		BufferedReader userReader = new BufferedReader(new InputStreamReader(System.in));
		HeaderFactory genCabeceras = new HeaderFactory();
		boolean cont = true;
		int contentLen = 0;
		String cookies = "";
		char[] cuerpo = new char[0];
		
		try 
		{
			is	= new BufferedReader (new InputStreamReader (s.getInputStream())); 
			os	= new BufferedWriter (new OutputStreamWriter (s.getOutputStream()));			
			
			while (cont) {
				System.out.print("Introduce la dirección del recurso, o escribe \"exit\" para salir: ");
				String recurso = userReader.readLine();
				System.out.println(recurso);
				if (recurso.equals("exit"))cont = false;
				if (recurso.isEmpty()) continue;
				String packet = genCabeceras.generaPeticion("GET", recurso, cookies);//genero un paquete sin contenido
				System.out.print("se enviará:\n" + packet);
				os.write(packet); // Envío datos sin contenido
				os.flush();
				cookies = "";
				
				String textoDevuelto;
				while((textoDevuelto = is.readLine()) != null && !textoDevuelto.isEmpty()){//.length() != 0) {
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
				is.read(cuerpo, 0, contentLen+10);
				System.out.print(new String(cuerpo) + "\r\n");

			}
			
			is.close();
			os.close();
			s.close();
		} catch (IOException e) { e.printStackTrace (); }
	}

	static public void main (String[] args)
	{
		HTTPSClient.initUserAuth ("127.0.0.1");
	}
}
