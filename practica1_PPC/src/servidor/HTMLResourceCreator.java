package servidor;

public class HTMLResourceCreator {

	public HTMLResourceCreator() {};
	
	public String creaHTML(String recurso) {
		String doc = "<!DOCTYPE html>\n<html>\n<body>\n<p>recurso pedido: " +  recurso + "</p>\n</body>\n</html>";
		return doc;
	};
}
