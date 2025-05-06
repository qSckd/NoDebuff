package club.nodebuff.moon.kit;

import lombok.Getter;
import lombok.Setter;
import com.mongodb.client.model.Sorts;
import club.nodebuff.moon.profile.Profile;
import club.nodebuff.moon.kit.meta.KitEditRules;
import club.nodebuff.moon.kit.meta.KitGameRules;
import club.nodebuff.moon.queue.Queue;
import club.nodebuff.moon.util.InventoryUtil;
import club.nodebuff.moon.Moon;
import club.nodebuff.moon.util.ItemBuilder;
import club.nodebuff.moon.util.ItemUtil;
import club.nodebuff.moon.util.CountUtil;
import club.nodebuff.moon.util.config.BasicConfigurationFile;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.UUID;


public class Kit {

	@Getter private static final List<Kit> kits = new ArrayList<>();

	@Getter private final String name;
	@Getter @Setter private boolean enabled;
    @Getter @Setter private int slot;
	@Getter @Setter private String knockbackProfile;
	@Setter private ItemStack displayIcon;
	@Getter private final List<KitLeaderboards> rankedEloLeaderboards = new ArrayList<>();
	@Getter private final KitLoadout kitLoadout = new KitLoadout();
	@Getter private final KitEditRules editRules = new KitEditRules();
	@Getter private final KitGameRules gameRules = new KitGameRules();
	private List<String> description;

	public Kit(String name) {
		this.name = name;
		this.displayIcon = new ItemStack(Material.DIAMOND_SWORD);
		this.description = Collections.singletonList("&7" + name + "'s description");
	}

	public ItemStack getDisplayIcon() {
		return this.displayIcon.clone();
	}

