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
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import org.lajuderia.models.AddGameModel;
import org.lajuderia.models.GameListModel;
import org.lajuderia.models.GameSelectionModel;
import org.lajuderia.views.AddGameView;
import org.lajuderia.views.GameListView;
import org.lajuderia.views.GameSelectionView;
import org.lajuderia.views.ShowGameView;

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
        new LoadMetacriticInfoWorker(_view.getSelectedModelRows()).execute();
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
                    _view.getSelectedModelRows()[0],
                    selectionController.getSelectedMetaInformation().getTitle()
            ).execute();
        }
    }    

    private void addNewGameToModel() {
        AddGameView view = new AddGameView(_view);
        AddGameModel model = new AddGameModel();
        AddGameController controller = new AddGameController(view, model);
            view.setVisible(true);
            
        if ( controller.wasOk() ) {
            _model.addNewGame(model.getGame());
        }
    }
    
    private void removeGamesFromModel() {
        //Store game ids in order to keep them once they are delete (positions will be change)
        String[] ids = new String[_view.getSelectedModelRows().length];
        
        int pos = 0 ;
        for ( int row : _view.getSelectedModelRows() ) {
            ids[pos] = (String) _model.getValueAt(row, GameListModel.ID_NUM_COLUMN);
            ++pos;
        }
        
        for (String id : ids) {
            _model.removeGameById(id);
        }
    }
    
    private void openGameForView() {
        ShowGameView view;
            view = new ShowGameView(
                _view,
                _model.findGameById((String) _model.getValueAt(_view.getSelectedModelRows()[0], GameListModel.ID_NUM_COLUMN))
            );
            view.setVisible(true);
    }
    
    private class GameListListener implements ActionListener {
        
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
                else if ( command.equals(lblBundle.getString("REMOVE_GAME"))) {
                    if ( _view.getSelectedModelRows().length > 0 ) {
                        if (JOptionPane.showConfirmDialog(
                            _view,
                            msgBundle.getString("CONFIRM_REMOVE_GAMES"),
                            msgBundle.getString("ALERT_DIALOG_TITLE"),
                            JOptionPane.OK_CANCEL_OPTION
                        ) == JOptionPane.OK_OPTION){
                        removeGamesFromModel();
                        resultMessage = msgBundle.getString("GAME_REMOVED");
                        }
                        else
                            resultMessage = msgBundle.getString("OPERATION_CANCELLED");
                    }
                    else
                        resultMessage = msgBundle.getString("SELECT_AT_LEAST_ONE_GAME");                    
                }
                else if ( command.equals(lblBundle.getString("VIEW_GAME"))) {
                    if ( _view.getSelectedModelRows().length == 1 ) {
                        openGameForView();
                    }
                    else
                        resultMessage = msgBundle.getString("SELECT_JUST_ONE_GAME");                    
                }
                _view.setStatusBarMessage(resultMessage);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(_view, ex.getMessage());
            }
        }        
    }
    
    public class LoadMetacriticInfoWorker extends SwingWorker<Void, Integer> {
        private final int[] _gamePositions;
        
        public LoadMetacriticInfoWorker(int[] gamePositions){
            this._gamePositions = gamePositions;
        }

        @Override
        protected Void doInBackground() {
            int count = 0 ;
            
            for ( int gamePosition : _gamePositions ) {
                if ( _model.updateGameWithMetaInfoAuto(gamePosition) ) {
                    ++count;
                    publish(count);
                }
            }
            
            return ( null );
        }
        
         @Override
        protected void process(List<Integer> chunks) {
            _view.setStatusBarMessage(String.format(java.util.ResourceBundle.getBundle("MessagesBundle").getString("METACRITIC_INFO_LOADING"), chunks.get(0), _gamePositions.length));
        }
        
        @Override
        protected void done(){
            _view.setStatusBarMessage(java.util.ResourceBundle.getBundle("MessagesBundle").getString("METACRITIC_INFO_LOADED"));
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
            _view.setStatusBarMessage(String.format(java.util.ResourceBundle.getBundle("MessagesBundle").getString("STEAM_LOADED"), _loadedGamesCount));
        }        
    }
    
    public class UpdateGameWithMetaInformationWorker extends SwingWorker<Void,Void> {
        private final int _pos;
        private final String _title;
        
        public UpdateGameWithMetaInformationWorker(int pos, String title) {
            this._pos = pos;
            this._title = title;
        }
        
        @Override
        protected Void doInBackground() throws Exception {
            _view.setStatusBarMessage(java.util.ResourceBundle.getBundle("MessagesBundle").getString("METACRITIC_INFO_EXECUTED"));
            _model.updateGameWithMetaInfoManual(_pos, _title);
            
            return ( null );
        }

        @Override
        protected void done() {
            _view.setStatusBarMessage(java.util.ResourceBundle.getBundle("MessagesBundle").getString("METACRITIC_INFO_LOADED"));
        }        
    }
}
