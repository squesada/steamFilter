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
import org.json.JSONException;
import org.json.JSONObject;
import org.lajuderia.beans.MetacriticGame;
import org.lajuderia.communication.SteamAPI;
import org.lajuderia.daos.SteamGameDAO;

/**
 *
 * @author Sergio
 */
public class GameListModel extends Observable {
    private HashMap<Integer, SteamGame> _gamesMap ;
    
    public GameListModel () {
        this._gamesMap = new HashMap<Integer, SteamGame>();
    }
    
    public void loadGamesFromDisk() throws Exception {        
        _gamesMap = Xml.loadGamesFromDisk();
        setChanged();
        notifyObservers();
    }
    
    public SteamGame findGameById(int id){
        return ( _gamesMap.get(id) ) ;
    }

    /**
     * @return the _gamesMap
     */
    public Iterator<SteamGame> getGamesIterator() {
        return _gamesMap.values().iterator();
    }

    public void saveGamesToDisk() throws Exception {
        Xml.saveGamesToDisk(_gamesMap);
    }
    
    public List<SteamGame> getUpdatedGameListFromSteam(long userId) {
        //TODO: Verificar que el usuario existe y tiene el perfil público
        
        List<SteamGame> newGameList;
            newGameList = new ArrayList<SteamGame>();
            for ( SteamGame game : SteamGameDAO.getUserOwnedGames(userId)) {
                if ( _gamesMap.get(game.getId()) == null ) {
                    setChanged();
                    _gamesMap.put(game.getId(), game);
                    newGameList.add(game);
                }
            }
            
            notifyObservers();
            
            return ( newGameList );
    }
    
    
    
    public boolean updateGameWithMetaInfo(int gameId) {
        boolean gameChanged = false ;
        SteamGame steamGame = _gamesMap.get(gameId);
        
        if ( steamGame != null ) {
            MetacriticGame metaGame;
            //TODO: Revisar si esto iría en el dao
                metaGame = readGameFromMetacriticJSon(
                    SteamAPI.getMetacriticInfo(steamGame.getName())
                );
                
            if ( metaGame != null && (!steamGame.hasMetaInformation() || !metaGame.equals(steamGame.getMetagame())) ) {
                steamGame.setMetagame(metaGame);
                setChanged();
                gameChanged = true;
            }
        }
        else {
            //TODO: lanzar error si no existe el juego en el hash
        }
        
        return ( gameChanged ) ;
    }
    
    private static MetacriticGame readGameFromMetacriticJSon(JSONObject gameInfo) {
        MetacriticGame game = null;
            try{
                String name = gameInfo.getString("name");
                String summary = gameInfo.getString("summary");
                String genre = gameInfo.getString("genre");
                int metascore = gameInfo.getInt("score");
                int userscore = (int) (gameInfo.getDouble("userscore")*10);
                
                game = new MetacriticGame(name, summary, genre, metascore, userscore);                
            }catch(JSONException ex)
            {}
            
            return ( game ) ;
    }
}
