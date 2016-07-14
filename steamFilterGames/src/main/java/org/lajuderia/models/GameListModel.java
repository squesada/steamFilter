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

import org.lajuderia.beans.IGDBInformation;
import org.lajuderia.communication.Xml;
import org.lajuderia.beans.SteamGame;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.TreeSet;
import javax.swing.table.AbstractTableModel;
import org.lajuderia.beans.AbstractPlatformGame;
import org.lajuderia.beans.Game;
import org.lajuderia.daos.IGDBInformationDAO;
import org.lajuderia.daos.SteamGameDAO;

/**
 * Model class related to the main view
 * @author Sergio
 */
public class GameListModel extends AbstractTableModel {
    private static final int ID_NUM_COLUMN = 0 ;
    private static final int TITLE_NUM_COLUMN = 1 ;
    private static final int GENRE_NUM_COLUMN = 2 ;
    private static final int RATING_NUM_COLUMN = 3 ;
    private static final int AGGREGATED_RATING_NUM_COLUMN = 4 ;
    private static final int T2B_NORMALLY_NUM_COLUMN = 5 ;
    private static final int COMPLETED_NUM_COLUMN = 6 ;
    private static final int FAVOURITE_NUM_COLUMN = 7 ;
    
    private final ResourceBundle textBundle =
            java.util.ResourceBundle.getBundle("TextsBundle");

    private final String[] COLUMN_TITLES  = {
        textBundle.getString("GAME_ID"),
        textBundle.getString("GAME_TITLE"),
        textBundle.getString("GAME_GENRE"),
        textBundle.getString("GAME_RATING"),
        textBundle.getString("GAME_AGGREGATED_RATING"),
        textBundle.getString("GAME_T2B_NORMALLY"),
        textBundle.getString("GAME_COMPLETED"),
        textBundle.getString("GAME_FAVOURITE")
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
            newGameList = new ArrayList<>();
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
     * Updates a game with the IGDB information
     * @param id Game ID
     * @return Boolean (true when the game has been update)
     */
    public boolean updateGameWithIGDBInfoAuto(String id) {
        boolean gameChanged = false ;
        Game theGame = _gameList.findGameById(id);

        if ( theGame != null ) {
            IGDBInformation igdbInformation = theGame.hasIGDBInformation()
                    ? IGDBInformationDAO.findMetaInfoByID(theGame.getIGDBInformation().getId())
                    : IGDBInformationDAO.findMetaInfoByTitle(theGame.getTitle());

            if ( igdbInformation != null && (!theGame.hasIGDBInformation() || !igdbInformation.equals(theGame.getIGDBInformation())) ) {
                theGame.setMetaInformation(igdbInformation);

                gameChanged = true;
            }
        }

        if ( gameChanged )
            fireTableDataChanged();

        return ( gameChanged ) ;
    }
    
    /**
     * Updates a game with the Metacritic information related to a game
     * @param id Game ID
     * @param igdbInformation Metainformation related to the game
     * @return Boolean (true when the game has been updated)
     */
    public boolean updateGameWithIGDBInfoManual(String id, IGDBInformation igdbInformation) {
        boolean gameChanged = false ;
        Game theGame = _gameList.findGameById(id);

        if ( theGame != null ) {
            if ( igdbInformation != null && (!theGame.hasIGDBInformation() || !igdbInformation.equals(theGame.getIGDBInformation())) ) {
                theGame.setMetaInformation(igdbInformation);
                
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

        if ( ( columnIndex == COMPLETED_NUM_COLUMN )
                || (columnIndex == FAVOURITE_NUM_COLUMN) 
                )
            columnClass = java.lang.Boolean.class;
        else if ( (columnIndex == RATING_NUM_COLUMN)
                || (columnIndex == T2B_NORMALLY_NUM_COLUMN)
                || (columnIndex == AGGREGATED_RATING_NUM_COLUMN)
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
                || (column == FAVOURITE_NUM_COLUMN)
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
            case RATING_NUM_COLUMN:
                if ( _gameList.get(row).hasIGDBInformation() )
                    result = (int) _gameList.get(row).getIGDBInformation().getRating();
                break;
            case T2B_NORMALLY_NUM_COLUMN:
                if ( _gameList.get(row).hasIGDBInformation() )
                    result = (int) _gameList.get(row).getIGDBInformation().getNormallyT2B();
                break;
            case AGGREGATED_RATING_NUM_COLUMN:
                if ( _gameList.get(row).hasIGDBInformation() ) {
                    result = (int) _gameList.get(row).getIGDBInformation().getAggregatedRating();
                }
                break;
            case COMPLETED_NUM_COLUMN:
                result = _gameList.get(row).isCompleted();
                break;
            case FAVOURITE_NUM_COLUMN:
                result = _gameList.get(row).isFavourite();
                break;
        }
        
        return ( result );
    }
    
    /**
     * Gets the genres list
     * @return String[]
     */
    public String[] getGenres() {        
        TreeSet<String> treeGenres = new TreeSet<>();
            for (Game a_gameList : _gameList) {
                treeGenres.add(a_gameList.getGenre());
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
            case FAVOURITE_NUM_COLUMN:
                _gameList.get(row).setFavourite((Boolean) value);
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
        Game findGameById(String id) {
            Game foundGame = null;
            int position = findGamePositionById(id);
                if ( position != -1 ) {
                    foundGame = get(position);
                }
            
            return ( foundGame );
        }
        
        int findGamePositionById(String id) {
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