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

public class RetrKeyword implements Keyword
{

	@Override
	public void process( MailReceiver processor )
	{
		PopReceiver receiver = (PopReceiver)processor;
		PopMailStructure mailStructure = receiver.getUserMails();

		int mailCount = mailStructure.getMailCount();
		ArrayList< String > mailList = mailStructure.getMailList();
		ArrayList< Integer > mailSizeList = mailStructure.getMailSizeList();
		ArrayList< Integer > deletedMessages = mailStructure.getMarkedForDeletion();

		String token = receiver.getToken( 1 );

		if ( token == null )
		{
			receiver.sendResponse( "-ERR Message Number is required" );
			return;
		}

		int mailId = Integer.parseInt( token );

		if ( mailId < 1 || mailId > mailCount || deletedMessages.contains( mailId - 1 ) )
		{
			receiver.sendResponse( "-ERR No such Message" );
			return;
		}

		receiver.sendResponse( "+OK " + mailSizeList.get( mailId - 1 ) + " octets\r\n" + mailList.get( mailId - 1 ) + "\r\n." );

	}

	@Override
	public void successiveProcess( MailReceiver processor )
	{
		// TODO Auto-generated method stub

	}

	@Override
	public String getName()
	{
		return "RETR";
	}

	@Override
	public KeyProperties getProperties()
	{
		PopProperties properties = new PopProperties();

		properties.setKeywordName( "RETR" );
		properties.setLastKey( "LIST" );
		properties.setLastKey( "STAT" );
		properties.setMultiOccurence( false );
		properties.setSkipSequenceCheck( true );
		properties.setTransactionStatus( 1 );

		return properties;
	}

	@Override
	public Keyword getClonedObject()
	{
		return new RetrKeyword();
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
