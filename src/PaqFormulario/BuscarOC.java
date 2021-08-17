/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package PaqFormulario;

import clases.Conexion;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ItemEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Administrador
 */
public class BuscarOC extends javax.swing.JDialog implements Runnable {

    private Conexion con;
    private ResultSet rs = null;
    private String CodPrv = null;
    static String CodDpto = null;
    static String ContadorOc = null;
    private ResultSet rsCodDpto;
    private ResultSet rsCodPrv;
    private Thread hiloPrv;
    private Thread hiloCat;
    private Thread hiloFec;
    private ArrayList arCodigo;

    /**
     * Creates new form BuscarOC
     */
    public BuscarOC(java.awt.Dialog parent, boolean modal) {
        super(parent, modal);
        initComponents();
        centrar();
        cargaCboDepto();
        cargaCboPrv();
        arCodigo=new ArrayList();
    }

    public void run() {
        if (hiloPrv != null) {
            ActualizarGrilla("p.pro_descripcion='" + cboprv.getSelectedItem() + "'");
        } else if (hiloCat != null) {
            ActualizarGrilla("c.cat_nombre='" + cbodpto.getSelectedItem() + "'");
        } else if (hiloFec != null) {
            SimpleDateFormat d = new SimpleDateFormat("yyyy-MM-dd");
            ActualizarGrilla("o.ocab_fecha>'" + d.format(txtfecdesde.getDate()) + "'");
        }
    }

    private void vaciarTabla() {
        int i = 0;

        while (i != tablaOc.getRowCount()) {
            tablaOc.setValueAt("", i, 0);
            tablaOc.setValueAt("", i, 1);
            tablaOc.setValueAt("", i, 2);
            tablaOc.setValueAt("", i, 3);
            tablaOc.setValueAt("", i, 4);
            tablaOc.setValueAt("", i, 5);
            i++;
        }
    }

    private void vaciarTablaDetalle() {
        int i = 0;

        while (i != tablaDetalle.getRowCount()) {
            tablaDetalle.setValueAt("", i, 0);
            tablaDetalle.setValueAt("", i, 1);
            tablaDetalle.setValueAt("", i, 2);

            i++;
        }
    }

    private void ActualizarGrilla(String and) {
        try {

            int c = 0;
            vaciarTabla();
            String sql = "SELECT o.ocab_codigo,c.cat_nombre,p.pro_descripcion,"
                    + "o.ocab_fecha,o.ocab_plazo_entrega,o.ocab_total,o.ocab_contador FROM "
                    + "ordencab o,proveedor p,catedra c WHERE o.pro_codigo="
                    + "p.pro_codigo AND o.cat_codigo=c.cat_codigo AND " + and;
            System.out.println(sql);
            DecimalFormat formato = new DecimalFormat("###,###");
            SimpleDateFormat date = new SimpleDateFormat("dd/MM/yyyy");
            conexion();
            rs = con.Consulta(sql);
            if (rs.next() == true) {
                rs.beforeFirst();
                while (rs.next()) {
                    if (tablaOc.getRowCount() <= rs.getRow() - 1) {
                        DefaultTableModel tm = (DefaultTableModel) tablaOc.getModel();
                        tm.addRow(new Object[]{"", "", "", "", "", ""});
                        tablaOc.setModel(tm);
                    }
                    tablaOc.setValueAt(rs.getString(1), c, 0);
                    tablaOc.setValueAt(rs.getString(2), c, 1);
                    tablaOc.setValueAt(rs.getString(3), c, 2);
                    if (rs.getDate(4) != null) {
                        tablaOc.setValueAt(date.format(rs.getDate(4)), c, 3);
                    } else if (rs.getDate(5) != null) {
                        tablaOc.setValueAt(date.format(rs.getDate(5)), c, 4);
                    }
                    arCodigo.add(c,rs.getObject(7));
                    tablaOc.setValueAt(formato.format(rs.getDouble(6)), c, 5);
                    System.out.println("Posicion: "+c+" Codigo: "+arCodigo.get(c));
                    c++;
                }
            }
            con.CerrarConexion();
        } catch (SQLException ex) {
            //Logger.getLogger(BuscarOC.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NullPointerException n) {
            //Logger.getLogger(BuscarOC.class.getName()).log(Level.SEVERE, null, n);
        }
        hiloCat = null;
        hiloPrv = null;
        hiloFec = null;
    }

