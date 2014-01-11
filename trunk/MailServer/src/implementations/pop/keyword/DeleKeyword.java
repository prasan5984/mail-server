package implementations.pop.keyword;

import implementations.PopMailStructure;
import implementations.keywords.properties.PopProperties;
import implementations.pop.PopReceiver;
import interfaces.KeyProperties;
import interfaces.Keyword;
import interfaces.Mail;
import interfaces.MailHandler;
import interfaces.MailReceiver;

import java.util.ArrayList;

public class DeleKeyword implements Keyword
{

	@Override
	public void process( MailReceiver processor )
	{
		PopReceiver receiver = (PopReceiver)processor;
		PopMailStructure mailStructure = receiver.getUserMails();

		String token = receiver.getToken( 1 );

		int mailCount = mailStructure.getMailCount();
		ArrayList< Integer > deletedMessages = mailStructure.getMarkedForDeletion();

		if ( token == null )
		{
			receiver.sendResponse( "-ERR Message Number is required" );
			return;
		}

		int mailId = Integer.parseInt( token );

		if ( mailId < 1 || mailId > mailCount )
		{
			receiver.sendResponse( "-ERR No such Message" );
			return;
		}

		if ( deletedMessages.contains( mailId - 1 ) )
		{
			receiver.sendResponse( "-ERR Message already deleted" );
			return;
		}

		mailStructure.markMessageNoForDeletion( mailId - 1 );
		receiver.sendResponse( "+OK Message " + mailId + " deleted" );

	}

	@Override
	public void successiveProcess( MailReceiver processor )
	{
		// TODO Auto-generated method stub

	}

	@Override
	public String getName()
	{
		return "DELE";
	}

	@Override
	public KeyProperties getProperties()
	{
		PopProperties properties = new PopProperties();

		properties.setKeywordName( "DELE" );
		properties.setLastKey( "RETR" );
		properties.setLastKey( "LIST" );
		properties.setMultiOccurence( false );
		properties.setSkipSequenceCheck( true );
		properties.setTransactionStatus( 1 );

		return properties;
	}

	@Override
	public Keyword getClonedObject()
	{
		return new DeleKeyword();
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
