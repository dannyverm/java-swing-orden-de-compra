/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package PaqFormulario;

import static PaqFormulario.AcesoUsuario.base;
import static PaqFormulario.AcesoUsuario.host;
import static PaqFormulario.AcesoUsuario.pass;
import static PaqFormulario.AcesoUsuario.user;
import clases.Conexion;
import com.lowagie.text.pdf.PdfWriter;
import com.mysql.jdbc.Connection;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.print.attribute.DocAttributeSet;
import javax.print.attribute.HashDocAttributeSet;
import javax.swing.InputMap;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.Document;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRTableModelDataSource;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;

/**
 *
 * @author Danny Veron
 */
public class OCDirecta extends javax.swing.JDialog implements Runnable {

    private Thread irImprimir;
    private Connection cn = null;
    private ResultSet rs = null;
    private ResultSet rsCodPrv = null;
    private ResultSet rsCodDpto = null;
    private ResultSet rsCodPago = null;
    private String CodPrv = null;
    static String CodDpto = null;
    private String codPago = null;
    private String textoTabla = null;
    public static String Departamento;
    public boolean botonagregar = false;
    public boolean botonmodificar = false;
    private boolean botoneliminar = false;
    public static boolean buelta = false;
    private InputMap map;
    private Conexion con;
    static String catedraOC = null;
    static String cantidadOC = null;
    static boolean OCvisible = false;
    private String Total;
    private double sumaTotalGral;
    private double sumaTotalExenta;
    private double sumaTotalIva5;
    private double sumaTotalIva10;
    private double liquidacionIva5;
    private double liquidacionIva10;
    private int c = 0;
    private boolean booleanFila = false;
    private String codImputacion;
    private boolean editableImputacion;
    private boolean validarFocus = false;
    private String DepartamentoXDefecto;
    static String DatoBotonBusqueda = "";
    static boolean pulsaboton = false;
    private boolean banNuevoArticulo;
    private boolean validaCeldaCodigo = false;
    private ArrayList arModalidad;
    private ResultSet rsCodMod;
    private String contadorOc;
    private java.util.Date fecha;
    private String rutaImprimir;

    /**
     * Creates new form OC
     */
    public OCDirecta(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        arModalidad = new ArrayList();
        cargaCboDepto();
        cargaCboPago();
        cargaCboPrv();
        botonEnter();
        txtCero();
        centrar();
        cargaCboImputacion();
        cargaCboMod();
        fecha = new java.util.Date();
        txtfecemi.setDate(fecha);
        agregarOyenteFechaEmision();
        btnnue.grabFocus();
        cbolug.setSelectedIndex(-1);
        leearchivodb();
        this.pack();
    }

    private void centrar() {
        Dimension pantalla = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frame = this.getSize();
        setLocation((pantalla.width / 2 - (frame.width / 2)), pantalla.height / 2 - (frame.height / 2));

    }

