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
package org.lajuderia.views;

import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import org.lajuderia.beans.Game;

/**
 *
 * @author Sergio
 */
public class ShowGameView extends JDialog {
    private final Game _game;
    private final int TEXTAREA_WIDTH = 100;
    private final int TEXTAREA_HEIGHT = 100 ;
    
    public ShowGameView(JFrame parent, Game game) {
        super(parent, true);
        
        this._game = game;
    
        initComponents();
        
        this.setLocationRelativeTo(parent);
    }

    private void initComponents() {
        setResizable(false);
        
        this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
            this.add(createMainInfoPanel());
            this.add(createPlatformPanel());
            this.add(createMetaInformationPanel());
            
        this.pack();
    }

    private JPanel createMainInfoPanel() {
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("LabelsBundle");
        
        JPanel pnl;
            pnl = new JPanel(new GridLayout(3, 2, 2, 2));
            pnl.setBorder(BorderFactory.createTitledBorder(bundle.getString("GAME_FORM_TITLE")));
            
            addLabeledTextFieldToPanel(pnl, bundle.getString("GAME_TITLE"), _game.getTitle());
            addLabeledTextFieldToPanel(pnl, bundle.getString("GAME_GENRE"), _game.getGenre());
            addLabeledCheckBoxFieldToPanel(pnl, bundle.getString("GAME_COMPLETED"), _game.isCompleted());
        
        return ( pnl );            
    }
    
    private JPanel createPlatformPanel() {        
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("LabelsBundle");
        
        JPanel pnl = new JPanel(new GridLayout(3, 2, 2, 2));
            pnl.setBorder(BorderFactory.createTitledBorder(bundle.getString("GAME_PLATFORM_TITLE")));            
            addLabeledTextFieldToPanel(pnl, bundle.getString("GAME_TITLE"), _game.getAssociatedGame().getTitle());
            addLabeledTextFieldToPanel(pnl, bundle.getString("GAME_PLATFORM"), _game.getAssociatedGame().getPlatform().toString());
            addLabeledTextFieldToPanel(pnl, bundle.getString("GAME_PLATFORM_ID"), Integer.toString(_game.getAssociatedGame().getId()));
        
        return ( pnl );
    }
    
    private JPanel createMetaInformationPanel() {
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("LabelsBundle");
        
        JPanel pnl;
            if ( _game.hasMetaInformation() ) {
                pnl = new JPanel(new GridLayout(5, 2, 2, 2));
                pnl.setBorder(BorderFactory.createTitledBorder(bundle.getString("GAME_METAINFORMATION_TITLE")));
                addLabeledTextFieldToPanel(pnl, bundle.getString("GAME_TITLE"), _game.getMetaInformation().getTitle());
                addLabeledTextFieldToPanel(pnl, bundle.getString("GAME_GENRE"), _game.getMetaInformation().getGenre());
                addLabeledTextFieldToPanel(pnl, bundle.getString("GAME_METASCORE"), Integer.toString(_game.getMetaInformation().getMetascore()));
                addLabeledTextFieldToPanel(pnl, bundle.getString("GAME_USERSCORE"), Integer.toString(_game.getMetaInformation().getUserscore()));                
            }
            else {
                pnl = new JPanel();
                pnl.add(new JLabel(java.util.ResourceBundle.getBundle("MessagesBundle").getString("GAME_HAS_NO_METAINFORMATION")));
            }
        
        return ( pnl );
    }
    
    private void addLabeledTextFieldToPanel(JPanel pnl, String label, String text) {
        JLabel lblTitle;
            lblTitle = new JLabel(label);
            pnl.add(lblTitle);

            JTextField textField = new JTextField();
                textField.setText(text);
                textField.setEditable(false);
                lblTitle.setLabelFor(textField);
                pnl.add(textField);
    }
    
    private void addLabeledCheckBoxFieldToPanel(JPanel pnl, String label, boolean selected) {
        JLabel lblTitle;
            lblTitle = new JLabel(label);
            pnl.add(lblTitle);

            JCheckBox checkBox = new JCheckBox();
                checkBox.setSelected(selected);
                checkBox.setEnabled(false);
                lblTitle.setLabelFor(checkBox);
                pnl.add(checkBox);
    }
}
