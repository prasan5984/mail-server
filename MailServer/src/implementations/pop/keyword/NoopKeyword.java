package implementations.pop.keyword;

import implementations.keywords.properties.PopProperties;
import implementations.pop.PopReceiver;
import interfaces.KeyProperties;
import interfaces.Keyword;
import interfaces.Mail;
import interfaces.MailHandler;
import interfaces.MailReceiver;

public class NoopKeyword implements Keyword
{

	@Override
	public void process( MailReceiver processor )
	{
		PopReceiver receiver = (PopReceiver)processor;
		receiver.sendResponse( "+OK" );
		
	}

	@Override
	public void successiveProcess( MailReceiver processor )
	{
		// TODO Auto-generated method stub

	}

	@Override
	public String getName()
	{
		return "NOOP";
	}

	@Override
	public KeyProperties getProperties()
	{
		PopProperties properties = new PopProperties();

		properties.setKeywordName( "NOOP" );
		properties.setLastKey( "NOOP" );
		properties.setMultiOccurence( false );
		properties.setSkipSequenceCheck( true );
		properties.setTransactionStatus( 1 );

		return properties;
	}

	@Override
	public Keyword getClonedObject()
	{
		return new NoopKeyword();
	}

	@Override
	public String getMailTransaction( MailHandler processor, Mail mail, String response )
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean getStatus()
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean skipOnSend()
	{
		// TODO Auto-generated method stub
		return false;
	}

}
