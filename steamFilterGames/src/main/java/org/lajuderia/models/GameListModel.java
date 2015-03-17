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

package org.lajuderia.models;

import java.util.ArrayList;
import org.lajuderia.communication.Xml;
import org.lajuderia.beans.SteamGame;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import org.lajuderia.beans.Game;
import org.lajuderia.beans.MetaInformation;
import org.lajuderia.daos.MetaInformationDAO;
import org.lajuderia.daos.SteamGameDAO;

/**
 *
 * @author Sergio
 */
public class GameListModel extends Observable {
    private HashMap<String, Game> _gamesMap ;
    
    public GameListModel () {
        this._gamesMap = new HashMap<String, Game>();
    }
    
    public void loadGamesFromDisk() throws Exception {        
        _gamesMap = Xml.loadGamesFromDisk();
        setChanged();
        notifyObservers();
    }
    
    public Game findGameById(String id){
        return ( _gamesMap.get(id) ) ;
    }

    /**
     * @return the _gamesMap
     */
    public Iterator<Game> getGamesIterator() {
        return _gamesMap.values().iterator();
    }

    public void saveGamesToDisk() throws Exception {
        Xml.saveGamesToDisk(_gamesMap.values());
    }
    
    public List<Game> getUpdatedGameListFromSteam(long userId) {
        //TODO: Verificar que el usuario existe y tiene el perfil público
        
        List<Game> newGameList;
            newGameList = new ArrayList<Game>();
            for ( SteamGame currentGame : SteamGameDAO.getUserOwnedGames(userId)) {
                Game newGame = new Game(currentGame);
                
                if ( _gamesMap.get(newGame.getId()) == null ) {
                    setChanged();
                    _gamesMap.put(newGame.getId(), newGame);
                    newGameList.add(newGame);
                }
            }
            
            notifyObservers();
            
            return ( newGameList );
    }
    
    public boolean updateGameWithMetaInfoAuto(String gameId) {
        return ( updateGameWithMetaInfoManual(gameId, null) ) ;
    }
    
    public boolean updateGameWithMetaInfoManual(String gameId, String title) {
        boolean gameChanged = false ;
        Game theGame = _gamesMap.get(gameId);
        
        if ( theGame != null ) {
            MetaInformation metaInformation = MetaInformationDAO.getMetaInfoByTitle(title != null ? title : theGame.getTitle());
                
            if ( metaInformation != null && (!theGame.hasMetaInformation() || !metaInformation.equals(theGame.getMetaInformation())) ) {
                theGame.setMetaInformation(metaInformation);
                setChanged();
                
                gameChanged = true;
            }
        }
        else {
            //TODO: lanzar error si no existe el juego en el hash
        }
        
        return ( gameChanged ) ;
    }
    
    public void insertGame(Game game){
        Game oldGame = _gamesMap.put(game.getId(), game);
        if ( !game.equals(oldGame) ){
            setChanged();
            notifyObservers();
        }        
    }
}
