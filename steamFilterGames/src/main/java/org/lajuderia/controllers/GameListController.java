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

package org.lajuderia.controllers ;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import org.lajuderia.beans.Game;
import org.lajuderia.models.GameListModel;
import org.lajuderia.models.GameSelectionModel;
import org.lajuderia.views.GameListView;
import org.lajuderia.views.GameSelectionView;

/**
 *
 * @author Sergio
 */
public class GameListController implements Observer {
    private final GameListView _view ;
    private final GameListModel _model ;
    GameListListener _listener = new GameListListener();
    
    public GameListController(GameListView view, GameListModel model) {
        this._view = view ;
        this._model = model ;        
        
        _view.registerListener(_listener);            
        _model.addObserver(GameListController.this);
    }

    private void importGamesFromSteam(long steamID) throws Exception {        
        new LoadSteamGamesWorker(steamID).execute();
    }

    private void loadGamesFromXML() throws Exception {
        _model.loadGamesFromDisk();
    }
    
    private void saveGamesToXML() throws Exception {
        _model.saveGamesToDisk();
    }
    
    private void updateGamesWithMetaInfoAuto() {
        List<String> gameIds;
            gameIds = new ArrayList<String>();
            for ( int row : _view.getSelectedTableRows() ) {
                gameIds.add((String) _view.getTableValueAt(row,GameListTableModel.ID_NUM_COLUMN));
            }
        
        new LoadMetacriticInfoWorker(gameIds).execute();
    }
    
    public void update(Observable o, Object o1) {
        GameListTableModel model;
            model = new GameListTableModel();
            
            Iterator<Game> it = _model.getGamesIterator();
            
            while ( it.hasNext() ){
               model.addRow(createArrayFromGame(it.next()));
            }
            
            model.addTableModelListener(_listener);
            _view.setTableModel(model);            
    }
    
    private Object[] createArrayFromGame(Game game){
        Object[] row ;
            row = new Object[6];
            row[0] = game.getId();
            row[1] = game.getTitle();
            row[2] = game.getGenre();
            row[3] = game.hasMetaInformation()
                    ? game.getMetaInformation().getMetascore()
                    : null ;
            row[4] = game.hasMetaInformation()
                    ? game.getMetaInformation().getUserscore()
                    : null ;
            row[5] = game.isCompleted();
            
            return ( row );
    }
    
    private void updateGameWithMetaInfoManual() {
        Game selectedGame = _model.findGameById((String) _view.getTableValueAt(_view.getSelectedTableRows()[0],GameListTableModel.ID_NUM_COLUMN));
        
        GameSelectionModel selectionModel = new GameSelectionModel();
        GameSelectionView selectionView;
            selectionView = new GameSelectionView(_view);            
            selectionView.setEnteredTitle(selectedGame.getTitle());
        
        GameSelectionController selectionController = new GameSelectionController(selectionView,selectionModel);
            selectionView.setVisible(true);
            
        if ( selectionController.wasOk() ) {
            new UpdateGameWithMetaInformationWorker(
                    selectedGame.getId(),
                    selectionController.getSelectedMetaInformation().getTitle()
            ).execute();
        }
    }
    
    private class GameListTableModel extends DefaultTableModel{
        public static final int ID_NUM_COLUMN = 0 ;
        public static final int NAME_NUM_COLUMN = 1 ;
        public static final int GENRE_NUM_COLUMN = 2 ;
        public static final int METASCORE_NUM_COLUMN = 3 ;
        public static final int USERSCORE_NUM_COLUMN = 4 ;
        public static final int COMPLETED_NUM_COLUMN = 5 ;
        private final ResourceBundle textBundle =
                java.util.ResourceBundle.getBundle("TextsBundle");
        
        private final String[] TABLE_TITLES = {
            textBundle.getString("GAME_ID"),
            textBundle.getString("GAME_TITLE"),
            textBundle.getString("GAME_GENRE"),
            textBundle.getString("GAME_METASCORE"),
            textBundle.getString("GAME_USERSCORE"),
            textBundle.getString("GAME_COMPLETED")
        };
        
        public GameListTableModel(){
            super();
            this.setColumnIdentifiers(TABLE_TITLES);
        }
        
        @Override
        public Class getColumnClass(int columnIndex) {
            Class columnClass;

            if ( columnIndex == COMPLETED_NUM_COLUMN )
                columnClass = java.lang.Boolean.class;
            else if ( ( columnIndex == METASCORE_NUM_COLUMN )
                    || ( columnIndex == USERSCORE_NUM_COLUMN )
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
                    || (column == NAME_NUM_COLUMN)
                    || (column == GENRE_NUM_COLUMN)
                );
        }
    }
    
    private class GameListListener implements ActionListener, TableModelListener {
        
