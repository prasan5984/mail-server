package implementations.keywords;

import implementations.MailServer;
import implementations.MailServerHelper;
import implementations.keywords.properties.MailKeysProperties;
import interfaces.KeyProperties;
import interfaces.Keyword;
import interfaces.Mail;
import interfaces.MailHandler;
import interfaces.MailReceiver;

import java.util.ArrayList;

import constants.MailServerConstants;

public class RcptTo implements Keyword
{

	private ArrayList< String >	toAddressList;
	private int					index;
	private boolean				status;

	@Override
	public void process( MailReceiver processor )
	{

		String tokenOne = processor.getToken( 1 );
				
		String toUser = tokenOne.replaceAll( "<(.*)>", "$1" );

		if ( toUser == null )
		{
			// Appropriate Error		
			return;
		}

		Mail mail = processor.getMail();
		mail.setTo( toUser.replaceAll( "\r\n$", "" ) );
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
		return "RCPT TO:";
	}

	@Override
	public KeyProperties getProperties()
	{
		MailKeysProperties prop = new MailKeysProperties();
		Keyword key = new MailFrom();
		prop.setKeywordName( "RCPT TO:" );
		prop.setLastKey( key.getName() );
		prop.setMultiOccurence( true );
		prop.setSkipSequenceCheck( false );
		return prop;
	}

	@Override
	public Keyword getClonedObject()
	{
		RcptTo rcptTo = new RcptTo();
		return rcptTo;
	}

	@Override
	public String getMailTransaction( MailHandler mailSender, Mail mail, String response )
	{
		if ( index == 0 )
			toAddressList = mail.getTo();
		if ( toAddressList != null && index == toAddressList.size() - 1 )
			status = true;
		return "RCPT TO:" + toAddressList.get( index++ ) + "\r\n";
	}

	@Override
	public boolean getStatus()
	{
		return status;
	}

	@Override
	public boolean skipOnSend()
	{
		// TODO Auto-generated method stub
		return false;
	}

}
