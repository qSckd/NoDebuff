package club.nodebuff.moon;

import lombok.AllArgsConstructor;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import club.nodebuff.moon.util.ReplaceUtil;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public enum Locale {

	KIT_EDITOR_START_RENAMING("KIT_EDITOR.START_RENAMING"),
	KIT_EDITOR_RENAMED("KIT_EDITOR.RENAMED"),
	KIT_EDITOR_NAME_TOO_LONG("KIT_EDITOR.NAME_TOO_LONG"),
	FOLLOW_START("FOLLOW.FOLLOW_START"),
	FOLLOW_END("FOLLOW.FOLLOW_END"),
	FOLLOWED_LEFT("FOLLOW.FOLLOWED_LEFT"),
	DUEL_SENT("DUEL.SENT"),
	DUEL_SENT_PARTY("DUEL.SENT_PARTY"),
	DUEL_RECEIVED("DUEL.RECEIVED"),
	DUEL_RECEIVED_PARTY("DUEL.RECEIVED_PARTY"),
	DUEL_RECEIVED_HOVER("DUEL.RECEIVED_HOVER"),
	DUEL_RECEIVED_CLICKABLE("DUEL.RECEIVED_CLICKABLE"),
	PARTY_HELP("PARTY.HELP"),
	PARTY_CREATE("PARTY.CREATE"),
	PARTY_DISBAND("PARTY.DISBAND"),
	PARTY_INVITE("PARTY.INVITE"),
	PARTY_INVITE_HOVER("PARTY.INVITE_HOVER"),
	PARTY_INVITE_BROADCAST("PARTY.INVITE_BROADCAST"),
	PARTY_JOIN("PARTY.JOIN"),
	PARTY_LEAVE("PARTY.LEAVE"),
	PARTY_PRIVACY_CHANGE("PARTY.PRIVACY_CHANGE"),
	PARTY_CHAT_PREFIX("PARTY.CHAT_PREFIX"),
	QUEUE_JOIN_UNRANKED("QUEUE.JOIN_UNRANKED"),
	QUEUE_LEAVE_UNRANKED("QUEUE.LEAVE_UNRANKED"),
	QUEUE_JOIN_RANKED("QUEUE.JOIN_RANKED"),
	RANKED_ERROR("QUEUE.RANKED-ERROR-MESSAGE"),
	QUEUE_LEAVE_RANKED("QUEUE.LEAVE_RANKED"),
	QUEUE_RANGE_INCREMENT("QUEUE.RANGE_INCREMENT"),
	MATCH_GIVE_KIT("MATCH.GIVE_KIT"),
	MATCH_ENDERPEARL_COOLDOWN("MATCH.ENDERPEARL_COOLDOWN"),
	MATCH_ENDERPEARL_COOLDOWN_EXPIRED("MATCH.ENDERPEARL_COOLDOWN_EXPIRED"),
	MATCH_START_SPECTATING("MATCH.START_SPECTATING"),
	MATCH_START_SPECTATING_RANKED("MATCH.START_SPECTATING_RANKED"),
	MATCH_START_SPECTATING_FFA("MATCH.START_SPECTATING_FFA"),
	MATCH_NOW_SPECTATING("MATCH.NOW_SPECTATING"),
	MATCH_NO_LONGER_SPECTATING("MATCH.NO_LONGER_SPECTATING"),
	MATCH_START_TIMER("MATCH.START_TIMER"),
	MATCH_BED_BROKEN("MATCH.BED_BROKEN"),
	MATCH_STARTED("MATCH.STARTED"),
	MATCH_WARNING("MATCH.WARNING"),
	MATCH_END_DETAILS("MATCH.END_DETAILS"),
	MATCH_END_WINNER_INVENTORY("MATCH.END_WINNER_INVENTORY"),
	MATCH_END_LOSER_INVENTORY("MATCH.END_LOSER_INVENTORY"),
	MATCH_CLICK_TO_VIEW_NAME("MATCH.CLICK_TO_VIEW_NAME"),
	MATCH_CLICK_TO_VIEW_HOVER("MATCH.CLICK_TO_VIEW_HOVER"),
	MATCH_ELO_CHANGES("MATCH.ELO_CHANGES"),
	MATCH_PLAYER_KILLED("MATCH.PLAYER_KILLED"),
	MATCH_PLAYER_DIED("MATCH.PLAYER_DIED"),
	MATCH_ROUNDS_TO_WIN("MATCH.ROUNDS_TO_WIN"),
	REMATCH_SENT_REQUEST("REMATCH.SENT_REQUEST"),
	REMATCH_RECEIVED_REQUEST("REMATCH.RECEIVED_REQUEST"),
	REMATCH_RECEIVED_REQUEST_HOVER("REMATCH.RECEIVED_REQUEST_HOVER"),
	ARROW_DAMAGE_INDICATOR("ARROW_DAMAGE_INDICATOR"),
	VIEWING_INVENTORY("VIEWING_INVENTORY"),
	EVENT_JOIN_BROADCAST("EVENT.JOIN_BROADCAST"),
	EVENT_JOIN_HOVER("EVENT.JOIN_HOVER"),
	EVENT_PLAYER_JOIN("EVENT.PLAYER_JOIN"),
	EVENT_PLAYER_LEAVE("EVENT.PLAYER_LEAVE"),
	EVENT_PLAYER_VOTE("EVENT.PLAYER_VOTE"),
	EVENT_ROUND_START("EVENT.ROUND_START"),
	EVENT_ROUND_START_TIMER("EVENT.ROUND_START_TIMER"),
	EVENT_ROUND_ELIMINATION("EVENT.ROUND_ELIMINATION"),
	EVENT_ROUND_OPPONENT("EVENT.ROUND_OPPONENT"),
	EVENT_START("EVENT.STARTED"),
	EVENT_FINISH("EVENT.FINISHED"),
	OPTIONS_SCOREBOARD_ENABLED("OPTIONS.SCOREBOARD_ENABLED"),
	OPTIONS_SCOREBOARD_DISABLED("OPTIONS.SCOREBOARD_DISABLED"),
	OPTIONS_RECEIVE_DUEL_REQUESTS_ENABLED("OPTIONS.RECEIVE_DUEL_REQUESTS_ENABLED"),
	OPTIONS_RECEIVE_DUEL_REQUESTS_DISABLED("OPTIONS.RECEIVE_DUEL_REQUESTS_DISABLED"),
	OPTIONS_SPECTATORS_ENABLED("OPTIONS.SPECTATORS_ENABLED"),
	OPTIONS_SPECTATORS_DISABLED("OPTIONS.SPECTATORS_DISABLED");

	private final String path;

	public String format(Object... objects) {
		String raw = Moon.get().getMainConfig().getString(path);
		if (raw == null) return "";
		return new MessageFormat(ChatColor.translateAlternateColorCodes('&', raw)).format(objects);
	}

	public String format(Player player, Object... objects) {
		String raw = Moon.get().getMainConfig().getString(path);
		if (raw == null) return "";
		List<String> list = new ArrayList<>();
		list.add(new MessageFormat(ChatColor.translateAlternateColorCodes('&', raw)).format(objects));
		return ReplaceUtil.format(list, player).toString().replace("[", "").replace("]", "");
	}

	public List<String> formatLines(Player player, Object... objects) {
		List<String> lines = new ArrayList<>();
		Object raw = Moon.get().getMainConfig().get(path);

		if (raw instanceof String) {
			lines.add(new MessageFormat(ChatColor.translateAlternateColorCodes('&', (String) raw)).format(objects));
		} else if (raw instanceof List) {
			for (String line : Moon.get().getMainConfig().getStringList(path)) {
				lines.add(new MessageFormat(ChatColor.translateAlternateColorCodes('&', line)).format(objects));
			}
		}

		return ReplaceUtil.format(lines, player);
	}

	public List<String> formatLines(Object... objects) {
		List<String> lines = new ArrayList<>();
		Object raw = Moon.get().getMainConfig().get(path);

		if (raw instanceof String) {
			lines.add(new MessageFormat(ChatColor.translateAlternateColorCodes('&', (String) raw)).format(objects));
		} else if (raw instanceof List) {
			for (String line : Moon.get().getMainConfig().getStringList(path)) {
				lines.add(new MessageFormat(ChatColor.translateAlternateColorCodes('&', line)).format(objects));
			}
		}

		return lines;
	}

	@Override
	public String toString() {
		return format();
	}
}
