package cliente;

public class HeaderFactory {
	//private String met;
	//private String res;
	private final static String VER= "HTTP/1.1";
	private final static String SALTO = "\r\n";
	
	public String generaPeticion(String met, String res, String cookie) {
		String peticion = met + " " + res +" "+ VER + SALTO;
		peticion += "Accept: *.html" + SALTO + "Accept: *.txt" + SALTO;
		peticion += "Host: localhost" + SALTO;
		peticion += "Connection: keep-alive" + SALTO;
		peticion += "User-Agent: java clase cliente" + SALTO;
		peticion += "Accept-language: es-en" + SALTO;
		if(!cookie.isEmpty()) peticion += "Cookie: " + cookie + SALTO;
		peticion += SALTO;
		return peticion;
	}
	
	public String procesaCookie(String setCookieStr) {
		String cookie = setCookieStr.substring(setCookieStr.indexOf("Recurso"));
		return cookie;
	}
	
	
	
}