    private void conexion() {
        con = new Conexion(AcesoUsuario.host, AcesoUsuario.base, AcesoUsuario.user, AcesoUsuario.pass);
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
            
        } catch (FileNotFoundException e) {
            System.out.println("Error al abrir el archivo");
        } catch (IOException e) {
            System.out.println("Error al leer");
        }
      
    }

    private void txtCero() {
        txtsubtotIva10.setText("0");
        txtsubtotExentas.setText("0");
        txtsubtotIva5.setText("0");
        txtliqiva10.setText("0");
        txtliqiva5.setText("0");
        txttotgral.setText("0");
        txttotiva.setText("0");
        txtliqiva10.setText("0");
    }

    private void botonEnter() {
        map = new InputMap();
        map.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false), "pressed");
        map.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, true), "released");
        btnnue.setInputMap(0, map);
        btncan.setInputMap(0, map);
        btngra.setInputMap(0, map);
        btnmod.setInputMap(0, map);
        btncer.setInputMap(0, map);
        btneli.setInputMap(0, map);
    }

    private void CerrarPdf() {
        String os = System.getProperty("os.name");
        if (os.contains("Windows")) {
            try {
                Runtime.getRuntime().exec("taskkill /F /IM AcroRd32.exe");
            } catch (IOException ex) {
                Logger.getLogger(OCDirecta.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            try {
                // Assuming a non Windows OS will be some version of Unix, Linux, or Mac 
                Runtime.getRuntime().exec("kill `ps -ef | grep -i firefox | grep -v grep | awk '{print $2}'`");
                Runtime.getRuntime().exec("kill `ps -ef | grep -i chrome | grep -v grep | awk '{print $2}'`");
                Runtime.getRuntime().exec("kill `ps -ef | grep -i safari | grep -v grep | awk '{print $2}'`");
            } catch (IOException ex) {
                Logger.getLogger(OCDirecta.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void agregarOyenteFechaEmision() {
        txtfecemi.addPropertyChangeListener(
                new java.beans.PropertyChangeListener() {
                    @Override
                    public void propertyChange(java.beans.PropertyChangeEvent evt) {
                        if (evt.getPropertyName().compareTo("date") == 0) {
                            txtPlazent.requestFocusInWindow();

                        }
                    }
                });

    }

    private void AjustarDescripcionTabla(String descripcion) {
        c = tablaOC.getSelectedRow();
        while (tablaOC.getValueAt(c, 3) != "") {
            c++;
        }
        String texto = "";
        StringTokenizer token = new StringTokenizer(descripcion);
        //System.out.println("DESCRIPCION: "+descripcion);
        int cont = 0;
        int fila = c;
        //System.out.println("FILA NRO: "+fila);
        while (cont == 0) {
            if (fila > 19) {
                //System.out.println("FILA NRO dentro del if: "+fila);
                JOptionPane.showMessageDialog(this, "La descripción supera el limite de filas",
                        "Error", JOptionPane.ERROR_MESSAGE);
                int borrar = c;
                while (borrar < tablaOC.getRowCount()) {
                    tablaOC.setValueAt("", borrar, 0);
                    tablaOC.setValueAt("", borrar, 1);
                    tablaOC.setValueAt("", borrar, 2);
                    tablaOC.setValueAt("", borrar, 3);
                    tablaOC.setValueAt("", borrar, 4);
                    tablaOC.setValueAt("", borrar, 5);
                    tablaOC.setValueAt("", borrar, 6);
                    tablaOC.setValueAt("", borrar, 7);
                    borrar++;
                }
                break;
            }
            if (texto.length() < 36 && token.hasMoreTokens() == false) {
                texto = texto.replace("|", "");
                tablaOC.setValueAt(texto.trim(), fila, 3);
                break;
            }
            String tok = token.nextToken() + " ";

            texto = texto + tok;
            // System.out.println("Texto length: " + texto.length());
            if (texto.length() > 35) {
                int Reemplazar = tok.length();
                tablaOC.setValueAt((texto.substring(0, texto.length() - Reemplazar)), fila, 3);
                texto = tok;
                fila++;
            } else if (tok.trim().equals("|")) {
                int Reemplazar = tok.length();
                texto = texto.replace("|", "");
                tablaOC.setValueAt((texto.substring(0, texto.length() - Reemplazar)).trim(), fila, 3);
                texto = tok;
                fila++;
            }
        }
        tablaOC.changeSelection(fila, 0, false, false);
    }

    private String sacarPunto(String txt) {
        txt = txt.replace(".", "").replaceAll(",", ".");

        return txt;
    }

    private void vaciarTabla() {
        int i = 0;
        while (i != tablaOC.getRowCount()) {
            tablaOC.setValueAt("", i, 0);
            tablaOC.setValueAt("", i, 1);
            tablaOC.setValueAt("", i, 2);
            tablaOC.setValueAt("", i, 3);
            tablaOC.setValueAt("", i, 4);
            tablaOC.setValueAt("", i, 5);
            tablaOC.setValueAt("", i, 6);
            tablaOC.setValueAt("", i, 7);
            i++;
        }
        tablaOC.changeSelection(0, 0, false, false);
    }

    private void datosEnTabla() {
        //tablaOC.editCellAt(tablaOC.getSelectedRow() + 1, 0);
        textoTabla = (String) tablaOC.getValueAt(tablaOC.getSelectedRow(), 0);

        if (tablaOC.getSelectedColumn() == 0) {
            int nroFila = tablaOC.getSelectedRow();
            if (textoTabla.equals("") || textoTabla.isEmpty() || textoTabla == null) {
                try {
                    catedraOC = (String) cbodpto.getSelectedItem();

                    if (banNuevoArticulo != true) {
                        OCvisible = true;
                        BuscarArticulo bus = new BuscarArticulo(this, true);
                        bus.setVisible(true);
                    }
                    OCvisible = false;

                    String sql = "SELECT * FROM articulo WHERE art_codigo=" + Articulo.codigoArticulo;
                    //System.out.println(sql);
                    if (cantidadOC != null) {
                        //Valnro solo sirve para validar si se ingresa nro o caracter

                        int valnro = Integer.parseInt(cantidadOC);
                        conexion();
                        rs = con.Consulta(sql);
                        if (rs.next()) {
                            int c = 0;
                            boolean ban = false;
                            while (c < tablaOC.getRowCount()) {
                                if (!tablaOC.getValueAt(c, 0).equals("") && c != tablaOC.getSelectedRow()
                                        && String.valueOf(tablaOC.getValueAt(c, 0)).equals(rs.getString(1))) {
                                    ban = true;
                                    break;
                                }
                                c++;
                            }
//                            if (ban == true) {
//                                JOptionPane.showMessageDialog(this, "El articulo selecionado está incluido en esta Orden de Compras");
//                                tablaOC.setValueAt("", tablaOC.getSelectedRow(), 0);
//                                booleanFila = true;
//                            } else {
                                tablaOC.setValueAt(rs.getString(1), nroFila, 0);
                                tablaOC.setValueAt(cantidadOC, nroFila, 1);
                                tablaOC.setValueAt(rs.getString(6), nroFila, 2);
                                tablaOC.setValueAt(separadorDeMiles(rs.getDouble(5)), nroFila, 4);
                                int colum = CalculoIva(cantidadOC, rs.getString(5), rs.getString("art_impuesto"));
                                tablaOC.setValueAt(Total, nroFila, colum);
                                AjustarDescripcionTabla(rs.getString(4));
                                //System.out.println(rs.getString(4));
//                            }
                            con.CerrarConexion();

                        }
                    } else {
                        booleanFila = true;
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(OCDirecta.class.getName()).log(Level.SEVERE, null, ex);
                } catch (NullPointerException n) {
                    JOptionPane.showMessageDialog(null, "El codigo ingresado no es valido", "Error", JOptionPane.ERROR_MESSAGE);
                    tablaOC.setValueAt("", tablaOC.getSelectedRow(), 0);
                    booleanFila = true;
                } catch (NumberFormatException n) {
                    JOptionPane.showMessageDialog(null, "El codigo ingresado no es valido", "Error", JOptionPane.ERROR_MESSAGE);
                    tablaOC.setValueAt("", tablaOC.getSelectedRow(), 0);
                    booleanFila = true;
                }
            } else {
                try {
                    String sql = "SELECT * FROM articulo WHERE art_codigo=" + textoTabla.replaceAll("null", "");
                    System.out.println(sql);
                    conexion();
                    rs = con.Consulta(sql);
                    if (rs.next()) {
                        int c = 0;
                        boolean ban = false;
                        while (c < tablaOC.getRowCount()) {
                            if (!tablaOC.getValueAt(c, 0).equals("") && c != tablaOC.getSelectedRow()
                                    && String.valueOf(tablaOC.getValueAt(c, 0)).equals(rs.getString(1))) {
                                ban = true;
                                break;
                            }
                            c++;
                        }
//                        if (ban == true) {
//                            JOptionPane.showMessageDialog(this, "El articulo selecionado está incluido en esta Orden de Compras");
//                            tablaOC.setValueAt("", tablaOC.getSelectedRow(), 0);
//                            booleanFila = true;
//                        } else {
                            String res = JOptionPane.showInputDialog(null, "Ingrese Cantidad para el articulo");
                            if (!res.isEmpty()) {
                                tablaOC.setValueAt(rs.getString(1), nroFila, 0);
                                tablaOC.setValueAt(res, nroFila, 1);
                                tablaOC.setValueAt(rs.getString(6), nroFila, 2);
                                tablaOC.setValueAt(separadorDeMiles(rs.getDouble(5)), nroFila, 4);
                                int colum = CalculoIva(res, rs.getString(5), rs.getString("art_impuesto"));
                                tablaOC.setValueAt(Total, nroFila, colum);
                                AjustarDescripcionTabla(rs.getString(4));
                                //System.out.println(rs.getString(4));
                                con.CerrarConexion();

                            } else {
                                tablaOC.setValueAt("", nroFila, 0);
                                booleanFila = true;
                            }
//                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "El codigo ingresado no existe", "Error", JOptionPane.ERROR_MESSAGE);
                        tablaOC.setValueAt("", tablaOC.getSelectedRow(), 0);
                        booleanFila = true;
                    }

                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "El codigo ingresado no existe", "Error", JOptionPane.ERROR_MESSAGE);
                    tablaOC.setValueAt("", tablaOC.getSelectedRow(), 0);
                    booleanFila = true;
                } catch (NullPointerException n) {
                    JOptionPane.showMessageDialog(null, "El codigo ingresado no es valido", "Error", JOptionPane.ERROR_MESSAGE);
                    tablaOC.setValueAt("", tablaOC.getSelectedRow(), 0);
                    booleanFila = true;
                }
            }
            calculo();
        }
        Articulo.codigoArticulo = null;
        textoTabla = "";
        banNuevoArticulo = false;
    }

    private String separadorDeMiles(double nro) {
        double num = nro;
        DecimalFormat formateador = new DecimalFormat("###,###");
        return String.valueOf(formateador.format(num));
    }

    private int CalculoIva(String cantidad, String precio, String tipoIva) {
        int columna = 0;
        int cant = Integer.parseInt(cantidad);
        int prec = Integer.parseInt(precio);
        Total = "";
        Total = separadorDeMiles(cant * prec);
        //sumaTotalGral = sumaTotalGral + Double.parseDouble(Total);
        //txttotgral.setText(separadorDeMiles(sumaTotalGral));

        if (tipoIva.equals("Exenta")) {
            //sumaTotalExenta = sumaTotalExenta + Double.parseDouble(Total);
            //txtsubtotExentas.setText(separadorDeMiles(sumaTotalExenta));

            columna = 5;
        } else if (tipoIva.equals("5%")) {
            /*double iva = Double.parseDouble(Total) / 21;
             sumaTotalIva5 = sumaTotalIva5 + Double.parseDouble(Total);
             txtsubtotIva5.setText(separadorDeMiles(sumaTotalIva5));

             liquidacionIva5 = liquidacionIva5 + iva;
             txtliqiva5.setText(separadorDeMiles(liquidacionIva5));
             */
            columna = 6;
        } else {
            /*double iva = Double.parseDouble(Total) / 11;
             sumaTotalIva10 = sumaTotalIva10 + Double.parseDouble(Total);
             txtsubtotIva10.setText(separadorDeMiles(sumaTotalIva10));

             liquidacionIva10 = liquidacionIva10 + iva;
             txtliqiva10.setText(separadorDeMiles(liquidacionIva10));
             */
            columna = 7;
        }
        //System.out.println((txtliqiva5.getText().replaceAll("\\.", "")).replaceAll("\\.", ""));
        /*double liquidacionIva =
         Double.parseDouble((txtliqiva5.getText().replaceAll("\\.", "")).replaceAll(",", "."))
         + Double.parseDouble((txtliqiva10.getText().replaceAll("\\.", "")).replaceAll(",", "."));
         txttotiva.setText(separadorDeMiles(liquidacionIva));
         */
        return columna;
    }

    public void calculo() {
        int c = 0;
        double sumaExenta = 0;
        double sumaIva5 = 0;
        double sumaIva10 = 0;
        int exenta = 0;
        int iva5 = 0;
        int iva10 = 0;
        while (c < tablaOC.getRowCount()) {
            if (tablaOC.getValueAt(c, 5).equals("")) {
                exenta = 0;
            } else {
                exenta = Integer.parseInt(sacarPunto(String.valueOf(tablaOC.getValueAt(c, 5))));
            }
            if (tablaOC.getValueAt(c, 6).equals("")) {
                iva5 = 0;
            } else {
                iva5 = Integer.parseInt(sacarPunto(String.valueOf(tablaOC.getValueAt(c, 6))));
            }
            if (tablaOC.getValueAt(c, 7).equals("")) {
                iva10 = 0;
            } else {
                iva10 = Integer.parseInt(sacarPunto(String.valueOf(tablaOC.getValueAt(c, 7))));
            }

            sumaExenta += exenta;
            sumaIva5 += iva5;
            sumaIva10 += iva10;
            c++;
        }
        txtsubtotExentas.setText(separadorDeMiles(sumaExenta));
        txtsubtotIva10.setText(separadorDeMiles(sumaIva10));
        txtsubtotIva5.setText(separadorDeMiles(sumaIva5));
        txttotgral.setText(separadorDeMiles(sumaExenta + sumaIva10 + sumaIva5));
        double nro1 = sumaIva10 / 11;
        //System.out.println("Numero 1 "+nro1);
        BigDecimal diez = new BigDecimal(nro1);
        diez = diez.setScale(0, RoundingMode.HALF_DOWN);
        double nro2 = sumaIva5 / 21;
        //System.out.println("Numero 2 "+nro2);
        BigDecimal cinco = new BigDecimal(nro2);
        cinco = cinco.setScale(0, RoundingMode.HALF_DOWN);

        txtliqiva10.setText(separadorDeMiles(Double.parseDouble(diez.toString())));
        txtliqiva5.setText(separadorDeMiles(Double.parseDouble(cinco.toString())));
        int total = Integer.parseInt(sacarPunto(txtliqiva10.getText())) + Integer.parseInt(sacarPunto(txtliqiva5.getText()));
        txttotiva.setText(separadorDeMiles(total));
    }

    private String fechaUsa(java.util.Date fecha) {
        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy"); //DEFINE FORMATO DE DATA
        String data = formato.format(fecha); //CONVERTE PRA STRING
        data = data.substring(6, 10) + data.substring(3, 5) + data.substring(0, 2);

        return data;
    }

    private String GetCod(String tabla, String descripcion, String cod, String des) {
        String codigo = null;
        try {

            String sql = "SELECT " + cod + " FROM " + tabla + " WHERE " + des + "='" + descripcion + "'";
            //Query(sql);
            rs.next();
            if (cod.equals("pro_cod")) {
                codigo = rs.getString("pro_cod");
            } else {
                codigo = rs.getString("med_cod");
            }
        } catch (SQLException ex) {
            Logger.getLogger(OCDirecta.class.getName()).log(Level.SEVERE, null, ex);
        }
        return codigo;
    }

    private void guardarDatosTabla(String codigo) {
        try {
            int fila = 0;
            int filades = 0;
            conexion();
            while (tablaOC.getValueAt(fila, 3) != "") {
                if (!tablaOC.getValueAt(fila, 0).equals("")) {
                    filades = fila;
                    String texto = (String) tablaOC.getValueAt(filades, 3) + " \n";
                    filades++;
                   // System.out.println("Texto1: " + texto);
                    while (tablaOC.getValueAt(filades, 0).equals("")) {
                        texto += (String) tablaOC.getValueAt(filades, 3) + " \n";
                        //System.out.println("Texto2: " + texto);
                        filades++;
                        if (filades == tablaOC.getRowCount()) {
                            break;
                        }
                    }
                    filades = 0;
                    String cod = "SELECT IFNULL(MAX(odet_cod),0)+1 codigo FROM detalle";
                    ResultSet rscodigo = con.Consulta(cod);
                    rscodigo.next();
                    String sql = "INSERT INTO detalle VALUES(" + rscodigo.getString(1) + "," + codigo
                            + "," + tablaOC.getValueAt(fila, 1) + "," + sacarPunto(String.valueOf(tablaOC.getValueAt(fila, 4))) + ",'"
                            + texto.trim() + "',"+tablaOC.getValueAt(fila, 0)+")";
                    //System.out.println(sql);
                    con.Guardar(sql);
                }
                fila++;
            }
            con.CerrarConexion();
        } catch (SQLException ex) {
            Logger.getLogger(OCDirecta.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ArrayIndexOutOfBoundsException ex) {
        }
    }

    private boolean validar(String query) {
        // System.out.println(query);
        boolean ban = true;
        try {
            //Query(query);
            if (rs.next() == false) {
                ban = false;
            }
        } catch (SQLException ex) {
            Logger.getLogger(OCDirecta.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ban;
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
                    Logger.getLogger(OCDirecta.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            con.CerrarConexion();
        } catch (SQLException ex) {
            Logger.getLogger(OCDirecta.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void cargaCboDepto() {
        conexion();
        try {
            String sql = "SELECT * FROM catedra WHERE cat_sede='" + AcesoUsuario.sede
                    + "' ORDER BY cat_nombre";

            rsCodDpto = con.Consulta(sql);
            cbodpto.removeAllItems();
            cbodpto.addItem("");
            while (rsCodDpto.next()) {
                try {
                    cbodpto.addItem(rsCodDpto.getString("cat_nombre"));
                } catch (SQLException ex) {
                    Logger.getLogger(OCDirecta.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            //cbodpto.addItem("");
            con.CerrarConexion();
        } catch (SQLException ex) {
            Logger.getLogger(OCDirecta.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void cargaCboImputacion() {
        conexion();
        try {
            String sql = "SELECT * FROM imputacion ORDER BY imp_des";

            rs = con.Consulta(sql);
            cboimputacion.removeAllItems();
            cboimputacion.addItem("");
            while (rs.next()) {
                try {
                    cboimputacion.addItem(rs.getString("imp_des"));
                } catch (SQLException ex) {
                    Logger.getLogger(OCDirecta.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            //cboimputacion.addItem("");
            cboimputacion.setSelectedIndex(-1);
            con.CerrarConexion();
        } catch (SQLException ex) {
            Logger.getLogger(OCDirecta.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void cargaCboMod() {
        conexion();
        try {
            String sql = "SELECT t.tip_des,m.mod_cod,m.mod_des FROM modalidad m,tipo_modalidad t WHERE "
                    + "t.tip_cod=m.tip_cod ORDER BY mod_des";
            rsCodMod = con.Consulta(sql);
            cbomod.removeAllItems();
            cbomod.addItem("");
            while (rsCodMod.next()) {
                try {
                    arModalidad.add(rsCodMod.getString(2));
                    cbomod.addItem(rsCodMod.getString(1) + " - " + rsCodMod.getString("mod_des"));
                } catch (SQLException ex) {
                    Logger.getLogger(OCDirecta.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
            con.CerrarConexion();
        } catch (SQLException ex) {
            Logger.getLogger(OCDirecta.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void cargaCboPago() {
        conexion();
        try {
            String sql = "SELECT * FROM forma_pago ORDER BY pag_descripcion";
            rsCodPago = con.Consulta(sql);
            cbopag.removeAllItems();
            cbopag.addItem("");
            while (rsCodPago.next()) {
                try {
                    if (!rsCodPago.getString("pag_descripcion").equals("")) {
                        cbopag.addItem(rsCodPago.getString("pag_descripcion"));
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(OCDirecta.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            //cbopago.addItem("");
            // cbomod.setSelectedIndex(-1);
            con.CerrarConexion();
        } catch (SQLException ex) {
            Logger.getLogger(OCDirecta.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    static public boolean validarFecha(String texto) {
        String aa, mm, dd;
        int resto, nue;
        boolean ban = true;

        aa = texto.substring(6, 10);
        mm = texto.substring(3, 5);
        dd = texto.substring(0, 2);
        resto = Integer.parseInt(aa) % 4;

        if (Integer.parseInt(aa) < 1000) {
            ban = false;
        }
        if (Integer.parseInt(mm) > 12) {
            ban = false;
        } else {
            // valida Febrero
            if (resto != 0 && Integer.parseInt(mm) == 2 && Integer.parseInt(dd) > 28) {
                ban = false;

            } else if (resto == 0 && Integer.parseInt(mm) == 2 && Integer.parseInt(dd) > 29) {
                ban = false;
            } //valida enero, marzo, mayo, julio, agosto, octubre, diciembre
            else if ((Integer.parseInt(mm) == 1 || Integer.parseInt(mm) == 3
                    || Integer.parseInt(mm) == 5 || Integer.parseInt(mm) == 7
                    || Integer.parseInt(mm) == 8 || Integer.parseInt(mm) == 10
                    || Integer.parseInt(mm) == 12) && Integer.parseInt(dd) > 31) {
                ban = false;
            } //valida febrero, abril, junio, septiembre, noviembre
            else if ((Integer.parseInt(mm) == 4
                    || Integer.parseInt(mm) == 6 || Integer.parseInt(mm) == 9
                    || Integer.parseInt(mm) == 11) && Integer.parseInt(dd) > 30) {
                ban = false;
            }
        }
//validar anho

        if (!ban) {
            JOptionPane.showMessageDialog(null, "Fecha no valida", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
        return ban;

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
            Logger.getLogger(OCDirecta.class.getName()).log(Level.SEVERE, null, ex);
        }
        return codigo;
    }

    private void habilitarCampos() {
        txtAdmin.setEnabled(true);
        txtDecano.setEnabled(true);
        txtPlazent.setEnabled(true);
        txtfecemi.setEnabled(true);
        cboimputacion.setEnabled(true);
        cbolug.setEnabled(true);
        txtoc.setEnabled(true);
        cbodpto.setEnabled(true);
        cbomod.setEnabled(true);
        cboprv.setEnabled(true);
        txtsubtotIva10.setEnabled(true);
        txtsubtotExentas.setEnabled(true);
        txtsubtotIva5.setEnabled(true);
        txtliqiva10.setEnabled(true);
        txtliqiva5.setEnabled(true);
        txttotgral.setEnabled(true);
        txttotiva.setEnabled(true);
        txtliqiva10.setEnabled(true);
        txttippresup.setEnabled(true);
        txtprograma.setEnabled(true);
        txtsubprog.setEnabled(true);
        txtproyecto.setEnabled(true);
        txtfuentefinan.setEnabled(true);
        txtorgfinanc.setEnabled(true);
        txtobjgasto.setEnabled(true);
        txtcdp.setEnabled(true);
        txtfecvigencia.setEnabled(true);
        txtal.setEnabled(true);
        cbopag.setEnabled(true);
        btnagrmismo.setEnabled(false);

    }

    private void capturarCodigoCombos() {
        if (cbodpto.getSelectedIndex() > 0) {
            CodDpto = comboCodigo("SELECT * FROM catedra", "cat_nombre", (String) cbodpto.getSelectedItem());
            //cboprv.grabFocus();
        }
        if (cboprv.getSelectedIndex() > 0) {
            CodPrv = comboCodigo("SELECT * FROM proveedor", "pro_descripcion", (String) cboprv.getSelectedItem());
        }
        if (cbomod.getSelectedIndex() > 0) {
            codPago = comboCodigo("SELECT * FROM forma_pago", "pag_descripcion", (String) cbopag.getSelectedItem());
        } else {
            codPago = "6";
        }
        if (cboimputacion.getSelectedIndex() > 0) {
            codImputacion = comboCodigo("SELECT * FROM imputacion", "imp_des", (String) cboimputacion.getSelectedItem());
        }
    }
    /*
     private void Restar() {
     int fila = tablaOC.getSelectedRow();
     int c = 5;
     int columna = 0;
     String monto = null;
     while (c < 8) {
     if (tablaOC.getValueAt(fila, c) != "") {
     columna = c;
     monto = String.valueOf(tablaOC.getValueAt(fila, c));
     }
     c++;
     }
     //System.out.println("la culumna es: " + columna);
     if (columna == 5) {
     int resta;
     resta = Integer.parseInt(sacarPunto(txtsubtotExentas.getText())) - Integer.parseInt(sacarPunto(monto));
     txtsubtotExentas.setText(String.valueOf(resta));
     int restaTotal = Integer.parseInt(sacarPunto(txttotgral.getText())) - Integer.parseInt(sacarPunto(monto));
     txttotgral.setText(separadorDeMiles(restaTotal));
     } else if (columna == 6) {
     int resta;
     resta = Integer.parseInt(sacarPunto(txtsubtotIva5.getText())) - Integer.parseInt(sacarPunto(monto));
     txtsubtotIva5.setText(separadorDeMiles(resta));
     sumaTotalIva5 = resta;
     int aux = Integer.parseInt(sacarPunto(monto)) / 21;
     System.out.println("Monto: " + monto + " Liquidacion 5:" + sacarPunto(txtliqiva5.getText()));
     int restaIva = Integer.parseInt(sacarPunto(txtliqiva5.getText())) - aux;
     txtliqiva5.setText(separadorDeMiles(restaIva));
     int restaTotalIva = Integer.parseInt(sacarPunto(txttotiva.getText())) - aux;
     txttotiva.setText(separadorDeMiles(restaTotalIva));

     int restaTotal = Integer.parseInt(sacarPunto(txttotgral.getText())) - Integer.parseInt(sacarPunto(monto));
     txttotgral.setText(separadorDeMiles(restaTotal));
     sumaTotalGral = restaTotal;
     } else if (columna == 7) {
     int resta;
     resta = Integer.parseInt(sacarPunto(txtsubtotIva10.getText())) - Integer.parseInt(sacarPunto(monto));
     txtsubtotIva10.setText(separadorDeMiles(resta));
     sumaTotalIva10 = resta;
     int aux = Integer.parseInt(sacarPunto(monto)) / 11;
     int restaIva = Integer.parseInt(sacarPunto(txtliqiva10.getText())) - aux;
     txtliqiva10.setText(separadorDeMiles(restaIva));
     int restaTotalIva = Integer.parseInt(sacarPunto(txttotiva.getText())) - aux;
     txttotiva.setText(separadorDeMiles(restaTotalIva));

     int restaTotal = Integer.parseInt(sacarPunto(txttotgral.getText())) - Integer.parseInt(sacarPunto(monto));
     txttotgral.setText(separadorDeMiles(restaTotal));
     sumaTotalGral = restaTotal;
     }
     }
     */

    private void limpiar() {
        c = 0;
        cbolug.setSelectedIndex(0);
        txtAdmin.setText("");
        txtDecano.setText("");
        txttippresup.setText("");
        txtprograma.setText("");
        txtsubprog.setText("");
        txtproyecto.setText("");
        txtfuentefinan.setText("");
        txtorgfinanc.setText("");
        txtobjgasto.setText("");
        txtcdp.setText("");
        txtfecvigencia.setDate(null);
        txtal.setDate(null);
        cboimputacion.setSelectedIndex(0);
        txtsubtotIva10.setText("0");
        txtsubtotIva5.setText("0");
        txtsubtotExentas.setText("0");
        txttotiva.setText("0");
        txtliqiva5.setText("0");
        txttotgral.setText("0");
        txtliqiva10.setText("0");
        cbodpto.setSelectedItem("");
        cbomod.setSelectedItem("");
        cboprv.setSelectedItem("");
        txtPlazent.setDate(null);
        txtoc.grabFocus();
        vaciarTabla();
    }

    private void imprimirXLS() {
        //Tipo de dato a imprimir:autodetectado
        DocFlavor flavor = DocFlavor.INPUT_STREAM.AUTOSENSE;

        //Se coge el servicio de impresion por defecto(impresora predeterminada)
        PrintService service = PrintServiceLookup.lookupDefaultPrintService();

        if (service != null) {
            FileInputStream fis = null;
            try {
                //Se crea un trabajo o servicio de impresion
                DocPrintJob job = service.createPrintJob();
                //Se crea los atributos del servicio a crear
                DocAttributeSet das = new HashDocAttributeSet();
                fis = new FileInputStream("c:\\ImprimirOc.xls");
                //Creacion de documento a imprimir)
                Doc doc = new SimpleDoc(fis, flavor, das);
                //Se envia el trabajo o servicio a imprimir
                job.print(doc, null);
            } catch (PrintException ex) {
                Logger.getLogger(OCDirecta.class.getName()).log(Level.SEVERE, null, ex);
            } catch (FileNotFoundException ex) {
                Logger.getLogger(OCDirecta.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    fis.close();
                } catch (IOException ex) {
                    Logger.getLogger(OCDirecta.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
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
            StringTokenizer token = new StringTokenizer(String.valueOf(cbomod.getSelectedItem()), "-");
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
            par.put("son", OCDirecta.NumericoToLetra(String.valueOf(Math.round(rs.getFloat("ocab_total")))));
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
            par.put("formaPago", cbopag.getSelectedItem());
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
            JasperExportManager.exportReportToPdfFile(jasperPrint, rutaImprimir+"\\reporte.pdf");
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
            Logger.getLogger(OCDirecta.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(FrmImprimir.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JRException ex) {
            Logger.getLogger(OCDirecta.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void imprimirOc() {
        try {
            String hc = "", cmi = "";
            // para ver el lugar de entrega
            if (cbolug.getSelectedIndex() == 0) {
                hc = "x";
                cmi = "";
            } else if (cbolug.getSelectedIndex() == 1) {
                hc = "";
                cmi = "x";
            }
            //btngra.doClick();
            btnagrmismo.setEnabled(true);
            SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy"); //DEFINE FORMATO DE DATA
            String fecemi = "";
            String fecpla = "";

            try {
                fecemi = formato.format(txtfecemi.getDate());
                fecpla = formato.format(txtPlazent.getDate());
            } catch (NullPointerException n) {
            }
            Map par = new HashMap();
            JRTableModelDataSource jrtmd = new JRTableModelDataSource(tablaOC.getModel());
            ClassLoader cl = this.getClass().getClassLoader();
            InputStream fis = (cl.getResourceAsStream("paqInformes/ImprimirOc.jasper"));
            JasperReport reporte = (JasperReport) JRLoader.loadObject(fis);
            par.put("hc", hc);
            par.put("cmi", cmi);
            par.put("senores", cboprv.getSelectedItem());
            par.put("fechaDeEmision", fecemi);
            par.put("fechaDeEntrega", fecpla);
            par.put("imputacionPresupuestaria", cboimputacion.getSelectedItem());
            par.put("subTotales", txtsubtotIva10.getText());
            par.put("totalGeneral", txttotgral.getText());
            par.put("liquidacionDelIva", txtliqiva10.getText());
            par.put("totalIva", txttotiva.getText());
            par.put("son", NumericoToLetra(txttotgral.getText().replaceAll("\\.", "")).toUpperCase());
            par.put("lugarDeEntrega", String.valueOf(cbolug.getSelectedItem()));
            par.put("admin", txtAdmin.getText());
            par.put("decano", txtDecano.getText());
            par.put("subtotal5", txtsubtotIva5.getText());
            par.put("liquidacionDelIva5", txtliqiva5.getText());
            JasperPrint jasperPrint = JasperFillManager.fillReport(reporte, par, jrtmd);

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
            JasperViewer.viewReport(jasperPrint, false);// vista= new JasperViewer(jasperPrint, false);
            //vista.setVisible(true);
            //JasperPrintManager.printReport(jasperPrint, false);
            irImprimir = null;
        } catch (JRException ex) {
            Logger.getLogger(OCDirecta.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Override
    public void run() {
        imprimirOc(txtoc.getText());
    }

    /**
     * Metodo que llama a la clase numeroLetra para convertir los numeros
     * enteros que se introducen a letras. Por ejemplo: 5 en Cinco; El resultado
     * retorna un String
     *
     * @param numero
     * @return
     */
    public static String NumericoToLetra(String nro) {

        if (nro.equals("0")) {
            return "cero";
        }
        return numeroLetra.numeroLetra(nro).replace("  ", " ").toUpperCase()+" GUARANIES.";
    }

    /**
     * Clase que permite la conversion de numerico a letra. Ejemplo: 5 - Cinco
     * La clase solamente soporta hasta 9 digitos.
     */
    /**
     * Clase que permite la conversion de numerico a letra. Ejemplo: 5 - Cinco
     * La clase solamente soporta hasta 9 digitos.
     */
    public static class numeroLetra {

        private static int nroPos = 0;

        public static String numeroLetra(String nro) {
            if (!isNumeric(nro)) {
                JOptionPane.showMessageDialog(null, "El numero ingresado contiene caracteres");
                return "";
            }
            if (nro.length() > 15) {
                JOptionPane.showMessageDialog(null, "El programa no posee la capacidad de más de 16 digitos");
                return "";
            }

            String[] veDatos = new String[1];

            switch (nro.length()) {
                case 1:
                case 2:
                case 3:
                    veDatos = new String[1];
                    break;
                case 4:
                case 5:
                case 6:
                    veDatos = new String[2];
                    break; //mil
                case 7:
                case 8:
                case 9:
                    veDatos = new String[3];
                    break; //millones
                case 10:
                case 11:
                case 12:
                    veDatos = new String[4];
                    break; //mil
                case 13:
                case 14:
                case 15:
                    veDatos = new String[5];
                    break; //billones
            }

            String tmp = "";
            for (int i = nro.length(), c = 0, p = 0; i > 0; i--, c++) {
                veDatos[p] = nro.substring(i - 1, i + c);
                if (c == 2) {
                    p += 1;
                    c = -1;
                }
            }

            for (nroPos = 0; nroPos < veDatos.length; nroPos++) {
                if (veDatos[nroPos].length() == 1) {
                    veDatos[nroPos] = "00" + veDatos[nroPos];
                }
                if (veDatos[nroPos].length() == 2) {
                    veDatos[nroPos] = "0" + veDatos[nroPos];
                }
                //Base de saltado
                if (veDatos[nroPos].equals("000") || veDatos[nroPos] == "000") {
                    //Ver para sintetizar estas condiciones en uno solo y largo
                    //ejemplo (nro.length()>=13 && nro.length()<=15 && nroPos==2) || (nro.length()>=10 && nro.length()<=12 && nroPos==2) || (veDatos.length >= 4 && nroPos==2)
                    if (nro.length() >= 13 && nro.length() <= 15 && nroPos == 2) {
                        //System.out.println(nro.substring(3, 6));
                        if (!nro.substring(3, 6).equals("000")) {
                            veDatos[nroPos] = "millones";
                            continue;
                        }
                    }
                    if (nro.length() >= 10 && nro.length() <= 12 && nroPos == 2) {
                        //System.out.println(nro.substring(3, 6));
                        if (!nro.substring(0, 3).equals("000")) {
                            veDatos[nroPos] = "millones";
                            continue;
                        }
                    }
                    if (veDatos.length >= 4 && nroPos == 2) {
                        if (Integer.parseInt(veDatos[4]) != 0 && Integer.parseInt(veDatos[3]) != 0) {
                            veDatos[nroPos] = "millones";
                            continue;
                        }
                    }
                    veDatos[nroPos] = "";
                    continue;
                }

                switch (nroPos) {
                    case 0:
                        veDatos[nroPos] = centesima(veDatos[nroPos]);
                        break;
                    case 1:
                        if (Integer.parseInt(veDatos[nroPos]) == 1) {
                            veDatos[nroPos] = "mil";
                        } else {
                            veDatos[nroPos] = centesima(veDatos[nroPos]) + "mil";
                        }
                        break;
                    case 2:
                        if (Integer.parseInt(veDatos[nroPos]) == 1) {
                            veDatos[nroPos] = "un millon";
                        } else {
                            veDatos[nroPos] = centesima(veDatos[nroPos]) + "millones";
                        }
                        break;
                    case 3:
                        if (Integer.parseInt(veDatos[nroPos]) == 1) {
                            veDatos[nroPos] = "mil";
                        } else {
                            veDatos[nroPos] = centesima(veDatos[nroPos]) + "mil";
                        }
                        break;
                    case 4:
                        if (Integer.parseInt(veDatos[nroPos]) == 1) {
                            veDatos[nroPos] = "un billon";
                        } else {
                            veDatos[nroPos] = centesima(veDatos[nroPos]) + "billones";
                        }
                        break;
                }
            }
            for (int i = veDatos.length; i > 0; i--) {
                tmp += veDatos[i - 1] + " ";
            }
            return tmp.trim().replace("  ", " ");
        }

        private static String centesima(String nro) {
            String tmp = "";
            int inro = 0;
            for (int i = 0; i < nro.length(); i++) {
                if (nro.substring(i, i + 1).equals("0")) {
                    continue;
                }
                switch (i) {
                    case 0:
                        if (Integer.parseInt(nro) == 100) {
                            tmp += "cien";
                        } else {
                            tmp += centena(nro.substring(i, i + 1));
                        }
                        break;
                    case 1:
                        inro = Integer.parseInt(nro.substring(i, i + 2));
                        if (inro >= 11 && inro <= 15) {
                            tmp += unidad(nro.substring(i, i + 2));
                            i = 2;
                        } else {
                            if (Integer.parseInt(nro.substring(i, i + 2)) % 10 == 0) {
                                tmp += decena(nro.substring(i, i + 1));
                            } else {
                                tmp += decena(nro.substring(i, i + 1)) + " y";
                            }
                        }
                        break;
                    case 2:
                        tmp += unidad(nro.substring(i, i + 1));
                        break;
                }
                tmp += " ";
            }
            return tmp;
        }

        private static String unidad(String nro) {
            switch (Integer.parseInt(nro)) {
                case 1:
                    if (nroPos == 0) {
                        nro = "uno";
                    } else {
                        nro = "un";
                    }
                    break;
                case 2:
                    nro = "dos";
                    break;
                case 3:
                    nro = "tres";
                    break;
                case 4:
                    nro = "cuatro";
                    break;
                case 5:
                    nro = "cinco";
                    break;
                case 6:
                    nro = "seis";
                    break;
                case 7:
                    nro = "siete";
                    break;
                case 8:
                    nro = "ocho";
                    break;
                case 9:
                    nro = "nueve";
                    break;
                case 11:
                    nro = "once";
                    break;
                case 12:
                    nro = "doce";
                    break;
                case 13:
                    nro = "trece";
                    break;
                case 14:
                    nro = "catorce";
                    break;
                case 15:
                    nro = "quince";
                    break;
                default:
                    nro = "";
                    break;
            }
            return nro;
        }

        private static String decena(String nro) {
            switch (Integer.parseInt(nro)) {
                case 1:
                    nro = "diez";
                    break;
                case 2:
                    nro = "veinte";
                    break;
                case 3:
                    nro = "treinta";
                    break;
                case 4:
                    nro = "cuarenta";
                    break;
                case 5:
                    nro = "cincuenta";
                    break;
                case 6:
                    nro = "sesenta";
                    break;
                case 7:
                    nro = "setenta";
                    break;
                case 8:
                    nro = "ochenta";
                    break;
                case 9:
                    nro = "noventa";
                    break;
                default:
                    nro = "";
                    break;
            }
            return nro;
        }

        private static String centena(String nro) {
            switch (Integer.parseInt(nro)) {
                case 1:
                    nro = "ciento";
                    break;
                case 2:
                    nro = "doscientos";
                    break;
                case 3:
                    nro = "trescientos";
                    break;
                case 4:
                    nro = "cuatrocientos";
                    break;
                case 5:
                    nro = "quinientos";
                    break;
                case 6:
                    nro = "seiscientos";
                    break;
                case 7:
                    nro = "setecientos";
                    break;
                case 8:
                    nro = "ochocientos";
                    break;
                case 9:
                    nro = "novecientos";
                    break;
                default:
                    nro = "";
                    break;
            }
            return nro;
        }
    }

    /**
     * Verifica si en un String se encuentra numerico (true) o no (false)
     *
     * @param nro
     * @return
     */
    public static Boolean isNumeric(String nro) {
        try {
            for (int i = 0; i < nro.length(); i++) {
                Integer.parseInt(nro.substring(i, i + 1));
            }
        } catch (Exception e) {
            return false;
        }
        return true;
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
        meneliminar = new javax.swing.JMenuItem();
        mencancelar = new javax.swing.JMenuItem();
        jLabel4 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        txtAdmin = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        txtDecano = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        btncan = new javax.swing.JButton();
        btngra = new javax.swing.JButton();
        btnimp = new javax.swing.JButton();
        btnmod = new javax.swing.JButton();
        btncer = new javax.swing.JButton();
        btnnue = new javax.swing.JButton();
        jLabel15 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        txttotiva = new javax.swing.JFormattedTextField();
        txtliqiva10 = new javax.swing.JFormattedTextField();
        txttotgral = new javax.swing.JFormattedTextField();
        txtoc = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        txtsubtotIva10 = new javax.swing.JFormattedTextField();
        jLabel2 = new javax.swing.JLabel();
        cbodpto = new javax.swing.JComboBox();
        cboprv = new javax.swing.JComboBox();
        jLabel5 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtsubtotIva5 = new javax.swing.JFormattedTextField();
        txtsubtotExentas = new javax.swing.JFormattedTextField();
        txtliqiva5 = new javax.swing.JFormattedTextField();
        txtfecemi = new com.toedter.calendar.JDateChooser("dd/MM/yyyy", "####/##/##", '_');
        txtPlazent = new com.toedter.calendar.JDateChooser("dd/MM/yyyy", "####/##/##", '_');
        btnprv = new javax.swing.JButton();
        btnagrmismo = new javax.swing.JButton();
        cbolug = new javax.swing.JComboBox();
        cboimputacion = new javax.swing.JComboBox();
        btnimputacion = new javax.swing.JButton();
        btnprv1 = new javax.swing.JButton();
        btneli = new javax.swing.JButton();
        btnprv2 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tablaOC = new javax.swing.JTable();
        jLabel14 = new javax.swing.JLabel();
        txtfecvigencia = new com.toedter.calendar.JDateChooser("dd/MM/yyyy", "####/##/##", '_');
        jLabel16 = new javax.swing.JLabel();
        txtal = new com.toedter.calendar.JDateChooser("dd/MM/yyyy", "####/##/##", '_');
        jLabel18 = new javax.swing.JLabel();
        txttippresup = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        txtprograma = new javax.swing.JTextField();
        jLabel20 = new javax.swing.JLabel();
        txtsubprog = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        txtproyecto = new javax.swing.JTextField();
        jLabel22 = new javax.swing.JLabel();
        txtfuentefinan = new javax.swing.JTextField();
        jLabel23 = new javax.swing.JLabel();
        txtorgfinanc = new javax.swing.JTextField();
        jLabel24 = new javax.swing.JLabel();
        txtobjgasto = new javax.swing.JTextField();
        jLabel25 = new javax.swing.JLabel();
        txtcdp = new javax.swing.JTextField();
        jLabel26 = new javax.swing.JLabel();
        cbopag = new javax.swing.JComboBox();
        btnprv3 = new javax.swing.JButton();
        cbomod = new javax.swing.JComboBox();

        meneliminar.setText("Eliminar");
        meneliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                meneliminarActionPerformed(evt);
            }
        });
        menuEmergente.add(meneliminar);

        mencancelar.setText("Cancelar");
        menuEmergente.add(mencancelar);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Orden de Compra");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
        });

        jLabel4.setText("Dir. de Admin y Finanzas");

        jLabel6.setText("Decano");

        jLabel13.setText("Modalidad");

        txtAdmin.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        txtAdmin.setEnabled(false);
        txtAdmin.setNextFocusableComponent(txtDecano);
        txtAdmin.setOpaque(false);
        txtAdmin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtAdminActionPerformed(evt);
            }
        });
        txtAdmin.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtAdminFocusLost(evt);
            }
        });

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel10.setText("TOTAL GENERAL");

        txtDecano.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        txtDecano.setEnabled(false);
        txtDecano.setNextFocusableComponent(cboimputacion);
        txtDecano.setOpaque(false);
        txtDecano.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtDecanoActionPerformed(evt);
            }
        });
        txtDecano.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtDecanoFocusLost(evt);
            }
        });

        jLabel12.setText("LIQUIDACIÓN DEL IVA");

        jLabel9.setText("SUB-TOTALES");

        jLabel8.setText("Lugar de entrega");

        jLabel11.setText("Imp. Presupuestaria");

        btncan.setText("Cancelar");
        btncan.setToolTipText("Cancelar");
        btncan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btncanActionPerformed(evt);
            }
        });

        btngra.setText("Grabar");
        btngra.setToolTipText("Grabar");
        btngra.setEnabled(false);
        btngra.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btngraActionPerformed(evt);
            }
        });

        btnimp.setText("Imprimir");
        btnimp.setToolTipText("Imprimir");
        btnimp.setEnabled(false);
        btnimp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnimpActionPerformed(evt);
            }
        });

        btnmod.setText("Modificar");
        btnmod.setToolTipText("Modificar");
        btnmod.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnmodActionPerformed(evt);
            }
        });

        btncer.setText("Salir");
        btncer.setToolTipText("Cerrar");
        btncer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btncerActionPerformed(evt);
            }
        });

        btnnue.setText("Nuevo");
        btnnue.setToolTipText("Agregar");
        btnnue.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        btnnue.setIconTextGap(0);
        btnnue.setMultiClickThreshhold(4L);
        btnnue.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnnueActionPerformed(evt);
            }
        });

        jLabel15.setText("Plazo de entrega");

        jLabel17.setText("TOTAL IVA");

        txttotiva.setEditable(false);
        txttotiva.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        txttotiva.setEnabled(false);
        txttotiva.setOpaque(false);

        txtliqiva10.setEditable(false);
        txtliqiva10.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        txtliqiva10.setEnabled(false);
        txtliqiva10.setOpaque(false);

        txttotgral.setEditable(false);
        txttotgral.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        txttotgral.setEnabled(false);
        txttotgral.setOpaque(false);

        txtoc.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        txtoc.setEnabled(false);
        txtoc.setNextFocusableComponent(cbodpto);
        txtoc.setOpaque(false);
        txtoc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtocActionPerformed(evt);
            }
        });

        jLabel7.setText("Servicio");

        txtsubtotIva10.setEditable(false);
        txtsubtotIva10.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        txtsubtotIva10.setEnabled(false);
        txtsubtotIva10.setOpaque(false);

        jLabel2.setText("O.C. Nº");

        cbodpto.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        cbodpto.setEnabled(false);
        cbodpto.setNextFocusableComponent(cboprv);
        cbodpto.setOpaque(false);
        cbodpto.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                cbodptoFocusGained(evt);
            }
        });

        cboprv.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        cboprv.setEnabled(false);
        cboprv.setNextFocusableComponent(cbolug);
        cboprv.setOpaque(false);

        jLabel5.setText("Fecha de Emisión");

        jLabel3.setText("Proveedor");

        txtsubtotIva5.setEditable(false);
        txtsubtotIva5.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        txtsubtotIva5.setEnabled(false);
        txtsubtotIva5.setOpaque(false);

        txtsubtotExentas.setEditable(false);
        txtsubtotExentas.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        txtsubtotExentas.setEnabled(false);
        txtsubtotExentas.setOpaque(false);

        txtliqiva5.setEditable(false);
        txtliqiva5.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        txtliqiva5.setEnabled(false);
        txtliqiva5.setOpaque(false);

        txtfecemi.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        txtfecemi.setEnabled(false);
        txtfecemi.setOpaque(false);

        txtPlazent.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        txtPlazent.setDebugGraphicsOptions(javax.swing.DebugGraphics.NONE_OPTION);
        txtPlazent.setEnabled(false);
        txtPlazent.setNextFocusableComponent(txtfecvigencia);
        txtPlazent.setOpaque(false);
        txtPlazent.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtPlazentFocusLost(evt);
            }
        });
        txtPlazent.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                txtPlazentPropertyChange(evt);
            }
        });

        btnprv.setIcon(new javax.swing.ImageIcon(getClass().getResource("/paqImagenes/iconoAgregar.gif"))); // NOI18N
        btnprv.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnprvActionPerformed(evt);
            }
        });

        btnagrmismo.setText("Agregar Mismo");
        btnagrmismo.setToolTipText("Agregar");
        btnagrmismo.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        btnagrmismo.setEnabled(false);
        btnagrmismo.setIconTextGap(0);
        btnagrmismo.setMultiClickThreshhold(4L);
        btnagrmismo.setNextFocusableComponent(txtoc);
        btnagrmismo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnagrmismoActionPerformed(evt);
            }
        });

        cbolug.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "", "HOSPITAL DE CLINICAS", "FACULTAD DE CIENCIAS MEDICAS" }));
        cbolug.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        cbolug.setEnabled(false);
        cbolug.setNextFocusableComponent(cboprv);
        cbolug.setOpaque(false);
        cbolug.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                cbolugFocusLost(evt);
            }
        });

        cboimputacion.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        cboimputacion.setEnabled(false);
        cboimputacion.setNextFocusableComponent(txtfecemi);
        cboimputacion.setOpaque(false);
        cboimputacion.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                cboimputacionFocusLost(evt);
            }
        });

        btnimputacion.setIcon(new javax.swing.ImageIcon(getClass().getResource("/paqImagenes/iconoAgregar.gif"))); // NOI18N
        btnimputacion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnimputacionActionPerformed(evt);
            }
        });

        btnprv1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/paqImagenes/iconoAgregar.gif"))); // NOI18N
        btnprv1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnprv1ActionPerformed(evt);
            }
        });

        btneli.setText("Anular");
        btneli.setToolTipText("Modificar");
        btneli.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btneliActionPerformed(evt);
            }
        });

        btnprv2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/paqImagenes/iconoAgregar.gif"))); // NOI18N
        btnprv2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnprv2ActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 0, 0));
        jLabel1.setText("Precione \"N\" para agregar nuevo articulo - \"Enter\" para buscar");
        jLabel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 153, 0)));

        tablaOC.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"", "","", "", "", null, null, null, null},
                {"", "", "", "", "", null, null, null, null},
                {"", "","", "", "", null, null, null, null},
                {"", "", "", "", "", null, null, null, null},
                {"", "", "", "", "", null, null, null, null},
                {"", "", "", "", "", null, null, null, null},
                {"", "", "", "", "", null, null, null, null},
                {"", "", "", "", "", null, null, null, null},
                {"", "", "", "", "", null, null, null, null}
            },
            new String [] {
                "Codigo", "Cantidad", "Unidad", "Descripcion", "Precio Uni.", "Total Exentas", "Total Grav. 5%", "Total Grav. 10%"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                true, false, true, true, true, true, true, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tablaOC.setColumnSelectionAllowed(true);
        tablaOC.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                tablaOCMouseReleased(evt);
            }
        });
        tablaOC.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                tablaOCFocusGained(evt);
            }
        });
        tablaOC.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                tablaOCPropertyChange(evt);
            }
        });
        tablaOC.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tablaOCKeyPressed(evt);
            }
        });
        jScrollPane2.setViewportView(tablaOC);
        tablaOC.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tablaOC.getColumnModel().getColumn(0).setPreferredWidth(70);
        tablaOC.getColumnModel().getColumn(0).setMaxWidth(100);
        tablaOC.getColumnModel().getColumn(3).setPreferredWidth(250);
        tablaOC.getColumnModel().getColumn(3).setMaxWidth(300);

        jLabel14.setText("Fec. de vigencia de contrato");

        txtfecvigencia.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        txtfecvigencia.setEnabled(false);
        txtfecvigencia.setNextFocusableComponent(txtal);
        txtfecvigencia.setOpaque(false);

        jLabel16.setText("al");

        txtal.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        txtal.setDebugGraphicsOptions(javax.swing.DebugGraphics.NONE_OPTION);
        txtal.setEnabled(false);
        txtal.setNextFocusableComponent(txttippresup);
        txtal.setOpaque(false);
        txtal.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtalFocusLost(evt);
            }
        });
        txtal.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                txtalPropertyChange(evt);
            }
        });

        jLabel18.setText("Tip. Presup.");

        txttippresup.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        txttippresup.setEnabled(false);
        txttippresup.setNextFocusableComponent(txtprograma);
        txttippresup.setOpaque(false);
        txttippresup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txttippresupActionPerformed(evt);
            }
        });
        txttippresup.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txttippresupFocusLost(evt);
            }
        });

        jLabel19.setText("Programa");

        txtprograma.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        txtprograma.setEnabled(false);
        txtprograma.setNextFocusableComponent(txtsubprog);
        txtprograma.setOpaque(false);
        txtprograma.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtprogramaActionPerformed(evt);
            }
        });
        txtprograma.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtprogramaFocusLost(evt);
            }
        });

        jLabel20.setText("Sub. Prog.");

        txtsubprog.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        txtsubprog.setEnabled(false);
        txtsubprog.setNextFocusableComponent(txtproyecto);
        txtsubprog.setOpaque(false);
        txtsubprog.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtsubprogActionPerformed(evt);
            }
        });
        txtsubprog.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtsubprogFocusLost(evt);
            }
        });

        jLabel21.setText("Proyecto");

        txtproyecto.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        txtproyecto.setEnabled(false);
        txtproyecto.setNextFocusableComponent(txtfuentefinan);
        txtproyecto.setOpaque(false);
        txtproyecto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtproyectoActionPerformed(evt);
            }
        });
        txtproyecto.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtproyectoFocusLost(evt);
            }
        });

        jLabel22.setText("Fuente Finan.");

        txtfuentefinan.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        txtfuentefinan.setEnabled(false);
        txtfuentefinan.setNextFocusableComponent(txtorgfinanc);
        txtfuentefinan.setOpaque(false);
        txtfuentefinan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtfuentefinanActionPerformed(evt);
            }
        });
        txtfuentefinan.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtfuentefinanFocusLost(evt);
            }
        });

        jLabel23.setText("Org. financ.");

        txtorgfinanc.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        txtorgfinanc.setEnabled(false);
        txtorgfinanc.setNextFocusableComponent(txtobjgasto);
        txtorgfinanc.setOpaque(false);
        txtorgfinanc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtorgfinancActionPerformed(evt);
            }
        });
        txtorgfinanc.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtorgfinancFocusLost(evt);
            }
        });

        jLabel24.setText("Obj. Gasto");

        txtobjgasto.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        txtobjgasto.setEnabled(false);
        txtobjgasto.setNextFocusableComponent(txtcdp);
        txtobjgasto.setOpaque(false);
        txtobjgasto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtobjgastoActionPerformed(evt);
            }
        });
        txtobjgasto.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtobjgastoFocusLost(evt);
            }
        });

        jLabel25.setText("C.D.P.");

        txtcdp.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        txtcdp.setEnabled(false);
        txtcdp.setNextFocusableComponent(txtfecvigencia);
        txtcdp.setOpaque(false);
        txtcdp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtcdpActionPerformed(evt);
            }
        });
        txtcdp.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtcdpFocusLost(evt);
            }
        });

        jLabel26.setText("Forma de Pago");

        cbopag.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        cbopag.setEnabled(false);
        cbopag.setNextFocusableComponent(txtAdmin);
        cbopag.setOpaque(false);
        cbopag.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                cbopagFocusLost(evt);
            }
        });

        btnprv3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/paqImagenes/iconoAgregar.gif"))); // NOI18N
        btnprv3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnprv3ActionPerformed(evt);
            }
        });

        cbomod.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        cbomod.setEnabled(false);
        cbomod.setNextFocusableComponent(cbopag);
        cbomod.setOpaque(false);
        cbomod.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                cbomodFocusLost(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel26, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel18, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(cbopag, javax.swing.GroupLayout.PREFERRED_SIZE, 308, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnprv3, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtAdmin, javax.swing.GroupLayout.PREFERRED_SIZE, 242, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(txtoc, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbodpto, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnprv1, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cboprv, javax.swing.GroupLayout.PREFERRED_SIZE, 281, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnprv, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(cbolug, javax.swing.GroupLayout.PREFERRED_SIZE, 286, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel13)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbomod, javax.swing.GroupLayout.PREFERRED_SIZE, 308, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnprv2, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(txtDecano, javax.swing.GroupLayout.PREFERRED_SIZE, 294, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel11)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cboimputacion, javax.swing.GroupLayout.PREFERRED_SIZE, 277, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnimputacion, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(txtfecemi, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel15)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtPlazent, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel14)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtfecvigencia, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel16)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtal, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(txttippresup, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel19)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtprograma, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel20)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtsubprog, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel21)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtproyecto, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel22)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtfuentefinan, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel23)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtorgfinanc, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel24)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtobjgasto, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel25)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtcdp, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE))))
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(563, 563, 563))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jScrollPane2)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(0, 0, Short.MAX_VALUE)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                    .addGroup(layout.createSequentialGroup()
                                                        .addComponent(jLabel9)
                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                        .addComponent(txtsubtotExentas, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                    .addComponent(jLabel12))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                    .addComponent(txtsubtotIva5, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(txtliqiva5, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                            .addComponent(jLabel10, javax.swing.GroupLayout.Alignment.TRAILING)))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(btnnue, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(btnagrmismo)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(btnmod, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(btneli, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(btngra)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(btnimp)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(btncan, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(btncer, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 51, Short.MAX_VALUE)
                                        .addComponent(jLabel17)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(txttotiva)
                                    .addComponent(txttotgral, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtsubtotIva10, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtliqiva10, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txtoc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel7)
                    .addComponent(cbodpto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(cboprv, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnprv)
                    .addComponent(btnprv1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel8)
                    .addComponent(jLabel13)
                    .addComponent(cbolug, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnprv2)
                    .addComponent(cbomod, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel26)
                    .addComponent(cbopag, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnprv3)
                    .addComponent(jLabel4)
                    .addComponent(txtAdmin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel11)
                    .addComponent(cboimputacion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnimputacion)
                    .addComponent(txtDecano, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel5)
                    .addComponent(jLabel15)
                    .addComponent(txtfecemi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtPlazent, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14)
                    .addComponent(jLabel16)
                    .addComponent(txtfecvigencia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txttippresup, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel18)
                    .addComponent(txtprograma, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel19)
                    .addComponent(txtsubprog, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel20)
                    .addComponent(txtproyecto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel21)
                    .addComponent(txtfuentefinan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel22)
                    .addComponent(txtorgfinanc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel23)
                    .addComponent(txtobjgasto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel24)
                    .addComponent(txtcdp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel25))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 252, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel9)
                    .addComponent(txtsubtotExentas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtsubtotIva5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtsubtotIva10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txttotgral, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtliqiva10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtliqiva5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel12)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel17)
                        .addComponent(txttotiva, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                        .addComponent(btnmod)
                        .addComponent(btnnue)
                        .addComponent(btnagrmismo))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btncer)
                            .addComponent(btncan))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(btnimp)
                            .addComponent(btngra)
                            .addComponent(btneli))))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtDecanoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtDecanoActionPerformed
        txtDecano.setText(txtDecano.getText().toUpperCase());
        cboimputacion.grabFocus();
    }//GEN-LAST:event_txtDecanoActionPerformed

    private void btncanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btncanActionPerformed
        validarFocus = true;
        botonagregar = false;
        botoneliminar = false;
        botonmodificar = false;
        c = 0;
        btnnue.setEnabled(true);
        btnmod.setEnabled(true);
        btngra.setEnabled(false);
        btneli.setEnabled(true);
        btnimp.setEnabled(false);
        btnagrmismo.setEnabled(false);
        cbolug.setEnabled(false);
        cbolug.setSelectedIndex(-1);
        txtAdmin.setText("");
        txtAdmin.setEnabled(false);
        txtDecano.setText("");
        txtDecano.setEnabled(false);
        cboimputacion.setSelectedIndex(-1);
        cboimputacion.setEnabled(false);
        txtoc.setText("");
        txtoc.setEnabled(false);
        txtsubtotIva10.setText("0");
        txtsubtotIva5.setText("0");
        txtsubtotExentas.setText("0");
        txttotiva.setText("0");
        txtliqiva5.setText("0");
        txttotgral.setText("0");
        txtliqiva10.setText("0");
        cbodpto.setSelectedItem("");
        cbodpto.setEnabled(false);
        cbomod.setSelectedItem("");
        cbomod.setEnabled(false);
        cboprv.setSelectedItem("");
        cbopag.setSelectedIndex(0);
        cbopag.setEnabled(false);
        txtPlazent.setDate(null);
        txtfecemi.setEnabled(false);
        txtPlazent.setEnabled(false);
        txtAdmin.setEnabled(false);
        txtDecano.setEnabled(false);
        txttippresup.setEnabled(false);
        txttippresup.setText("");
        txtprograma.setEnabled(false);
        txtprograma.setText("");
        txtsubprog.setEnabled(false);
        txtsubprog.setText("");
        txtproyecto.setEnabled(false);
        txtproyecto.setText("");
        txtfuentefinan.setEnabled(false);
        txtfuentefinan.setText("");
        txtorgfinanc.setEnabled(false);
        txtorgfinanc.setText("");
        txtobjgasto.setEnabled(false);
        txtobjgasto.setText("");
        txtcdp.setEnabled(false);
        txtcdp.setText("");
        txtfecvigencia.setEnabled(false);
        txtfecvigencia.setDate(null);
        txtal.setEnabled(false);
        txtal.setDate(null);
        sumaTotalGral = 0;
        sumaTotalExenta = 0;
        sumaTotalIva5 = 0;
        sumaTotalIva10 = 0;
        liquidacionIva5 = 0;
        liquidacionIva10 = 0;
        contadorOc = "";
        vaciarTabla();
    }//GEN-LAST:event_btncanActionPerformed

    private void btngraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btngraActionPerformed
        if (cbodpto.getSelectedItem() == "" || cboimputacion.getSelectedItem() == "" || cbolug.getSelectedItem() == ""
                || cbomod.getSelectedItem() == "" || cboprv.getSelectedItem() == "") {
            JOptionPane.showMessageDialog(this, "No ha completado alguntos campos!!!", "Error", JOptionPane.ERROR_MESSAGE);
        } else {

            try {
                conexion();
                String sql = "SELECT * FROM ordencab WHERE ocab_codigo='" + txtoc.getText() + "' ";
                rs = con.Consulta(sql);
                if (rs.next() && botonagregar == true && !txtoc.getText().equals("")) {
                    JOptionPane.showMessageDialog(this, "El número de Orden de Compra ya existe");
                    con.CerrarConexion();
                } else {
                    int res = JOptionPane.showConfirmDialog(null, "Realmente, ¿Desea Grabar"
                            + " los Datos?", "Mensaje", JOptionPane.OK_CANCEL_OPTION);
                    if (!txtoc.getText().isEmpty()) {
                        btnimp.setEnabled(true);
                        btnimp.grabFocus();
                    }
                    String feEmi = null, fePla = null, fevig = null, feal = null;
                    SimpleDateFormat date = new SimpleDateFormat("yyyyMMdd");
                    capturarCodigoCombos();
                    if (txtfecemi.getDate() != null) {
                        feEmi = date.format(txtfecemi.getDate());
                    }
                    if (txtPlazent.getDate() != null) {
                        fePla = date.format(txtPlazent.getDate());
                    }
                    if (txtfecvigencia.getDate() != null) {
                        fevig = date.format(txtfecvigencia.getDate());
                    }
                    if (txtal.getDate() != null) {
                        feal = date.format(txtal.getDate());
                    }

                    DepartamentoXDefecto = (String) cbodpto.getSelectedItem();
                    if (res == JOptionPane.OK_OPTION) {
                        btnagrmismo.setEnabled(true);
                        tablaOC.setEnabled(false);

                        tablaOC.clearSelection();
                        tablaOC.requestFocus(false);
                        if (botonagregar == true) {
                            conexion();
                            rs = con.Consulta("SELECT IFNULL(MAX(ocab_contador),0)+1 codigo FROM ordencab");
                            rs.next();
                            contadorOc = rs.getString("codigo");

                            String sqlOc = "INSERT INTO ordencab (ocab_contador,ocab_codigo,mod_cod,imp_cod,pag_codigo,"
                                    + "usu_cod,pro_codigo,cat_codigo,ocab_fecha,ocab_plazo_entrega,"
                                    + "ocab_exenta,ocab_gra5,ocab_gra10,ocab_iva5,ocab_iva10,"
                                    + "ocab_total,ocab_entrega,ocab_director,ocab_decano,ocab_estado,"
                                    + "tipo_presup,programa,sub_prog,proyecto,fuente_finan,org_financ,obj_gasto,cdp,"
                                    + "fec_vigencia,fec_al,fec_carga) VALUES (" + contadorOc + ",'" + txtoc.getText().trim() + "'," + arModalidad.get(cbomod.getSelectedIndex() - 1)
                                    + "," + codImputacion + "," + codPago + "," + AcesoUsuario.CodigoUsuario + "," + CodPrv + "," + CodDpto
                                    + "," + feEmi + "," + fePla + ",'" + sacarPunto(txtsubtotExentas.getText()) + "','"
                                    + sacarPunto(txtsubtotIva5.getText()) + "','" + sacarPunto(txtsubtotIva10.getText())
                                    + "','" + sacarPunto(txtliqiva5.getText()) + "','" + sacarPunto(txtliqiva10.getText())
                                    + "','" + sacarPunto(txttotgral.getText()) + "','" + cbolug.getSelectedItem()
                                    + "','" + txtAdmin.getText() + "','" + txtDecano.getText() + "'"
                                    + ",'A','" + txttippresup.getText() + "','" + txtprograma.getText()
                                    + "','" + txtsubprog.getText() + "','" + txtproyecto.getText() + "','" + txtfuentefinan.getText()
                                    + "','" + txtorgfinanc.getText() + "','" + txtobjgasto.getText() + "','" + txtcdp.getText() + "'," + fevig
                                    + "," + feal + "," + date.format(this.fecha) + ")";
                            System.out.println(sqlOc);
                            con.Guardar(sqlOc);
                            btngra.setEnabled(false);
                            if (!txtoc.getText().isEmpty()) {
                                btnimp.setEnabled(true);
                                btnimp.grabFocus();
                            }
                            botonagregar = false;
                            con.CerrarConexion();
                            guardarDatosTabla(contadorOc);
                        } else if (botonmodificar == true) {
                            conexion();
                            String modificar = "UPDATE ordencab SET ocab_codigo='" + txtoc.getText()
                                    + "',imp_cod=" + codImputacion
                                    + ",mod_cod=" + arModalidad.get(cbomod.getSelectedIndex() - 1)
                                    + ",pag_codigo=" + codPago + "," + "usu_cod=" + AcesoUsuario.CodigoUsuario
                                    + ",pro_codigo=" + CodPrv + ",cat_codigo=" + CodDpto + ","
                                    + "ocab_fecha=" + feEmi + ",ocab_plazo_entrega=" + fePla + ","
                                    + "ocab_exenta=" + sacarPunto(txtsubtotExentas.getText()) + ","
                                    + "ocab_gra5=" + sacarPunto(txtsubtotIva5.getText()) + ","
                                    + "ocab_gra10=" + sacarPunto(txtsubtotIva10.getText()) + ","
                                    + "ocab_iva5=" + sacarPunto(txtliqiva5.getText()) + ","
                                    + "ocab_iva10=" + sacarPunto(txtliqiva10.getText()) + ","
                                    + "ocab_total=" + sacarPunto(txttotgral.getText()) + ","
                                    + "ocab_entrega='" + cbolug.getSelectedItem() + "',"
                                    + "ocab_director='" + txtAdmin.getText() + "',"
                                    + "ocab_decano='" + txtDecano.getText() + "',"
                                    + "tipo_presup='" + txttippresup.getText() + "',"
                                    + "programa='" + txtprograma.getText() + "',"
                                    + "sub_prog='" + txtsubprog.getText() + "',"
                                    + "proyecto='" + txtproyecto.getText() + "',"
                                    + "fuente_finan='" + txtfuentefinan.getText() + "',"
                                    + "org_financ='" + txtorgfinanc.getText() + "',"
                                    + "obj_gasto='" + txtobjgasto.getText() + "',"
                                    + "cdp='" + txtcdp.getText() + "',"
                                    + "fec_vigencia=" + fevig + ","
                                    + "fec_al=" + feal
                                    + " WHERE ocab_contador=" + contadorOc;
                           // System.out.println(modificar);
                            con.Guardar(modificar);
                            String eliminarDet = "DELETE FROM detalle WHERE ocab_contador=" + contadorOc;
                            con.Guardar(eliminarDet);
                            botonmodificar = false;
                            con.CerrarConexion();
                            guardarDatosTabla(contadorOc);
                            btngra.setEnabled(false);
                        } else if (botoneliminar == true) {
                            conexion();
                            String eliminar = "UPDATE ordencab SET ocab_estado='N'"
                                    + " WHERE ocab_contador=" + txtoc.getText();
                            botoneliminar = false;
                            con.Guardar(eliminar);
                            con.CerrarConexion();
                            btneli.setEnabled(false);
                        }
                        Total = "0";
                    } else {
                        //btnagrepro.setEnabled(true);
                    }
                }
                //btnagrepro.grabFocus();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this,"Error al grabar los datos","Error",JOptionPane.ERROR_MESSAGE);
                Logger.getLogger(OCDirecta.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_btngraActionPerformed

    private void btnimpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnimpActionPerformed
        //btngra.doClick();
        //btnnue.setEnabled(true);
        CerrarPdf();
        File f = new File(rutaImprimir+"\\reporte.pdf");
        f.delete();
        System.out.println("contador: " + contadorOc);
        imprimirOc(contadorOc);
    }//GEN-LAST:event_btnimpActionPerformed

    private void btnmodActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnmodActionPerformed
        tablaOC.setEnabled(true);
        botonmodificar = true;
        btngra.setEnabled(true);
        //btnimp.setEnabled(true);
        txtoc.setEnabled(true);
        txtoc.grabFocus();
        btneli.setEnabled(false);
        btnmod.setEnabled(false);
        contadorOc = "";
    }//GEN-LAST:event_btnmodActionPerformed

    private void btncerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btncerActionPerformed
        validarFocus = true;
        this.dispose();
    }//GEN-LAST:event_btncerActionPerformed

    private void btnnueActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnnueActionPerformed
        try {
            tablaOC.setEnabled(true);
            btncan.doClick();
            habilitarCampos();
            //cbodpto.setSelectedItem(DepartamentoXDefecto);
            botonagregar = true;
            String sqlMaxOc = "SELECT ocab_codigo,ocab_entrega,ocab_director,"
                    + "ocab_decano FROM ordencab WHERE ocab_contador=(SELECT max(ocab_contador) "
                    + "FROM ordencab WHERE usu_cod=" + AcesoUsuario.CodigoUsuario + ")";
            System.out.println(sqlMaxOc);
            conexion();
            rs = con.Consulta(sqlMaxOc);
            if (rs.next()) {
                if (!rs.getString("ocab_codigo").equals("")) {
                    int sumaOC = Integer.parseInt(rs.getString("ocab_codigo")) + 1;
                    //System.out.println("Suma Oc: "+sumaOC);
                    txtoc.setText(String.valueOf(sumaOC));
                }
                cbolug.setSelectedItem(rs.getString("ocab_entrega"));
                txtAdmin.setText(rs.getString("ocab_director"));
                txtDecano.setText(rs.getString("ocab_decano"));
                con.CerrarConexion();
            }
            btnnue.setEnabled(false);
            btnmod.setEnabled(false);
            btncan.setEnabled(true);
            btneli.setEnabled(false);
            btngra.setEnabled(true);
            txtoc.setEnabled(true);
            txtoc.grabFocus();
            txtoc.selectAll();
            contadorOc = "";
        } catch (SQLException ex) {
            Logger.getLogger(OCDirecta.class.getName()).log(Level.SEVERE, null, ex);
        }

    }//GEN-LAST:event_btnnueActionPerformed

    private void txtocActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtocActionPerformed
        DecimalFormat frm = new DecimalFormat("###,###");
        if (botonmodificar == true || botoneliminar == true) {
            String oc = "";
            if (txtoc.getText().isEmpty()) {
                BuscarOC bus = new BuscarOC(this, true);
                bus.setVisible(true);
                if (!BuscarOC.ContadorOc.isEmpty() || BuscarOC.ContadorOc != null) {
                    limpiar();
                    try {
                        contadorOc = BuscarOC.ContadorOc;
                        String consultaOc = "SELECT o.ocab_codigo,tm.tip_des,m.mod_des,i.imp_des,fp.pag_descripcion,p.pro_descripcion,"
                                + "c.cat_nombre,o.ocab_fecha,o.ocab_plazo_entrega,o.ocab_exenta,o.ocab_gra5"
                                + ",o.ocab_gra10,o.ocab_iva5,o.ocab_iva10,o.ocab_total,o.ocab_entrega,"
                                + "o.ocab_director,o.ocab_decano,o.tipo_presup,o.programa,o.sub_prog,o.proyecto,"
                                + "o.fuente_finan,o.org_financ,o.obj_gasto,o.cdp,o.fec_vigencia,o.fec_al FROM ordencab o,imputacion i,forma_pago fp,"
                                + "proveedor p,catedra c, modalidad m,tipo_modalidad tm WHERE o.imp_cod=i.imp_cod AND o.pag_codigo="
                                + "fp.pag_codigo AND o.pro_codigo=p.pro_codigo AND o.cat_codigo=c.cat_codigo "
                                + "AND o.mod_cod=m.mod_cod AND m.tip_cod=tm.tip_cod "
                                + "AND o.ocab_estado='A' AND o.ocab_contador='" + contadorOc + "'";
                        //System.out.println(consultaOc);
                        conexion();
                        rs = con.Consulta(consultaOc);
                        rs.next();
                        cbodpto.setSelectedItem(rs.getString("c.cat_nombre"));
                        cboimputacion.setSelectedItem(rs.getString("i.imp_des"));
                        cbolug.setSelectedItem(rs.getString("o.ocab_entrega"));
                        cbopag.setSelectedItem(rs.getString("fp.pag_descripcion"));
                        cbomod.setSelectedItem(rs.getString("tm.tip_des") + " - " + rs.getString("m.mod_des"));
                        cboprv.setSelectedItem(rs.getString("p.pro_descripcion"));
                        txtoc.setText(rs.getString("o.ocab_codigo"));
                        txtAdmin.setText(rs.getString("o.ocab_director"));
                        txtDecano.setText(rs.getString("o.ocab_decano"));
                        txtPlazent.setDate(rs.getDate("o.ocab_plazo_entrega"));
                        txtfecemi.setDate(rs.getDate("o.ocab_fecha"));
                        txtAdmin.setText(rs.getString("o.ocab_director"));
                        txttippresup.setText(rs.getString("o.tipo_presup"));
                        txtprograma.setText(rs.getString("o.programa"));
                        txtsubprog.setText(rs.getString("o.sub_prog"));
                        txtproyecto.setText(rs.getString("o.proyecto"));
                        txtfuentefinan.setText(rs.getString("o.fuente_finan"));
                        txtorgfinanc.setText(rs.getString("o.org_financ"));
                        txtobjgasto.setText(rs.getString("o.obj_gasto"));
                        txtcdp.setText(rs.getString("o.cdp"));
                        txtfecvigencia.setDate(rs.getDate("o.fec_vigencia"));
                        txtal.setDate(rs.getDate("o.fec_al"));
                        String consultaDetalleOc = "SELECT  d.art_codigo,"
                                + "d.odet_descripcion,a.art_unidad,d.odet_cantidad,d.odet_preciouni,a.art_impuesto "
                                + "FROM ordencab o,detalle d,articulo a WHERE d.art_codigo=a.art_codigo "
                                + "AND o.ocab_contador=d.ocab_contador"
                                + " AND o.ocab_contador='" + contadorOc + "'";
                        //System.out.println(consultaDetalleOc);
                        rs = con.Consulta(consultaDetalleOc);
                        tablaOC.changeSelection(0, 0, false, false);
                        int cont = 0;
                        while (rs.next()) {
                            //System.out.println("Codigo articulo: "+rs.getString(1)+" en la fila: "+c);
                            tablaOC.setValueAt(rs.getString(1), cont, 0);
                            tablaOC.setValueAt(rs.getString(3), cont, 2);
                            tablaOC.setValueAt(rs.getString(4), cont, 1);
                            tablaOC.setValueAt(frm.format(rs.getDouble(5)), cont, 4);
                            int columnaImpuesto = 0;
                            String impuesto = rs.getString(6);

                            if (impuesto.equals("Exenta")) {
                                columnaImpuesto = 5;
                            } else if (impuesto.equals("5%")) {
                                columnaImpuesto = 6;
                            } else {
                                columnaImpuesto = 7;
                            }
                            double tot = rs.getInt(4) * rs.getInt(5);
                            tablaOC.setValueAt(frm.format(tot), cont, columnaImpuesto);
                            //tablaOC.setValueAt(rs.getString(2), cont, 3);
                            AjustarDescripcionTabla(rs.getString(2));
                            //System.out.println(rs.getString(2));
                            while (tablaOC.getValueAt(cont, 3) != "") {
                                cont++;
                            }
                            //"Codigo", "Cantidad", "Unidad", "Descripcion",
                            //"Precio Uni.", "Total Exentas", "Total Grav. 5%", "Total Grav. 10%"
                            calculo();
                        }

                        tablaOC.clearSelection();
                        con.CerrarConexion();

                    } catch (SQLException ex) {
//                        Logger.getLogger(OC.class.getName()).log(Level.SEVERE, null, ex);
//                        JOptionPane.showMessageDialog(null, "El codigo no existe", "Error", JOptionPane.ERROR_MESSAGE);
//                        txtoc.selectAll();
                    } catch (ArrayIndexOutOfBoundsException ex) {
                        //Logger.getLogger(OC.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    habilitarCampos();
                    txtoc.grabFocus();
                }
            } else {
                oc = txtoc.getText();
                limpiar();
                try {
                    String consultaOc = "SELECT o.ocab_contador,tm.tip_des,m.mod_des,i.imp_des,fp.pag_descripcion,p.pro_descripcion,"
                            + "c.cat_nombre,o.ocab_fecha,o.ocab_plazo_entrega,o.ocab_exenta,o.ocab_gra5"
                            + ",o.ocab_gra10,o.ocab_iva5,o.ocab_iva10,o.ocab_total,o.ocab_entrega,"
                            + "o.ocab_director,o.ocab_decano,o.tipo_presup,o.programa,o.sub_prog,o.proyecto,"
                            + "o.fuente_finan,o.org_financ,o.obj_gasto,o.cdp,o.fec_vigencia,o.fec_al FROM ordencab o,imputacion i,forma_pago fp,"
                            + "proveedor p,catedra c, modalidad m,tipo_modalidad tm WHERE o.imp_cod=i.imp_cod AND o.pag_codigo="
                            + "fp.pag_codigo AND o.pro_codigo=p.pro_codigo AND o.cat_codigo=c.cat_codigo "
                            + "AND o.mod_cod=m.mod_cod AND m.tip_cod=tm.tip_cod "
                            + "AND o.ocab_estado='A' AND o.ocab_codigo='" + oc + "'";
                    //System.out.println(consultaOc);
                    conexion();
                    rs = con.Consulta(consultaOc);
                    if (rs.next()) {
                        contadorOc = rs.getString("o.ocab_contador");
                        cbodpto.setSelectedItem(rs.getString("c.cat_nombre"));
                        cboimputacion.setSelectedItem(rs.getString("i.imp_des"));
                        cbolug.setSelectedItem(rs.getString("o.ocab_entrega"));
                        cbopag.setSelectedItem(rs.getString("fp.pag_descripcion"));
                        cbomod.setSelectedItem(rs.getString("tm.tip_des") + " - " + rs.getString("m.mod_des"));
                        cboprv.setSelectedItem(rs.getString("p.pro_descripcion"));
                        txtAdmin.setText(rs.getString("o.ocab_director"));
                        txtDecano.setText(rs.getString("o.ocab_decano"));
                        txtPlazent.setDate(rs.getDate("o.ocab_plazo_entrega"));
                        txtfecemi.setDate(rs.getDate("o.ocab_fecha"));
                        txtAdmin.setText(rs.getString("o.ocab_director"));
                        txttippresup.setText(rs.getString("o.tipo_presup"));
                        txtprograma.setText(rs.getString("o.programa"));
                        txtsubprog.setText(rs.getString("o.sub_prog"));
                        txtproyecto.setText(rs.getString("o.proyecto"));
                        txtfuentefinan.setText(rs.getString("o.fuente_finan"));
                        txtorgfinanc.setText(rs.getString("o.org_financ"));
                        txtobjgasto.setText(rs.getString("o.obj_gasto"));
                        txtcdp.setText(rs.getString("o.cdp"));
                        txtfecvigencia.setDate(rs.getDate("o.fec_vigencia"));
                        txtal.setDate(rs.getDate("o.fec_al"));
                        String consultaDetalleOc = "SELECT  d.art_codigo,"
                                + "d.odet_descripcion,a.art_unidad,d.odet_cantidad,d.odet_preciouni,a.art_impuesto "
                                + "FROM ordencab o,detalle d,articulo a WHERE d.art_codigo=a.art_codigo "
                                + "AND o.ocab_contador=d.ocab_contador"
                                + " AND o.ocab_contador=" + contadorOc;
                        System.out.println(consultaDetalleOc);
                        rs = con.Consulta(consultaDetalleOc);
                        tablaOC.changeSelection(0, 0, false, false);
                        int cont = 0;
                        while (rs.next()) {
                            //System.out.println("Codigo articulo: "+rs.getString(1)+" en la fila: "+c);
                            tablaOC.setValueAt(rs.getString(1), cont, 0);
                            tablaOC.setValueAt(rs.getString(3), cont, 2);
                            tablaOC.setValueAt(rs.getString(4), cont, 1);
                            tablaOC.setValueAt(frm.format(rs.getDouble(5)), cont, 4);
                            int columnaImpuesto = 0;
                            String impuesto = rs.getString(6);

                            if (impuesto.equals("Exenta")) {
                                columnaImpuesto = 5;
                            } else if (impuesto.equals("5%")) {
                                columnaImpuesto = 6;
                            } else {
                                columnaImpuesto = 7;
                            }
                            double tot = rs.getInt(4) * rs.getInt(5);
                            tablaOC.setValueAt(frm.format(tot), cont, columnaImpuesto);
                            //tablaOC.setValueAt(rs.getString(2), cont, 3);
                            AjustarDescripcionTabla(rs.getString(2));
                            //System.out.println(rs.getString(2));
                            while (tablaOC.getValueAt(cont, 3) != "") {
                                cont++;
                            }
                            //"Codigo", "Cantidad", "Unidad", "Descripcion",
                            //"Precio Uni.", "Total Exentas", "Total Grav. 5%", "Total Grav. 10%"
                            calculo();
                        }

                        tablaOC.clearSelection();
                    } else {
                        JOptionPane.showMessageDialog(null, "El codigo no existe", "Error", JOptionPane.ERROR_MESSAGE);
                        txtoc.selectAll();
                    }
                    con.CerrarConexion();

                } catch (SQLException ex) {
                    Logger.getLogger(OCDirecta.class.getName()).log(Level.SEVERE, null, ex);
                    JOptionPane.showMessageDialog(null, "El codigo no existe", "Error", JOptionPane.ERROR_MESSAGE);
                    txtoc.selectAll();
                } catch (ArrayIndexOutOfBoundsException ex) {
                    //Logger.getLogger(OC.class.getName()).log(Level.SEVERE, null, ex);
                }
                habilitarCampos();
                txtoc.grabFocus();
            }
        } else {
            cbodpto.setEnabled(true);
            cbodpto.grabFocus();
        }
    }//GEN-LAST:event_txtocActionPerformed
    private void txtAdminActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtAdminActionPerformed
        txtAdmin.setText(txtAdmin.getText().toUpperCase());
        txtDecano.grabFocus();
    }//GEN-LAST:event_txtAdminActionPerformed

    private void txtPlazentPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_txtPlazentPropertyChange
        if (evt.getPropertyName().compareTo("date") == 0) {
            txtAdmin.requestFocusInWindow();
        }
    }//GEN-LAST:event_txtPlazentPropertyChange

    private void txtPlazentFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtPlazentFocusLost
        String dia = Integer.toString(txtPlazent.getCalendar().get(Calendar.DAY_OF_MONTH));
        String mes = Integer.toString(txtPlazent.getCalendar().get(Calendar.MONTH) + 1);
        String year = Integer.toString(txtPlazent.getCalendar().get(Calendar.YEAR));
        String fecha = (year + "-" + mes + "-" + dia);
        //System.out.println("La fecha es: " + fecha);
    }//GEN-LAST:event_txtPlazentFocusLost

    private void txtAdminFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtAdminFocusLost
        txtAdmin.setText(txtAdmin.getText().toUpperCase());
    }//GEN-LAST:event_txtAdminFocusLost

    private void txtDecanoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtDecanoFocusLost
        txtDecano.setText(txtDecano.getText().toUpperCase());
    }//GEN-LAST:event_txtDecanoFocusLost

    private void btnagrmismoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnagrmismoActionPerformed
        if (!txtoc.getText().isEmpty()) {
            int oc = Integer.parseInt(txtoc.getText()) + 1;
            txtoc.setText(String.valueOf(oc));
        }
        vaciarTabla();
        tablaOC.setEnabled(true);
        txtPlazent.setDate(null);
        txtoc.requestFocusInWindow();
        txtoc.selectAll();
        btnimp.setEnabled(false);
        btnagrmismo.setEnabled(false);
        botonagregar = true;
        btnnue.setEnabled(true);
        btngra.setEnabled(true);
        contadorOc = "";
    }//GEN-LAST:event_btnagrmismoActionPerformed

    private void cbolugFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cbolugFocusLost
        cbomod.grabFocus();
    }//GEN-LAST:event_cbolugFocusLost

    private void cboimputacionFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cboimputacionFocusLost
        /*if (editableImputacion == false) {
         if (cbodpto.getSelectedIndex() > 0) {
         //System.out.println("Codigo de imputacion: "+codImputacion);
         tablaOC.changeSelection(0, 0, false, false);
         tablaOC.requestFocus();
         btngra.setEnabled(true);
         //cboprv.grabFocus();
         }
         }*/
    }//GEN-LAST:event_cboimputacionFocusLost

    private void btnimputacionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnimputacionActionPerformed
        tablaOC.clearSelection();
        pulsaboton = true;
        Imputacion in = new Imputacion(this, true);
        in.setVisible(true);
        cargaCboImputacion();
        cboimputacion.setSelectedItem(DatoBotonBusqueda);
        cboimputacion.grabFocus();
        pulsaboton = false;
    }//GEN-LAST:event_btnimputacionActionPerformed

    private void btnprv1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnprv1ActionPerformed
        pulsaboton = true;
        Catedra cat = new Catedra(null, true);
        cat.setVisible(true);
        cargaCboDepto();
        cbodpto.setSelectedItem(DatoBotonBusqueda);
        pulsaboton = false;
    }//GEN-LAST:event_btnprv1ActionPerformed

    private void btnprvActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnprvActionPerformed
        pulsaboton = true;
        Proveedor prv = new Proveedor(null, true);
        prv.setVisible(true);
        cargaCboPrv();
        cboprv.setSelectedItem(DatoBotonBusqueda);
        pulsaboton = false;
    }//GEN-LAST:event_btnprvActionPerformed

    private void meneliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_meneliminarActionPerformed
        int nro = 0;
        int contador = 0;
        DefaultTableModel tm = null;
        try {
            if (!tablaOC.getValueAt(tablaOC.getSelectedRow(), 0).equals("") && tablaOC.getSelectedColumn() == 0) {
                tm = (DefaultTableModel) tablaOC.getModel();
                int fila = tablaOC.getSelectedRow();

                //Restar();
                while (fila >= 0) {
                    tm.removeRow(fila);
                    tablaOC.setModel(tm);

                    if (!tablaOC.getValueAt(fila, 0).equals("") || tablaOC.getValueAt(fila, 3).equals("")) {
                        contador++;
                        break;
                    }
                    contador++;
                }

                //System.out.println("Contador= "+contador);
                while (nro != contador) {
                    //System.out.println("El numero de fila= "+nro);
                    tm.addRow(new Object[]{"", "", "", "", "", "", "", ""});
                    tablaOC.setModel(tm);
                    nro++;
                }
                //System.out.println("Nro de filas en la tabla= "+tablaOC.getRowCount());
            }
            calculo();
        } catch (ArrayIndexOutOfBoundsException a) {
            while (nro != contador + 1) {
                //System.out.println("El numero de fila= "+nro);
                tm.addRow(new Object[]{"", "", "", "", "", "", "", ""});
                tablaOC.setModel(tm);
                nro++;
            }
            //JOptionPane.showMessageDialog(null, "No ha seleccionado una fila",
            //        "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_meneliminarActionPerformed

    private void btneliActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btneliActionPerformed
        tablaOC.setEnabled(true);
        botoneliminar = true;
        btngra.setEnabled(true);
        txtoc.setEnabled(true);
        txtoc.grabFocus();
        btnmod.setEnabled(false);
        btneli.setEnabled(false);
    }//GEN-LAST:event_btneliActionPerformed

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        validarFocus = true;
        botonagregar = false;
        botoneliminar = false;
        botonmodificar = false;
    }//GEN-LAST:event_formWindowClosed

    private void btnprv2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnprv2ActionPerformed
        pulsaboton = true;
        FrmModalidad pag = new FrmModalidad(this, true);
        pag.setVisible(true);
        cargaCboMod();
        cbomod.setSelectedItem(DatoBotonBusqueda);
        pulsaboton = false;
    }//GEN-LAST:event_btnprv2ActionPerformed

    private void tablaOCFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tablaOCFocusGained
        if (booleanFila == true) {
            tablaOC.changeSelection(tablaOC.getSelectedRow() - 1, 0, false, false);
            booleanFila = false;
        }
    }//GEN-LAST:event_tablaOCFocusGained

    private void tablaOCKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tablaOCKeyPressed
