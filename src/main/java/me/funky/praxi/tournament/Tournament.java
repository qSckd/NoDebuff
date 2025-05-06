package me.funky.praxi.tournament;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import me.funky.praxi.Praxi;
import me.funky.praxi.arena.Arena;
import me.funky.praxi.kit.Kit;
import me.funky.praxi.match.impl.BasicTeamMatch;
import me.funky.praxi.match.participant.MatchGamePlayer;
import me.funky.praxi.participant.GameParticipant;
import me.funky.praxi.party.Party;
import me.funky.praxi.profile.Profile;
import me.funky.praxi.profile.ProfileState;
import me.funky.praxi.util.CC;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

/* Author: Zatrex
   Project: Shadow
   Date: 2024/5/15
 */

public class Tournament {

    public static BukkitRunnable RUNNABLE;
    @Getter private static Tournament currentTournament;

    static {
        Tournament.RUNNABLE = null;
        Tournament.currentTournament = null;
    }

    @Getter private List<Party> participants;
    @Getter private List<TournamentMatch> tournamentMatches;
    @Getter @Setter private Kit kit;
    @Getter @Setter private int participatingCount;
    @Getter private int round;
    @Getter @Setter private int teamCount;
    @Getter @Setter private boolean canceled;

    public Tournament() {

        this.participants = new ArrayList<Party>() {
            @Override
            public Iterator<Party> iterator() {
                this.filter();
                return super.iterator();
            }

            @Override
            public int size() {
                this.filter();
                return super.size();
            }

            private void filter() {
                final List<Party> toRemove = Lists.newArrayList();
                for (int i = 0; i < super.size(); ++i) {
                    final Party party = this.get(i);
                    if (party.isDisbanded()) {
                        toRemove.add(party);
                    }
                }
                this.removeAll(toRemove);
            }
        };
        this.tournamentMatches = Lists.newArrayList();
        this.canceled = false;
    }

    public static void createNewTournament() {

        if (getCurrentTournament() != null) {
            throw new RuntimeException("Tournament is already running!");
        }
        currentTournament = new Tournament();
    }

    public static void cancelCurrentTournament() {

        if (getCurrentTournament() == null) {
            throw new RuntimeException("There is no tournament running!");
        }
        currentTournament.cancel();
        currentTournament = null;
    }

    public boolean isParticipating(final Player player) {
        final Party party = Profile.getByUuid(player.getUniqueId()).getParty();
        return party != null && this.participants.contains(Profile.getByUuid(player.getUniqueId()).getParty());
    }

    public boolean hasStarted() {
        return this.round != 0;
    }

    public int getStartingParticipatingCount() {
        if (this.participatingCount == 0) {
            return this.getParticipatingCount();
        }
        return this.participatingCount;
    }

    public int getParticipatingCount() {
        return this.participants.size();
    }

    public void leave(final Party party) {
        Preconditions.checkState(this.round == 0, "This tournament already started.");
        if (this.participants.contains(party)) {
            for (final Party partyparticipants : this.participants) {
                for (final UUID uuid : partyparticipants.getPlayers()) {
                    Player player = Bukkit.getPlayer(uuid);
                    player.sendMessage(CC.translate(CC.GRAY + "[" + "&b" + CC.BOLD + "Tournament" + CC.GRAY + "] " + CC.RED + party.getLeader().getPlayer().getName() + CC.WHITE + " has left " + CC.GRAY + "(" + this.participants.size() + "/50)"));
                }
            }
            this.participants.remove(party);
        }
    }

    public void participate(final Party party) {
        Preconditions.checkState(this.round == 0, "This tournament already started.");
        if (!this.participants.contains(party)) {
            this.participants.add(party);
            for (final Party partyparticipants : this.participants) {
                for (final UUID uuid : partyparticipants.getPlayers()) {
                    Player player = Bukkit.getPlayer(uuid);
                    player.sendMessage(CC.translate(CC.GRAY + "[" + "&b" + CC.BOLD + "Tournament" + CC.GRAY + "] " + CC.GREEN + party.getLeader().getPlayer().getName() + CC.WHITE + " has joined " + CC.GRAY + "(" + this.participants.size() + "/50)"));
                }
            }
        }
    }

    public void cancel() {
        this.canceled = true;
        this.participants.clear();
        this.tournamentMatches.clear();
    }