        public void actionPerformed(ActionEvent ae) {
            String resultMessage = null;
            ResourceBundle lblBundle = java.util.ResourceBundle.getBundle("LabelsBundle");
            ResourceBundle msgBundle = java.util.ResourceBundle.getBundle("MessagesBundle");
            String command = ae.getActionCommand();

            try {
                if ( command.equals(lblBundle.getString("LOAD_GAMES")) ) {
                    loadGamesFromXML();
                    resultMessage = msgBundle.getString("GAMES_LOADED");
                }
                else if ( command.equals(lblBundle.getString("IMPORT_GAMES_FROM_STEAM")) ) {
                    ResourceBundle res = java.util.ResourceBundle
                        .getBundle("ConfigurationBundle") ;
        
                    String steamID ;
                        steamID = JOptionPane.showInputDialog(
                            _view, 
                            java.util.ResourceBundle
                                .getBundle("MessagesBundle").getString("STEAM_USER_ID_REQUEST"),
                            res.containsKey("STEAM_USER_ID")
                                ? res.getString("STEAM_USER_ID")
                                : ""
                        );
                        
                        if ( steamID != null && !steamID.isEmpty() && steamID.matches("\\d*")) {                        
                            importGamesFromSteam(Long.parseLong(steamID));
                            resultMessage = msgBundle.getString("STEAM_LOADING");
                        }
                        else
                            resultMessage = msgBundle.getString("OPERATION_CANCELLED");
                }
                else if ( command.equals(lblBundle.getString("SAVE_GAMES")) ) {
                    if (JOptionPane.showConfirmDialog(
                            _view,
                            msgBundle.getString("CONFIRM_WRITE_GAMES_TO_DISK"),
                            msgBundle.getString("ALERT_DIALOG_TITLE"),
                            JOptionPane.OK_CANCEL_OPTION
                        ) == JOptionPane.OK_OPTION){
                        saveGamesToXML();
                        resultMessage = msgBundle.getString("GAMES_SAVED");
                    }
                    else
                        resultMessage = msgBundle.getString("OPERATION_CANCELLED");
                }
                else if ( command.equals(lblBundle.getString("IMPORT_METACRITIC"))) {
                    updateGamesWithMetaInfoAuto();
                    resultMessage = msgBundle.getString("METACRITIC_INFO_EXECUTED");
                } else if ( command.equals(lblBundle.getString("IMPORT_METACRITIC_MANUAL"))) {
                    if ( _view.getSelectedTableRows().length == 1 ) {
                        updateGameWithMetaInfoManual();
                    }
                    else
                        resultMessage = msgBundle.getString("SELECT_JUST_ONE_GAME");
                }
                _view.setMessageStatus(resultMessage);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(_view, ex.getMessage());
            }
        }

        public void tableChanged(TableModelEvent tme) {
            Game theGame = _model.findGameById((String) _view.getTableModelValueAt(tme.getLastRow(), 0)) ;
                switch ( tme.getColumn() ) {
                    case GameListTableModel.NAME_NUM_COLUMN:
                        theGame.setTitle((String) _view.getTableModelValueAt(tme.getLastRow(), tme.getColumn()));
                        break ;
                    case GameListTableModel.GENRE_NUM_COLUMN:
                        theGame.setGenre((String) _view.getTableModelValueAt(tme.getLastRow(), tme.getColumn()));
                        break ;
                    case GameListTableModel.COMPLETED_NUM_COLUMN:
                        theGame.setCompleted((Boolean) _view.getTableModelValueAt(tme.getLastRow(), tme.getColumn()));
                        break ;
                    default:                       
                } 
        }        
    }
    
    
    public class LoadMetacriticInfoWorker extends SwingWorker<Void, Integer> {
        private final List<String> _gameIds;
        
        public LoadMetacriticInfoWorker(List<String> gameIds){
            this._gameIds = gameIds;
        }

        @Override
        protected Void doInBackground() {
            int count = 0 ;
            
            for ( String gameId : _gameIds ) {
                if ( _model.updateGameWithMetaInfoAuto(gameId) ) {
                    ++count;
                    publish(count);
                }
            }
            
            return ( null );
        }
        
         @Override
        protected void process(List<Integer> chunks) {
            _view.setMessageStatus(String.format(java.util.ResourceBundle.getBundle("MessagesBundle").getString("METACRITIC_INFO_LOADING"), chunks.get(0), _gameIds.size()));
        }
        
        @Override
        protected void done(){
            _model.notifyObservers();
            _view.setMessageStatus(java.util.ResourceBundle.getBundle("MessagesBundle").getString("METACRITIC_INFO_LOADED"));
        }        
    }
    
    public class LoadSteamGamesWorker extends SwingWorker<Void, Void> {
        private final long _steamId;
        private int _loadedGamesCount;
        
        public LoadSteamGamesWorker(Long steamId){
            this._steamId = steamId;
        }

        @Override
        protected Void doInBackground() throws Exception {
            _loadedGamesCount = _model.getUpdatedGameListFromSteam(_steamId).size() ;
            
            return ( null );
        }
        
        @Override
        protected void done(){
            _view.setMessageStatus(String.format(java.util.ResourceBundle.getBundle("MessagesBundle").getString("STEAM_LOADED"), _loadedGamesCount));
        }        
    }
    
    public class UpdateGameWithMetaInformationWorker extends SwingWorker<Void,Void> {
        private final String _gameId;
        private final String _title;
        
        public UpdateGameWithMetaInformationWorker(String gameId, String title) {
            this._gameId = gameId;
            this._title = title;
        }
        
        @Override
        protected Void doInBackground() throws Exception {
            _view.setMessageStatus(java.util.ResourceBundle.getBundle("MessagesBundle").getString("METACRITIC_INFO_EXECUTED"));
            _model.updateGameWithMetaInfoManual(_gameId, _title);
            
            return ( null );
        }

        @Override
        protected void done() {
            _model.notifyObservers();
            _view.setMessageStatus(java.util.ResourceBundle.getBundle("MessagesBundle").getString("METACRITIC_INFO_LOADED"));
        }        
    }
}
