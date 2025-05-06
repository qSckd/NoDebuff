package club.nodebuff.moon.commands.admin.match;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import club.nodebuff.moon.Locale;
import club.nodebuff.moon.match.Match;
import club.nodebuff.moon.match.participant.MatchGamePlayer;
import club.nodebuff.moon.participant.GameParticipant;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.entity.Player;

@CommandAlias("debugmatch")
@CommandPermission("shadow.admin.match")
@Description("A Debug command used by developers.")
public class MatchTestCommand extends BaseCommand {

    @Default
	public void execute(Player player) {
		List<GameParticipant<MatchGamePlayer>> list = Arrays.asList(
				new GameParticipant<>(new MatchGamePlayer(UUID.randomUUID(), "Test1")),
				new GameParticipant<>(new MatchGamePlayer(UUID.randomUUID(), "Test2")),
				new GameParticipant<>(new MatchGamePlayer(UUID.randomUUID(), "Test3")),
				new GameParticipant<>(new MatchGamePlayer(UUID.randomUUID(), "Test4")));

		BaseComponent[] components = Match.generateInventoriesComponents(
				Locale.MATCH_END_WINNER_INVENTORY.format("s"), list);

		player.spigot().sendMessage(components);
	}

}
