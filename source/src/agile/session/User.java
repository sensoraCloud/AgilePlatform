/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package agile.session;

import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreNotOpenException;

/**
 *
 * @author ruego
 */
public class User {
    
    private String Username=new String();
    private String Password=new String();
    
    private String licenseNumber="1243567889";
    
    private int securityLevelRead=5;
    private int securityLevelWrite=5;
    
    private RecordStore rs;
    private String fileLogin="login";
    
    public User() {
        
        if (isset()){
            
            try {
                
                rs=RecordStore.openRecordStore(fileLogin,false);
                byte[] userByte=rs.getRecord(1);
                byte[] passByte=rs.getRecord(2);
                rs.closeRecordStore();
                
                this.Username=new String(userByte);
                this.Password=new String(passByte);
              
                //#debug info
                 System.out.println("Credenziali lette.");
                 
            } catch (RecordStoreException ex) {
                 //#debug info
                 System.out.println("Impossibile aprire il file di credenziali: "+ex.getMessage());
                // ex.printStackTrace();
            }
            
        }
        
    }
    
    public void reset(){
        try{
            RecordStore.deleteRecordStore(this.fileLogin);
             //#debug info
             System.out.println("Credenziali eliminate");
        } catch(Exception e){
            // do nothing
             //#debug error
             System.out.println("Impossibile eliminare credenziali.");
        }
    }
  
    public boolean write(String user,String pass){
        try{
            RecordStore.deleteRecordStore(this.fileLogin);
        } catch(Exception e){          
        }
        
        try {
           
            //#debug info
            System.out.println("Scrittura credenziali");
            
            this.setUsername(user);
            this.setPassword(pass);

            rs = RecordStore.openRecordStore(this.fileLogin, true);

            try {
                
                rs.addRecord(user.getBytes(), 0, user.length());
                rs.addRecord(pass.getBytes(), 0, pass.length());
                
            } catch (RecordStoreNotOpenException ex) {
                //#debug error
                System.out.println(ex.getMessage());
            } catch (RecordStoreException ex) {
                //#debug error
                System.out.println(ex.getMessage());
            }

            rs.closeRecordStore();

        } catch (Exception ex) {
            
            //#debug error
            System.out.println("Error write login: " + ex.getMessage());
          
            return false;
        }
        
        return true;
    }
    
   
    public boolean isset(){
        
        try {            
            rs=RecordStore.openRecordStore(fileLogin,false);
            rs.closeRecordStore();           
            return true;
        } catch (RecordStoreException ex) {
             //#debug info
             System.out.println("login not exist!");
            return false;
        }
       
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String Username) {
        this.Username = Username;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String Password) {
        this.Password = Password;
    }

    public int getSecurityLevelRead() {
        return securityLevelRead;
    }

    public void setSecurityLevelRead(int securityLevelRead) {
        this.securityLevelRead = securityLevelRead;
    }

    public int getSecurityLevelWrite() {
        return securityLevelWrite;
    }

    public void setSecurityLevelWrite(int securityLevelWrite) {
        this.securityLevelWrite = securityLevelWrite;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

}
