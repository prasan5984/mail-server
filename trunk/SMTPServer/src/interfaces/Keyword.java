package interfaces;

public interface Keyword extends Cloneable
{

	public void process( MailReceiver processor );

	public void successiveProcess( MailReceiver processor );

	public String getName();

	public KeyProperties getProperties();

	public Keyword getClonedObject();

	public String getMailTransaction( MailHandler processor, Mail mail, String response );

	public boolean getStatus();

	public boolean skipOnSend();

}
