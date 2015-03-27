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

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JCheckBox;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import org.lajuderia.beans.AbstractPlatformGame;
import org.lajuderia.beans.PlatformGameFactory;
import org.lajuderia.models.AddGameModel;
import org.lajuderia.views.AddGameView;

/**
 * Controller class to add a game
 * @author Sergio
 */
public class AddGameController {
    private final AddGameView _view;
    private final AddGameModel _model;
    private boolean _wasOk = false;
    
    /**
     * Constructor
     * @param view the related view
     * @param model the related model
     */
    public AddGameController(AddGameView view, AddGameModel model) {
        this._view = view;
        this._model = model;
        
        AddGameListener listener = new AddGameListener();
            this._view.registerActionListener(listener);
            this._view.registerDocumentListener(listener);
            this._view.registerItemListener(listener);
    }
    
    /**
     * Gets true if the view was closed by pressing OK button
     * @return Boolean
     */
    public boolean wasOk() {
        return ( _wasOk );
    }
    
    /**
     * Listener class to add a game
     */
    private class AddGameListener implements ActionListener, DocumentListener, ItemListener {

        public void actionPerformed(ActionEvent ae) {
            ResourceBundle lblBundle =
                    java.util.ResourceBundle.getBundle("LabelsBundle");
            
            String command = ae.getActionCommand();
                if (command.equals(lblBundle.getString("OK"))) {
                    _wasOk = true;
                }
                else {
                    _model.clearGame();
                }
                
                _view.dispose();
        }

        public void keyTyped(KeyEvent ke) {
            // Do nothing
        }

        public void keyPressed(KeyEvent ke) {
            // Do nothing
        }

        public void keyReleased(KeyEvent ke) {
            Component source = (Component) ke.getSource();
            
            if ( source.getName().equals("TITLE") ) {
                _model.getGame().setTitle(((JTextField) source).getText().trim());
            }
            else if ( source.getName().equals("GENRE")) {
                _model.getGame().setGenre(((JTextField) source).getText().trim());
            }
        }

        public void itemStateChanged(ItemEvent ie) {
            Component source = (Component) ie.getSource();
            
            if ( (source.getName().equals("PLATFORM")) && (ie.getStateChange() == ItemEvent.SELECTED) )  {
                _model.getGame().setAssociatedGame(PlatformGameFactory.create((AbstractPlatformGame.PlatformGame) ie.getItem()) );
            }
            else if ( source.getName().equals("COMPLETED")) {
                _model.getGame().setCompleted(((JCheckBox) ie.getItem()).isSelected() );
            }
        }   

        public void insertUpdate(DocumentEvent de) {
            updateModel(de);
        }

        public void removeUpdate(DocumentEvent de) {
            updateModel(de);
        }

        public void changedUpdate(DocumentEvent de) {
            updateModel(de);
        }

        private void updateModel(DocumentEvent e) {
            Document doc;
                doc = e.getDocument();
                
            String text = null ;
            try {
                text = doc.getText(0, doc.getLength());
            } catch (BadLocationException ex) {
            }
                
            if ( doc.getProperty("field").equals(AddGameView.TITLE_MODEL_FIELD) ) {
                _model.getGame().setTitle(text);
            }
            else if ( doc.getProperty("field").equals(AddGameView.GENRE_MODEL_FIELD) ) {
                _model.getGame().setGenre(text);
            }
        }
    }
}
