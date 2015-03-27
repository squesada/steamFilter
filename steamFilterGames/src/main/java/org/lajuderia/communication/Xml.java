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


import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import org.lajuderia.beans.MetaInformation;
import java.util.Iterator;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.lajuderia.beans.AbstractPlatformGame;
import org.lajuderia.beans.AbstractPlatformGame.PlatformGame;
import org.lajuderia.beans.Game;
import org.lajuderia.beans.PlatformGameFactory;
import org.lajuderia.exceptions.CustomException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.xml.sax.SAXException;

/**
 * 
 * @author Sergio
 */
public class Xml {
    public final static String TAG = "communication.Xml";
    
    /**
     * Loads the xml which contains the game list previously saved from disk
     * @return List of games
     * @throws org.lajuderia.exceptions.CustomException
     */
    public List<Game> loadGamesFromDisk() throws CustomException {        
        List<Game> gameList = new ArrayList<Game>();
        
        Document doc = null ;
            try {
                doc = getNewDocumentBuilder().parse(new java.io.File("steamgames.xml"));
            } catch ( IOException ex ) {
                throw (new CustomException(TAG, ex));
            } catch ( ParserConfigurationException ex ) {
                throw (new CustomException(TAG, ex));
            } catch ( SAXException ex ) {
                throw (new CustomException(TAG, ex));
            }
            
            for ( int i = 0 ; i < doc.getDocumentElement().getChildNodes().getLength() ; i++){
                 Game game;
                    game = readGameFromNamedNodeMap(doc.getDocumentElement().getChildNodes().item(i).getAttributes()) ;
                    gameList.add(game);
            }
            
            return ( gameList ) ;
    }

    /**
     * Saves the game list to disk (xml format)
     * @param gameList The game list
     * @throws org.lajuderia.exceptions.CustomException 
     */
    public void saveGamesToDisk(Collection<Game> gameList) throws CustomException {
        Document xmlGames = null ;
            try {
                xmlGames = getNewDocumentBuilder().getDOMImplementation().createDocument(null, "steamgames", null);
            } catch (ParserConfigurationException ex) {
                throw (new CustomException(TAG, ex));
            }
            xmlGames.setXmlVersion("1.0");
            Element root = xmlGames.getDocumentElement();
            
            Iterator<Game> it = gameList.iterator();
            
            while (it.hasNext()){
                Element elGame = xmlGames.createElement("game");
                    createElementFromGame(it.next(), elGame);
                    root.appendChild(elGame);
            }
            
            Source source = new DOMSource(xmlGames);
                Result result = new StreamResult(new java.io.File("steamgames.xml"));
                    Transformer transformer;
                        try {
                            transformer = TransformerFactory.newInstance().newTransformer();
                            transformer.transform(source, result);
                        } catch ( TransformerException ex ) {
                            throw (new CustomException(TAG, ex));
                        }
    }
    
    protected DocumentBuilder getNewDocumentBuilder() throws ParserConfigurationException {
        return ( DocumentBuilderFactory.newInstance().newDocumentBuilder() );
    }
    
    private void createElementFromGame(Game game, Element element) {
        element.setAttribute("title", game.getTitle());
        element.setAttribute("genre", game.getGenre());
        element.setAttribute("completed", Boolean.toString(game.isCompleted()));
        
        if ( game.hasAssociatedPlatformGame()){
            element.setAttribute("platform", game.getAssociatedGame().getPlatform().toString());
            element.setAttribute("platformid", Integer.toString(game.getAssociatedGame().getId()));
            element.setAttribute("platformname", game.getAssociatedGame().getTitle());
        }
        
        if ( game.hasMetaInformation() ) {
            element.setAttribute("metaname", game.getMetaInformation().getTitle());
            element.setAttribute("metadescription", game.getMetaInformation().getSummary());
            element.setAttribute("metagenre", game.getMetaInformation().getGenre());
            element.setAttribute("metascore", Integer.toString(game.getMetaInformation().getMetascore()));
            element.setAttribute("userscore", Integer.toString(game.getMetaInformation().getUserscore()));
        }        
    }

    private Game readGameFromNamedNodeMap(NamedNodeMap nodeMap) {
        Game game ;
            game = new Game();
            game.setTitle(nodeMap.getNamedItem("title").getNodeValue());
            game.setGenre(nodeMap.getNamedItem("genre").getNodeValue());
            game.setCompleted(Boolean.parseBoolean(nodeMap.getNamedItem("completed").getNodeValue()));
            
            if ( nodeMap.getNamedItem("platform") != null ) {
                AbstractPlatformGame associatedGame;
                    associatedGame = PlatformGameFactory.create(PlatformGame.valueOf(nodeMap.getNamedItem("platform").getNodeValue()));
                    associatedGame.setId(Integer.parseInt(nodeMap.getNamedItem("platformid").getNodeValue()));
                    associatedGame.setName(nodeMap.getNamedItem("platformname").getNodeValue());
                    
                game.setAssociatedGame(associatedGame);
            }
            
            if ( nodeMap.getNamedItem("metaname") != null ) {
                MetaInformation metaInformation;
                    metaInformation = new MetaInformation(
                        nodeMap.getNamedItem("metaname").getNodeValue(),
                        nodeMap.getNamedItem("metadescription").getNodeValue(),
                        nodeMap.getNamedItem("metagenre").getNodeValue(),
                        Integer.parseInt(nodeMap.getNamedItem("metascore").getNodeValue()),
                        Integer.parseInt(nodeMap.getNamedItem("userscore").getNodeValue())
                    );
                    
                game.setMetaInformation(metaInformation);
                
            }  
            
            return ( game );
    }
}
