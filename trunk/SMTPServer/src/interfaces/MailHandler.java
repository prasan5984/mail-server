package interfaces;

public interface MailHandler
{

	public String processMailTransaction( String line );

	public MailHelper getHelper();

	public void onCompletion();

}
