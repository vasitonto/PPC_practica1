package cliente;

public class HeaderFactory {
	//private String met;
	//private String res;
	private final static String VER= "HTTP/1.1";
	
	public String generaPeticion(String met, String res, String cookie) {
		String peticion = met + " " + res +" "+ VER +" "+ "\n";
		peticion += "Accept: *.html\nAccept: *.txt\n";
		peticion += "Host: localhost\n";
		peticion += "Connection: keep-alive\n";
		peticion += "User-Agent: java clase cliente\n";
		peticion += "Accept-language: es-en\n";
		if(!cookie.isEmpty()) peticion += "Cookie: " + cookie;
		peticion += "\n";
		return peticion;
	}
	
	public String procesaCookie(String setCookieStr) {
		String cookie = setCookieStr.substring(setCookieStr.indexOf("Recurso"));
		return cookie;
	}
	
	
	
}
