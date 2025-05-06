package club.nodebuff.moon.util;

import club.nodebuff.moon.profile.Profile;
import club.nodebuff.moon.profile.option.killeffect.SpecialEffects;
import club.nodebuff.moon.profile.option.killmessages.KillMessages;
import club.nodebuff.moon.profile.option.trail.Trail;
import club.nodebuff.moon.util.PlayerUtil;
import club.nodebuff.moon.util.CC;
import club.nodebuff.moon.util.menu.Button;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class PurchaseUtil {

    public static void purchaseItem(Player player, Object i) {
        Profile profile = Profile.getByUuid(player.getUniqueId());
        if (i instanceof SpecialEffects) {
            SpecialEffects item = (SpecialEffects) i;
            if (profile.getCoins() >= item.getPrice()) {
                profile.setCoins(profile.getCoins() - item.getPrice());
                profile.addPermission(item.getPermission());
                player.sendMessage(CC.translate("&aPurchased the " + item.getName() + " kill effect!"));
                Button.playSuccess(player);
            } else {
                player.sendMessage(CC.translate("&cYou don't have enough coins."));
                Button.playFail(player);
            }
        } if (i instanceof KillMessages) {
            KillMessages item = (KillMessages) i;
            if (profile.getCoins() >= item.getPrice()) {
                profile.setCoins(profile.getCoins() - item.getPrice());
                profile.addPermission(item.getPermission());
                player.sendMessage(CC.translate("&aPurchased the " + item.getName() + " kill message!"));
                Button.playSuccess(player);
            } else {
                player.sendMessage(CC.translate("&cYou don't have enough coins."));
                Button.playFail(player);
            }
        } if (i instanceof Trail) {
            Trail item = (Trail) i;
            if (profile.getCoins() >= item.getPrice()) {
                profile.setCoins(profile.getCoins() - item.getPrice());
                profile.addPermission(item.getPermission());
                player.sendMessage(CC.translate("&aPurchased the " + item.getName() + " trail!"));
                Button.playSuccess(player);
            } else {
                player.sendMessage(CC.translate("&cYou don't have enough coins."));
                Button.playFail(player);
            }
        } else if (!(i instanceof KillMessages) && !(i instanceof SpecialEffects)) {
            player.sendMessage(CC.translate("&cSomething went wrong, please alert a administrator."));
            Button.playFail(player);
        }
    }

}
