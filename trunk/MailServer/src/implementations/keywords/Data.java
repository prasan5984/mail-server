package implementations.keywords;

import implementations.keywords.properties.MailKeysProperties;
import interfaces.KeyProperties;
import interfaces.Keyword;
import interfaces.Mail;
import interfaces.MailHandler;
import interfaces.MailReceiver;
import constants.MailServerConstants;

public final class Data implements Keyword
{

	private String	data;
	private boolean	status;
	private boolean	reprocessFlag;

	@Override
	public void process( MailReceiver processor )
	{
		processor.setRetainKeyword( true );
		processor.sendResponse( MailServerConstants.DATA_CODE + " " + MailServerConstants.CODE_MSG_MAP.get( MailServerConstants.DATA_CODE ) );
	}

	@Override
	public void successiveProcess( MailReceiver processor )
	{

		if ( data == null )
			data = processor.getToken( 0 );
		else
			data = data + processor.getToken( 0 );

		if ( data.contains( "\r\n.\r\n" ) )
		{
			Mail mail = processor.getMail();
			mail.setData( data );
			processor.setMail( mail );
			processor.addCurrentMail();
			processor.setRetainKeyword( false );
			processor.setMailCompletion( true );

			processor.sendResponse( MailServerConstants.DEFAULT_SUCCESS_CODE + " "
					+ MailServerConstants.CODE_MSG_MAP.get( MailServerConstants.DEFAULT_SUCCESS_CODE ) );
		}

	}

	@Override
	public String getName()
	{
		// TODO Auto-generated method stub
		return "DATA";
	}

	@Override
	public KeyProperties getProperties()
	{
		MailKeysProperties prop = new MailKeysProperties();
		Keyword key = new RcptTo();
		prop.setKeywordName( "DATA" );
		prop.setLastKey( key.getName() );
		prop.setMultiOccurence( false );
		prop.setSkipSequenceCheck( false );
		return prop;
	}

	@Override
	public Keyword getClonedObject()
	{
		Data data = new Data();
		return data;
	}

	@Override
	public String getMailTransaction( MailHandler mailSender, Mail mail, String response )
	{
		if ( !reprocessFlag )
		{
			reprocessFlag = true;
			return "DATA" + "\r\n";
		}
		else
		{
			status = true;
			return mail.getData();
		}

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
