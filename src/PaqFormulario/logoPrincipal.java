/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * logoPrincipal.java
 *
 * Created on 28-jun-2010, 20:19:32
 */

package PaqFormulario;

import java.awt.Graphics;
import javax.swing.ImageIcon;

/**
 *
 * @author Danny Veron
 */
public class logoPrincipal extends javax.swing.JPanel {
    ImageIcon image;
    /** Creates new form logoPrincipal */
    public logoPrincipal() {
        initComponents();
        image=new ImageIcon(getClass().getResource("/paqImagenes/LogoMcastaña.png"));
        setSize(image.getIconWidth(),image.getIconHeight());
    
    }


    @Override
    protected void paintComponent(Graphics g) {
        
        g.drawImage(image.getImage(), 0, 0,this);
        setOpaque(false);
        super.paintComponent(g);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        setMaximumSize(new java.awt.Dimension(960, 600));
        setMinimumSize(new java.awt.Dimension(960, 600));
        setPreferredSize(new java.awt.Dimension(960, 600));
        setLayout(new java.awt.CardLayout());
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

}
