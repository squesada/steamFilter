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

import org.lajuderia.models.GameSelectionModel;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListModel;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionListener;

/**
 *
 * @author Sergio
 */
public class GameSelectionView extends JDialog{
    private javax.swing.JButton btAccept;
    private javax.swing.JButton btCancel;
    private javax.swing.JLabel lbStatus;
    private javax.swing.JList lstGames;
    private javax.swing.JTextField txtTitle;
    
    public GameSelectionView(JFrame parent) {
        super(parent,true);        
        this.initComponents();
        this.setLocationRelativeTo(parent);
    }

    private void initComponents() {
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("LabelsBundle");
        setResizable(false);  
        
        //North
        JPanel pnlNorth = new JPanel(new FlowLayout());            
            txtTitle = new javax.swing.JTextField();
            txtTitle.setColumns(20);
            btAccept = new javax.swing.JButton(bundle.getString("OK"));
            btCancel = new javax.swing.JButton(bundle.getString("CANCEL"));
            
            pnlNorth.add(new javax.swing.JLabel(bundle.getString("GAME_TITLE")));
            pnlNorth.add(txtTitle);
            pnlNorth.add(btAccept);
            pnlNorth.add(btCancel);
            
        this.add(pnlNorth, BorderLayout.NORTH);
        
        //Center
        JScrollPane jScrollPane1 = new javax.swing.JScrollPane();
        lstGames = new javax.swing.JList();
        lstGames.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane1.setViewportView(lstGames);
        
        this.add(jScrollPane1, BorderLayout.CENTER);
        
        //South
        lbStatus = new javax.swing.JLabel();
        lbStatus.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        this.add(lbStatus, BorderLayout.SOUTH);
        
        this.pack();
    }
    
    public void setEnteredTitle(String title){
        this.txtTitle.setText(title);
    }
    
    public String getEnteredTitle(){
        return ( txtTitle.getText() );
    }
    
    public void registerActionListener(ActionListener listener) {
        this.btAccept.addActionListener(listener);
        this.btCancel.addActionListener(listener);
    }
    
    public void registerDocumentListener(DocumentListener listener) {
        this.txtTitle.getDocument().addDocumentListener(listener);
    }
    
    public void registerListSelectionListener(ListSelectionListener listener) {
        this.lstGames.addListSelectionListener(listener);
    }
    
    public void setListModel(ListModel<GameSelectionModel> model) {
        lstGames.setModel(model);
    }
    
    public void setMessageStatus(String message) {
        this.lbStatus.setText(message);
    }
    
    public Object getSelectedItem(){
        return ( lstGames.getSelectedValue() );
    }
    
    public void setAllowToAccept(boolean enable){
        btAccept.setEnabled(enable);
    }
    
}
