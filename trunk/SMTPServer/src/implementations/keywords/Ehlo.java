package implementations.keywords;

import implementations.SMTPMail;
import implementations.keywords.properties.MailKeysProperties;
import interfaces.KeyProperties;
import interfaces.Keyword;
import interfaces.Mail;
import interfaces.MailHandler;
import interfaces.MailReceiver;
import constants.MailServerConstants;

public class Ehlo implements Keyword {

	@Override
	public void process(MailReceiver processor) {

		Mail mail = new SMTPMail();
		processor.setMail(mail);

		processor.sendResponse(MailServerConstants.DEFAULT_SUCCESS_CODE
				+ " "
				+ MailServerConstants.CODE_MSG_MAP
						.get(MailServerConstants.DEFAULT_SUCCESS_CODE));
	}

	@Override
	public void successiveProcess(MailReceiver processor) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getName() {
		return "EHLO";
	}

	@Override
	public KeyProperties getProperties() {
		MailKeysProperties prop = new MailKeysProperties();
		prop.setKeywordName("EHLO");
		prop.setMultiOccurence(true);
		prop.setSkipSequenceCheck(true);
		return prop;
	}

	@Override
	public Keyword getClonedObject() {
		Ehlo ehlo = new Ehlo();
		return ehlo;
	}

	@Override
	public String getMailTransaction(MailHandler mailSender, Mail mail, String response) {
		return "Ehlo" + "\r\n";
	}

	@Override
	public boolean getStatus() {
		return true;
	}

	@Override
	public boolean skipOnSend()
	{
		// TODO Auto-generated method stub
		return false;
	}
}
