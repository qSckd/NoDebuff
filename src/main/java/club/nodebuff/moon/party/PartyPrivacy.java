package club.nodebuff.moon.party;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum PartyPrivacy {

	OPEN("Open"),
	CLOSED("Closed");

	private String readable;

}
