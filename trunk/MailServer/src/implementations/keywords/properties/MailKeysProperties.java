package implementations.keywords.properties;

import interfaces.KeyProperties;

import java.util.ArrayList;

public class MailKeysProperties implements KeyProperties {

	private ArrayList<String> previousKeys = new ArrayList<String>();
	private boolean multiOccurence;
	private boolean skipSequenceCheck;
	private String keywordName;

	public ArrayList<String> getLastKey() {
		return previousKeys;
	}

	public void setLastKey(String lastKey) {
		this.previousKeys.add(lastKey);
	}

	public boolean isMultiOccurence() {
		return multiOccurence;
	}

	public void setMultiOccurence(boolean multiOccurence) {
		this.multiOccurence = multiOccurence;
	}

	public boolean isSkipSequenceCheck() {
		return skipSequenceCheck;
	}

	public void setSkipSequenceCheck(boolean skipSequenceCheck) {
		this.skipSequenceCheck = skipSequenceCheck;
	}

	public String getKeywordName() {
		return keywordName;
	}

	public void setKeywordName(String keywordName) {
		this.keywordName = keywordName;
	}

}
