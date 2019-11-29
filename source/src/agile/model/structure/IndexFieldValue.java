/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package agile.model.structure;

/**
 *
 * @author ruego
 */
public class IndexFieldValue {
    
    private String fieldID;
    private Object fieldValue;
    private String typeUI;

    public String getFieldID() {
        return fieldID;
    }

    public void setFieldID(String fieldName) {
        this.fieldID = fieldName;
    }

    public Object getFieldValue() {
        return fieldValue;
    }

    public Object getFieldValueString() {

        String value=null;

        if (fieldValue instanceof String) {
            value = (String) fieldValue;
        } else if (fieldValue instanceof Integer) {
            value = String.valueOf(((Integer) fieldValue).intValue());
        } else if (fieldValue instanceof Double) {
            value = String.valueOf(((Double) fieldValue).doubleValue());
        } else if (fieldValue instanceof Long) {
            value = String.valueOf(((Long) fieldValue).longValue());
        } else if (fieldValue instanceof Boolean) {
            value = String.valueOf(((Boolean) fieldValue).booleanValue());
        }

        return value;
    }

    public void setFieldValue(Object fieldValue) {
        this.fieldValue = fieldValue;
    }

    public String getTypeUI() {
        return typeUI;
    }

    public void setTypeUI(String typeUI) {
        this.typeUI = typeUI;
    }

}
