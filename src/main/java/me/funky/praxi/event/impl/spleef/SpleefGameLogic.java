package me.funky.praxi.event.impl.spleef;

import me.funky.praxi.Praxi;
import me.funky.praxi.Locale;
import me.funky.praxi.event.game.EventGame;
import me.funky.praxi.event.game.EventGameLogic;
import me.funky.praxi.event.game.EventGameLogicTask;
import me.funky.praxi.event.game.EventGameState;
import me.funky.praxi.event.game.map.EventGameMap;
import me.funky.praxi.event.game.map.vote.EventGameMapVoteData;
import me.funky.praxi.kit.Kit;
import me.funky.praxi.participant.GameParticipant;
import me.funky.praxi.participant.GamePlayer;
import me.funky.praxi.profile.Profile;
import me.funky.praxi.profile.ProfileState;
import me.funky.praxi.profile.hotbar.Hotbar;
import me.funky.praxi.profile.hotbar.HotbarItem;
import me.funky.praxi.profile.visibility.VisibilityLogic;
import me.funky.praxi.util.ItemBuilder;
import me.funky.praxi.util.CC;
import me.funky.praxi.util.Cooldown;
import me.funky.praxi.util.BlockUtil;
import me.funky.praxi.util.PlayerUtil;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class SpleefGameLogic implements EventGameLogic {

    private final EventGame game;
    @Getter
    private final EventGameLogicTask logicTask;
    @Getter
    private GameParticipant<GamePlayer> participantA;
    @Getter
    private GameParticipant<GamePlayer> participantB;
    @Getter
    private int roundNumber;
    private GameParticipant winningParticipant;

    SpleefGameLogic(EventGame game) {
        this.game = game;
        this.logicTask = new EventGameLogicTask(game);
        this.logicTask.runTaskTimer(Praxi.get(), 0, 20L);
    }

    @Override
    public EventGameLogicTask getGameLogicTask() {
        return logicTask;
    }

    @Override
    public void startEvent() {
        Bukkit.getOnlinePlayers().forEach(player -> {
            Locale.EVENT_START.formatLines(game.getEvent().getDisplayName(),
                    game.getParticipants().size(), game.getMaximumPlayers())
                    .forEach(line -> player.sendMessage(CC.translate(line)));
        });

        int chosenMapVotes = 0;

        for (Map.Entry<EventGameMap, EventGameMapVoteData> entry : game.getVotesData().entrySet()) {
            if (game.getGameMap() == null) {
                game.setGameMap(entry.getKey());
                chosenMapVotes = entry.getValue().getPlayers().size();
            } else {
                if (entry.getValue().getPlayers().size() >= chosenMapVotes) {
                    game.setGameMap(entry.getKey());
                    chosenMapVotes = entry.getValue().getPlayers().size();
                }
            }
        }

        for (GameParticipant<GamePlayer> participant : game.getParticipants()) {
            for (GamePlayer gamePlayer : participant.getPlayers()) {
                Player player = gamePlayer.getPlayer();

                if (player != null) {
                    PlayerUtil.reset(player);
                    player.teleport(game.getGameMap().getSpectatorPoint());
                    Hotbar.giveHotbarItems(player);
                }
            }
        }
    }

    @Override
    public boolean canStartEvent() {
        return game.getRemainingParticipants() >= 2;
    }

    @Override
    public void preEndEvent() {
        for (GameParticipant participant : game.getParticipants()) {
            if (!participant.isEliminated()) {
                winningParticipant = participant;
                break;
            }
        }

        if (winningParticipant != null) {

            Bukkit.getOnlinePlayers().forEach(player -> {
                Locale.EVENT_FINISH.formatLines(game.getEvent().getDisplayName(),
                        winningParticipant.getConjoinedNames(),
                        (winningParticipant.getPlayers().size() == 1 ? "has" : "have"),
                        game.getEvent().getDisplayName()).forEach(line -> player.sendMessage(CC.translate(line)));
            });
        }
    }

    @Override
    public void endEvent() {
        EventGame.setActiveGame(null);
        EventGame.setCooldown(new Cooldown(60_000L * 3L));

        SpleefEvent spleefEvent = (SpleefEvent) game.getEvent();
        spleefEvent.getChangedBlocks().forEach((blockState) -> blockState.getLocation().getBlock().setType(blockState.getType()));

        for (GameParticipant<GamePlayer> participant : game.getParticipants()) {
            for (GamePlayer gamePlayer : participant.getPlayers()) {
                Player player = gamePlayer.getPlayer();

                if (player != null) {
                    Profile profile = Profile.getByUuid(player.getUniqueId());
                    profile.setState(ProfileState.LOBBY);

                    Hotbar.giveHotbarItems(player);
                    Praxi.get().getEssentials().teleportToSpawn(player);
                    VisibilityLogic.handle(player);
                }
            }
        }
    }

    @Override
    public boolean canEndEvent() {
        return game.getRemainingParticipants() <= 1;
    }

    @Override
    public void cancelEvent() {
        game.sendMessage(ChatColor.DARK_RED + "The event has been cancelled by an administrator!");

        EventGame.setActiveGame(null);
        EventGame.setCooldown(new Cooldown(60_000L * 3L));

        for (GameParticipant<GamePlayer> participant : game.getParticipants()) {
            for (GamePlayer gamePlayer : participant.getPlayers()) {
                Player player = gamePlayer.getPlayer();

                if (player != null) {
                    Profile profile = Profile.getByUuid(player.getUniqueId());
                    profile.setState(ProfileState.LOBBY);

                    Hotbar.giveHotbarItems(player);

                    Praxi.get().getEssentials().teleportToSpawn(player);
                }
            }
        }
    }

    @Override
    public void preStartRound() {

    }

    @Override
    public void startRound() {
        game.sendMessage(Locale.EVENT_ROUND_START.format(game.getGameLogic().getRoundNumber(),
                participantA.getConjoinedNames(), participantB.getConjoinedNames()));

        game.sendSound(Sound.ORB_PICKUP, 1.0F, 15F);

        game.getGameMap().teleportFighters(game);

        for (GameParticipant<GamePlayer> participant : new GameParticipant[]{participantA, participantB}) {
            for (GamePlayer gamePlayer : participant.getPlayers()) {
                Player player = gamePlayer.getPlayer();

                if (player != null) {
                    player.getInventory().setArmorContents(new ItemStack[4]);
                    player.getInventory().clear();
                    player.getInventory().setHeldItemSlot(0);
                    player.getInventory().setItem(0, new ItemBuilder(Material.DIAMOND_SPADE).name(CC.translate("&aSpleef Tool")).amount(1).build());
                    player.updateInventory();
					PlayerUtil.allowMovement(player);
                }
            }
        }
    }

    @Override
    public boolean canStartRound() {
        return false;
    }

    @Override
    public void endRound() { }

    @Override
    public boolean canEndRound() {
        return false;
    }

    @Override
    public void onVote(Player player, EventGameMap gameMap) {
        if (game.getGameState() == EventGameState.WAITING_FOR_PLAYERS ||
            game.getGameState() == EventGameState.STARTING_EVENT) {
            EventGameMapVoteData voteData = game.getVotesData().get(gameMap);

            if (voteData != null) {
                if (voteData.hasVote(player.getUniqueId())) {
                    player.sendMessage(ChatColor.RED + "You have already voted for that map!");
                } else {
                    for (EventGameMapVoteData otherVoteData : game.getVotesData().values()) {
                        if (otherVoteData.hasVote(player.getUniqueId())) {
                            otherVoteData.getPlayers().remove(player.getUniqueId());
                        }
                    }

                    voteData.addVote(player.getUniqueId());

                    game.sendMessage(Locale.EVENT_PLAYER_VOTE.format(
                            player.getName(),
                            gameMap.getMapName(),
                            voteData.getPlayers().size()
                    ));
                }
            } else {
                player.sendMessage(ChatColor.RED + "A map with that name does not exist.");
            }
        } else {
            player.sendMessage(ChatColor.RED + "The event has already started.");
        }
    }

    @Override
    public void onJoin(Player player) {
        game.getParticipants().add(new GameParticipant<>(new GamePlayer(player.getUniqueId(), player.getName())));

        game.sendMessage(Locale.EVENT_PLAYER_JOIN.format(player.getName(),
                game.getParticipants().size(),
                game.getMaximumPlayers()));

        Profile profile = Profile.getByUuid(player.getUniqueId());
        profile.setState(ProfileState.EVENT);

        Hotbar.giveHotbarItems(player);

        for (Map.Entry<EventGameMap, EventGameMapVoteData> entry : game.getVotesData().entrySet()) {
            ItemStack itemStack = Hotbar.getItems().get(HotbarItem.MAP_SELECTION).clone();
            ItemMeta itemMeta = itemStack.getItemMeta();

            itemMeta.setDisplayName(itemMeta.getDisplayName().replace("%MAP%", entry.getKey().getMapName()));
            itemStack.setItemMeta(itemMeta);

            player.getInventory().addItem(itemStack);
        }

        player.updateInventory();
        player.teleport(game.getEvent().getLobbyLocation());

        VisibilityLogic.handle(player);

        for (GameParticipant<GamePlayer> gameParticipant : game.getParticipants()) {
            for (GamePlayer gamePlayer : gameParticipant.getPlayers()) {
                if (!gamePlayer.isDisconnected()) {
                    Player bukkitPlayer = gamePlayer.getPlayer();

                    if (bukkitPlayer != null) {
                        VisibilityLogic.handle(bukkitPlayer, player);
                    }
                }
            }
        }
    }

    @Override
    public void onLeave(Player player) {
        if (isPlaying(player)) {
            onDeath(player, null);
        }

        Iterator<GameParticipant<GamePlayer>> iterator = game.getParticipants().iterator();

        while (iterator.hasNext()) {
            GameParticipant<GamePlayer> participant = iterator.next();

            if (participant.containsPlayer(player.getUniqueId())) {
                iterator.remove();

                for (GamePlayer gamePlayer : participant.getPlayers()) {
                    if (!gamePlayer.isDisconnected()) {
                        Player bukkitPlayer = gamePlayer.getPlayer();

                        if (bukkitPlayer != null) {
                            if (game.getGameState() == EventGameState.WAITING_FOR_PLAYERS ||
                                    game.getGameState() == EventGameState.STARTING_EVENT) {
                                game.sendMessage(Locale.EVENT_PLAYER_LEAVE.format(
                                        bukkitPlayer.getName(),
                                        game.getRemainingPlayers(),
                                        game.getMaximumPlayers()
                                ));
                            }

                            Profile profile = Profile.getByUuid(bukkitPlayer.getUniqueId());
                            profile.setState(ProfileState.LOBBY);

                            Hotbar.giveHotbarItems(bukkitPlayer);
                            VisibilityLogic.handle(bukkitPlayer, player);

                            Praxi.get().getEssentials().teleportToSpawn(bukkitPlayer);
                        }
                    }
                }
            }
        }

        VisibilityLogic.handle(player);
    }

    @Override
    public void onMove(Player player) {
        if (isPlaying(player)) {
            GamePlayer gamePlayer = game.getGamePlayer(player);

            if (gamePlayer != null) {
                if (BlockUtil.isOnLiquid(player.getLocation(), 0)) {
                    if (!gamePlayer.isDead()) {
                        onDeath(player, null);
                    }
                }
            }
        }
    }

    @Override
    public void onDeath(Player player, Player killer) {
        GamePlayer deadGamePlayer = game.getGamePlayer(player);

        if (deadGamePlayer != null) {
            deadGamePlayer.setDead(true);
        }

        if (participantA.isAllDead() || participantB.isAllDead()) {
            GameParticipant winner = getWinningParticipant();
            winner.reset();

            GameParticipant loser = getLosingParticipant();
            loser.setEliminated(true);

            if (canEndEvent()) {
                preEndEvent();
                game.setGameState(EventGameState.ENDING_EVENT);
                logicTask.setNextAction(3);
            } else if (canEndRound()) {
                game.setGameState(EventGameState.ENDING_ROUND);
                logicTask.setNextAction(1);
            }
        }
    }

    @Override
    public String getPlayerA() {
        if (participantA != null) {
            return participantA.getConjoinedNames();
        }
        return null;
    }

    @Override
    public String getPlayerB() {
        if (participantB != null) {
            return participantB.getConjoinedNames();
        }
        return null;
    }

    @Override
    public boolean isPlaying(Player player) {
        return (participantA != null && participantA.containsPlayer(player.getUniqueId())) ||
                (participantB != null && participantB.containsPlayer(player.getUniqueId()));
    }

    @Override
    public int getRoundNumber() {
        return 1;
    }

    private GameParticipant getWinningParticipant() {
		if (participantA.isAllDead()) {
			return participantB;
		} else {
			return participantA;
		}
	}

	private GameParticipant getLosingParticipant() {
		if (participantA.isAllDead()) {
			return participantA;
		} else {
			return participantB;
		}
	}
}