package club.nodebuff.moon.util;

import lombok.experimental.UtilityClass;
import me.clip.placeholderapi.PlaceholderAPI;
import club.nodebuff.moon.Moon;
import club.nodebuff.moon.match.Match;
import club.nodebuff.moon.profile.Profile;
import club.nodebuff.moon.profile.ProfileState;
import club.nodebuff.moon.profile.managers.DivisionsManager;
import club.nodebuff.moon.divisions.ProfileDivision;
import club.nodebuff.moon.event.game.EventGame;
import club.nodebuff.moon.match.impl.BasicFreeForAllMatch;
import club.nodebuff.moon.match.impl.BasicTeamMatch;
import club.nodebuff.moon.match.participant.MatchGamePlayer;
import club.nodebuff.moon.participant.GameParticipant;
import club.nodebuff.moon.queue.QueueProfile;
import club.nodebuff.moon.util.ProgressBar;
import org.bukkit.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public final class ReplaceUtil {

    public static List<String> format(List<String> lines, Player player) {
        List<String> formattedLines = new ArrayList<>();
        Profile profile = Profile.getByUuid(player.getUniqueId());
        QueueProfile queueProfile = profile.getQueueProfile();
        DivisionsManager divisionManager = Moon.get().getDivisionsManager();
        ProfileDivision profileDivision = profile.getDivision();
        ProfileDivision expDivision = Moon.get().getDivisionsManager().getNextDivisionByXP(profile.getExperience());
        ProfileDivision oldDivision = Moon.get().getDivisionsManager().getDivisionForBarByXP(profile.getExperience());
        ProfileDivision eloDivision = Moon.get().getDivisionsManager().getNextDivisionByELO(profile.getGlobalElo());
        for (String line : lines) {
            line = line.replaceAll("<online>", String.valueOf(Bukkit.getServer().getOnlinePlayers().size()));
            line = line.replaceAll("<in-queue>", String.valueOf(Moon.get().getCache().getInQueues()));
            line = line.replaceAll("<in-match>", String.valueOf(Moon.get().getCache().getMatches().size() * 2));
            line = line.replaceAll("<player>", player.getName());
            line = line.replaceAll("<ping>", String.valueOf((BukkitReflection.getPing(player))));
            line = line.replaceAll("<selected_kill_effect>", CC.translate(profile.getOptions().killEffect().getName()));
            line = line.replaceAll("<selected_kill_message>", CC.translate(profile.getOptions().killMessage().getName()));
            line = line.replaceAll("<theme>", CC.translate("&" + profile.getOptions().theme().getColor().getChar()));
            line = line.replaceAll("<coins>", String.valueOf(profile.getCoins()));
            line = line.replaceAll("<event_tokens>", String.valueOf(profile.getEventTokens()));
            line = line.replaceAll("<player_exp>", String.valueOf(profile.getExperience()));
            line = line.replaceAll("<player_division>", CC.translate(profile.getDivision().getDisplayName()));
            line = line.replaceAll("<player_level>", CC.translate(profile.getDivision().getXpLevel()));
            line = line.replaceAll("<splitter>", "┃");
            if (Moon.get().getDivisionsManager().isXPBased()) {
                line = line.replaceAll("<player_bar>", ProgressBar.getBar(profile.getExperience() - oldDivision.getExperience(), expDivision.getExperience() - oldDivision.getExperience()));
            } else {
                line = line.replaceAll("<player_bar>", ProgressBar.getBar(profile.getGlobalElo(), eloDivision.getMaxElo()));
            }
            if (line.contains("<follow>") && profile.getFollowing().isEmpty()) {
                continue;
            } else {
                line = line.replaceAll("<follow>", "");
            }

            if (!profile.getFollowing().isEmpty()) {
                line = line.replaceAll("<followedPlayer>", Bukkit.getPlayer(profile.getFollowing().get(0)).getName());
            } else {
                line = line.replaceAll("<followedPlayer>", "");
            }

            if (profile.getState() == ProfileState.QUEUEING) {
                line = line.replaceAll("<kit>", queueProfile.getQueue().getKit().getName());
                line = line.replaceAll("<type>", queueProfile.getQueue().isRanked() ? "Ranked" : "Unranked");
                line = line.replaceAll("<time>", TimeUtil.millisToTimer(queueProfile.getPassed()));
                line = line.replaceAll("<minElo>", String.valueOf(queueProfile.getMinRange()));
                line = line.replaceAll("<maxElo>", String.valueOf(queueProfile.getMaxRange()));
            }
            if (profile.getState() == ProfileState.EDITING) {
                line = line.replaceAll("<kit>", profile.getKitEditorData().getSelectedKit().getName());
            }

             if (profile.getState() == ProfileState.EVENT) {
                line = line.replaceAll("<event_name>", EventGame.getActiveGame().getEvent().getDisplayName());
                if (EventGame.getActiveGame().getGameLogic().getGameLogicTask().getNextAction() > 0) {
      	            line = line.replaceAll("<event_starting>", String.valueOf(EventGame.getActiveGame().getGameLogic().getGameLogicTask().getNextActionTime()));
                } else {
                    line = line.replaceAll("<event_starting>", String.valueOf(0));
                }
	            line = line.replaceAll("<event_players_remaining>", String.valueOf(EventGame.getActiveGame().getRemainingPlayers()));
	            line = line.replaceAll("<event_players_max>", String.valueOf(EventGame.getActiveGame().getMaximumPlayers()));
	            line = line.replaceAll("<event_round>", String.valueOf(EventGame.getActiveGame().getGameLogic().getRoundNumber()));
                line = line.replaceAll("<player_a>", EventGame.getActiveGame().getGameLogic().getPlayerA());
                line = line.replaceAll("<player_b>", EventGame.getActiveGame().getGameLogic().getPlayerB());
            }

            if (profile.getParty() != null) {
                line = line.replaceAll("<leader>", profile.getParty().getLeader().getName());
                line = line.replaceAll("<party-size>", String.valueOf(profile.getParty().getListOfPlayers().size()));
            }

             Match match = profile.getMatch();
	     if (match != null) {
                if (match instanceof BasicTeamMatch) {
                    GameParticipant<MatchGamePlayer> participantA = match.getParticipantA();
                    GameParticipant<MatchGamePlayer> participantB = match.getParticipantB();

                    boolean aTeam = match.getParticipantA().containsPlayer(player.getUniqueId());
                    GameParticipant<MatchGamePlayer> playerTeam = aTeam ? participantA : participantB;
                    GameParticipant<MatchGamePlayer> opponentTeam = aTeam ? participantB : participantA;

                    line = line.replaceAll("<opponentsCount>", String.valueOf(opponentTeam.getAliveCount()))
                            .replaceAll("<opponentsMax>", String.valueOf(opponentTeam.getPlayers().size()))
                            .replaceAll("<teamCount>", String.valueOf(playerTeam.getAliveCount()))
                            .replaceAll("<teamMax>", String.valueOf(playerTeam.getPlayers().size()));
                }
                if (match instanceof BasicFreeForAllMatch) {
                    BasicFreeForAllMatch basicFreeForAllMatch = (BasicFreeForAllMatch) match;
                    line = line.replaceAll("<remaning>", String.valueOf(basicFreeForAllMatch.getRemainingTeams()));
                }
			}

            if (profile.getState() == ProfileState.FIGHTING) {
                boolean aTeam = match.getParticipantA().containsPlayer(player.getUniqueId());
                line = line.replaceAll("<rival>", match.getOpponent(player).getName());
                line = line.replaceAll("<rival-ping>", String.valueOf(BukkitReflection.getPing(match.getOpponent(player))));
                line = line.replaceAll("<your-hits>", String.valueOf(match.getGamePlayer(player).getHits()));
                line = line.replaceAll("<their-hits>", String.valueOf(match.getGamePlayer(match.getOpponent(player)).getHits()));
                line = line.replaceAll("<your-combo>", String.valueOf(match.getGamePlayer(player).getCombo()));
                line = line.replaceAll("<their-combo>", String.valueOf(match.getGamePlayer(match.getOpponent(player)).getCombo()));
                line = line.replaceAll("<diffrence>", getDifference(player));
                if (aTeam) {
                    if (match.getKit().getGameRules().isStickFight()) {
                        line = line.replaceAll("<your_lives>", String.valueOf(match.getPlayerALives()));
                        line = line.replaceAll("<their_lives>", String.valueOf(match.getPlayerBLives()));
                    }
                    line = line.replaceAll("<kills>", String.valueOf(match.getPlayerAKills()));
                } else {
                    if (match.getKit().getGameRules().isStickFight()) {
                        line = line.replaceAll("<your_lives>", String.valueOf(match.getPlayerBLives()));
                        line = line.replaceAll("<their_lives>", String.valueOf(match.getPlayerALives()));
				}
                    line = line.replaceAll("<kills>", String.valueOf(match.getPlayerBKills()));
                }
                line = line.replaceAll("<duration>", match.getDuration());
                line = line.replaceAll("<kit>", match.getKit().getName());
                line = line.replaceAll("<arena>", match.getArena().getName());
                if (match.getKit().getGameRules().isBedfight()) {
				    line = line.replaceAll("<bedA>", match.isBedABroken() ? CC.RED + CC.X : CC.GREEN + CC.CHECKMARK + (aTeam ? " &7(You)" : ""));
                    line = line.replaceAll("<bedB>", match.isBedBBroken() ? CC.RED + CC.X : CC.GREEN + CC.CHECKMARK + (aTeam ? "" : " &7(You)"));
                } else if (match.getKit().getGameRules().isEggwars()) {
				    line = line.replaceAll("<eggA>", match.isEggABroken() ? CC.RED + CC.X : CC.GREEN + CC.CHECKMARK);
                    line = line.replaceAll("<eggB>", match.isEggBBroken() ? CC.RED + CC.X : CC.GREEN + CC.CHECKMARK);
			    } else if (match.getKit().getGameRules().isBridge() || match.getKit().getGameRules().isBattleRush()) {
			   	    line = line.replaceAll("<portalA>", getPortalShit(player, "A"));
                    line = line.replaceAll("<portalB>", getPortalShit(player, "B"));
                    line = line.replaceAll("<match_round>", String.valueOf(match.getRoundNumberBridge() + 1));
                }
            }

            if (profile.getState() == ProfileState.SPECTATING) {
                line = line.replaceAll("<duration>", match.getDuration());
                line = line.replaceAll("<kit>", match.getKit().getName());
                line = line.replaceAll("<arena>", match.getArena().getName());
                line = line.replaceAll("<player_a>", match.getParticipantA().getConjoinedNames());
                line = line.replaceAll("<player_b>", match.getParticipantB().getConjoinedNames());
                line = line.replaceAll("<ping_a>", String.valueOf((BukkitReflection.getPing(match.getParticipantA().getPlayer()))));
                line = line.replaceAll("<ping_b>", String.valueOf((BukkitReflection.getPing(match.getParticipantB().getPlayer()))));
                if (match.getKit().getGameRules().isBedfight() || match.getKit().getGameRules().isBridge()) {
                    line = line.replaceAll("<player_a_kills>", String.valueOf(match.getPlayerAKills()));
                    line = line.replaceAll("<player_b_kills>", String.valueOf(match.getPlayerBKills()));
                }
            }

            if (Moon.get().isPlaceholder()) {
                formattedLines.add(PlaceholderAPI.setPlaceholders(player, line));
            } else {
                formattedLines.add(line);
            }
        }
        return formattedLines;
    }

    public String getDifference(Player player){
        Profile profile = Profile.getByUuid(player.getUniqueId());
        Match match = profile.getMatch();
        if(match.getGamePlayer(player).getHits() - match.getGamePlayer(match.getOpponent(player)).getHits() > 0){
            return CC.translate("&a(+" + (match.getGamePlayer(player).getHits() - match.getGamePlayer(match.getOpponent(player)).getHits()) + ")");
        } else if(match.getGamePlayer(player).getHits() - match.getGamePlayer(match.getOpponent(player)).getHits() < 0){
            return CC.translate("&c(" + (match.getGamePlayer(player).getHits() - match.getGamePlayer(match.getOpponent(player)).getHits()) + ")");
        } else {
            return CC.translate("&e(" + (match.getGamePlayer(player).getHits() - match.getGamePlayer(match.getOpponent(player)).getHits()) + ")");
         }
    }

	private String getPortalShit(Player player, String team) {
        ChatColor main;
        ChatColor normal;
        Profile profile = Profile.getByUuid(player.getUniqueId());
        Match match = profile.getMatch();
        boolean aTeam = match.getParticipantA().containsPlayer(player.getUniqueId());
        int playerPortalCount;
        if (team.equals("A")) {
            playerPortalCount = match.portalCountA;
        } else {
            playerPortalCount = match.portalCountB;
        }
        if (team.equals("A")) {
            main = ChatColor.RED;
            normal = ChatColor.GRAY;
        } else {
            main = ChatColor.BLUE;
            normal = ChatColor.GRAY;
        }
        if (match.getKit().getGameRules().isBridge()) {
            switch (playerPortalCount) {
                case 5:
                    return main + "⬤⬤⬤⬤⬤";
                case 4:
                    return main + "⬤⬤⬤⬤" + normal + "⬤";
                case 3:
                    return main + "⬤⬤⬤" + normal +"⬤⬤";
                case 2:
                    return main + "⬤⬤" + normal + "⬤⬤⬤";
                case 1:
                    return main + "⬤" + normal + "⬤⬤⬤⬤";
                default:
                    return normal + "⬤⬤⬤⬤⬤";
            }
        } else if (match.getKit().getGameRules().isBattleRush()) {
            switch (playerPortalCount) {
                case 3:
                    return main + "⬤⬤⬤";
                case 2:
                    return main + "⬤⬤" + normal + "⬤";
                case 1:
                    return main + "⬤" + normal + "⬤⬤";
                default:
                    return normal + "⬤⬤⬤";
            }
        }
        return "null";
    }
}
