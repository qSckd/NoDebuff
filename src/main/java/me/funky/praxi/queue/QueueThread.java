package me.funky.praxi.queue;

import me.funky.praxi.Praxi;
import me.funky.praxi.arena.Arena;
import me.funky.praxi.match.Match;
import me.funky.praxi.match.impl.BasicTeamMatch;
import me.funky.praxi.match.participant.MatchGamePlayer;
import me.funky.praxi.participant.GameParticipant;
import me.funky.praxi.participant.TeamGameParticipant;
import me.funky.praxi.match.Match;
import me.funky.praxi.util.CC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class QueueThread extends Thread {

	@Override
	public void run() {
		while (true) {
			try {
				for (Queue queue : Queue.getQueues()) {
					queue.getPlayers().forEach(QueueProfile::tickRange);
					if (queue.isDuos()) {
						if (queue.getPlayers().size() < 4) {
							continue;
						}

						for (QueueProfile firstQueueProfile : queue.getPlayers()) {
							Player firstPlayer = Bukkit.getPlayer(firstQueueProfile.getPlayerUuid());

							if (firstPlayer == null) {
								continue;
							}

							for (QueueProfile secondQueueProfile : queue.getPlayers()) {
								if (firstQueueProfile.equals(secondQueueProfile)) {
									continue;
								}

								Player secondPlayer = Bukkit.getPlayer(secondQueueProfile.getPlayerUuid());

								if (secondPlayer == null) {
									continue;
								}


								for (QueueProfile thirdQueueProfile : queue.getPlayers()) {
										if (firstQueueProfile.equals(thirdQueueProfile) || secondQueueProfile.equals(thirdQueueProfile)) {
											continue;
										}

										Player thirdPlayer = Bukkit.getPlayer(thirdQueueProfile.getPlayerUuid());

										if (thirdPlayer == null) {
											continue;
										}

										for (QueueProfile fourthQueueProfile : queue.getPlayers()) {
											if (firstQueueProfile.equals(fourthQueueProfile) || secondQueueProfile.equals(fourthQueueProfile)  || thirdQueueProfile.equals(fourthQueueProfile)) {
												continue;
											}

											Player fourthPlayer = Bukkit.getPlayer(fourthQueueProfile.getPlayerUuid());

											if (fourthPlayer == null) {
												continue;
											}

										// Find arena
										final Arena arena = Arena.getRandomArena(queue.getKit());

										if (arena == null) {
											continue;
										}

										// Update arena
										arena.setActive(true);

										// Remove players from queue
										queue.getPlayers().remove(firstQueueProfile);
										queue.getPlayers().remove(secondQueueProfile);
										queue.getPlayers().remove(thirdQueueProfile);
										queue.getPlayers().remove(fourthQueueProfile);


										Match match;

											MatchGamePlayer leaderA = new MatchGamePlayer(firstPlayer.getUniqueId(), firstPlayer.getName());
											MatchGamePlayer leaderB = new MatchGamePlayer(secondPlayer.getUniqueId(), secondPlayer.getName());

											GameParticipant<MatchGamePlayer> participantA = new TeamGameParticipant<>(leaderA);
											GameParticipant<MatchGamePlayer> participantB = new TeamGameParticipant<>(leaderB);

											MatchGamePlayer gamePlayer1 = new MatchGamePlayer(thirdPlayer.getUniqueId(), thirdPlayer.getName());
											MatchGamePlayer gamePlayer2 = new MatchGamePlayer(fourthPlayer.getUniqueId(), fourthPlayer.getName());

											Random random = new Random();
											if (random.nextBoolean()) {
												participantA.getPlayers().add(gamePlayer2);
												participantB.getPlayers().add(gamePlayer1);
											} else {
												participantA.getPlayers().add(gamePlayer1);
												participantB.getPlayers().add(gamePlayer2);
											}


											// Create match
											match = new BasicTeamMatch(null, queue.getKit(), arena, false, participantA, participantB, false);
											new BukkitRunnable() {
												@Override
												public void run() {
													match.start();
												}
											}.runTask(Praxi.get());

										}
								}
							}
						}
					} else {
						if (queue.getPlayers().size() < 2) {
							continue;
						}

						for (QueueProfile firstQueueProfile : queue.getPlayers()) {
							Player firstPlayer = Bukkit.getPlayer(firstQueueProfile.getPlayerUuid());

							if (firstPlayer == null) {
								continue;
							}

							for (QueueProfile secondQueueProfile : queue.getPlayers()) {
								if (firstQueueProfile.equals(secondQueueProfile)) {
									continue;
								}

								Player secondPlayer = Bukkit.getPlayer(secondQueueProfile.getPlayerUuid());

								if (secondPlayer == null) {
									continue;
								}

//							if (firstProfile.getOptions().isUsingPingFactor() ||
//							    secondProfile.getOptions().isUsingPingFactor()) {
//								if (firstPlayer.getPing() >= secondPlayer.getPing()) {
//									if (firstPlayer.getPing() - secondPlayer.getPing() >= 50) {
//										continue;
//									}
//								} else {
//									if (secondPlayer.getPing() - firstPlayer.getPing() >= 50) {
//										continue;
//									}
//								}
//							}

								if (queue.isRanked()) {
									if (!firstQueueProfile.isInRange(secondQueueProfile.getElo()) ||
											!secondQueueProfile.isInRange(firstQueueProfile.getElo())) {
										continue;
									}
								}

								// Find arena
								final Arena arena = Arena.getRandomArena(queue.getKit());

								if (arena == null) {
									continue;
								}

								// Update arena
								arena.setActive(true);

								// Remove players from queue
								queue.getPlayers().remove(firstQueueProfile);
								queue.getPlayers().remove(secondQueueProfile);

								MatchGamePlayer playerA = new MatchGamePlayer(firstPlayer.getUniqueId(),
										firstPlayer.getName(), firstQueueProfile.getElo());

								MatchGamePlayer playerB = new MatchGamePlayer(secondPlayer.getUniqueId(),
										secondPlayer.getName(), secondQueueProfile.getElo());

								GameParticipant<MatchGamePlayer> participantA = new GameParticipant<>(playerA);
								GameParticipant<MatchGamePlayer> participantB = new GameParticipant<>(playerB);

								// Create match
								Match match = new BasicTeamMatch(queue, queue.getKit(), arena, queue.isRanked(),
										participantA, participantB, false);

								String[] opponentMessages = formatMessages(firstPlayer.getName(),
										secondPlayer.getName(), firstQueueProfile.getElo(), secondQueueProfile.getElo(),
										queue.isRanked());

								firstPlayer.sendMessage(opponentMessages[0]);
								secondPlayer.sendMessage(opponentMessages[1]);

								new BukkitRunnable() {
									@Override
									public void run() {
										match.start();
									}
								}.runTask(Praxi.get());
							}
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();

				try {
					Thread.sleep(100L);
				} catch (InterruptedException e2) {
					e2.printStackTrace();
				}

				continue;
			}

			try {
				Thread.sleep(100L);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private String[] formatMessages(String player1, String player2, int player1Elo, int player2Elo, boolean ranked) {
		String player1Format = player1 + (ranked ? CC.PINK + " (" + player1Elo + ")" : "");
		String player2Format = player2 + (ranked ? CC.PINK + " (" + player2Elo + ")" : "");

		return new String[]{
				CC.YELLOW + CC.BOLD + "Found opponent: " + CC.GREEN + player1Format + CC.YELLOW + " vs. " +
				CC.RED + player2Format,
				CC.YELLOW + CC.BOLD + "Found opponent: " + CC.GREEN + player2Format + CC.YELLOW + " vs. " +
				CC.RED + player1Format
		};
	}
}
