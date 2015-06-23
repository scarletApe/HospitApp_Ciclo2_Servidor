package multixsoft.hospitapp.receiver;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import multixsoft.hospitapp.utilities.AESCipher;
import multixsoft.hospitapp.webservice.AdapterRest;
import org.json.simple.JSONObject;

/**
 * REST Web Service
 * @author Ivan Tovar
 * @version 1.0
 * @date 12/May/2015
 */
@Path("privacycontrol")
public class PrivacyControl {

    @Context
    private UriInfo context;
    private AdapterRest adapter;
    private byte [] key = "instutomexsegsoc".getBytes();

    public PrivacyControl() {
        adapter = new AdapterRest();
    }
    @GET
    @Path("accessaspatient")
    @Produces("text/plain")
    public int accessAsPatient( @QueryParam("nss") String nss, 
            @QueryParam("password") byte [] password) {
        if(!isValid(nss)){
            return -1;
        }
        if(!isValid(new String(decrypt(password, key)))) {
            return -1;
        }        
        
        JSONObject jObj = (JSONObject)adapter.get("patient/"+nss);
        
        if(jObj.isEmpty()) {
            return -1;
        }
        if(!jObj.get("password").equals(password)){
            return -1;
        }
        return 1;
    }
    
    @GET
    @Path("accessasadmindoctor")
    @Produces("text/plain")
    public int accessAsAdminDoctor(@QueryParam("username") String username, 
            @QueryParam("password") byte [] password) {
        if(!isValid(username)){
            return -1;
        }
        
        if(!isValid(new String(decrypt(password, key)))) {
            return -1;
        }
        JSONObject doctor = (JSONObject)adapter.get("doctor/"+username);
        JSONObject admin = (JSONObject)adapter.get("admin/"+username);
        if(doctor==null || doctor.isEmpty()) {
            if(admin == null || admin.isEmpty()) {
                return -1;
            }
            else if(admin.get("password").equals(password)){
                return 2;
            }
        }
        else if(doctor.get("password").equals(password)){
            return 1;
        }
        return -1;
    }
    
    public byte [] encrypt(byte [] text, byte [] key) {
        try {
            return AESCipher.encrypt(key, key, text);
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException | NoSuchPaddingException | 
                InvalidKeyException | InvalidAlgorithmParameterException | IllegalBlockSizeException | 
                BadPaddingException ex) {
            System.err.println("Error durante el encriptado");
            Logger.getLogger(PrivacyControl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public byte [] decrypt(byte [] text, byte [] key) {
        try {
            return AESCipher.decrypt(key, key, text);
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException | NoSuchPaddingException | 
                InvalidKeyException | InvalidAlgorithmParameterException | IllegalBlockSizeException | 
                BadPaddingException ex) {
            Logger.getLogger(PrivacyControl.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println("Error durante el desencriptado");
        }
        return null;
    }
    
    private boolean isValid(String string) {
        if(string.contains(";")) {
            return false;
        }
        
        if(string.contains("--")) {
            return false;
        }
        
        if(string.contains("/*")) {
            return false;
        }
        
        if(string.contains("==")) {
            return false;
        }
        return true;
    }
}
