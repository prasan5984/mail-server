package interfaces;

public interface NetworkInteractor
{

	public void startServer();

	public Object initiateConnection( String ip );

	public void sendMessage( Object ip, String msg );

	public void onClose( Object socket );

}
