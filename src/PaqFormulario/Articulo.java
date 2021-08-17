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
import javax.swing.InputMap;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

/**
 *
 * @author Administrador
 */
public class Articulo extends javax.swing.JDialog {

    private Connection cn = null;
    private Conexion con;
    private ResultSet rs = null;
    static ResultSet res = null;
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
    static String codigoArticulo = null;
    private String codigoCatedra;
    private ResultSet rscboUni;

    /**
     * Creates new form Articulo
     */
    public Articulo(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        con = new Conexion(AcesoUsuario.host, AcesoUsuario.base, AcesoUsuario.user, AcesoUsuario.pass);
        centrar();
        botonEnter();
        cargaCboCatedra();
        cargaCboUni();
        btnAgr.requestFocusInWindow();
        cboimp.setSelectedIndex(-1);
        codigoArticulo = null;
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
        btnCan.setInputMap(0, map);
        btnGra.setInputMap(0, map);
        btnMod.setInputMap(0, map);
        btnBus.setInputMap(0, map);
        btnSal.setInputMap(0, map);
    }

    private boolean Validar() {
        boolean val = false;
        if (cbocat.getSelectedIndex() == 0 || cbotip.getSelectedIndex() == 0 || txtdes.getText().equals("")) {
            val = true;
        }
        return val;
    }

