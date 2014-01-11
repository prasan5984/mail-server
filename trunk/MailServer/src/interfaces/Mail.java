package interfaces;

import java.util.ArrayList;

public interface Mail
{

	public void setFrom( String from );

	public void setTo( String to );

	public void setData( String data );

	public String getFrom();

	public ArrayList< String > getTo();

	public String getDomain();

	public String getData();

	public Mail getClone();

	public void setToList( ArrayList< String > addList );

	public void setDomain( String domain );

}
