/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package PaqFormulario;

import clases.Conexion;
import com.mysql.jdbc.Connection;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.InputMap;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JRViewer;
import net.sf.jasperreports.view.JasperViewer;
import org.apache.log4j.BasicConfigurator;

/**
 *
 * @author Danny Veron
 */
public class ReporteOC extends javax.swing.JDialog {

    private Connection cn = null;
    private ResultSet rs = null;
    static logoPrincipal logo;
    private Conexion con;
    private ResultSet rscboCat;
    private Object Codtur;
    private Object Codcar;
    static boolean ConsultaVisible;
    private ResultSet rscboTip;
    private ResultSet rscboPrv;
    private String Codproveedor;
    private String Codcatedra;
    private String Codtipo;

    /**
     * Creates new form ReporteEstadoDeCuenta
     */
    public ReporteOC(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        botonEnter();
        cargaCboCat();
        cargaCboPrv();
        //cargaCboTip();
        centrar();
    }

    private void botonEnter() {
        InputMap map = new InputMap();
        map.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false), "pressed");
        map.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, true), "released");
//        btnAcar.setInputMap(0, map);
//        btntipo.setInputMap(0, map);
        btnAver.setInputMap(0, map);
    }

    private void centrar() {
        Dimension pantalla = Toolkit.getDefaultToolkit().getScreenSize();
        this.setSize(pantalla.getSize());
        Dimension frame = this.getSize();
        setLocation((pantalla.width / 2 - (frame.width / 2)), 0);//pantalla.height / 2 - (frame.height / 2));
    }

    private void conexion() {
        con = new Conexion(AcesoUsuario.host, AcesoUsuario.base, AcesoUsuario.user, AcesoUsuario.pass);
    }

    private void cargaCboCat() {
        try {
            conexion();
            String sql = "SELECT * FROM catedra WHERE cat_sede='" + AcesoUsuario.sede
                    + "' ORDER BY cat_nombre";
            rscboCat = con.Consulta(sql);
            cbodep.removeAllItems();
            cbodep.addItem("");
            while (rscboCat.next()) {
                try {
                    cbodep.addItem(rscboCat.getString(2));
                } catch (SQLException ex) {
                    Logger.getLogger(ReporteOC.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            con.CerrarConexion();
        } catch (SQLException ex) {
            Logger.getLogger(ReporteOC.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

//    private void cargaCboTip() {
//        try {
//            conexion();
//            String sql = "SELECT * FROM tipo_articulo ORDER BY tip_descripcion";
//            rscboTip = con.Consulta(sql);
//            cbotip.removeAllItems();
//            cbotip.addItem("");
//            while (rscboTip.next()) {
//                try {
//                    cbotip.addItem(rscboTip.getString(3));
//                } catch (SQLException ex) {
//                    Logger.getLogger(ReporteOC.class.getName()).log(Level.SEVERE, null, ex);
//                }
//            }
//            con.CerrarConexion();
//        } catch (SQLException ex) {
//            Logger.getLogger(ReporteOC.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }

    private void cargaCboPrv() {
        try {
            conexion();
            String sql = "SELECT * FROM proveedor ORDER BY pro_descripcion";
            rscboPrv = con.Consulta(sql);
            cboprv.removeAllItems();
            cboprv.addItem("");
            while (rscboPrv.next()) {
                try {
                    cboprv.addItem(rscboPrv.getString(2));
                } catch (SQLException ex) {
                    Logger.getLogger(ReporteOC.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            con.CerrarConexion();
        } catch (SQLException ex) {
            Logger.getLogger(ReporteOC.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private String comboCodigo(String consulta, String ColumnaBD, String descripcion) {
        String codigo = "";
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
            Logger.getLogger(ReporteOC.class.getName()).log(Level.SEVERE, null, ex);
        }
        return codigo;
    }

    private void capturarCodigoCombos() {
        if (cboprv.getSelectedIndex() > 0) {
            Codproveedor = comboCodigo("SELECT * FROM proveedor", "pro_descripcion", (String) cboprv.getSelectedItem());
            //cboprv.grabFocus();
        }
        if (cbodep.getSelectedIndex() > 0) {
            try {
                conexion();rs=con.Consulta("SELECT * FROM catedra WHERE cat_nombre='"+cbodep.getSelectedItem()+"' AND cat_sede='" + AcesoUsuario.sede+"'" );
                rs.next();
                Codcatedra=rs.getString(1);
                con.CerrarConexion();
                //cboprv.grabFocus();
            } catch (SQLException ex) {
                Logger.getLogger(ReporteOC.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
//        if (cbotip.getSelectedIndex() > 0) {
//            Codtipo = comboCodigo("SELECT * FROM tipo_articulo", "tip_descripcion", (String) cbotip.getSelectedItem());
//            //cboprv.grabFocus();
//        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        grupBoton = new javax.swing.ButtonGroup();
        panel = new javax.swing.JPanel();
        panelVertodo = new javax.swing.JPanel();
        btnAver = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        txtcod = new javax.swing.JTextField();
        panelCi = new javax.swing.JPanel();
        btnAcar1 = new javax.swing.JButton();
        txtfecdesde = new com.toedter.calendar.JDateChooser("dd/MM/yyyy", "####/##/##", '_');
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txtfechasta = new com.toedter.calendar.JDateChooser("dd/MM/yyyy", "####/##/##", '_');
        jLabel26 = new javax.swing.JLabel();
        cbodep = new javax.swing.JComboBox();
        jLabel24 = new javax.swing.JLabel();
        cboprv = new javax.swing.JComboBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Informes-OC");

        panel.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        panel.setLayout(new java.awt.CardLayout());

        panelVertodo.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(51, 153, 0), 2, true), "Busqueda Por Nº de OC Detallado", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(0, 0, 153))); // NOI18N

        btnAver.setText("Actualizar");
        btnAver.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAverActionPerformed(evt);
            }
        });

        jLabel6.setText("O.C. Nº");

        javax.swing.GroupLayout panelVertodoLayout = new javax.swing.GroupLayout(panelVertodo);
        panelVertodo.setLayout(panelVertodoLayout);
        panelVertodoLayout.setHorizontalGroup(
            panelVertodoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelVertodoLayout.createSequentialGroup()
                .addGap(52, 52, 52)
                .addComponent(btnAver)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelVertodoLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtcod, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        panelVertodoLayout.setVerticalGroup(
            panelVertodoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelVertodoLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(panelVertodoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(txtcod, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(43, 43, 43)
                .addComponent(btnAver)
                .addGap(26, 26, 26))
        );

        panelCi.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(51, 153, 0), 2, true), "Busqueda por fecha", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(0, 0, 153))); // NOI18N

        btnAcar1.setText("Actualizar");
        btnAcar1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAcar1ActionPerformed(evt);
            }
        });

        txtfecdesde.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel1.setText("Desde");

        jLabel2.setText("Hasta");

        txtfechasta.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel26.setText("Dependencia");

        cbodep.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel24.setText("Proveedor");

        cboprv.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        javax.swing.GroupLayout panelCiLayout = new javax.swing.GroupLayout(panelCi);
        panelCi.setLayout(panelCiLayout);
        panelCiLayout.setHorizontalGroup(
            panelCiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelCiLayout.createSequentialGroup()
                .addGroup(panelCiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelCiLayout.createSequentialGroup()
                        .addGap(55, 55, 55)
                        .addGroup(panelCiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel26, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel24, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelCiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelCiLayout.createSequentialGroup()
                                .addComponent(txtfecdesde, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtfechasta, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(cbodep, javax.swing.GroupLayout.PREFERRED_SIZE, 221, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cboprv, javax.swing.GroupLayout.PREFERRED_SIZE, 369, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(panelCiLayout.createSequentialGroup()
                        .addGap(195, 195, 195)
                        .addComponent(btnAcar1)))
                .addContainerGap(23, Short.MAX_VALUE))
        );
        panelCiLayout.setVerticalGroup(
            panelCiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelCiLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelCiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel1)
                    .addComponent(txtfecdesde, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(txtfechasta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelCiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbodep, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel26))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelCiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cboprv, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel24))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnAcar1)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(222, Short.MAX_VALUE)
                .addComponent(panelVertodo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelCi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(182, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(panelCi, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelVertodo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panel, javax.swing.GroupLayout.DEFAULT_SIZE, 542, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnAverActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAverActionPerformed
        try {
            ClassLoader cl = this.getClass().getClassLoader();
            InputStream fis = (cl.getResourceAsStream("paqInformes/OcPorNro.jasper"));
            JasperReport reporte = (JasperReport) JRLoader.loadObject(fis);
            conexion();
            String sql="SELECT o.ocab_codigo,p.pro_descripcion,o.ocab_fecha,o.ocab_plazo_entrega,"
                    + "c.cat_nombre FROM ordencab o,catedra c,proveedor p "
                    + "WHERE c.cat_codigo=o.cat_codigo AND p.pro_codigo=o.pro_codigo AND o.ocab_codigo='"
                    +txtcod.getText()+"'";
            rs=con.Consulta(sql);
            if(rs.next()){
            Map par = new HashMap();
            par.put("oc", rs.getObject(1));
            par.put("proveedor", rs.getObject(2));
            par.put("fechaemision", rs.getDate(3));
            par.put("plazoentrega", rs.getDate(4));
            par.put("dependencia", rs.getObject(5));
            JasperPrint jasperPrint;
            jasperPrint = JasperFillManager.fillReport(reporte, par, con.getConexion());
            panel.removeAll();
            panel.updateUI();
            panel.add(new JRViewer(jasperPrint), "Panel1");
            panel.updateUI();
            con.CerrarConexion();
            }else{
                JOptionPane.showMessageDialog(this, "El codigo no existe");
            }
        } catch (SQLException ex) {
            Logger.getLogger(ReporteOC.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JRException ex) {
            Logger.getLogger(ReporteOC.class.getName()).log(Level.SEVERE, null, ex);
        }

        /*if(HiloVerTodo==null){
         HiloVerTodo=new Thread(this);
         HiloVerTodo.start();
         }*/
    }//GEN-LAST:event_btnAverActionPerformed

    private void btnAcar1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAcar1ActionPerformed
        try {
            capturarCodigoCombos();
            SimpleDateFormat d = new SimpleDateFormat("yyyy-MM-dd");
            Map par = new HashMap();
            String informe="OcPorFecha.jasper";
            par.put("fecdesde", d.format(txtfecdesde.getDate()));
            par.put("fechasta", d.format(txtfechasta.getDate()));
            if(cbodep.getSelectedIndex()!=0 && cboprv.getSelectedIndex()==0 ){
                par.put("catedra", Codcatedra); 
                informe="OcPorCatedra.jasper";
            }
            if(cbodep.getSelectedIndex()==0 && cboprv.getSelectedIndex()!=0 ){
                par.put("proveedor", Codproveedor);
                informe="OcPorProveedor.jasper";
            }
            if(cbodep.getSelectedIndex()!=0 && cboprv.getSelectedIndex()!=0 ){
                par.put("catedra",Codcatedra);
                par.put("proveedor", Codproveedor);
                informe="OcPorCatedraProveedor.jasper";
            }
            ClassLoader cl = this.getClass().getClassLoader();
            InputStream fis = (cl.getResourceAsStream("paqInformes/"+informe));
            JasperReport reporte = (JasperReport) JRLoader.loadObject(fis);
            conexion();
            
            JasperPrint jasperPrint = JasperFillManager.fillReport(reporte, par, con.getConexion());
            panel.removeAll();
            panel.updateUI();
            panel.add(new JRViewer(jasperPrint), "Panel1");
            panel.updateUI();
            Codcar = "";
        } catch (JRException ex) {
            Logger.getLogger(ReporteOC.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NullPointerException n){
            JOptionPane.showMessageDialog(this, "Tiene que ingresar fecha ''Desde''; ''Hasta''!!!","Error",JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnAcar1ActionPerformed

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
            java.util.logging.Logger.getLogger(ReporteOC.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ReporteOC.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ReporteOC.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ReporteOC.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                ReporteOC dialog = new ReporteOC(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton btnAcar1;
    private javax.swing.JButton btnAver;
    private javax.swing.JComboBox cbodep;
    private javax.swing.JComboBox cboprv;
    private javax.swing.ButtonGroup grupBoton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel panel;
    private javax.swing.JPanel panelCi;
    private javax.swing.JPanel panelVertodo;
    public static javax.swing.JTextField txtcod;
    private com.toedter.calendar.JDateChooser txtfecdesde;
    private com.toedter.calendar.JDateChooser txtfechasta;
    // End of variables declaration//GEN-END:variables
}
