/*
 * es_pad.java
 *
 * Created on 12 marzo 2008, 10.28
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package agile.util;

/**
 *
 * @author rocco
 */
public class String_Util {
    
    /** Creates a new instance of es_pad */
    public String_Util() {
    }
    
      public static String padLeft(String stringa,int numElms,String elmDaIns)
	
	{		
          String newtext=stringa;
          
          int diff=numElms-stringa.length();
                    
          if (diff>0){
              
              newtext="";
              
              for (int i=0;i<diff;i++){
                  
                  newtext=newtext+elmDaIns;
                  
              }
              
              newtext=newtext+stringa;
              
          }
          
          return newtext;
      }
      
       public static String padRight(String stringa,int numElms,String elmDaIns)
	
	{		
          String newtext=stringa;
          
          int diff=numElms-stringa.length();
                    
          if (diff>0){
              
              newtext="";
              
              for (int i=0;i<diff;i++){
                  
                  newtext=newtext+elmDaIns;
                  
              }
              
              newtext=stringa+newtext;
              
          }
          
          return newtext;
      }
       
     public static String getStrDouble2Dec(double numero) {

        //manca approssimazione del numero
        String str1 = null;

        int numNoVirgola = (int) ((numero + (double) 0.00001) * 100);
        int parteIntera = (int) numero;
        int parteDec = numNoVirgola - (parteIntera * 100);

        str1 = "00" + String.valueOf(parteDec);
        str1 = str1.substring(str1.length() - 2, str1.length());
        str1 = String.valueOf(parteIntera) + "," + str1;

        return str1;

    }

}
