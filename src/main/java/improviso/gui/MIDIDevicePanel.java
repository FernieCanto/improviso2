/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package improviso.gui;

import improviso.ImprovisoException;
import java.awt.Dimension;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 *
 * @author cfern
 */
public class MIDIDevicePanel extends javax.swing.JPanel {
    private CompositionController controller;
    MIDIDeviceListModel model = new MIDIDeviceListModel();

    /**
     * Creates new form MIDIDevicePanel
     */
    public MIDIDevicePanel(CompositionController controller) {
        this.controller = controller;
        initComponents();
        deviceList.setModel(model);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        labelSelectedDevice = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        deviceList = new javax.swing.JList<>();
        btnSaveMIDI = new javax.swing.JButton();

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("MIDI Devices"));

        jLabel1.setText("Selected device:");

        labelSelectedDevice.setText("None");

        jLabel3.setText("Available devices");

        deviceList.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        deviceList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        deviceList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                deviceListValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(deviceList);

        btnSaveMIDI.setText("Save MIDI file");
        btnSaveMIDI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveMIDIActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(labelSelectedDevice))
                            .addComponent(jLabel3)
                            .addComponent(btnSaveMIDI))
                        .addGap(0, 499, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(labelSelectedDevice))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 195, Short.MAX_VALUE)
                .addComponent(btnSaveMIDI)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void deviceListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_deviceListValueChanged
        labelSelectedDevice.setText(model.getMidiInfoAt(deviceList.getSelectedIndex()).getName());
        try {
            this.controller.setDeviceInfo(model.getMidiInfoAt(deviceList.getSelectedIndex()));
        } catch (MidiUnavailableException ex) {
            JOptionPane.showMessageDialog(null, "Unable to select MIDI device.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_deviceListValueChanged

    private void btnSaveMIDIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveMIDIActionPerformed
        if (controller.isCompositionLoaded()) {
            try {
                final JFileChooser fc = new JFileChooser();
                fc.setPreferredSize(new Dimension(1200, 800));
                fc.setCurrentDirectory(new File("."));
                if(fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                    controller.saveMIDI(fc.getSelectedFile().getAbsolutePath());
                }
            } catch (IOException ex) {
                System.out.println(ex);
                JOptionPane.showMessageDialog(this, "Error saving composition: " + ex.getMessage());
            } catch (InvalidMidiDataException ex) {
                Logger.getLogger(MIDIDevicePanel.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ImprovisoException ex) {
                Logger.getLogger(MIDIDevicePanel.class.getName()).log(Level.SEVERE, null, ex);
            } catch (MidiUnavailableException ex) {
                Logger.getLogger(MIDIDevicePanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_btnSaveMIDIActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnSaveMIDI;
    private javax.swing.JList<String> deviceList;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel labelSelectedDevice;
    // End of variables declaration//GEN-END:variables
}
