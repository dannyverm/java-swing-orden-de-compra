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
import java.awt.event.ActionEvent;
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
public final class TipoArticulo extends javax.swing.JDialog {

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
    private ResultSet rscboCat;
    private String codigoCat;

    /**
     * Creates new form Catedra
     */
    public TipoArticulo(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        con = new Conexion(AcesoUsuario.host, AcesoUsuario.base, AcesoUsuario.user, AcesoUsuario.pass);
        botonEnter();
        centrar();
        cargaCboCatedra();
        cbocat.grabFocus();
        cbocat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ActionPerformed(evt);
            }
        });
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

    private void ActualizarGrilla() {
        String Nombre;
        try {

            int c = 0;
            vaciarTabla();
            String sql = "SELECT t.tip_cod,t.tip_descripcion,c.cat_nombre"
                    + " FROM tipo_articulo t,catedra c"
                    + " WHERE t.cat_codigo=c.cat_codigo"
                    + " AND t.cat_codigo=" + codigoCat
                    + " ORDER BY tip_descripcion";
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
                    Nombre = rs.getString("t.tip_descripcion");
                    TablaCatedra.setValueAt(rs.getString(1), c, 0);
                    TablaCatedra.setValueAt(Nombre, c, 1);
                    TablaCatedra.setValueAt(rs.getString(3), c, 2);
                    c++;
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(TipoArticulo.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(TipoArticulo.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(TipoArticulo.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ban;
    }

    private void cargaCboCatedra() {
        try {
            String sql = "SELECT * FROM catedra WHERE cat_sede='" + AcesoUsuario.sede
                    + "' ORDER BY cat_nombre";
            System.out.println(sql);
            rscboCat = con.Consulta(sql);
            cbocat.removeAllItems();

            while (rscboCat.next()) {
                try {
                    cbocat.addItem(rscboCat.getString("cat_nombre"));
                } catch (SQLException ex) {
                    Logger.getLogger(Usuario.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            cbocat.setSelectedIndex(-1);
        } catch (SQLException ex) {
            Logger.getLogger(Usuario.class.getName()).log(Level.SEVERE, null, ex);
        }
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
        jScrollPane1 = new javax.swing.JScrollPane();
        TablaCatedra = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        cbocat = new javax.swing.JComboBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Tipo de Articulo");

        jPanel.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        btnAgr.setText("Agregar"); // NOI18N
        btnAgr.setEnabled(false);
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

        javax.swing.GroupLayout jPanelLayout = new javax.swing.GroupLayout(jPanel);
        jPanel.setLayout(jPanelLayout);
        jPanelLayout.setHorizontalGroup(
            jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel13)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtCod, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(13, 13, 13)
                .addComponent(jLabel18)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtNom, javax.swing.GroupLayout.PREFERRED_SIZE, 298, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelLayout.createSequentialGroup()
                .addContainerGap(18, Short.MAX_VALUE)
                .addComponent(btnAgr, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnMod, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnGra, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnCan, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnSal, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(32, 32, 32))
        );
        jPanelLayout.setVerticalGroup(
            jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelLayout.createSequentialGroup()
                .addGap(33, 33, 33)
                .addGroup(jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtCod, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel13))
                    .addGroup(jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtNom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel18)))
                .addGap(29, 29, 29)
                .addGroup(jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(btnAgr, javax.swing.GroupLayout.DEFAULT_SIZE, 33, Short.MAX_VALUE)
                    .addComponent(btnMod, javax.swing.GroupLayout.DEFAULT_SIZE, 33, Short.MAX_VALUE)
                    .addComponent(btnGra, javax.swing.GroupLayout.DEFAULT_SIZE, 33, Short.MAX_VALUE)
                    .addComponent(btnCan, javax.swing.GroupLayout.DEFAULT_SIZE, 33, Short.MAX_VALUE)
                    .addComponent(btnSal, javax.swing.GroupLayout.DEFAULT_SIZE, 33, Short.MAX_VALUE))
                .addGap(40, 40, 40))
        );

        TablaCatedra.setAutoCreateRowSorter(true);
        TablaCatedra.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Codigo", "Nombre", "Cátedra"
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

        jLabel1.setText("Cátedra");

        cbocat.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "SAN LORENZO", "SAJONIA" }));
        cbocat.setSelectedIndex(-1);
        cbocat.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        cbocat.setNextFocusableComponent(btnAgr);
        cbocat.setOpaque(false);
        cbocat.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbocatItemStateChanged(evt);
            }
        });
        cbocat.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                cbocatFocusLost(evt);
            }
        });
        cbocat.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                cbocatKeyPressed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 559, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(45, 45, 45)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbocat, javax.swing.GroupLayout.PREFERRED_SIZE, 194, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(cbocat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnAgrActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgrActionPerformed
        if (cbocat.getSelectedIndex() > 0) {
            try {
                btnGra.setEnabled(true);
                botonagregar = true;
                String sql = "Select ifnull(max(tip_cod),0)+1 codigo From tipo_articulo";
                System.out.println(sql);
                rs = con.Consulta(sql);
                rs.next();
                txtCod.setText(rs.getString("codigo"));
                btnAgr.setEnabled(false);
                btnMod.setEnabled(false);
                btnCan.setEnabled(true);
                txtNom.setEnabled(true);
                txtNom.grabFocus();
            } catch (SQLException ex) {
                Logger.getLogger(TipoArticulo.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            JOptionPane.showMessageDialog(this, "No ha seleccionado un Servicio!!!", "Error", JOptionPane.ERROR_MESSAGE);
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
        txtNom.setEnabled(true);
        txtNom.grabFocus();
        btnAgr.setEnabled(false);
        btnMod.setEnabled(false);
        btnCan.setEnabled(true);
}//GEN-LAST:event_btnModActionPerformed

    private void btnModKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnModKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_RIGHT) {
            this.btnSal.grabFocus();
        } else if (evt.getKeyCode() == KeyEvent.VK_LEFT) {
            btnAgr.grabFocus();
        }
}//GEN-LAST:event_btnModKeyPressed

    private void btnGraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGraActionPerformed

        int res = JOptionPane.showConfirmDialog(null, "Realmente, ¿Desea Grabar"
                + " los Datos?", "Mensaje", JOptionPane.OK_CANCEL_OPTION);

        if (res == JOptionPane.OK_OPTION) {
            if (botonagregar == true) {
                String sql = "INSERT INTO tipo_articulo (tip_cod,cat_codigo,tip_descripcion)"
                        + " VALUES (" + txtCod.getText() + ",'" + codigoCat + "','"
                        + txtNom.getText() + "')";
                //System.out.println(sql);
                con.Guardar(sql);
                ActualizarGrilla();
                botonagregar = false;
            }
            if (botonmodificar == true) {
                String update = "UPDATE tipo_articulo SET "
                        + "cat_codigo='" + codigoCat
                        + "' tip_descripcion='" + txtNom.getText()
                        + "' WHERE tip_cod=" + txtCod.getText();
                //System.out.println(update);
                con.Guardar(update);
                ActualizarGrilla();
                botonmodificar = false;
            }
            this.btnCan.doClick();
        } else {
            this.btnCan.doClick();
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
        txtCod.setText("");
        txtCod.setEnabled(false);
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
        String sql = "SELECT * FROM tipo_articulo WHERE cat_codigo='"
                + codigoCat + "' AND tip_descripcion='" + txtNom.getText() + "'";
        if (!txtNom.getText().isEmpty()) {
            if (comparar(sql) == false) {
                txtNom.setText(txtNom.getText().toUpperCase());
                btnGra.setEnabled(true);
                btnGra.grabFocus();
            } else {
                JOptionPane.showMessageDialog(this, "La descripción ya existe!!!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
}//GEN-LAST:event_txtNomActionPerformed

    private void txtCodActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCodActionPerformed
}//GEN-LAST:event_txtCodActionPerformed

    private void TablaCatedraMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_TablaCatedraMouseClicked
        try {
            String sql = "Select * FROM tipo_articulo "
                    + "WHERE tip_cod=" + TablaCatedra.getValueAt(TablaCatedra.getSelectedRow(), 0);
            //System.out.println(sql);
            rs = con.Consulta(sql);
            rs.next();
            txtCod.setText(rs.getString("tip_cod"));
            txtNom.setText(rs.getString("tip_descripcion"));

        } catch (SQLException ex) {
            Logger.getLogger(TipoArticulo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_TablaCatedraMouseClicked

    private void cbocatKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cbocatKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            if (cbocat.getSelectedIndex() != -1) {
                try {
                    rscboCat.beforeFirst();
                    while (rscboCat.next()) {
                        if (rscboCat.getString("cat_nombre").equals(cbocat.getSelectedItem())) {
                            codigoCat = rscboCat.getString("cat_codigo");
                            ActualizarGrilla();
                            break;
                        }
                    }

                } catch (SQLException ex) {
                    Logger.getLogger(TipoArticulo.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            btnAgr.setEnabled(true);
            btnAgr.grabFocus();
        }
    }//GEN-LAST:event_cbocatKeyPressed

    private void cbocatFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cbocatFocusLost
        if (cbocat.getSelectedIndex() != -1) {
            //  System.out.println("ha upei chongo");
            try {
                rscboCat.beforeFirst();
                while (rscboCat.next()) {
                    if (rscboCat.getString("cat_nombre").equals(cbocat.getSelectedItem())) {
                        codigoCat = rscboCat.getString("cat_codigo");
                        ActualizarGrilla();
                        btnAgr.setEnabled(true);
                        btnAgr.grabFocus();
                        break;
                    }
                }

            } catch (SQLException ex) {
                Logger.getLogger(TipoArticulo.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }//GEN-LAST:event_cbocatFocusLost

    private void cbocatItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbocatItemStateChanged
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            if (cbocat.getSelectedIndex() != -1) {
                try {
                    rscboCat.beforeFirst();
                    while (rscboCat.next()) {
                        if (rscboCat.getString("cat_nombre").equals(cbocat.getSelectedItem())) {
                            codigoCat = rscboCat.getString("cat_codigo");
                            ActualizarGrilla();
                            break;
                        }
                    }

                } catch (SQLException ex) {
                    Logger.getLogger(TipoArticulo.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            btnAgr.setEnabled(true);
        }
    }//GEN-LAST:event_cbocatItemStateChanged

    private void txtNomFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtNomFocusLost
        txtNom.setText(txtNom.getText().toUpperCase());
    }//GEN-LAST:event_txtNomFocusLost

    private void ActionPerformed(ActionEvent evt) {
        if (evt.getSource() == cbocat) {
            if (cbocat.getSelectedIndex() != 0) {
                //  System.out.println("ha upei chongo");
                try {
                    rscboCat.beforeFirst();
                    while (rscboCat.next()) {
                        if (rscboCat.getString("cat_nombre").equals(cbocat.getSelectedItem())) {
                            codigoCat = rscboCat.getString("cat_codigo");
                            ActualizarGrilla();
                            btnAgr.setEnabled(true);
                            break;
                        }
                    }

                } catch (SQLException ex) {
                    Logger.getLogger(TipoArticulo.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                TipoArticulo dialog = new TipoArticulo(new javax.swing.JFrame(), true);
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
    private javax.swing.JComboBox cbocat;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JPanel jPanel;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField txtCod;
    private javax.swing.JTextField txtNom;
    // End of variables declaration//GEN-END:variables
}
