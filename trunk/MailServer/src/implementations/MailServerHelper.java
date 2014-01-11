package implementations;

import interfaces.MailHelper;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import constants.MailServerConstants;

public class MailServerHelper implements MailHelper
{

	public static String getResponseLine( int defaultSuccessCode, String msg )
	{
		return defaultSuccessCode + " " + msg;
	}

	public static boolean userExist( String user )
	{
		// To be implemented after User Account Management
		return true;
	}

	public static ArrayList< String > getKeywordClasses( String keywordFile )
	{
		LinkedHashMap< String, String > keywordClassMap = new LinkedHashMap< String, String >();
		try
		{
			MailServerHelper helper = new MailServerHelper();

			BufferedReader reader =
					new BufferedReader( new InputStreamReader( helper.getClass().getClassLoader().getResourceAsStream( keywordFile ) ) );
			String line = null;

			while ( ( line = reader.readLine() ) != null )
			{
				if ( line.matches( "^##.*" ) )
				{
					String[] args = line.split( "##" );
					keywordClassMap.put( args[ 1 ], args[ 2 ] );
				}
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

		return new ArrayList< String >( keywordClassMap.values() );
	}

	public static String getDomain( String address )
	{
		//address.replaceFirst( "<(.*)>", "$1" );
		String[] addressArray = address.split( "@", 2 );
		return addressArray[ 1 ];
	}

	public static ArrayList< String > tokenize( String line )
	{
		String[] parArray = line.split( " ", 2 );
		ArrayList< String > tokens = new ArrayList< String >( Arrays.asList( parArray ) );
		return tokens;
	}

	public static HashMap< String, String > loadProperties()
	{

		HashMap< String, String > propertyValueMap = new HashMap< String, String >();
		try
		{
			MailServerHelper helper = new MailServerHelper();

			BufferedReader reader =
					new BufferedReader( new InputStreamReader( helper.getClass().getClassLoader()
							.getResourceAsStream( MailServerConstants.PROPERTIES_FILE ) ) );
			String line = null;

			while ( ( line = reader.readLine() ) != null )
			{
				String[] args = line.split( "::" );
				propertyValueMap.put( args[ 0 ], args[ 1 ] );
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

		return propertyValueMap;

	}

	public static String getFormattedDirectory( String directory )
	{
		directory.replaceAll( "\\\\", "/" );

		if ( !directory.endsWith( "/" ) )
			return directory + "/";

		return directory;
	}

	public static Map< String, String > getContentAsMap( String content )
	{
		HashMap< String, String > userMap = new HashMap< String, String >();

		for ( String str : content.split( "\n" ) )
		{
			String[] userArray = str.split( " ", 2 );
			userMap.put( userArray[ 0 ], userArray[ 1 ] );

		}

		return userMap;
	}

}