    private void ActualizarGrillaDetalle() {
        try {
            int c = 0;
            vaciarTablaDetalle();
            String sql = "SELECT od.art_codigo,od.odet_cantidad,a.art_descripcion,"
                    + "od.odet_preciouni FROM articulo a,detalle od,ordencab o "
                    + "WHERE od.art_codigo=a.art_codigo AND o.ocab_contador=od.ocab_contador "
                    + "AND o.ocab_contador="
                    + arCodigo.get(tablaOc.getSelectedRow());
            System.out.println(sql);
            conexion();
            rs = con.Consulta(sql);
            if (rs.next() == true) {
                rs.beforeFirst();
                while (rs.next()) {
                    if (tablaDetalle.getRowCount() <= rs.getRow() - 1) {
                        DefaultTableModel tm = (DefaultTableModel) tablaDetalle.getModel();
                        tm.addRow(new Object[]{"", "", ""});
                        tablaDetalle.setModel(tm);
                    }
                    tablaDetalle.setValueAt(rs.getString(1), c, 0);
                    tablaDetalle.setValueAt(rs.getString(2), c, 1);
                    tablaDetalle.setValueAt(rs.getString(3), c, 2);
                    tablaDetalle.setValueAt(rs.getString(4), c, 3);
                    c++;
                }
            }
            con.CerrarConexion();
        } catch (SQLException ex) {
            Logger.getLogger(BuscarOC.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void cargaCboPrv() {
        try {
            String sql = "SELECT * FROM proveedor ORDER BY pro_descripcion";
            conexion();
            rsCodPrv = con.Consulta(sql);
            cboprv.removeAllItems();
            //cboprv.addItem("");
            while (rsCodPrv.next()) {
                try {
                    cboprv.addItem(rsCodPrv.getString("pro_descripcion"));
                } catch (SQLException ex) {
                    Logger.getLogger(OC.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            cboprv.addItem("");
            cboprv.setSelectedIndex(-1);
        } catch (SQLException ex) {
            Logger.getLogger(OC.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void cargaCboDepto() {
        conexion();
        try {
            String sql = "SELECT * FROM catedra WHERE cat_sede='" + AcesoUsuario.sede
                    + "' ORDER BY cat_nombre";

            rsCodDpto = con.Consulta(sql);
            cbodpto.removeAllItems();
            //cbodpto.addItem("");
            while (rsCodDpto.next()) {
                try {
                    cbodpto.addItem(rsCodDpto.getString("cat_nombre"));
                } catch (SQLException ex) {
                    Logger.getLogger(OC.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            cbodpto.addItem("");
            cbodpto.setSelectedIndex(-1);
            con.CerrarConexion();
        } catch (SQLException ex) {
            Logger.getLogger(OC.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void centrar() {
        Dimension pantalla = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frame = this.getSize();
        setLocation((pantalla.width / 2 - (frame.width / 2)), pantalla.height / 2 - (frame.height / 2));
    }

    private void conexion() {
        con = new Conexion(AcesoUsuario.host, AcesoUsuario.base, AcesoUsuario.user, AcesoUsuario.pass);
    }

    private String comboCodigo(String consulta, String ColumnaBD, String descripcion) {
        String codigo = null;
        try {
            conexion();
            rs = con.Consulta(consulta + " WHERE " + ColumnaBD + "='" + descripcion + "'");
            //System.out.println(consulta + " WHERE " + ColumnaBD + "='" + descripcion + "'");
            rs.beforeFirst();
            if (rs.next()) {
                rs.beforeFirst();
                while (rs.next()) {

                    if (descripcion.equals(rs.getString(ColumnaBD))) {
                        codigo = rs.getString(1);
                        break;
                    }
                }
            }
            con.CerrarConexion();
        } catch (SQLException ex) {
            Logger.getLogger(OC.class.getName()).log(Level.SEVERE, null, ex);
        }
        return codigo;
    }

    private void codigosCombos() {
        CodPrv = comboCodigo("SELECT * FROM proveedor", "pro_descripcion", (String) cboprv.getSelectedItem());
        CodDpto = comboCodigo("SELECT * FROM catedra", "cat_nombre", (String) cbodpto.getSelectedItem());
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        menu = new javax.swing.JPopupMenu();
        menSeleccionar = new javax.swing.JMenuItem();
        jPanel1 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        cboprv = new javax.swing.JComboBox();
        jLabel7 = new javax.swing.JLabel();
        cbodpto = new javax.swing.JComboBox();
        jPanel3 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        txtfecdesde = new com.toedter.calendar.JDateChooser("dd/MM/yyyy", "####/##/##", '_');
        jLabel5 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablaOc = new javax.swing.JTable();
        jLabel4 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tablaDetalle = new javax.swing.JTable();

        menSeleccionar.setText("Seleccionar");
        menSeleccionar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menSeleccionarActionPerformed(evt);
            }
        });
        menu.add(menSeleccionar);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Buscar OC");

        jPanel1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel3.setText("Proveedor");

        cboprv.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        cboprv.setNextFocusableComponent(cbodpto);
        cboprv.setOpaque(false);
        cboprv.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cboprvItemStateChanged(evt);
            }
        });
        cboprv.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                cboprvFocusLost(evt);
            }
        });

        jLabel7.setText("Servicio");

        cbodpto.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        cbodpto.setNextFocusableComponent(txtfecdesde);
        cbodpto.setOpaque(false);
        cbodpto.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbodptoItemStateChanged(evt);
            }
        });
        cbodpto.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                cbodptoFocusLost(evt);
            }
        });

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Busqueda - Fecha", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, null, new java.awt.Color(0, 0, 204)));

        jButton1.setText("Buscar");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        txtfecdesde.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        txtfecdesde.setOpaque(false);
        txtfecdesde.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                txtfecdesdePropertyChange(evt);
            }
        });

        jLabel5.setText("Fecha desde");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtfecdesde, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel5)
                    .addComponent(txtfecdesde, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(42, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addGap(4, 4, 4)
                        .addComponent(cbodpto, 0, 289, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addGap(4, 4, 4)
                        .addComponent(cboprv, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap(42, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel3)
                    .addComponent(cboprv, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel7)
                    .addComponent(cbodpto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        tablaOc.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "OC. N°", "Suministros", "Proveedor", "Fecha Emisión", "Plazo - Entrega", "Monto Total"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tablaOc.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tablaOcMouseClicked(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                tablaOcMouseReleased(evt);
            }
        });
        jScrollPane1.setViewportView(tablaOc);
        if (tablaOc.getColumnModel().getColumnCount() > 0) {
            tablaOc.getColumnModel().getColumn(0).setPreferredWidth(80);
            tablaOc.getColumnModel().getColumn(0).setMaxWidth(80);
            tablaOc.getColumnModel().getColumn(3).setPreferredWidth(90);
            tablaOc.getColumnModel().getColumn(3).setMaxWidth(90);
            tablaOc.getColumnModel().getColumn(4).setPreferredWidth(90);
            tablaOc.getColumnModel().getColumn(4).setMaxWidth(90);
            tablaOc.getColumnModel().getColumn(5).setPreferredWidth(90);
            tablaOc.getColumnModel().getColumn(5).setMaxWidth(90);
        }

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(51, 153, 0));
        jLabel4.setText("Detalle");

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder()));

        tablaDetalle.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Codigo", "Cantida", "Articulo", "Precio Uni."
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(tablaDetalle);
        if (tablaDetalle.getColumnModel().getColumnCount() > 0) {
            tablaDetalle.getColumnModel().getColumn(0).setPreferredWidth(80);
            tablaDetalle.getColumnModel().getColumn(1).setPreferredWidth(90);
            tablaDetalle.getColumnModel().getColumn(1).setMaxWidth(90);
        }

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 729, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(340, 340, 340)
                .addComponent(jLabel4)
                .addContainerGap(369, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jScrollPane1)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cboprvFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cboprvFocusLost
        if (cboprv.getSelectedIndex() > -1) {
            cboprv.setSelectedIndex(-1);
        }
    }//GEN-LAST:event_cboprvFocusLost

    private void cbodptoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cbodptoFocusLost
        if (cbodpto.getSelectedIndex() > -1) {
            cbodpto.setSelectedIndex(-1);
            //cboprv.grabFocus();
        }
    }//GEN-LAST:event_cbodptoFocusLost

    private void txtfecdesdePropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_txtfecdesdePropertyChange
        if (evt.getPropertyName().compareTo("date") == 0) {
        }
    }//GEN-LAST:event_txtfecdesdePropertyChange

    private void cboprvItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cboprvItemStateChanged
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            if (hiloPrv == null) {
                hiloPrv = new Thread(this);
                hiloPrv.start();
            }
            //ActualizarGrilla("p.pro_descripcion='" + cboprv.getSelectedItem() + "'");
        }
    }//GEN-LAST:event_cboprvItemStateChanged

    private void cbodptoItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbodptoItemStateChanged
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            if (hiloCat == null) {
                hiloCat = new Thread(this);
                hiloCat.start();
            }
            //ActualizarGrilla("c.cat_nombre='" + cbodpto.getSelectedItem() + "'");
        }
    }//GEN-LAST:event_cbodptoItemStateChanged

    private void tablaOcMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaOcMouseClicked
        ActualizarGrillaDetalle();
    }//GEN-LAST:event_tablaOcMouseClicked

    private void menSeleccionarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menSeleccionarActionPerformed
