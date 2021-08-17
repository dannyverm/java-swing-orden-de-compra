/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * Proveedor.java
 *
 * Created on 4/10/2012, 11:21:29 AM
 */
package PaqFormulario;

import clases.Conexion;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
public final class Proveedor extends javax.swing.JDialog {

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
    public boolean tablabuscar = false;
    public boolean txtcodbuscar = false;
    public boolean lostfocusDescripcion = false;

    /**
     * Creates new form Proveedor
     */
    public Proveedor(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        con = new Conexion(AcesoUsuario.host, AcesoUsuario.base, AcesoUsuario.user, AcesoUsuario.pass);
        botonEnter();
        centrar();
        ActualizarGrilla();
        btnAgr.grabFocus();
//        CargaTablaCatedra();
    }

    private void centrar() {
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

        while (i != TablaProveedor.getRowCount()) {
            TablaProveedor.setValueAt("", i, 0);
            TablaProveedor.setValueAt("", i, 1);
            TablaProveedor.setValueAt("", i, 2);
            i++;
        }
    }

    private void ActualizarGrilla() {
        String Nombre = null;
        try {

            int c = 0;
            vaciarTabla();
            String sql = "SELECT *"
                    + " FROM proveedor"
                    + " ORDER BY pro_descripcion";
            //System.out.println(sql);
            rs = con.Consulta(sql);
            if (rs.next() == true) {
                rs.beforeFirst();
                while (rs.next()) {
                    if (TablaProveedor.getRowCount() <= rs.getRow() - 1) {
                        DefaultTableModel tm = (DefaultTableModel) TablaProveedor.getModel();
                        tm.addRow(new Object[]{new String(""), new String("")});
                        TablaProveedor.setModel(tm);
                    }
                    Nombre = rs.getString("pro_descripcion");
                    TablaProveedor.setValueAt(rs.getString(1), c, 0);
                    TablaProveedor.setValueAt(Nombre, c, 1);
                    TablaProveedor.setValueAt(rs.getString(3), c, 2);
                    c++;
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(Catedra.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

//    private void CargaTablaCatedra() { 
//        try {
//
//            int c = 0;
//            String sql = "SELECT *"
//                    + " FROM catedra WHERE cat_sede='"+AcesoUsuario.sede
//                    + "' ORDER BY cat_nombre";
//            //System.out.println(sql);
//            rs = con.Consulta(sql);
//            if (rs.next() == true) {
//                rs.beforeFirst();
//                while (rs.next()) {
//                    if (TablaCatedra.getRowCount() <= rs.getRow() - 1) {
//                        DefaultTableModel tm = (DefaultTableModel) TablaCatedra.getModel();
//                        tm.addRow(new Object[]{null,null,null });
//                        TablaCatedra.setModel(tm);
//                    }
//                    TablaCatedra.setValueAt(rs.getString(1), c, 0);
//                    TablaCatedra.setValueAt(rs.getString(2), c, 1);
//                    c++;
//                }
//            }
//        } catch (SQLException ex) {
//            Logger.getLogger(Catedra.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
    
    
    private boolean comparar(String sql) {
        boolean r = false;
        try {
            rs = con.Consulta(sql);
            if (rs.next()) {
                r = true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(Proveedor.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(Proveedor.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ban;
    }

    private void buscar(String val) {
        String sql1 = "SELECT * FROM proveedor WHERE  pro_codigo=" + val;
        System.out.println(val);

        if (validar(sql1) == true) {
            try {
                String sql = "Select * FROM proveedor "
                        + "WHERE pro_codigo=" + val;
                //System.out.println(sql);
                rs = con.Consulta(sql);
                rs.next();
                txtCod.setText(rs.getString("pro_codigo"));
                txtdes.setText(rs.getString("pro_descripcion"));
                txtruc.setText(rs.getString("pro_ruc"));
                txtdir.setText(rs.getString("pro_direccion"));
                txttel.setText(rs.getString("pro_telefono"));
                txtcon.setText(rs.getString("pro_contacto"));
                btnMod.setEnabled(true);
                tablabuscar = true;
            } catch (SQLException ex) {
                Logger.getLogger(Proveedor.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            JOptionPane.showMessageDialog(null, "El Codigo No Existe");
            this.txtCod.setText("");
            this.txtCod.grabFocus();
        }

        if (txtcodbuscar == true) {
            txtCod.setEnabled(false);
            txtdes.setEnabled(true);
            txtdes.grabFocus();
            txtcodbuscar = false;
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

        jScrollPane1 = new javax.swing.JScrollPane();
        TablaProveedor = new javax.swing.JTable();
        jPanel = new javax.swing.JPanel();
        btnAgr = new javax.swing.JButton();
        btnMod = new javax.swing.JButton();
        btnGra = new javax.swing.JButton();
        btnCan = new javax.swing.JButton();
        btnSal = new javax.swing.JButton();
        jLabel13 = new javax.swing.JLabel();
        txtdes = new javax.swing.JTextField();
        txtCod = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        txtruc = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        txtdir = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        txttel = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        txtcon = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Proveedores");

        TablaProveedor.setModel(new javax.swing.table.DefaultTableModel(
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
                "Codigo", "Descripción", "Telefono"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        TablaProveedor.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                TablaProveedorMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(TablaProveedor);
        TablaProveedor.getColumnModel().getColumn(0).setPreferredWidth(80);
        TablaProveedor.getColumnModel().getColumn(0).setMaxWidth(80);
        TablaProveedor.getColumnModel().getColumn(2).setPreferredWidth(100);
        TablaProveedor.getColumnModel().getColumn(2).setMaxWidth(100);

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

        txtdes.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        txtdes.setEnabled(false);
        txtdes.setNextFocusableComponent(txtruc);
        txtdes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtdesActionPerformed(evt);
            }
        });
        txtdes.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtdesFocusLost(evt);
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

        jLabel14.setText("Ruc"); // NOI18N

        txtruc.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        txtruc.setEnabled(false);
        txtruc.setNextFocusableComponent(txtdir);
        txtruc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtrucActionPerformed(evt);
            }
        });

        jLabel19.setText("Dirección"); // NOI18N

        txtdir.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        txtdir.setEnabled(false);
        txtdir.setNextFocusableComponent(txttel);
        txtdir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtdirActionPerformed(evt);
            }
        });
        txtdir.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtdirFocusLost(evt);
            }
        });

        jLabel15.setText("Telefono"); // NOI18N

        txttel.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        txttel.setEnabled(false);
        txttel.setNextFocusableComponent(txtcon);
        txttel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txttelActionPerformed(evt);
            }
        });

