/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package improviso.gui;
import improviso.*;
import java.io.*;
import java.awt.Dimension;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

/**
 *
 * @author User
 */
public class Main extends javax.swing.JFrame {
    final private SectionPanel sectionPanel;
    final private TrackPanel trackPanel;
    final private CompositionController controller = new CompositionController();
    /**
     * Creates new form Main
     */
    public Main() {
        initComponents();
        sectionPanel = new SectionPanel(this.controller);
        trackPanel = new TrackPanel(this.controller);
        tabs.add("Sections", sectionPanel);
        tabs.add("Tracks", trackPanel);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tabs = new javax.swing.JTabbedPane();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu2 = new javax.swing.JMenu();
        itemOpen = new javax.swing.JMenuItem();
        itemImportXML = new javax.swing.JMenuItem();
        itemSave = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        itemExit = new javax.swing.JMenuItem();
        jMenu3 = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        tabs.setTabLayoutPolicy(javax.swing.JTabbedPane.SCROLL_TAB_LAYOUT);
        tabs.setToolTipText("");

        jMenu2.setText("File");

        itemOpen.setText("Open file");
        itemOpen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                itemOpenActionPerformed(evt);
            }
        });
        jMenu2.add(itemOpen);

        itemImportXML.setText("Import from XML");
        itemImportXML.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                itemImportXMLActionPerformed(evt);
            }
        });
        jMenu2.add(itemImportXML);

        itemSave.setText("Save file");
        itemSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                itemSaveActionPerformed(evt);
            }
        });
        jMenu2.add(itemSave);
        jMenu2.add(jSeparator1);

        itemExit.setText("Exit");
        itemExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                itemExitActionPerformed(evt);
            }
        });
        jMenu2.add(itemExit);

        jMenuBar1.add(jMenu2);

        jMenu3.setText("Edit");
        jMenuBar1.add(jMenu3);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tabs, javax.swing.GroupLayout.DEFAULT_SIZE, 880, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tabs, javax.swing.GroupLayout.DEFAULT_SIZE, 557, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    public SectionConfiguration getSectionConfiguration(String sectionId) {
        return controller.getSectionConfiguration(sectionId);
    }
    
    public void applyChangesToSection(String sectionId, SectionConfiguration config) {
        controller.applyChangesToSection(sectionId, config);
    }
    
    public void playSection(String sectionId) {
        try {
            this.controller.playSection(sectionId);
        } catch (InvalidMidiDataException | ImprovisoException | IOException | MidiUnavailableException ex) {
            JOptionPane.showMessageDialog(rootPane, "Error playing section: " + ex.getMessage());
        }
    }
    
    private void itemImportXMLActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_itemImportXMLActionPerformed
        importXMLFile();
    }//GEN-LAST:event_itemImportXMLActionPerformed

    private void itemExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_itemExitActionPerformed
        System.exit(0);
    }//GEN-LAST:event_itemExitActionPerformed

    private void itemSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_itemSaveActionPerformed
        saveFile();
    }//GEN-LAST:event_itemSaveActionPerformed

    private void itemOpenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_itemOpenActionPerformed
        openFile();
    }//GEN-LAST:event_itemOpenActionPerformed

    private void importXMLFile() {
        final JFileChooser fc = new JFileChooser();
        fc.setPreferredSize(new Dimension(1200, 800));
        fc.setCurrentDirectory(new File("."));
        if(fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                controller.importXML(fc.getSelectedFile().getAbsolutePath());
                this.sectionPanel.setSectionList(controller.getSectionList());
            } catch (ImprovisoException ex) {
                JOptionPane.showMessageDialog(rootPane, "Error creating composition: " + ex.getMessage());
            } catch (ParserConfigurationException ex) {
                JOptionPane.showMessageDialog(rootPane, "Configuration error: " + ex.getMessage());
            } catch (SAXException ex) {
                JOptionPane.showMessageDialog(rootPane, "Error parsing XML file: " + ex.getMessage());
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(rootPane, "Error reading file: " + ex.getMessage());
            }
        };
    }
    
    private void openFile() {
        try {
            final JFileChooser fc = new JFileChooser();
            fc.setPreferredSize(new Dimension(1200, 800));
            fc.setCurrentDirectory(new File("."));
            if(fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                controller.openComposition(fc.getSelectedFile().getAbsolutePath());
                this.sectionPanel.setSectionList(controller.getSectionList());
            }
        } catch (ClassNotFoundException | IOException ex) {
            System.out.println(ex);
            JOptionPane.showMessageDialog(rootPane, "Error opening composition: " + ex.getMessage());
        }
    }
    
    private void saveFile() {
        if (controller.isCompositionLoaded()) {
            try {
                final JFileChooser fc = new JFileChooser();
                fc.setPreferredSize(new Dimension(1200, 800));
                fc.setCurrentDirectory(new File("."));
                if(fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                    controller.saveComposition(fc.getSelectedFile().getAbsolutePath());
                }
            } catch (IOException ex) {
                System.out.println(ex);
                JOptionPane.showMessageDialog(rootPane, "Error saving composition: " + ex.getMessage());
            }
        }
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Main().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem itemExit;
    private javax.swing.JMenuItem itemImportXML;
    private javax.swing.JMenuItem itemOpen;
    private javax.swing.JMenuItem itemSave;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JTabbedPane tabs;
    // End of variables declaration//GEN-END:variables

}
