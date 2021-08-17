/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package PaqFormulario;

import clases.Conexion;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ItemEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;
import org.omg.SendingContext.RunTime;

/**
 *
 * @author Daniverm
 */
public class FrmImprimir extends javax.swing.JDialog {

    Conexion con;
    ResultSet rs;
    private ResultSet rsCodPrv;
    private String CodPrv;
    private ArrayList arContador;
    private String rutaImprimir;

    /**
     * Creates new form FrmImprimir
     */
    public FrmImprimir(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        centrar();
        cargaCboPrv();
        leearchivodb();
        arContador = new ArrayList();
    }
    public void leearchivodb() {
        //InputStream f = getClass().getResourceAsStream("/clases/config.txt");
        //BufferedReader br = new BufferedReader(new InputStreamReader(f));
        File f = new File("C:\\OC\\Config\\config.txt");
        String s = "";
        //lee caracteres contenidos en archivo
        try {
            FileReader fr = new FileReader(f);
            BufferedReader br = new BufferedReader(fr);
            s = br.readLine();
            //System.out.println(s);
            //para leer palabra por palabra desde el archivo
            StringTokenizer tk = new StringTokenizer(s, "-");
           rutaImprimir = tk.nextToken();
           // System.out.println("Ruta para pdf: "+rutaImprimir);
        } catch (FileNotFoundException e) {
            System.out.println("Error al abrir el archivo");
        } catch (IOException e) {
            System.out.println("Error al leer");
        }
      
    }

    private void conexion() {
        con = new Conexion(AcesoUsuario.host, AcesoUsuario.base, AcesoUsuario.user, AcesoUsuario.pass);
    }

    private void centrar() {
        Dimension pantalla = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frame = this.getSize();
        setLocation((pantalla.width / 2 - (frame.width / 2)), pantalla.height / 2 - (frame.height / 2));
    }
    