    private boolean comparar(String sql) {
        boolean r = false;
        try {
            rs = con.Consulta(sql);
            if (rs.next()) {
                r = true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(Articulo.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(Articulo.class.getName()).log(Level.SEVERE, null, ex);
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
            cbocat.addItem("");
            rscboCat.beforeFirst();
            while (rscboCat.next()) {
                try {
                    cbocat.addItem(rscboCat.getString("cat_nombre"));
                } catch (SQLException ex) {
                    Logger.getLogger(Articulo.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
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
            cbotip.addItem("");
            rscboTipo.beforeFirst();
            while (rscboTipo.next()) {
                try {
                    cbotip.addItem(rscboTipo.getString("tip_descripcion"));
                } catch (SQLException ex) {
                    Logger.getLogger(Articulo.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(Articulo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
     private void cargaCboUni() {
        try {
            String sql;
            sql = "SELECT * FROM unidadmed";
            //System.out.println(sql);
            rscboUni = con.Consulta(sql);
            cbouni.removeAllItems();
            rscboUni.beforeFirst();
            while (rscboUni.next()) {
                try {
                    cbouni.addItem(rscboUni.getString("des"));
                } catch (SQLException ex) {
                    Logger.getLogger(Articulo.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(Articulo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void VerArticulo() {
        //System.out.println("Codigo de articulo: "+codigoArticulo);
        try {
            if (!codigoArticulo.isEmpty()) {
                try {
                    String sql = "SELECT a.art_codigo,c.cat_nombre,c.cat_codigo,t.tip_descripcion"
                            + ",a.art_descripcion,a.art_precio,a.art_unidad,a.art_presentacion"
                            + ",a.art_impuesto FROM articulo a,tipo_articulo t,catedra c WHERE c.cat_codigo=a.cat_codigo "
                            + "AND t.tip_cod=a.tip_cod AND a.art_codigo=" + codigoArticulo;
                    //System.out.println(sql);
                    rs = con.Consulta(sql);
                    if (rs.next()) {
                        codigoCatedra = rs.getString("cat_codigo");
                        txtcod.setText(codigoArticulo);
                        txtdes.setText(rs.getString("a.art_descripcion"));
                        txtprecio.setText(rs.getString("a.art_precio"));
                        txtpresentacion.setText(rs.getString("a.art_presentacion"));
                        cbocat.setSelectedItem(rs.getString("c.cat_nombre"));
                        cargaCboTipo();
                        cbotip.setSelectedItem(rs.getString("t.tip_descripcion"));
                        cbouni.setSelectedItem(rs.getString("a.art_unidad"));
                        cboimp.setSelectedItem(rs.getString("a.art_impuesto"));
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(Articulo.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } catch (NullPointerException n) {
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

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtcod = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtprecio = new javax.swing.JFormattedTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtdes = new javax.swing.JTextArea();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtpresentacion = new javax.swing.JTextArea();
        btnAgr = new javax.swing.JButton();
        btnMod = new javax.swing.JButton();
        btnGra = new javax.swing.JButton();
        btnCan = new javax.swing.JButton();
        btnSal = new javax.swing.JButton();
        cbouni = new javax.swing.JComboBox();
        btnBus = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        cbotip = new javax.swing.JComboBox();
        jLabel6 = new javax.swing.JLabel();
        cbocat = new javax.swing.JComboBox();
        jLabel8 = new javax.swing.JLabel();
        cboimp = new javax.swing.JComboBox();
        btnprv = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Articulo");

        jPanel1.setBorder(javax.swing.BorderFactory.createBevelBorder(0));

        jLabel1.setText("Codigo");

        txtcod.setBorder(javax.swing.BorderFactory.createBevelBorder(0));
        txtcod.setEnabled(false);
        txtcod.setOpaque(false);
        txtcod.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtcodActionPerformed(evt);
            }
        });

        jLabel2.setText("Descripción");

        jLabel3.setText("Precio");

        txtprecio.setBorder(javax.swing.BorderFactory.createBevelBorder(0));
        txtprecio.setText("0");
        txtprecio.setEnabled(false);
        txtprecio.setNextFocusableComponent(cbouni);
        txtprecio.setOpaque(false);
        txtprecio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtprecioActionPerformed(evt);
            }
        });

        txtdes.setColumns(20);
        txtdes.setLineWrap(true);
        txtdes.setRows(5);
        txtdes.setBorder(javax.swing.BorderFactory.createBevelBorder(0));
        txtdes.setEnabled(false);
        txtdes.setNextFocusableComponent(txtprecio);
        txtdes.setOpaque(false);
        txtdes.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtdesFocusLost(evt);
            }
        });
        txtdes.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtdesKeyPressed(evt);
            }
        });
        jScrollPane1.setViewportView(txtdes);

        jLabel4.setText("Unidad");

        jLabel5.setText("Presentación");

        txtpresentacion.setColumns(20);
        txtpresentacion.setLineWrap(true);
        txtpresentacion.setRows(5);
        txtpresentacion.setBorder(javax.swing.BorderFactory.createBevelBorder(0));
        txtpresentacion.setEnabled(false);
        txtpresentacion.setNextFocusableComponent(btnGra);
        txtpresentacion.setOpaque(false);
        txtpresentacion.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtpresentacionFocusLost(evt);
            }
        });
        txtpresentacion.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtpresentacionKeyPressed(evt);
            }
        });
        jScrollPane2.setViewportView(txtpresentacion);

        btnAgr.setText("Agregar"); // NOI18N
        btnAgr.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAgrActionPerformed(evt);
            }
        });

        btnMod.setText("Modificar"); // NOI18N
        btnMod.setEnabled(false);
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

        cbouni.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Unidad", "Metros", "Kilos", "Litros", "M2" }));
        cbouni.setBorder(javax.swing.BorderFactory.createBevelBorder(0));
        cbouni.setEnabled(false);
        cbouni.setNextFocusableComponent(txtpresentacion);
        cbouni.setOpaque(false);
        cbouni.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                cbouniFocusLost(evt);
            }
        });
        cbouni.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                cbouniKeyPressed(evt);
            }
        });

        btnBus.setText("Buscar"); // NOI18N
        btnBus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBusActionPerformed(evt);
            }
        });
        btnBus.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                btnBusKeyPressed(evt);
            }
        });

        jLabel7.setText("Tipo de articulo");

        cbotip.setBorder(javax.swing.BorderFactory.createBevelBorder(0));
        cbotip.setEnabled(false);
        cbotip.setNextFocusableComponent(txtdes);
        cbotip.setOpaque(false);
        cbotip.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbotipItemStateChanged(evt);
            }
        });
        cbotip.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                cbotipFocusLost(evt);
            }
        });
        cbotip.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                cbotipKeyPressed(evt);
            }
        });

        jLabel6.setText("Servicio");

        cbocat.setBorder(javax.swing.BorderFactory.createBevelBorder(0));
        cbocat.setEnabled(false);
        cbocat.setNextFocusableComponent(cbotip);
        cbocat.setOpaque(false);
        cbocat.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbocatItemStateChanged(evt);
            }
        });

        jLabel8.setText("Impuesto");

        cboimp.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "", "5%", "10%", "Exenta" }));
        cboimp.setBorder(javax.swing.BorderFactory.createBevelBorder(0));
        cboimp.setEnabled(false);
        cboimp.setNextFocusableComponent(txtpresentacion);
        cboimp.setOpaque(false);
        cboimp.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cboimpItemStateChanged(evt);
            }
        });

        btnprv.setIcon(new javax.swing.ImageIcon(getClass().getResource("/paqImagenes/iconoAgregar.gif"))); // NOI18N
        btnprv.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnprvActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(78, 78, 78)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 321, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(txtcod, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbocat, javax.swing.GroupLayout.PREFERRED_SIZE, 216, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(cboimp, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(txtprecio, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbouni, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(cbotip, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnprv, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(265, 265, 265)
                                .addComponent(btnCan, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnBus, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnSal, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(btnAgr, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnMod)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnGra, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap())
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnAgr, btnBus, btnCan, btnGra, btnMod, btnSal});

        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(cbocat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel6))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtcod, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel1)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel7)
                    .addComponent(cbotip, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnprv))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtprecio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(cbouni, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(cboimp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(btnAgr)
                    .addComponent(btnMod, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnGra, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCan, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnBus)
                    .addComponent(btnSal, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnAgr, btnBus, btnCan, btnGra, btnMod, btnSal});

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnAgrActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgrActionPerformed

        botonagregar = true;
        btnMod.setEnabled(false);
        btnCan.setEnabled(true);
        cbocat.setEnabled(true);
        cbocat.grabFocus();
        txtdes.setEnabled(true);
        txtprecio.setEnabled(true);
        txtpresentacion.setEnabled(true);
        cbotip.setEnabled(true);
        cbouni.setEnabled(true);
        cboimp.setEnabled(true);
        btnAgr.setEnabled(false);

    }//GEN-LAST:event_btnAgrActionPerformed

    private void btnModActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnModActionPerformed
        btnMod.setEnabled(false);
        botonmodificar = true;
        cbotip.setEnabled(true);
        cbotip.grabFocus();
        cboimp.setEnabled(true);
        txtdes.setEnabled(true);
        txtprecio.setEnabled(true);
        txtpresentacion.setEnabled(true);
        cbotip.setEnabled(true);
        cbouni.setEnabled(true);
        btnAgr.setEnabled(false);
        btnCan.setEnabled(true);
        btnGra.setEnabled(true);

    }//GEN-LAST:event_btnModActionPerformed

    private void btnModKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnModKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_RIGHT) {
            this.btnSal.grabFocus();
        } else if (evt.getKeyCode() == KeyEvent.VK_LEFT) {
            btnAgr.grabFocus();
        }
    }//GEN-LAST:event_btnModKeyPressed

    private void btnGraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGraActionPerformed
        System.out.println("Catedra: " + cbocat.getSelectedIndex() + " tipo art: " + cbotip.getSelectedIndex() + " unidad: " + cbouni.getSelectedIndex());
        if (Validar() == true) {
            JOptionPane.showMessageDialog(this, "Hay campos vacios; Verifiquelo!!!", "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            int resp = JOptionPane.showConfirmDialog(null, "Realmente, ¿Desea Grabar"
                    + " los Datos?", "Mensaje", JOptionPane.OK_CANCEL_OPTION);

            if (resp == JOptionPane.OK_OPTION) {
                if (botonagregar == true) {
                    try {
                        String codigo = "Select ifnull(max(art_codigo),0)+1 codigo From articulo";
//                        System.out.println(codigo);
                        rs = con.Consulta(codigo);
                        rs.next();
                        txtcod.setText(rs.getString("codigo"));
                        String sql = "INSERT INTO articulo (art_codigo,cat_codigo,tip_cod,"
                                + "art_descripcion,art_precio,art_unidad,art_cod_catalogo,"
                                + "art_presentacion,art_impuesto) VALUES (" + txtcod.getText() + ","
                                + codigoCatedra + ",'" + codigoTipo + "','" + txtdes.getText().trim()
                                + "','" + txtprecio.getText() + "','" + cbouni.getSelectedItem()
                                + "','" + null + "','" + txtpresentacion.getText().trim() + "','"
                                + cboimp.getSelectedItem() + "')";
                        //System.out.println(sql);
                        con.Guardar(sql);
                        if (OC.OCvisible == true) {
                            Articulo.codigoArticulo = txtcod.getText();
                            String cantidad = JOptionPane.showInputDialog(null, "Ingrese cantidad");
                            if (cantidad != null) {
                                OC.cantidadOC = cantidad;
                            }
                            this.dispose();
                        }
                        botonagregar = false;
                    } catch (SQLException ex) {
                        Logger.getLogger(Articulo.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                if (botonmodificar == true) {
                    String update = "UPDATE articulo SET "
                            + "cat_codigo=" + codigoCatedra
                            + ",tip_cod=" + codigoTipo
                            + ",art_descripcion='" + txtdes.getText().trim()
                            + "',art_precio='" + txtprecio.getText()
                            + "',art_unidad='" + cbouni.getSelectedItem()
                            + "',art_presentacion='" + txtpresentacion.getText().trim()
                            + "',art_impuesto='" + cboimp.getSelectedItem()
                            + "' WHERE art_codigo=" + txtcod.getText();
                    System.out.println(update);
                    con.Guardar(update);

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
        tablabuscar = false;
        botonagregar = false;
        botonborrar = false;
        botonmodificar = false;
        btnCan.setEnabled(false);
        btnGra.setEnabled(false);
        btnAgr.setEnabled(true);
        btnMod.setEnabled(false);
        txtcod.setText("");
        txtdes.setText("");
        txtdes.setEnabled(false);
        txtprecio.setEnabled(false);
        txtprecio.setText("");
        txtpresentacion.setText("");
        cboimp.setSelectedIndex(-1);
        cboimp.setEnabled(false);
        txtpresentacion.setEnabled(false);
        cbocat.setSelectedIndex(-1);
        cbocat.setEnabled(false);
        cbotip.setSelectedIndex(-1);
        cbotip.setEnabled(false);
        cbouni.setSelectedIndex(-1);
        cbouni.setEnabled(false);
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
            btnBus.grabFocus();
        }
    }//GEN-LAST:event_btnSalKeyPressed

    private void btnBusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBusActionPerformed
        BuscarArticulo ba = new BuscarArticulo(this, true);
        ba.setVisible(true);
        VerArticulo();
        btnCan.setEnabled(true);
        btnMod.setEnabled(true);
        btnMod.grabFocus();
    }//GEN-LAST:event_btnBusActionPerformed

    private void btnBusKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnBusKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_RIGHT) {
            btnSal.grabFocus();
        } else if (evt.getKeyCode() == KeyEvent.VK_LEFT) {
            btnAgr.grabFocus();
        }
    }//GEN-LAST:event_btnBusKeyPressed

    private void cbotipKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cbotipKeyPressed

    }//GEN-LAST:event_cbotipKeyPressed

    private void txtdesKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtdesKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_TAB) {
            txtprecio.setEnabled(true);
            txtprecio.grabFocus();
        }
    }//GEN-LAST:event_txtdesKeyPressed

    private void txtcodActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtcodActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtcodActionPerformed

    private void txtprecioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtprecioActionPerformed
        cbouni.setEnabled(true);
        cbouni.grabFocus();
    }//GEN-LAST:event_txtprecioActionPerformed

    private void cbouniKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cbouniKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            txtpresentacion.setEnabled(true);
            txtpresentacion.grabFocus();
        }
    }//GEN-LAST:event_cbouniKeyPressed

    private void txtpresentacionKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtpresentacionKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_TAB) {
            cboimp.setEnabled(true);
            cboimp.grabFocus();
        }
    }//GEN-LAST:event_txtpresentacionKeyPressed

    private void cbotipFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cbotipFocusLost
        /* if (cbotip.getSelectedIndex() > -1) {
         //  System.out.println("ha upei chongo");
         try {
         rscboTipo.beforeFirst();
         while (rscboTipo.next()) {
         if (rscboTipo.getString("tip_descripcion").equals(cbotip.getSelectedItem())) {
         codigoTipo = rscboTipo.getString("tip_cod");
         txtdes.setEnabled(true);
         txtdes.grabFocus();
         break;
         }
         }

         } catch (SQLException ex) {
         Logger.getLogger(TipoArticulo.class.getName()).log(Level.SEVERE, null, ex);
         }
         }*/
    }//GEN-LAST:event_cbotipFocusLost

    private void cbouniFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cbouniFocusLost
        txtpresentacion.setEnabled(true);
        txtpresentacion.grabFocus();
    }//GEN-LAST:event_cbouniFocusLost

    private void btnprvActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnprvActionPerformed
        TipoArticulo tipo = new TipoArticulo(null, true);
        tipo.setVisible(true);
        cargaCboTipo();
    }//GEN-LAST:event_btnprvActionPerformed

    private void cboimpItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cboimpItemStateChanged
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            if (cboimp.getSelectedIndex() != 0) {
                btnGra.setEnabled(true);
            }
        }
    }//GEN-LAST:event_cboimpItemStateChanged

    private void cbotipItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbotipItemStateChanged
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            if (cbotip.getSelectedIndex() > 0) {
                //  System.out.println("ha upei chongo");
                try {
                    rscboTipo.beforeFirst();
                    while (rscboTipo.next()) {
                        if (rscboTipo.getString("tip_descripcion").equals(cbotip.getSelectedItem())) {
                            codigoTipo = rscboTipo.getString("tip_cod");
                            txtdes.setEnabled(true);
                            txtdes.grabFocus();
                            break;
                        }
                    }

                } catch (SQLException ex) {
                    Logger.getLogger(TipoArticulo.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }//GEN-LAST:event_cbotipItemStateChanged

    private void cbocatItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbocatItemStateChanged
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            if (cbocat.getSelectedIndex() > 0) {
                //  System.out.println("ha upei chongo");
                try {
                    rscboCat.beforeFirst();
                    while (rscboCat.next()) {
                        if (rscboCat.getString("cat_nombre").equals(cbocat.getSelectedItem())) {
                            codigoCatedra = rscboCat.getString("cat_codigo");
                            cargaCboTipo();
                            cbotip.setEnabled(true);
                            break;
                        }
                    }

                } catch (SQLException ex) {
                    Logger.getLogger(Articulo.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }//GEN-LAST:event_cbocatItemStateChanged

    private void txtdesFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtdesFocusLost
        txtdes.setText(txtdes.getText().toUpperCase());
    }//GEN-LAST:event_txtdesFocusLost

    private void txtpresentacionFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtpresentacionFocusLost
        txtpresentacion.setText(txtpresentacion.getText().toUpperCase());
    }//GEN-LAST:event_txtpresentacionFocusLost

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
            java.util.logging.Logger.getLogger(Articulo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Articulo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Articulo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Articulo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                Articulo dialog = new Articulo(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton btnAgr;
    private javax.swing.JButton btnBus;
    private javax.swing.JButton btnCan;
    private javax.swing.JButton btnGra;
    private javax.swing.JButton btnMod;
    private javax.swing.JButton btnSal;
    private javax.swing.JButton btnprv;
    private javax.swing.JComboBox cbocat;
    private javax.swing.JComboBox cboimp;
    private javax.swing.JComboBox cbotip;
    private javax.swing.JComboBox cbouni;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextField txtcod;
    private javax.swing.JTextArea txtdes;
    private javax.swing.JFormattedTextField txtprecio;
    private javax.swing.JTextArea txtpresentacion;
    // End of variables declaration//GEN-END:variables
}
