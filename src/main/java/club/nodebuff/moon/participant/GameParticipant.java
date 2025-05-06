package club.nodebuff.moon.participant;

import lombok.Getter;
import lombok.Setter;
import club.nodebuff.moon.util.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class GameParticipant<T extends GamePlayer> {

	@Getter private final T leader;
	@Getter @Setter private int roundWins;
	@Getter @Setter private boolean eliminated;

	public GameParticipant(T leader) {
		this.leader = leader;
	}

	public List<T> getPlayers() {
		return Collections.singletonList(leader);
	}

	public boolean isAllDead() {
		return leader.isDead();
	}

	public int getAliveCount() {
		return leader.isDead() ? 0 : 1;
	}

	public boolean containsPlayer(UUID uuid) {
		return leader.getUuid().equals(uuid);
	}

	public String getConjoinedNames() {
		return leader.getUsername();
	}

    public String getName() {
		return leader.getUsername();
	}

    public Player getPlayer() {
		return Bukkit.getPlayer(leader.getUuid());
	}

	public void reset() {
		for (GamePlayer gamePlayer : getPlayers()) {
			gamePlayer.setDead(false);
		}
	}

	public void sendMessage(String message) {
		for (GamePlayer gamePlayer : getPlayers()) {
			if (!gamePlayer.isDisconnected()) {
				Player player = gamePlayer.getPlayer();

				if (player != null) {
					player.sendMessage(message);
				}
			}
		}
	}

	public void sendMessage(List<String> messages) {
		for (GamePlayer gamePlayer : getPlayers()) {
			if (!gamePlayer.isDisconnected()) {
				Player player = gamePlayer.getPlayer();

				if (player != null) {
					for (String message : messages) {
						player.sendMessage(message);
					}
				}
			}
		}
	}

	public void sendTitle(String header, String footer, int duration) {
        for (GamePlayer gamePlayer : getPlayers()) {
            if (!gamePlayer.isDisconnected()) {
                PlayerUtil.sendTitle(gamePlayer.getPlayer(), header, footer, duration);
            }
        }
    }

	public void sendSound(Sound sound, float volume, float pitch) {
		for (GamePlayer gamePlayer : getPlayers()) {
			if (!gamePlayer.isDisconnected()) {
				Player player = gamePlayer.getPlayer();

				if (player != null) {
					player.playSound(player.getLocation(), sound, volume, pitch);
				}
			}
		}
	}
	public void addSpeed() {
        for (GamePlayer gamePlayer : getPlayers()) {
            if (!gamePlayer.isDisconnected()) {
                Player player = gamePlayer.getPlayer();

                if (player != null) {
                    PotionEffect speedEffect = new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1, true, false);
                    player.addPotionEffect(speedEffect);
                }
            }
        }
    }
}
