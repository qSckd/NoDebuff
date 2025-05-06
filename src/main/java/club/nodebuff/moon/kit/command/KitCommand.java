package club.nodebuff.moon.kit.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import club.nodebuff.moon.kit.Kit;
import club.nodebuff.moon.kit.manage.KitManagerMenu;
import club.nodebuff.moon.kit.manage.KitManagerSelectKitMenu;
import club.nodebuff.moon.util.CC;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.util.Collections;

@CommandAlias("kit")
@CommandPermission("shadow.admin.kit")
@Description("Command to manage kits.")
public class KitCommand extends BaseCommand {

    @HelpCommand
    @Syntax("[page]")
    public void help(Player player, CommandHelp help) {
        help.showHelp();
    }

    @Subcommand("create")
    @Syntax("<kit>")
    public void create(Player player, String kitName) {
        if (Kit.getByName(kitName) != null) {
            player.sendMessage(CC.RED + "A kit with that name already exists.");
            return;
        }

        Kit kit = new Kit(kitName);
        kit.save();

        Kit.getKits().add(kit);

        player.sendMessage(CC.GREEN + "You created a new kit.");
    }

	@Subcommand("delete")
    @CommandCompletion("@kits")
    @Syntax("<kit>")
    public void delete(Player player, String kitName) {
        if (!Kit.getKits().contains(Kit.getByName(kitName))) {
            player.sendMessage(CC.translate("A kit with that name does not exist."));
            return;
        }
        Kit kit = Kit.getByName(kitName);
        if (kit != null) {
            kit.delete(kit);

            player.sendMessage(CC.RED + "Deleted kit \"" + kit.getName() + "\"");
        }
    }

    @Subcommand("save")
    public void save(Player player) {
        for (Kit kit : Kit.getKits()) {
            kit.save();
        }

        player.sendMessage(CC.GREEN + "Saved all kits!");
    }

    @Subcommand("toggle")
    @Syntax("<kit>")
    public void toggle(Player player, String kitName) {
        if (!Kit.getKits().contains(Kit.getByName(kitName))) {
            player.sendMessage(CC.translate("&cA kit with that name does not exist."));
            return;
        }
        Kit kit = Kit.getByName(kitName);
        if (kit == null) return;

        kit.setEnabled(!kit.isEnabled());
        kit.save();

        player.sendMessage(CC.GREEN + "You have updated the kit's status to " + (kit.isEnabled() ? "Enabled" : ChatColor.RED + "Disabled" + "."));
    }

    @Subcommand("list")
    public void list(Player player) {
        player.sendMessage(ChatColor.AQUA + "Kits");

        for (Kit kit : Kit.getKits()) {
            player.sendMessage(kit.getName());
        }
    }

	@Subcommand("description")
    @CommandCompletion("@kits")
    @Syntax("<kit> <description>")
    public void setdescription(Player player, String kitName, String description) {
        if (!Kit.getKits().contains(Kit.getByName(kitName))) {
            player.sendMessage(CC.translate("&cA kit with that name does not exist."));
            return;
        }
        Kit kit = Kit.getByName(kitName);
        if (kit == null) return;

        kit.setDescription(Collections.singletonList(description));
        kit.save();

        player.sendMessage(CC.GREEN + "You have updated the kit's description.");
    }

    @Subcommand("setloadout")
	@CommandCompletion("@kits")
    @Syntax("<kit>")
    public void setloadout(Player player, String kitName) {
        if (kitName == null) {
            player.sendMessage(CC.RED + "A kit with that name does not exist.");
            return;
        }

        Kit kit = Kit.getByName(kitName);
        kit.getKitLoadout().setArmor(player.getInventory().getArmorContents());
        kit.getKitLoadout().setContents(player.getInventory().getContents());
        kit.save();

        player.sendMessage(CC.GREEN + "You have updated the kit's loadout.");
    }

