/*
 * es_timestamp.java
 *
 * Created on 12 settembre 2006, 15.56
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package agile.util;

import de.enough.polish.util.TextUtil;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 *
 * @author rocco
 */


public class Date_Util {
    
 	public static final int dmyHHmmss = 1;
	public static final int mdyHHmmss = 2;
	public static final int ymdHHmmss = 3;

	public static Long getStampFromDate(Date data) {

        Calendar cim_act_1 = Calendar.getInstance();
        cim_act_1.setTime(data);
        
        Calendar gt = Calendar.getInstance();
        gt.setTimeZone(TimeZone.getTimeZone("UTC"));
        
        //gt.set(cim_act_1.get(Calendar.YEAR), cim_act_1.get(Calendar.MONTH), cim_act_1.get(Calendar.DAY_OF_MONTH), cim_act_1.get(Calendar.HOUR_OF_DAY), cim_act_1.get(Calendar.MINUTE), cim_act_1.get(Calendar.SECOND));

        gt.set(Calendar.DAY_OF_MONTH, cim_act_1.get(Calendar.DAY_OF_MONTH));
        gt.set(Calendar.MONTH, cim_act_1.get(Calendar.MONTH));
        gt.set(Calendar.YEAR, cim_act_1.get(Calendar.YEAR));

        gt.set(Calendar.HOUR_OF_DAY, cim_act_1.get(Calendar.HOUR_OF_DAY));
        gt.set(Calendar.MINUTE, cim_act_1.get(Calendar.MINUTE));
        gt.set(Calendar.SECOND, cim_act_1.get(Calendar.SECOND));

        long comer = gt.getTime().getTime() / 1000;

        return new Long(comer);

    }
        
     public static Long getStamp00000FromDate(Date data) {

        Calendar cim_act_1 = Calendar.getInstance();
        cim_act_1.setTime(data);
        
        Calendar gt = Calendar.getInstance();
        gt.setTimeZone(TimeZone.getTimeZone("UTC"));
        
        //gt.set(cim_act_1.get(Calendar.YEAR), cim_act_1.get(Calendar.MONTH), cim_act_1.get(Calendar.DAY_OF_MONTH), cim_act_1.get(Calendar.HOUR_OF_DAY), cim_act_1.get(Calendar.MINUTE), cim_act_1.get(Calendar.SECOND));

        gt.set(Calendar.DAY_OF_MONTH, cim_act_1.get(Calendar.DAY_OF_MONTH));
        gt.set(Calendar.MONTH, cim_act_1.get(Calendar.MONTH));
        gt.set(Calendar.YEAR, cim_act_1.get(Calendar.YEAR));

        gt.set(Calendar.HOUR_OF_DAY, 0);
        gt.set(Calendar.MINUTE, 0);
        gt.set(Calendar.SECOND, 0);

        long comer = gt.getTime().getTime() / 1000;

        return new Long(comer);

    }

