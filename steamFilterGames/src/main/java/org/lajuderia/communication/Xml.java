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
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

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
                 NamedNodeMap attr = doc.getDocumentElement().getChildNodes().item(i).getAttributes();
                    SteamGame game ;
                        game = new SteamGame(
                            Integer.parseInt(attr.getNamedItem("id").getNodeValue()),
                            attr.getNamedItem("name").getNodeValue(),
                            attr.getNamedItem("genre").getNodeValue(),
                            Boolean.parseBoolean(attr.getNamedItem("completed").getNodeValue())
                        );
                        game.setMetagame(new MetacriticGame(
                            attr.getNamedItem("metaname").getNodeValue(),
                            attr.getNamedItem("metadescription").getNodeValue(),
                            attr.getNamedItem("metagenre").getNodeValue(),
                            Integer.parseInt(attr.getNamedItem("metascore").getNodeValue()),
                            Integer.parseInt(attr.getNamedItem("userscore").getNodeValue())
                        ));
                    
                    gamesMap.put(game.getId(), game);
            }
            
            return ( gamesMap ) ;
    }
}
