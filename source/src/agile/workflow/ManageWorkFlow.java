/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package agile.workflow;

import de.enough.polish.util.Locale;
import java.util.Hashtable;
import java.util.Vector;
import agile.model.structure.DocObj;
import agile.model.structure.Property;
import agile.session.Session;

/**
 *
 * @author ruego
 */
public class ManageWorkFlow {
    
    private  Hashtable listNodesId; //key: id value: node
    private  Hashtable listNodesDesc; //key: desc value: node
    private  Hashtable listFlow;
    
    //list activity
    public static final int SEND_TO_SERVER_ACTIVITY=1;
    
    
    public String getActivityIDDesc(int activityID){
    
        switch (activityID){
        
            case SEND_TO_SERVER_ACTIVITY: return Locale.get("SEND_TO_SERVER_ACTIVITY");
                      
        
        }
        
        return null;
    
    }
    
    public Integer getFlowID(String typeObj) {

       return ((DocObj) Session.getListStructureObj().get(typeObj)).getFlowID();
       
    }
    
    public String getDescStartNodeID(String typeObj) {

        Flow fl = (Flow) getListFlow().get(getFlowID(typeObj));

        if (fl != null) {

            Vector prop = fl.getPropertyByKey("startNodeID");

            if (prop.size() == 1) {

                Integer startNodeID = (Integer)((Property) prop.elementAt(0)).getValue();

                if (startNodeID != null) {
                    return ((Node) getListNodesId().get(startNodeID)).getDescription();
                } else {
                    return null;
                }
            } else {
                return null;
            }

        } else {
            return null;
        }
    }
    
    
    public  Vector getNextDescNodes(String typeObj,String fromNodeDesc){
    
        Flow fl = (Flow) getListFlow().get(getFlowID(typeObj));
        Integer fromNodeID = ((Node) getListNodesDesc().get(fromNodeDesc)).getNodeID();

        //loop on roles and take high priority role for fromNodeDesc
        int size = fl.getRoles().size();
        Role role = null;
        Vector nextNodesDesc = null; 
        
        for (int r = 0; r < size; r++) {

            role = (Role) fl.getRoles().elementAt(r);

            if (role.getFromNodeID().intValue() == fromNodeID.intValue()) {

                nextNodesDesc.addElement(((Node) this.getListNodesId().get(role.getToNodeID())).getDescription());

            }

        }

        
        return nextNodesDesc;
       
    }
    
  
    
    public String getNextHighPriorityDescNode(String typeObj, String fromNodeDesc) {

        Flow fl = (Flow) getListFlow().get(getFlowID(typeObj));
        Integer fromNodeID = ((Node) getListNodesDesc().get(fromNodeDesc)).getNodeID();

        //loop on roles and take high priority role for fromNodeDesc
        int size = fl.getRoles().size();
        Role role = null;
        Role candiateRole = null;
        for (int r = 0; r < size; r++) {

            role = (Role) fl.getRoles().elementAt(r);

            if (role.getFromNodeID().intValue() == fromNodeID.intValue()) {

                if (candiateRole == null) {

                    candiateRole = role;

                } else {

                    if (candiateRole.getPriority().intValue() <= role.getPriority().intValue()) {

                        candiateRole = role;
                    }

                }

            }

        }

        if (candiateRole != null) {
            return ((Node) this.getListNodesId().get(candiateRole.getToNodeID())).getDescription();
        } else {
            return null;
        }
    }

    
     public Integer getActivityIDNextHighPriority(String typeObj, String fromNodeDesc) {

        Flow fl = (Flow) getListFlow().get(getFlowID(typeObj));
        Integer fromNodeID = ((Node) getListNodesDesc().get(fromNodeDesc)).getNodeID();

        //loop on roles and take high priority role for fromNodeDesc
        int size = fl.getRoles().size();
        Role role = null;
        Role candiateRole = null;
        for (int r = 0; r < size; r++) {

            role = (Role) fl.getRoles().elementAt(r);

            if (role.getFromNodeID().intValue() == fromNodeID.intValue()) {

                if (candiateRole == null) {

                    candiateRole = role;

                } else {

                    if (candiateRole.getPriority().intValue() <= role.getPriority().intValue()) {

                        candiateRole = role;
                    }

                }

            }

        }

        if (candiateRole != null) {
            return candiateRole.getActivity();
        } else {
            return null;
        }
    }

    public Hashtable getListNodesFlowID(Integer flowID) {

        Hashtable listNodes = new Hashtable();

        Flow fl = (Flow) getListFlow().get(flowID);

        //loop on roles
        int size = fl.getRoles().size();

        Role role = null;

        for (int r = 0; r < size; r++) {

            role = (Role) fl.getRoles().elementAt(r);

            if (!listNodes.containsKey(((Node)getListNodesId().get(role.getFromNodeID())).getDescription())) {

                listNodes.put(  ((Node)getListNodesId().get(role.getFromNodeID())).getDescription() , getListNodesId().get(role.getFromNodeID()));
            }

            if (!listNodes.containsKey(((Node)getListNodesId().get(role.getToNodeID())).getDescription())) {

                listNodes.put(((Node)getListNodesId().get(role.getToNodeID())).getDescription() , getListNodesId().get(role.getToNodeID()));
            }


        }

        return listNodes;
    }


    public  Hashtable getListFlow() {
         if (listFlow==null)
            listFlow=new Hashtable();
        return listFlow;
    }

    public  void setListFlow(Hashtable listFlow) {
        this.listFlow = listFlow;
    }

    public Hashtable getListNodesId() {
         if (listNodesId==null)
            listNodesId=new Hashtable();
        return listNodesId;
    }

    public void setListNodesId(Hashtable listNodesIdDesc) {
        this.listNodesId = listNodesIdDesc;
    }

    public Hashtable getListNodesDesc() {
         if (listNodesDesc==null)
            listNodesDesc=new Hashtable();
        return listNodesDesc;
    }

    public void setListNodesDesc(Hashtable listNodesDescID) {
        this.listNodesDesc = listNodesDescID;
    }
    
    

}
