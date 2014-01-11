package implementations.account;

import implementations.MailServer;
import implementations.MailServerHelper;
import implementations.PopMailStructure;
import interfaces.Keyword;
import interfaces.Mail;
import interfaces.MailHandler;
import interfaces.NetworkInteractor;
import interfaces.account.UserManagement;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import constants.MailServerConstants;

public class MailUserManagement implements UserManagement
{

	private Map< String, String >			userCredentialsMap	= new HashMap< String, String >();
	private NetworkInteractor				interactor;
	private File							file, sessionFile;
	private String							serverDirectory;
	private Map< String, ReentrantLock >	userLocks			= new HashMap< String, ReentrantLock >();

	@Override
	public void initialize()
	{
		loadUserDetails();
		interactor = MailServer.getInteractor();
	}

	@Override
	public void onRead( Object ip, String msg )
	{
		String[] msgArray = msg.split( " " );

		String user = null;
		String password = null;

		

		if ( msgArray[ 0 ].equals( "CREATE" ) )
		{
			user = ( msgArray[ 1 ] ).toUpperCase();
			password = msgArray[ 2 ];
			
			synchronized (userCredentialsMap)
			{
				if ( userCredentialsMap.containsKey( user ) )
				{
					sendResponse( ip, "ERR|Username Not Available" );
				}
				else
				{
					addUpdateUser( user, password );
					createUserDir( user );
					sendResponse( ip, "OK" );
				}
			}
		}
		else if ( msgArray[ 0 ].equals( "EDIT" ) )
		{
			user = ( msgArray[ 2 ] ).toUpperCase();
			password = msgArray[ 3 ];			
			String newPassword = msgArray[ 4 ];
			synchronized (userCredentialsMap)
			{
				if ( !msgArray[ 1 ].equals( "CHGPASS" ) )
				{
					sendResponse( ip, "ERR|Sub Argument Not Recognized" );
				}
				else
				{
					if ( !userCredentialsMap.containsKey( user ) )
					{
						sendResponse( ip, "ERR|User not found" );
						return;
					}

					if ( !userCredentialsMap.get( user ).equals( password ) )
					{
						sendResponse( ip, "ERR|Invalid Username/Password" );
						return;
					}

					addUpdateUser( user, newPassword );
					sendResponse( ip, "OK" );
				}
			}
		}
		else if ( msgArray[ 0 ].equals( "QUIT" ) )
		{
			interactor.onClose( ip );
		}

		else
		{
			sendResponse( ip, "Argument Not Recognized" );
		}
	}

	private String createUserDir( String user )
	{

		String userDir = serverDirectory + "\\" + user;
		File userDirectory = new File( userDir );
		userDirectory.mkdir();
		return userDir;
	}

