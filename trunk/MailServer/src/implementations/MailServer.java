package implementations;

import implementations.account.MailUserManagement;
import implementations.pop.PopProcessor;
import interfaces.MailProcessor;
import interfaces.NetworkInteractor;
import interfaces.account.UserManagement;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import constants.MailServerConstants;

public class MailServer
{

	private static NetworkInteractor		interactor;
	private static MailProcessor			smtpMailProcessor, popMailProcessor;
	public static HashMap< String, String >	propertyValueMap	= new HashMap< String, String >();
	private static ExecutorService			incomingExecutorService, outgoingExecutorService, senderExecutorService;
	private static UserManagement			userManager;
	private static int						ClientListenerPort;
	private static String					localDomainName;

	public static void initialize()
	{

		setUserManager( new MailUserManagement() );
		propertyValueMap = MailServerHelper.loadProperties();
		incomingExecutorService = Executors.newFixedThreadPool( Integer.parseInt( propertyValueMap.get( "THREAD_COUNT" ) ) );
		outgoingExecutorService = Executors.newFixedThreadPool( Integer.parseInt( propertyValueMap.get( "THREAD_COUNT" ) ) );
		setSenderExecutorService( Executors.newFixedThreadPool( Integer.parseInt( propertyValueMap.get( "THREAD_COUNT" ) ) ) );
		setClientListenerPort( Integer.parseInt( propertyValueMap.get( "CLIENT_LISTENER_PORT" ) ) );
		setLocalDomainName( propertyValueMap.get( "LOCAL_DOMAIN_NAME" ) );

		interactor = new SocketNetworkInteractor();
		smtpMailProcessor = new SMTPMailProcessor();
		smtpMailProcessor.initialize();

		setPopMailProcessor( new PopProcessor() );
		getPopMailProcessor().initialize();
		getUserManager().initialize();

		new MailServerConstants();

	}

	public static ExecutorService getIncomingExecutorService()
	{
		return incomingExecutorService;
	}

	public static ExecutorService getOutgoingExecutorService()
	{
		return outgoingExecutorService;
	}

	public static NetworkInteractor getInteractor()
	{
		return interactor;
	}

	public static MailProcessor getMailProcessor()
	{
		return smtpMailProcessor;
	}

	public static void main( String[] args )
	{
		initialize();
		interactor.startServer();
	}

	public static void setUserManager( UserManagement userManager )
	{
		MailServer.userManager = userManager;
	}

	public static UserManagement getUserManager()
	{
		return userManager;
	}

	public static void setPopMailProcessor( MailProcessor popMailProcessor )
	{
		MailServer.popMailProcessor = popMailProcessor;
	}

	public static MailProcessor getPopMailProcessor()
	{
		return popMailProcessor;
	}

	public static void setSenderExecutorService( ExecutorService senderExecutorService )
	{
		MailServer.senderExecutorService = senderExecutorService;
	}

	public static ExecutorService getSenderExecutorService()
	{
		return senderExecutorService;
	}

	public static void setClientListenerPort( int clientListenerPort )
	{
		ClientListenerPort = clientListenerPort;
	}

	public static int getClientListenerPort()
	{
		return ClientListenerPort;
	}

	public static void setLocalDomainName( String localDomainName )
	{
		MailServer.localDomainName = localDomainName;
	}

	public static String getLocalDomainName()
	{
		return localDomainName;
	}

}
