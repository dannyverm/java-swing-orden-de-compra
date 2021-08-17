/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * Catedra.java
 *
 * Created on 4/10/2012, 10:11:31 AM
 */
package PaqFormulario;

import clases.Conexion;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.sql.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.InputMap;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Danny Veron
 */
public final class FrmModalidad extends javax.swing.JDialog {

    private Connection cn = null;
    private Conexion con;
    private ResultSet rs = null;
    private ResultSet rsSer = null;
    private Statement st = null;
    public String SerCod = null;
    private String PasUsu = null;
    public boolean botonagregar = false;
    public boolean botonmodificar = false;
    public boolean botonborrar = false;
    private ResultSet rsCodMod;
    private ArrayList arCodigo;

    /**
     * Creates new form Catedra
     */
    public FrmModalidad(java.awt.Dialog parent, boolean modal) {
        super(parent, modal);
        initComponents();
        botonEnter();
        centrar();
        arCodigo = new ArrayList();
        cargaCboMod();
        ActualizarGrilla();
        btnAgr.grabFocus();
    }

    private void conexion() {
        con = new Conexion(AcesoUsuario.host, AcesoUsuario.base, AcesoUsuario.user, AcesoUsuario.pass);
    }

    private void cargaCboMod() {
        conexion();
        try {
            String sql = "SELECT * FROM tipo_modalidad ORDER BY tip_des";
            rsCodMod = con.Consulta(sql);
            cbotip.removeAllItems();
            cbotip.addItem("");
            while (rsCodMod.next()) {
                try {
                    arCodigo.add(rsCodMod.getString("tip_cod"));
                    cbotip.addItem(rsCodMod.getString("tip_des"));
                } catch (SQLException ex) {
                    Logger.getLogger(OC.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            //cbopago.addItem("");
            con.CerrarConexion();
        } catch (SQLException ex) {
            Logger.getLogger(OC.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void centrar() {
        Dimension pantalla = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frame = this.getSize();
        setLocation((pantalla.width / 2 - (frame.width / 2)), pantalla.height / 2 - (frame.height / 2));
    }

    private void botonEnter() {
        InputMap map = new InputMap();
        map.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false), "pressed");
        map.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, true), "released");
        btnAgr.setInputMap(0, map);
        this.btnCan.setInputMap(0, map);
        this.btnGra.setInputMap(0, map);
        this.btnMod.setInputMap(0, map);
        this.btnSal.setInputMap(0, map);
    }

    private void vaciarTabla() {
        int i = 0;

        while (i != TablaCatedra.getRowCount()) {
            TablaCatedra.setValueAt("", i, 0);
            TablaCatedra.setValueAt("", i, 1);
            i++;
        }
    }

    private void ActualizarGrilla() {
        String Nombre = null;
        try {

            int c = 0;
            vaciarTabla();
            String sql = "SELECT *"
                    + " FROM modalidad where estado is null"
                    + " ORDER BY mod_des";
            //System.out.println(sql);
            conexion();
            rs = con.Consulta(sql);
            if (rs.next() == true) {
                rs.beforeFirst();
                while (rs.next()) {
                    if (TablaCatedra.getRowCount() <= rs.getRow() - 1) {
                        DefaultTableModel tm = (DefaultTableModel) TablaCatedra.getModel();
                        tm.addRow(new Object[]{"", ""});
                        TablaCatedra.setModel(tm);
                    }
                    Nombre = rs.getString("mod_des");
                    TablaCatedra.setValueAt(rs.getString(1), c, 0);
                    TablaCatedra.setValueAt(Nombre, c, 1);

                    c++;
                }
            }
            con.CerrarConexion();
        } catch (SQLException ex) {
            Logger.getLogger(FrmModalidad.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private boolean comparar(String sql) {
        boolean r = false;
        try {
            conexion();
            rs = con.Consulta(sql);
            if (rs.next()) {
                r = true;
            }
            con.CerrarConexion();
        } catch (SQLException ex) {
            Logger.getLogger(FrmModalidad.class.getName()).log(Level.SEVERE, null, ex);
        }
        return r;
    }

    private boolean validar(String query) {
        boolean ban = true;
        try {
            // System.out.println(query);
            conexion();
            rs = con.Consulta(query);
            if (rs.next() == false) {
                ban = false;
            }
            con.CerrarConexion();
        } catch (SQLException ex) {
            Logger.getLogger(FrmModalidad.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ban;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel = new javax.swing.JPanel();
        btnAgr = new javax.swing.JButton();
        btnMod = new javax.swing.JButton();
        btnGra = new javax.swing.JButton();
        btnCan = new javax.swing.JButton();
        btnSal = new javax.swing.JButton();
        jLabel13 = new javax.swing.JLabel();
        txtNom = new javax.swing.JTextField();
        txtCod = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        cbotip = new javax.swing.JComboBox();
        jLabel19 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        TablaCatedra = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Modalidad de Contratacion");

        jPanel.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        btnAgr.setText("Agregar"); // NOI18N
        btnAgr.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAgrActionPerformed(evt);
            }
        });
        btnAgr.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                btnAgrKeyPressed(evt);
            }
        });

        btnMod.setText("Modificar"); // NOI18N
        btnMod.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnModActionPerformed(evt);
            }
        });
        btnMod.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                btnModKeyPressed(evt);
            }
        });

        btnGra.setText("Grabar"); // NOI18N
        btnGra.setEnabled(false);
        btnGra.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGraActionPerformed(evt);
            }
        });
        btnGra.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                btnGraKeyPressed(evt);
            }
        });

        btnCan.setText("Cancelar"); // NOI18N
        btnCan.setEnabled(false);
        btnCan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCanActionPerformed(evt);
            }
        });
        btnCan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                btnCanKeyPressed(evt);
            }
        });

        btnSal.setText("Cerrar"); // NOI18N
        btnSal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalActionPerformed(evt);
            }
        });
        btnSal.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                btnSalKeyPressed(evt);
            }
        });

        jLabel13.setText("Codigo"); // NOI18N

        txtNom.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        txtNom.setEnabled(false);
        txtNom.setNextFocusableComponent(btnGra);
        txtNom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNomActionPerformed(evt);
            }
        });
        txtNom.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtNomFocusLost(evt);
            }
        });

        txtCod.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        txtCod.setEnabled(false);
        txtCod.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCodActionPerformed(evt);
            }
        });

        jLabel18.setText("Descripción"); // NOI18N

        cbotip.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cbotip.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        cbotip.setEnabled(false);
        cbotip.setNextFocusableComponent(txtNom);

        jLabel19.setText("Tipo de Modalidad"); // NOI18N

        javax.swing.GroupLayout jPanelLayout = new javax.swing.GroupLayout(jPanel);
        jPanel.setLayout(jPanelLayout);
        jPanelLayout.setHorizontalGroup(
            jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelLayout.createSequentialGroup()
                .addGroup(jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(btnAgr, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnMod, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnGra, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnCan, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnSal, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel18)
                            .addComponent(jLabel13))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtNom, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanelLayout.createSequentialGroup()
                                .addComponent(txtCod, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel19)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbotip, javax.swing.GroupLayout.PREFERRED_SIZE, 283, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap())
        );
        jPanelLayout.setVerticalGroup(
            jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelLayout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addGroup(jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtCod, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13)
                    .addComponent(cbotip, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel19))
                .addGap(4, 4, 4)
                .addGroup(jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtNom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel18))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(btnAgr, javax.swing.GroupLayout.DEFAULT_SIZE, 33, Short.MAX_VALUE)
                    .addComponent(btnMod, javax.swing.GroupLayout.DEFAULT_SIZE, 33, Short.MAX_VALUE)
                    .addComponent(btnGra, javax.swing.GroupLayout.DEFAULT_SIZE, 33, Short.MAX_VALUE)
                    .addComponent(btnCan, javax.swing.GroupLayout.DEFAULT_SIZE, 33, Short.MAX_VALUE)
                    .addComponent(btnSal, javax.swing.GroupLayout.DEFAULT_SIZE, 33, Short.MAX_VALUE))
                .addGap(39, 39, 39))
        );

        TablaCatedra.setAutoCreateRowSorter(true);
        TablaCatedra.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "Codigo", "Descripcion"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        TablaCatedra.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                TablaCatedraMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(TablaCatedra);
        TablaCatedra.getColumnModel().getColumn(0).setPreferredWidth(80);
        TablaCatedra.getColumnModel().getColumn(0).setMaxWidth(80);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane1)
                    .addComponent(jPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnAgrActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgrActionPerformed
        try {
            botonagregar = true;
            String sql = "Select ifnull(max(mod_cod),0)+1 codigo From modalidad";
            //System.out.println(sql);
            conexion();
            rs = con.Consulta(sql);
            rs.next();
            txtCod.setText(rs.getString("codigo"));
            btnAgr.setEnabled(false);
            btnMod.setEnabled(false);
            btnCan.setEnabled(true);
            txtNom.setEnabled(true);
            btnGra.setEnabled(true);
            cbotip.setEnabled(true);
            cbotip.grabFocus();
            con.CerrarConexion();
        } catch (SQLException ex) {
            Logger.getLogger(FrmModalidad.class.getName()).log(Level.SEVERE, null, ex);
        }
}//GEN-LAST:event_btnAgrActionPerformed

    private void btnAgrKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnAgrKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_RIGHT) {
            this.btnMod.grabFocus();
        } else if (evt.getKeyCode() == KeyEvent.VK_LEFT) {
            this.btnSal.grabFocus();
        }
}//GEN-LAST:event_btnAgrKeyPressed

    private void btnModActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnModActionPerformed
        botonmodificar = true;
