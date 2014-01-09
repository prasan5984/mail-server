package implementations.pop.keyword;

import java.util.ArrayList;

import implementations.PopMailStructure;
import implementations.keywords.properties.PopProperties;
import implementations.pop.PopReceiver;
import interfaces.KeyProperties;
import interfaces.Keyword;
import interfaces.Mail;
import interfaces.MailHandler;
import interfaces.MailReceiver;

public class StatKeyword implements Keyword
{

	@Override
	public void process( MailReceiver processor )
	{
		PopReceiver receiver = (PopReceiver)processor;
		PopMailStructure mailStructure = receiver.getUserMails();

		int mailCount = mailStructure.getMailCount();
		int totMailSize = mailStructure.getTotMailSize();
		ArrayList< Integer > mailSizeList = mailStructure.getMailSizeList();
		ArrayList< Integer > deletedMails = mailStructure.getMarkedForDeletion();

		for ( int i = 0; i < deletedMails.size(); i++ )
		{
			mailCount = mailCount - 1;
			totMailSize = totMailSize - mailSizeList.get( i );
		}

		receiver.sendResponse( "+OK " + mailCount + " " + totMailSize );
	}

	@Override
	public void successiveProcess( MailReceiver processor )
	{
		// TODO Auto-generated method stub

	}

	@Override
	public String getName()
	{
		return "STAT";
	}

	@Override
	public KeyProperties getProperties()
	{
		PopProperties properties = new PopProperties();

		properties.setKeywordName( "STAT" );
		properties.setLastKey( "PASS" );
		properties.setMultiOccurence( false );
		properties.setSkipSequenceCheck( true );
		properties.setTransactionStatus( 1 );

		return properties;
	}

	@Override
	public Keyword getClonedObject()
	{
		return new StatKeyword();
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
