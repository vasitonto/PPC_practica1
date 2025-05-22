package servidor;

import java.io.FileInputStream;
import java.net.Socket;
import java.security.KeyStore;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.TrustManagerFactory;

public class HTTPSListener extends Thread {
	
	public static final int PORT = 8443;
	
	static public final String			SV_STORE	= "certs/serverks.jks";
	static public final String			SV_PWD		= "serverppc";
	static public final String			SV_CERT_PWD	= "serverppc";
	static public final String			CA_STORE	= "certs/caks.jks";
	static public final String			CA_PWD		= "practicasppc";
	
	public HTTPSListener(){
		
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

			ctx = SSLContext.getInstance("TLS");
			ctx.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
			fac = ctx.getServerSocketFactory();
			s =  (SSLServerSocket) fac.createServerSocket(PORT);
			s.setNeedClientAuth(true);
			
			while (s != null)
			{
				s1 = s.accept();
				new GestorPeticiones(s1).start();
			}
		
		} catch (Exception e) { e.printStackTrace(); }		
}

	public void run() {
		
	}
}
