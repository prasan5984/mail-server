package src.main;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.Scanner;

public class MailClient
{

	private String			serverIp	= "10.113.59.124";
	private int				serverPort	= 9000;
	private CharsetDecoder	decoder		= Charset.forName( "UTF-8" ).newDecoder();
	private CharsetEncoder	encoder		= Charset.forName( "UTF-8" ).newEncoder();
	private SocketChannel	channel;
	private String			input		= "0";
	private String			user, pass, newPass;
	private Socket			clientSocket;

	public static void main( String[] args )
	{
		( new MailClient() ).startClient();

	}

	public void startClient()
	{
		printOptions();
		input = readLine();

		while ( !input.equals( "1" ) && !input.equals( "2" ) )
		{
			System.out.println( "Invalid option" );
			printOptions();
			input = readLine();
		}

		readEntities();

	}

	private void readEntities()
	{
		user = readEntity( "Username" );
		if ( input.equals( "1" ) )
			pass = readEntity( "Password" );
		else
		{
			pass = readEntity( "Current Password" );
			newPass = readEntity( "New Password" );
		}
		startServerCommunication();
	}

	private void startServerCommunication()
	{
		initiateConnection();

		if ( input.equals( "1" ) )
			sendMessage( "CREATE " + user + " " + pass );
		else
			sendMessage( "EDIT CHGPASS " + user + " " + pass + " " + newPass );

		String[] msg = splitMessage( readMessage() );

		sendMessage( "Quit" );

		try
		{
			channel.close();
		}
		catch ( IOException e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if ( msg[ 0 ].equals( "ERR" ) )
		{
			System.out.println( msg[ 1 ] );
			readEntities();
		}
		else
		{
			System.out.println( "Action Completed" );
		}

	}

	private String readEntity( String entityName )
	{
		boolean validationFl = false;
		String entity = null;

		while ( !validationFl )
		{
			System.out.println( "Enter " + entityName );
			entity = readLine();
			validationFl = validate( entity );

			if ( !validationFl )
				System.out.println( entityName + " can contain only alphabets and digits" );
		}
		return entity;

	}

	private void printOptions()
	{
		System.out.println( "Choose an option:\n1. New User\n2.Edit User" );
	}

	private String readLine()
	{
		Scanner scanner = new Scanner( System.in );
		String input = scanner.nextLine();
		return input;
	}

	private boolean validate( String entity )
	{
		if ( !entity.matches( "[A-Za-z0-9]*" ) )
			return false;
		return true;
	}

	private void initiateConnection()
	{
		try
		{
			SocketAddress inetaddr = new InetSocketAddress( serverIp, serverPort );
			channel = SocketChannel.open();
			clientSocket = channel.socket();
			clientSocket.connect( inetaddr );
		}
		catch ( IOException e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private String readMessage()
	{

		ByteBuffer byteBuf = ByteBuffer.allocate( 1024 );
		String msg = "";

		try
		{
			channel.read( byteBuf );
			byteBuf.flip();
			msg = decoder.decode( byteBuf ).toString();
			byteBuf.clear();
		}
		catch ( CharacterCodingException e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();

		}
		catch ( IOException e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return msg;

	}

	private void sendMessage( String msg )
	{

		CharBuffer charBuf = CharBuffer.wrap( msg );
		ByteBuffer byteBuf = null;

		try
		{
			byteBuf = encoder.encode( charBuf );
			channel.write( byteBuf );
		}
		catch ( CharacterCodingException e1 )
		{
			e1.printStackTrace();
		}
		catch ( IOException e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private String[] splitMessage( String msg )
	{
		return msg.split( "\\|" );
	}

}
