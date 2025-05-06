package club.nodebuff.moon.tournament.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import club.nodebuff.moon.Moon;
import club.nodebuff.moon.kit.Kit;
import club.nodebuff.moon.tournament.Tournament;
import club.nodebuff.moon.party.Party;
import club.nodebuff.moon.profile.Profile;
import club.nodebuff.moon.util.CC;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collections;

@CommandAlias("tournament|tourney|customevent|tournaments|1v1tournament|2v2tournament")
@Description("Command to manage tournament.")
public class TournamentCommand extends BaseCommand {

	@Default
    @Subcommand("help")
    public void help(Player player) {
			for (String line : Moon.get().getMainConfig().getStringList("TOURNAMENT.HELP.USER")) {
		  	    player.sendMessage(CC.translate(line));
			}

			if (player.hasPermission("moon.admin.tournament")) {
				for (String line : Moon.get().getMainConfig().getStringList("TOURNAMENT.HELP.ADMIN")) {
		  	        player.sendMessage(CC.translate(line));

				}
			}
    }

    @Subcommand("join")
	public void join(Player player) {
        if (Tournament.getCurrentTournament() == null || Tournament.getCurrentTournament().hasStarted()) {
            player.sendMessage(ChatColor.RED + "There is no tournament active currently!");
            return;
        }
        Profile profile = Profile.getByUuid(player.getUniqueId());
        if (Tournament.getCurrentTournament().getTeamCount() == 1) {
            Party party = Profile.getByUuid(player.getUniqueId()).getParty();
            if (party != null && party.getPlayers().size() != 1) {
                player.sendMessage(ChatColor.YELLOW + "This is a solo Tournament.");
                return;
            }
        } else {
            final Party party = Profile.getByUuid(player.getUniqueId()).getParty();
            if (party == null || party.getPlayers().size() != Tournament.getCurrentTournament().getTeamCount()) {
                player.sendMessage(ChatColor.RED + "The Tournament needs " + Tournament.getCurrentTournament().getTeamCount() + " players to start.");
                return;
            }
            if (!party.getLeader().getUniqueId().equals(player.getUniqueId())) {
                player.sendMessage(ChatColor.RED + "Only Leaders can do this.");
                return;
            }
        }
        if (profile.isBusy()) {
            player.sendMessage(ChatColor.RED + "You cannot join the Tournament in your current state.");
            return;
        }
        Party party = Profile.getByUuid(player.getUniqueId()).getParty();
        if (party == null) {
            player.chat("/party create");
            party = Profile.getByUuid(player.getUniqueId()).getParty();
        }
        Tournament.getCurrentTournament().participate(party);
    }

    @Subcommand("leave")
	public void leave(Player player) {
        if (Tournament.getCurrentTournament() == null || Tournament.getCurrentTournament().hasStarted()) {
            player.sendMessage(ChatColor.RED + "There isn't a Tournament you can leave");
            return;
        }
        Party party = Profile.getByUuid(player.getUniqueId()).getParty();
        if (party == null) {
            player.sendMessage(ChatColor.RED + "You aren't currently in a Tournament.");
            return;
        }
        if (!Tournament.getCurrentTournament().isParticipating(player)) {
            player.sendMessage(ChatColor.RED + "You aren't currently in a Tournament.");
            return;
        }
        if (!party.getLeader().getUniqueId().equals(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "Only Leaders can do this.");
            return;
        }
        Tournament.getCurrentTournament().leave(party);
    }

	@Subcommand("info")
	public void info(Player player) {
        if (Tournament.getCurrentTournament() != null) {
            Tournament tournament = Tournament.getCurrentTournament();
            StringBuilder builder = new StringBuilder();
            builder.append(ChatColor.DARK_AQUA).append("Tournament ").append(tournament.getTeamCount() + "v" + tournament.getTeamCount()).append("'s matches:");
            builder.append(ChatColor.DARK_AQUA).append(" ").append(ChatColor.BLUE).append("\n");
            builder.append(CC.AQUA + "Kit: ").append(ChatColor.WHITE + tournament.getKit().getName()).append("\n");
            builder.append(ChatColor.BLUE).append(" ").append(ChatColor.BLUE).append("\n");
            for (Tournament.TournamentMatch match : tournament.getTournamentMatches()) {
                String teamANames = match.getParticipantA().getLeader().getPlayer().getName();
                String teamBNames = match.getParticipantB().getLeader().getPlayer().getName();
                builder.append(ChatColor.DARK_AQUA + teamANames + "'s Party").append(ChatColor.WHITE + " vs. ").append(ChatColor.AQUA + teamBNames + "'s Party").append("\n");
        }
            builder.append(ChatColor.BLUE).append(" ").append(ChatColor.BLUE).append("\n");
            builder.append(CC.AQUA).append("Round: ").append(ChatColor.WHITE).append(tournament.getRound());
            builder.append("\n");
            builder.append(CC.AQUA).append("Players: ").append(ChatColor.WHITE).append(tournament.getParticipatingCount()).append("\n");
            player.sendMessage(builder.toString());
		} else {
			player.sendMessage(CC.RED + "There is no tournament active currently!");	
		}
    }

