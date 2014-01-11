package implementations;

import interfaces.Mail;

import java.util.ArrayList;

public class SMTPMail implements Mail
{
	private String				mailFrom	= null;
	private ArrayList< String >	mailTo		= new ArrayList< String >();
	private String				data		= null;
	private String				domain		= null;

	@Override
	public void setFrom( String from )
	{
		mailFrom = from;
	}

	@Override
	public void setTo( String to )
	{
		mailTo.add( to );
	}

	@Override
	public void setData( String data )
	{
		this.data = data;
	}

	@Override
	public String getFrom()
	{
		return mailFrom;
	}

	@Override
	public ArrayList< String > getTo()
	{
		return mailTo;
	}

	@Override
	public String getDomain()
	{
		return domain;
	}

	@Override
	public String getData()
	{
		return data;
	}

	@Override
	public Mail getClone()
	{
		Mail mail = new SMTPMail();
		mail.setFrom( this.mailFrom );
		mail.setToList( this.mailTo );
		mail.setDomain( this.domain );
		mail.setData( this.data );
		return mail;

	}

	@Override
	public void setToList( ArrayList< String > addList )
	{
		mailTo = addList;
	}

	@Override
	public void setDomain( String domain )
	{
		this.domain = domain;

	}

}
