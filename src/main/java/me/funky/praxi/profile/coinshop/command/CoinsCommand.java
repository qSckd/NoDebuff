package club.nodebuff.moon.profile.coinshop.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import club.nodebuff.moon.profile.Profile;
import club.nodebuff.moon.util.CC;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

@CommandAlias("coin|coins")
@Description("Coin commands.")
public class CoinsCommand extends BaseCommand {

    @Default
    public void execute(Player player) {
        Profile profile = Profile.getByUuid(player.getUniqueId());
        player.sendMessage(CC.translate("&cYour coins: &f" + profile.getCoins() + " coins."));
        if (player.hasPermission("moon.coins.admin")) {
            player.sendMessage("");
            player.sendMessage(CC.translate("&c&l/coin set &7- &fSets a player's amount of coins."));
            player.sendMessage(CC.translate("&c&l/coin add &7- &fAdds the specified amount of coins to a player."));
            player.sendMessage(CC.translate("&c&l/coin remove &7- &fTakes the specified amount of coins from a player."));
        }
    }

    @Subcommand("add")
    @CommandPermission("moon.admin.coins")
    @CommandCompletion("@players")
	@Syntax("<target> <coins>")
    public void add(Player player, OfflinePlayer target, Integer coins) {
        try {
            if (coins == Integer.MAX_VALUE || coins < 0) {
                player.sendMessage(CC.translate("&cInvalid coins amount!"));
                return;
            }

            Profile profile = Profile.getByUuid(target.getUniqueId());

            if (profile.getCoins() > Integer.MAX_VALUE - coins) {
                player.sendMessage(CC.translate("&cAdding " + coins + " coins would make " + target.getName() + "'s total coins too high!"));
                return;
            }

            profile.setCoins(profile.getCoins() + coins);
            player.sendMessage(CC.translate("&fAdded&c " + coins + " &fcoins to " + target.getName() + "'s coins amount."));
        } catch (NumberFormatException ex) {
            player.sendMessage(CC.translate("&c" + coins + " is not an number or is a number higher than the max allowed."));
        }
    }

    @Subcommand("remove")
    @CommandPermission("moon.admin.coins")
    @CommandCompletion("@players")
	@Syntax("<target> <coins>")
    public void remove(Player player, OfflinePlayer target, Integer coins) {
        try {
            Profile profile = Profile.getByUuid(target.getUniqueId());
            if (profile.getCoins() == 0 || profile.getCoins() - coins < 0) {
                player.sendMessage(CC.translate("&cYou cannot remove any more coins from " + target.getName() + "."));
                return;
            }
            profile.setCoins(profile.getCoins() - coins);
            player.sendMessage(CC.translate("&fRemoved&c " + coins + " &fcoins from " + target.getName() + "'s coins amount."));
        } catch (NumberFormatException ex) {
            player.sendMessage(CC.translate("&c" + coins + " is not an number or is a number higher than the max allowed."));
        }
    }

    @Subcommand("set")
    @CommandPermission("moon.admin.coins")
    @CommandCompletion("@players")
    @Syntax("<target> <coins>")
    public void set(Player player, OfflinePlayer target, Integer coins) {
        try {
            if (coins == null || coins < 0) {
                player.sendMessage(CC.translate("&cInvalid coins amount!"));
                return;
            }

            Profile profile = Profile.getByUuid(target.getUniqueId());
            profile.setCoins(coins);
            player.sendMessage(CC.translate("&fSet&c " + target.getName() + "&f's coins amount to " + coins + "."));
        } catch (NumberFormatException ex) {
            player.sendMessage(CC.translate("&c" + coins + " is not an number or is a number higher than the max allowed."));
        }
    }
}