    @Subcommand("host")
	@CommandPermission("moon.donator.tournament")
	@Syntax("<kit> <team size>")
	public void host(Player player, String ladder, Integer size) {
        if (Tournament.getCurrentTournament() != null) {
            player.sendMessage(ChatColor.RED + "The Tournament has already started.");
            return;
        }
        Tournament.createNewTournament();
        if (size != 1 && size != 2) {
            player.sendMessage(ChatColor.RED + "Please choose 1 or 2.");
            Tournament.cancelCurrentTournament();
            return;
        }
        if (size == 1) {
            Tournament.getCurrentTournament().setTeamCount(1);
        } else {
            Tournament.getCurrentTournament().setTeamCount(2);
        }
        if (Kit.getByName(ladder) != null) {
            Tournament.getCurrentTournament().setKit(Kit.getByName(ladder));
            final String type = Tournament.getCurrentTournament().getTeamCount() + "vs" + Tournament.getCurrentTournament().getTeamCount();
            Bukkit.broadcastMessage(CC.translate(CC.GRAY + "[" + "&b" + CC.BOLD + "Tournament" + CC.GRAY + "] " + CC.WHITE + "A " + ChatColor.GOLD + ChatColor.BOLD + type + " " + Tournament.getCurrentTournament().getKit().getName() + ChatColor.WHITE + " tournament is being hosted by " + player.getName()));
            broadcastMessage(ChatColor.GREEN + "[Click to join]");
            (Tournament.RUNNABLE = new BukkitRunnable() {
                private int countdown = 60;
                public void run() {
                    --this.countdown;
                    if ((this.countdown % 10 == 0 || this.countdown <= 10) && this.countdown > 0) {
                        Bukkit.broadcastMessage(CC.translate(CC.GRAY + "[" + "&b" + CC.BOLD + "Tournament" + CC.GRAY + "] " + CC.WHITE + "The Tournament is starting in " + "&b" + this.countdown + ChatColor.WHITE + " seconds. "));
                        broadcastMessage(ChatColor.GREEN + "[Click to join]");
                    }
                    if (this.countdown <= 0) {
                        Tournament.RUNNABLE = null;
                        this.cancel();
                        if (Tournament.getCurrentTournament().getParticipatingCount() < 2) {
                            Bukkit.broadcastMessage(CC.translate(CC.GRAY + "[" + "&b" + CC.BOLD + "Tournament" + CC.GRAY + "] " + CC.RED + "The Tournament has been cancelled ."));
                            Tournament.cancelCurrentTournament();
                        } else {
                            Tournament.getCurrentTournament().start();
                        }
                    }
                }
            }).runTaskTimer(Moon.get(), 20L, 20L);
            return;
        }
        player.sendMessage(ChatColor.RED + "Please choose a valid kit");
        Tournament.cancelCurrentTournament();
    }

    @Subcommand("forcestart")
	@CommandPermission("moon.admin.tournament")
	public void forcestart(Player player) {
        if (Tournament.getCurrentTournament() == null) {
            player.sendMessage(CC.RED + "There is no Tournament available to forcestart.");
            return;
        }
        Tournament.getCurrentTournament().start();
        Tournament.RUNNABLE.cancel();
        Tournament.RUNNABLE = null;
        player.sendMessage(CC.GREEN + "The tournament was started!");
    }

    @Subcommand("cancel")
	@CommandPermission("moon.admin.tournament")
	public void cancel(Player player) {
        if (Tournament.getCurrentTournament() == null) {
            player.sendMessage(ChatColor.RED + "There isn't an active Tournament right now");
            return;
        }
        if (Tournament.RUNNABLE != null) {
            Tournament.RUNNABLE.cancel();
        }
        Tournament.cancelCurrentTournament();
        player.sendMessage(ChatColor.RED + "The Tournament has been cancelled");
    }

	private static void broadcastMessage(String message) {
        BaseComponent[] fromLegacyText;
        BaseComponent[] component = fromLegacyText = TextComponent.fromLegacyText(message);
        for (BaseComponent baseComponent : fromLegacyText) {
            baseComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tournament join"));
            baseComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(ChatColor.GREEN + "Click to join the Tournament")));
        }
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(component);
        }
    }
}
