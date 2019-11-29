/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package agile.view;

import de.enough.polish.ui.TextField;
import de.enough.polish.ui.UiAccess;
import de.enough.polish.util.Locale;
import de.enough.polish.ui.AlertType;
import de.enough.polish.ui.Command;
import de.enough.polish.ui.Displayable;

import agile.control.Event;
import agile.control.ManageEvent;
import agile.control.ManageViewNavigation;
import agile.session.Session;

/**
 *
 * @author ruego
 */
public class LoginForm extends MainForm implements Viewable {

    public static Command login = new Command(Locale.get("command.login"), Command.BACK, 0);

    public LoginForm() {

        super("Login", false, true, false, null);

        setHelpFile("login");

    }

    public void showView() {

        ManageViewNavigation.getDisplay().setCurrent(this);

    }

    
    private TextField ut = null;
    private TextField pw = null;
    private String user = null;
    private String password = null;
    private boolean existUser = false;

    public LoginForm prepareView(int typeFORM) {

        setTypeFORM(typeFORM);


        //controllo se l'utente è gia settato
        if ((Session.getActiveUser() != null) && (Session.getActiveUser().getUsername() != null) && (!Session.getActiveUser().getUsername().equals(""))) {

            user = Session.getActiveUser().getUsername();
            password = Session.getActiveUser().getPassword();
            existUser = true;

        } else {

            existUser = false;

        }


        //#style simpleinput
        pw = new TextField(Locale.get("LoginForm.pw") + " :", null, 100, TextField.ANY);
        UiAccess.setInputMode(pw, UiAccess.MODE_NATIVE);



        switch (getTypeFORM()) {

            case MainForm.TYPEFORM_INIT_LOGIN:
                this.addCommand(login);
                this.addCommand(ManageViewNavigation.exit);
                //#style simpleinput
                ut = new TextField(Locale.get("LoginForm.user") + " :", user, 100, ((user != null) ? TextField.UNEDITABLE : TextField.ANY));
                UiAccess.setInputMode(ut, UiAccess.MODE_NATIVE);

                break;

            case MainForm.TYPEFORM_UPDATE_LOGIN:
                this.addCommand(ManageViewNavigation.backCommand);
                this.addCommand(ManageViewNavigation.home);
                this.addCommand(ManageViewNavigation.saveCommand);
                //#style simpleinput
                ut = new TextField(Locale.get("LoginForm.user") + " :", user, 100, TextField.ANY);
                UiAccess.setInputMode(ut, UiAccess.MODE_NATIVE);

                break;

        }

        this.append(ut);
        this.append(pw);

        return this;

    }

    public void commandAction(Command cmd, Displayable disp) {

        if (cmd == login) {

            if ((ut.getString() != null) && (!ut.getString().equals("")) && (pw.getString() != null) && (!pw.getString().equals(""))) {

                if (existUser) {

                    //controllo pw
                    if ((password != null) && (password.equalsIgnoreCase(pw.getString()))) {

                        ManageViewNavigation.goNextNoReturn((Viewable) ManageViewNavigation.getMainMenu());

                    } else {

                        ManageViewNavigation.showAlert("Info.", Locale.get("message.loginfailed"), null, AlertType.INFO);

                    }



                } else {

                    boolean result = Session.getActiveUser().write(ut.getString(), pw.getString());

                    if (!result) {

                        ManageViewNavigation.showAlert("Info.", Locale.get("message.errwrite"), null, AlertType.INFO);

                    } else {

                        ManageViewNavigation.goNextNoReturn((Viewable) ManageViewNavigation.getMainMenu());

                    }


                }


            } else {

                ManageViewNavigation.showAlert("Info.", Locale.get("message.errlogin"), null, AlertType.INFO);

            }

        } else if (cmd == ManageViewNavigation.saveCommand) {

            if ((ut.getString() != null) && (!ut.getString().equals("")) && (pw.getString() != null) && (!pw.getString().equals(""))) {

                boolean result = Session.getActiveUser().write(ut.getString(), pw.getString());

                if (!result) {

                    ManageViewNavigation.showAlert("Info.", Locale.get("message.errwrite"), null, AlertType.INFO);

                } else {

                    e = new Event();
                    Session.getMngEvent().handleEvent(ManageEvent.GOBACK_EVENT, e);

                }

            } else {

                ManageViewNavigation.showAlert("Info.", Locale.get("message.errlogin"), null, AlertType.INFO);

            }

        } else if (cmd == ManageViewNavigation.healpCommand) {
            e = new Event();
            e.setByName("helpFile", getHelpFile());
            Session.getMngEvent().handleEvent(ManageEvent.HELP_EVENT, e);
        } else if (cmd == ManageViewNavigation.exit) {
            exitMIDlet();
        } else if (cmd == ManageViewNavigation.backCommand) {

            e = new Event();
            Session.getMngEvent().handleEvent(ManageEvent.GOBACK_EVENT, e);


        } else if (cmd == ManageViewNavigation.home) {

            e = new Event();
            Session.getMngEvent().handleEvent(ManageEvent.GOHOME_EVENT, e);

        }

    }

    public void exitMIDlet() {

        try {

            ManageViewNavigation.getDisplay().setCurrent(null);
            ManageViewNavigation.getMidlet().notifyDestroyed();

        } catch (Exception e) {
        }

    }
}
