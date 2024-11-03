/**
 * Created on 06-oct-2021
 *
 * @author Humberto Martinez Barbera
 */
package servidor;

import java.io.*;
import java.net.*;
import javax.net.ssl.*;
import java.security.*;

public class SSLServer 
{
	static public final boolean			DEBUG		= true;

	static public final String			SV_STORE	= "certs/serverks.jks";
	static public final String			SV_PWD		= "serverppc";
	static public final String			SV_CERT_PWD	= "serverppc";
	static public final String			CA_STORE	= "certs/caks.jks";
	static public final String			CA_PWD		= "practicasppc";

	static public void instanceEchoServerAnon (int port)
	{
		SSLContext				ctx;
		SSLServerSocketFactory	fac; 
		SSLServerSocket			s; 
		Socket					s1;
		
		try 
		{
//			ctx = SSLContext.getInstance ("SSLv3");
			ctx = SSLContext.getInstance ("TLSv1.2");
			ctx.init (null, null, null);
			fac	= ctx.getServerSocketFactory (); 
			s 	= (SSLServerSocket) fac.createServerSocket (port);
			
			if (DEBUG)
			{
				String[] supported = s.getSupportedCipherSuites();
				System.out.println ("*** AUTHENTICATED");			
				System.out.println ("    =============");			
				for (String str : supported)
					if (str.indexOf ("anon") < 0)
						System.out.println (str);			
				System.out.println ();			
				System.out.println ("*** ANONYMOUS");			
				System.out.println ("    =========");			
				for (String str : supported)
					if (str.indexOf ("anon") > 0)
						System.out.println (str);			
				System.out.println ();			
			}
//			s.setEnabledCipherSuites (new String[] {"SSL_DH_anon_WITH_DES_CBC_SHA"});
			s.setEnabledCipherSuites (new String[] {"TLS_DH_anon_WITH_AES_128_CBC_SHA"});
			
			while (s != null)
			{
				System.out.println ("SERVER WAITING");
				s1 = s.accept();
				serverEcho (s1);
			}
		
		} catch (Exception e) { e.printStackTrace(); }		
	}
	
	static public void instanceEchoServerCert (int port)
	{		
		SSLContext				ctx;
		SSLServerSocketFactory	fac; 
		SSLServerSocket			s; 
		Socket					s1;
		KeyStore 				ksm, kst;
		KeyManagerFactory 		kmf;
		TrustManagerFactory 	tmf;		
		
		try {
						
			ksm = KeyStore.getInstance("JKS");
			ksm.load(new FileInputStream(SV_STORE), SV_PWD.toCharArray());
			kmf = KeyManagerFactory.getInstance("SunX509");
			kmf.init(ksm, SV_CERT_PWD.toCharArray());
			
			kst = KeyStore.getInstance("JKS");
			kst.load(new FileInputStream(CA_STORE), CA_PWD.toCharArray());
			tmf = TrustManagerFactory.getInstance("SunX509");
			tmf.init(kst);

			ctx = SSLContext.getInstance("TLSv1.2");
			ctx.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
			fac = ctx.getServerSocketFactory();
			s =  (SSLServerSocket) fac.createServerSocket(port);
			s.setNeedClientAuth(true);
			
			while (s != null)
			{
				System.out.println ("SERVER WAITING");
				s1 = s.accept();
				serverEcho (s1);
			}
			
		} catch (Exception e) { e.printStackTrace(); }
	}

	static protected void serverEcho (Socket s)
	{
		BufferedWriter			os; 
		BufferedReader			is;
		String					echo;
		
		try 
		{
			is	= new BufferedReader (new InputStreamReader (s.getInputStream())); 
			os	= new BufferedWriter (new OutputStreamWriter (s.getOutputStream()));			
		
			System.out.println ("SERVER RECEIVING");
			echo = is.readLine ();
			System.out.println ("SERVER RECEIVED <"+echo+">");
			String test = echo+"\n";
			System.out.println ("SERVER ANSWERING");
			os.write (test, 0, test.length ());
			os.flush ();
			
			s.close ();
			System.out.println ("FINISHED");
		} catch (Exception e) { e.printStackTrace(); }		
	}

	static public void main (String[] args)
	{
//		SSLServer.instanceEchoServerAnon (4430);
		SSLServer.instanceEchoServerCert (4430);
	}
}
