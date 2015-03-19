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
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.SwingWorker;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.lajuderia.beans.MetaInformation;
import org.lajuderia.models.GameSelectionModel;
import org.lajuderia.views.GameSelectionView;

/**
 *
 * @author Sergio
 */
public class GameSelectionController {
    private final GameSelectionModel _model;
    private final GameSelectionView _view;
    private final GameSelectionListener _listener = new GameSelectionListener();
    private LoadMetaInfoWorker _worker;
    private MetaInformation _selectedItem;
    private boolean _endOk = false;
    
    public GameSelectionController(GameSelectionView view, GameSelectionModel model) {
        this._model = model;
        this._view = view;        
        
        _view.registerActionListener(_listener);
        _view.registerKeyListener(_listener);
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
    
    public boolean wasOk(){
        return ( _endOk );
    }
    
    public MetaInformation getSelectedMetaInformation() {
        return ( _selectedItem );
    }
    
    private class LoadMetaInfoWorker extends SwingWorker<Void, Integer> {
        private final String _title;
        private int _foundGamesCount;
        
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

    private class GameSelectionListener implements ActionListener, KeyListener, ListSelectionListener{
        
        private final ResourceBundle lblBundle = java.util.ResourceBundle.getBundle("LabelsBundle");
        private final ResourceBundle msgBundle = java.util.ResourceBundle.getBundle("MessagesBundle");
            
        public GameSelectionListener() {
        }

        public void actionPerformed(ActionEvent ae) {
            if ( ae.getActionCommand().equals(lblBundle.getString("OK"))) {
                _endOk = true;
            }
            else if ( ae.getActionCommand().equals(lblBundle.getString("CANCEL"))) {
                //Do nothing
            }
            
            _view.dispose();
        }

        public void keyTyped(KeyEvent ke) {
            //Do nothing
        }

        public void keyPressed(KeyEvent ke) {
            //Do nothing
        }

        public void keyReleased(KeyEvent ke) {
            updateGameList();
            _view.setMessageStatus(msgBundle.getString("METACRITIC_INFO_EXECUTED"));
        }        

        public void valueChanged(ListSelectionEvent lse) {
            if ( ((JList) lse.getSource()).getSelectedIndex() != -1 ) {
                _selectedItem = (MetaInformation) ((JList) lse.getSource()).getSelectedValue();
                _view.setAllowToAccept(true);

            }
            else {
                _view.setAllowToAccept(false);
            }            
        }
    }
    
}
