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

import org.lajuderia.beans.SteamGame;
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
import org.lajuderia.models.GameListModel;
import org.lajuderia.views.GameListView;

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

    private int importGamesFromSteam() throws Exception {
        ResourceBundle res = java.util.ResourceBundle
                .getBundle("ConfigurationBundle") ;
        
        String steamID ;
            steamID = JOptionPane.showInputDialog(
                _view, 
                java.util.ResourceBundle
                    .getBundle("MessagesBundle").getString("REQUEST_STEAM_ID"),
                res.containsKey("STEAM_USER_ID")
                    ? res.getString("STEAM_USER_ID")
                    : ""
            );
        
        int addedGamesCount ;
        try {
            addedGamesCount = _model
                    .getUpdatedGameListFromSteam(Long.parseLong(steamID)).size() ;
        } catch (Exception ex) {
            throw ( ex ) ;
        }
        
        return ( addedGamesCount ) ;
    }

    private void loadGamesFromXML() throws Exception {
        _model.loadGamesFromDisk();
    }
    
    private void saveGamesToXML() throws Exception {
        _model.saveGamesToDisk();
    }
    
    private void updateGamesWithMetaInfo() {
        List<Integer> gameIds;
            gameIds = new ArrayList<Integer>();
            for ( int row : _view.getSelectedTableRows() ) {
                gameIds.add((Integer) _view.getTableValueAt(row,GameListTableModel.ID_NUM_COLUMN));
            }
        
        new LoadMetacriticInfoWorker(gameIds).execute();
    }
    
    public void update(Observable o, Object o1) {
        GameListTableModel model;
            model = new GameListTableModel();
            
            _view.setTableModel(model);

            Iterator<SteamGame> it = _model.getGamesIterator();
            
            while ( it.hasNext() ){
               model.addRow(gameToArray(it.next()));
            }
            
            model.addTableModelListener(_listener);
    }
    
    private Object[] gameToArray(SteamGame game){
        Object[] row ;
            row = new Object[6];
            row[0] = game.getId();
            row[1] = game.getName();
            row[2] = game.getGenre();
            row[3] = game.hasMetaInformation()
                    ? game.getMetagame().getMetascore()
                    : null ;
            row[4] = game.hasMetaInformation()
                    ? game.getMetagame().getUserscore()
                    : null ;
            row[5] = game.isCompleted();
            
            return ( row );
    }
    
    private class GameListTableModel extends DefaultTableModel{
        public static final int ID_NUM_COLUMN = 0 ;
        public static final int METASCORE_NUM_COLUMN = 3 ;
        public static final int USERSCORE_NUM_COLUMN = 4 ;
        public static final int COMPLETED_NUM_COLUMN = 5 ;
        
        private final String[] TABLE_TITLES = {"ID","Nombre","Género","Metascore","Userscore","Completado"};
        
        public GameListTableModel(){
            super();
            this.setColumnIdentifiers(TABLE_TITLES);
        }
        
        @Override
        public Class getColumnClass(int columnIndex) {
            Class columnClass;

            if ( columnIndex == COMPLETED_NUM_COLUMN )
                columnClass = java.lang.Boolean.class;
            else if ( ( columnIndex == ID_NUM_COLUMN )
                    || ( columnIndex == METASCORE_NUM_COLUMN )
                    || ( columnIndex == USERSCORE_NUM_COLUMN )
                    )
                columnClass = java.lang.Integer.class;
            else
                columnClass = java.lang.String.class;

            return (columnClass);
        }
        
        @Override
        public boolean isCellEditable(int row, int column){
            return ( column == COMPLETED_NUM_COLUMN ) ;
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
                    resultMessage = msgBundle.getString("GAMES_PROPERLY_LOADED");
                }
                else if ( command.equals(lblBundle.getString("IMPORT_GAMES_FROM_STEAM")) ) {
                    int addedGamesCount = importGamesFromSteam();
                    resultMessage = String.format(
                        msgBundle.getString("GAMES_PROPERLY_IMPORTED") ,
                        addedGamesCount
                    );
                }
                else if ( command.equals(lblBundle.getString("SAVE_GAMES")) ) {
                    saveGamesToXML();
                    resultMessage = msgBundle.getString("GAMES_PROPERLY_SAVED");
                }
                else if ( command.equals(lblBundle.getString("IMPORT_METACRITIC"))) {
                    updateGamesWithMetaInfo();
                    resultMessage = msgBundle.getString("METACRITIC_INFO_EXECUTED");
                }
                _view.setMessageStatus(resultMessage);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(_view, ex.getMessage());
            }
        }

        public void tableChanged(TableModelEvent tme) {
            if ( tme.getColumn() == GameListTableModel.COMPLETED_NUM_COLUMN ) {
                _model.findGameById((Integer) _view.getTableValueAt(tme.getLastRow(), 1))
                        .setCompleted((Boolean) _view.getTableValueAt(tme.getLastRow(), 6));
            }
        }        
    }
    
    
    public class LoadMetacriticInfoWorker extends SwingWorker<Void, Integer> {
        private final List<Integer> _gameIds;
        
        public LoadMetacriticInfoWorker(List<Integer> gameIds){
            this._gameIds = gameIds;
        }

        @Override
        protected Void doInBackground() throws Exception {
            int count = 0 ;
            
            for ( int gameId : _gameIds ) {
                if ( _model.updateGameWithMetaInfo(gameId) ) {
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
}