    private void CerrarPdf() {
        String os = System.getProperty("os.name");
        if (os.contains("Windows")) {
            try {
                Runtime.getRuntime().exec("taskkill /F /IM AcroRd32.exe");
            } catch (IOException ex) {
                Logger.getLogger(OC.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void cargaCboPrv() {
        try {
            String sql = "SELECT * FROM proveedor ORDER BY pro_descripcion";
            conexion();
            rsCodPrv = con.Consulta(sql);
            cboprv.removeAllItems();
            cboprv.addItem("");
            while (rsCodPrv.next()) {
                try {
                    cboprv.addItem(rsCodPrv.getString("pro_descripcion"));
                } catch (SQLException ex) {
                    Logger.getLogger(OC.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            con.CerrarConexion();
        } catch (SQLException ex) {
            Logger.getLogger(OC.class.getName()).log(Level.SEVERE, null, ex);
        }
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

    private void capturarCodigoCombos() {
        if (cboprv.getSelectedIndex() > 0) {
            CodPrv = comboCodigo("SELECT * FROM proveedor", "pro_descripcion", (String) cboprv.getSelectedItem());
        }
    }

    private void actualizaTabla(String and) {
        try {
            SimpleDateFormat d = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat d2 = new SimpleDateFormat("dd-MM-yyyy");
            String sql = "SELECT o.ocab_codigo,o.fec_carga,p.pro_descripcion,o.ocab_total,o.ocab_contador"
                    + ",t.tip_des,m.mod_cod,m.mod_des"
                    + " FROM ordencab o,proveedor p,modalidad m,tipo_modalidad t WHERE p.pro_codigo=o.pro_codigo "
                    + "AND t.tip_cod=m.tip_cod AND m.mod_cod=o.mod_cod AND o.fec_carga BETWEEN '"
                    + d.format(txtdesde.getDate()) + "' AND '"
                    + d.format(txthasta.getDate()) + "' "
                    + "AND usu_cod=" + AcesoUsuario.CodigoUsuario
                    + " " + and;
            System.out.println(sql);
            conexion();
            rs = con.Consulta(sql);
            DefaultTableModel tm = (DefaultTableModel) tabla.getModel();
            DecimalFormat separador = new DecimalFormat("###,###");
            if (rs.next()) {
                rs.beforeFirst();
                while (rs.next()) {
                    String dat=null;
                    if(rs.getDate(2)!=null){
                        dat=d2.format(rs.getDate(2));
                    }
                    arContador.add(rs.getObject("o.ocab_contador"));
                    tm.addRow(new Object[]{rs.getString(1), dat, rs.getString(3),
                                separador.format(rs.getDouble(4)), rs.getString(6)+" - "+rs.getString(8)});
                    tabla.setModel(tm);
                }
                con.CerrarConexion();
            }
        } catch (SQLException ex) {
            Logger.getLogger(FrmImprimir.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private String separadorDeMiles(double nro) {
        double num = nro;
        DecimalFormat formateador = new DecimalFormat("###,###");
        return String.valueOf(formateador.format(num));
    }

    private void imprimirOc(String codigo) {
        try {
            String sql = "SELECT p.pro_descripcion,o.ocab_fecha,o.ocab_plazo_entrega,"
                    + "i.imp_des,o.ocab_gra10,o.ocab_total,o.ocab_iva10,o.ocab_entrega,"
                    + "o.ocab_director,o.ocab_decano,o.ocab_gra5,o.ocab_iva5,o.tipo_presup"
                    + ",o.programa,o.sub_prog,o.proyecto,o.fuente_finan,o.org_financ,"
                    + "o.obj_gasto,o.cdp,o.fec_vigencia,o.fec_al FROM ordencab o"
                    + ",proveedor p,imputacion i WHERE p.pro_codigo=o.pro_codigo AND i.imp_cod=o.imp_cod "
                    + "AND o.ocab_contador='" + codigo + "'";
            //System.out.println(sql);
            conexion();
            rs = con.Consulta(sql);
            rs.next();
            String hc = "", cmi = "";
            // para ver el lugar de entrega
            if (rs.getString("ocab_entrega").equals("HOSPITAL DE CLINICAS ASUNCION")) {
                hc = "x";
                cmi = "";
            } else if (rs.getString("ocab_entrega").equals("HOSPITAL DE CLINICAS SAN LORENZO")) {
                hc = "";
                cmi = "x";
            }
            //btngra.doClick();
            SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy"); //DEFINE FORMATO DE DATA
            String fecemi = "";
            String fecpla = "";
            String vigencia = "";
            String al = "";
            String lpn = "";
            String lco = "";
            String cd = "";
            String cdPorExcep = "";
            String cExcluida = "";
            String cExcluida2 = "";

            if (rs.getDate("fec_vigencia") != null) {
                vigencia = formato.format(rs.getDate("fec_vigencia"));
            }
            if (rs.getDate("fec_al") != null) {
                al = formato.format(rs.getDate("fec_al"));
            }
            StringTokenizer token = new StringTokenizer(String.valueOf(tabla.getValueAt(tabla.getSelectedRow(), 4)), "-");
            String modalidad = token.nextToken();
            String modalidad2 = token.nextToken();
            //System.out.println("Modalidad= "+modalidad+" Modalidad2= "+modalidad2);
            if (modalidad.equals("L.P.N. ")) {
                lpn = modalidad2;
            }
            if (modalidad.equals("L.C.O. ")) {
                lco = modalidad2;
            }
            if (modalidad.equals("C.D. ")) {
                cd = modalidad2;
            }
            if (modalidad.equals("C.D. Por Excep. ")) {
                cdPorExcep = modalidad2;
            }
            if (modalidad.equals("C. Excluida ")) {
                cExcluida2 = modalidad2;
            }
            if (modalidad.equals("C. Excluida ") && modalidad2.equals(" ")) {
                cExcluida = "X";
            }
            try {
                fecemi = formato.format(rs.getDate("ocab_fecha"));
                fecpla = formato.format(rs.getDate("ocab_plazo_entrega"));

            } catch (NullPointerException n) {
            }
            //System.out.println("Subtotales: " + separadorDeMiles(rs.getDouble("ocab_gra10")));
            Map par = new HashMap();
            //JRTableModelDataSource jrtmd = new JRTableModelDataSource(tabla.getModel());
            ClassLoader cl = this.getClass().getClassLoader();
            InputStream fis = (cl.getResourceAsStream("paqInformes/ImprimirOc2.jasper"));
            par.put("ocCod", codigo);
            par.put("hc", hc);
            par.put("cmi", cmi);
            par.put("senores", rs.getString("pro_descripcion"));
            par.put("fechaDeEmision", fecemi);
            par.put("fechaDeEntrega", fecpla);
            par.put("imputacionPresupuestaria", rs.getString("imp_des"));
            par.put("subTotales", separadorDeMiles(rs.getDouble("ocab_gra10")));
            par.put("totalGeneral", separadorDeMiles(rs.getDouble("ocab_total")));
            par.put("liquidacionDelIva", separadorDeMiles(rs.getDouble("ocab_iva10")));
            par.put("totalIva", separadorDeMiles(rs.getDouble("ocab_iva10") + rs.getDouble("ocab_iva5")));
            par.put("son", OC.NumericoToLetra(String.valueOf(Math.round(rs.getFloat("ocab_total")))));
            par.put("lugarDeEntrega", rs.getString("ocab_entrega"));
            par.put("admin", rs.getString("ocab_director"));
            par.put("decano", rs.getString("ocab_decano"));
            par.put("subtotal5", separadorDeMiles(rs.getDouble("ocab_gra5")));
            par.put("liquidacionDelIva5", separadorDeMiles(rs.getDouble("ocab_iva5")));
            par.put("tipoPresup", rs.getString("tipo_presup"));
            par.put("programa", rs.getString("programa"));
            par.put("subPrograma", rs.getString("sub_prog"));
            par.put("proyecto", rs.getString("proyecto"));
            par.put("fuenteFinan", rs.getString("fuente_finan"));
            par.put("orgFinan", rs.getString("org_financ"));
            par.put("objGasto", rs.getString("obj_gasto"));
            par.put("CDP", rs.getString("cdp"));
            par.put("vigencia", vigencia);
            par.put("al", al);
            par.put("lpn", lpn);
            par.put("lco", lco);
            par.put("cd", cd);
            par.put("cdPorExcep", cdPorExcep);
            par.put("cExcluida", cExcluida);
            par.put("cExcluida2", cExcluida2);
            //se carga el reporte
            JasperReport jasperReport;
            JasperPrint jasperPrint;

            jasperReport = (JasperReport) JRLoader.loadObject(fis);
            //se procesa el archivo jasper
            jasperPrint = JasperFillManager.fillReport(jasperReport, par, con.getConexion());
            //se crea el archivo PDF
            JasperExportManager.exportReportToPdfFile(jasperPrint,  rutaImprimir+"\\reporte.pdf");
            File f = new File(rutaImprimir+"\\reporte.pdf");
            java.awt.Desktop.getDesktop().open(f);
            con.CerrarConexion();

            //Exportando a excel
            /*JRXlsExporter exporter = new JRXlsExporter();
             exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
             exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, "c:\\ImprimirOc.xls");
             exporter.setParameter(JRXlsExporterParameter.IS_ONE_PAGE_PER_SHEET, Boolean.TRUE);
             exporter.exportReport();
             //imprimirXLS();
             File f = new File("c:\\ImprimirOc.xls");
             java.awt.Desktop.getDesktop().print(f);*/
            //JasperPrintManager.printReport(jasperPrint, true);
            // vista= new JasperViewer(jasperPrint, false);
            //vista.setVisible(true);
            //JasperPrintManager.printReport(jasperPrint, false);
        } catch (IOException ex) {
            Logger.getLogger(OC.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(FrmImprimir.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JRException ex) {
            Logger.getLogger(OC.class.getName()).log(Level.SEVERE, null, ex);
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

        menuEmergente = new javax.swing.JPopupMenu();
        menimp = new javax.swing.JMenuItem();
        jScrollPane1 = new javax.swing.JScrollPane();
        tabla = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        txtdesde = new com.toedter.calendar.JDateChooser("dd/MM/yyyy", "####/##/##", '_');
        btnsal = new javax.swing.JButton();
        btnlim = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        txthasta = new com.toedter.calendar.JDateChooser("dd/MM/yyyy", "####/##/##", '_');
        jLabel3 = new javax.swing.JLabel();
        cboprv = new javax.swing.JComboBox();

        menimp.setIcon(new javax.swing.ImageIcon(getClass().getResource("/paqImagenes/print.png"))); // NOI18N
        menimp.setText("Imprimir");
        menimp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menimpActionPerformed(evt);
            }
        });
        menuEmergente.add(menimp);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Imprimir Ordenes de Compra");

        tabla.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "OC Nº", "Fecha", "Proveedor", "Monto", "Modalidad"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tabla.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                tablaMouseReleased(evt);
            }
        });
        jScrollPane1.setViewportView(tabla);
        if (tabla.getColumnModel().getColumnCount() > 0) {
            tabla.getColumnModel().getColumn(0).setPreferredWidth(100);
            tabla.getColumnModel().getColumn(0).setMaxWidth(150);
            tabla.getColumnModel().getColumn(1).setPreferredWidth(70);
            tabla.getColumnModel().getColumn(1).setMaxWidth(90);
            tabla.getColumnModel().getColumn(3).setPreferredWidth(100);
            tabla.getColumnModel().getColumn(3).setMaxWidth(100);
        }

        jLabel1.setText("Desde");

        txtdesde.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        txtdesde.setDateFormatString("dd/MM/yyyy");
        txtdesde.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                txtdesdePropertyChange(evt);
            }
        });

        btnsal.setIcon(new javax.swing.ImageIcon(getClass().getResource("/paqImagenes/salirPuerta.png"))); // NOI18N
        btnsal.setText("Salir");
        btnsal.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnsal.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnsal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnsalActionPerformed(evt);
            }
        });

        btnlim.setIcon(new javax.swing.ImageIcon(getClass().getResource("/paqImagenes/Icono-limpiar.png"))); // NOI18N
        btnlim.setText("Limpiar");
        btnlim.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnlim.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnlim.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnlimActionPerformed(evt);
            }
        });

