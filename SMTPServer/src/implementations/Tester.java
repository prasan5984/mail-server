package implementations;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

public class Tester
{
	public static void main( String[] args )
	{

		MailServer.main( null );
		SocketNetworkInteractor interactor = (SocketNetworkInteractor)MailServer.getInteractor();
		SocketChannel ch = (SocketChannel)interactor.initiateConnection( "mailtrap.io" );
		
		CharsetEncoder encoder = Charset.forName( "UTF-8" ).newEncoder();

		CharBuffer charBuf = CharBuffer.wrap( "Ehlo\r\n" );
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

}