    public static Date getDatefromStamp(long stamp) {

        Calendar cim = Calendar.getInstance();
        cim.setTimeZone(TimeZone.getTimeZone("UTC"));
        cim.setTime(new Date(stamp * 1000));
        Calendar cim_act = Calendar.getInstance();

        //cim_act.set(cim.get(Calendar.YEAR),cim.get(Calendar.MONTH),cim.get(Calendar.DAY_OF_MONTH),cim.get(Calendar.HOUR_OF_DAY),cim.get(Calendar.MINUTE),cim.get(cim.SECOND));

        cim_act.set(Calendar.DAY_OF_MONTH, cim.get(Calendar.DAY_OF_MONTH));
        cim_act.set(Calendar.MONTH, cim.get(Calendar.MONTH));
        cim_act.set(Calendar.YEAR, cim.get(Calendar.YEAR));

        cim_act.set(Calendar.HOUR_OF_DAY, cim.get(Calendar.HOUR_OF_DAY));
        cim_act.set(Calendar.MINUTE, cim.get(Calendar.MINUTE));
        cim_act.set(Calendar.SECOND, cim.get(Calendar.SECOND));

        Date cramer = cim_act.getTime();
      
        
        return cramer;


    }
    
    
     static public Long getStampFromStr(String datastr,int formato){
                        
        if (datastr==null) return null;       
              
        String[] words = null;
        words = TextUtil.split(datastr, ' ');
        String strd[] = TextUtil.split(words[0], '/');
        String strd2[] = TextUtil.split(words[0], '-');

         int giorno = 0;
         int mese = 0;
         int anno = 0;
         int ora= 0;
         int minuti= 0;
         int secondi= 0;

        if (strd.length > 1) {
            if (strd[2].length() < 3) {
                switch (formato) {
                    case 1:
                        anno = Integer.parseInt(strd[2]);
                        ;
                        break;

                    case 2:
                        anno = Integer.parseInt(strd[2]);
                        ;
                        break;

                    case 3:
                        anno = Integer.parseInt(strd[0]);
                        ;
                        break;

                }
                ;

                if ((formato == 1) | (formato == 2)) {
                    if (anno < 70) {
                        strd[2] = "20" + strd[2];
                    } else {
                        strd[2] = "19" + strd[2];
                    }
                } else {
                    if (anno < 70) {
                        strd[0] = "20" + strd[0];
                    } else {
                        strd[0] = "19" + strd[0];
                    }
                }

                if (words.length < 2) {
                    datastr = strd[0] + "/" + strd[1] + "/" + strd[2];                    
                } else {
                    datastr = strd[0] + "/" + strd[1] + "/" + strd[2] + " " + words[1];
                }
            }

        } else {
            if (strd2[2].length() < 3) {
                switch (formato) {
                    case 1:
                        anno = Integer.parseInt(strd2[2]);
                        ;
                        break;

                    case 2:
                        anno = Integer.parseInt(strd2[2]);
                        ;
                        break;

                    case 3:
                        anno = Integer.parseInt(strd2[0]);
                        ;
                        break;

                }
                ;
                if ((formato == 1) | (formato == 2)) {
                    if (anno < 70) {
                        strd2[2] = "20" + strd2[2];
                    } else {
                        strd2[2] = "19" + strd2[2];
                    }
                } else {
                    if (anno < 70) {
                        strd2[0] = "20" + strd2[0];
                    } else {
                        strd2[0] = "19" + strd2[0];
                    }
                }
                
                if (words.length < 2) {
                    datastr = strd2[0] + "/" + strd2[1] + "/" + strd2[2];
                } else {
                    datastr = strd2[0] + "/" + strd2[1] + "/" + strd2[2] + " " + words[1];
                }
            }


        }

        if (words.length < 2) {
            datastr = datastr + " 00:00:00";
        } else {
      
            String[] app = TextUtil.split(datastr, ' ');
            words[1] = TextUtil.replace(app[1], ".", ":");
            datastr = app[0] + " " + words[1];
        }


     
        String[] app = null;
        String[] campi= null;
                
        switch (formato) {
            case 1:
                
                // "dd/MM/yyyy HH:mm:ss";
                
                app = TextUtil.split(datastr, ' ');
                campi= TextUtil.split(app[0], '/');
                
                giorno=Integer.parseInt(campi[0]);
                mese =Integer.parseInt(campi[1]);
                anno=Integer.parseInt(campi[2]);
                
                campi= TextUtil.split(app[1], ':');
                
                ora=Integer.parseInt(campi[0]);
                minuti=Integer.parseInt(campi[1]);
                secondi=Integer.parseInt(campi[2]);
                
                break;

            case 2:
                // "MM/dd/yyyy HH:mm:ss";
                
                app = TextUtil.split(datastr, ' ');
                campi= TextUtil.split(app[0], '/');
                
                giorno=Integer.parseInt(campi[1]);
                mese =Integer.parseInt(campi[0]);
                anno=Integer.parseInt(campi[2]);
                
                campi= TextUtil.split(app[1], ':');
                
                ora=Integer.parseInt(campi[0]);
                minuti=Integer.parseInt(campi[1]);
                secondi=Integer.parseInt(campi[2]);
                
                break;

            case 3:
                // "yyyy/MM/dd HH:mm:ss";                
                app = TextUtil.split(datastr, ' ');
                campi= TextUtil.split(app[0], '/');
                
                giorno=Integer.parseInt(campi[2]);
                mese =Integer.parseInt(campi[1]);
                anno=Integer.parseInt(campi[0]);
                
                campi= TextUtil.split(app[1], ':');
                
                ora=Integer.parseInt(campi[0]);
                minuti=Integer.parseInt(campi[1]);
                secondi=Integer.parseInt(campi[2]);
                
                break;

        }
        
          
        Calendar cal=Calendar.getInstance(); 
        cal.setTimeZone(TimeZone.getDefault());
        
        cal.set( Calendar.DAY_OF_MONTH , giorno);
        cal.set( Calendar.MONTH , mese-1);
        cal.set( Calendar.YEAR , anno);
        
        cal.set( Calendar.HOUR_OF_DAY , ora);      
        cal.set( Calendar.MINUTE , minuti);        
        cal.set( Calendar.SECOND , secondi);      
        
        Long tsTime1 = getStampFromDate(cal.getTime());
        
	return tsTime1; 
        
    }    
     
     
    public static Date getDatefromStr(String datastr, int formato) {

        if (datastr == null) {
            return null;
        }
        
        String[] words = null;
        words = TextUtil.split(datastr, ' ');
        String strd[] = TextUtil.split(words[0], '/');
        String strd2[] = TextUtil.split(words[0], '-');

        int giorno = 0;
        int mese = 0;
        int anno = 0;
        int ora = 0;
        int minuti = 0;
        int secondi = 0;

        if (strd.length > 1) {
            if (strd[2].length() < 3) {
                switch (formato) {
                    case 1:
                        anno = Integer.parseInt(strd[2]);
                        ;
                        break;

                    case 2:
                        anno = Integer.parseInt(strd[2]);
                        ;
                        break;

                    case 3:
                        anno = Integer.parseInt(strd[0]);
                        ;
                        break;

                }
                ;

                if ((formato == 1) | (formato == 2)) {
                    if (anno < 70) {
                        strd[2] = "20" + strd[2];
                    } else {
                        strd[2] = "19" + strd[2];
                    }
                } else {
                    if (anno < 70) {
                        strd[0] = "20" + strd[0];
                    } else {
                        strd[0] = "19" + strd[0];
                    }
                }

                if (words.length < 2) {
                    datastr = strd[0] + "/" + strd[1] + "/" + strd[2];
                } else {
                    datastr = strd[0] + "/" + strd[1] + "/" + strd[2] + " " + words[1];
                }
            }

        } else {
            if (strd2[2].length() < 3) {
                switch (formato) {
                    case 1:
                        anno = Integer.parseInt(strd2[2]);
                        ;
                        break;

                    case 2:
                        anno = Integer.parseInt(strd2[2]);
                        ;
                        break;

                    case 3:
                        anno = Integer.parseInt(strd2[0]);
                        ;
                        break;

                }
                ;
                if ((formato == 1) | (formato == 2)) {
                    if (anno < 70) {
                        strd2[2] = "20" + strd2[2];
                    } else {
                        strd2[2] = "19" + strd2[2];
                    }
                } else {
                    if (anno < 70) {
                        strd2[0] = "20" + strd2[0];
                    } else {
                        strd2[0] = "19" + strd2[0];
                    }
                }

                if (words.length < 2) {
                    datastr = strd2[0] + "/" + strd2[1] + "/" + strd2[2];
                } else {
                    datastr = strd2[0] + "/" + strd2[1] + "/" + strd2[2] + " " + words[1];
                }
            }


        }

        if (words.length < 2) {
            datastr = datastr + " 00:00:00";
        } else {

            String[] app = TextUtil.split(datastr, ' ');
            words[1] = TextUtil.replace(app[1], ".", ":");
            datastr = app[0] + " " + words[1];
        }



        String[] app = null;
        String[] campi = null;

        switch (formato) {
            case 1:

                // "dd/MM/yyyy HH:mm:ss";

                app = TextUtil.split(datastr, ' ');
                campi = TextUtil.split(app[0], '/');

                giorno = Integer.parseInt(campi[0]);
                mese = Integer.parseInt(campi[1]);
                anno = Integer.parseInt(campi[2]);

                campi = TextUtil.split(app[1], ':');

                ora = Integer.parseInt(campi[0]);
                minuti = Integer.parseInt(campi[1]);
                secondi = Integer.parseInt(campi[2]);

                break;

            case 2:
                // "MM/dd/yyyy HH:mm:ss";

                app = TextUtil.split(datastr, ' ');
                campi = TextUtil.split(app[0], '/');

                giorno = Integer.parseInt(campi[1]);
                mese = Integer.parseInt(campi[0]);
                anno = Integer.parseInt(campi[2]);

                campi = TextUtil.split(app[1], ':');

                ora = Integer.parseInt(campi[0]);
                minuti = Integer.parseInt(campi[1]);
                secondi = Integer.parseInt(campi[2]);

                break;

            case 3:
                // "yyyy/MM/dd HH:mm:ss";                
                app = TextUtil.split(datastr, ' ');
                campi = TextUtil.split(app[0], '/');

                giorno = Integer.parseInt(campi[2]);
                mese = Integer.parseInt(campi[1]);
                anno = Integer.parseInt(campi[0]);

                campi = TextUtil.split(app[1], ':');

                ora = Integer.parseInt(campi[0]);
                minuti = Integer.parseInt(campi[1]);
                secondi = Integer.parseInt(campi[2]);

                break;

        }


        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getDefault());
        