        jLabel2.setText("Hasta");

        txthasta.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        txthasta.setDateFormatString("dd/MM/yyyy");
        txthasta.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                txthastaPropertyChange(evt);
            }
        });

        jLabel3.setText("Proveedor");

        cboprv.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        cboprv.setEnabled(false);
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

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1)
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtdesde, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txthasta, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cboprv, 0, 429, Short.MAX_VALUE)
                        .addContainerGap())))
            .addGroup(layout.createSequentialGroup()
                .addGap(304, 304, 304)
                .addComponent(btnlim)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnsal)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnlim, btnsal});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txtdesde, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(txthasta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3)
                    .addComponent(cboprv, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnlim)
                    .addComponent(btnsal))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnlim, btnsal});

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnsalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnsalActionPerformed
        this.dispose();
    }//GEN-LAST:event_btnsalActionPerformed

    private void tablaMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaMouseReleased
        if (evt.isPopupTrigger()) {
            menuEmergente.show(tabla, evt.getX(), evt.getY());
        }
    }//GEN-LAST:event_tablaMouseReleased

    private void txtdesdePropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_txtdesdePropertyChange
        if (txthasta.getDate() != null) {
            DefaultTableModel t = (DefaultTableModel) tabla.getModel();
            while (t.getRowCount() >= 1) {
                t.removeRow(0);
            }
            txthasta.setDate(null);
            cboprv.setSelectedIndex(0);
            cboprv.setEnabled(false);
        }
    }//GEN-LAST:event_txtdesdePropertyChange

    private void menimpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menimpActionPerformed
        if (tabla.getValueAt(tabla.getSelectedRow(), 0).equals("")) {
            String input = JOptionPane.showInputDialog(this, "Ingrese Nº de O.C.");
            if (!input.isEmpty()) {
                try {
                    String consulta = "SELECT * FROM ordencab WHERE ocab_codigo='" + input + "'";
                    conexion();
                    rs = con.Consulta(consulta);
                    if (rs.next()) {
                        JOptionPane.showMessageDialog(this, "El Numero de Orden ya existe");
                    } else {
                        String update = "UPDATE ordencab SET ocab_codigo='" + input + "' WHERE "
                                + "ocab_contador=" + arContador.get(tabla.getSelectedRow());
                        System.out.println(update);
                        con.Guardar(update);
                        imprimirOc(String.valueOf(arContador.get(tabla.getSelectedRow())));
                        if (txtdesde.getDate() != null) {
                            DefaultTableModel t = (DefaultTableModel) tabla.getModel();
                            while (t.getRowCount() >= 1) {
                                t.removeRow(0);
                            }
                            tabla.setModel(t);
                            actualizaTabla("");
                            cboprv.setEnabled(true);
                        }
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(FrmImprimir.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } else {
            imprimirOc(String.valueOf(arContador.get(tabla.getSelectedRow())));
        }
    }//GEN-LAST:event_menimpActionPerformed

    private void txthastaPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_txthastaPropertyChange
        if (evt.getPropertyName().equals("date") && txthasta.getDate() != null) {
            if (txtdesde.getDate() != null) {
                DefaultTableModel t = (DefaultTableModel) tabla.getModel();
                while (t.getRowCount() >= 1) {
                    t.removeRow(0);
                }
                tabla.setModel(t);
                actualizaTabla("");
                cboprv.setEnabled(true);
            }
        }
    }//GEN-LAST:event_txthastaPropertyChange

    private void cboprvFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cboprvFocusLost
    }//GEN-LAST:event_cboprvFocusLost

    private void cboprvItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cboprvItemStateChanged
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            if (cboprv.getSelectedIndex() > 0) {
                DefaultTableModel t = (DefaultTableModel) tabla.getModel();
                while (t.getRowCount() >= 1) {
                    t.removeRow(0);
                }
                capturarCodigoCombos();
                actualizaTabla("AND p.pro_codigo=" + CodPrv);
            }
        }
    }//GEN-LAST:event_cboprvItemStateChanged

    private void btnlimActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnlimActionPerformed

        DefaultTableModel tm = (DefaultTableModel) tabla.getModel();
        while (tm.getRowCount() > 0) {
            tm.removeRow(0);
        }
        tabla.setModel(tm);
        txtdesde.setDate(null);
        txthasta.setDate(null);
        cboprv.setSelectedIndex(0);
    }//GEN-LAST:event_btnlimActionPerformed

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
            java.util.logging.Logger.getLogger(FrmImprimir.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(FrmImprimir.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(FrmImprimir.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FrmImprimir.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                FrmImprimir dialog = new FrmImprimir(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton btnlim;
    private javax.swing.JButton btnsal;
    private javax.swing.JComboBox cboprv;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JMenuItem menimp;
    private javax.swing.JPopupMenu menuEmergente;
    private javax.swing.JTable tabla;
    private com.toedter.calendar.JDateChooser txtdesde;
    private com.toedter.calendar.JDateChooser txthasta;
    // End of variables declaration//GEN-END:variables
}
