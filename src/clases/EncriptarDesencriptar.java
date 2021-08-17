/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package clases;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author Daniverm
 */
public class EncriptarDesencriptar {
    
    static public byte[] encriptar(String textoPlano) {
        byte[] textoencriptado = null;
        try {
            String claveOriginal = textoPlano;
            String semilla = "jesus33maria13";
            // Generamos una clave secreta.
            SecretKeySpec desKey = new SecretKeySpec(new String((semilla.trim().concat("99999999")).substring(0, 8)).getBytes(), "DES");
            Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, desKey);
            textoencriptado = cipher.doFinal(claveOriginal.getBytes());
            //textoencriptado= new String(textocifrado, "UTF8");
        } catch (IllegalBlockSizeException ex) {
            Logger.getLogger(EncriptarDesencriptar.class.getName()).log(Level.SEVERE, null, ex);
        } catch (BadPaddingException ex) {
            Logger.getLogger(EncriptarDesencriptar.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidKeyException ex) {
            Logger.getLogger(EncriptarDesencriptar.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(EncriptarDesencriptar.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchPaddingException ex) {
            Logger.getLogger(EncriptarDesencriptar.class.getName()).log(Level.SEVERE, null, ex);
        }
        return textoencriptado;
    }

    static public String desencriptar(byte[] textoPlano) {
        String texto = null;
        try {

            String semilla = "jesus33maria13";
            SecretKeySpec desKey = new SecretKeySpec(new String((semilla.trim().concat("99999999")).substring(0, 8)).getBytes(), "DES");
            Cipher cifrador = Cipher.getInstance("DES/ECB/PKCS5Padding");
            cifrador.init(Cipher.DECRYPT_MODE, desKey);
            byte[] textodesencriptado = cifrador.doFinal(textoPlano);
            texto = (new String(textodesencriptado, "UTF8"));
        } catch (IllegalBlockSizeException ex) {
            Logger.getLogger(EncriptarDesencriptar.class.getName()).log(Level.SEVERE, null, ex);
        } catch (BadPaddingException ex) {
            Logger.getLogger(EncriptarDesencriptar.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(EncriptarDesencriptar.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchPaddingException ex) {
            Logger.getLogger(EncriptarDesencriptar.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(EncriptarDesencriptar.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidKeyException ex) {
            Logger.getLogger(EncriptarDesencriptar.class.getName()).log(Level.SEVERE, null, ex);
        }
        return texto;
    }
    
//    public static void main (String args[]) throws Exception{
//        EncriptarDesencriptar e=new EncriptarDesencriptar();
//        System.out.println("Texto a encriptar= dani veron");
//        byte[] textoEncriptado= e.encriptar("dani veron");
//        System.out.println("Texto encriptado= "+textoEncriptado);
//        System.out.println("Texto Plano= "+e.desencriptar(textoEncriptado));
//    }
    
}