//        try {
        if ((!tablaOC.getValueAt(tablaOC.getSelectedRow(), 3).equals("")
                || !String.valueOf(tablaOC.getValueAt(tablaOC.getSelectedRow(), 3)).equals(""))
                && evt.getKeyCode() != KeyEvent.VK_TAB && evt.getKeyCode() != KeyEvent.VK_DOWN
                && evt.getKeyCode() != KeyEvent.VK_LEFT && evt.getKeyCode() != KeyEvent.VK_RIGHT
                && evt.getKeyCode() != KeyEvent.VK_UP && tablaOC.getSelectedColumn() == 0) {
            JOptionPane.showMessageDialog(this, "La fila seleccionada no es correcta");
        }
        if ((!tablaOC.getValueAt(tablaOC.getSelectedRow(), 1).equals("")
                || !String.valueOf(tablaOC.getValueAt(tablaOC.getSelectedRow(), 1)).equals(""))
                && evt.getKeyCode() != KeyEvent.VK_TAB && evt.getKeyCode() != KeyEvent.VK_DOWN
                && evt.getKeyCode() != KeyEvent.VK_LEFT && evt.getKeyCode() != KeyEvent.VK_RIGHT
                && evt.getKeyCode() != KeyEvent.VK_UP && tablaOC.getSelectedColumn() == 0) {
            JOptionPane.showMessageDialog(this, "Debe eliminar la Fila antes de ingresar un nuevo dato");
        } else if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            //System.out.println("Tabla posicion 0: "+tablaOC.getValueAt(tablaOC.getSelectedRow(), 0));
            try {
                tablaOC.getCellEditor().stopCellEditing();
            } catch (NullPointerException n) {
            }
            datosEnTabla();
        } else if (evt.getKeyCode() == KeyEvent.VK_N && tablaOC.getSelectedColumn() == 0) {
            try {
                tablaOC.getCellEditor().stopCellEditing();
            } catch (NullPointerException n) {
            }
            OCvisible = true;
            Articulo ar = new Articulo(null, true);
            ar.setVisible(true);
            banNuevoArticulo = true;
            datosEnTabla();
        }