    @Subcommand("getloadout")
	@CommandCompletion("@kits")
    @Syntax("<kit>")
    public void getloadout(Player player, String kitName) {
        if (kitName == null) {
            player.sendMessage(CC.RED + "A kit with that name does not exist.");
            return;
        }

        Kit kit = Kit.getByName(kitName);
        player.getInventory().setArmorContents(kit.getKitLoadout().getArmor());
        player.getInventory().setContents(kit.getKitLoadout().getContents());
        player.updateInventory();

        player.sendMessage(CC.GREEN + "You received the kit's loadout.");
    }

    @Subcommand("setslot")
	@CommandCompletion("@kits")
    @Syntax("<kit> <slot>")
    public void setslot(Player player, String kitName, int slot) {
        if (kitName == null) {
            player.sendMessage(CC.RED + "A kit with that name does not exist.");
            return;
        }

        Kit kit = Kit.getByName(kitName);
        kit.setSlot(slot);
        kit.save();

        player.sendMessage(CC.GREEN + "You have updated the kit's slot.");
    }

    @Subcommand("icon")
	@CommandCompletion("@kits")
    @Syntax("<kit>")
    public void icon(Player player, String kitName) {
        if (!Kit.getKits().contains(Kit.getByName(kitName))) {
            player.sendMessage(CC.translate("&cA kit with that name does not exist."));
            return;
        }
        Kit kit = Kit.getByName(kitName);
        if (kit == null) return;

        kit.setDisplayIcon(player.getItemInHand());
        kit.save();

        player.sendMessage(CC.GREEN + "You have updated the kit's icon.");
    }

    @Subcommand("info")
    @CommandCompletion("@kits")
    @Syntax("<kit>")
    public void info(Player player, String kitName) {
        if (!Kit.getKits().contains(Kit.getByName(kitName))) {
            player.sendMessage(CC.translate("&cA kit with that name does not exist."));
            return;
        }
        Kit kit = Kit.getByName(kitName);
        if (kit == null) return;
           player.sendMessage(CC.translate("&b&lKit Info"));
           player.sendMessage("");
           player.sendMessage(CC.translate(" &7▪ &fName: &b" + kit.getName()));
	       player.sendMessage(CC.translate(" &7▪ &fEnabled: " + (kit.isEnabled() ? ChatColor.GREEN + "True" : ChatColor.RED + "False")));
           player.sendMessage(CC.translate(" &7▪ &fRanked: " + (kit.getGameRules().isRanked() ? ChatColor.GREEN + "True" : ChatColor.RED + "False")));
           player.sendMessage(CC.translate(" &7▪ &fKnockback Profile: &b" + (kit.getKnockbackProfile() == null ? "Default" : kit.getKnockbackProfile())));
           player.sendMessage(CC.translate(""));
           player.sendMessage(CC.translate("&b&lGame Rules"));
           player.sendMessage(CC.translate(""));
           player.sendMessage(CC.translate(" &7▪ &fBedfight: " + (kit.getGameRules().isBedfight() ? ChatColor.GREEN + "True" : ChatColor.RED + "False")));
           player.sendMessage(CC.translate(" &7▪ &fBoxing: " + (kit.getGameRules().isBoxing() ? ChatColor.GREEN + "True" : ChatColor.RED + "False")));
           player.sendMessage(CC.translate(" &7▪ &fOnehit: " + (kit.getGameRules().isOnehit() ? ChatColor.GREEN + "True" : ChatColor.RED + "False")));
           player.sendMessage(CC.translate(" &7▪ &fBridge: " + (kit.getGameRules().isBridge() ? ChatColor.GREEN + "True" : ChatColor.RED + "False")));
           player.sendMessage(CC.translate(" &7▪ &fBattleRush: " + (kit.getGameRules().isBattleRush() ? ChatColor.GREEN + "True" : ChatColor.RED + "False")));
           player.sendMessage(CC.translate(" &7▪ &fEggwars: " + (kit.getGameRules().isEggwars() ? ChatColor.GREEN + "True" : ChatColor.RED + "False")));
           player.sendMessage(CC.translate(" &7▪ &fBuild: " + (kit.getGameRules().isBuild() ? ChatColor.GREEN + "True" : ChatColor.RED + "False")));
           player.sendMessage(CC.translate(" &7▪ &fHit Delay: &b" + (kit.getGameRules().getHitDelay())));
    }