	private void loadUserDetails()
	{
		serverDirectory = /*MailServerHelper.getFormattedDirectory( MailServer.propertyValueMap.get( "MAIL_SERVER_ROOT_FOLDER" ) );*/
		MailServer.propertyValueMap.get( "MAIL_SERVER_ROOT_FOLDER" );

		String filePath = serverDirectory + "/" + MailServerConstants.CREDENTIALS_FILE_NAME;
		String sessionFilePath = serverDirectory + "/" + MailServerConstants.SESSION_FILE_NAME;

		file = new File( filePath );
		sessionFile = new File( sessionFilePath );

		Map< String, String > userMap = new HashMap< String, String >();

		try
		{
			if ( file.exists() )
			{
				FileInputStream fInputStream = new FileInputStream( file );
				FileChannel fCh = fInputStream.getChannel();

				long fileLongSize = file.length();
				int fileSize = (int)fileLongSize;

				if ( fileSize > 0 )
				{
					ByteBuffer byteBuffer = ByteBuffer.allocate( fileSize );
					fCh.read( byteBuffer );

					String fileContent = readMessage( byteBuffer );
					userCredentialsMap = MailServerHelper.getContentAsMap( fileContent );
				}

				fCh.close();
			}

			else
			{
				file.createNewFile();
			}

			if ( sessionFile.exists() )
			{

				FileInputStream sessionInputStream = new FileInputStream( sessionFile );
				FileChannel sessionChannel = sessionInputStream.getChannel();

				int size = (int)sessionFile.length();

				if ( size > 0 )
				{

					ByteBuffer sessionFileBuffer = ByteBuffer.allocate( size );
					sessionChannel.read( sessionFileBuffer );

					String sessionFileContent = readMessage( sessionFileBuffer );
					processSessionFile( sessionFileContent );
					saveCredentialsFile();
				}
				sessionChannel.close();
				//sessionInputStream.close();

				sessionFile.delete();

			}

		}
		catch ( FileNotFoundException e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch ( IOException e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void sendResponse( Object ip, String msg )
	{
		interactor.sendMessage( ip, msg );
	}

	private void addUpdateUser( String rawUser, String pass )
	{
		String user = rawUser.toUpperCase();

		if ( userCredentialsMap.containsKey( user ) )
		{
			userCredentialsMap.remove( user );
		}

		userCredentialsMap.put( user, pass );
		String content = user + " " + pass + "\n";
		writeFile( sessionFile, true, content );

	}

	private void processSessionFile( String content )
	{
		String[] transactionList = content.split( "\n" );

		for ( String str : transactionList )
		{
			String[] userArray = str.split( " " );
			String uName = userArray[ 0 ];
			String pass = userArray[ 1 ];

			if ( userCredentialsMap.containsKey( uName ) )
			{
				userCredentialsMap.remove( uName );
			}

			userCredentialsMap.put( uName, pass );

		}

	}

	private void saveCredentialsFile()
	{
		String content = "";
		for ( String str : userCredentialsMap.keySet() )
		{
			content = content + str + " " + userCredentialsMap.get( str ) + "\n";
		}

		writeFile( file, false, content );

	}

	private void writeFile( File file, Boolean append, String content )
	{
		FileOutputStream fileOpStream;
		try
		{
			file.createNewFile();

			fileOpStream = new FileOutputStream( file, append );
			FileChannel fileCh = fileOpStream.getChannel();

			CharBuffer chBuf = CharBuffer.wrap( content );
			ByteBuffer buf = Charset.forName( "UTF-8" ).encode( chBuf );

			fileCh.write( buf );
			fileCh.close();
		}
		catch ( FileNotFoundException e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch ( IOException e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private String readMessage( ByteBuffer byteBuf )
	{
		byteBuf.flip();
		String msg = MailServerConstants.CHARACTER_SET.decode( byteBuf ).toString();
		byteBuf.clear();
		return msg;

	}

	@Override
	public boolean checkUser( String user )
	{
		if ( userCredentialsMap.containsKey( user.toUpperCase() ) )
			return true;
		return false;
	}

	@Override
	public void saveMail( String user, String msg )
	{
		String userDir = createUserDir( user.toUpperCase() );
		File file = new File( userDir + "\\" + MailServerConstants.DEFAULT_MAIL_FILENAME );
		writeFile( file, true, msg );

	}

	@Override
	public void onAccept( Object socketAddress )
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
	public void setInteractor( NetworkInteractor interactor )
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void registerSender( Object domain, MailHandler handler )
	{
		// TODO Auto-generated method stub

	}

	@Override
	public LinkedHashMap< String, Keyword > getKeywordMap()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void sendMail( ArrayList< Mail > mailList )
	{
		// TODO Auto-generated method stub

	}

	@Override
	public String getUserCredentials( String user )
	{
		return userCredentialsMap.get( user.toUpperCase() );
	}

	@Override
	public PopMailStructure getUserMailDtls( String user )

	{
		String userCaps = user.toUpperCase();

		String filePath = serverDirectory + "/" + userCaps + "/" + MailServerConstants.DEFAULT_MAIL_FILENAME;
		File file = new File( filePath );

		int fileSize = (int)file.length();

		String content = "";
		int mailCount = 0, totSize = 0;
		LinkedHashMap< String, Integer > mailSizeMap = new LinkedHashMap< String, Integer >();

		if ( fileSize > 0 )
		{
			content = readFile( file );
			String[] mail = content.split( "\r\n\\.\r\n" );
			mailCount = mail.length;

			for ( int i = 0; i < mail.length; i++ )
			{
				int mailSize = getMailSize( mail[ i ] );
				totSize = mailSize + totSize;
				mailSizeMap.put( mail[ i ], mailSize );

			}
		}

		PopMailStructure mailStructure = new PopMailStructure();

		mailStructure.setUser( userCaps );
		mailStructure.setMailCount( mailCount );
		mailStructure.setMailSizeMap( mailSizeMap );
		mailStructure.setTotMailSize( totSize );

		return mailStructure;
	}

	private int getMailSize( String mail )
	{
		String trimmedMail = ( mail.replaceFirst( "^\\.", "" ) ).replaceAll( "\r\n\\.", "\r\n" );
		byte[] mailByte = trimmedMail.getBytes();
		return mailByte.length;
	}

	private ReentrantLock getUserLock( String user )
	{
		String userCaps = user.toUpperCase();

		synchronized (userLocks)
		{
			if ( userLocks.containsKey( userCaps ) )
				return userLocks.get( userCaps );

			ReentrantLock lock = new ReentrantLock();
			userLocks.put( userCaps, lock );
			return lock;
		}

	}

	@Override
	public void lockUser( String user )
	{
		getUserLock( user ).lock();

	}

	@Override
	public void releaseUser( String user )
	{
		getUserLock( user.toUpperCase() ).unlock();

	}

	private String readFile( File file )
	{
		String fileContent = "";
		try
		{
			if ( file.exists() )
			{
				FileInputStream fInputStream;
				fInputStream = new FileInputStream( file );
				FileChannel fCh = fInputStream.getChannel();

				long fileLongSize = file.length();
				int fileSize = (int)fileLongSize;

				if ( fileSize > 0 )
				{
					ByteBuffer byteBuffer = ByteBuffer.allocate( fileSize );
					fCh.read( byteBuffer );
					fileContent = readMessage( byteBuffer );
				}

				fCh.close();
			}
		}
		catch ( FileNotFoundException e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch ( IOException e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return fileContent;
	}

	@Override
	public void updateUser( PopMailStructure mailStructure )
	{
		String user = mailStructure.getUser().toUpperCase();
		ArrayList< String > mailList = mailStructure.getMailList();
		ArrayList< Integer > markedMails = mailStructure.getMarkedForDeletion();

		if ( markedMails.size() > 0 )
		{
			Iterator< String > iterator = mailList.iterator();
			int k = mailList.size();
			for ( int j = 0; j < k; j++ )
			{
				iterator.next();
				for ( int i : markedMails )
					if ( i == j )
						iterator.remove();

			}

			String filePath = serverDirectory + "/" + user + "/" + MailServerConstants.DEFAULT_MAIL_FILENAME;
			File file = new File( filePath );

			String content = "";

			for ( String str : mailList )
				content = str + "\r\n.\r\n";

			writeFile( file, false, content );
		}
	}
}
