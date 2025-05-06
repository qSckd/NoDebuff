package me.funky.praxi.adapter.board;

import com.bizarrealex.aether.scoreboard.Board;
import com.bizarrealex.aether.scoreboard.BoardAdapter;
import com.bizarrealex.aether.scoreboard.cooldown.BoardCooldown;
import me.funky.praxi.Praxi;
import me.funky.praxi.match.Match;
import me.funky.praxi.match.MatchState;
import me.funky.praxi.match.impl.BasicFreeForAllMatch;
import me.funky.praxi.match.impl.BasicTeamMatch;
import me.funky.praxi.profile.Profile;
import me.funky.praxi.profile.ProfileState;
import me.funky.praxi.event.game.EventGameState;
import me.funky.praxi.event.game.EventGame;
import me.funky.praxi.queue.QueueProfile;
import me.funky.praxi.util.ReplaceUtil;
import me.funky.praxi.util.PlayerUtil;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class ScoreboardAdapter implements BoardAdapter {

    @Override
    public String getTitle(Player player) {

        if (Praxi.get().isReplay()) {
            if (PlayerUtil.inReplay(player)) {
                return getFormattedReplayTitle(player);
            }
		}
        return getFormattedTitle(player);
    }

    @Override
    public List<String> getScoreboard(Player player, Board board, Set<BoardCooldown> cooldowns) {
        Profile profile = Profile.getByUuid(player.getUniqueId());

        if (!profile.getOptions().showScoreboard()) {
			return null;
		}

        if (profile == null) {
            return null;
        }

        switch (profile.getState()) {
            case FIGHTING:
                return getMatchLines(player, profile);
            case SPECTATING:
                return getSpectatingLines(player);
            case EVENT:
                return getEventLines(player);
            case QUEUEING:
                return getQueueingLines(player, profile);
            default:
				return getLobbyLines(player, profile);
        }
    }

    private String getFormattedTitle(Player player) {
        String animatedTitle = getAnimatedText();
        return ReplaceUtil.format(Collections.singletonList(animatedTitle), player).get(0);
    }

    private String getFormattedReplayTitle(Player player) {
        String animatedReplayTitle = getAnimatedReplayText();
        return ReplaceUtil.format(Collections.singletonList(animatedReplayTitle), player).get(0);
    }

    private String getAnimatedText() {
        int index = (int) ((System.currentTimeMillis() / Praxi.get().getScoreboardConfig().getInteger("TITLE.UPDATE-INTERVAL")) 
                % Praxi.get().getScoreboardConfig().getStringList("TITLE.DEFAULT").size());
        return Praxi.get().getScoreboardConfig().getStringList("TITLE.DEFAULT").get(index);
    }

    private String getAnimatedReplayText() {
        int index = (int) ((System.currentTimeMillis() / Praxi.get().getScoreboardConfig().getInteger("TITLE.UPDATE-INTERVAL")) 
                % Praxi.get().getScoreboardConfig().getStringList("TITLE.REPLAY").size());
        return Praxi.get().getScoreboardConfig().getStringList("TITLE.REPLAY").get(index);
    }

    private List<String> getLobbyLines(Player player, Profile profile) {
        if (profile.getParty() != null) {
            return getFormattedLines(player, "IN-PARTY.LOBBY");
        }

        if (Praxi.get().isReplay() && PlayerUtil.inReplay(player)) {
            return getFormattedLines(player, "MATCH.REPLAY");
        }

        if (profile.getState() == ProfileState.EDITING) {
            return getFormattedLines(player, "EDITING");
        }

        return getFormattedLines(player, "LOBBY");
    }

    private List<String> getSpectatingLines(Player player) {
        return getFormattedLines(player, "MATCH.SPECTATING");
    }

    private List<String> getEventLines(Player player) {
        EventGameState eventState = EventGame.getActiveGame().getGameState();

        switch (eventState) {
            case WAITING_FOR_PLAYERS:
                return getFormattedLines(player, "EVENT.WAITING");
            case STARTING_ROUND:
                return getFormattedLines(player, "EVENT.STARTING");
            case PLAYING_ROUND:
                return getFormattedLines(player, "EVENT.PLAYING");
            case ENDING_EVENT:
                return getFormattedLines(player, "EVENT.ENDING");
            default:
                return null;
        }
    }

    private List<String> getQueueingLines(Player player, Profile profile) {
        QueueProfile queueProfile = profile.getQueueProfile();
        String path = queueProfile.getQueue().isRanked() ? "QUEUE.RANKED" : "QUEUE.UNRANKED";
        return getFormattedLines(player, path);
    }

    private List<String> getMatchLines(Player player, Profile profile) {
        Match match = profile.getMatch();
        if (match == null) {
            return null;
        }

        if (match instanceof BasicTeamMatch && profile.getParty() != null) {
            if (match.getKit().getGameRules().isBedfight()) {
                return getFormattedLines(player, "MATCH.TEAMS.IN-MATCH-BEDFIGHT");
            } else if (match.getKit().getGameRules().isBoxing()) {
                return getFormattedLines(player, "MATCH.TEAMS.IN-MATCH-BOXING");
            } else if (match.getKit().getGameRules().isBridge()) {
                return getFormattedLines(player, "MATCH.TEAMS.IN-MATCH-BRIDGE");
		    } else {
                return getFormattedLines(player, "IN-PARTY.IN-SPLIT-MATCH");
            }
        }

        if (match instanceof BasicFreeForAllMatch) {
            return getFormattedLines(player, "IN-PARTY.IN-FFA-MATCH");
        }

        return getMatchStateLines(player, match);
    }

    private List<String> getMatchStateLines(Player player, Match match) {
        switch (match.getState()) {
            /*case STARTING_ROUND:
                return getFormattedLines(player, "MATCH.STARTING");*/
            case STARTING_ROUND:
                return getPlayingRoundLines(player, match);
            case PLAYING_ROUND:
                return getPlayingRoundLines(player, match);
            case ENDING_MATCH:
                return getFormattedLines(player, "MATCH.ENDING");
            default:
                return null;
        }
    }

    private List<String> getPlayingRoundLines(Player player, Match match) {
        String path;

        if (match.getKit().getGameRules().isBoxing()) {
            path = "MATCH.IN-MATCH-BOXING";
        } else if (match.getKit().getGameRules().isOnehit()) {
            path = "MATCH.IN-MATCH-ONEHIT";
        } else if (match.getKit().getGameRules().isBedfight()) {
            path = "MATCH.IN-MATCH-BEDFIGHT";
		} else if (match.getKit().getGameRules().isEggwars()) {
            path = "MATCH.IN-MATCH-EGGWARS";
        } else if (match.getKit().getGameRules().isBridge()) {
            path = "MATCH.IN-MATCH-BRIDGE";
        } else if (match.getKit().getGameRules().isBattleRush()) {
            path = "MATCH.IN-MATCH-BATTLERUSH";
        } else if (match.getKit().getGameRules().isStickFight()) {
            path = "MATCH.IN-MATCH-STICKFIGHT";
        } else {
            path = "MATCH.IN-MATCH";
        }

        return getFormattedLines(player, path);
    }

    private List<String> getFormattedLines(Player player, String path) {
        List<String> lines = new ArrayList<>(Praxi.get().getScoreboardConfig().getStringList(path));
        return ReplaceUtil.format(lines, player);
    }

    @Override
	public void onScoreboardCreate(Player p0, Scoreboard p1) {

	}
}
