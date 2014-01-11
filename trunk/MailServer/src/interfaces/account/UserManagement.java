package interfaces.account;

import implementations.PopMailStructure;
import interfaces.MailProcessor;

public interface UserManagement extends MailProcessor
{

	public void initialize();

	public void onRead( Object ip, String msg );

	public boolean checkUser( String user );

	public void saveMail( String user, String msg );

	public String getUserCredentials( String user );

	public PopMailStructure getUserMailDtls( String user );

	public void lockUser( String user );

	public void releaseUser( String user );
	
	public void updateUser (PopMailStructure mailStructure);

}
