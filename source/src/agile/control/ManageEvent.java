/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package agile.control;

import de.enough.polish.util.Locale;
import java.util.Hashtable;
import agile.model.service.ManageService;
import agile.model.structure.AnagBean;
import agile.model.structure.AnagObj;
import agile.model.structure.DocBean;
import agile.model.structure.DocObj;
import agile.model.structure.PaginatedGroupValues;
import agile.model.structure.PaginatedValues;
import agile.model.structure.Structure;
import agile.session.Session;
import agile.view.AdmAnagBeanObjForm;
import agile.view.AdmDocDetailForm;
import agile.view.AdminConnectionForm;
import agile.view.AdminDataForm;
import agile.view.GroupValueObjForm;
import agile.view.HelpForm;
import agile.view.LoginForm;
import agile.view.MainForm;
import agile.view.PageListForm;
import agile.view.SearchForm;
import agile.view.Viewable;

/**
 *
 * @author ruego
 */
public class ManageEvent {
      
    //list EventID     
    public static final int GOBACK_EVENT=1;
    public static final int GOHOME_EVENT=2;  
    public static final int HELP_EVENT=3; 
    public static final int MAIN_MENU_SELECT=4;
    public static final int VIEW_ANAGBEANFORM_EVENT=5;
    public static final int VIEW_DOCBEANFORM_EVENT=6;   
    public static final int VIEW_PAGELISTOBJS_RESULT_EVENT=8;
    public static final int REBUILD_PAGELISTOBJS_RESULT_EVENT=9;  
    public static final int VIEW_GROUPVALUEOBJ_RESULT_EVENT=10;
    public static final int REBUILD_GROUPVALUEOBJ_RESULT_EVENT=11;
    public static final int REBUILD_SEARCH_RESULT_EVENT=12;
    public static final int VIEW_PROGRESS_EVENT=13;
    
    public static final int REBUILD_DOC_UPDATE_HEAD_EVENT=14;
    public static final int REBUILD_DOC_NEW_ROW_EVENT=15;
    public static final int REBUILD_DOC_UPDATE_ROW_EVENT=16;
    public static final int REBUILD_DOC_DELETE_ROW_EVENT=17;
    
    public static final int VIEW_CART=18;
    public static final int VIEW_ANAGBEANFORM_CART_EVENT=19;
    public static final int REBUILD_GOBACK_PAGELISTVALUE_EVENT=20;
    public static final int REBUILD_GOBACK_SEARCHVALUE_EVENT=21;
    
    public static final int VIEW_FINDDOC_EVENT=22;
    public static final int VIEW_SENT_OBJS_EVENT=23;
    
    public static final int VIEW_UPDATE_LOGIN_EVENT=24;
    public static final int VIEW_ADMIN_CONNECTION_EVENT=25;
    public static final int VIEW_ADMIN_DATA_EVENT=26;
    
    public static final int REBUILD_ADMDATA_EVENT=27;
    public static final int GOBACK_THREAD_EVENT=28;
    
    
    
