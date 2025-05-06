package club.nodebuff.moon.party.menu;

import lombok.AllArgsConstructor;
import club.nodebuff.moon.Moon;
import club.nodebuff.moon.arena.Arena;
import club.nodebuff.moon.kit.Kit;
import club.nodebuff.moon.match.impl.BasicFreeForAllMatch;
import club.nodebuff.moon.match.impl.BasicTeamMatch;
import club.nodebuff.moon.match.participant.MatchGamePlayer;
import club.nodebuff.moon.participant.GameParticipant;
import club.nodebuff.moon.participant.TeamGameParticipant;
import club.nodebuff.moon.party.Party;
import club.nodebuff.moon.party.PartyEvent;
import club.nodebuff.moon.profile.Profile;
import club.nodebuff.moon.match.Match;
import club.nodebuff.moon.util.CC;
import club.nodebuff.moon.util.Constants;
import club.nodebuff.moon.util.ItemBuilder;
import club.nodebuff.moon.util.menu.Button;
import club.nodebuff.moon.util.menu.Menu;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
public class PartyEventSelectKitMenu extends Menu {

	private PartyEvent partyEvent;

	@Override
	public String getTitle(Player player) {
		return Moon.get().getMenusConfig().getString("PARTY.SELECT-KIT.TITLE");
	}

    @Override
    public int getSize() {
		return Moon.get().getMenusConfig().getInteger("PARTY.SELECT-KIT.SIZE");
    }

	@Override
	public Map<Integer, Button> getButtons(Player player) {
		Map<Integer, Button> buttons = new HashMap<>();

        int i = 10;

		for (int j = 0; j < getSize(); ++j) {
			buttons.put(j, Constants.BLACK_PANE);
		}

		for (Kit kit : Kit.getKits()) {
			if (kit.isEnabled()) {
				buttons.put(kit.getSlot(), new SelectKitButton(partyEvent, kit));
			}
		}

		return buttons;
	}

	@AllArgsConstructor
	private class SelectKitButton extends Button {

		private PartyEvent partyEvent;
		private Kit kit;

		@Override
		public ItemStack getButtonItem(Player player) {
			List<String> lore = new ArrayList<>();
			lore.add("");
			lore.add(" &aClick here to select.");
			return new ItemBuilder(kit.getDisplayIcon())
					.name("&b&l" + kit.getName())
					.lore(lore)
					.clearFlags()
					.build();
		}

		@Override
		public void clicked(Player player, ClickType clickType) {
			Menu.currentlyOpenedMenus.get(player.getName()).setClosedByMenu(true);

			player.closeInventory();

			Profile profile = Profile.getByUuid(player.getUniqueId());

			if (profile.getParty() == null) {
				player.sendMessage(CC.RED + "You are not in a party.");
				return;
			}

			if (profile.getParty().getPlayers().size() <= 1) {
				player.sendMessage(CC.RED + "You do not have enough players in your party to start an event.");
				return;
			}

			Party party = profile.getParty();
			Arena arena = Arena.getRandomArena(kit);

			if (arena == null) {
				player.sendMessage(CC.RED + "There are no available arenas.");
				return;
			}

			arena.setActive(true);

			Match match;

			if (partyEvent == PartyEvent.FFA) {
				List<GameParticipant<MatchGamePlayer>> participants = new ArrayList<>();

				for (Player partyPlayer : party.getListOfPlayers()) {
					participants.add(new GameParticipant<>(
							new MatchGamePlayer(partyPlayer.getUniqueId(), partyPlayer.getName())));
				}

				match = new BasicFreeForAllMatch(null, kit, arena, participants);
			} else {
				Player partyLeader = party.getLeader();
				Player randomLeader = Bukkit.getPlayer(party.getPlayers().get(1));

				MatchGamePlayer leaderA = new MatchGamePlayer(partyLeader.getUniqueId(), partyLeader.getName());
				MatchGamePlayer leaderB = new MatchGamePlayer(randomLeader.getUniqueId(), randomLeader.getName());

				GameParticipant<MatchGamePlayer> participantA = new TeamGameParticipant<>(leaderA);
				GameParticipant<MatchGamePlayer> participantB = new TeamGameParticipant<>(leaderB);

				List<Player> players = new ArrayList<>(party.getListOfPlayers());
				Collections.shuffle(players);

				for (Player otherPlayer : players) {
					if (participantA.containsPlayer(otherPlayer.getUniqueId()) ||
					    participantB.containsPlayer(otherPlayer.getUniqueId())) {
						continue;
					}

					MatchGamePlayer gamePlayer = new MatchGamePlayer(otherPlayer.getUniqueId(), otherPlayer.getName());

					if (participantA.getPlayers().size() > participantB.getPlayers().size()) {
						participantB.getPlayers().add(gamePlayer);
					} else {
						participantA.getPlayers().add(gamePlayer);
					}
				}

				// Create match
				match = new BasicTeamMatch(null, kit, arena, false, participantA, participantB, true); // TODO: idk if i need to make this true or false
			}

			// Start match
			match.start();
		}

	}

}