//        this.txtCod.setEnabled(true);
//        this.txtCod.grabFocus();
        btnAgr.setEnabled(false);
        btnMod.setEnabled(false);
        btnCan.setEnabled(true);
        btnGra.setEnabled(true);
        txtNom.setEnabled(true);
        cbotip.setEnabled(true);
        cbotip.grabFocus();
}//GEN-LAST:event_btnModActionPerformed

    private void btnModKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnModKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_RIGHT) {
            this.btnSal.grabFocus();
        } else if (evt.getKeyCode() == KeyEvent.VK_LEFT) {
            btnAgr.grabFocus();
        }
}//GEN-LAST:event_btnModKeyPressed

    private void btnGraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGraActionPerformed
        if (cbotip.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un Tipo de Modalidad");
            cbotip.grabFocus();
        } else {
            int res = JOptionPane.showConfirmDialog(null, "Realmente, ¿Desea Grabar"
                    + " los Datos?", "Mensaje", JOptionPane.OK_CANCEL_OPTION);

            if (res == JOptionPane.OK_OPTION) {
                try {

                    if (botonagregar == true) {

                        String sql = "INSERT INTO modalidad (mod_cod,tip_cod,mod_des)"
                                + " VALUES (" + this.txtCod.getText() + "," + arCodigo.get(cbotip.getSelectedIndex() - 1) + ",'" + this.txtNom.getText()
                                + "')";
                        //System.out.println(sql);
                        conexion();
                        con.Guardar(sql);
                        con.CerrarConexion();
                        OC.DatoBotonBusqueda = this.txtNom.getText();
                        botonagregar = false;
                        this.dispose();

                    }
                    if (botonmodificar == true) {
                        conexion();
                        String update = "UPDATE modalidad SET "
                                + "tip_cod=" + arCodigo.get(cbotip.getSelectedIndex() - 1)
                                + ",mod_des='" + this.txtNom.getText()
                                + "' WHERE mod_cod=" + this.txtCod.getText();
                        //System.out.println(update);
                        con.Guardar(update);
                        con.CerrarConexion();
                        OC.DatoBotonBusqueda = this.txtNom.getText();
                        botonmodificar = false;
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(FrmModalidad.class.getName()).log(Level.SEVERE, null, ex);
                }

                this.dispose();
                this.btnCan.doClick();

            } else {
                this.btnCan.doClick();
            }
        }
}//GEN-LAST:event_btnGraActionPerformed

    private void btnGraKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnGraKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_RIGHT) {
            this.btnCan.grabFocus();
        }
}//GEN-LAST:event_btnGraKeyPressed

    private void btnCanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCanActionPerformed
        botonagregar = false;
        botonborrar = false;
        botonmodificar = false;
        btnCan.setEnabled(false);
        btnGra.setEnabled(false);
        btnAgr.setEnabled(true);
        btnMod.setEnabled(true);
        txtCod.setText("");
        txtNom.setText("");
        txtNom.setEnabled(false);
        cbotip.setSelectedIndex(0);
        cbotip.setEnabled(false);
        btnAgr.grabFocus();
}//GEN-LAST:event_btnCanActionPerformed

    private void btnCanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnCanKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_RIGHT) {
            this.btnSal.grabFocus();
        } else if (evt.getKeyCode() == KeyEvent.VK_LEFT) {
            this.btnGra.grabFocus();
        }
}//GEN-LAST:event_btnCanKeyPressed

    private void btnSalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalActionPerformed
        this.dispose();
}//GEN-LAST:event_btnSalActionPerformed

    private void btnSalKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnSalKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_LEFT) {
            this.btnMod.grabFocus();
        }
}//GEN-LAST:event_btnSalKeyPressed

    private void txtNomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNomActionPerformed
        btnGra.setEnabled(true);
        btnGra.grabFocus();
}//GEN-LAST:event_txtNomActionPerformed

    private void txtCodActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCodActionPerformed
        String sql1 = "SELECT * FROM modalidad WHERE  mod_cod=" + txtCod.getText();

        if (FrmPrincipal.isNumeric(txtCod.getText())) {
            if (validar(sql1) == true) {

                if (botonmodificar == true) {
                    try {
                        String sql = "Select * FROM modalidad "
                                + "WHERE mod_cod=" + txtCod.getText();
                        //System.out.println(sql);
                        rs = con.Consulta(sql);
                        rs.next();
                        txtNom.setText(rs.getString("mod_des"));
                        txtNom.setEnabled(true);
                        txtNom.grabFocus();
                    } catch (SQLException ex) {
                        Logger.getLogger(FrmModalidad.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                this.txtCod.setEnabled(false);
            } else {
                JOptionPane.showMessageDialog(null, "El Codigo No Existe");
                this.txtCod.setText("");
                this.txtCod.grabFocus();
            }
        } else {
            JOptionPane.showMessageDialog(null, "Solo puede Ingresar Numeros");
            this.txtCod.setText("");
            this.txtCod.grabFocus();
        }
}//GEN-LAST:event_txtCodActionPerformed

    private void txtNomFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtNomFocusLost
        String sql = "SELECT * FROM modalidad WHERE mod_des='" + txtNom.getText() + "'";
        if (!txtNom.getText().isEmpty()) {
            if (comparar(sql) == false && botonmodificar == true) {
                txtNom.setText(txtNom.getText().toUpperCase());
                btnGra.setEnabled(true);
                btnGra.grabFocus();
            } else if (comparar(sql) == true && botonagregar == true) {
                JOptionPane.showMessageDialog(this, "La descripción ya existe!!!", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                txtNom.setText(txtNom.getText().toUpperCase());
            }
        }
    }//GEN-LAST:event_txtNomFocusLost

    private void TablaCatedraMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_TablaCatedraMouseClicked
        try {
            conexion();
            String sql = "Select m.mod_cod,m.mod_des,t.tip_des FROM modalidad m,tipo_modalidad t "
                    + "WHERE m.tip_cod=t.tip_cod AND m.mod_cod=" + TablaCatedra.getValueAt(TablaCatedra.getSelectedRow(), 0);
            System.out.println(sql);
            rs = con.Consulta(sql);
            if (rs.next()) {
                txtCod.setText(rs.getString("mod_cod"));
                txtNom.setText(rs.getString("mod_des"));
                cbotip.setSelectedItem(rs.getString("tip_des"));
            }
            con.CerrarConexion();
        } catch (SQLException ex) {
            Logger.getLogger(FrmModalidad.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_TablaCatedraMouseClicked

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                FrmModalidad dialog = new FrmModalidad(new javax.swing.JDialog(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable TablaCatedra;
    public static javax.swing.JButton btnAgr;
    private javax.swing.JButton btnCan;
    private javax.swing.JButton btnGra;
    private javax.swing.JButton btnMod;
    private javax.swing.JButton btnSal;
    private javax.swing.JComboBox cbotip;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JPanel jPanel;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField txtCod;
    private javax.swing.JTextField txtNom;
    // End of variables declaration//GEN-END:variables
}