    @Subcommand("manage")
    @CommandCompletion("@kits")
    @Syntax("<kit>")
    public void manage(Player player, @Optional String kitName) {
        if (kitName == null) {
            new KitManagerSelectKitMenu().openMenu(player);
            return;
        }
        if (!Kit.getKits().contains(Kit.getByName(kitName))) {
			player.sendMessage(CC.translate("&cA kit with that name does not exist."));
			return;
        }
		Kit kit = Kit.getByName(kitName);
        if (kit == null) return;

         new KitManagerMenu(kit).openMenu(player);
    }

    @Subcommand("build")
	@CommandCompletion("@kits")
    @Syntax("<kit>")
    public void build(Player player, String kitName) {
        if (!Kit.getKits().contains(Kit.getByName(kitName))) {
            player.sendMessage(CC.translate("&cA kit with that name does not exist."));
            return;
        }
        Kit kit = Kit.getByName(kitName);
        if (kit == null) return;

        kit.getGameRules().setBuild(!kit.getGameRules().isBuild());
        kit.save();

        player.sendMessage(CC.GREEN + "You have updated the kit's build status to " + (kit.getGameRules().isBuild() ? "Enabled" : ChatColor.RED + "Disabled" + "."));
    }

    @Subcommand("boxing")
	@CommandCompletion("@kits")
    @Syntax("<kit>")
    public void boxing(Player player, String kitName) {
        if (!Kit.getKits().contains(Kit.getByName(kitName))) {
            player.sendMessage(CC.translate("&cA kit with that name does not exist."));
            return;
        }
        Kit kit = Kit.getByName(kitName);
        if (kit == null) return;

        kit.getGameRules().setBoxing(!kit.getGameRules().isBoxing());
        kit.save();

        player.sendMessage(CC.GREEN + "You have updated the kit's boxing status to " + (kit.getGameRules().isBoxing() ? "Enabled" : ChatColor.RED + "Disabled" + "."));
    }

    @Subcommand("ranked")
	@CommandCompletion("@kits")
    @Syntax("<kit>")
    public void ranked(Player player, String kitName) {
        if (!Kit.getKits().contains(Kit.getByName(kitName))) {
            player.sendMessage(CC.translate("&cA kit with that name does not exist."));
            return;
        }
        Kit kit = Kit.getByName(kitName);
        if (kit == null) return;

        kit.getGameRules().setRanked(!kit.getGameRules().isRanked());
        kit.save();

        player.sendMessage(CC.GREEN + "You have updated the kit's ranked status to " + (kit.getGameRules().isRanked() ? "Enabled" : ChatColor.RED + "Disabled" + "."));
    }

    @Subcommand("nodamage")
	@CommandCompletion("@kits")
    @Syntax("<kit>")
    public void nodamage(Player player, String kitName) {
        if (!Kit.getKits().contains(Kit.getByName(kitName))) {
            player.sendMessage(CC.translate("&cA kit with that name does not exist."));
            return;
        }
        Kit kit = Kit.getByName(kitName);
        if (kit == null) return;

        kit.getGameRules().setNoDamage(!kit.getGameRules().isNoDamage());
        kit.save();

        player.sendMessage(CC.GREEN + "You have updated the kit's no-damage status to " + (kit.getGameRules().isNoDamage() ? "Enabled" : ChatColor.RED + "Disabled" + "."));
    }

    @Subcommand("editable")
	@CommandCompletion("@kits")
    @Syntax("<kit>")
    public void editable(Player player, String kitName) {
        if (!Kit.getKits().contains(Kit.getByName(kitName))) {
            player.sendMessage(CC.translate("&cA kit with that name does not exist."));
            return;
        }
        Kit kit = Kit.getByName(kitName);
        if (kit == null) return;

        kit.getGameRules().setEditable(!kit.getGameRules().isEditable());
        kit.save();

        player.sendMessage(CC.GREEN + "You have updated the kit's editable status to " + (kit.getGameRules().isEditable() ? "Enabled" : ChatColor.RED + "Disabled" + "."));
    }

