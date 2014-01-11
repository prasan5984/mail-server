package implementations.pop;

import implementations.MailServer;
import implementations.PopMailStructure;
import implementations.keywords.properties.PopProperties;
import interfaces.Keyword;
import interfaces.Mail;
import interfaces.MailHelper;
import interfaces.MailProcessor;
import interfaces.MailReceiver;
import interfaces.NetworkInteractor;
import interfaces.account.UserManagement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import constants.MailServerConstants;

public class PopReceiver implements MailReceiver
{
	public static NetworkInteractor				interactor;
	Object										channelId;
	public static HashMap< String, Keyword >	keywordMap	= new HashMap< String, Keyword >();
	public static ArrayList< Mail >				mailList	= new ArrayList< Mail >();
	ArrayList< String >							tokens;
	private Keyword								lastKeyword;
	//private boolean								retainKeyword;
	//private int									mailState		= 0;
	MailHelper									helper;
	private Object								conId;
	public static MailProcessor					mailProcessor;
	//private boolean								mailCompletion	= false;
	private int									status;
	private static UserManagement				userManagement;

	// POP Related
	private String								curUser;
	private String								userCredentials;
	private PopMailStructure					mailStructure;

	public PopReceiver()
	{

	}

	public PopReceiver( Object channel )
	{
		channelId = channel;
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
					String trimLine = ( restLine.replaceAll( "\r\n$", "" ) ).replaceFirst( " ", "" );
					if (!trimLine.equals( "" ))
						tokens.add( trimLine );
				}
				return tokens;
			}

		}
		sendResponse( MailServerConstants.POP_ERR_CODE + " " + "Key not supported" );
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
		PopProperties properties = (PopProperties)key.getProperties();

		int currentKeyStatus = properties.getTransactionStatus();

		if ( currentKeyStatus > status )
		{
			sendResponse( MailServerConstants.POP_ERR_CODE + " " + "User not authenticated" );
			return null;

		}

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
			sendResponse( MailServerConstants.POP_ERR_CODE + " " + "Key not in sequence" );
		return null;

	}

	@Override
	public void initialize()

	{
		mailProcessor = MailServer.getPopMailProcessor();
		keywordMap = mailProcessor.getKeywordMap();
		interactor = MailServer.getInteractor();
		userManagement = MailServer.getUserManager();

	}

	@Override
	public void onConnecting()
	{
		sendResponse( MailServerConstants.POP_WELCOME_MESSAGE );

	}

	@Override
	public void sendResponse( String string )
	{
		interactor.sendMessage( channelId, string + "\r\n" );

	}

	@Override
	public Mail getMail()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setMail( Mail mail )
	{
		// TODO Auto-generated method stub

	}

	@Override
	public MailHelper getHelper()
	{
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public boolean isRetainKeyword()
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setRetainKeyword( boolean retainKeyword )
	{
		// TODO Auto-generated method stub

	}

	@Override
	public String getToken( int index )
	{
		if ( index < tokens.size() )
			return tokens.get( index );
		return null;

	}

	@Override
	public boolean isMailCompletion()
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setMailCompletion( boolean mailCompletion )
	{
		// TODO Auto-generated method stub

	}

	@Override
	public NetworkInteractor getInteractor()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCompletion()
	{
		interactor.onClose( conId );

	}

	public void setStatus( int status )
	{
		this.status = status;
	}

	public int getStatus()
	{
		return status;
	}

	public boolean checkUser( String user )
	{
		if ( userManagement.checkUser( user ) )
		{
			setCurUser( user );
			return true;
		}

		return false;
	}

	public String getUserCredentials()
	{
		userCredentials = userManagement.getUserCredentials( this.curUser );
		return userCredentials;
	}

	public PopMailStructure getUserMails()
	{
		if ( mailStructure == null )
			setMailStructure( userManagement.getUserMailDtls( this.curUser ) );
		return mailStructure;

	}

	public UserManagement getUserManagement()
	{
		return userManagement;
	}

	public void setMailStructure( PopMailStructure mailStructure )
	{
		this.mailStructure = mailStructure;
	}

	public void setUserCredentials( String userCredentials )
	{
		this.userCredentials = userCredentials;
	}

	public void setCurUser( String curUser )
	{
		this.curUser = curUser;
	}

	public String getCurUser()
	{
		return this.curUser;
	}

	@Override
	public void processMail()
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addCurrentMail()
	{
		// TODO Auto-generated method stub
		
	}

}
