package club.nodebuff.moon.match.task;

import lombok.Getter;
import lombok.Setter;
import club.nodebuff.moon.Moon;
import club.nodebuff.moon.Locale;
import club.nodebuff.moon.match.Match;
import club.nodebuff.moon.match.MatchState;
import club.nodebuff.moon.match.impl.BasicTeamMatch;
import club.nodebuff.moon.match.participant.MatchGamePlayer;
import club.nodebuff.moon.participant.GameParticipant;
import club.nodebuff.moon.participant.GamePlayer;
import club.nodebuff.moon.util.CC;
import club.nodebuff.moon.util.PlayerUtil;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.Sound;
import org.bukkit.scheduler.BukkitRunnable;

public class MatchLogicTask extends BukkitRunnable {

    private final Match match;
	private final Moon plugin = Moon.get();
    @Setter
    private int nextAction = 6;

    public MatchLogicTask(Match match) {
        this.match = match;
    }


    @Override
    public void run() {
        if (!Moon.get().getCache().getMatches().contains(match)) {
            cancel();
            return;
        }
        nextAction--;
        if (match.getState() == MatchState.STARTING_ROUND) {
            if (match.getKit().getGameRules().isSumo() || match.getKit().getGameRules().isBedfight() || match.getKit().getGameRules().isBridge() || match.getKit().getGameRules().isStickFight() || match.getKit().getGameRules().isBattleRush()) {
                for (GameParticipant<MatchGamePlayer> gameParticipant : match.getParticipants()) {
                    for (GamePlayer gamePlayer : gameParticipant.getPlayers()) {
                        PlayerUtil.denyMovement(gamePlayer.getPlayer());
                    }
                }
            }
            if (nextAction == 0) {
                match.onRoundStart();
                match.setState(MatchState.PLAYING_ROUND);
                match.sendMessage(Locale.MATCH_STARTED.format());
                match.sendMessage(" ");
                //match.sendMessage(Locale.MATCH_WARNING.format());

				if (match.getKit().getGameRules().isSumo() || match.getKit().getGameRules().isBedfight() || match.getKit().getGameRules().isStickFight() || match.getKit().getGameRules().isBridge() || match.getKit().getGameRules().isBattleRush()) {
                    for (GameParticipant<MatchGamePlayer> gameParticipant : match.getParticipants()) {
                        for (GamePlayer gamePlayer : gameParticipant.getPlayers()) {
                            PlayerUtil.allowMovement(gamePlayer.getPlayer());
                        }
                    }
                }
                match.sendSound(Sound.FIREWORK_BLAST, 1.0F, 1.0F);
            } else {
                if (match.getState() != MatchState.ENDING_MATCH) {
                    match.sendMessage(Locale.MATCH_START_TIMER.format(nextAction, nextAction == 1 ? "" : "s"));
                    match.sendTitle(CC.translate("&b" + nextAction), "", 20);
				    Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    match.sendTitle(CC.translate("&aFight!"), "&fGood Luck.", 20);
                    }, 20 * (nextAction - 0));
                    match.sendSound(Sound.CLICK, 1.0F, 1.0F);
                    if (match.getKit().getGameRules().isBoxing() || match.getKit().getGameRules().isOnehit()) {
                        for (GameParticipant<MatchGamePlayer> players : match.getParticipants()) {
                            players.addSpeed();
                        }
                    }
                }
            }
        } else if (match.getState() == MatchState.ENDING_ROUND) {
            if (nextAction == 0) {
                if (match.canStartRound()) {
                    match.onRoundStart();
                }
            }
        } else if (match.getState() == MatchState.ENDING_MATCH) {
            if (nextAction == 0) {
                match.end();
            }
        }
    }

}

