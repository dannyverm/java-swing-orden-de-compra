/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package PaqFormulario;

import clases.Conexion;
import clases.EncriptarDesencriptar;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.StringTokenizer;
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
public class Usuario extends javax.swing.JDialog {

    private Connection cn = null;
    static Conexion con;
    private ResultSet rs = null;
    private ResultSet rsSer = null;
    private Statement st = null;
    public String SerCod = null;
    private String PasUsu = null;
    public boolean botonagregar = false;
    public boolean botonmodificar = false;
    public boolean botonborrar = false;
    private ResultSet rscboCat;
    private boolean evento = false;
    private boolean evento2;
    private String catedra;
    private String sede;
    private boolean tablabuscar=false;
    private boolean txtcodbuscar=false;

    /**
     * Creates new form Usuario
     */
    public Usuario(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        botonEnter();
        //System.out.println("host: " + AcesoUsuario.host);
        con = new Conexion(AcesoUsuario.host, AcesoUsuario.base, AcesoUsuario.user, AcesoUsuario.pass);
        ActualizarGrilla();
        cargaCboCatedra();
        centrar();
        btnAgr.grabFocus();
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

    public void vaciarTabla() {
        int i = 0;

        while (i != TablaUsuario.getRowCount()) {
            TablaUsuario.setValueAt("", i, 0);
            TablaUsuario.setValueAt("", i, 1);
            TablaUsuario.setValueAt("", i, 2);
            i++;
        }
    }

    public boolean validar(String query) {
        boolean ban = true;
        try {
            // System.out.println(query);

            rs = con.Consulta(query);
            if (rs.next() == false) {
                ban = false;
            }
        } catch (SQLException ex) {
            Logger.getLogger(Usuario.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ban;
    }

    private void ActualizarGrilla() {
        String Nombre = null;
        try {

            int c = 0;
            vaciarTabla();
            String sql = "Select *"
                    + " FROM usuario"
                    + " ORDER BY usu_cod";
            //System.out.println(sql);
            rs = con.Consulta(sql);
            if (rs.next() == true) {
                rs.beforeFirst();
                while (rs.next()) {
                    if (TablaUsuario.getRowCount() <= rs.getRow() - 1) {
                        DefaultTableModel tm = (DefaultTableModel) TablaUsuario.getModel();
                        tm.addRow(new Object[]{new String(""), new String(""), new String("")});
                        TablaUsuario.setModel(tm);
                    }
                    TablaUsuario.setValueAt(rs.getString(1), c, 0);
                    TablaUsuario.setValueAt(rs.getString(3), c, 1);
                    TablaUsuario.setValueAt(rs.getString(4), c, 2);
                    c++;
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(Usuario.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void cargaCboCatedra() {
        try {
            String sql = "SELECT * FROM catedra ORDER BY cat_nombre";
            rscboCat = con.Consulta(sql);
            cbocat.removeAllItems();
            cbocat.addItem("");
            while (rscboCat.next()) {
                try {
                    cbocat.addItem(rscboCat.getString("cat_nombre"));
                } catch (SQLException ex) {
                    Logger.getLogger(Usuario.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(Usuario.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void CodigoCatedra() {
        try {
            catedra = (String) cbocat.getSelectedItem();
            
            String sql = "SELECT cat_codigo FROM catedra WHERE cat_nombre ='" 
                    + cbocat.getSelectedItem()+ "' AND cat_sede='"+cbosede.getSelectedItem() +"'";
            System.out.println(sql);
            rs = con.Consulta(sql);
            rs.next();
            catedra = rs.getString("cat_codigo");
            
        } catch (SQLException ex) {
            Logger.getLogger(Usuario.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void buscar(String val) {
        String sql1 = "SELECT * FROM usuario WHERE  usu_cod=" + val;
        //System.out.println(val);

        if (validar(sql1) == true) {
            try {
                String sql = "Select * FROM usuario "
                        + "WHERE usu_cod=" + val;
                
                //System.out.println(sql);
                rs = con.Consulta(sql);
                rs.next();
                txtCod.setText(rs.getString("usu_cod"));
                txtNom.setText(rs.getString("usu_nom"));
                txtape.setText(rs.getString("usu_ape"));
                byte[] user=rs.getBytes("usu_user");
                txtusu.setText(EncriptarDesencriptar.desencriptar(user));
                String sql2="SELECT * FROM catedra WHERE cat_codigo="+rs.getString("cat_codigo");
                rs=con.Consulta(sql2);
                rs.next();
                cbocat.setSelectedItem(rs.getString("cat_nombre"));
                cbosede.setSelectedItem(rs.getString("cat_sede"));
                btnMod.setEnabled(true);
                tablabuscar = true;
            } catch (SQLException ex) {
                Logger.getLogger(Usuario.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            JOptionPane.showMessageDialog(null, "El Codigo No Existe");
            this.txtCod.setText("");
            this.txtCod.grabFocus();
        }

        if (txtcodbuscar == true) {
            txtCod.setEnabled(false);
            txtNom.setEnabled(true);
            txtNom.grabFocus();
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

        jLabel3 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        TablaUsuario = new javax.swing.JTable();
        jPanel = new javax.swing.JPanel();
        btnAgr = new javax.swing.JButton();
        btnMod = new javax.swing.JButton();
        btnGra = new javax.swing.JButton();
        btnCan = new javax.swing.JButton();
        btnSal = new javax.swing.JButton();
        jLabel13 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        txtNom = new javax.swing.JTextField();
        txtCod = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        txtusu = new javax.swing.JFormattedTextField();
        jLabel1 = new javax.swing.JLabel();
        txtPas = new javax.swing.JPasswordField();
        jLabel2 = new javax.swing.JLabel();
        txtPas2 = new javax.swing.JPasswordField();
        jLabel5 = new javax.swing.JLabel();
        cbocat = new javax.swing.JComboBox();
        jLabel19 = new javax.swing.JLabel();
        txtape = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        cbosede = new javax.swing.JComboBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Usuario");

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(51, 153, 0));
        jLabel3.setText("Usuario");

        TablaUsuario.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
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
                "Codigo", "Nombre", "Usuario"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        TablaUsuario.getTableHeader().setReorderingAllowed(false);
        TablaUsuario.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                TablaUsuarioMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(TablaUsuario);

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

        jLabel15.setText("Usuario"); // NOI18N

        txtNom.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        txtNom.setEnabled(false);
        txtNom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNomActionPerformed(evt);
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

        txtusu.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        txtusu.setEnabled(false);
        txtusu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtusuActionPerformed(evt);
            }
        });
        txtusu.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtusuFocusLost(evt);
            }
        });
        txtusu.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtusuKeyPressed(evt);
            }
        });

        jLabel1.setText("Contraseña");

        txtPas.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        txtPas.setEnabled(false);
        txtPas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPasActionPerformed(evt);
            }
        });

        jLabel2.setText("Confirmar Contraseña");

        txtPas2.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        txtPas2.setEnabled(false);
        txtPas2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPas2ActionPerformed(evt);
            }
        });
        txtPas2.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtPas2FocusLost(evt);
            }
        });

        jLabel5.setText("Servicio");

        cbocat.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "", "SL", "SJ" }));
        cbocat.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        cbocat.setEnabled(false);
        cbocat.setOpaque(false);
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

        jLabel19.setText("Apellido"); // NOI18N

        txtape.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        txtape.setEnabled(false);
        txtape.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtapeActionPerformed(evt);
            }
        });

        jLabel6.setText("Sede");

        cbosede.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "", "SL", "SJ" }));
        cbosede.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        cbosede.setEnabled(false);
        cbosede.setOpaque(false);
        cbosede.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                cbosedeFocusLost(evt);
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
                    .addGroup(jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(jPanelLayout.createSequentialGroup()
                            .addComponent(jLabel13)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(txtCod, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(13, 13, 13)
                            .addComponent(jLabel18)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(txtNom, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jLabel19)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(txtape, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanelLayout.createSequentialGroup()
                            .addComponent(jLabel15)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(txtusu, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(jLabel1)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(txtPas, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(jLabel2)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(txtPas2, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanelLayout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbocat, javax.swing.GroupLayout.PREFERRED_SIZE, 237, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbosede, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanelLayout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(btnAgr, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnMod, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnGra, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnCan, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnSal, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(40, Short.MAX_VALUE))
        );
        jPanelLayout.setVerticalGroup(
            jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtCod, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel13))
                    .addGroup(jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtNom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel18)
                        .addComponent(txtape, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel19)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtPas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(txtPas2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15)
                    .addComponent(txtusu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel6)
                        .addComponent(cbosede, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel5)
                        .addComponent(cbocat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(15, 15, 15)
                .addGroup(jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(btnAgr, javax.swing.GroupLayout.DEFAULT_SIZE, 33, Short.MAX_VALUE)
                    .addComponent(btnMod, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jScrollPane1)
                            .addComponent(jPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(240, 240, 240)
                        .addComponent(jLabel3)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 192, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnAgrActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgrActionPerformed
        try {
            botonagregar = true;
            String sql = "Select ifnull(max(usu_cod),0)+1 codigo From usuario Order By usu_cod";
            //System.out.println(sql);
            rs = con.Consulta(sql);
            rs.next();
            this.txtCod.setText(rs.getString("codigo"));
            btnAgr.setEnabled(false);
            this.btnMod.setEnabled(false);
            this.btnCan.setEnabled(true);
            this.txtNom.setEnabled(true);
            this.txtNom.grabFocus();
        } catch (SQLException ex) {
            Logger.getLogger(Usuario.class.getName()).log(Level.SEVERE, null, ex);
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
                String sql = "INSERT INTO usuario (usu_cod,cat_codigo,usu_nom,"
                        + "usu_ape,usu_user,usu_pas)"
                        + " VALUES (" + txtCod.getText() + ","
                        + catedra + ",'"+ txtNom.getText() + "','" 
                        + txtape.getText() + "','" + txtusu.getText() + "','" 
                        + txtPas.getText() + "')";
                System.out.println(sql);
                con.Guardar(sql);
                ActualizarGrilla();
                botonagregar = false;
            }
            if (botonmodificar == true) {
               
                String update = "UPDATE usuario SET "
                        + "cat_codigo='" +catedra 
                        + "',usu_nom='" + txtNom.getText()
                        + "',usu_ape='" + txtape.getText()
                        + "',usu_user='" +txtusu.getText()//txtusu.getText()
                        + "', usu_pas='" + txtPas.getText()
                        + "' WHERE usu_cod=" + txtCod.getText();
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
        txtPas.setText("");
        txtPas.setEnabled(false);
        txtPas2.setText("");
        txtPas2.setEnabled(false);
        txtusu.setText("");
        txtusu.setEnabled(false);
        txtape.setText("");
        txtape.setEnabled(false);
        cbocat.setSelectedIndex(0);
        cbocat.setEnabled(false);
        cbosede.setSelectedIndex(0);
        cbosede.setEnabled(false);
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
        txtNom.setText(txtNom.getText().toUpperCase());
        txtape.setEnabled(true);
        txtape.grabFocus();
    }//GEN-LAST:event_txtNomActionPerformed

    private void txtCodActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCodActionPerformed
        
    }//GEN-LAST:event_txtCodActionPerformed

    private void txtusuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtusuActionPerformed
        if (txtusu.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "''El campo no puede estar vacio''");
            txtusu.grabFocus();
        } else {
            try {
                evento = true;
                String sql = "SELECT * FORM usuario WHERE usu_nic='" + txtusu.getText() + "'";
                //System.out.println(sql);
                rs = con.Consulta(sql);
                if (rs.next()) {
                    JOptionPane.showMessageDialog(null, "''El nombre de usuario ya existe escoje otro por favor''");
                    txtusu.setText("");
                    txtusu.grabFocus();
                } else {
                    txtPas.setEnabled(true);
                    txtPas.grabFocus();
                    txtusu.setText(txtusu.getText().toUpperCase());
                }
            } catch (SQLException ex) {
                Logger.getLogger(Usuario.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_txtusuActionPerformed

    private void txtusuKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtusuKeyPressed
    }//GEN-LAST:event_txtusuKeyPressed

    private void txtPasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPasActionPerformed
        txtPas2.setEnabled(true);
        txtPas2.grabFocus();
        PasUsu = this.txtPas.getText();
    }//GEN-LAST:event_txtPasActionPerformed

    private void txtPas2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPas2ActionPerformed
        cbocat.setEnabled(true);
        cbocat.grabFocus();
    }//GEN-LAST:event_txtPas2ActionPerformed

    private void txtPas2FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtPas2FocusLost
        if (!PasUsu.equals(txtPas2.getText())) {
            JOptionPane.showMessageDialog(null, "Los Campos no Coinciden");
            this.txtPas2.setText("");
            this.txtPas.setEnabled(true);
            this.txtPas.setText("");
            this.txtPas.grabFocus();
        }
    }//GEN-LAST:event_txtPas2FocusLost

    private void txtapeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtapeActionPerformed
        txtape.setText(txtape.getText().toUpperCase());
        txtusu.setEnabled(true);
        txtusu.grabFocus();
    }//GEN-LAST:event_txtapeActionPerformed

    private void txtusuFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtusuFocusLost
        if (evento == false) {
            if (txtusu.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "''El campo no puede estar vacio''");
                txtusu.grabFocus();
            } else {


                try {
                    String sql = "SELECT * FORM usuario WHERE usu_nic='" + txtusu.getText() + "'";
                    //System.out.println(sql);
                    rs = con.Consulta(sql);
                    if (rs.next()) {
                        JOptionPane.showMessageDialog(null, "''El nombre de usuario ya existe escoje otro por favor''");
                        txtusu.setText("");
                        txtusu.grabFocus();
                    } else {
                        txtPas.setEnabled(true);
                        txtPas.grabFocus();
                        txtusu.setText(txtusu.getText().toUpperCase());
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(Usuario.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        evento = false;
    }//GEN-LAST:event_txtusuFocusLost

    private void cbocatFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cbocatFocusLost
        if (evento2 == false) {
         
            cbosede.setEnabled(true);
            cbosede.grabFocus();
        }
        evento2 = false;

    }//GEN-LAST:event_cbocatFocusLost

    private void cbocatKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cbocatKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            evento2 = true;
            cbosede.setEnabled(true);
            cbosede.grabFocus();
      
        }
    }//GEN-LAST:event_cbocatKeyPressed

    private void cbosedeFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cbosedeFocusLost
       btnGra.setEnabled(true);btnGra.grabFocus();
        CodigoCatedra();
    }//GEN-LAST:event_cbosedeFocusLost

    private void cbosedeKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cbosedeKeyPressed
       if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
        btnGra.setEnabled(true);btnGra.grabFocus();
        CodigoCatedra();
        }
    }//GEN-LAST:event_cbosedeKeyPressed

    private void TablaUsuarioMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_TablaUsuarioMouseClicked
        buscar(String.valueOf(TablaUsuario.getValueAt(TablaUsuario.getSelectedRow(), 0)));
    }//GEN-LAST:event_TablaUsuarioMouseClicked

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
            java.util.logging.Logger.getLogger(Usuario.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Usuario.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Usuario.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Usuario.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                Usuario dialog = new Usuario(new javax.swing.JFrame(), true);
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
    private javax.swing.JTable TablaUsuario;
    public static javax.swing.JButton btnAgr;
    private javax.swing.JButton btnCan;
    private javax.swing.JButton btnGra;
    private javax.swing.JButton btnMod;
    private javax.swing.JButton btnSal;
    private javax.swing.JComboBox cbocat;
    private javax.swing.JComboBox cbosede;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField txtCod;
    private javax.swing.JTextField txtNom;
    private javax.swing.JPasswordField txtPas;
    private javax.swing.JPasswordField txtPas2;
    private javax.swing.JTextField txtape;
    private javax.swing.JFormattedTextField txtusu;
    // End of variables declaration//GEN-END:variables
}
