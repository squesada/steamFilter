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
import java.util.concurrent.ExecutionException;
import javax.swing.JOptionPane;
import javax.swing.RowFilter;
import javax.swing.SwingWorker;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableRowSorter;
import org.lajuderia.models.AddGameModel;
import org.lajuderia.models.GameListModel;
import org.lajuderia.models.GameSelectionModel;
import org.lajuderia.views.AddGameView;
import org.lajuderia.views.GameListView;
import org.lajuderia.views.GameSelectionView;
import org.lajuderia.views.ShowGameView;

/**
 * Controller class from main view
 * @author Sergio
 */
public class GameListController {
    private final GameListView _view ;
    private final GameListModel _model ;
    GameListListener _listener = new GameListListener();
    
    /**
     * Constructor
     * @param view the view
     * @param model the model
     */
    public GameListController(GameListView view, GameListModel model) {
        this._view = view ;
        this._model = model ;
        
        this._view.setTableModel(_model);
        
        _view.registerActionListener(_listener);
        _view.registerListModelListener(_listener);
    }

    private void importGamesFromSteam(long steamID) throws Exception {        
        new LoadSteamGamesWorker(steamID).execute();
    }

    private void loadGamesFromXML() throws Exception {
        _model.loadGamesFromDisk();
        _view.updateGenreCheckBoxList(_model.getGenres());
    }
    
    private void saveGamesToXML() throws Exception {
        _model.saveGamesToDisk();        
    }
    
    private void updateGamesWithMetaInfoAuto() {
        int[] selectedRows ;
            selectedRows = _view.getSelectedModelRows();
            
        String[] ids ;
            ids = new String[selectedRows.length];
            for ( int i = 0 ; i < selectedRows.length ; ++i ) {
                ids[i] = (String) _model.getValueAt(selectedRows[i], GameListModel.ID_NUM_COLUMN);
            }
            
        new LoadMetacriticInfoWorker(ids).execute();
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
                    (String) _model.getValueAt(_view.getSelectedModelRows()[0],GameListModel.ID_NUM_COLUMN),
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
    
    private class GameListListener implements ActionListener, ListSelectionListener {
        
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
                else if ( command.equals(lblBundle.getString("REFRESH")) ) {
                    _view.updateGenreCheckBoxList(_model.getGenres());
                    resultMessage = msgBundle.getString("GENRES_REFRESHED"); 
                }
                _view.setStatusBarMessage(resultMessage);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(_view, ex.getMessage());
            }
        }        

        public void valueChanged(ListSelectionEvent lse) {
            List<String> genres = _view.getSelectedGenres();
            RowFilter filter = null;
            
            if ( genres.size() > 0 ) {
                String expr = "(".concat(genres.get(0)).concat(")");
                for ( int i = 1 ; i < genres.size() ; i++ ) {
                    expr += "|(".concat(genres.get(i)).concat(")");
                }
                filter = RowFilter.regexFilter(expr,GameListModel.GENRE_NUM_COLUMN);                
            }
            ((TableRowSorter) _view.getTableRowSorter()).setRowFilter(filter);
        }
    }
    
    /**
     * Worker which loads Metacritic information from a list of games
     */
    public class LoadMetacriticInfoWorker extends SwingWorker<Boolean, Integer> {
        private final String[] _gameIds;
        
        /**
         * Constructor
         * @param gameIds List of game IDs 
         */
        public LoadMetacriticInfoWorker(String[] gameIds){
            this._gameIds = gameIds;
        }

        @Override
        protected Boolean doInBackground() {
            int count = 0 ;
            boolean wasOk = true;
            
            for ( String id : _gameIds ) {
                if ( _model.updateGameWithMetaInfoAuto(id) ) {
                    ++count;
                    publish(count);
                }
                else {
                    wasOk = false;
                }
            }
            
            return ( wasOk );
        }
        
         @Override
        protected void process(List<Integer> chunks) {
            _view.setStatusBarMessage(String.format(java.util.ResourceBundle.getBundle("MessagesBundle").getString("METACRITIC_INFO_LOADING"), chunks.get(0), _gameIds.length));
        }
        
        @Override
        protected void done(){
            String message;
            
            try {
                message = get()
                        ? java.util.ResourceBundle.getBundle("MessagesBundle").getString("METACRITIC_INFO_LOADED")
                        : java.util.ResourceBundle.getBundle("MessagesBundle").getString("METACRITIC_INFO_NOT_LOADED");                    
            } catch (InterruptedException ex) {
                message = ex.getMessage();
            } catch (ExecutionException ex) {
                message = ex.getMessage();
            }
            
            _view.setStatusBarMessage(message);
        }        
    }
    
    /**
     * Worker which loads into model the games a Steam user owns
     */
    public class LoadSteamGamesWorker extends SwingWorker<Void, Void> {
        private final long _steamId;
        private int _loadedGamesCount;
        
        /**
         * Constructor
         * @param steamId Steam user ID
         */
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
    
    /**
     * Worker which loads Metacritic information from a game
     */
    public class UpdateGameWithMetaInformationWorker extends SwingWorker<Boolean,Void> {
        private final String _id;
        private final String _title;
        
        /**
         * Constructor
         * @param id Steam game ID
         * @param title Metacritic game title
         */
        public UpdateGameWithMetaInformationWorker(String id, String title) {
            this._id = id;
            this._title = title;
        }
        
        @Override
        protected Boolean doInBackground() throws Exception {
            _view.setStatusBarMessage(java.util.ResourceBundle.getBundle("MessagesBundle").getString("METACRITIC_INFO_EXECUTED"));
                        
            return ( _model.updateGameWithMetaInfoManual(_id, _title) );
        }

        @Override
        protected void done() {
            String message;
            
            try {
                message = get()
                        ? java.util.ResourceBundle.getBundle("MessagesBundle").getString("METACRITIC_INFO_LOADED")
                        : java.util.ResourceBundle.getBundle("MessagesBundle").getString("GAME_HAS_NO_METAINFORMATION");
            } catch (InterruptedException ex) {
                message = ex.getMessage();
            } catch (ExecutionException ex) {
                message = ex.getMessage();
            }
                        
            _view.setStatusBarMessage(message);            
        }        
    }
}
