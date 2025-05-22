package servidor;
import java.io.*;
import java.net.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class HTTPListener extends Thread {
	
	public static final int PORT = 8000;
	
	public void run(){
		
		ServerSocket s = null;
		Socket cliente = null;
		
		try {
			s = new ServerSocket (PORT);
		} catch (IOException e) { e.printStackTrace (); }
		
		while (true)
		{
			try
			{
				cliente = s.accept();
				new GestorPeticiones(cliente).start();
			} catch (IOException e) { e.printStackTrace (); }
		}
	}
}

