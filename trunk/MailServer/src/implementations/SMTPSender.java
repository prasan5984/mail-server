package implementations;

import implementations.keywords.properties.MailKeysProperties;
import interfaces.Keyword;
import interfaces.Mail;
import interfaces.MailHandler;
import interfaces.MailHelper;
import interfaces.MailProcessor;
import interfaces.NetworkInteractor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;

import constants.MailServerConstants;

public class SMTPSender implements MailHandler, Runnable
{

	private Mail									mail;
	protected static ArrayList< MailKeysProperties >	keywordPropertySequence	= new ArrayList< MailKeysProperties >();
	public static LinkedHashMap< String, Keyword >	keywordMap				= new LinkedHashMap< String, Keyword >();
	private int										keywordIndex			= -1;
	private MailKeysProperties							currentProperty;
	private Keyword									currentKeyword;
	private static MailProcessor					mailProcessor;
	private Object									conId;
	private boolean									tempCompletionFl		= false;
	private boolean									completionFlag			= false;
	private NetworkInteractor						interactor;

	SMTPSender( Mail mailObj )
	{
		mail = mailObj;
	}

	public SMTPSender()
	{
		// TODO Auto-generated constructor stub
	}

	public void initialize()
	{
		mailProcessor = MailServer.getMailProcessor();
		keywordMap = mailProcessor.getKeywordMap();
		setPropertySequence( keywordMap.values() );
		interactor = MailServer.getInteractor();

	}

	private void setPropertySequence( Collection< Keyword > keys )
	{
		Iterator< Keyword > i = keys.iterator();

		while ( i.hasNext() )
		{
			Keyword key = i.next();
			if ( !key.skipOnSend() )
				keywordPropertySequence.add( (MailKeysProperties)key.getProperties() );
		}

	}

	private void tempCommands()
	{
		mailProcessor.sendResponse( conId, "auth plain AHByYXNhbm5hLWJmMWY5ZDVkYmFlYTJiM2YANTEwMTY2MTlkOWVmMTBhOQ==" + "\r\n" );
		tempCompletionFl = true;
		//keywordIndex--;

	}

	public String processMailTransaction( String response )
	{

		if ( completionFlag )
		{
			mailProcessor.getInteractor().onClose( conId );
			return null;

		}

		// Temporary Block
		if ( MailServerConstants.TEST_FLAG && keywordIndex == 0 && !tempCompletionFl )
		{
			tempCommands();
			return null;
		}
		
		if ( currentProperty == null /*&& ( !currentProperty.isMultiOccurence() )*/|| currentKeyword.getStatus() )
		{
			currentProperty = keywordPropertySequence.get( ++keywordIndex );
			currentKeyword = keywordMap.get( currentProperty.getKeywordName() ).getClonedObject();

		}

		String msg = currentKeyword.getMailTransaction( this, mail, response );

		mailProcessor.sendResponse( conId, msg );

		return null;

	}

	private int getCodeStatus( String code )
	{
		return 0;
	}

	private ArrayList< String > tokenize( String line )
	{
		return null;
	}

	@Override
	public void run()
	{
		NetworkInteractor interactor = mailProcessor.getInteractor();
		String domain = mail.getDomain();
		conId = interactor.initiateConnection( domain );
		mailProcessor.registerSender( conId, this );

	}

	public String getEndString()
	{
		return null;
	}

	@Override
	public MailHelper getHelper()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCompletion()
	{
		completionFlag = true;

	}

}
