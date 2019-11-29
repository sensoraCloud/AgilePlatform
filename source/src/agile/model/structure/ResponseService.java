/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package agile.model.structure;

/**
 *
 * @author ruego
 */
public class ResponseService {
    
    private boolean status;
    private Object responseObj;

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public Object getResponseObj() {
        return responseObj;
    }

    public void setResponseObj(Object responseObj) {
        this.responseObj = responseObj;
    }
    
    

}