//        OC.txtoc.setText(String.valueOf(tablaOc.getValueAt(tablaOc.getSelectedRow(), 0)));
        ContadorOc=String.valueOf(arCodigo.get(tablaOc.getSelectedRow()));
        this.dispose();
    }//GEN-LAST:event_menSeleccionarActionPerformed

    private void tablaOcMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaOcMouseReleased
        if (evt.isPopupTrigger()) {
            menu.show(tablaOc, evt.getX(), evt.getY());
        }
    }//GEN-LAST:event_tablaOcMouseReleased

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        if (hiloFec == null) {
            hiloFec = new Thread(this);
            hiloFec.start();
        }
    }//GEN-LAST:event_jButton1ActionPerformed

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
            java.util.logging.Logger.getLogger(BuscarOC.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(BuscarOC.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(BuscarOC.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(BuscarOC.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                BuscarOC dialog = new BuscarOC(new javax.swing.JDialog(), true);
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
    private javax.swing.JComboBox cbodpto;
    private javax.swing.JComboBox cboprv;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JMenuItem menSeleccionar;
    private javax.swing.JPopupMenu menu;
    private javax.swing.JTable tablaDetalle;
    private javax.swing.JTable tablaOc;
    private com.toedter.calendar.JDateChooser txtfecdesde;
    // End of variables declaration//GEN-END:variables
}
