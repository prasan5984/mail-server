package implementations.keywords;

import implementations.keywords.properties.MailKeysProperties;
import interfaces.KeyProperties;
import interfaces.Keyword;
import interfaces.Mail;
import interfaces.MailHandler;
import interfaces.MailReceiver;
import constants.MailServerConstants;

public class Quit implements Keyword
{

	@Override
	public void process( MailReceiver processor )
	{
		if ( processor.isMailCompletion() )
			processor.processMail( );

		processor.sendResponse( MailServerConstants.DEFAULT_SUCCESS_CODE + " "
				+ MailServerConstants.CODE_MSG_MAP.get( MailServerConstants.DEFAULT_SUCCESS_CODE ) );

		processor.onCompletion();

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
		MailKeysProperties prop = new MailKeysProperties();
		prop.setKeywordName( "QUIT" );
		prop.setMultiOccurence( false );
		prop.setSkipSequenceCheck( true );
		return prop;
	}

	@Override
	public Keyword getClonedObject()
	{
		Quit quit = new Quit();
		return quit;
	}

	@Override
	public String getMailTransaction( MailHandler mailSender, Mail mail, String response )
	{
		mailSender.onCompletion();
		return "Quit" + "\r\n";
	}

	@Override
	public boolean getStatus()
	{
		return true;
	}

	@Override
	public boolean skipOnSend()
	{
		// TODO Auto-generated method stub
		return false;
	}
}
