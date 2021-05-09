
package hr.algebra.utils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import hr.algebra.model.Player;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;


public class DOMUtils {

    private static final String FILENAME_PLAYERS_MOVES = "players.xml";

    public static void savePlayers(List<Player> players) {

        try {
            Document document = createDocument("players");
            players.forEach(c -> document.getDocumentElement().appendChild(createPlayerElement(c, document)));
            saveDocument(document, FILENAME_PLAYERS_MOVES);
        } catch (ParserConfigurationException | TransformerException e) {
            Logger.getLogger(DOMUtils.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    private static Element createPlayerElement(Player player, Document document) throws DOMException {
        Element element = document.createElement("player");
        element.setAttributeNode(createAttribute(document, "id", String.valueOf(player.getId())));
        element.appendChild(createElement(document, "name", player.getName()));
        element.appendChild(createElement(document, "points", String.valueOf(player.getPoints())));
        element.appendChild(createElement(document, "challenge", player.getChallenge()));
        element.appendChild(createElement(document, "game", player.getGame()));
        return element;
    }

    private static Document createDocument(String root) throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        DOMImplementation domImplementation = builder.getDOMImplementation();
        return domImplementation.createDocument(null, root, null);
    }

    private static Attr createAttribute(Document document, String name, String value) {
        Attr attr = document.createAttribute(name);
        attr.setValue(value);
        return attr;
    }

    private static Node createElement(Document document, String tagName, String data) {
        Element element = document.createElement(tagName);
        Text text = document.createTextNode(data);
        element.appendChild(text);
        return element;
    }

    private static void saveDocument(Document document, String fileName) throws TransformerException {
        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = factory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        //transformer.transform(new DOMSource(document), new StreamResult(System.out));
        transformer.transform(new DOMSource(document), new StreamResult(new File(fileName)));
    }

    public static List<Player> loadPlayers() {
        List<Player> players = FXCollections.observableArrayList();
        try {
            Document document = createDocument(new File(FILENAME_PLAYERS_MOVES));
            NodeList nodes = document.getElementsByTagName("player");
            for (int i = 0; i < nodes.getLength(); i++) {
       
                players.add(processPlayerNode((Element) nodes.item(i)));
            }

        } catch (ParserConfigurationException | IOException | SAXException e) {
            Logger.getLogger(DOMUtils.class.getName()).log(Level.SEVERE, null, e);
        }
        return players;
    }

    private static Document createDocument(File file) throws SAXException, ParserConfigurationException, IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(file);
        return document;
    }

    private static Player processPlayerNode(Element element) {
        return new Player(
                Integer.valueOf(element.getAttribute("id")),
                element.getElementsByTagName("name").item(0).getTextContent(),
                Integer.valueOf(element.getElementsByTagName("points").item(0).getTextContent()),
                element.getElementsByTagName("challenge").item(0).getTextContent(),
                element.getElementsByTagName("game").item(0).getTextContent());
    }



}