        jLabel16.setText("Contacto"); // NOI18N

        txtcon.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        txtcon.setEnabled(false);
        txtcon.setNextFocusableComponent(btnGra);
        txtcon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtconActionPerformed(evt);
            }
        });
        txtcon.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtconFocusLost(evt);
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
                        .addComponent(jLabel15)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txttel, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel16)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtcon, javax.swing.GroupLayout.PREFERRED_SIZE, 255, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(jPanelLayout.createSequentialGroup()
                            .addComponent(jLabel13)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(txtCod, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(13, 13, 13)
                            .addComponent(jLabel18)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(txtdes, javax.swing.GroupLayout.PREFERRED_SIZE, 319, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanelLayout.createSequentialGroup()
                            .addComponent(jLabel14)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(txtruc, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(18, 18, 18)
                            .addComponent(jLabel19)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(txtdir))))
                .addGap(0, 67, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnAgr, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnMod, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnGra, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnCan, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnSal, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(44, 44, 44))
        );
        jPanelLayout.setVerticalGroup(
            jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtCod, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel13))
                    .addGroup(jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtdes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel18)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtruc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14)
                    .addComponent(txtdir, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel19))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txttel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15)
                    .addComponent(txtcon, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(btnAgr)
                    .addComponent(btnMod)
                    .addComponent(btnGra, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnCan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnSal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane1)
                    .addComponent(jPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
            btnGra.setEnabled(true);
            botonagregar = true;
            String sql = "Select ifnull(max(pro_codigo),0)+1 codigo From proveedor";
            System.out.println(sql);
            rs = con.Consulta(sql);
            rs.next();
            txtCod.setText(rs.getString("codigo"));
            txtdes.setEnabled(true);
            txtcon.setEnabled(true);
            txtdir.setEnabled(true);
            txtruc.setEnabled(true);
            txttel.setEnabled(true);
            btnAgr.setEnabled(false);
            btnMod.setEnabled(false);
            btnCan.setEnabled(true);
            txtdes.setEnabled(true);
            txtdes.grabFocus();
        } catch (SQLException ex) {
            Logger.getLogger(Proveedor.class.getName()).log(Level.SEVERE, null, ex);
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
        if (tablabuscar == false) {
            botonmodificar = true;
            txtCod.setEnabled(true);
            txtCod.grabFocus();
            btnAgr.setEnabled(false);
            btnMod.setEnabled(false);
            btnCan.setEnabled(true);
        } else {
            botonmodificar = true;
            txtdes.setEnabled(true);
            txtdes.grabFocus();
            txtcon.setEnabled(true);
            txtdir.setEnabled(true);
            txtruc.setEnabled(true);
            txttel.setEnabled(true);
            btnGra.setEnabled(true);
            btnAgr.setEnabled(false);
            btnMod.setEnabled(false);
            btnCan.setEnabled(true);
            tablabuscar = false;
        }
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
                String sql = "INSERT INTO proveedor (pro_codigo,pro_descripcion,pro_telefono,"
                        + "pro_direccion,pro_ruc,pro_contacto)"
                        + " VALUES (" + txtCod.getText() + ",'" + txtdes.getText()
                        + "','" + txttel.getText() + "','" + txtdir.getText() + "','" + txtruc.getText()
                        + "','" + txtcon.getText() + "')";
                //System.out.println(sql);
                con.Guardar(sql);
                ActualizarGrilla();
                botonagregar = false;
            }
            if (botonmodificar == true) {
                String update = "UPDATE proveedor SET "
                        + "pro_descripcion='" + txtdes.getText()
                        + "',pro_telefono='" + txttel.getText()
                        + "',pro_direccion='" + txtdir.getText()
                        + "',pro_ruc='" + txtruc.getText()
                        + "',pro_contacto='" + txtcon.getText()
                        + "' WHERE cat_codigo=" + this.txtCod.getText();
                //System.out.println(update);
                con.Guardar(update);
                ActualizarGrilla();
                botonmodificar = false;
            }
            OC.DatoBotonBusqueda = txtdes.getText();
            if (OC.pulsaboton == true) {
                this.dispose();
            }
            btnCan.doClick();
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
        tablabuscar = false;
        botonagregar = false;
        botonborrar = false;
        botonmodificar = false;
        btnCan.setEnabled(false);
        btnGra.setEnabled(false);
        btnAgr.setEnabled(true);
        btnMod.setEnabled(true);
        txtCod.setText("");
        txtdes.setText("");
        txtcon.setEnabled(false);
        txtcon.setText("");
        txtdir.setEnabled(false);
        txtdir.setText("");
        txtruc.setEnabled(false);
        txtruc.setText("");
        txttel.setEnabled(false);
        txttel.setText("");
        txtdes.setEnabled(false);
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

    private void txtdesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtdesActionPerformed
        txtruc.grabFocus();        
}//GEN-LAST:event_txtdesActionPerformed

    private void txtCodActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCodActionPerformed
        txtcodbuscar = true;
        buscar(txtCod.getText());
}//GEN-LAST:event_txtCodActionPerformed

    private void txtrucActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtrucActionPerformed
        txtdir.setEnabled(true);
        txtdir.grabFocus();
    }//GEN-LAST:event_txtrucActionPerformed

    private void txtdirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtdirActionPerformed
        txttel.setEnabled(true);
        txttel.grabFocus();
    }//GEN-LAST:event_txtdirActionPerformed

    private void txttelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txttelActionPerformed
        txtcon.setEnabled(true);
        txtcon.grabFocus();
    }//GEN-LAST:event_txttelActionPerformed

    private void txtconActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtconActionPerformed
        btnGra.setEnabled(true);
        btnGra.grabFocus();
    }//GEN-LAST:event_txtconActionPerformed

    private void TablaProveedorMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_TablaProveedorMouseClicked
        buscar(String.valueOf(TablaProveedor.getValueAt(TablaProveedor.getSelectedRow(), 0)));
    }//GEN-LAST:event_TablaProveedorMouseClicked

    private void txtdesFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtdesFocusLost
        String sql = "SELECT * FROM proveedor WHERE pro_codigo='" + txtdes.getText() + "'";
        lostfocusDescripcion = true;
        if (!txtdes.getText().isEmpty()) {
            if (comparar(sql) == false) {
                txtdes.setText(txtdes.getText().toUpperCase());
            } else {
                JOptionPane.showMessageDialog(this, "La descripción ya existe!!!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            int res = JOptionPane.showConfirmDialog(null, "El campo no puede estar vacio"
                    + "...¿Desea cancelar?", "Mensaje", JOptionPane.YES_NO_OPTION);
            if (res == JOptionPane.YES_OPTION) {
                btnCan.doClick();
            } else {
                txtdes.grabFocus();
                lostfocusDescripcion = false;
            }
        }
    }//GEN-LAST:event_txtdesFocusLost

    private void txtdirFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtdirFocusLost
        txtdir.setText(txtdir.getText().toUpperCase());
    }//GEN-LAST:event_txtdirFocusLost

    private void txtconFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtconFocusLost
        btnGra.setEnabled(true);
    }//GEN-LAST:event_txtconFocusLost

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                Proveedor dialog = new Proveedor(new javax.swing.JFrame(), true);
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
    private javax.swing.JTable TablaProveedor;
    public static javax.swing.JButton btnAgr;
    private javax.swing.JButton btnCan;
    private javax.swing.JButton btnGra;
    private javax.swing.JButton btnMod;
    private javax.swing.JButton btnSal;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JPanel jPanel;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField txtCod;
    private javax.swing.JTextField txtcon;
    private javax.swing.JTextField txtdes;
    private javax.swing.JTextField txtdir;
    private javax.swing.JTextField txtruc;
    private javax.swing.JTextField txttel;
    // End of variables declaration//GEN-END:variables
}
