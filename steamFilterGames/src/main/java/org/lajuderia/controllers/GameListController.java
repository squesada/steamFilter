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
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;
import javax.swing.JOptionPane;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import org.lajuderia.models.GameListModel;
import org.lajuderia.views.GameListView;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Sergio
 */
public class GameListController implements Observer {
    private GameListView _view ;
    private GameListModel _model ;
    GameListListener _listener = new GameListListener();
    
    public GameListController(GameListView view, GameListModel model) {
        this._view = view ;
        this._model = model ;        
        
        _view.registerListener(_listener);        
            
        _model.addObserver(GameListController.this);
    }

    private void importGamesFromSteam() {
        String steamID = JOptionPane.showInputDialog(
                _view, 
                java.util.ResourceBundle
                    .getBundle("MessagesBundle").getString("REQUEST_STEAM_ID"),
                java.util.ResourceBundle
                        .getBundle("ConfigurationBundle").getString("STEAM_USER_ID")
        );
        
    }

    private void loadGamesFromXML() throws Exception {
        _model.loadGamesFromDisk();
    }

    public void update(Observable o, Object o1) {
        SteamGamesTableModel model;
            model = new SteamGamesTableModel();
            
            _view.setTableModel(model);

            Iterator<SteamGame> it = _model.getGamesIterator();
            
            while ( it.hasNext() ){
               model.addRow(gameToArray(it.next()));
            }
            
            model.addTableModelListener(_listener);
    }
    
    private Object[] gameToArray(SteamGame game){
        Object[] row ;
            row = new Object[7];
            row[0] = false;
            row[1] = game.getId();
            row[2] = game.getName();
            row[3] = game.getGenre();
            row[4] = game.getMetagame().getMetascore();
            row[5] = game.getMetagame().getUserscore();
            row[6] = game.isCompleted();
            
            return ( row );
    }
    
    private class SteamGamesTableModel extends DefaultTableModel{
        private final String[] TABLE_TITLES = {"Sel","ID","Nombre","Género","Metascore","Userscore","Completado"};
        
        public SteamGamesTableModel(){
            super();
            this.setColumnIdentifiers(TABLE_TITLES);
        }
        
        @Override
        public Class getColumnClass(int columnIndex) {
            Class columnClass;

            if (columnIndex == 0 || columnIndex == 6 )
                columnClass = java.lang.Boolean.class;
            else if ( columnIndex == 1 || columnIndex == 4 || columnIndex == 5 )
                columnClass = java.lang.Integer.class;
            else
                columnClass = java.lang.String.class;

            return (columnClass);
        }
        
        @Override
        public boolean isCellEditable(int row, int column){
            return ( column == 0 || column == 6 ) ;
        }
    }
    
    private class GameListListener implements ActionListener, TableModelListener {
        
        public void actionPerformed(ActionEvent ae) {
            ResourceBundle res = java.util.ResourceBundle.getBundle("LabelsBundle");
            String command = ae.getActionCommand();

            try {
                if ( command.equals(res.getString("LOAD_GAMES")) ) {
                    loadGamesFromXML();
                }
                else if ( command.equals(res.getString("IMPORT_GAMES_FROM_STEAM")) ) {
                    importGamesFromSteam();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(_view, ex.getMessage());
            }
            
            System.out.println(command);
        }

        public void tableChanged(TableModelEvent tme) {
            if ( tme.getColumn() == 6 ) {
                _model.findGameById((Integer) _view.getTableValueAt(tme.getLastRow(), 1))
                        .setCompleted((Boolean) _view.getTableValueAt(tme.getLastRow(), 6));
            }
        }
        
    }

}
