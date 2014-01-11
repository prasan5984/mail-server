package implementations;

import implementations.keywords.properties.MailKeysProperties;
import interfaces.Keyword;
import interfaces.Mail;
import interfaces.MailHelper;
import interfaces.MailProcessor;
import interfaces.MailReceiver;
import interfaces.NetworkInteractor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import constants.MailServerConstants;

public class SMTPReceiver implements MailReceiver
{

	// public static HashMap< Keyword, SMTPProperties > keywordPropertiesMap =
	// new HashMap< Keyword, SMTPProperties >();
	public static HashMap< String, Keyword >	keywordMap		= new HashMap< String, Keyword >();
	public static ArrayList< Mail >				mailList		= new ArrayList< Mail >();
	ArrayList< String >							tokens;
	private Mail								currentMail;
	private Keyword								lastKeyword;
	private boolean								retainKeyword;
	private int									mailState		= 0;
	MailHelper									helper;
	private Object								conId;
	private String								currentMsg;
	public static MailProcessor					mailProcessor;
	private boolean								mailCompletion	= false;
	private static NetworkInteractor			interactor;
	private static ArrayList< Mail >			receivedMails;

	public SMTPReceiver( Object channel )
	{
		conId = channel;
	}

	public void onCompletion()
	{
		interactor.onClose( conId );

	}

	public SMTPReceiver()
	{

	}

	public void initialize()
	{
		mailProcessor = MailServer.getMailProcessor();
		keywordMap = mailProcessor.getKeywordMap();

		interactor = MailServer.getInteractor();

	}

	@Override
	public void onConnecting()
	{
		receivedMails = new ArrayList< Mail >();
		sendResponse( MailServerConstants.SMTP_WELCOME_MESSAGE );
	}

	public ArrayList< String > tokenize( String line, Set< String > set )
	{
		ArrayList< String > tokens = new ArrayList< String >();

		for ( Iterator< String > itr = set.iterator(); itr.hasNext(); )
		{
			String expression = itr.next();
			if ( line.toUpperCase().startsWith( expression ) )
			{
				int expLength = expression.length();
				tokens.add( line.substring( 0, expLength ) );
				if ( line.length() > expLength + 1 )
				{
					String restLine = line.substring( expLength );
					String trimLine = ( restLine.replaceAll( "\r\n$", "" ) ).replaceFirst( "^ ", "" );
					if ( !trimLine.equals( "" ) )
						tokens.add( trimLine );

					tokens.add( line.substring( expLength ) );
				}

				return tokens;
			}

		}
		sendResponse( MailServerConstants.UNKNOWN_CMD_CODE + " " + MailServerConstants.CODE_MSG_MAP.get( MailServerConstants.UNKNOWN_CMD_CODE ) );
		this.setMailCompletion( false );
		return null;

	}

	@Override
	public String processMailTransaction( String line )
	{

		tokens = new ArrayList< String >();

		if ( isRetainKeyword() )
		{
			tokens.add( line );
			lastKeyword.successiveProcess( this );
			return null;
		}

		tokens = tokenize( line, keywordMap.keySet() );

		if ( tokens == null ) { return null; }

		String keyword = tokens.get( 0 );

		Keyword orgKey = keywordMap.get( keyword );

		Keyword key = orgKey.getClonedObject();
		MailKeysProperties properties = (MailKeysProperties)key.getProperties();

		boolean processFl = false;

		if ( !properties.isSkipSequenceCheck() )
		{
			if ( lastKeyword != null )
			{
				if ( lastKeyword.getName().equals( key.getName() ) )
				{
					if ( properties.isMultiOccurence() )
						processFl = true;
				}
				else if ( properties.getLastKey().contains( lastKeyword.getName() ) )
					processFl = true;
			}
		}
		else
			processFl = true;

		if ( processFl )
		{
			key.process( this );
			lastKeyword = key;
		}

		else
		{
			sendResponse( MailServerConstants.SEQ_ERROR_CODE + " " + MailServerConstants.CODE_MSG_MAP.get( MailServerConstants.SEQ_ERROR_CODE ) );
			this.setMailCompletion( false );
		}
		return null;

	}

	@Override
	public void sendResponse( String responseCode )
	{
		mailProcessor.sendResponse( conId, responseCode + "\r\n" );

	}

	public int getMailState()
	{
		return mailState;
	}

	public void setMailState( int state )
	{
		mailState = state;
	}

	public Mail getMail()
	{
		return currentMail;

	}

	public void setMail( Mail mail )
	{
		currentMail = mail;
	}

	@Override
	public String getToken( int index )
	{
		if ( tokens.size() > index )
			return tokens.get( index );
		return null;
	}

	@Override
	public MailHelper getHelper()
	{
		return helper;
	}

	private ArrayList< Mail > splitMails( Mail mail )
	{
		ArrayList< String > toAddresses = mail.getTo();
		HashMap< String, ArrayList< String >> domainAddressMap = new HashMap< String, ArrayList< String >>();

		for ( String address : toAddresses )
		{
			String domain = MailServerHelper.getDomain( address );

			if ( !domainAddressMap.containsKey( domain ) )
			{
				ArrayList< String > toAddress = new ArrayList< String >();
				toAddress.add( address );
				domainAddressMap.put( domain, toAddress );
			}
			else
			{
				ArrayList< String > toAddress = domainAddressMap.get( domain );
				toAddress.add( address );
			}

		}

		ArrayList< Mail > mailList = new ArrayList< Mail >();

		for ( String domain : domainAddressMap.keySet() )
		{
			Mail newMail = mail.getClone();
			newMail.setToList( domainAddressMap.get( domain ) );
			newMail.setDomain( domain );
			mailList.add( newMail );
		}

		return mailList;

	}

	public void processMail()
	{
		for ( Mail mail : receivedMails )
		{
			ArrayList< Mail > mailList = splitMails( mail );
			mailProcessor.sendMail( mailList );
		}
	}

	public boolean isRetainKeyword()
	{
		return retainKeyword;
	}

	public void setRetainKeyword( boolean retainKeyword )
	{
		this.retainKeyword = retainKeyword;
	}

	public void setMailCompletion( boolean mailCompletion )
	{
		this.mailCompletion = mailCompletion;
	}

	public boolean isMailCompletion()
	{
		return mailCompletion;
	}

	public NetworkInteractor getInteractor()
	{
		return interactor;
	}

	@Override
	public void addCurrentMail()
	{
		receivedMails.add( currentMail );

	}
}