        cal.set(Calendar.DAY_OF_MONTH, giorno);
        cal.set(Calendar.MONTH, mese - 1);
        cal.set(Calendar.YEAR, anno);

        cal.set(Calendar.HOUR_OF_DAY, ora);
        cal.set(Calendar.MINUTE, minuti);
        cal.set(Calendar.SECOND, secondi);

        return cal.getTime();

    }
    
    
    public static String getStrfromData(Date data, int formato,boolean time) {

        String datastr = null;
        
      
        int giorno = 0;
        int mese = 0;
        int anno = 0;
        int ora = 0;
        int minuti = 0;
        int secondi = 0;

        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getDefault());
        cal.setTime(data);
        
        giorno = cal.get(Calendar.DAY_OF_MONTH);
        mese = cal.get(Calendar.MONTH)+1;
        anno = cal.get(Calendar.YEAR);

        ora = cal.get(Calendar.HOUR_OF_DAY);
        minuti = cal.get(Calendar.MINUTE);
        secondi = cal.get(Calendar.SECOND);

        String timeStr=null;
        
        switch (formato) {
            case 1:

                // "dd/MM/yyyy HH:mm:ss";

                datastr=String_Util.padLeft(String.valueOf(giorno),2,"0") + "/" + String_Util.padLeft(String.valueOf(mese),2,"0") + "/" + String.valueOf(anno) ;

                if (time){
                
                    timeStr=String_Util.padLeft(String.valueOf(ora),2,"0") + ":" + String_Util.padLeft(String.valueOf(minuti),2,"0") + ":" + String_Util.padLeft(String.valueOf(secondi),2,"0") ;
                    datastr=datastr + " " + timeStr;
                }                
                
                break;

            case 2:
                // "MM/dd/yyyy HH:mm:ss";

                datastr=String_Util.padLeft(String.valueOf(mese),2,"0") + "/" + String_Util.padLeft(String.valueOf(giorno),2,"0") + "/" + String.valueOf(anno) ;

                if (time){
                
                    timeStr=String_Util.padLeft(String.valueOf(ora),2,"0") + ":" + String_Util.padLeft(String.valueOf(minuti),2,"0") + ":" + String_Util.padLeft(String.valueOf(secondi),2,"0") ;
                    datastr=datastr + " " + timeStr;
                }       

                break;

            case 3:
                
                // "yyyy/MM/dd HH:mm:ss";                
               datastr= String.valueOf(anno) + "/" + String_Util.padLeft(String.valueOf(mese),2,"0") + "/" +  String_Util.padLeft(String.valueOf(giorno),2,"0") ;

                if (time){
                
                    timeStr=String_Util.padLeft(String.valueOf(ora),2,"0") + ":" + String_Util.padLeft(String.valueOf(minuti),2,"0") + ":" + String_Util.padLeft(String.valueOf(secondi),2,"0") ;
                    datastr=datastr + " " + timeStr;
                }       

                break;

        }


        return datastr;

    }

        

    
}
