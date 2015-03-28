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
import java.util.Arrays;
import org.lajuderia.communication.Xml;
import org.lajuderia.beans.SteamGame;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.TreeSet;
import javax.swing.table.AbstractTableModel;
import org.lajuderia.beans.AbstractPlatformGame;
import org.lajuderia.beans.Game;
import org.lajuderia.beans.MetaInformation;
import org.lajuderia.daos.MetaInformationDAO;
import org.lajuderia.daos.SteamGameDAO;

/**
 * Model class related to the main view
 * @author Sergio
 */
public class GameListModel extends AbstractTableModel {
    public static final int ID_NUM_COLUMN = 0 ;
    public static final int TITLE_NUM_COLUMN = 1 ;
    public static final int GENRE_NUM_COLUMN = 2 ;
    public static final int METASCORE_NUM_COLUMN = 3 ;
    public static final int USERSCORE_NUM_COLUMN = 4 ;
    public static final int MEANSCORE_NUM_COLUMN = 5 ;
    public static final int COMPLETED_NUM_COLUMN = 6 ;
    private final ResourceBundle textBundle =
            java.util.ResourceBundle.getBundle("TextsBundle");

    private final String[] COLUMN_TITLES  = {
        textBundle.getString("GAME_ID"),
        textBundle.getString("GAME_TITLE"),
        textBundle.getString("GAME_GENRE"),
        textBundle.getString("GAME_METASCORE"),
        textBundle.getString("GAME_USERSCORE"),
        textBundle.getString("GAME_MEANSCORE"),
        textBundle.getString("GAME_COMPLETED")
    };

    private final ArrayListGame _gameList = new ArrayListGame();
    
    /**
     * Updates the model with the cache information stored in the hdd
     * @throws Exception 
     */
    public void loadGamesFromDisk() throws Exception {        
        boolean hasChanged = false;
        
        for ( Game game : new Xml().loadGamesFromDisk() ) {
            if ( _gameList.findGameById(game.getId()) == null ) {
                _gameList.add(game);
                hasChanged = true;
            }
        }
        
        if ( hasChanged )
            fireTableDataChanged();
    }
    
    /**
     * Searchs a game from the model
     * @param id game ID
     * @return Game
     */
    public Game findGameById(String id){
        return ( _gameList.findGameById(id) ) ;
    }

    /**
     * Returns the game iterator
     * @return Iterator of Game
     */
    public Iterator<Game> getGamesIterator() {
        return _gameList.iterator();
    }

    /**
     * Saves the model to hdd
     * @throws Exception 
     */
    public void saveGamesToDisk() throws Exception {
        new Xml().saveGamesToDisk(_gameList);
    }
    
    /**
     * Updates the model with the Steam user library information
     * @param userId Steam user identifier
     * @return List of Game
     */
    public List<Game> getUpdatedGameListFromSteam(long userId) {
        //TODO: Verificar que el usuario existe y tiene el perfil público
        
        boolean hasChanged = false;
        
        List<Game> newGameList;
            newGameList = new ArrayList<Game>();
            for ( SteamGame currentGame : SteamGameDAO.getUserOwnedGames(userId)) {
                Game newGame = new Game(currentGame);
                
                if ( _gameList.findGameById(newGame.getId()) == null ) {
                   _gameList.add(newGame);
                    newGameList.add(newGame);
                    hasChanged = true;
                }
            }
            
            if ( hasChanged )
                fireTableDataChanged();
            
            return ( newGameList );
    }
    
    /**
     * Updates a game with the Metacritic information
     * @param id Game ID
     * @return Boolean (true when the game has been update)
     */
    public boolean updateGameWithMetaInfoAuto(String id) {
        boolean result = updateGameWithMetaInfoManual(id, null);
        if ( result )
            fireTableDataChanged();
        
        return ( result );
    }
    
    /**
     * Updates a game with the Metacritic information related to a game
     * @param id Game ID
     * @param title Game title to search
     * @return Boolean (true when the game has been updated)
     */
    public boolean updateGameWithMetaInfoManual(String id, String title) {
        boolean gameChanged = false ;
        Game theGame = _gameList.findGameById(id);
        
        if ( theGame != null ) {
            MetaInformation metaInformation = MetaInformationDAO.findMetaInfoByTitle(title != null ? title : theGame.getTitle());
                
            if ( metaInformation != null && (!theGame.hasMetaInformation() || !metaInformation.equals(theGame.getMetaInformation())) ) {
                theGame.setMetaInformation(metaInformation);
                
                gameChanged = true;
            }
        }
        
        if ( gameChanged )
            fireTableDataChanged();
        
        return ( gameChanged ) ;
    }    
    
    @Override
    public Class getColumnClass(int columnIndex) {
        Class columnClass;

        if ( columnIndex == COMPLETED_NUM_COLUMN )
            columnClass = java.lang.Boolean.class;
        else if ( (columnIndex == METASCORE_NUM_COLUMN)
                || (columnIndex == USERSCORE_NUM_COLUMN)
                || (columnIndex == MEANSCORE_NUM_COLUMN)
                )
            columnClass = java.lang.Integer.class;
        else
            columnClass = java.lang.String.class;

        return (columnClass);
    }

