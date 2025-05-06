package club.nodebuff.moon.event.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import club.nodebuff.moon.profile.Profile;
import club.nodebuff.moon.util.CC;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

@CommandAlias("tokens|eventtokens")
@Description("Event Tokens commands.")
public class EventTokensCommand extends BaseCommand {

    @Default
    public void execute(Player player) {
        Profile profile = Profile.getByUuid(player.getUniqueId());
        player.sendMessage(CC.translate("&cYour tokens: &f" + profile.getEventTokens() + " tokens."));
        if (player.hasPermission("moon.admin.tokens")) {
            player.sendMessage("");
            player.sendMessage(CC.translate("&c&l/tokens set &7- &fSets a player's amount of tokens."));
            player.sendMessage(CC.translate("&c&l/tokens remove &7- &fTakes the specified amount of tokens from a player."));
        }
    }

    @Subcommand("remove")
    @CommandPermission("moon.admin.tokens")
    @CommandCompletion("@players")
	@Syntax("<target> <tokens>")
    public void remove(Player player, OfflinePlayer target, Integer tokens) {
        try {
            Profile profile = Profile.getByUuid(target.getUniqueId());
            if (profile.getEventTokens() == 0 || profile.getEventTokens() - tokens < 0) {
                player.sendMessage(CC.translate("&cYou cannot remove any more tokens from " + target.getName() + "."));
                return;
            }
            profile.setEventTokens(profile.getEventTokens() - tokens);
            player.sendMessage(CC.translate("&fRemoved&c " + tokens + " &ftokens from " + target.getName() + "'s tokens amount."));
        } catch (NumberFormatException ex) {
            player.sendMessage(CC.translate("&c" + tokens + " is not an number or is a number higher than the max allowed."));
        }
    }

    @Subcommand("set")
    @CommandPermission("moon.admin.tokens")
    @CommandCompletion("@players")
    @Syntax("<target> <tokens>")
    public void set(Player player, OfflinePlayer target, Integer tokens) {
        try {
            if (tokens == null || tokens < 0) {
                player.sendMessage(CC.translate("&cInvalid tokens amount!"));
                return;
            }

            Profile profile = Profile.getByUuid(target.getUniqueId());
            profile.setEventTokens(tokens);
            player.sendMessage(CC.translate("&fSet&c " + target.getName() + "&f's tokens amount to " + tokens + "."));
        } catch (NumberFormatException ex) {
            player.sendMessage(CC.translate("&c" + tokens + " is not an number or is a number higher than the max allowed."));
        }
    }
}
