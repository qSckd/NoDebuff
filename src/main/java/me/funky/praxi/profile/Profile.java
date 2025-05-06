package me.funky.praxi.profile;

import lombok.Getter;
import lombok.Setter;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.common.collect.Lists;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import me.funky.praxi.Praxi;
import me.funky.praxi.arena.Arena;
import me.funky.praxi.divisions.ProfileDivision;
import me.funky.praxi.duel.DuelProcedure;
import me.funky.praxi.duel.DuelRequest;
import me.funky.praxi.match.Match;
import me.funky.praxi.match.MatchInfo;
import me.funky.praxi.party.Party;
import me.funky.praxi.queue.QueueProfile;
import me.funky.praxi.kit.Kit;
import me.funky.praxi.kit.KitLoadout;
import me.funky.praxi.kit.KitLeaderboards;
import me.funky.praxi.profile.meta.ProfileKitEditorData;
import me.funky.praxi.profile.meta.ProfileKitData;
import me.funky.praxi.profile.meta.ProfileRematchData;
import me.funky.praxi.profile.meta.option.ProfileOptions;
import me.funky.praxi.profile.option.killeffect.SpecialEffects;
import me.funky.praxi.profile.option.killmessages.KillMessages;
import me.funky.praxi.profile.option.trail.Trail;
import me.funky.praxi.profile.times.Times;
import me.funky.praxi.profile.themes.Themes;
import me.funky.praxi.util.CC;
import me.funky.praxi.util.Cooldown;
import me.funky.praxi.util.InventoryUtil;
import me.funky.praxi.util.config.BasicConfigurationFile;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.scheduler.BukkitRunnable;
import org.bson.Document;

