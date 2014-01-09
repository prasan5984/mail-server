package implementations.pop.keyword;

import implementations.keywords.properties.PopProperties;
import implementations.pop.PopReceiver;
import interfaces.KeyProperties;
import interfaces.Keyword;
import interfaces.Mail;
import interfaces.MailHandler;
import interfaces.MailReceiver;

public class PassKeyword implements Keyword
{

	@Override
	public void process( MailReceiver processor )
	{
		PopReceiver receiver = (PopReceiver)processor;
		String password = receiver.getToken( 1 );
		if ( receiver.getUserCredentials().equals( password ) )
		{
			receiver.setStatus( 1 );
			receiver.getUserManagement().lockUser( receiver.getCurUser() );
			receiver.sendResponse( "+OK" );
		}
		else
			receiver.sendResponse( "-ERR Authentication Failed" );

	}

	@Override
	public void successiveProcess( MailReceiver processor )
	{
		// TODO Auto-generated method stub

	}

	@Override
	public String getName()
	{
		return "PASS";
	}

	@Override
	public KeyProperties getProperties()
	{
		PopProperties properties = new PopProperties();

		properties.setKeywordName( "PASS" );
		properties.setLastKey( "USER" );
		properties.setMultiOccurence( false );
		properties.setSkipSequenceCheck( true );
		properties.setTransactionStatus( 0 );

		return properties;
	}

	@Override
	public Keyword getClonedObject()
	{
		return new PassKeyword();
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
