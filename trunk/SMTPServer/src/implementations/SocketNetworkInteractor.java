package implementations;

import interfaces.MailProcessor;
import interfaces.NetworkInteractor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class SocketNetworkInteractor implements NetworkInteractor
{

	private static ServerSocketChannel	srvChannel, popChannel, userChannel;
	private static ServerSocket			incomingSocket, popSocket;
	final public static Charset			CHARACTER_SET			= Charset.forName( "UTF-8" );
	public static String				serverAddress			= "127.0.0.1";
	public static int					serverPort				= 25;
	private OutgoingConnector			outConnector;
	private IncomingConnector			inConnector;
	private List< SocketChannel >		readSockets				= new ArrayList< SocketChannel >();
	private List< SocketChannel >		readOutSockets			= new ArrayList< SocketChannel >();
	private List< SocketChannel >		closeSockets			= new ArrayList< SocketChannel >();
	private List< SocketChannel >		clientRequestSockets	= new ArrayList< SocketChannel >();
	public static int					clientRequestPort		= 9000;
	private static int					popPort					= 110;

	public void startServer()
	{

		try
		{
			srvChannel = ServerSocketChannel.open();
			srvChannel.configureBlocking( false );
			incomingSocket = srvChannel.socket();
			InetSocketAddress address = new InetSocketAddress( serverPort );
			incomingSocket.bind( address );

		}
		catch ( IOException e )
		{
			System.out.println( "Unable to start the server on port: " + serverPort );
			e.printStackTrace();
		}

		try
		{
			popChannel = ServerSocketChannel.open();
			popChannel.configureBlocking( false );
			popSocket = popChannel.socket();
			popSocket.bind( new InetSocketAddress( popPort ) );

		}
		catch ( IOException e )
		{
			System.out.println( "Unable to start the server on port: " + popPort );
			e.printStackTrace();
		}

		readSockets = Collections.synchronizedList( readSockets );

		// Incoming Listener

		MailProcessor mailProcessor = MailServer.getMailProcessor();

		inConnector = new IncomingConnector( mailProcessor, srvChannel );
		( new Thread( inConnector ) ).start();

		MailProcessor popProcessor = MailServer.getPopMailProcessor();

		//POP3 Listener
		IncomingConnector popListner = new IncomingConnector( popProcessor, popChannel );
		( new Thread( popListner ) ).start();

		// Outgoing Listener
		outConnector = new OutgoingConnector( mailProcessor );
		( new Thread( outConnector ) ).start();

		try
		{
			userChannel = ServerSocketChannel.open();
			userChannel.configureBlocking( false );
			ServerSocket userSocket = userChannel.socket();

			InetSocketAddress clientRequestAddress = new InetSocketAddress( clientRequestPort );
			userSocket.bind( clientRequestAddress );

		}
		catch ( IOException e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Client Request Listener
		MailServer.getUserManager();
		IncomingConnector clientListener = new IncomingConnector( MailServer.getUserManager(), userChannel );
		( new Thread( clientListener ) ).start();

	}

	public void sendMessage( Object obj, String msg )
	{
		SocketChannel ch = (SocketChannel)obj;

		CharsetEncoder encoder = CHARACTER_SET.newEncoder();

		CharBuffer charBuf = CharBuffer.wrap( msg );
		ByteBuffer byteBuf = null;
		try
		{
			byteBuf = encoder.encode( charBuf );
		}
		catch ( CharacterCodingException e1 )
		{
			e1.printStackTrace();
		}

		if ( ch != null )
		{
			synchronized (ch)
			{
				try
				{
					ch.write( byteBuf );
				}
				catch ( IOException e )
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}

	@Override
	public Object initiateConnection( String domain )
	{
		/*		InetAddress ip = null;
				try
				{
					ip = InetAddress.getByName( domain );
				}
				catch ( UnknownHostException e1 )
				{
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}*/
		InetSocketAddress inetaddr = new InetSocketAddress( domain, 25 );
		SocketChannel channel = null;
		try
		{
			channel = SocketChannel.open();
			channel.connect( inetaddr );
			channel.configureBlocking( false );
			outConnector.registerChannel( (SocketChannel)channel );
			//MailServer.getOutgoingExecutorService().execute( outConnector );

		}
		catch ( IOException e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return channel;
		//return null;

	}

	class NetworkProcessorThread implements Runnable
	{
		private Object			parameter;
		private int				actionType;
		private MailProcessor	processor;

		public NetworkProcessorThread( MailProcessor mailProcessor, int actType, Object parameter )
		{
			actionType = actType;
			this.parameter = parameter;
			this.processor = mailProcessor;
		}

		public void run()
		{

			if ( actionType == 0 )
			{
				processor.onAccept( parameter );

			}
			else
			//if ( actionType == 1 )
			{
				SocketChannel channel = (SocketChannel)parameter;

				CharsetDecoder decoder = CHARACTER_SET.newDecoder();
				ByteBuffer byteBuf = ByteBuffer.allocate( 1024 );

				String msg = "";
				int r;

				try
				{

					while ( true )
					{

						r = channel.read( byteBuf );

						if ( r <= 0 )
							break;

						byteBuf.flip();
						msg = msg + decoder.decode( byteBuf ).toString();
						break;
					}
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
					//e.printStackTrace();

				}
				finally
				{
					if ( msg == "" )
						onClose( channel );

					if ( readSockets.contains( channel ) )
						readSockets.remove( channel );

					if ( readOutSockets.contains( channel ) )
						readOutSockets.remove( channel );

					if ( clientRequestSockets.contains( channel ) )
						clientRequestSockets.remove( channel );

				}

				if ( msg != null )
					if ( msg.length() != 0 )
					{
						processor.onRead( channel, msg );

						/*if ( actionType == 1 )
							MailServer.getMailProcessor().onRead( channel, msg );
						else
							MailServer.getUserManager().onRead( channel, msg );*/
					}
			}

		}
	}

	class IncomingConnector implements Runnable
	{

		Selector			selector;
		ServerSocketChannel	serverSocket;
		MailProcessor		mailProcessor;

		IncomingConnector( MailProcessor mailProcessor, ServerSocketChannel serverSocket )
		{
			this.serverSocket = serverSocket;

			try
			{
				selector = Selector.open();
				this.serverSocket.register( selector, SelectionKey.OP_ACCEPT );
				this.mailProcessor = mailProcessor;
			}
			catch ( IOException e )
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		@Override
		public void run()
		{
			try
			{

				while ( true )
				{

					selector.select();

					Set selectedKeys = selector.selectedKeys();
					Iterator iterator = selectedKeys.iterator();

					while ( iterator.hasNext() )
					{
						SelectionKey selectionKey = (SelectionKey)iterator.next();
						SocketChannel channel = null;

						if ( ( selectionKey.readyOps() & SelectionKey.OP_ACCEPT ) == SelectionKey.OP_ACCEPT )
						{
							ServerSocketChannel key = (ServerSocketChannel)selectionKey.channel();

							channel = key.accept();

							if ( checkAndCloseSocket( channel ) )
							{
								iterator.remove();
								continue;
							}

							channel.configureBlocking( false );
							channel.register( selector, SelectionKey.OP_READ );

							NetworkProcessorThread processThread = new NetworkProcessorThread( mailProcessor, 0, channel );
							MailServer.getIncomingExecutorService().execute( processThread );

							iterator.remove();

						}
						else if ( ( selectionKey.readyOps() & SelectionKey.OP_READ ) == SelectionKey.OP_READ )

						{
							channel = (SocketChannel)selectionKey.channel();

							if ( checkAndCloseSocket( channel ) )
							{
								iterator.remove();
								continue;
							}

							if ( !readSockets.contains( channel ) )
							{
								readSockets.add( channel );
								NetworkProcessorThread processThread = new NetworkProcessorThread( mailProcessor, 1, channel );
								MailServer.getIncomingExecutorService().execute( processThread );
							}

							iterator.remove();
						}

					}

				}

			}
			catch ( IOException e )
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	class OutgoingConnector implements Runnable
	{

		Selector		selector;
		MailProcessor	mailProcessor;

		OutgoingConnector( MailProcessor processor )
		{
			mailProcessor = processor;
			try
			{
				selector = Selector.open();
			}
			catch ( IOException e )
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		@Override
		public void run()
		{
			try
			{

				while ( true )
				{

					synchronized (selector)
					{
						selector.select( 500 );

						Set selectedKeys = selector.selectedKeys();
						Iterator iterator = selectedKeys.iterator();

						while ( iterator.hasNext() )
						{
							SelectionKey selectionKey = (SelectionKey)iterator.next();
							SocketChannel channel = null;

							/*if ( ( selectionKey.readyOps() & SelectionKey.OP_CONNECT ) == SelectionKey.OP_CONNECT )
							{
								SocketChannel key = (SocketChannel)selectionKey.channel();

								channel = key;

								//channel.configureBlocking( false );
								//channel.register( selector, SelectionKey.OP_READ );
								NetworkProcessorThread processThread = new NetworkProcessorThread( 1, channel );
								MailServer.getExecutorService().submit( processThread );

								iterator.remove();

							}
							else */if ( ( selectionKey.readyOps() & SelectionKey.OP_READ ) == SelectionKey.OP_READ )

							{
								channel = (SocketChannel)selectionKey.channel();

								if ( checkAndCloseSocket( channel ) )
								{
									iterator.remove();
									continue;
								}

								if ( !readOutSockets.contains( channel ) )
								{
									readOutSockets.add( channel );
									NetworkProcessorThread processThread = new NetworkProcessorThread( mailProcessor, 1, channel );
									MailServer.getOutgoingExecutorService().execute( processThread );
								}
								iterator.remove();
							}

						}
					}
				}

			}
			catch ( IOException e )
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		public void registerChannel( SocketChannel channel )
		{
			selector.wakeup();
			synchronized (selector)
			{
				try
				{
					//channel.register( selector, SelectionKey.OP_CONNECT );
					channel.register( selector, SelectionKey.OP_READ );
				}
				catch ( ClosedChannelException e )
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}

	}

	private boolean checkAndCloseSocket( SocketChannel socket )
	{
		synchronized (closeSockets)
		{
			if ( closeSockets.contains( socket ) )
			{
				try
				{
					socket.close();
				}
				catch ( IOException e )
				{

				}
				closeSockets.remove( socket );
				return true;
			}
		}

		return false;
	}

	@Override
	public void onClose( Object socket )
	{
		SocketChannel ch = (SocketChannel)socket;
		if ( !closeSockets.contains( ch ) )
			synchronized (closeSockets)
			{
				closeSockets.add( ch );
			}

	}

}