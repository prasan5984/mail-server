package implementations.keywords;

import constants.MailServerConstants;
import implementations.keywords.properties.MailKeysProperties;
import interfaces.KeyProperties;
import interfaces.Keyword;
import interfaces.Mail;
import interfaces.MailHandler;
import interfaces.MailReceiver;

public class MailFrom implements Keyword
{

	@Override
	public void process( MailReceiver processor )
	{
		Mail mail = processor.getMail();
		String from = processor.getToken( 1 );
		if ( from == null )
		{
			// Enter appropriate errors
			return;
		}

		mail.setFrom( from.replaceAll( "\r\n$", "" ) );
		processor.setMail( mail );

		processor.sendResponse( MailServerConstants.DEFAULT_SUCCESS_CODE + " "
				+ MailServerConstants.CODE_MSG_MAP.get( MailServerConstants.DEFAULT_SUCCESS_CODE ) );
	}

	@Override
	public void successiveProcess( MailReceiver processor )
	{
		// TODO Auto-generated method stub

	}

	@Override
	public String getName()
	{
		return "MAIL FROM:";
	}

	@Override
	public KeyProperties getProperties()
	{
		MailKeysProperties prop = new MailKeysProperties();
		Keyword key = new Ehlo();
		Keyword key1 = new Helo();
		Keyword key2 = new Data();
		prop.setKeywordName( "MAIL FROM:" );
		prop.setLastKey( key.getName() );
		prop.setLastKey( key1.getName() );
		prop.setLastKey( key2.getName() );
		prop.setMultiOccurence( false );
		prop.setSkipSequenceCheck( false );
		return prop;

	}

	@Override
	public Keyword getClonedObject()
	{
		MailFrom mailFrom = new MailFrom();
		return mailFrom;
	}

	@Override
	public String getMailTransaction( MailHandler mailSender, Mail mail, String response )
	{
		mail.getFrom();
		return "Mail From: " + mail.getFrom() + "\r\n";
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
