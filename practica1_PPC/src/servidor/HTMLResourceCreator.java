package servidor;

public class HTMLResourceCreator {

	public HTMLResourceCreator() {};
	
	public String creaHTML(String recurso) {
		String doc = "<!DOCTYPE html>\r\n<html>\r\n<body>\r\n"
				+ "<h2>Hola, Cliente :)</h2>\r\n"
				+ "<p>recurso pedido: " +  recurso + "</p>\r\n"
				+ "</body>\r\n</html>";
		return doc;
	};
}
