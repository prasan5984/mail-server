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

public class ListKeyword implements Keyword
{

	@Override
	public void process( MailReceiver processor )
	{
		PopReceiver receiver = (PopReceiver)processor;
		PopMailStructure mailStructure = receiver.getUserMails();

		int mailCount = mailStructure.getMailCount();
		String token = receiver.getToken( 1 );

		ArrayList< Integer > mailSizeList = mailStructure.getMailSizeList();

		String response = "+OK ";

		if ( token == null )
		{
			response = response + mailCount + " messages(" + mailStructure.getTotMailSize() + ")\r\n";

			for ( int i = 1; i <= mailCount; i++ )
				response = response + " " + i + " " + mailSizeList.get( i - 1 ) + "\r\n";

			response = response + ".";
		}
		else
		{
			int mailId = Integer.parseInt( token );

			if ( mailId < 1 || mailId > mailCount )
			{
				receiver.sendResponse( "-ERR Unknown Message Number" );
				return;
			}

			response = response + " " + mailId + " " + mailSizeList.get( mailId - 1 );
		}

		receiver.sendResponse( response );
	}

	@Override
	public void successiveProcess( MailReceiver processor )
	{
		// TODO Auto-generated method stub

	}

	@Override
	public String getName()
	{
		return "LIST";
	}

	@Override
	public KeyProperties getProperties()
	{
		PopProperties properties = new PopProperties();

		properties.setKeywordName( "LIST" );
		properties.setLastKey( "PASS" );
		properties.setLastKey( "STAT" );
		properties.setMultiOccurence( false );
		properties.setSkipSequenceCheck( true );
		properties.setTransactionStatus( 1 );

		return properties;
	}

	@Override
	public Keyword getClonedObject()
	{
		return new ListKeyword();
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