//        } catch (ArrayIndexOutOfBoundsException e) {
//        }

    }//GEN-LAST:event_tablaOCKeyPressed

    private void tablaOCPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_tablaOCPropertyChange
        if (evt.getPropertyName().equals("tableCellEditor")) {
            if (tablaOC.getSelectedColumn() == 4 && !tablaOC.getValueAt(tablaOC.getSelectedRow(), 4).equals("")) {
                try {
                    String sql = "SELECT * FROM articulo WHERE art_codigo=" + tablaOC.getValueAt(tablaOC.getSelectedRow(), 0);
                    conexion();
                    rs = con.Consulta(sql);
                    rs.next();
                    //System.out.println("Fila de la tabla: " + tablaOC.getSelectedRow());
                    int colum = CalculoIva((String) tablaOC.getValueAt(tablaOC.getSelectedRow(), 1), sacarPunto((String) tablaOC.getValueAt(tablaOC.getSelectedRow(), 4)),
                            rs.getString("art_impuesto"));
                    tablaOC.setValueAt(Total, tablaOC.getSelectedRow(), colum);
                    calculo();
                    tablaOC.setValueAt(separadorDeMiles(Double.parseDouble((String) tablaOC.getValueAt(tablaOC.getSelectedRow(), 4))), tablaOC.getSelectedRow(), 4);

                } catch (SQLException ex) {
                    Logger.getLogger(OCDirecta.class
                            .getName()).log(Level.SEVERE, null, ex);
                } catch (NullPointerException n) {
                }
            }
            if (tablaOC.getSelectedColumn() == 0 && !String.valueOf(
                    tablaOC.getValueAt(tablaOC.getSelectedRow(), 0)).equals("")) {
                try {
                    Integer.parseInt(String.valueOf(tablaOC.getValueAt(tablaOC.getSelectedRow(), 0)));
                } catch (NumberFormatException e) {
                    validaCeldaCodigo = true;
                    JOptionPane.showMessageDialog(this, "Solo puede ingresar numeros en la columna Codigo", "Error", JOptionPane.ERROR_MESSAGE);
                    tablaOC.setValueAt("", tablaOC.getSelectedRow(), 0);
                }
            }

        }
    }//GEN-LAST:event_tablaOCPropertyChange

    private void tablaOCMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaOCMouseReleased
        if (evt.isPopupTrigger()) {
            menuEmergente.show(tablaOC, evt.getX(), evt.getY());
        }
    }//GEN-LAST:event_tablaOCMouseReleased

    private void cbodptoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cbodptoFocusGained