	public void save() {
		String path = "kits." + name;

		BasicConfigurationFile configFile = Moon.get().getKitsConfig();
		configFile.getConfiguration().set(path + ".enabled", enabled);
        configFile.getConfiguration().set(path + ".slot", slot);
        configFile.getConfiguration().set(path + ".editable", gameRules.isEditable());
		configFile.getConfiguration().set(path + ".description", description);
		configFile.getConfiguration().set(path + ".icon.material", displayIcon.getType().name());
		configFile.getConfiguration().set(path + ".icon.durability", displayIcon.getDurability());
		configFile.getConfiguration().set(path + ".loadout.armor", InventoryUtil.itemStackArrayToBase64(kitLoadout.getArmor()));
		configFile.getConfiguration().set(path + ".loadout.contents", InventoryUtil.itemStackArrayToBase64(kitLoadout.getContents()));
		configFile.getConfiguration().set(path + ".game-rules.allow-build", gameRules.isBuild());
		configFile.getConfiguration().set(path + ".game-rules.ranked", gameRules.isRanked());
		configFile.getConfiguration().set(path + ".game-rules.spleef", gameRules.isSpleef());
		configFile.getConfiguration().set(path + ".game-rules.parkour", gameRules.isParkour());
		configFile.getConfiguration().set(path + ".game-rules.sumo", gameRules.isSumo());
		configFile.getConfiguration().set(path + ".game-rules.boxing", gameRules.isBoxing());
		configFile.getConfiguration().set(path + ".game-rules.bedfight", gameRules.isBedfight());
        configFile.getConfiguration().set(path + ".game-rules.bot", gameRules.isBot());
		configFile.getConfiguration().set(path + ".game-rules.eggwars", gameRules.isEggwars());
		configFile.getConfiguration().set(path + ".game-rules.bridge", gameRules.isBridge());
        configFile.getConfiguration().set(path + ".game-rules.battlerush", gameRules.isBattleRush());
        configFile.getConfiguration().set(path + ".game-rules.stickfight", gameRules.isStickFight());
        configFile.getConfiguration().set(path + ".game-rules.fireballroyale", gameRules.isFireballRoyale());
        configFile.getConfiguration().set(path + ".game-rules.hcf", gameRules.isHcf());
		configFile.getConfiguration().set(path + ".game-rules.onehit", gameRules.isOnehit());
		configFile.getConfiguration().set(path + ".game-rules.health-regeneration", gameRules.isHealthRegeneration());
		configFile.getConfiguration().set(path + ".game-rules.show-health", gameRules.isShowHealth());
        configFile.getConfiguration().set(path + ".game-rules.no-damage", gameRules.isNoDamage());
		configFile.getConfiguration().set(path + ".game-rules.hit-delay", gameRules.getHitDelay());
		configFile.getConfiguration().set(path + ".edit-rules.allow-potion-fill", editRules.isAllowPotionFill());

		if (knockbackProfile != null) {
			configFile.getConfiguration().set(path + ".knockback-profile", knockbackProfile);
		}

		try {
			configFile.getConfiguration().save(configFile.getFile());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void updateKitLeaderboards() {
		if (!rankedEloLeaderboards.isEmpty()) rankedEloLeaderboards.clear();
		for (Document document : Profile.getCollection().find().sort(Sorts.descending("kitStatistics." + getName() + ".elo")).limit(10).into(new ArrayList<>())) {
			Document kitStatistics = (Document) document.get("kitStatistics");
			UUID uuid = UUID.fromString(document.getString("uuid"));
			if (kitStatistics.containsKey(getName())) {
				Document kitDocument = (Document) kitStatistics.get(getName());
				KitLeaderboards kitLeaderboards = new KitLeaderboards();
				kitLeaderboards.setName(Bukkit.getOfflinePlayer(uuid).getName());
				kitLeaderboards.setElo((Integer) kitDocument.get("elo"));
				kitLeaderboards.setWins((Integer) kitDocument.get("won"));
                kitLeaderboards.setWinStreak((Integer) kitDocument.get("winstreak"));
				rankedEloLeaderboards.add(kitLeaderboards);
			}
		}
	}

	public static void init() {
		FileConfiguration config = Moon.get().getKitsConfig().getConfiguration();

		for (String key : config.getConfigurationSection("kits").getKeys(false)) {
			String path = "kits." + key;

			Kit kit = new Kit(key);
			kit.setEnabled(config.getBoolean(path + ".enabled"));
            kit.setSlot(config.getInt(path + ".slot"));

			if (config.contains(path + ".knockback-profile")) {
				kit.setKnockbackProfile(config.getString(path + ".knockback-profile"));
			}

			kit.setDisplayIcon(new ItemBuilder(Material.valueOf(config.getString(path + ".icon.material")))
					.durability(config.getInt(path + ".icon.durability"))
					.build());
			kit.setDescription(config.getStringList(path + ".description"));

            if (config.contains(path + ".loadout.armor")) {
				try {
					kit.getKitLoadout().setArmor(InventoryUtil.itemStackArrayFromBase64(config.getString(path + ".loadout.armor")));
				} catch (IOException ignore) {
					System.out.print("We got a small issue here.");
				}
			}

            if (config.contains(path + ".loadout.contents")) {
				try {
					kit.getKitLoadout().setContents(InventoryUtil.itemStackArrayFromBase64(config.getString(path + ".loadout.contents")));
				} catch (IOException ignore) {
					System.out.print("We got a small issue here.");
				}
			}

            kit.getGameRules().setEditable(config.getBoolean(path + ".editable"));
			kit.getGameRules().setBuild(config.getBoolean(path + ".game-rules.allow-build"));
			kit.getGameRules().setRanked(config.getBoolean(path + ".game-rules.ranked"));
			kit.getGameRules().setSpleef(config.getBoolean(path + ".game-rules.spleef"));
			kit.getGameRules().setParkour(config.getBoolean(path + ".game-rules.parkour"));
			kit.getGameRules().setSumo(config.getBoolean(path + ".game-rules.sumo"));
			kit.getGameRules().setBoxing(config.getBoolean(path + ".game-rules.boxing"));
			kit.getGameRules().setBedfight(config.getBoolean(path + ".game-rules.bedfight"));
            kit.getGameRules().setBot(config.getBoolean(path + ".game-rules.bot"));
			kit.getGameRules().setEggwars(config.getBoolean(path + ".game-rules.eggwars"));
			kit.getGameRules().setBridge(config.getBoolean(path + ".game-rules.bridge"));
            kit.getGameRules().setBattleRush(config.getBoolean(path + ".game-rules.battlerush"));
            kit.getGameRules().setStickFight(config.getBoolean(path + ".game-rules.stickfight"));
            kit.getGameRules().setFireballRoyale(config.getBoolean(path + ".game-rules.fireballroyale"));
            kit.getGameRules().setHcf(config.getBoolean(path + ".game-rules.hcf"));
			kit.getGameRules().setOnehit(config.getBoolean(path + ".game-rules.onehit"));
			kit.getGameRules().setHealthRegeneration(config.getBoolean(path + ".game-rules.health-regeneration"));
			kit.getGameRules().setShowHealth(config.getBoolean(path + ".game-rules.show-health"));
            kit.getGameRules().setNoDamage(config.getBoolean(path + ".game-rules.no-damage"));
			kit.getGameRules().setHitDelay(config.getInt(path + ".game-rules.hit-delay"));
			kit.getEditRules().setAllowPotionFill(config.getBoolean(".edit-rules.allow-potion-fill"));

			if (config.getConfigurationSection(path + ".edit-rules.items") != null) {
				for (String itemKey : config.getConfigurationSection(path + ".edit-rules.items")
				                            .getKeys(false)) {
					kit.getEditRules().getEditorItems().add(new ItemBuilder(Material.valueOf(
							config.getString(path + ".edit-rules.items." + itemKey + ".material")))
							.durability(config.getInt(path + ".edit-rules.items." + itemKey + ".durability"))
							.amount(config.getInt(path + ".edit-rules.items." + itemKey + ".amount"))
							.build());
				}
			}

			kits.add(kit);
		}

		kits.forEach(kit -> {
			if (kit.isEnabled()) {
				new Queue(kit, false);
				new Queue(kit, true);
			}
		});
	}
	
	public void delete(Kit kit) {
		String path = "kits." + name;

		BasicConfigurationFile configFile = Moon.get().getKitsConfig();
		configFile.getConfiguration().set(path, null);

		try {
			configFile.getConfiguration().save(configFile.getFile());
		} catch (IOException e) {
			e.printStackTrace();
		}
        kits.remove(kit);
	}

	public void setDescription(List<String> description) {
       this.description = description;
    }

	public List<String> getDescription() {
        return this.description;
    }

    public int countDebuffs(KitLoadout kit) {
        return CountUtil.countStacksMatching(kit.getContents(), CountUtil.DEBUFF_POTION_PREDICATE);
    }

    public int countFood(KitLoadout kit) {
        return CountUtil.countStacksMatching(kit.getContents(), CountUtil.EDIBLE_PREDICATE);
    }

    public int countPearls(KitLoadout kit) {
        return CountUtil.countStacksMatching(kit.getContents(), v -> v.getType() == Material.ENDER_PEARL);
    }

    public int countHeals(KitLoadout kit) {
        return CountUtil.countStacksMatching(kit.getContents(), CountUtil.INSTANT_HEAL_POTION_PREDICATE);
    }

	public static Kit getByName(String name) {
		for (Kit kit : kits) {
			if (kit.getName().equalsIgnoreCase(name)) {
				return kit;
			}
		}

		return null;
	}

}
