package interfaces;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public interface MailProcessor {

	public void initialize();

	public void onAccept(Object socketAddress);

	public void onRead(Object socketAddress, String msg);

	public NetworkInteractor getInteractor();

	public void setInteractor(NetworkInteractor interactor);

	public void registerSender(Object domain, MailHandler handler);

	public void sendResponse(Object ip, String msg);
	
	public LinkedHashMap< String, Keyword > getKeywordMap();

	public void sendMail( ArrayList< Mail > mailList );

}
