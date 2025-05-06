package club.nodebuff.moon.queue.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import club.nodebuff.moon.Moon;
import club.nodebuff.moon.profile.Profile;
import club.nodebuff.moon.queue.Queue;
import club.nodebuff.moon.util.Constants;
import club.nodebuff.moon.util.RandomStrings;
import club.nodebuff.moon.util.CC;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Objects;

@CommandAlias("ranked|cban|rankedban|banmqaaz|banskid")
@CommandPermission("shadow.ranked.kit")
@Description("Command to manage rankedbans.")
public class RankedCommand extends BaseCommand {

    @HelpCommand
    @Syntax("[page]")
    public void help(Player player, CommandHelp help) {
        help.showHelp();
    }

	@Subcommand("ban")
	@Syntax("<target> <reason>")
	@CommandCompletion("@players")
    public void ban(Player player, OfflinePlayer target, String reason) {
        if (target != null) {
            Profile profile = Profile.getByUuid(target.getUniqueId());
            if (profile.isRankedBan()) {
                player.sendMessage(CC.translate("&c" + target.getName() + " is already banned from ranked! (Reason: " + profile.getRankedBanReason() + ")"));
            } else {
                profile.setRankedBan(true);
                boolean six_or_eight = Constants.getRandom().nextBoolean();
                String banID = RandomStrings.generateRandomString(six_or_eight ? 6 : 8);
                if (reason != null) profile.setRankedBanReason(reason);
                profile.setRankedBanID(banID);
                player.sendMessage(CC.translate("&a" + target.getName() + " was successfully banned from ranked" + (reason != null ? " for " + reason + "!" : "!")));
                if (profile.isInQueue()) {
                    profile.getQueueProfile().getQueue().removePlayer(profile.getQueueProfile());
                }
                if (target.isOnline()) {
                    Player onlineTarget = (Player) target;
                    onlineTarget.sendMessage(CC.translate("&cYou were permanently banned from ranked."));
                    onlineTarget.sendMessage(CC.translate("&cReason: &f" + reason));
                    onlineTarget.sendMessage(CC.translate("&cID: &f" + banID));
                }
            }
        } else {
            player.sendMessage(CC.translate("&cUsage: /ranked ban <player> <reason>"));
        }
    }
	@Subcommand("idcheck")
	@Syntax("<id>")
    public void idcheck(Player player, String id) {
        if (id != null && !id.equalsIgnoreCase("None")) {
            if (checkBanID(id) != null) {
                Profile profile = Objects.requireNonNull(checkBanID(id));
                Player targetPlayer = Objects.requireNonNull(profile).getPlayer();
                player.sendMessage(CC.CHAT_BAR);
                player.sendMessage(CC.translate("&cRanked ban stats for &f" + targetPlayer.getName() + ":"));
                player.sendMessage("");
                player.sendMessage(CC.translate("&cBanned: " + (profile.isRankedBan() ? "&aYes" : "&cNo")));
                player.sendMessage(CC.translate("&cBan reason: &f" + profile.getRankedBanReason()));
                player.sendMessage(CC.translate("&cBan ID: &f" + profile.getRankedBanID()));
                player.sendMessage(CC.CHAT_BAR);
            } else {
                player.sendMessage(CC.translate("&cCould not find a ranked ban with that id."));
            }
        } else {
            player.sendMessage(CC.translate("&cUsage: /ranked bancheck <id>"));
        }
    }

    private Profile checkBanID(String banID) {
        for (Profile profile : Profile.getProfiles().values()) {
            if (profile.isRankedBan()) {
                if (profile.getRankedBanID().equalsIgnoreCase(banID)) {
                    return profile;
                }
            }
        }
        return null;
    }
	@Subcommand("playercheck")
	@Syntax("<target>")
	@CommandCompletion("@players")
    public void playercheck(Player player, OfflinePlayer target) {
        if (target != null) {
            Profile profile = Profile.getByUuid(target.getUniqueId());
            player.sendMessage(CC.CHAT_BAR);
            player.sendMessage(CC.translate("&cRanked ban stats for &f" + target.getName() + ":"));
            player.sendMessage("");
            player.sendMessage(CC.translate("&cBanned: " + (profile.isRankedBan() ? "&aYes" : "&cNo")));
            player.sendMessage(CC.translate("&cBan reason: &f" + profile.getRankedBanReason()));
            player.sendMessage(CC.translate("&cBan ID: &f" + profile.getRankedBanID()));
            player.sendMessage(CC.CHAT_BAR);
        } else {
            player.sendMessage(CC.translate("&cUsage: /ranked playercheck <player>"));
        }
    }
	@Subcommand("unban")
	@Syntax("<target>")
	@CommandCompletion("@players")
    public void unban(Player player, OfflinePlayer target) {
        if (target != null) {
            Profile profile = Profile.getByUuid(target.getUniqueId());
            if (!profile.isRankedBan()) {
                player.sendMessage(CC.translate("&c" + target.getName() + " is not banned from ranked!"));
                return;
            } else {
                profile.setRankedBan(false);
                profile.setRankedBanReason("None");
                profile.setRankedBanID("None");
                player.sendMessage(CC.translate("&a" + target.getName() + " was successfully unbanned from ranked!"));
                if (target.isOnline()) {
                    target.getPlayer().sendMessage(CC.translate("&aYou were unbanned from the ranked queues!"));
                }
            }
        } else {
            player.sendMessage(CC.translate("&cUsage: /ranked unban <player>"));
        }
    }
}