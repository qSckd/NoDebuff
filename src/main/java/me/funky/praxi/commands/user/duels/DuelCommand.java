package me.funky.praxi.commands.user.duels;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import me.funky.praxi.duel.DuelProcedure;
import me.funky.praxi.duel.menu.DuelSelectKitMenu;
import me.funky.praxi.arena.Arena;
import me.funky.praxi.duel.DuelRequest;
import me.funky.praxi.match.Match;
import me.funky.praxi.match.impl.BasicTeamMatch;
import me.funky.praxi.match.participant.MatchGamePlayer;
import me.funky.praxi.participant.GameParticipant;
import me.funky.praxi.participant.TeamGameParticipant;
import me.funky.praxi.party.Party;
import me.funky.praxi.profile.Profile;
import me.funky.praxi.util.CC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@CommandAlias("duel")
@Description("Duel a player.")
public class DuelCommand extends BaseCommand {

    @Default
    @Syntax("<target>")
	public void execute(Player sender, String user) {
        Player target = Bukkit.getPlayer(user);
		if (target == null) {
			sender.sendMessage(CC.RED + "A player with that name could not be found.");
			return;
		}

		if (sender.hasMetadata("frozen")) {
			sender.sendMessage(CC.RED + "You cannot duel while frozen.");
			return;
		}

		if (target.hasMetadata("frozen")) {
			sender.sendMessage(CC.RED + "You cannot duel a frozen player.");
			return;
		}

		if (sender.getUniqueId().equals(target.getUniqueId())) {
			sender.sendMessage(CC.RED + "You cannot duel yourself.");
			return;
		}

		Profile senderProfile = Profile.getByUuid(sender.getUniqueId());
		Profile targetProfile = Profile.getByUuid(target.getUniqueId());

		if (senderProfile.isBusy()) {
			sender.sendMessage(CC.RED + "You cannot duel right now.");
			return;
		}

		if (targetProfile.isBusy()) {
			sender.sendMessage(CC.translate(target.getDisplayName() + " &cis currently busy."));
			return;
		}

		if (!targetProfile.getOptions().receiveDuelRequests()) {
			sender.sendMessage(CC.RED + "That player is not accepting duel requests at the moment.");
			return;
		}

		DuelRequest duelRequest = targetProfile.getDuelRequest(sender);

		if (duelRequest != null) {
			if (!senderProfile.isDuelRequestExpired(duelRequest)) {
				sender.sendMessage(CC.RED + "You already sent that player a duel request.");
				return;
			}
		}

		if (senderProfile.getParty() != null && targetProfile.getParty() == null) {
			sender.sendMessage(CC.RED + "You cannot send a party duel request to a player that is not in a party.");
			return;
		}

		if (senderProfile.getParty() == null && targetProfile.getParty() != null) {
			sender.sendMessage(CC.RED + "You cannot send a duel request to a player in a party.");
			return;
		}

		if (senderProfile.getParty() != null && targetProfile.getParty() != null) {
			if (senderProfile.getParty().equals(targetProfile.getParty())) {
				sender.sendMessage(CC.RED + "You cannot duel your own party.");
				return;
			}
		}

		DuelProcedure procedure = new DuelProcedure(sender, target, senderProfile.getParty() != null);
		senderProfile.setDuelProcedure(procedure);

		new DuelSelectKitMenu().openMenu(sender);
	}

    @Subcommand("accept")
    @Syntax("<target>")
    public void accept(Player player, String user) {
        Player target = Bukkit.getPlayer(user);
		if (target == null) {
			player.sendMessage(CC.RED + "This player is currently offline.");
			return;
		}

		if (player.hasMetadata("frozen")) {
			player.sendMessage(CC.RED + "You can't duel while frozen.");
			return;
		}

		if (target.hasMetadata("frozen")) {
			player.sendMessage(CC.RED + "You can't duel a frozen player.");
			return;
		}

		Profile playerProfile = Profile.getByUuid(player.getUniqueId());

		if (playerProfile.isBusy()) {
			player.sendMessage(CC.RED + "You can't duel right now.");
			return;
		}

		Profile targetProfile = Profile.getByUuid(target.getUniqueId());

		if (targetProfile.isBusy()) {
			player.sendMessage(target.getDisplayName() + CC.RED + " is currently busy.");
			return;
		}

		DuelRequest duelRequest = playerProfile.getDuelRequest(target);

		if (duelRequest != null) {
			if (targetProfile.isDuelRequestExpired(duelRequest)) {
				player.sendMessage(CC.RED + "That duel request has expired!");
				return;
			}

			if (duelRequest.isParty()) {
				if (playerProfile.getParty() == null) {
					player.sendMessage(CC.RED + "You do not have a party to duel with.");
					return;
				} else if (targetProfile.getParty() == null) {
					player.sendMessage(CC.RED + "That player does not have a party to duel with.");
					return;
				}
			} else {
				if (playerProfile.getParty() != null) {
					player.sendMessage(CC.RED + "You can't duel whilst in a party.");
					return;
				} else if (targetProfile.getParty() != null) {
					player.sendMessage(CC.RED + "That player is in a party and cannot duel right now.");
					return;
				}
			}

			Arena arena = duelRequest.getArena();

			if (arena.isActive()) {
				arena = Arena.getRandomArena(duelRequest.getKit());
			}

			if (arena == null) {
				player.sendMessage(CC.RED + "Tried to start a match but there are no available arenas.");
				return;
			}

			playerProfile.getDuelRequests().remove(duelRequest);

			arena.setActive(true);

			GameParticipant<MatchGamePlayer> participantA = null;
			GameParticipant<MatchGamePlayer> participantB = null;

			if (duelRequest.isParty()) {
				for (Party party : new Party[]{ playerProfile.getParty(), targetProfile.getParty() }) {
					Player leader = party.getLeader();
					MatchGamePlayer gamePlayer = new MatchGamePlayer(leader.getUniqueId(), leader.getName());
					TeamGameParticipant<MatchGamePlayer> participant = new TeamGameParticipant<>(gamePlayer);

					for (Player partyPlayer : party.getListOfPlayers()) {
						if (!partyPlayer.getPlayer().equals(leader)) {
							participant.getPlayers().add(new MatchGamePlayer(partyPlayer.getUniqueId(),
									partyPlayer.getName()));
						}
					}

					if (participantA == null) {
						participantA = participant;
					} else {
						participantB = participant;
					}
				}
			} else {
				MatchGamePlayer playerA = new MatchGamePlayer(player.getUniqueId(), player.getName());
				MatchGamePlayer playerB = new MatchGamePlayer(target.getUniqueId(), target.getName());

				participantA = new GameParticipant<>(playerA);
				participantB = new GameParticipant<>(playerB);
			}

			Match match = new BasicTeamMatch(null, duelRequest.getKit(), arena, false, participantA, participantB, true);
			match.start();
		} else {
			player.sendMessage(CC.RED + "You do not have a duel request from that player.");
		}
	}

}
