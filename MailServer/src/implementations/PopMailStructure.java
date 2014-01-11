package implementations;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class PopMailStructure
{
	private String								user;
	private int									mailCount;
	private ArrayList< String >					mailList;
	private ArrayList< Integer >				markedForDeletion	= new ArrayList< Integer >();
	private ArrayList< Integer >				mailSizeList;
	private LinkedHashMap< String, Integer >	mailSizeMap;
	private int									totMailSize;

	public void setMailCount( int mailCount )
	{
		this.mailCount = mailCount;
	}

	public int getMailCount()
	{
		return mailCount;
	}

	public ArrayList< String > getMailList()
	{
		return mailList;
	}

	public void markMessageNoForDeletion( Integer messageNo )
	{
		markedForDeletion.add( messageNo );
	}

	public ArrayList< Integer > getMarkedForDeletion()
	{
		return markedForDeletion;
	}

	public void setUser( String user )
	{
		this.user = user;
	}

	public String getUser()
	{
		return user;
	}

	public void setMailSizeMap( LinkedHashMap< String, Integer > mailSizeMap )
	{
		this.mailSizeMap = mailSizeMap;
		mailList = new ArrayList< String >( this.mailSizeMap.keySet() );
		mailSizeList = new ArrayList< Integer >( this.mailSizeMap.values() );
	}

	public LinkedHashMap< String, Integer > getMailSizeMap()
	{
		return mailSizeMap;
	}

	public void setTotMailSize( int totMailSize )
	{
		this.totMailSize = totMailSize;
	}

	public int getTotMailSize()
	{
		return totMailSize;
	}

	public ArrayList< Integer > getMailSizeList()
	{
		return mailSizeList;
	}

	public void resetDeletedMessages()
	{
		this.markedForDeletion.clear();
	}

}
