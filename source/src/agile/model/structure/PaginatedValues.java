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
public class PaginatedValues {
     
    private int numPages;
    private Vector pagesValue;
    private Vector pagesSerializedObjList;
    private String field;
    private Object value;
    private String typeObj;

    public PaginatedValues(int numPages,Vector pagesValue, Vector serializedObjList,String typeObj,String field,Object value) {

        setNumPages(numPages);
        setPagesValue(pagesValue);
        setPagesSerializedObjList(serializedObjList);
        setTypeObj(typeObj);
        setField(field);
        setValue(value); 
    }

    public Vector getPagesValue() {
        return pagesValue;
    }

    public void setPagesValue(Vector pagesValue) {
        this.pagesValue = pagesValue;
    }

    public Vector getPagesSerializedObjList() {
        return pagesSerializedObjList;
    }

    public void setPagesSerializedObjList(Vector pagesSerializedObjList) {
        this.pagesSerializedObjList = pagesSerializedObjList;
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

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getTypeObj() {
        return typeObj;
    }

    public void setTypeObj(String typeObj) {
        this.typeObj = typeObj;
    }

   
    

}
