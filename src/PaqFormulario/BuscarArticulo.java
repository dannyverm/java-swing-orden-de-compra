/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package PaqFormulario;

import clases.Conexion;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Administrador
 */
public class BuscarArticulo extends javax.swing.JDialog {

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
    private ResultSet rscboCat;
    private ResultSet rscboTipo;
    private String codigoTipo;
    private String codigoCatedra;

    /**
     * Creates new form BuscarArticulo
     */
    public BuscarArticulo(java.awt.Dialog parent, boolean modal) {
        super(parent, modal);
        initComponents();
        con = new Conexion(AcesoUsuario.host, AcesoUsuario.base, AcesoUsuario.user, AcesoUsuario.pass);
        centrar();
        cargaCboCatedra();
        cbocat.grabFocus();
        cbocat.setSelectedItem(OC.catedraOC);
        Articulo.codigoArticulo=null;
    }

    private void centrar() {
        Dimension pantalla = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frame = this.getSize();
        setLocation((pantalla.width / 2 - (frame.width / 2)), pantalla.height / 2 - (frame.height / 2));
    }

    private void cargaCboCatedra() {
        try {
            String sql = "SELECT * FROM catedra WHERE cat_sede='" + AcesoUsuario.sede
                    + "' ORDER BY cat_nombre";
            rscboCat = con.Consulta(sql);
            cbocat.removeAllItems();
            while (rscboCat.next()) {
                try {
                    cbocat.addItem(rscboCat.getString("cat_nombre"));
                } catch (SQLException ex) {
                    Logger.getLogger(Articulo.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            cbocat.setSelectedIndex(-1);
        } catch (SQLException ex) {
            Logger.getLogger(Articulo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void cargaCboTipo() {
        try {
            String sql;
            sql = "SELECT * FROM tipo_articulo WHERE cat_codigo="
                    + "(SELECT cat_codigo FROM catedra WHERE cat_nombre='"
                    + cbocat.getSelectedItem() + "' AND cat_sede='" + AcesoUsuario.sede + "')"
                    + " ORDER BY tip_descripcion";
            //System.out.println(sql);
            rscboTipo = con.Consulta(sql);
            cbotip.removeAllItems();

            while (rscboTipo.next()) {
                try {
                    cbotip.addItem(rscboTipo.getString("tip_descripcion"));
                } catch (SQLException ex) {
                    Logger.getLogger(Articulo.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            cbotip.setSelectedIndex(-1);
        } catch (SQLException ex) {
            Logger.getLogger(Articulo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void vaciarTabla() {
        int i = 0;

        while (i != tabla.getRowCount()) {
            tabla.setValueAt("", i, 0);
            tabla.setValueAt("", i, 1);
            tabla.setValueAt("", i, 2);
            tabla.setValueAt("", i, 3);
            tabla.setValueAt("", i, 4);
            i++;
        }
    }

    private void EliminarFila() {
        {
            try {
                DefaultTableModel temp = (DefaultTableModel) tabla.getModel();
                while (tabla.getRowCount() > 0) {
                    temp.removeRow(temp.getRowCount() - 1);
                }

            } catch (ArrayIndexOutOfBoundsException e) {
            }
        }
    }

    private void ActualizarGrilla(String and) {
        try {

            int c = 0;
            EliminarFila();
            vaciarTabla();
            String sql = "SELECT * FROM articulo WHERE cat_codigo="
                    + codigoCatedra + " " + and +" ORDER BY art_descripcion";
            //System.out.println(sql);
            rs = con.Consulta(sql);
            if (rs.next() == true) {
                rs.beforeFirst();
                while (rs.next()) {
                    if (tabla.getRowCount() <= rs.getRow() - 1) {
                        DefaultTableModel tm = (DefaultTableModel) tabla.getModel();
                        tm.addRow(new Object[]{"", "", "", ""});
                        tabla.setModel(tm);
                    }
                    tabla.setValueAt(rs.getString(1), c, 0);
                    tabla.setValueAt(rs.getString(4), c, 1);
                    tabla.setValueAt(rs.getString(8), c, 2);
                    tabla.setValueAt(rs.getString(6), c, 3);
                    tabla.setValueAt(rs.getString(5), c, 4);
                    c++;
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(TipoArticulo.class.getName()).log(Level.SEVERE, null, ex);
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
        tabla = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        cbocat = new javax.swing.JComboBox();
        txtdes = new javax.swing.JTextField();
        cbotip = new javax.swing.JComboBox();
        jLabel7 = new javax.swing.JLabel();
        btncan = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Busqueda");

        tabla.setAutoCreateRowSorter(true);
        tabla.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        tabla.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Codigo", "Descripción", "Presentación", "Unidad", "Precio"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tabla.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        tabla.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tablaMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tabla);
        tabla.getColumnModel().getColumn(0).setPreferredWidth(70);
        tabla.getColumnModel().getColumn(0).setMaxWidth(70);
        tabla.getColumnModel().getColumn(3).setPreferredWidth(100);
        tabla.getColumnModel().getColumn(4).setPreferredWidth(80);

        jPanel2.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel1.setText("Descripción");

        jLabel6.setText("Cátedra");

        cbocat.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cbocat.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        cbocat.setNextFocusableComponent(cbotip);
        cbocat.setOpaque(false);
        cbocat.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbocatItemStateChanged(evt);
            }
        });

        txtdes.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        txtdes.setEnabled(false);
        txtdes.setOpaque(false);
        txtdes.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtdesKeyPressed(evt);
            }
        });

        cbotip.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        cbotip.setEnabled(false);
        cbotip.setOpaque(false);
        cbotip.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbotipItemStateChanged(evt);
            }
        });

        jLabel7.setText("Tipo de articulo");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cbotip, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtdes, javax.swing.GroupLayout.PREFERRED_SIZE, 415, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbocat, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(164, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbocat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbotip, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtdes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        btncan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/paqImagenes/cancelar.png"))); // NOI18N
        btncan.setToolTipText("Cancelar");
        btncan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btncanActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(307, 307, 307)
                        .addComponent(btncan, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 11, Short.MAX_VALUE)
                .addComponent(btncan, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btncanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btncanActionPerformed
        this.dispose();
    }//GEN-LAST:event_btncanActionPerformed

    private void txtdesKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtdesKeyPressed
        ActualizarGrilla("AND art_descripcion LIKE '%" + txtdes.getText() + "%'");
    }//GEN-LAST:event_txtdesKeyPressed

    private void tablaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaMouseClicked
        //System.out.println("Dato de la tabla" + tabla.getValueAt(tabla.getSelectedRow(), 0));
        Articulo.codigoArticulo = (String) tabla.getValueAt(tabla.getSelectedRow(), 0);

        if (OC.OCvisible == true) {
            String res = JOptionPane.showInputDialog(null, "Ingrese cantidad");
            if (res != null) {
                OC.cantidadOC = res;
            }
        }
        this.dispose();
    }//GEN-LAST:event_tablaMouseClicked

    private void cbocatItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbocatItemStateChanged
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            if (cbocat.getSelectedIndex() > -1) {
                //  System.out.println("ha upei chongo");
                try {
                    rscboCat.beforeFirst();
                    while (rscboCat.next()) {
                        if (rscboCat.getString("cat_nombre").equals(cbocat.getSelectedItem())) {
                            codigoCatedra = rscboCat.getString("cat_codigo");
                            cargaCboTipo();
                            ActualizarGrilla("");
                            cbotip.setEnabled(true);
                            txtdes.setEnabled(true);
                            break;
                        }
                    }

                } catch (SQLException ex) {
                    Logger.getLogger(Articulo.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }//GEN-LAST:event_cbocatItemStateChanged

    private void cbotipItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbotipItemStateChanged
        if(evt.getStateChange()==ItemEvent.SELECTED){
            if (cbotip.getSelectedIndex() > -1) {
                //  System.out.println("ha upei chongo");
                try {
                    rscboTipo.beforeFirst();
                    while (rscboTipo.next()) {
                        if (rscboTipo.getString("tip_descripcion").equals(cbotip.getSelectedItem())) {
                            codigoTipo = rscboTipo.getString("tip_cod");
                            ActualizarGrilla("AND tip_cod=" + codigoTipo);
                            break;
                        }
                    }

                } catch (SQLException ex) {
                    Logger.getLogger(TipoArticulo.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }//GEN-LAST:event_cbotipItemStateChanged

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
            java.util.logging.Logger.getLogger(BuscarArticulo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(BuscarArticulo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(BuscarArticulo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(BuscarArticulo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                BuscarArticulo dialog = new BuscarArticulo(new javax.swing.JDialog(), true);
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
    private javax.swing.JButton btncan;
    private javax.swing.JComboBox cbocat;
    private javax.swing.JComboBox cbotip;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tabla;
    private javax.swing.JTextField txtdes;
    // End of variables declaration//GEN-END:variables
}
