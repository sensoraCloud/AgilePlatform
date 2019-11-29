/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package agile.model.structure;

import java.util.Vector;

/**
 *
 * @author ruego
 */
public class PaginatedGroupValues {
     
    private int numPages;
    private Vector pagesGroupValue;    
    private String field;
    private String typeObj;

    public PaginatedGroupValues(int numPages,Vector pagesGroupValue, String typeObj,String field) {

        setNumPages(numPages);
        setPagesGroupValue(pagesGroupValue);       
        setTypeObj(typeObj);
        setField(field);
       
    }

   

    public int getNumPages() {
        return numPages;
    }

    public void setNumPages(int numPages) {
        this.numPages = numPages;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

   
    public String getTypeObj() {
        return typeObj;
    }

    public void setTypeObj(String typeObj) {
        this.typeObj = typeObj;
    }

    public Vector getPagesGroupValue() {
        return pagesGroupValue;
    }

    public void setPagesGroupValue(Vector pagesGroupValue) {
        this.pagesGroupValue = pagesGroupValue;
    }

   
    

}
