/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package agile.session;

import java.util.Hashtable;
import agile.control.ManageEvent;
import agile.model.dao.AnagBeanDAO;
import agile.model.dao.DocBeanDAO;
import agile.model.dao.ManageConfig;
import agile.model.dao.ManageIndex;
import agile.model.service.ManageService;
import agile.model.thread.ManageThread;
import agile.webservice.RequestService;
import agile.workflow.ManageWorkFlow;
import de.enough.polish.rmi.RemoteClient;


/**
 *
 * @author ruego
 */
public class Session {
    
    private static final int maxLinesRMS=150;    
    
    private static User   activeUser;  
    private static Hashtable listStructureObj;    
    private static Hashtable listConfig;   
    private static ManageService mngService;
    private static ManageEvent mngEvent;
    private static ManageThread mngThread;
    private static ManageIndex mngIndex;
    private static AnagBeanDAO anagBeanDAO;
    private static DocBeanDAO docBeanDAO;
    private static ManageConfig mngConfig;
    private static ManageWorkFlow mngWrkFlw;
    private static RequestService webService;
    
 
    public static User getActiveUser() {
        return activeUser;
    }

    public static void setActiveUser(User aActiveUser) {
        activeUser = aActiveUser;
    }

   

    
    public static ManageService getMngService() {
         if (mngService==null)
            mngService=new ManageService();
        return mngService;       
        
    }

    public static ManageEvent getMngEvent() {
         if (mngEvent==null)
            mngEvent=new ManageEvent();        
        return mngEvent;
    }

    public static ManageThread getMngThread() {
         if (mngThread==null)
            mngThread=new ManageThread();     
        return mngThread;
    }

   
    public static ManageIndex getMngIndex() {
        
        if (mngIndex==null)
            mngIndex=new ManageIndex();  
        
        return mngIndex;
    }
    

    public static void setMngIndex(ManageIndex aMngIndex) {
        mngIndex = aMngIndex;
    }

    public static AnagBeanDAO getAnagBeanDAO() {
        
          if (anagBeanDAO==null)
            anagBeanDAO=new AnagBeanDAO();  
        
        return anagBeanDAO;
    }

    public static void setAnagBeanDAO(AnagBeanDAO aAnagBeanDAO) {
        anagBeanDAO = aAnagBeanDAO;
    }

    public static ManageConfig getMngConfig() {
          if (mngConfig==null)
            mngConfig=new ManageConfig();  
        
        return mngConfig;
    }

    public static void setMngConfig(ManageConfig aMngConfig) {
        mngConfig = aMngConfig;
    }

    public static Hashtable getListConfig() {
         if (listConfig == null) {
            listConfig = new Hashtable();
        }
        return listConfig;
    }

    public static void setListConfig(Hashtable aListConfig) {
        listConfig = aListConfig;
    }

    public static int getMaxLinesRMS() {
        return maxLinesRMS;
    }

    public static DocBeanDAO getDocBeanDAO() {
         if (docBeanDAO==null)
            docBeanDAO=new DocBeanDAO();  
        return docBeanDAO;
    }

    public static void setDocBeanDAO(DocBeanDAO aDocBeanDAO) {
        docBeanDAO = aDocBeanDAO;
    }

    public static Hashtable getListStructureObj() {
         if (listStructureObj==null)
            listStructureObj=new Hashtable(); 
        return listStructureObj;
    }

    public static void setListStructureObj(Hashtable aListStructureObj) {
        listStructureObj = aListStructureObj;
    }

    public static ManageWorkFlow getMngWrkFlw() {
         if (mngWrkFlw==null)
            mngWrkFlw=new ManageWorkFlow();
        return mngWrkFlw;
    }

    public static void setMngWrkFlw(ManageWorkFlow aMngWrkFlw) {
        mngWrkFlw = aMngWrkFlw;
    }

    /**
     * @return the webService
     */
    public static RequestService getWebService() {
         if (webService==null){

//             ResponseService resp = Session.getMngService().doService(ManageService.GET_URL_CONNECTION_SERVICE, null);

             String url="http://commodari.homeip.net:8080/services/RequestService";

//             if (resp.getStatus()) {
//
//                 url = (String) resp.getResponseObj();
//             }


             webService=(RequestService) RemoteClient.open("agile.webservice.RequestService", url );

         }
           
        return webService;
    }

    /**
     * @param aWebService the webService to set
     */
    public static void setWebService(RequestService aWebService) {
        webService = aWebService;
    }
   
    
    
}
