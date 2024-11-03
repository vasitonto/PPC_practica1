/**
 * Created on 06-oct-2021 
 *
 * @author Humberto Martinez Barbera
 */
package cliente;

import java.io.*;
import java.net.*;
import java.security.*;
import javax.net.ssl.*;

public class SSLClient 
{
	static public final String			KSTORE		= "certs/clientks.jks";
	static public final String			KS_PWD		= "clientppc";
	static public final String			CERT_PWD	= "clientppc";

	static public void instanceEchoClientAnon (String address, int port)
	{
		SSLContext				ctx;
		SSLSocketFactory		fac; 
		SSLSocket				s; 
		
		try 
		{
//			ctx = SSLContext.getInstance ("SSLv3");
			ctx = SSLContext.getInstance ("TLSv1.2");
			ctx.init (null, null, null);
			fac	= ctx.getSocketFactory (); 
			s 	= (SSLSocket) fac.createSocket (address, port);
//			s.setEnabledCipherSuites (new String[] {"SSL_DH_anon_WITH_DES_CBC_SHA"});
			s.setEnabledCipherSuites (new String[] {"TLS_DH_anon_WITH_AES_128_CBC_SHA"});
			s.setNeedClientAuth (false);
			
			clientEcho (s);
		} catch (Exception e) { e.printStackTrace(); }		
	}

	static public void instanceEchoClientCert (String address, int port)
	{		
		SSLContext				ctx;
		SSLSocketFactory		fac; 
		SSLSocket				s; 
		KeyStore 				ks;
		KeyManagerFactory 		kmf;
		TrustManagerFactory 	tmf;		
		
		try {
						
			ks = KeyStore.getInstance("JKS");
			ks.load(new FileInputStream(KSTORE), KS_PWD.toCharArray());
			
			kmf = KeyManagerFactory.getInstance("SunX509");
			kmf.init(ks, CERT_PWD.toCharArray());
				
			tmf = TrustManagerFactory.getInstance("SunX509");
			tmf.init(ks);

			ctx = SSLContext.getInstance("TLSv1.2");
			ctx.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
			fac = ctx.getSocketFactory();
			s =  (SSLSocket) fac.createSocket (address, port);
			
			clientEcho (s);
			
		} catch (Exception e) { e.printStackTrace(); }
	}

	static protected void clientEcho (Socket s)
	{
		BufferedWriter			os; 
		BufferedReader			is;
		String					echo;
		
		try 
		{
			is	= new BufferedReader (new InputStreamReader (s.getInputStream())); 
			os	= new BufferedWriter (new OutputStreamWriter (s.getOutputStream()));			
			
			String test = "Hola Mundo\n\n\n";
			System.out.println ("CLIENT SENDING");
			os.write (test, 0, test.length ());
			os.flush ();
			System.out.println ("CLIENT RECEIVING");
			echo = is.readLine ();
			
			System.out.println ("ECHO <"+echo+">");
			s.close ();
		} catch (Exception e) { e.printStackTrace(); }		
	}

	static public void main (String[] args)
	{
//		SSLClient.instanceEchoClientAnon ("127.0.0.1", 4430);
		SSLClient.instanceEchoClientCert ("127.0.0.1", 4430);
	}
}
