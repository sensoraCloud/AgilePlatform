package agile.webservice;

import de.enough.polish.io.Externalizable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class RequestParameters implements Externalizable {


    public RequestParameters(String licenseNumber, String password,String typeObj,String typeReq, String user) {
		super();
		this.licenseNumber = licenseNumber;
		this.password = password;
		this.typeObj = typeObj;
                this.typeReq = typeReq;
		this.user = user;
		
	}

    private String licenseNumber;

    public void setLicenseNumber( String licenseNumber ) {
        this.licenseNumber = licenseNumber;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    private String password;

    public void setPassword( String password ) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    private String typeObj;

    public void setTypeObj( String typeObj ) {
        this.typeObj = typeObj;
    }

    public String getTypeObj() {
        return typeObj;
    }

    private String typeReq;

    public void setTypeReq( String typeReq ) {
        this.typeReq = typeReq;
    }

    public String getTypeReq() {
        return typeReq;
    }

    private String user;

    public void setUser( String user ) {
        this.user = user;
    }

    public String getUser() {
        return user;
    }

    public void read(DataInputStream in) throws IOException {

        this.licenseNumber = in.readUTF();
        this.password = in.readUTF();
        this.typeObj = in.readUTF();
        this.typeReq = in.readUTF();
        this.user = in.readUTF();

	}

	public void write(DataOutputStream out) throws IOException {
		
		out.writeUTF( this.licenseNumber );
                out.writeUTF( this.password );
                out.writeUTF( this.typeObj );
                out.writeUTF( this.typeReq );
                out.writeUTF( this.user );
		
	}


}