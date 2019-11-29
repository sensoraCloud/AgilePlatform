/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package agile.view;

import javax.microedition.lcdui.Displayable;


/**
 *
 * @author ruego
 */
public interface Viewable  {

    
    public void showView();//permit to view different objet than (this) Form Example: some View must viewable TabbedPanel that it can't append to form
    public String getTitle();//control the master form title ticker
    public String getTickerMex();//control the master form ticker
    public void setTicker(String mex);//control the master form ticker
    
}
