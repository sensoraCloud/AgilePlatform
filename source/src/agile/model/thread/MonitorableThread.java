/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package agile.model.thread;

import de.enough.polish.util.Locale;
import java.util.Hashtable;
import agile.control.ManageViewNavigation;

/**
 *
 * @author ruego
 */
public class MonitorableThread implements Runnable {
    
    private String title;
    private Integer threadID;  
    private Thread thread;      //thread in which to run task. task is in progress if thread != null
    private boolean paused, cancelled,terminate;   //indicates task has been paused or cancelled by client and not yet restarted
    private boolean failed;  //indicates whether progress is indeterminate or if task is doomed to fail or failed
    private int percentage = -1;
    private Hashtable params;
    private String typeGauge;
    
    /** Creates a new instance of SimpleTask */
    public MonitorableThread(Integer threadID,String title) {
        
        if (threadID == null) {
            throw new NullPointerException();
        }
        
        setTitle(title);
        setPercentage(-1);
        
        this.threadID = threadID;
             
    }
         
    public Integer getThreadID() {
        return threadID;
    }
    
      
    public int getPercentage() {
        return percentage;
    }
    
    public boolean isFailed() {
        return failed;
    }
    
        public int getStatus() {
        
        int percentageSnapshot = percentage;
        
        if (failed) {
            return -1;
        }
        else if (isCancelled()) {
            return 4;
        }
        else if (isPaused()) {
            return 2;
        }
        else if (percentageSnapshot >= 100) {
            return 5;
        }
        else if (thread == null) {
            return 0;
        }
        else {
            return 1;
        }
    }
    
   public synchronized String getPercentageText() {
       
        StringBuffer sb = new StringBuffer();
        int percentageSnapshot = percentage;
        
        //sb.append(percentageSnapshot);
       // sb.append("%<br />");
       
        if (failed) {
            sb.append(Locale.get("message.failed"));
        }
        else if (isCancelled()) {
            sb.append(Locale.get("message.cancelled"));
        }
        else if (isPaused()) {
            sb.append(Locale.get("message.paused"));
        }
        else if (percentageSnapshot >= 100) {
            sb.append(Locale.get("message.completed"));
        }
        else if (percentageSnapshot == -1) {
            sb.append(Locale.get("message.noTimeRemaining"));
        }
        else if (thread == null) {
            sb.append(Locale.get("message.nostarted"));
        }
        else {
            sb.append(Locale.get("message.progress"));
        }
        return sb.toString();
    }
    
   
   
    // public synchronized void start(CachedRowSetDataProvider nodeDataProvider,javax.sql.rowset.CachedRowSet SintesiggRowSet) {
    public synchronized void start(Hashtable params,String typeGauge) {

       setPercentage(-1);
        
       setTypeGauge(typeGauge);
       
        try {
            setParams(params);
        } catch (Exception ex) {

            setFailed(true);
        }

        //we're about to (re)start the task, so set pause and cancelled to false
        setPaused(false);
        setCancelled(false);
        setFailed(false);
        setTerminate(false);

      
        setThread(new Thread(this));        
        
         //make gauge
        if (getTypeGauge()!=null) 
            ManageViewNavigation.getPrgsThread().prepareView(getThreadID().intValue(),typeGauge);
         
        thread.start();        


    }
    
    // public synchronized void start(CachedRowSetDataProvider nodeDataProvider,javax.sql.rowset.CachedRowSet SintesiggRowSet) {
    public synchronized void startAgain() {
      
        //we're about to (re)start the task, so set pause and cancelled to false
        setPaused(false);
        setCancelled(false);
        setFailed(false);
        setTerminate(false);

        setPercentage(0);

        setThread(new Thread(this)); 
        
        //make gauge
        if (getTypeGauge()!=null)        
          ManageViewNavigation.getPrgsThread().prepareView(getThreadID().intValue(),null);
        
        thread.start();
       

    }
    
    public synchronized void pause() {
        setPaused(true);
    }
    
     public synchronized void reStart() {
        setPaused(false);
    }
    
    public synchronized void cancel() {
        
//        if (thread!=null){
//            thread.interrupt();
//            setThread(null);
//        }        
       
        setCancelled(true);
    }
   
    public Hashtable getParams() {
        return params;
    }

    public void setParams(Hashtable params) {
        this.params = params;
    }
    
    public void progress(int total, int current){
        setPercentage((int) (100 * ((float)current/(float)total)));       
    }

 
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isPaused() {
        return paused;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public synchronized void setPaused(boolean paused) {
        
        if (paused==false){
            try {
            notifyAll();
        } catch (Exception e) {           

        }
        }
        this.paused = paused;
    }
    
    public synchronized void waitUntilAliveIs() {

        try {
            
            wait();       
           
        } catch (Exception e) {           

        }

    }

    public void setCancelled(boolean cancelled) {
        
        this.cancelled = cancelled;
        try {
            notifyAll();
        } catch (Exception e) {           

        }
        
    }

    public void setFailed(boolean failed) {
        
        this.failed = failed;
        try {
            notifyAll();
        } catch (Exception e) {           

        }
    }

    public void setPercentage(int percentage) {
        this.percentage = percentage;
    }

    public void setThread(Thread thread) {
        this.thread = thread;
    }
    
    public void destroyThread() {

        this.setCancelled(true);

        while (!isTerminate()) {
            try {
                wait(200);
            } catch (Exception e) {
            }
        }

        this.thread = null;

    }

    
    public void run() {
      
   
    }
    
    public void onComplete() {
      
   
    }

    public String getTypeGauge() {
        return typeGauge;
    }

    public void setTypeGauge(String typeGauge) {
        this.typeGauge = typeGauge;
    }

    public boolean isTerminate() {
        return terminate;
    }

    public void setTerminate(boolean terminate) {
        this.terminate = terminate;
    }

   
    

}