import java.io.IOException;
import java.util.*;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class Profile {

	@Getter private static Map<UUID, Profile> profiles = new HashMap<>();
	public static MongoCollection<Document> collection;
    @Getter private static final List<KitLeaderboards> globalEloLeaderboards = new ArrayList<>();

	@Getter @Setter int globalElo = 1000;
	@Getter @Setter int globalExp = 0;
	@Getter private UUID uuid;
	@Getter private String name;
    @Getter @Setter private final List<UUID> followers;
    @Getter @Setter private final List<UUID> following;
	@Getter @Setter private ProfileState state;
	@Getter private final ProfileOptions options;
	@Getter private final ProfileKitEditorData kitEditorData;
	@Getter private final Map<Kit, ProfileKitData> kitData;
    @Getter private final List<MatchInfo> matchHistory = Lists.newArrayList();
	@Getter private final List<DuelRequest> duelRequests;
	@Getter @Setter private DuelProcedure duelProcedure;
	@Getter @Setter private ProfileRematchData rematchData;
	@Getter @Setter private Party party;
	@Getter @Setter private Match match;
	@Getter @Setter private QueueProfile queueProfile;
	@Getter @Setter private Cooldown enderpearlCooldown;
	@Getter @Setter private Cooldown voteCooldown;
	@Getter @Setter private List<String> permissions;


    /**
	 * This stores a player's amount of coins.
	 */
	@Getter @Setter private int coins;

    /**
	 * This stores a player's amount of event tokens.
	 */
	@Getter @Setter private int eventTokens;

	/**
	 * This stores a player's exp.
	 */
	@Getter @Setter private int experience;

    /**
	 * This stores a player's level.
	 */
	@Getter @Setter private int level;

    /**
	 * This stores a player's ranked ban status.
	 */
    @Getter @Setter private boolean rankedBan;

	/**
	 * This stores the reason for a player's ranked ban.
	 */
	@Getter @Setter private String rankedBanReason;

	/**
	 * This stores the ID for a player's ranked ban.
	 */
	@Getter @Setter private String rankedBanID;

	public Profile(UUID uuid) {
		this.uuid = uuid;
		this.state = ProfileState.LOBBY;
		this.options = new ProfileOptions();
		this.kitEditorData = new ProfileKitEditorData();
		this.kitData = new HashMap<>();
		this.duelRequests = new ArrayList<>();
        this.followers = new ArrayList<>();
        this.following = new ArrayList<>();
		this.enderpearlCooldown = new Cooldown(0);
		this.voteCooldown = new Cooldown(0);
		this.permissions = new ArrayList<>();
		this.rankedBan = false;
		this.rankedBanReason = "None";
		this.rankedBanID = "None";
		this.coins = 0;
        this.eventTokens = 0;
		this.experience = 0;
		this.level = 0;

		for (Kit kit : Kit.getKits()) {
			this.kitData.put(kit, new ProfileKitData());
		}
		this.calculateGlobalElo();
	}

	public Profile(Player player) {
		this.uuid = uuid;
		this.state = ProfileState.LOBBY;
		this.options = new ProfileOptions();
		this.kitEditorData = new ProfileKitEditorData();
		this.kitData = new HashMap<>();
		this.duelRequests = new ArrayList<>();
        this.followers = new ArrayList<>();
        this.following = new ArrayList<>();
		this.enderpearlCooldown = new Cooldown(0);
		this.voteCooldown = new Cooldown(0);
		this.permissions = new ArrayList<>();
		this.rankedBan = false;
		this.rankedBanReason = "None";
		this.rankedBanID = "None";
		this.coins = 0;
        this.eventTokens = 0;
		this.experience = 0;
		this.level = 0;

		for (Kit kit : Kit.getKits()) {
			this.kitData.put(kit, new ProfileKitData());
		}
		this.calculateGlobalElo();
	}

	public static void setCollection(MongoCollection<Document> profileCollection) {
        collection = profileCollection;
    }

	public static MongoCollection<Document> getCollection() {
        return collection;
    }

	public int getWins() {
        int wins = 0;
        for (Map.Entry<Kit, ProfileKitData> entry : this.getKitData().entrySet()) {
            ProfileKitData profileKitData = entry.getValue();
            wins += profileKitData.getWon();
        }
        return wins;
    }

    public int getKills() {
        int kills = 0;
        for (Map.Entry<Kit, ProfileKitData> entry : this.getKitData().entrySet()) {
            ProfileKitData profileKitData = entry.getValue();
            kills += profileKitData.getKills();
        }
        return kills;
    }

    public int getLoses() {
        int loses = 0;
        for (Map.Entry<Kit, ProfileKitData> entry : this.getKitData().entrySet()) {
            ProfileKitData profileKitData = entry.getValue();
            loses += profileKitData.getLost();
        }
        return loses;
    }

    public int getElo() {
        int elo = 0;
        int totalQueue = 0;

        for (Map.Entry<Kit, ProfileKitData> entry : this.getKitData().entrySet()) {
            ProfileKitData profileKitData = entry.getValue();
            elo += profileKitData.getElo();
            totalQueue++;
        }
        if (totalQueue == 0) {
            return 0;
        }
        return elo / totalQueue;
    }

	public void calculateGlobalElo() {
		int globalElo = 0;
		int kitCounter = 0;
		for (Kit kit : this.kitData.keySet()) {
			if (kit.getGameRules().isRanked()) {
				globalElo += this.kitData.get(kit).getElo();
				kitCounter++;
			}
		}
		try {
			this.globalElo = Math.round((float) globalElo / kitCounter);
		} catch (ArithmeticException ex) {
			ex.printStackTrace();
			this.globalElo = 1000;
		}
	}

	public Player getPlayer() {
		return Bukkit.getPlayer(uuid);
	}

	public DuelRequest getDuelRequest(Player sender) {
		for (DuelRequest duelRequest : duelRequests) {
			if (duelRequest.getSender().equals(sender.getUniqueId())) {
				return duelRequest;
			}
		}

		return null;
	}

	public boolean isDuelRequestExpired(DuelRequest duelRequest) {
		if (duelRequest != null) {
			if (duelRequest.isExpired()) {
				duelRequests.remove(duelRequest);
				return true;
			}
		}

		return false;
	}

	public boolean isInQueue() {
		return state == ProfileState.QUEUEING;
	}

	public boolean isInMatch() {
		return match != null;
	}

	public boolean isInFight() {
		return state == ProfileState.FIGHTING && match != null;
	}

	public boolean isBusy() {
		return state != ProfileState.LOBBY;
	}

	public void load() {
    Document document = collection.find(Filters.eq("uuid", uuid.toString())).first();

    if (document == null) {
        this.save();
        return;
    }

    Document options = (Document) document.get("options");
    this.options.showScoreboard(((Boolean) options.get("showScoreboard")).booleanValue());
    this.options.receiveDuelRequests(((Boolean) options.get("receiveDuelRequests")).booleanValue());
    this.options.allowSpectators(((Boolean) options.get("allowSpectators")).booleanValue());
    this.options.spectatorMessages(((Boolean) options.get("spectatorMessages")).booleanValue());
    this.options.showPlayers(((Boolean) options.get("showPlayers")).booleanValue());
    this.options.time(Times.valueOf((String) options.get("time")));
    this.options.theme(Themes.valueOf((String) options.get("theme")));
    this.options.killEffect(SpecialEffects.valueOf((String) options.get("killEffect")));
	this.options.killMessage(KillMessages.valueOf((String) options.get("killMessage")));
    this.options.trail(Trail.valueOf((String) options.get("trail")));

    Document playerStatus = (Document) document.get("status");
    this.setRankedBan(((Boolean) playerStatus.get("rankedBan")).booleanValue());
    this.setRankedBanID((String) playerStatus.get("rankedBanID"));
    this.setRankedBanReason((String) playerStatus.get("rankedBanReason"));
    this.setCoins(((Integer) playerStatus.get("coins")).intValue());
    this.setEventTokens(((Integer) playerStatus.get("eventTokens")).intValue());
    this.setLevel(((Integer) playerStatus.get("level")).intValue());
    this.setExperience(((Integer) playerStatus.get("experience")).intValue()); 
    List<String> permissionsList = (List<String>) playerStatus.get("permissions");
    this.setPermissions(permissionsList);


    if (document.containsKey("matches")) {
        Document matchesDocument = (Document) document.get("matches");

        matchesDocument.forEach((s, o) -> {
        Document matchDocument = (Document) o;
        String winningParticipant = matchDocument.getString("winningParticipant");
        String losingParticipant = matchDocument.getString("losingParticipant");
        Kit kit = Kit.getByName(matchDocument.getString("kit"));
        Arena arena = Arena.getByName(matchDocument.getString("arena"));
        long duration = matchDocument.getLong("duration");
        String date = matchDocument.getString("date");
        UUID uuid = UUID.fromString(matchDocument.getString("uuid"));
        String type = matchDocument.getString("type");

        this.getMatchHistory().add(new MatchInfo(winningParticipant, losingParticipant, kit, arena, duration, date, uuid, type));});
    }
    Document kitStatistics = (Document) document.get("kitStatistics");

    for (String key : kitStatistics.keySet()) {
        Document kitDocument = (Document) kitStatistics.get(key);
        Kit kit = Kit.getByName(key);

        if (kit != null) {
            ProfileKitData profileKitData = new ProfileKitData();
            profileKitData.setElo(((Integer) kitDocument.get("elo")).intValue());
            profileKitData.setWon(((Integer) kitDocument.get("won")).intValue());
            profileKitData.setLost(((Integer) kitDocument.get("lost")).intValue());
            profileKitData.setKills(((Integer) kitDocument.get("kills")).intValue());
            profileKitData.setWinstreak(((Integer) kitDocument.get("winstreak")).intValue());

            kitData.put(kit, profileKitData);
        }
    }

    Document kitsDocument = (Document) document.get("loadouts");

    for (String key : kitsDocument.keySet()) {
        Kit kit = Kit.getByName(key);

        if (kit != null) {
            JsonArray kitsArray = new JsonParser().parse((String) kitsDocument.get(key)).getAsJsonArray();
            KitLoadout[] loadouts = new KitLoadout[4];

            for (JsonElement kitElement : kitsArray) {
                JsonObject kitObject = kitElement.getAsJsonObject();

                KitLoadout loadout = new KitLoadout(kitObject.get("name").getAsString());
                loadout.setArmor(InventoryUtil.deserializeInventory(kitObject.get("armor").getAsString()));
                loadout.setContents(InventoryUtil.deserializeInventory(kitObject.get("contents").getAsString()));

                loadouts[kitObject.get("index").getAsInt()] = loadout;
            }

            kitData.get(kit).setLoadouts(loadouts);
        }
    }
}

	public void save() {
		Document document = new Document();
		document.put("uuid", uuid.toString());

		Document optionsDocument = new Document();
		optionsDocument.put("showScoreboard", options.showScoreboard());
		optionsDocument.put("receiveDuelRequests", options.receiveDuelRequests());
		optionsDocument.put("allowSpectators", options.allowSpectators());
        optionsDocument.put("spectatorMessages", options.spectatorMessages());
		optionsDocument.put("showPlayers", options.showPlayers());
		optionsDocument.put("time", options.time().toString());
		optionsDocument.put("theme", options.theme().toString());
		optionsDocument.put("killEffect", options.killEffect().toString());
		optionsDocument.put("killMessage", options.killMessage().toString());
        optionsDocument.put("trail", options.trail().toString());
		document.put("options", optionsDocument);

		Document statusDocument = new Document();
		statusDocument.put("rankedBan", rankedBan);
		statusDocument.put("rankedBanID", rankedBanID);
		statusDocument.put("rankedBanReason", rankedBanReason);
		statusDocument.put("coins", coins);
        statusDocument.put("eventTokens", eventTokens);
		statusDocument.put("level", level);
		statusDocument.put("experience", experience);
        List<String> permissionsList = permissions;
        statusDocument.put("permissions", permissionsList);
		document.put("status", statusDocument);

        if (!this.getMatchHistory().isEmpty()) {
            Document matchesDocument = new Document();
            for (int i = 0; i < this.getMatchHistory().size(); i++) {
                MatchInfo match = this.getMatchHistory().get(i);

                Document matchDocument = new Document()
                        .append("winningParticipant", match.getWinningParticipant())
                        .append("losingParticipant", match.getLosingParticipant())
                        .append("kit", match.getKit().getName())
                        .append("arena", match.getArena().getName())
                        .append("duration", match.getDuration())
                        .append("date", match.getDate())
                        .append("uuid", match.getUuid().toString())
                        .append("type", match.getType());

                matchesDocument.append(String.valueOf(i), matchDocument);
            }
            document.append("matches", matchesDocument);
        }

		Document kitStatisticsDocument = new Document();

		for (Map.Entry<Kit, ProfileKitData> entry : kitData.entrySet()) {
			Document kitDocument = new Document();
			kitDocument.put("elo", entry.getValue().getElo());
			kitDocument.put("won", entry.getValue().getWon());
			kitDocument.put("lost", entry.getValue().getLost());
            kitDocument.put("kills", entry.getValue().getKills());
			kitDocument.put("winstreak", entry.getValue().getWinstreak());
			kitStatisticsDocument.put(entry.getKey().getName(), kitDocument);
		}

		document.put("kitStatistics", kitStatisticsDocument);

		Document kitsDocument = new Document();

		for (Map.Entry<Kit, ProfileKitData> entry : kitData.entrySet()) {
			JsonArray kitsArray = new JsonArray();

			for (int i = 0; i < 4; i++) {
				KitLoadout loadout = entry.getValue().getLoadout(i);

				if (loadout != null) {
					JsonObject kitObject = new JsonObject();
					kitObject.addProperty("index", i);
					kitObject.addProperty("name", loadout.getCustomName());
					kitObject.addProperty("armor", InventoryUtil.serializeInventory(loadout.getArmor()));
					kitObject.addProperty("contents", InventoryUtil.serializeInventory(loadout.getContents()));
					kitsArray.add(kitObject);
				}
			}

			kitsDocument.put(entry.getKey().getName(), kitsArray.toString());
		}

		document.put("loadouts", kitsDocument);

		collection.replaceOne(Filters.eq("uuid", uuid.toString()), document, new ReplaceOptions().upsert(true));
	}

	public static void init() {
		collection = Praxi.get().getMongoDatabase().getCollection("profiles");

		// Players might have joined before the plugin finished loading
		for (Player player : Bukkit.getOnlinePlayers()) {
			Profile profile = new Profile(player.getUniqueId());

			try {
				profile.load();
			} catch (Exception e) {
				player.kickPlayer(CC.RED + "The server is loading...");
				continue;
			}

			profiles.put(player.getUniqueId(), profile);
		}

		// Expire duel requests
		new BukkitRunnable() {
			@Override
			public void run() {
				for (Profile profile : Profile.getProfiles().values()) {
					Iterator<DuelRequest> iterator = profile.duelRequests.iterator();

					while (iterator.hasNext()) {
						DuelRequest duelRequest = iterator.next();

						if (duelRequest.isExpired()) {
							duelRequest.expire();
							iterator.remove();
						}
					}
				}
			}
		}.runTaskTimerAsynchronously(Praxi.get(), 60L, 60L);

		// Save every 5 minutes to prevent data loss
		new BukkitRunnable() {
			@Override
			public void run() {
				for (Profile profile : Profile.getProfiles().values()) {
					profile.save();
				}
			}
		}.runTaskTimerAsynchronously(Praxi.get(), 6000L, 6000L);
	}

	public static Profile getByUuid(UUID uuid) {
		Profile profile = profiles.get(uuid);

		if (profile == null) {
			profile = new Profile(uuid);
		}

		return profile;
	}

    public ProfileDivision getDivision() {
        if (Praxi.get().getDivisionsManager().isXPBased()) {
            return Praxi.get().getDivisionsManager().getDivisionByXP(this.getExperience());
        } else {
            return Praxi.get().getDivisionsManager().getDivisionByELO(this.getGlobalElo());
        }
    }

    public void addPermission(String permission) {
        permissions.add(permission);
    }

    public boolean hasPermission(String permission) {
        return permissions.contains(permission);
    }

}