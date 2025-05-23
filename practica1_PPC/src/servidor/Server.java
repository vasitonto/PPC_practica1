package servidor;

public class Server {
	
	public static void main(String[] args) {
		HTTPListener servidorHttp = new HTTPListener();
		HTTPSListener servidorHttps = new HTTPSListener();
		
		servidorHttp.start();
		servidorHttps.start();
	}
}

