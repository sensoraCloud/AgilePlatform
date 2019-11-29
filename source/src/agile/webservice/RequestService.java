package agile.webservice;

import de.enough.polish.rmi.Remote;
import de.enough.polish.rmi.RemoteException;

public interface RequestService extends Remote {

    /**
     *
     */
    public String syncTypeObj(RequestParameters params, String jsonObj) throws RemoteException;

    /**
     *
     */
    public String sendObj(RequestParameters params, String jsonObj) throws RemoteException;

}
