package implementations;

import interfaces.Mail;

public class SMTPSaver implements Runnable
{

	private Mail	mailObject;

	public SMTPSaver( Mail mail )
	{
		mailObject = mail;
	}

	public SMTPSaver()
	{
		// TODO Auto-generated constructor stub
	}

	private void saveMailLocally()
	{
		for ( String toAddress : mailObject.getTo() )
		{
			String[] toAddressStrings = toAddress.split( "@" );
			String localUser = toAddressStrings[ 0 ];

			if ( MailServer.getUserManager().checkUser( localUser ) )
				MailServer.getUserManager().saveMail( localUser, mailObject.getData() );
		}

	}

	@Override
	public void run()
	{
		saveMailLocally();
	}

}
