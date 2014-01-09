package implementations;

import interfaces.Keyword;
import interfaces.Mail;
import interfaces.MailHelper;
import interfaces.MailProcessor;
import interfaces.MailReceiver;
import interfaces.MailHandler;
import interfaces.NetworkInteractor;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import constants.MailServerConstants;

public class SMTPMailProcessor implements MailProcessor
{

	private Map< Object, MailHandler >				handlerMap;
	private NetworkInteractor						interactor;
	public static LinkedHashMap< String, Keyword >	keywordMap	= new LinkedHashMap< String, Keyword >();

	@Override
	public void onAccept( Object ip )
	{
		if ( !handlerMap.containsKey( ip ) )
		{
			MailReceiver transactionHandler = new SMTPReceiver( ip );
			transactionHandler.onConnecting();
			handlerMap.put( ip, transactionHandler );
		}
	}

	@Override
	public void onRead( Object ip, String msg )
	{
		if ( handlerMap.containsKey( ip ) )
		{
			MailHandler transactionHandler = handlerMap.get( ip );
			transactionHandler.processMailTransaction( msg );
		}
	}

	@Override
	public void registerSender( Object ip, MailHandler handler )
	{
		handlerMap.put( ip, handler );
	}

	@Override
	public void sendMail( ArrayList< Mail > mailList )
	{

		for ( Mail mail : mailList )
		{
			String domain = mail.getDomain();
			if ( domain.equalsIgnoreCase( MailServerConstants.LOCAL_DOMAIN_NAME ) )
			{
				SMTPSaver saver = new SMTPSaver( mail );
				//MailServer.getExecutorService().execute( saver );
				saver.run();

			}
			else
			{
				SMTPSender sender = new SMTPSender( mail );
				//MailServer.getExecutorService().execute( sender );
				sender.run();
			}
		}
	}

	public NetworkInteractor getInteractor()
	{
		return interactor;
	}

	public void setInteractor( NetworkInteractor interactor )
	{
		this.interactor = interactor;
	}

	@Override
	public void initialize()
	{
		setKeywordMap();
		handlerMap = Collections.synchronizedMap( new HashMap< Object, MailHandler >() );
		( new SMTPReceiver() ).initialize();
		( new SMTPSender() ).initialize();
		interactor = MailServer.getInteractor();

	}

	@Override
	public void sendResponse( Object ip, String msg )
	{
		MailServer.getInteractor().sendMessage( ip, msg );
	}

	public void setKeywordMap()
	{
		ArrayList< String > classNames = MailServerHelper.getKeywordClasses(MailServerConstants.KEYWORD_CONF_FILE);

		for ( String className : classNames )
		{

			Keyword key = null;
			try
			{
				key = (Keyword)Class.forName( className ).getConstructor().newInstance();
			}
			catch ( IllegalArgumentException e )
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch ( SecurityException e )
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch ( InstantiationException e )
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch ( IllegalAccessException e )
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch ( InvocationTargetException e )
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch ( NoSuchMethodException e )
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch ( ClassNotFoundException e )
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			/*try {
				key = (Keyword) Class.forName(className).getConstructor().newInstance();
			} 
			catch (ClassNotFoundException | InstantiationException
					| IllegalAccessException | IllegalArgumentException
					| InvocationTargetException | NoSuchMethodException
					| SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
			keywordMap.put( key.getName(), key );
		}
	}

	@Override
	public LinkedHashMap< String, Keyword > getKeywordMap()
	{
		return keywordMap;
	}

}