    @Override
    public boolean isCellEditable(int row, int column){
        return (
                (column == COMPLETED_NUM_COLUMN)
                || (column == TITLE_NUM_COLUMN)
                || (column == GENRE_NUM_COLUMN)
            );
    }

    public int getRowCount() {
        return ( _gameList.size() );
    }

    public int getColumnCount() {
        return ( COLUMN_TITLES.length );
    }

    @Override
    public String getColumnName(int i) {
        return ( COLUMN_TITLES[i] );
    }    
    
    /**
     * Gets the value from the table model
     * @param row Model row number
     * @param col Model column number
     * @return Object
     */
    public Object getValueAt(int row, int col ){
        Object result = null ;
        
        switch ( col ) {
            case ID_NUM_COLUMN:
                result = _gameList.get(row).getId();
                break;
            case TITLE_NUM_COLUMN:
                result = _gameList.get(row).getTitle();
                break;
            case GENRE_NUM_COLUMN:
                result = _gameList.get(row).getGenre();
                break;
            case METASCORE_NUM_COLUMN:
                if ( _gameList.get(row).hasMetaInformation() )
                    result = _gameList.get(row).getMetaInformation().getMetascore();
                break;
            case USERSCORE_NUM_COLUMN:
                if ( _gameList.get(row).hasMetaInformation() )
                    result = _gameList.get(row).getMetaInformation().getUserscore();
                break;
            case MEANSCORE_NUM_COLUMN:
                if ( _gameList.get(row).hasMetaInformation() ) {
                    result = _gameList.get(row).getMetaInformation().getMetascore() == 0
                            ? _gameList.get(row).getMetaInformation().getUserscore()
                            : (_gameList.get(row).getMetaInformation().getUserscore() == 0
                                ? _gameList.get(row).getMetaInformation().getMetascore()
                                : (_gameList.get(row).getMetaInformation().getMetascore()
                                    + _gameList.get(row).getMetaInformation().getUserscore()) /2);
                }
                break;
            case COMPLETED_NUM_COLUMN:
                result = _gameList.get(row).isCompleted();
                break;
        }
        
        return ( result );
    }
    
    /**
     * Gets the genres list
     * @return String[]
     */
    public String[] getGenres() {        
        TreeSet<String> treeGenres = new TreeSet<String>();
            if ( _gameList.size() > 0 ) {
                Iterator<Game> it = _gameList.iterator() ;
                while ( it.hasNext() ) {
                    treeGenres.add(it.next().getGenre());
                }
        }
                
        return ( Arrays.copyOf(treeGenres.toArray(), treeGenres.size(), String[].class));
    }

    @Override
    public void setValueAt(Object value, int row, int col) {
        
        switch ( col ) {
            case TITLE_NUM_COLUMN:
                _gameList.get(row).setTitle(value.toString());
                break;
            case GENRE_NUM_COLUMN:
                _gameList.get(row).setGenre(value.toString());
                break;
            case COMPLETED_NUM_COLUMN:
                _gameList.get(row).setCompleted((Boolean) value);
                break;
        }
    }

    /**
     * Adds a game to model
     * @param game Game to add
     */
    public void addNewGame(Game game) {
        game.getAssociatedGame().setId(searchMaxId(game.getAssociatedGame().getPlatform()) + 1);
        _gameList.add(game);
        fireTableDataChanged();
    }

    private int searchMaxId(AbstractPlatformGame.PlatformGame platform) {
        int maxId = 0;
        
        for ( Game game : _gameList ){
            if ( game.hasAssociatedPlatformGame()
                    && game.getAssociatedGame().getPlatform().equals(platform)
                    && game.getAssociatedGame().getId() > maxId ) {
                maxId = game.getAssociatedGame().getId() ;
            }
        }
        
        return ( maxId );
    }

    /**
     * Removes a game from the model
     * @param id Game ID
     */
    public void removeGameById(String id) {
        int gamePosition = _gameList.findGamePositionById(id);
        _gameList.remove(gamePosition);
        fireTableDataChanged();
    }
    
    /**
     * Subclass from ArrayList<Game> which allows find an element by ID
     */
    private class ArrayListGame extends ArrayList<Game> {
        public Game findGameById(String id) {
            Game foundGame = null;
            int position = findGamePositionById(id);
                if ( position != -1 ) {
                    foundGame = get(position);
                }
            
            return ( foundGame );
        }
        
        public int findGamePositionById(String id) {            
            return ( getGamePositionByIdAux(id, 0, this.size()-1 ));
        }
        
        private int getGamePositionByIdAux(String id, int numFrom, int numTo) {
            int position = -1;
            
            if ( numFrom == numTo ) {
                if ( this.get(numTo).getId().equals(id) )
                    position = numTo;
            }
            else if ( numFrom < numTo ) {
                position = getGamePositionByIdAux(id, numFrom, (numFrom+numTo)/2);
            
                if ( position == -1 ) {
                    position = getGamePositionByIdAux(id, (numFrom+numTo)/2+1, numTo);
                }
            }
            
            return ( position );
        }        
    }
}