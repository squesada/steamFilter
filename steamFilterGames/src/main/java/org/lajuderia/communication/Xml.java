/*
 * The MIT License
 *
 * Copyright 2015 Sergio.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package org.lajuderia.communication;


import org.lajuderia.beans.MetacriticGame;
import org.lajuderia.beans.SteamGame;
import java.util.HashMap;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

/**
 *
 * @author Sergio
 */
public class Xml {
    public static HashMap<Integer,SteamGame> loadGamesFromDisk() throws Exception {        
        HashMap<Integer,SteamGame> gamesMap = new HashMap<Integer, SteamGame>();
        
        Document doc ;
            doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new java.io.File("steamgames.xml"));
            
            for ( int i = 0 ; i < doc.getDocumentElement().getChildNodes().getLength() ; i++){
                 SteamGame game;
                    game = readGameFromNamedNodeMap(doc.getDocumentElement().getChildNodes().item(i).getAttributes()) ;
                    gamesMap.put(game.getId(), game);
            }
            
            return ( gamesMap ) ;
    }
    
    public static void saveGamesToDisk(HashMap<Integer,SteamGame> gamesMap) throws Exception {
        Document xmlGames = DocumentBuilderFactory.newInstance().newDocumentBuilder().getDOMImplementation().createDocument(null, "steamgames", null);
            xmlGames.setXmlVersion("1.0");
            Element root = xmlGames.getDocumentElement();
            
            for (SteamGame game : gamesMap.values()){
                Element elGame = xmlGames.createElement("game");
                    saveGameToXmlElement(game, elGame);
                    root.appendChild(elGame);
            }
            
            Source source = new DOMSource(xmlGames);
                Result result = new StreamResult(new java.io.File("steamgames.xml")); //nombre del archivo
                    Transformer transformer = TransformerFactory.newInstance().newTransformer();
                        transformer.transform(source, result);
    }
    
    private static void saveGameToXmlElement(SteamGame game, Element element) {
        element.setAttribute("id", Integer.toString(game.getId()));
        element.setAttribute("name", game.getName());
        element.setAttribute("genre", game.getGenre());
        element.setAttribute("completed", Boolean.toString(game.isCompleted()));
        
        if ( game.hasMetaInformation() ) {
            element.setAttribute("metaname", game.getMetagame().getName());
            element.setAttribute("metadescription", game.getMetagame().getSummary());
            element.setAttribute("metagenre", game.getGenre());
            element.setAttribute("metascore", Integer.toString(game.getMetagame().getMetascore()));
            element.setAttribute("userscore", Integer.toString(game.getMetagame().getUserscore()));
        }
    }
    
    private static SteamGame readGameFromNamedNodeMap(NamedNodeMap nodeMap) {
        SteamGame game ;
            game = new SteamGame(
                Integer.parseInt(nodeMap.getNamedItem("id").getNodeValue()),
                nodeMap.getNamedItem("name").getNodeValue(),
                nodeMap.getNamedItem("genre").getNodeValue(),
                Boolean.parseBoolean(nodeMap.getNamedItem("completed").getNodeValue())
            );
            
            if ( nodeMap.getNamedItem("metaname") != null ) {
                game.setMetagame(new MetacriticGame(
                    nodeMap.getNamedItem("metaname").getNodeValue(),
                    nodeMap.getNamedItem("metadescription").getNodeValue(),
                    nodeMap.getNamedItem("metagenre").getNodeValue(),
                    Integer.parseInt(nodeMap.getNamedItem("metascore").getNodeValue()),
                    Integer.parseInt(nodeMap.getNamedItem("userscore").getNodeValue())
                ));
            }
            
            return ( game );
    }
}
