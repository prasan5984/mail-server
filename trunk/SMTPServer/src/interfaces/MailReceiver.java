package interfaces;

public interface MailReceiver extends MailHandler
{

	public void initialize();

	public void onConnecting();

	public void sendResponse( String string );

	public Mail getMail();

	public void setMail( Mail mail );

	public MailHelper getHelper();

	public void processMail( );

	public boolean isRetainKeyword();

	public void setRetainKeyword( boolean retainKeyword );

	public String getToken( int index );

	public boolean isMailCompletion();

	public void setMailCompletion( boolean mailCompletion );
	
	public void addCurrentMail ();

	public NetworkInteractor getInteractor();

	public void onCompletion();
}
