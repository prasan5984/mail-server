package constants;

import java.nio.charset.Charset;
import java.util.HashMap;

public class MailServerConstants
{

	public MailServerConstants()
	{
		CODE_MSG_MAP.put( DEFAULT_SUCCESS_CODE, "OK" );
		CODE_MSG_MAP.put( USER_NOT_EXIST, "NO SUCH USER" );
		CODE_MSG_MAP.put( DATA_CODE, "Start mail input; end with <CRLF>.<CRLF>" );
		CODE_MSG_MAP.put( SEQ_ERROR_CODE, "Keyword sent is out of sequence" );
		CODE_MSG_MAP.put( UNKNOWN_CMD_CODE, "Command not recognized" );

	}

	public final static String					SMTP_WELCOME_MESSAGE	= "220 Connection established to mail server";
	public final static String					POP_WELCOME_MESSAGE		= "+OK Pop3 Server Ready";

	// Response Codes
	public final static int						DEFAULT_SUCCESS_CODE	= 250;
	public final static int						USER_NOT_EXIST			= 550;
	public final static int						DATA_CODE				= 354;
	public final static int						SEQ_ERROR_CODE			= 503;
	public final static int						UNKNOWN_CMD_CODE		= 500;

	// Code Message Map
	public static HashMap< Integer, String >	CODE_MSG_MAP			= new HashMap< Integer, String >();

	public static final String					DATA_TERMINATOR			= " . ";

	public static final String					KEYWORD_CONF_FILE		= "KeywordMap.conf";

	public static final String					POP_CONF_FILE			= "PopKeywordMap.conf";

	public static final String					PROPERTIES_FILE			= "Properties.conf";

	public static final boolean					TEST_FLAG				= true;

	public static final String					CREDENTIALS_FILE_NAME	= "Credentials.txt";

	public static final String					SESSION_FILE_NAME		= "SessionTransaction.txt";

	final public static Charset					CHARACTER_SET			= Charset.forName( "UTF-8" );

	public static final String					DEFAULT_MAIL_FILENAME	= "Mails.txt";

	public static final String					POP_ERR_CODE				= "-ERR";

}
