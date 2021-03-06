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
package org.lajuderia.controllers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;
import javax.swing.JList;
import javax.swing.SwingWorker;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.lajuderia.beans.IGDBInformation;
import org.lajuderia.models.GameSelectionModel;
import org.lajuderia.views.GameSelectionView;

/**
 * Controller class from game selection functionality
 * @author Sergio
 */
public class GameSelectionController {
    private final GameSelectionModel _model;
    private final GameSelectionView _view;
    private final GameSelectionListener _listener = new GameSelectionListener();
    private LoadMetaInfoWorker _worker;
    private IGDBInformation _selectedItem;
    private boolean _endOk = false;
    
    /**
     * Constructor
     * @param view the view
     * @param model the model
     */
    public GameSelectionController(GameSelectionView view, GameSelectionModel model) {
        this._model = model;
        this._view = view;        
        
        _view.registerActionListener(_listener);
        _view.registerDocumentListener(_listener);
        _view.registerListSelectionListener(_listener);
        _view.setListModel(_model);
            
        _view.setAllowToAccept(false);
        
        updateGameList();
    }
    
    private void updateGameList() {
        if ( ( _worker != null ) && ( !_worker.isDone()) )
            _worker.cancel(true);
        
        _worker = new LoadMetaInfoWorker(_view.getEnteredTitle());
        _worker.execute();
    }
    
    /**
     * Returns true when the view was closed using the OK button
     * @return 
     */
    public boolean wasOk(){
        return ( _endOk );
    }
    
    /**
     * Gets the selected game from the view
     * @return 
     */
    public IGDBInformation getSelectedMetaInformation() {
        return ( _selectedItem );
    }
    
    private class LoadMetaInfoWorker extends SwingWorker<Void, Integer> {
        private final String _title;
        private int _foundGamesCount;
        
        /**
         * Constructor
         * @param title the game title
         */
        public LoadMetaInfoWorker(String title){
            this._title = title;
            this._foundGamesCount = 0;
        }

        @Override
        protected Void doInBackground() throws Exception {
            _foundGamesCount = _model.updateModelWithSimilarMetainfoTo(_title);
            
            return ( null );
        }
        
        @Override
        protected void done(){
            _view.setMessageStatus(String.format(java.util.ResourceBundle.getBundle("MessagesBundle").getString("METACRITIC_GAMES_FOUND"), _foundGamesCount));
        }
    }

    private class GameSelectionListener implements ActionListener, DocumentListener, ListSelectionListener{
        
        private final ResourceBundle lblBundle = java.util.ResourceBundle.getBundle("LabelsBundle");
        private final ResourceBundle msgBundle = java.util.ResourceBundle.getBundle("MessagesBundle");

        public void actionPerformed(ActionEvent ae) {
            if ( ae.getActionCommand().equals(lblBundle.getString("OK"))) {
                _endOk = true;
            }
            else if ( ae.getActionCommand().equals(lblBundle.getString("CANCEL"))) {
                //Do nothing
            }
            
            _view.dispose();
        }

        public void valueChanged(ListSelectionEvent lse) {
            if ( ((JList) lse.getSource()).getSelectedIndex() != -1 ) {
                _selectedItem = (IGDBInformation) ((JList) lse.getSource()).getSelectedValue();
                _view.setAllowToAccept(true);

            }
            else {
                _view.setAllowToAccept(false);
            }            
        }

        public void insertUpdate(DocumentEvent de) {
            updateGameList();
            _view.setMessageStatus(msgBundle.getString("IGDB_INFO_EXECUTED"));
        }

        public void removeUpdate(DocumentEvent de) {
            updateGameList();
            _view.setMessageStatus(msgBundle.getString("IGDB_INFO_EXECUTED"));
        }

        public void changedUpdate(DocumentEvent de) {
            updateGameList();
            _view.setMessageStatus(msgBundle.getString("IGDB_INFO_EXECUTED"));
        }
    }
    
}
