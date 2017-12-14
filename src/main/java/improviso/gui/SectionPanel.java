/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package improviso.gui;

import improviso.ImprovisoException;
import java.io.IOException;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;
import javax.swing.JOptionPane;

/**
 *
 * @author LENOVO G40
 */
public class SectionPanel extends javax.swing.JPanel {
    final private CompositionController controller;
    /**
     * Creates new form SectionPanel
     * @param controller
     */
    public SectionPanel(CompositionController controller) {
        this.controller = controller;
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        listSections = new javax.swing.JList<>();
        panelSection = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        comboSectionType = new javax.swing.JComboBox<>();
        jPanel1 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        txtFixedSectionLengthMin = new javax.swing.JTextField();
        txtFixedSectionLengthMax = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txtSectionTempo = new javax.swing.JTextField();
        chkSectionInterruptTracks = new javax.swing.JCheckBox();
        btnSectionApply = new javax.swing.JButton();
        btnSectionPlay = new javax.swing.JButton();

        listSections.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        listSections.setName("listSections"); // NOI18N
        listSections.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                listSectionsValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(listSections);

        panelSection.setBorder(javax.swing.BorderFactory.createTitledBorder("Section"));

        jLabel2.setText("Type:");

        comboSectionType.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Fixed", "Variable" }));
        comboSectionType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboSectionTypeActionPerformed(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel3.setText("Length:");

        txtFixedSectionLengthMin.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtFixedSectionLengthMinFocusLost(evt);
            }
        });

        txtFixedSectionLengthMax.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtFixedSectionLengthMaxFocusLost(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtFixedSectionLengthMin, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(txtFixedSectionLengthMax, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(69, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtFixedSectionLengthMin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtFixedSectionLengthMax, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel4.setText("Tempo:");

        chkSectionInterruptTracks.setText("Interrupt tracks?");

        btnSectionApply.setText("Apply");
        btnSectionApply.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSectionApplyActionPerformed(evt);
            }
        });

        btnSectionPlay.setText("Play");
        btnSectionPlay.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSectionPlayActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelSectionLayout = new javax.swing.GroupLayout(panelSection);
        panelSection.setLayout(panelSectionLayout);
        panelSectionLayout.setHorizontalGroup(
            panelSectionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelSectionLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelSectionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(panelSectionLayout.createSequentialGroup()
                        .addGroup(panelSectionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelSectionLayout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(comboSectionType, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(panelSectionLayout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtSectionTempo, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(chkSectionInterruptTracks)
                            .addGroup(panelSectionLayout.createSequentialGroup()
                                .addComponent(btnSectionApply)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnSectionPlay)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        panelSectionLayout.setVerticalGroup(
            panelSectionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelSectionLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelSectionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(comboSectionType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelSectionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(txtSectionTempo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkSectionInterruptTracks)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(panelSectionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSectionApply)
                    .addComponent(btnSectionPlay))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelSection, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(panelSection, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 206, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(71, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    public void setSectionList(String[] list) {
        this.listSections.setListData(list);
    }
    
    private boolean isSectionTypeFixed() {
        return comboSectionType.getSelectedIndex() == 0;
    }
            
    private void loadSectionAttributes(String selectedValue) {
        SectionConfiguration config = controller.getSectionConfiguration(selectedValue);
        if (config.getType() == SectionConfiguration.SectionType.TYPE_FIXED) {
            comboSectionType.setSelectedIndex(0);
            txtFixedSectionLengthMin.setEnabled(true);
            txtFixedSectionLengthMin.setText(config.getLengthMin().toString());
            txtFixedSectionLengthMax.setEnabled(true);
            txtFixedSectionLengthMax.setText(config.getLengthMax().toString());
        } else {
            comboSectionType.setSelectedIndex(1);
            txtFixedSectionLengthMin.setEnabled(false);
            txtFixedSectionLengthMin.setText("");
            txtFixedSectionLengthMax.setEnabled(false);
            txtFixedSectionLengthMax.setText("");
        }
        txtSectionTempo.setText(config.getTempo().toString());
        chkSectionInterruptTracks.setSelected(config.getInterruptTracks());
    }
    
    private void listSectionsValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_listSectionsValueChanged
        loadSectionAttributes(listSections.getSelectedValue());
    }//GEN-LAST:event_listSectionsValueChanged

    private void comboSectionTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboSectionTypeActionPerformed
        if (isSectionTypeFixed()) {
            txtFixedSectionLengthMin.setEnabled(true);
            txtFixedSectionLengthMax.setEnabled(true);
        } else {
            txtFixedSectionLengthMin.setText("");
            txtFixedSectionLengthMin.setEnabled(false);
            txtFixedSectionLengthMax.setText("");
            txtFixedSectionLengthMax.setEnabled(false);
        }
    }//GEN-LAST:event_comboSectionTypeActionPerformed

    private void txtFixedSectionLengthMinFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtFixedSectionLengthMinFocusLost
        if (!txtFixedSectionLengthMin.getText().isEmpty() && txtFixedSectionLengthMax.getText().isEmpty()) {
            txtFixedSectionLengthMax.setText(txtFixedSectionLengthMin.getText());
        }
    }//GEN-LAST:event_txtFixedSectionLengthMinFocusLost

    private void txtFixedSectionLengthMaxFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtFixedSectionLengthMaxFocusLost
        if (!txtFixedSectionLengthMax.getText().isEmpty() && txtFixedSectionLengthMin.getText().isEmpty()) {
            txtFixedSectionLengthMin.setText(txtFixedSectionLengthMax.getText());
        }
    }//GEN-LAST:event_txtFixedSectionLengthMaxFocusLost

    private void btnSectionApplyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSectionApplyActionPerformed
        SectionConfiguration config = controller.getSectionConfiguration(listSections.getSelectedValue());
        if (isSectionTypeFixed()) {
            if (txtFixedSectionLengthMin.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "The section's minimum length must be informed.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (txtFixedSectionLengthMax.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "The section's maximum length must be informed.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int sectionLengthMin;
            int sectionLengthMax;
            try {
                sectionLengthMin = Integer.parseInt(txtFixedSectionLengthMin.getText());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Invalid value for the section's minimum length.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                sectionLengthMax = Integer.parseInt(txtFixedSectionLengthMax.getText());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Invalid value for the section's maximum length.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (sectionLengthMin > sectionLengthMax) {
                JOptionPane.showMessageDialog(null, "The section's maximum length must be greater or equal to the minimum length.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            config.setLengthMin(sectionLengthMin);
            config.setLengthMax(sectionLengthMax);
        }
        int tempo;
        try {
            tempo = Integer.parseInt(txtSectionTempo.getText());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "Invalid value for the tempo.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        config.setTempo(tempo);
        config.setInterruptTracks(chkSectionInterruptTracks.isSelected());
        controller.applyChangesToSection(listSections.getSelectedValue(), config);
    }//GEN-LAST:event_btnSectionApplyActionPerformed

    private void btnSectionPlayActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSectionPlayActionPerformed
        if (listSections.getSelectedValue() != null) {
            try {
                controller.playSection(listSections.getSelectedValue());
            } catch (InvalidMidiDataException | ImprovisoException | IOException | MidiUnavailableException ex) {
                JOptionPane.showMessageDialog(null, "Error playing section: " + ex.getMessage());
            }
        }
    }//GEN-LAST:event_btnSectionPlayActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnSectionApply;
    private javax.swing.JButton btnSectionPlay;
    private javax.swing.JCheckBox chkSectionInterruptTracks;
    private javax.swing.JComboBox<String> comboSectionType;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JList<String> listSections;
    private javax.swing.JPanel panelSection;
    private javax.swing.JTextField txtFixedSectionLengthMax;
    private javax.swing.JTextField txtFixedSectionLengthMin;
    private javax.swing.JTextField txtSectionTempo;
    // End of variables declaration//GEN-END:variables
}
