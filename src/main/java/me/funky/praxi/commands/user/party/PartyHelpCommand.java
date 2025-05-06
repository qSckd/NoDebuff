package club.nodebuff.moon.commands.user.party;

import club.nodebuff.moon.Locale;
import club.nodebuff.moon.util.command.command.CPL;
import club.nodebuff.moon.util.command.command.CommandMeta;
import org.bukkit.entity.Player;

@CommandMeta(label = { "p", "p help", "party", "party help" })
public class PartyHelpCommand {

	public void execute(Player player) {
		Locale.PARTY_HELP.formatLines().forEach(player::sendMessage);
	}

}
