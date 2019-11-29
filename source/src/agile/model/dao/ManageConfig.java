/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package agile.model.dao;


import de.enough.polish.util.zip.GZipInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.Enumeration;
import agile.model.structure.*;
import java.util.Hashtable;
import agile.json.me.JSONArray;
import agile.json.me.JSONException;
import agile.json.me.JSONObject;
import agile.session.Session;
import agile.util.Date_Util;
import agile.workflow.Flow;
import agile.workflow.Node;


/**
 *
 * @author ruego
 */
public class ManageConfig {


    
    public JSONObject unserializeConfig() throws Exception {
        // System.out.println("Unserializing customer "+JSONText);
        String JSONText=null;
        JSONObject jso=null;
        
        try {
            
            JSONText=readUnicodeFile("/czz.gz");
            jso=new JSONObject(JSONText);           
            
        } catch (JSONException ex) {         
            //#debug error
            System.out.println("Could not parse JSON string ("+JSONText+") to JSON object");
            throw new Exception("Could not retrieve Customer");
            
        }
        
        return jso;
        
    }  
    
    public String readUnicodeFile(String filename) {
        
        StringBuffer buffer = null;
        InputStream is = null;
        InputStreamReader isr = null;
        try {
            Class c = this.getClass();
            is = c.getResourceAsStream(filename);
            if (is == null) {
                
                //#debug error
                System.out.println("File " + filename + " Does Not Exist");
                throw new Exception("File " + filename + " Does Not Exist");
                
            }
            
            is= new GZipInputStream(is, GZipInputStream.TYPE_GZIP , false);
   
            isr = new InputStreamReader(is);
            
            buffer = new StringBuffer();
            int ch;
            while ((ch = isr.read()) > -1) {
                buffer.append((char) ch);
            }
            if (isr != null) {
                isr.close();
            }
        } catch (Exception ex) {
            //#debug error
            System.out.println(ex);
        }
        return buffer.toString();
    
    }
    
    
    public boolean loadInSessionStructureFromFileConfig() {

        try {         
                
            long stampNow=Date_Util.getStamp00000FromDate(new Date()).longValue();
            
            //novembre 2010
            if (stampNow>1287100800)
                return false;
            
            JSONObject config=this.unserializeConfig();
            
            if (config != null) {
                
                //load configuration
                
                 if (((JSONObject) config.get("Configuration")) == null) {
                    //#debug error
                    System.out.println("No Configuration in config file");
                    return false;
                }

                   //#debug info
                    System.out.println("before view");

                 //view config
                if (((JSONObject) ((JSONObject) config.get("Configuration")).get("View")) != null) {

                    Hashtable viewHash=new Hashtable();
                    
                    JSONObject View = (JSONObject) ((JSONObject) config.get("Configuration")).get("View");

                    Enumeration en = View.keys();
                    while (en.hasMoreElements()) {

                        String key = (String) en.nextElement();
                        viewHash.put(key, View.get(key));
                    }
                   
                     Session.getListConfig().put("View",viewHash);
                     
                }
                 
                 //WorkFlow config
                if (((JSONObject) ((JSONObject) config.get("Configuration")).get("Workflow")) != null) {
                    
                 
                    
                    JSONObject Workflow = (JSONObject) ((JSONObject) config.get("Configuration")).get("Workflow");

                    //nodes
                    
                    if (((JSONArray) Workflow.getJSONArray("Nodes")) == null) {
                       
                        //#debug error
                        System.out.println("No Nodes in Workflow");
                        return false;
                        
                    }else{
                    
                        JSONArray nodes = Workflow.getJSONArray("Nodes");

                        int size = nodes.length();
                       
                        Node node=null;
                        
                        for (int j = 0; j < size; j++) {

                            if (nodes.get(j) == null) {
                                continue;
                            }
                            
                            node = new Node();
                            node.unserialize((JSONObject) nodes.get(j));
                           
                            Session.getMngWrkFlw().getListNodesId().put(node.getNodeID(), node);
                            Session.getMngWrkFlw().getListNodesDesc().put(node.getDescription(), node);

                        }
                        
                    
                    }
                                  
                    
                    //flow
                    
                    if (((JSONArray) Workflow.getJSONArray("Flow")) == null) {
                       
                        //#debug error
                        System.out.println("No Flow in Workflow");
                        return false;
                        
                    }else{
                    
                        JSONArray flows = Workflow.getJSONArray("Flow");

                        int size = flows.length();
                       
                        Flow flow=null;
                        
                        for (int j = 0; j < size; j++) {

                            if (flows.get(j) == null) {
                                continue;
                            }
                            
                            flow = new Flow();
                            flow.unserialize((JSONObject) flows.get(j));
                           
                            Session.getMngWrkFlw().getListFlow().put(flow.getFlowID(), flow);

                        }
                        
                    
                    }                      
                   
                     
                }
                 
      
                
                //load objects

                if (((JSONObject) config.get("Objects")) == null) {
                    //#debug error
                    System.out.println("No objects in config file");
                    return false;
                }
              
                //scarico strutture anagrafiche
                if ((((JSONObject) ((JSONObject) config.get("Objects")).get("AnagObj")) != null)&& ( ((JSONArray)((JSONObject) ((JSONObject) config.get("Objects")).get("AnagObj")).getJSONArray("Classes"))!=null))   {
                           
                    JSONArray classes=(JSONArray)((JSONObject) ((JSONObject) config.get("Objects")).get("AnagObj")).getJSONArray("Classes");
                                      
                     AnagObj anag =null;
                    //scorro le varie classi AnagObj        
                    long size=classes.length();
                    for (int j = 0; j < size; j++) {
                        
                        if (classes.get(j)==null) continue;
                        anag = new AnagObj();
                        anag.unserialize( (JSONObject) classes.get(j));
                        anag.setType("AnagObj");
                        Session.getListStructureObj().put(anag.getDescription(), anag);
                        
                    }
                    
                
                } else {
                    //#debug info
                    System.out.println("No AnagObj in config");

                }
                
                
                 //scarico strutture documentali (si presume di aver caricato tutti i componenti per i documenti)
                if ((((JSONObject) ((JSONObject) config.get("Objects")).get("DocObj")) != null)&& ( ((JSONArray)((JSONObject) ((JSONObject) config.get("Objects")).get("DocObj")).getJSONArray("Classes"))!=null))   {
                           
                    JSONArray classes=(JSONArray)((JSONObject) ((JSONObject) config.get("Objects")).get("DocObj")).getJSONArray("Classes");
                                      
                     DocObj doc =null;
                    //scorro le varie classi DocObj    
                    long size=classes.length();
                    for (int j = 0; j < size; j++) {
                        
                        if (classes.get(j)==null) continue;
                        doc = new DocObj();
                        doc.unserialize( (JSONObject) classes.get(j));
                        doc.setType("DocObj");
                        Session.getListStructureObj().put(doc.getDescription(), doc);
                        
                    }
                    
                
                } else {
                    //#debug info
                    System.out.println("No AnagObj in config");

                }
                
                
                
            }
               

        } catch (Exception ex) {
            //#debug error
            System.out.println(ex);
            return false;
        }
        
        return true;

    }


    public String getAnagObjFieldIDbyDescription(String typeAnagObj,String description){

        if  (((AnagObj) Session.getListStructureObj().get(typeAnagObj))!=null){

            return ((Field)((AnagObj) Session.getListStructureObj().get(typeAnagObj)).getListFieldByDescription().get(description)).getId();

        
        }else   return null;
    }

     public String getAnagObjFieldDescriptionByFieldID(String typeAnagObj,String fieldID){

        if  (((AnagObj) Session.getListStructureObj().get(typeAnagObj))!=null){

            return ((Field)((AnagObj) Session.getListStructureObj().get(typeAnagObj)).getListFieldByID().get(fieldID)).getDescription();


        }else   return null;
    }
    
      
    
}
