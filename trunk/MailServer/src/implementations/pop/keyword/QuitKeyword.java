package implementations.pop.keyword;

import implementations.PopMailStructure;
import implementations.keywords.properties.PopProperties;
import implementations.pop.PopReceiver;
import interfaces.KeyProperties;
import interfaces.Keyword;
import interfaces.Mail;
import interfaces.MailHandler;
import interfaces.MailReceiver;
import interfaces.account.UserManagement;

public class QuitKeyword implements Keyword
{

	@Override
	public void process( MailReceiver processor )
	{
		PopReceiver receiver = (PopReceiver)processor;
		PopMailStructure mailStructure = receiver.getUserMails();
		UserManagement userManager = receiver.getUserManagement();

		if ( receiver.getStatus() != 0 )
		{
			userManager.updateUser( mailStructure );
			userManager.releaseUser( mailStructure.getUser() );
		}

		receiver.setCurUser( null );
		receiver.setUserCredentials( null );
		receiver.setMailStructure( null );
		receiver.sendResponse( "+OK Bye" );
		receiver.onCompletion();
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
		return "QUIT";
	}

	@Override
	public KeyProperties getProperties()
	{
		PopProperties properties = new PopProperties();

		properties.setKeywordName( "QUIT" );
		properties.setLastKey( "RETR" );
		properties.setLastKey( "PASS" );
		properties.setLastKey( "USER" );
		properties.setMultiOccurence( false );
		properties.setSkipSequenceCheck( true );
		properties.setTransactionStatus( 0 );

		return properties;
	}

	@Override
	public Keyword getClonedObject()
	{
		return new QuitKeyword();
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
