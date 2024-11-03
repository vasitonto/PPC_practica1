package servidor;
import java.io.*;
import java.net.*;

class Server
{
	public static void main( String args[])
	{
		ServerSocket s = null;
		Socket cliente = null;
		
		try {
			s = new ServerSocket (80);
		} catch (IOException e) { e.printStackTrace (); }
		
		while (true)
		{
			try
			{
				cliente = s.accept();
				new GestorPeticion(cliente).start();
			} catch (IOException e) { e.printStackTrace (); }
		}
	}
}

