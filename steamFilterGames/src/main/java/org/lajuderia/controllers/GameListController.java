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
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import org.lajuderia.beans.Game;
import org.lajuderia.models.GameListModel;
import org.lajuderia.models.GameSelectionModel;
import org.lajuderia.views.GameListView;
import org.lajuderia.views.GameSelectionView;

/**
 *
 * @author Sergio
 */
public class GameListController {
    private final GameListView _view ;
    private final GameListModel _model ;
    GameListListener _listener = new GameListListener();
    
    public GameListController(GameListView view, GameListModel model) {
        this._view = view ;
        this._model = model ;
        
        this._view.setTableModel(_model);
        
        _view.registerListener(_listener);
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
            for ( int row : _view.getSelectedModelRows() ) {
                gameIds.add((String) _model.getValueAt(row, GameListModel.ID_NUM_COLUMN));
            }
        
        new LoadMetacriticInfoWorker(gameIds).execute();
    }
    
    private void updateGameWithMetaInfoManual() {
        GameSelectionModel selectionModel = new GameSelectionModel();
        GameSelectionView selectionView;
            selectionView = new GameSelectionView(_view);            
            selectionView.setEnteredTitle((String) _model.getValueAt(_view.getSelectedModelRows()[0], GameListModel.TITLE_NUM_COLUMN));
        
        GameSelectionController selectionController = new GameSelectionController(selectionView,selectionModel);
            selectionView.setVisible(true);
            
        if ( selectionController.wasOk() ) {
            new UpdateGameWithMetaInformationWorker(
                    (String) _model.getValueAt(_view.getSelectedModelRows()[0], GameListModel.ID_NUM_COLUMN),
                    selectionController.getSelectedMetaInformation().getTitle()
            ).execute();
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
                    if ( _view.getSelectedModelRows().length == 1 ) {
                        updateGameWithMetaInfoManual();
                    }
                    else
                        resultMessage = msgBundle.getString("SELECT_JUST_ONE_GAME");
                }
                else if ( command.equals(lblBundle.getString("ADD_GAME"))) {
                    addNewGameToModel();
                    resultMessage = msgBundle.getString("GAME_ADDED");
                }
                _view.setMessageStatus(resultMessage);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(_view, ex.getMessage());
            }
        }

        public void tableChanged(TableModelEvent tme) {
            Game theGame = _model.findGameById((String) _model.getValueAt(tme.getLastRow(), 0)) ;
                switch ( tme.getColumn() ) {
                    case GameListModel.TITLE_NUM_COLUMN:
                        theGame.setTitle((String) _model.getValueAt(tme.getLastRow(), tme.getColumn()));
                        break ;
                    case GameListModel.GENRE_NUM_COLUMN:
                        theGame.setGenre((String) _model.getValueAt(tme.getLastRow(), tme.getColumn()));
                        break ;
                    case GameListModel.COMPLETED_NUM_COLUMN:
                        theGame.setCompleted((Boolean) _model.getValueAt(tme.getLastRow(), tme.getColumn()));
                        break ;
                    default:                       
                } 
        }        

        private void addNewGameToModel() {
            //TODO: Crear dialog de introducción de info
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
            _view.setMessageStatus(java.util.ResourceBundle.getBundle("MessagesBundle").getString("METACRITIC_INFO_LOADED"));
        }        
    }
}