    public void start() {
        Collections.shuffle(this.participants);
        if (this.participatingCount == 0) {
            this.participatingCount = this.participants.size();
        }
        Bukkit.broadcastMessage(CC.translate(CC.GRAY + "[" + "&b" + CC.BOLD + "Tournament" + CC.GRAY + "] " + ChatColor.DARK_AQUA + "Round " + (this.round++ + 1) + ChatColor.WHITE + " has started!."));
        final Iterator<Party> iterator = this.participants.iterator();
        while (iterator.hasNext()) {
            Party player = iterator.next();
            if (!iterator.hasNext()) {
                player.sendMessage(CC.translate(CC.GRAY + "[" + "&b" + CC.BOLD + "Tournament" + CC.GRAY + "] " + ChatColor.RED + "You do not have any player to fight! Please wait for the next round"));
                break;
            }
            Party other = iterator.next();
            Arena arena = Arena.getRandomArena(this.kit);
            GameParticipant<MatchGamePlayer> teamA = new GameParticipant<>(new MatchGamePlayer(player.getLeader().getPlayer().getUniqueId(), player.getLeader().getPlayer().getDisplayName()));
            GameParticipant<MatchGamePlayer> teamB = new GameParticipant<>(new MatchGamePlayer(other.getLeader().getPlayer().getUniqueId(), other.getLeader().getPlayer().getDisplayName()));
            TournamentMatch tournamentMatch = new TournamentMatch(teamA, teamB, this.getKit(), arena);
            for (UUID uuid : player.getPlayers()) {
                Player player2 = Bukkit.getPlayer(uuid);
                Profile otherData = Profile.getByUuid(player2.getUniqueId());
                otherData.setState(ProfileState.FIGHTING);
                otherData.setMatch(tournamentMatch);
                if (!player.getLeader().getUniqueId().equals(player2.getUniqueId())) {
                    teamA.getPlayers().add(new MatchGamePlayer(player2.getUniqueId(), player2.getName()));
                }
            }
            for (UUID uuid : other.getPlayers()) {
                Player player2 = Bukkit.getPlayer(uuid);
                Profile otherData = Profile.getByUuid(player2.getUniqueId());
                otherData.setState(ProfileState.FIGHTING);
                otherData.setMatch(tournamentMatch);
                if (!other.getLeader().getUniqueId().equals(player2.getUniqueId())) {
                    teamB.getPlayers().add(new MatchGamePlayer(player2.getUniqueId(), player2.getName()));
                }
            }
            tournamentMatch.start();
            this.tournamentMatches.add(tournamentMatch);
        }
    }

    public class TournamentMatch extends BasicTeamMatch {
        private GameParticipant<MatchGamePlayer> participantA;
        private GameParticipant<MatchGamePlayer> participantB;

        public TournamentMatch(final GameParticipant<MatchGamePlayer> teamA, final GameParticipant<MatchGamePlayer> teamB, final Kit ladder, final Arena arena) {
            super(null, ladder, arena, false, teamA, teamB, true); // TODO: Check if bolt gives coins if player wins a tournament round
            this.participantA = teamA;
            this.participantB = teamB;
        }

        @Override
        public void end() {
            super.end();
            if (!Tournament.this.canceled) {
                final GameParticipant<MatchGamePlayer> winningTeam = this.getWinningParticipant();
                final GameParticipant<MatchGamePlayer> losingTeam = this.getLosingParticipant();
                for (final MatchGamePlayer losingPlayer : losingTeam.getPlayers()) {
                    Tournament.this.participants.remove(Profile.getByUuid(losingPlayer.getUuid()).getParty());
                }
                Tournament.this.tournamentMatches.remove(this);
                final StringBuilder builder = new StringBuilder();
                for (final MatchGamePlayer matchPlayer : winningTeam.getPlayers()) {
                    builder.append(CC.translate(matchPlayer.getPlayer().getName()));
                    builder.append(" ");
                }
                StringBuilder builders = new StringBuilder();
                for (final MatchGamePlayer matchPlayer2 : losingTeam.getPlayers()) {
                    builders.append(CC.translate(matchPlayer2.getPlayer().getName()));
                    builders.append(" ");
                }
                Bukkit.broadcastMessage(CC.translate(CC.GRAY + "[" + "&b" + CC.BOLD + "Tournament" + CC.GRAY + "] " + CC.RESET + CC.translate(builders.toString()) + ChatColor.WHITE + "has been eliminated. " + ChatColor.GRAY + "(" + Tournament.this.participants.size() + "/" + Tournament.this.participatingCount + ")"));
                if (Tournament.this.tournamentMatches.isEmpty()) {
                    if (Tournament.this.participants.size() <= 1) {
                        Bukkit.broadcastMessage(CC.translate(CC.BLUE + CC.BOLD + ""));
                        Bukkit.broadcastMessage(CC.translate(CC.GRAY + "[" + "&b" + CC.BOLD + "Tournament" + CC.GRAY + "] " + CC.RESET + CC.translate(builder.toString()) + ChatColor.GREEN + "won the tournament"));
                        Bukkit.broadcastMessage(CC.translate(CC.BLUE + CC.BOLD + ""));
                        Tournament.currentTournament = null;
                    } else {
                        new BukkitRunnable() {
                            public void run() {
                                Tournament.this.start();
                            }
                        }.runTaskLater(Praxi.get(), 100L);
                    }
                }
            }
        }
    }
}
