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

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.awt.event.KeyListener;
import java.util.ResourceBundle;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.lajuderia.beans.AbstractPlatformGame.PlatformGame;

/**
 *
 * @author Sergio
 */
public class AddGameView extends JDialog {
    private JTextField _txtTitle;
    private JTextField _txtGenre;
    private JComboBox<PlatformGame> _cbPlatform; 
    private JCheckBox _cbxCompleted;
    private JButton _btOK;
    private JButton _btCancel;
    
    public AddGameView(JFrame parent){
        super(parent, true);
        this.initComponents();
        this.setLocationRelativeTo(parent);
    }

    private void initComponents() {
        this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
            this.add(createFormPanel());
            this.add(createButtonPanel());
                
        this.pack();
    }

    private JPanel createFormPanel() {
        ResourceBundle labelsBundle =
            java.util.ResourceBundle.getBundle("LabelsBundle");
        
        JPanel pnlForm = new JPanel(new GridLayout(4, 2, 2, 2));
            pnlForm.setBorder(BorderFactory.createTitledBorder(labelsBundle.getString("GAME_FORM_TITLE")));

            //Title
            JLabel lblTitle;
                lblTitle = new JLabel(labelsBundle.getString("GAME_TITLE"));
                pnlForm.add(lblTitle);

                _txtTitle = new JTextField(10);
                _txtTitle.setName("TITLE");
                lblTitle.setLabelFor(_txtTitle);
                pnlForm.add(_txtTitle);

            //Genre
            JLabel lblGenre;
                lblGenre = new JLabel(labelsBundle.getString("GAME_GENRE"));
                pnlForm.add(lblGenre);

                _txtGenre = new JTextField(15);
                
                _txtGenre.setName("GENRE");
                lblGenre.setLabelFor(_txtGenre);
                pnlForm.add(_txtGenre);

            //Platform
            JLabel lblPlatform;
                lblPlatform = new JLabel(labelsBundle.getString("GAME_PLATFORM"));
                pnlForm.add(lblPlatform);

                _cbPlatform = new JComboBox<PlatformGame>(PlatformGame.values());
                _cbPlatform.setSelectedItem(PlatformGame.DEFAULT_PLATFORM);
                
                _cbPlatform.setName("PLATFORM");
                lblPlatform.setLabelFor(_cbPlatform);
                pnlForm.add(_cbPlatform);

            //Completed
            JLabel lblCompleted;
                lblCompleted = new JLabel(labelsBundle.getString("GAME_COMPLETED"));
                pnlForm.add(lblCompleted);

                _cbxCompleted = new JCheckBox();
                
                _cbxCompleted.setName("COMPLETED");
                lblCompleted.setLabelFor(_cbxCompleted);
                pnlForm.add(_cbxCompleted);
                
        return ( pnlForm );
    }

    private JPanel createButtonPanel() {
        ResourceBundle labelsBundle =
            java.util.ResourceBundle.getBundle("LabelsBundle");
        
        JPanel pnlButton = new JPanel(new FlowLayout());
            //Buttons
            _btOK = new JButton(labelsBundle.getString("OK"));
            pnlButton.add(_btOK);        
            _btCancel = new JButton(labelsBundle.getString("CANCEL"));
            pnlButton.add(_btCancel);
            
        return ( pnlButton );
    }
    
    
    public void registerActionListener(ActionListener listener){
        _btOK.addActionListener((ActionListener) listener);
        _btCancel.addActionListener((ActionListener) listener);
    }
    
    public void registerKeyListener(KeyListener listener){
        _txtTitle.addKeyListener(listener);
        _txtGenre.addKeyListener(listener);
    }
    
    public void registerItemListener(ItemListener listener){
        _cbxCompleted.addItemListener((ItemListener) listener);
        _cbPlatform.addItemListener(listener);
    } 
}
