package implementations.pop;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import constants.MailServerConstants;

import implementations.MailServer;
import implementations.MailServerHelper;
import interfaces.Keyword;
import interfaces.Mail;
import interfaces.MailHandler;
import interfaces.MailProcessor;
import interfaces.NetworkInteractor;

public class PopProcessor implements MailProcessor
{

	private Map< Object, MailHandler >				handlerMap;
	private NetworkInteractor						interactor;
	public static LinkedHashMap< String, Keyword >	keywordMap	= new LinkedHashMap< String, Keyword >();

	@Override
	public void initialize()
	{
		setKeywordMap();

		handlerMap = Collections.synchronizedMap( new HashMap< Object, MailHandler >() );
		( new PopReceiver() ).initialize();
		interactor = MailServer.getInteractor();

	}

	@Override
	public void onAccept( Object socketAddress )
	{
		if ( !handlerMap.containsKey( socketAddress ) )
		{
			PopReceiver receiver = new PopReceiver( socketAddress );
			handlerMap.put( socketAddress, receiver );
			receiver.onConnecting();
		}

	}

	@Override
	public void onRead( Object socketAddress, String msg )
	{
		if ( handlerMap.containsKey( socketAddress ) )
			( handlerMap.get( socketAddress ) ).processMailTransaction( msg );

	}

	@Override
	public NetworkInteractor getInteractor()
	{
		return interactor;
	}

	@Override
	public void setInteractor( NetworkInteractor interactor )
	{
		this.interactor = interactor;

	}

	@Override
	public void registerSender( Object domain, MailHandler handler )
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void sendResponse( Object ip, String msg )
	{
		getInteractor().sendMessage( ip, msg );

	}

	@Override
	public LinkedHashMap< String, Keyword > getKeywordMap()
	{

		return keywordMap;
	}

	@Override
	public void sendMail( ArrayList< Mail > mailList )
	{
		// TODO Auto-generated method stub

	}

	public void setKeywordMap()
	{
		ArrayList< String > classNames = MailServerHelper.getKeywordClasses( MailServerConstants.POP_CONF_FILE );

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
}