//        if (botonmodificar == false) {
//            if (txtoc.getText().isEmpty()) {
//                txtoc.grabFocus();
//                JOptionPane.showMessageDialog(null, "El campo no puede estar vacio", "Mensaja", JOptionPane.INFORMATION_MESSAGE);
//            }
//        }
    }//GEN-LAST:event_cbodptoFocusGained

    private void txtalFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtalFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_txtalFocusLost

    private void txtalPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_txtalPropertyChange
        // TODO add your handling code here:
    }//GEN-LAST:event_txtalPropertyChange

    private void txttippresupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txttippresupActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txttippresupActionPerformed

    private void txttippresupFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txttippresupFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_txttippresupFocusLost

    private void txtprogramaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtprogramaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtprogramaActionPerformed

    private void txtprogramaFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtprogramaFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_txtprogramaFocusLost

    private void txtsubprogActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtsubprogActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtsubprogActionPerformed

    private void txtsubprogFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtsubprogFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_txtsubprogFocusLost

    private void txtproyectoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtproyectoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtproyectoActionPerformed

    private void txtproyectoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtproyectoFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_txtproyectoFocusLost

    private void txtfuentefinanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtfuentefinanActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtfuentefinanActionPerformed

    private void txtfuentefinanFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtfuentefinanFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_txtfuentefinanFocusLost

    private void txtorgfinancActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtorgfinancActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtorgfinancActionPerformed

    private void txtorgfinancFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtorgfinancFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_txtorgfinancFocusLost

    private void txtobjgastoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtobjgastoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtobjgastoActionPerformed

    private void txtobjgastoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtobjgastoFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_txtobjgastoFocusLost

    private void txtcdpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtcdpActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtcdpActionPerformed

    private void txtcdpFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtcdpFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_txtcdpFocusLost

    private void cbopagFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cbopagFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_cbopagFocusLost

    private void btnprv3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnprv3ActionPerformed
        FrmForma forma = new FrmForma(this, true);
        forma.setVisible(true);
        cargaCboPago();
    }//GEN-LAST:event_btnprv3ActionPerformed

    private void cbomodFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cbomodFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_cbomodFocusLost

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
            java.util.logging.Logger.getLogger(OCDirecta.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(OCDirecta.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(OCDirecta.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(OCDirecta.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                OCDirecta dialog = new OCDirecta(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton btnagrmismo;
    private javax.swing.JButton btncan;
    private javax.swing.JButton btncer;
    private javax.swing.JButton btneli;
    private javax.swing.JButton btngra;
    private javax.swing.JButton btnimp;
    private javax.swing.JButton btnimputacion;
    private javax.swing.JButton btnmod;
    public static javax.swing.JButton btnnue;
    private javax.swing.JButton btnprv;
    private javax.swing.JButton btnprv1;
    private javax.swing.JButton btnprv2;
    private javax.swing.JButton btnprv3;
    private javax.swing.JComboBox cbodpto;
    private javax.swing.JComboBox cboimputacion;
    private javax.swing.JComboBox cbolug;
    private javax.swing.JComboBox cbomod;
    private javax.swing.JComboBox cbopag;
    private javax.swing.JComboBox cboprv;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JMenuItem mencancelar;
    private javax.swing.JMenuItem meneliminar;
    private javax.swing.JPopupMenu menuEmergente;
    private javax.swing.JTable tablaOC;
    private javax.swing.JTextField txtAdmin;
    private javax.swing.JTextField txtDecano;
    private com.toedter.calendar.JDateChooser txtPlazent;
    private com.toedter.calendar.JDateChooser txtal;
    public static javax.swing.JTextField txtcdp;
    private com.toedter.calendar.JDateChooser txtfecemi;
    private com.toedter.calendar.JDateChooser txtfecvigencia;
    public static javax.swing.JTextField txtfuentefinan;
    private javax.swing.JFormattedTextField txtliqiva10;
    private javax.swing.JFormattedTextField txtliqiva5;
    public static javax.swing.JTextField txtobjgasto;
    public static javax.swing.JTextField txtoc;
    public static javax.swing.JTextField txtorgfinanc;
    public static javax.swing.JTextField txtprograma;
    public static javax.swing.JTextField txtproyecto;
    public static javax.swing.JTextField txtsubprog;
    private javax.swing.JFormattedTextField txtsubtotExentas;
    private javax.swing.JFormattedTextField txtsubtotIva10;
    private javax.swing.JFormattedTextField txtsubtotIva5;
    public static javax.swing.JTextField txttippresup;
    private javax.swing.JFormattedTextField txttotgral;
    private javax.swing.JFormattedTextField txttotiva;
    // End of variables declaration//GEN-END:variables
}
