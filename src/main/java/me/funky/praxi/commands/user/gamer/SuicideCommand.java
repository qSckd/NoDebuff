package me.funky.praxi.commands.user.gamer;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import me.funky.praxi.profile.Profile;
import me.funky.praxi.profile.ProfileState;
import me.funky.praxi.util.CC;
import org.bukkit.entity.Player;

@CommandAlias("suicide|killme|iwanttogivehimfreewin|imbadplayer|lethimwin|iwanttogetfreelose|opme|freeop")
@Description("Kill your self.")
public class SuicideCommand extends BaseCommand {

    @Default
    public void execute(Player player) {
		Profile profile = Profile.getByUuid(player.getUniqueId());
		if (profile.getState() == ProfileState.FIGHTING) {
            player.setHealth(0);
            player.sendMessage(CC.translate("&cYou have killed yourself! Oh noes"));
        } else {
			player.sendMessage(CC.translate("&cYou can use this command in match only."));
		    return;
		}
    }
}