    public void handleEvent(int eventID, Event e) {

        try {


            Hashtable params = null;

            switch (eventID) {
                
                case GOBACK_EVENT:

                    ManageViewNavigation.goBack();

                    break;
                    
                case GOBACK_THREAD_EVENT:

                    Thread bk = new Thread(new Runnable() {

                        public void run() {
                            ManageViewNavigation.goBack();
                        }
                    });

                    bk.start();

                    break;
                    
                case GOHOME_EVENT:

                    ManageViewNavigation.goHome();

                    break;
                    
                case HELP_EVENT:
             
                    ManageViewNavigation.goNext((Viewable) (new HelpForm()).prepareView((String)e.getByName("helpFile")));
                    
                    break;
                    
                case VIEW_PROGRESS_EVENT:

                    ManageViewNavigation.goNext((Viewable) ManageViewNavigation.getPrgsThread() );
           
                    break;
                     
                case MAIN_MENU_SELECT:
                    
                     //#debug info                
                    System.out.println("1-Evento SELEZIONATO Main Menu Option!");

                    //anagrafic
                    if ((e.getByName("typeObjSelText") != null) && (((String) e.getByName("typeObjSelText")).equals("AnagObj"))) {

                        //new
                        if ((e.getByName("comandoText") != null) && (((String) e.getByName("comandoText")).equals(Locale.get("mainmenuform.menu.newelement")))) {
                           
                            ManageViewNavigation.goNext((Viewable)  (new AdmAnagBeanObjForm()).prepareView(MainForm.TYPEFORM_NEW, new AnagBean((String)e.getByName("objSelText")),null,null));
           
                        }

                        //search
                         if ((e.getByName("comandoText") != null) && (((String) e.getByName("comandoText")).equals(Locale.get("mainmenuform.menu.search")))) {
                             
                             ManageViewNavigation.goNext((Viewable) (new SearchForm()).prepareView(MainForm.TYPEFORM_SEARCH_STANDARD, (String) e.getByName("objSelText"), null,null, null, null,null, 0)  );
                
                        }
                        
                        //list
                         if ((e.getByName("comandoText") != null) && (((String) e.getByName("comandoText")).equals(Locale.get("mainmenuform.menu.list")))) {
          
                            params = new Hashtable(3);
                            params.put("typeObj",  (String) e.getByName("objSelText")  );
                            params.put("searchField", (String)((AnagObj) Session.getListStructureObj().get((String) e.getByName("objSelText") )).getDefaultGroupIndex() );                          
                            params.put("structureObjClass", (AnagObj) Session.getListStructureObj().get((String) e.getByName("objSelText")));
                            params.put("typeSourceEvent", "View_new_groupValueObj" );                          
                            
                            Session.getMngService().doService(ManageService.SEARCH_GROUPVALUE_SERVICE, params);                
                
                           
                        }
                        
                         //workflow
                         if ((e.getByName("comandoText") != null) && (((String) e.getByName("comandoText")).equals(Locale.get("mainmenuform.menu.workflow")))) {

                             
                             ManageViewNavigation.goNext((Viewable) (new SearchForm()).prepareView(MainForm.TYPEFORM_SEARCH_WORKFLOW, (String) e.getByName("objSelText"), null,null, null, null,null, 0) );

                
                        }
                        

                    } else//document 
                    if ((e.getByName("typeObjSelText") != null) && (((String) e.getByName("typeObjSelText")).equals("DocObj"))) {

                        
                          //new
                        if ((e.getByName("comandoText") != null) && (((String) e.getByName("comandoText")).equals(Locale.get("mainmenuform.menu.newelement")))) {
          
                            ManageViewNavigation.goNext((Viewable) (new SearchForm()).prepareView(MainForm.TYPEFORM_SEARCH_NEWDOC, null,null, null, (String) e.getByName("objSelText"), null,null, 0));                           
                            
                        }                       
                        
                        

                        //search
                        if ((e.getByName("comandoText") != null) && (((String) e.getByName("comandoText")).equals(Locale.get("mainmenuform.menu.search")))) {

                          
                            ManageViewNavigation.goNext((Viewable)   (new SearchForm()).prepareView(MainForm.TYPEFORM_SEARCH_STANDARD, (String) e.getByName("objSelText"), null, null,null, null,null, 0));


                        }


                        //list
                        if ((e.getByName("comandoText") != null) && (((String) e.getByName("comandoText")).equals(Locale.get("mainmenuform.menu.list")))) {

                            params = new Hashtable(3);
                            params.put("typeObj", (String) e.getByName("objSelText"));
                            params.put("searchField", (String) ((DocObj) Session.getListStructureObj().get((String) e.getByName("objSelText"))).getDefaultGroupIndex());
                            params.put("structureObjClass", (DocObj) Session.getListStructureObj().get((String) e.getByName("objSelText")));
                            params.put("typeSourceEvent", "View_new_groupValueObj");

                            Session.getMngService().doService(ManageService.SEARCH_GROUPVALUE_SERVICE, params);


                        }
                        
                         //workflow
                         if ((e.getByName("comandoText") != null) && (((String) e.getByName("comandoText")).equals(Locale.get("mainmenuform.menu.workflow")))) {

                             

                             ManageViewNavigation.goNext((Viewable) (new SearchForm()).prepareView(MainForm.TYPEFORM_SEARCH_WORKFLOW, (String) e.getByName("objSelText"), null,null, null, null,null, 0));

                
                        }


                    }
                    
                    
                    break;
                    
    
                case VIEW_ANAGBEANFORM_EVENT:
                   
                     ManageViewNavigation.goNext((Viewable)(new AdmAnagBeanObjForm()).prepareView( ((Integer)e.getByName("typeFORM")).intValue(), (AnagBean)e.getByName("AnagBeanObj"),(String)e.getByName("typeDoc"),null));
                                      
                    break;
                    
                case VIEW_ANAGBEANFORM_CART_EVENT:
                    
                    ManageViewNavigation.goNext((Viewable) (new AdmAnagBeanObjForm()).prepareView(((Integer) e.getByName("typeFORM")).intValue(), (AnagBean) e.getByName("AnagBeanObj"), (String) e.getByName("typeDoc"), (DocBean) e.getByName("doc")));

                    break;
                    
                    
                case VIEW_DOCBEANFORM_EVENT:
    
                    if (((String) e.getByName("callForm") != null) && (((String) e.getByName("callForm")).equals("AdmAnagBean"))) {

                                                 
                        ManageViewNavigation.goNextNoReturn((Viewable) (new AdmDocDetailForm()).prepareView((DocBean) e.getByName("DocBeanObj"), 0, null));
                  
                    } else if (((String) e.getByName("callForm") != null) && (((String) e.getByName("callForm")).equals("Cart_newDoc"))) {
                         
                        ManageViewNavigation.goNextNoReturn((Viewable) (new AdmDocDetailForm()).prepareView((DocBean) e.getByName("DocBeanObj"), 0, null));
                 
                    } else if (((String) e.getByName("callForm") != null) && (((String) e.getByName("callForm")).equals("Cart_UpdateDoc"))) {
    
                        ManageViewNavigation.goBack();
                        
                       ((AdmDocDetailForm) ManageViewNavigation.getCurrentDisplayed() ).prepareView(null, AdmDocDetailForm.ADD_CANCEL_COMMAND_ACTION, null);


                    } else if (((String) e.getByName("callForm") != null) && (((String) e.getByName("callForm")).equals("DuplicateDoc"))) {

                   
                        ManageViewNavigation.goNextsetMainMenuBack((Viewable) (new AdmDocDetailForm()).prepareView((DocBean) e.getByName("DocBeanObj"), 0, null));
                 

                    }  else {

                        ManageViewNavigation.goNext((Viewable)  (new AdmDocDetailForm()).prepareView((DocBean) e.getByName("DocBeanObj"), 0, null));
                    }
                    
                    break;
                    
                                  
                case REBUILD_DOC_UPDATE_HEAD_EVENT:
                    
                    //oggi
//
//                    (new AdmDocDetailForm())a.prepareView(null,AdmDocDetailForm.COMMIT_UPDATE_HEAD_ACTION,(AnagBean) e.getByName("oldObj"));
//                    
//                    e = new Event();
//                    Session.getMngEvent().handleEvent(ManageEvent.GOBACK_EVENT, e);
                    
                    //go back and rebuild back view
                    ((AdmDocDetailForm)ManageViewNavigation.goBack()).prepareView(null,AdmDocDetailForm.COMMIT_UPDATE_HEAD_ACTION,(AnagBean) e.getByName("oldObj"));
                    

                    break;
                    
                case REBUILD_DOC_NEW_ROW_EVENT:

                     ManageViewNavigation.goBack();
                    
                    ((AdmDocDetailForm) ManageViewNavigation.getCurrentDisplayed() ).prepareView(null, AdmDocDetailForm.COMMIT_NEW_ROW_ACTION, (AnagBean) e.getByName("oldObj"));

                   
                    break;   
                    
                    
                case REBUILD_DOC_UPDATE_ROW_EVENT:

                     ManageViewNavigation.goBack();
                    
                     ((AdmDocDetailForm) ManageViewNavigation.getCurrentDisplayed() ).prepareView(null,AdmDocDetailForm.COMMIT_UPDATE_ROW_ACTION,(AnagBean) e.getByName("oldObj"));

                     
                    break;
                    
                case REBUILD_DOC_DELETE_ROW_EVENT:

                     ManageViewNavigation.goBack();
                    
                    ((AdmDocDetailForm) ManageViewNavigation.getCurrentDisplayed() ).prepareView(null, AdmDocDetailForm.DELETE_LAST_ROW_ACTION,null);

                   
                    break;
                    
                case VIEW_GROUPVALUEOBJ_RESULT_EVENT:

                     boolean returnForm=((Boolean) e.getByName("returnForm")).booleanValue();
                     //getGrpValue().prepareView((PaginatedGroupValues) e.getByName("PaginatedGroupValues"), 0, Class.forName( AnagObj(da type Obj on AnangObj ) ).new instance..  );
                     
                     PaginatedGroupValues pg=((PaginatedGroupValues) e.getByName("PaginatedGroupValues"));
                     
                     GroupValueObjForm groupForm=null;
                     
                    if (((Structure) Session.getListStructureObj().get(pg.getTypeObj())).getType().equals("AnagObj")) {
        
                       groupForm= (new GroupValueObjForm()).prepareView(pg, 0, (AnagObj) Session.getListStructureObj().get(  pg.getTypeObj() ) );
            
                    } else if (((Structure) Session.getListStructureObj().get(pg.getTypeObj())).getType().equals("DocObj")) {

                        groupForm= (new GroupValueObjForm()).prepareView(pg, 0, (DocObj) Session.getListStructureObj().get(  pg.getTypeObj() ) );
            
                    }
                     
                     if (returnForm)
                         ManageViewNavigation.goNext((Viewable)groupForm);
                     else ManageViewNavigation.goNextNoReturn((Viewable)groupForm);
                     
                    break;
                    
                 case VIEW_PAGELISTOBJS_RESULT_EVENT:

                     boolean returnForm2=((Boolean) e.getByName("returnForm")).booleanValue();
                     
                     PageListForm pgForm= (new PageListForm()).prepareView((PaginatedValues) e.getByName("PaginatedValues"), 0);
                                          
                     if (returnForm2)
                         ManageViewNavigation.goNext((Viewable)pgForm);
                     else ManageViewNavigation.goNextNoReturn((Viewable)pgForm);
                     
                    break;
                    
                  case REBUILD_PAGELISTOBJS_RESULT_EVENT:
                    
                    //rebuild current pageList
                      
                    ((PageListForm)  ManageViewNavigation.getCurrentDisplayed()).prepareView(null,0);
                      
                   //oggi ManageViewNavigation.getPageList().prepareView(null,0);
                    
                    break;
                    
                 case REBUILD_GROUPVALUEOBJ_RESULT_EVENT:
                     
                    //rebuild current groupValue
                    ((GroupValueObjForm)  ManageViewNavigation.getCurrentDisplayed()).prepareView((PaginatedGroupValues) e.getByName("PaginatedGroupValues"), 0 ,null);
                   
                    //getGrpValue().prepareView((PaginatedGroupValues) e.getByName("PaginatedGroupValues"), 0, Class.forName( AnagObj(da type Obj on AnangObj ) ).new instance..  );
                   //oggi ManageViewNavigation.getGrpValue().prepareView((PaginatedGroupValues) e.getByName("PaginatedGroupValues"), 0 ,null);
                      
                    break;
                    
                case REBUILD_SEARCH_RESULT_EVENT:

                    ( (SearchForm) ManageViewNavigation.getCurrentDisplayed() ).prepareView(0, null, null,null, null, null,null, 0);
                    
                    break;
                    
                case VIEW_CART:

                    SearchForm srcFrm = (new SearchForm()).prepareView(MainForm.TYPEFORM_SEARCH_CART, (String) e.getByName("searchTypeObj"),null,null,null,(DocBean)e.getByName("doc"),null, 0);
                   
                    if ((e.getByName("goback") != null) && (((Boolean) e.getByName("goback"))).booleanValue()) {
                        ManageViewNavigation.goNext((Viewable) srcFrm );
                    } else {
                        ManageViewNavigation.goNextNoReturn((Viewable) srcFrm );
                    }
                    
                    break;
                    
                case REBUILD_GOBACK_PAGELISTVALUE_EVENT:
                    
                     ManageViewNavigation.goBack();

                     (  (PageListForm) ManageViewNavigation.getCurrentDisplayed() ).prepareView(null,(  (PageListForm) ManageViewNavigation.getCurrentDisplayed() ).getCurrentPage());
                  

                    break;
                    
                case REBUILD_GOBACK_SEARCHVALUE_EVENT:
                                        
                    ManageViewNavigation.goBack();
                    
                     (  (SearchForm) ManageViewNavigation.getCurrentDisplayed() ).prepareView(0, null, null, null,null, null,null,   (  (SearchForm) ManageViewNavigation.getCurrentDisplayed() ).getCurrentPage());
                  

                    break;
                    
                 case VIEW_FINDDOC_EVENT:
                     
                    SearchForm srcForm= (new SearchForm()).prepareView(MainForm.TYPEFORM_SEARCH_STANDARD, (String)e.getByName("typeObj"),(String)e.getByName("searchField") ,(String)e.getByName("searchFieldValue") , null, null,null, 0);
   
                    ManageViewNavigation.goNext((Viewable)srcForm);
                 
                    //lunch Search
                    srcForm.doSearch();
                    
                    break;
                    
             case VIEW_SENT_OBJS_EVENT:
                    
                    SearchForm searcForm=(new SearchForm()).prepareView(MainForm.TYPEFORM_SEARCH_WORKFLOW, (String)e.getByName("typeObj"),(String)e.getByName("searchField") ,(String)e.getByName("searchFieldValue") , null, null,(String)e.getByName("descNode"), 0);
   
                    ManageViewNavigation.goNextsetMainMenuBack((Viewable)searcForm);
                 
                    //lunch Search
                    searcForm.doSearch();
                    
                    break;
                    
                 case VIEW_UPDATE_LOGIN_EVENT:                   
   
                    ManageViewNavigation.goNext((Viewable) (new LoginForm()).prepareView(MainForm.TYPEFORM_UPDATE_LOGIN));
                                                        
                    break;    
                    
               case VIEW_ADMIN_CONNECTION_EVENT:
                    
   
                    ManageViewNavigation.goNext((Viewable)(new AdminConnectionForm()).prepareView());
                 
                                       
                    break;   
                    
               case VIEW_ADMIN_DATA_EVENT:                   
   
                    ManageViewNavigation.goNext((Viewable) (new AdminDataForm()).prepareView());
                 
                                       
                    break;    
                    
               case REBUILD_ADMDATA_EVENT:
                   
                   ((AdminDataForm)ManageViewNavigation.getCurrentDisplayed()).prepareView();
                    
                   
               break;       
                                        
               


            }
            
            params = null;
            e = null;


        } catch (Exception e2) {
            //#debug error
            System.out.println(e2.getMessage()==null?"Null":e2.getMessage());
           
        }

    }

   
}
