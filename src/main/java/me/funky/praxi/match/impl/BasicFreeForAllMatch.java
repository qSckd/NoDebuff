package club.nodebuff.moon.match.impl;

import club.nodebuff.moon.Locale;
import club.nodebuff.moon.arena.Arena;
import club.nodebuff.moon.kit.Kit;
import club.nodebuff.moon.match.Match;
import club.nodebuff.moon.match.MatchSnapshot;
import club.nodebuff.moon.match.participant.MatchGamePlayer;
import club.nodebuff.moon.participant.GameParticipant;
import club.nodebuff.moon.profile.Profile;
import club.nodebuff.moon.profile.meta.ProfileKitData;
import club.nodebuff.moon.queue.Queue;
import club.nodebuff.moon.adapter.spigot.SpigotManager;
import club.nodebuff.moon.util.*;
import java.util.ArrayList;
import java.util.List;

import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class BasicFreeForAllMatch extends Match {

	private final List<GameParticipant<MatchGamePlayer>> participants;
	private GameParticipant<MatchGamePlayer> winningParticipant;

	public BasicFreeForAllMatch(Queue queue, Kit kit, Arena arena, List<GameParticipant<MatchGamePlayer>> participants) {
		super(queue, kit, arena, false, false);

		this.participants = participants;
	}

	@Override
	public void setupPlayer(Player player) {
		// Set the player as alive
		MatchGamePlayer gamePlayer = getGamePlayer(player);
		gamePlayer.setDead(false);

		// If the player disconnected, skip any operations for them
		if (gamePlayer.isDisconnected()) {
			return;
		}

		// Reset the player's inventory
		PlayerUtil.reset(player);

        Profile profile = Profile.getByUuid(player.getUniqueId());

		// Deny movement if the kit is sumo or bedfight
		if (getKit().getGameRules().isSumo() || getKit().getGameRules().isBedfight() || getKit().getGameRules().isBridge() || getKit().getGameRules().isEggwars() || getKit().getGameRules().isStickFight() || getKit().getGameRules().isBattleRush()) {
            PlayerUtil.denyMovement(player);
        }

		// Set the player's max damage ticks
		player.setMaximumNoDamageTicks(getKit().getGameRules().getHitDelay());

		// Set the player's kb using kits config
		if (getKit().getKnockbackProfile() != null) {
			SpigotManager.getSpigot().setKnockback(player, getKit().getKnockbackProfile());
		}

		// If the player has no kits, apply the default kit, otherwise
		// give the player a list of kit books to choose from
		if (!getKit().getGameRules().isSumo()) {
            ProfileKitData kitData = profile.getKitData().get(getKit());

            if (kitData.getKitCount() > 0) {
                profile.getKitData().get(getKit()).giveBooks(player);
            } else if (getKit().getGameRules().isBedfight()) {
                player.getInventory().setContents(getKit().getKitLoadout().getContents());
                KitUtils.giveBedFightKit(player);
               player.sendMessage(CC.translate(Locale.MATCH_GIVE_KIT.format(player, "Default", getKit().getName(), "&" + profile.getOptions().theme().getColor().getChar())));
                player.updateInventory();
                profile.getMatch().getGamePlayer(player).setKitLoadout(kit.getKitLoadout());
            } else if (getKit().getGameRules().isBridge() || getKit().getGameRules().isBattleRush()) {
                player.getInventory().setContents(getKit().getKitLoadout().getContents());
                KitUtils.giveBridgeKit(player);
                player.sendMessage(CC.translate(Locale.MATCH_GIVE_KIT.format(player, "Default", getKit().getName(), "&" + profile.getOptions().theme().getColor().getChar())));
                player.updateInventory();
                profile.getMatch().getGamePlayer(player).setKitLoadout(kit.getKitLoadout());
            } else if (getKit().getGameRules().isStickFight()) {
                player.getInventory().setContents(getKit().getKitLoadout().getContents());
                KitUtils.giveLivesKit(player);
                player.sendMessage(CC.translate(Locale.MATCH_GIVE_KIT.format(player, "Default", getKit().getName(), "&" + profile.getOptions().theme().getColor().getChar())));
                player.updateInventory();
                profile.getMatch().getGamePlayer(player).setKitLoadout(kit.getKitLoadout());
            } else {
                player.getInventory().setArmorContents(getKit().getKitLoadout().getArmor());
                player.getInventory().setContents(getKit().getKitLoadout().getContents());
				player.sendMessage(CC.translate(Locale.MATCH_GIVE_KIT.format(player, "Default", getKit().getName(), "&" + profile.getOptions().theme().getColor().getChar())));

                player.updateInventory();
                profile.getMatch().getGamePlayer(player).setKitLoadout(kit.getKitLoadout());
            }
        }

		// Teleport the player to their spawn point
		Location spawn = arena.getSpawnA();

		if (spawn.getBlock().getType() == Material.AIR) {
			player.teleport(spawn);
		} else {
			player.teleport(spawn.add(0, 2, 0));
		}
	}

	@Override
	public boolean canEndMatch() {
		return getRemainingTeams() <= 1;
	}

	@Override
	public boolean canStartRound() {
		return false;
	}

	@Override
	public boolean canEndRound() {
		return getRemainingTeams() <= 1;
	}

	@Override
	public void onRoundEnd() {
		for (GameParticipant<MatchGamePlayer> gameParticipant : participants) {
			if (!gameParticipant.isAllDead()) {
				winningParticipant = gameParticipant;
				break;
			}
		}

		if (!kit.getGameRules().isSumo()) {
			// Make all snapshots available
			for (MatchSnapshot snapshot : snapshots) {
				snapshot.setCreatedAt(System.currentTimeMillis());
				MatchSnapshot.getSnapshots().put(snapshot.getUuid(), snapshot);
			}
		}

		super.onRoundEnd();
	}

	@Override
	public boolean isOnSameTeam(Player first, Player second) {
		return first.equals(second);
	}

	@Override
	public List<GameParticipant<MatchGamePlayer>> getParticipants() {
		return new ArrayList<>(participants);
	}

	@Override
	public org.bukkit.ChatColor getRelationColor(Player viewer, Player target) {
		if (viewer.equals(target)) {
			return org.bukkit.ChatColor.GREEN;
		} else {
			for (GameParticipant<MatchGamePlayer> participant : participants) {
				if (participant.containsPlayer(target.getUniqueId())) {
					return org.bukkit.ChatColor.RED;
				}
			}

			return org.bukkit.ChatColor.YELLOW;
		}
	}

	@Override
	public void addSpectator(Player spectator, Player target) {
		super.addSpectator(spectator, target);

		spectator.sendMessage(Locale.MATCH_START_SPECTATING.format(CC.GREEN, target.getUniqueId()));
	}

	@Override
	public List<BaseComponent[]> generateEndComponents() {
		List<BaseComponent[]> componentsList = new ArrayList<>();

		for (String line : Locale.MATCH_END_DETAILS.formatLines()) {
			if (line.equalsIgnoreCase("%INVENTORIES%")) {
				List<GameParticipant<MatchGamePlayer>> participants = new ArrayList<>(this.participants);
				participants.remove(winningParticipant);

				BaseComponent[] winners = generateInventoriesComponents(
						Locale.MATCH_END_WINNER_INVENTORY.format(""), winningParticipant);

				BaseComponent[] losers = generateInventoriesComponents(
						Locale.MATCH_END_LOSER_INVENTORY.format(participants.size() > 1 ? "s" : ""), participants);

				componentsList.add(winners); // &a&l[Replay]
				componentsList.add(losers);

				continue;
			}

			if (line.equalsIgnoreCase("%ELO_CHANGES%")) {
				continue;
			}

			componentsList.add(new ChatComponentBuilder("").parse(line).create());
		}

		return componentsList;
	}

	public int getRemainingTeams() {
		int remaining = 0;

		for (GameParticipant<MatchGamePlayer> gameParticipant : participants) {
			if (!gameParticipant.isAllDead()) {
				remaining++;
			}
		}

		return remaining;
	}

}
