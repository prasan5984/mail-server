package implementations.pop.keyword;

import implementations.PopMailStructure;
import implementations.keywords.properties.PopProperties;
import implementations.pop.PopReceiver;
import interfaces.KeyProperties;
import interfaces.Keyword;
import interfaces.Mail;
import interfaces.MailHandler;
import interfaces.MailReceiver;

public class RsetKeyword implements Keyword
{

	@Override
	public void process( MailReceiver processor )
	{
		PopReceiver receiver = (PopReceiver)processor;
		PopMailStructure mailStructure = receiver.getUserMails();

		int mailCount = mailStructure.getMailCount();

		mailStructure.resetDeletedMessages();
		receiver.sendResponse( "+OK maildrop has " + mailCount + " messages" );

		return;
	}

	@Override
	public void successiveProcess( MailReceiver processor )
	{
		// TODO Auto-generated method stub

	}

	@Override
	public String getName()
	{
		return "RSET";
	}

	@Override
	public KeyProperties getProperties()
	{
		PopProperties properties = new PopProperties();

		properties.setKeywordName( "RSET" );
		properties.setLastKey( "RETR" );
		properties.setMultiOccurence( false );
		properties.setSkipSequenceCheck( true );
		properties.setTransactionStatus( 1 );

		return properties;
	}

	@Override
	public Keyword getClonedObject()
	{
		return new RsetKeyword();
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
