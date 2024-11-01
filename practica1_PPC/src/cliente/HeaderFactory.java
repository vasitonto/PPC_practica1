package cliente;

public class HeaderFactory {
	//private String met;
	//private String res;
	private final static String VER= "HTTP/1.1";
	
	public String generaPeticion(String met, String res, String cookie) {
		String peticion = met + " " + res +" "+ VER +" "+ "\r\n";
		peticion += "Accept: *.html\r\nAccept: *.txt\r\n";
		peticion += "Host: localhost\r\n";
		peticion += "Connection: keep-alive\r\n";
		peticion += "User-Agent: java clase cliente\r\n";
		peticion += "Accept-language: es-en\r\n";
		if(!cookie.isEmpty()) peticion += "Cookie: " + cookie;
		peticion += "\r\n";
		return peticion;
	}
	
	public String procesaCookie(String setCookieStr) {
		String cookie = setCookieStr.substring(setCookieStr.indexOf("Recurso"));
		return cookie;
	}
	
	
	
}
