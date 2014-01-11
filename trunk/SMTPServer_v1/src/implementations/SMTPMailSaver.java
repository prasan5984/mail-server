package implementations;

import interfaces.Mail;

public class SMTPMailSaver implements Runnable {

	private Mail mailObject;

	public SMTPMailSaver(Mail mail) {
		mailObject = mail;
	}

	private void saveMailLocally() {
		//Logic to save the mail locally will be written during POP3 implementation
	}

	@Override
	public void run() {
		saveMailLocally();
	}

}
