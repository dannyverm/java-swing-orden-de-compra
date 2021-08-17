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
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import java.sql.*;
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
public final class Catedra extends javax.swing.JDialog {

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

    /**
     * Creates new form Catedra
     */
    public Catedra(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        con = new Conexion(AcesoUsuario.host, AcesoUsuario.base, AcesoUsuario.user, AcesoUsuario.pass);
        botonEnter();
        centrar();
        ActualizarGrilla();
        btnAgr.grabFocus();
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
            TablaCatedra.setValueAt("", i, 2);
            i++;
        }
    }

    private boolean validaServicio() {
        boolean ban = false;
        try {
            String sql = "SELECT * FROM catedra WHERE cat_nombre='" + txtNom.getText()
                    + "' AND cat_sede='" + cbosede.getSelectedItem() + "' and estado is null";
            System.out.println(sql);
            ResultSet res=con.Consulta(sql);
            if (res.next()) {
                ban = true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(Catedra.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ban;
    }

    private void ActualizarGrilla() {
        String Nombre = null;
        try {

            int c = 0;
            vaciarTabla();
            String sql = "SELECT *"
                    + " FROM catedra where estado is null"
                    + " ORDER BY cat_nombre";
            //System.out.println(sql);
            rs = con.Consulta(sql);
            if (rs.next() == true) {
                rs.beforeFirst();
                while (rs.next()) {
                    if (TablaCatedra.getRowCount() <= rs.getRow() - 1) {
                        DefaultTableModel tm = (DefaultTableModel) TablaCatedra.getModel();
                        tm.addRow(new Object[]{new String(""), new String(""), new String("")});
                        TablaCatedra.setModel(tm);
                    }
                    Nombre = rs.getString("cat_nombre");
                    TablaCatedra.setValueAt(rs.getString(1), c, 0);
                    TablaCatedra.setValueAt(Nombre, c, 1);
                    TablaCatedra.setValueAt(rs.getString(3), c, 2);
                    c++;
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(Catedra.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private boolean comparar(String sql) {
        boolean r = false;
        try {
            rs = con.Consulta(sql);
            if (rs.next()) {
                r = true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(Catedra.class.getName()).log(Level.SEVERE, null, ex);
        }
        return r;
    }

    private boolean validar(String query) {
        boolean ban = true;
        try {
            // System.out.println(query);

            rs = con.Consulta(query);
            if (rs.next() == false) {
                ban = false;
            }
        } catch (SQLException ex) {
            Logger.getLogger(Catedra.class.getName()).log(Level.SEVERE, null, ex);
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
        jLabel1 = new javax.swing.JLabel();
        cbosede = new javax.swing.JComboBox();
        jScrollPane1 = new javax.swing.JScrollPane();
        TablaCatedra = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Servicio");

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
        txtNom.setNextFocusableComponent(cbosede);
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

        jLabel18.setText("Nombre"); // NOI18N

        jLabel1.setText("Sede");

        cbosede.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "", "SL", "SJ" }));
        cbosede.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        cbosede.setNextFocusableComponent(btnGra);
        cbosede.setOpaque(false);
        cbosede.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbosedeItemStateChanged(evt);
            }
        });
        cbosede.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                cbosedeKeyPressed(evt);
            }
        });

        javax.swing.GroupLayout jPanelLayout = new javax.swing.GroupLayout(jPanel);
        jPanel.setLayout(jPanelLayout);
        jPanelLayout.setHorizontalGroup(
            jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelLayout.createSequentialGroup()
                        .addComponent(btnAgr, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnMod, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnGra, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnCan, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnSal, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanelLayout.createSequentialGroup()
                        .addComponent(jLabel13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtCod, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(13, 13, 13)
                        .addComponent(jLabel18)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtNom, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbosede, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanelLayout.setVerticalGroup(
            jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelLayout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addGroup(jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtCod, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel13))
                    .addGroup(jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtNom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel18)
                        .addComponent(jLabel1)
                        .addComponent(cbosede, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(32, 32, 32)
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
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Codigo", "Nombre", "Sede"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnAgrActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgrActionPerformed
        try {
            botonagregar = true;
            String sql = "Select ifnull(max(cat_codigo),0)+1 codigo From catedra";
//            System.out.println(sql);
            rs = con.Consulta(sql);
            rs.next();
            txtCod.setText(rs.getString("codigo"));
            btnAgr.setEnabled(false);
            btnMod.setEnabled(false);
            btnCan.setEnabled(true);
            txtNom.setEnabled(true);
            txtNom.grabFocus();
        } catch (SQLException ex) {
            Logger.getLogger(Catedra.class.getName()).log(Level.SEVERE, null, ex);
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
        txtNom.grabFocus();
}//GEN-LAST:event_btnModActionPerformed

    private void btnModKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnModKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_RIGHT) {
            this.btnSal.grabFocus();
        } else if (evt.getKeyCode() == KeyEvent.VK_LEFT) {
            btnAgr.grabFocus();
        }
}//GEN-LAST:event_btnModKeyPressed

    private void btnGraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGraActionPerformed
        if (validaServicio() == true) {
            JOptionPane.showMessageDialog(this, "La descipción ya existe!!! \nNo se guardaron los datos", "Error", JOptionPane.ERROR_MESSAGE);
        } else {

            int res = JOptionPane.showConfirmDialog(null, "Realmente, ¿Desea Grabar"
                    + " los Datos?", "Mensaje", JOptionPane.OK_CANCEL_OPTION);

            if (res == JOptionPane.OK_OPTION) {
                if (botonagregar == true) {
                    String sql = "INSERT INTO catedra (cat_codigo,cat_nombre,cat_sede)"
                            + " VALUES (" + this.txtCod.getText() + ",'" + this.txtNom.getText() + "','"
                            + cbosede.getSelectedItem() + "')";
                    //System.out.println(sql);
                    con.Guardar(sql);
                    ActualizarGrilla();
                    botonagregar = false;
                    if (OC.pulsaboton == true) {
                        OC.DatoBotonBusqueda = txtNom.getText();
                        this.dispose();
                    }
                }
                if (botonmodificar == true) {
                    String update = "UPDATE catedra SET "
                            + "cat_nombre='" + this.txtNom.getText()
                            + "',cat_sede='" + cbosede.getSelectedItem()
                            + "' WHERE cat_codigo=" + this.txtCod.getText();
                    //System.out.println(update);
                    con.Guardar(update);
                    ActualizarGrilla();
                    botonmodificar = false;
                }
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
        cbosede.setEnabled(false);
        cbosede.setSelectedIndex(0);
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
        String sql = "SELECT * FROM catedra WHERE cat_codigo='" + txtNom.getText() + "'";
        if (!txtNom.getText().isEmpty()) {
            if (comparar(sql) == false) {
                txtNom.setText(txtNom.getText().toUpperCase());
                cbosede.setEnabled(true);
                cbosede.grabFocus();
            } else {
                JOptionPane.showMessageDialog(this, "La descripción ya existe!!!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
}//GEN-LAST:event_txtNomActionPerformed

    private void txtCodActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCodActionPerformed
        String sql1 = "SELECT * FROM catedra WHERE  cat_codigo=" + txtCod.getText();

        if (FrmPrincipal.isNumeric(txtCod.getText())) {
            if (validar(sql1) == true) {

                if (botonmodificar == true) {
                    try {
                        String sql = "Select * FROM catedra "
                                + "WHERE cat_codigo=" + txtCod.getText();
                        //System.out.println(sql);
                        rs = con.Consulta(sql);
                        rs.next();
                        txtNom.setText(rs.getString("cat_nombre"));
                        txtNom.setEnabled(true);
                        cbosede.setEnabled(true);
                        cbosede.setSelectedItem(rs.getString("cat_sede"));
                        txtNom.grabFocus();
                    } catch (SQLException ex) {
                        Logger.getLogger(Catedra.class.getName()).log(Level.SEVERE, null, ex);
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

    private void cbosedeKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cbosedeKeyPressed
    }//GEN-LAST:event_cbosedeKeyPressed

    private void cbosedeItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbosedeItemStateChanged
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            btnGra.setEnabled(true);
        }
    }//GEN-LAST:event_cbosedeItemStateChanged

    private void txtNomFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtNomFocusLost
        txtNom.setText(txtNom.getText().toUpperCase());
    }//GEN-LAST:event_txtNomFocusLost

    private void TablaCatedraMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_TablaCatedraMouseClicked
        try {
            String sql = "Select * FROM catedra "
                    + "WHERE cat_codigo=" + TablaCatedra.getValueAt(TablaCatedra.getSelectedRow(), 0);
            System.out.println(sql);
            rs = con.Consulta(sql);
            rs.next();
            txtCod.setText(rs.getString("cat_codigo"));
            txtNom.setText(rs.getString("cat_nombre"));
            cbosede.setSelectedItem(rs.getString("cat_sede"));
        } catch (SQLException ex) {
            Logger.getLogger(Catedra.class.getName()).log(Level.SEVERE, null, ex);
        }

    }//GEN-LAST:event_TablaCatedraMouseClicked

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                Catedra dialog = new Catedra(new javax.swing.JFrame(), true);
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
    private javax.swing.JComboBox cbosede;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JPanel jPanel;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField txtCod;
    private javax.swing.JTextField txtNom;
    // End of variables declaration//GEN-END:variables
}