    @Subcommand("showhealth")
	@CommandCompletion("@kits")
    @Syntax("<kit>")
    public void showhealth(Player player, String kitName) {
        if (!Kit.getKits().contains(Kit.getByName(kitName))) {
            player.sendMessage(CC.translate("A kit with that name does not exist."));
            return;
        }
        Kit kit = Kit.getByName(kitName);
        if (kit == null) return;

        kit.getGameRules().setShowHealth(!kit.getGameRules().isShowHealth());
        kit.save();

        player.sendMessage(CC.GREEN + "You have updated the kit's showhealth status to " + (kit.getGameRules().isShowHealth() ? "Enabled" : ChatColor.RED + "Disabled" + "."));
    }

    @Subcommand("bedfight")
	@CommandCompletion("@kits")
    @Syntax("<kit>")
    public void bedfight(Player player, String kitName) {
        if (!Kit.getKits().contains(Kit.getByName(kitName))) {
            player.sendMessage(CC.translate("&cA kit with that name does not exist."));
            return;
        }
        Kit kit = Kit.getByName(kitName);
        if (kit == null) return;

        kit.getGameRules().setBedfight(!kit.getGameRules().isBedfight());
        kit.save();

        player.sendMessage(CC.GREEN + "You have updated the kit's bedfight status to " + (kit.getGameRules().isBedfight() ? "Enabled" : ChatColor.RED + "Disabled" + "."));
    }

    @Subcommand("bridge")
    @CommandCompletion("@kits")
    @Syntax("<kit>")
    public void bridge(Player player, String kitName) {
        if (!Kit.getKits().contains(Kit.getByName(kitName))) {
            player.sendMessage(CC.translate("&cA kit with that name does not exist."));
            return;
        }
        Kit kit = Kit.getByName(kitName);
        if (kit == null) return;

        kit.getGameRules().setBridge(!kit.getGameRules().isBridge());
        kit.save();

        player.sendMessage(CC.GREEN + "You have updated the kit's bridge status to " + (kit.getGameRules().isBridge() ? "Enabled" : ChatColor.RED + "Disabled" + "."));
    }

    @Subcommand("onehit")
	@CommandCompletion("@kits")
    @Syntax("<kit>")
    public void onehit(Player player, String kitName) {
        if (!Kit.getKits().contains(Kit.getByName(kitName))) {
            player.sendMessage(CC.translate("&cA kit with that name does not exist."));
            return;
        }
        Kit kit = Kit.getByName(kitName);
        if (kit == null) return;

        kit.getGameRules().setOnehit(!kit.getGameRules().isOnehit());
        kit.save();

        player.sendMessage(CC.GREEN + "You have updated the kit's onehit status to " + (kit.getGameRules().isOnehit() ? "Enabled" : ChatColor.RED + "Disabled" + "."));
    }

    @Subcommand("bowhit")
    @CommandCompletion("@kits")
    @Syntax("<kit>")
    public void bowhit(Player player, String kitName) {
        if (!Kit.getKits().contains(Kit.getByName(kitName))) {
            player.sendMessage(CC.translate("&cA kit with that name does not exist."));
            return;
        }
        Kit kit = Kit.getByName(kitName);
        if (kit == null) return;

        kit.getGameRules().setBowboost(true);
        kit.save();

        player.sendMessage(CC.GREEN + "You have updated the kit's bowhit status to " + (kit.getGameRules().isBowboost() ? "Enabled" : ChatColor.RED + "Disabled" + "."));
    }

    @Subcommand("kb")
	@CommandCompletion("@kits")
    @Syntax("<kit> <kb>")
    public void onehit(Player player, String kitName, String kb) {
        if (!Kit.getKits().contains(Kit.getByName(kitName))) {
            player.sendMessage(CC.translate("&cA kit with that name does not exist."));
            return;
        }
        Kit kit = Kit.getByName(kitName);
        if (kit == null) return;

        kit.setKnockbackProfile(kb);
        kit.save();

        player.sendMessage(CC.GREEN + "You have updated " + kitName + "'s kb.");
    }
}