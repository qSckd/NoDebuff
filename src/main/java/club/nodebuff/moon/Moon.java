package club.nodebuff.moon;

import com.bizarrealex.aether.Aether;
import com.bizarrealex.aether.AetherOptions;
import co.aikar.commands.BukkitCommandCompletionContext;
import co.aikar.commands.CommandCompletions;
import co.aikar.commands.PaperCommandManager;
import club.nodebuff.moon.adapter.spigot.SpigotManager;
import club.nodebuff.moon.adapter.lunar.*;
import club.nodebuff.moon.adapter.holograms.HologramAdapter;
import club.nodebuff.moon.divisions.command.DivisionsCommand;
import club.nodebuff.moon.adapter.papi.PlaceholderAdapter;
import club.nodebuff.moon.adapter.board.ScoreboardAdapter;
import club.nodebuff.moon.arena.Arena;
import club.nodebuff.moon.arena.ArenaListener;
import club.nodebuff.moon.arena.ArenaType;
import club.nodebuff.moon.arena.ArenaTypeAdapter;
import club.nodebuff.moon.arena.ArenaTypeTypeAdapter;
import club.nodebuff.moon.arena.command.ArenaCommand;
import club.nodebuff.moon.queue.command.RankedCommand;
import club.nodebuff.moon.queue.command.QueueCommand;
import club.nodebuff.moon.commands.admin.general.SetSpawnCommand;
import club.nodebuff.moon.commands.admin.general.SetHoloCommand;
import club.nodebuff.moon.commands.admin.general.RenameCommand;
import club.nodebuff.moon.commands.admin.general.SudoCommand;
import club.nodebuff.moon.commands.admin.general.FollowCommand;
import club.nodebuff.moon.tournament.command.TournamentCommand;
import club.nodebuff.moon.commands.user.gamer.SuicideCommand;
import club.nodebuff.moon.commands.user.gamer.TrollCommand;
import club.nodebuff.moon.profile.coinshop.command.CoinsCommand;
import club.nodebuff.moon.essentials.Essentials;
import club.nodebuff.moon.event.Event;
import club.nodebuff.moon.event.EventTypeAdapter;
import club.nodebuff.moon.commands.event.admin.EventAdminCommand;
import club.nodebuff.moon.commands.event.admin.EventHelpCommand;
import club.nodebuff.moon.commands.event.admin.EventSetLobbyCommand;
import club.nodebuff.moon.event.game.EventGameListener;
import club.nodebuff.moon.commands.event.user.EventCancelCommand;
import club.nodebuff.moon.commands.event.user.EventClearCooldownCommand;
import club.nodebuff.moon.commands.event.user.EventForceStartCommand;
import club.nodebuff.moon.event.command.HostCommand;
import club.nodebuff.moon.event.command.EventTokensCommand;
import club.nodebuff.moon.commands.event.user.EventInfoCommand;
import club.nodebuff.moon.commands.event.user.EventJoinCommand;
import club.nodebuff.moon.commands.event.user.EventLeaveCommand;
import club.nodebuff.moon.commands.event.admin.EventsCommand;
import club.nodebuff.moon.event.game.map.EventGameMap;
import club.nodebuff.moon.event.game.map.EventGameMapTypeAdapter;
import club.nodebuff.moon.commands.event.map.EventMapCreateCommand;
import club.nodebuff.moon.commands.event.map.EventMapDeleteCommand;
import club.nodebuff.moon.commands.event.map.EventMapSetSpawnCommand;
import club.nodebuff.moon.commands.event.map.EventMapStatusCommand;
import club.nodebuff.moon.commands.event.map.EventMapsCommand;
import club.nodebuff.moon.commands.event.admin.EventAddMapCommand;
import club.nodebuff.moon.commands.event.admin.EventRemoveMapCommand;
import club.nodebuff.moon.commands.event.vote.EventMapVoteCommand;
import club.nodebuff.moon.commands.admin.match.MatchTestCommand;
import club.nodebuff.moon.commands.user.match.ViewInventoryCommand;
import club.nodebuff.moon.commands.user.match.LeaveCommand;
import club.nodebuff.moon.commands.user.duels.DuelCommand;
import club.nodebuff.moon.commands.user.duels.RematchCommand;
import club.nodebuff.moon.kit.Kit;
import club.nodebuff.moon.kit.KitTypeAdapter;
import club.nodebuff.moon.kit.command.KitCommand;
import club.nodebuff.moon.kit.KitEditorListener;
import club.nodebuff.moon.match.Match;
import club.nodebuff.moon.commands.user.match.SpectateCommand;
import club.nodebuff.moon.commands.user.match.StopSpectatingCommand;
import club.nodebuff.moon.match.MatchListener;
import club.nodebuff.moon.party.Party;
import club.nodebuff.moon.commands.user.party.PartyChatCommand;
import club.nodebuff.moon.commands.user.party.PartyCloseCommand;
import club.nodebuff.moon.commands.user.party.PartyCreateCommand;
import club.nodebuff.moon.commands.user.party.PartyDisbandCommand;
import club.nodebuff.moon.commands.user.party.PartyHelpCommand;
import club.nodebuff.moon.commands.user.party.PartyInfoCommand;
import club.nodebuff.moon.commands.user.party.PartyInviteCommand;
import club.nodebuff.moon.commands.user.party.PartyJoinCommand;
import club.nodebuff.moon.commands.user.party.PartyKickCommand;
import club.nodebuff.moon.commands.user.party.PartyLeaderCommand;
import club.nodebuff.moon.commands.user.party.PartyLeaveCommand;
import club.nodebuff.moon.commands.user.party.PartyOpenCommand;
import club.nodebuff.moon.commands.user.party.PartySettingsCommand;
import club.nodebuff.moon.party.PartyListener;
import club.nodebuff.moon.profile.Profile;
import club.nodebuff.moon.profile.managers.DivisionsManager;
import club.nodebuff.moon.commands.donater.FlyCommand;
import club.nodebuff.moon.profile.ProfileListener;
import club.nodebuff.moon.profile.hotbar.Hotbar;
import club.nodebuff.moon.profile.option.trail.listener.TrailListener;
import club.nodebuff.moon.commands.user.settings.ToggleDuelRequestsCommand;
import club.nodebuff.moon.commands.user.settings.ToggleScoreboardCommand;
import club.nodebuff.moon.commands.user.settings.ToggleSpectatorsCommand;
import club.nodebuff.moon.commands.user.settings.ProfileSettingsCommand;
import club.nodebuff.moon.commands.user.settings.SettingsCommand;
import club.nodebuff.moon.commands.user.general.leaderboards.LeaderboardsCommand;
import club.nodebuff.moon.commands.user.general.PracticeCommand;
import club.nodebuff.moon.commands.user.general.MsgCommand;
import club.nodebuff.moon.commands.user.general.CoinShopCommand;
import club.nodebuff.moon.commands.user.general.CosmeticsCommand;
import club.nodebuff.moon.commands.user.general.StatsCommand;
import club.nodebuff.moon.queue.QueueListener;
import club.nodebuff.moon.queue.QueueThread;
import club.nodebuff.moon.leaderboard.Leaderboard;
//import club.nodebuff.moon.util.ConfigManager;
import club.nodebuff.moon.util.InventoryUtil;
import club.nodebuff.moon.util.command.Honcho;
import club.nodebuff.moon.util.config.BasicConfigurationFile;
import club.nodebuff.moon.util.hologram.HologramHandler;
import club.nodebuff.moon.util.menu.MenuListener;
import club.nodebuff.moon.util.CC;
import club.nodebuff.moon.util.Cache;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.Difficulty;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import lombok.Getter;

import java.util.Arrays;
import java.util.stream.Collectors;

public class Moon extends JavaPlugin {

	private static Moon Moon;

	@Getter private BasicConfigurationFile mainConfig, settingsConfig, arenasConfig, kitsConfig, eventsConfig, hotbarConfig, scoreboardConfig, menusConfig, divisionsConfig, hologramConfig, databaseConfig;
	@Getter private Cache cache;
	@Getter private Hotbar hotbar;
	@Getter private PaperCommandManager paperCommandManager;
	@Getter private MongoDatabase mongoDatabase;
	@Getter private MongoClient mongoClient;
	@Getter private Honcho honcho;
	@Getter private Essentials essentials;
    @Getter private DivisionsManager divisionsManager;
	@Getter private RichPresence richPresence;
    @Getter private HologramHandler hologramHandler;
    @Getter private HologramAdapter hologramAdapter;
	@Getter private EventNotification eventNotification;
	@Getter private boolean placeholder = false;
	@Getter private boolean replay = false;
	@Getter private boolean lunar = false;

	public void configsLoad() {
        mainConfig = new BasicConfigurationFile(this, "config");
        settingsConfig = new BasicConfigurationFile(this, "settings");
		arenasConfig = new BasicConfigurationFile(this, "cache/arenas");
		kitsConfig = new BasicConfigurationFile(this, "cache/kits");
		eventsConfig = new BasicConfigurationFile(this, "cache/events");
		hotbarConfig = new BasicConfigurationFile(this, "hotbar");
		scoreboardConfig = new BasicConfigurationFile(this, "scoreboard");
		menusConfig = new BasicConfigurationFile(this, "menus");
		divisionsConfig = new BasicConfigurationFile(this, "divisions");
        hologramConfig = new BasicConfigurationFile(this, "holograms");
        databaseConfig = new BasicConfigurationFile(this, "database");
    }

    private void lunarClient () {
       this.richPresence = new RichPresence();
       this.eventNotification = new EventNotification();
    }

	@Override
	public void onEnable() {
		long oldTime = System.currentTimeMillis();
		Moon = this;
		configsLoad();
		loadMongo();
		getServer().getPluginManager().registerEvents(new MenuListener(), this);
		honcho = new Honcho(this);
		this.essentials = new Essentials(this);

        cache = new Cache();
		hotbar = new Hotbar();
		Moon.get().getHotbar().init();

		Kit.init();
		Arena.init();
		Profile.init();
		Match.init();
		Party.init();
		Event.init();
		EventGameMap.init();
        Leaderboard.updateLeaderboards();
		Leaderboard.init();
		SpigotManager.init();
        this.divisionsManager = new DivisionsManager(this);
        this.divisionsManager.init();
		loadCommandManager();
        hologramHandler = new HologramHandler(this);
        hologramAdapter = new HologramAdapter();
        hologramAdapter.registerHolograms();

		// Scoreboard load
        new Aether(this, new ScoreboardAdapter(), new AetherOptions().hook(true));

		// Load Queue System 
		new QueueThread().start();

        // Load Arena/Kit/Event Things
		getHoncho().registerTypeAdapter(Arena.class, new ArenaTypeAdapter());
		getHoncho().registerTypeAdapter(ArenaType.class, new ArenaTypeTypeAdapter());
		getHoncho().registerTypeAdapter(Kit.class, new KitTypeAdapter());
		getHoncho().registerTypeAdapter(EventGameMap.class, new EventGameMapTypeAdapter());
		getHoncho().registerTypeAdapter(Event.class, new EventTypeAdapter());

		// Load Commands
		Arrays.asList(
				// Event Commands */
				new EventAdminCommand(),
				new EventHelpCommand(),
				new EventCancelCommand(),
				new EventClearCooldownCommand(),
				new EventForceStartCommand(),
				new EventInfoCommand(),
				new EventJoinCommand(),
				new EventLeaveCommand(),
				new EventSetLobbyCommand(),
				new EventMapCreateCommand(),
				new EventMapDeleteCommand(),
				new EventMapsCommand(),
				new EventMapSetSpawnCommand(),
				new EventMapStatusCommand(),
				new EventMapVoteCommand(),
				new EventAddMapCommand(),
				new EventRemoveMapCommand(),
				new EventsCommand(),
				// Party Commands */
				new PartyChatCommand(),
				new PartyCloseCommand(),
				new PartyCreateCommand(),
				new PartyDisbandCommand(),
				new PartyHelpCommand(),
				new PartyInfoCommand(),
				new PartyInviteCommand(),
				new PartyJoinCommand(),
				new PartyKickCommand(),
                new PartyLeaderCommand(),
				new PartyLeaveCommand(),
				new PartyOpenCommand()
		).forEach(command -> getHoncho().registerCommand(command));

        // Load Listeners
		Arrays.asList(
				new KitEditorListener(),
				new PartyListener(),
				new ProfileListener(),
				new PartyListener(),
				new MatchListener(),
				new QueueListener(),
				new ArenaListener(),
				new EventGameListener(),
                new TrailListener()
		).forEach(listener -> getServer().getPluginManager().registerEvents(listener, this));

		Arrays.asList(
				Material.WORKBENCH,
				Material.STICK,
				Material.WOOD_PLATE,
				Material.WOOD_BUTTON,
				Material.SNOW_BLOCK
		).forEach(InventoryUtil::removeCrafting);

		// Set the difficulty for each world to HARD
		// Clear the droppedItems for each world
		getServer().getWorlds().forEach(world -> {
			world.setDifficulty(Difficulty.HARD);
			getEssentials().clearEntities(world);
		});

		for (World world : get().getServer().getWorlds()) {
                    world.setGameRuleValue("doDaylightCycle", "false");
                    world.setGameRuleValue("doWeatherCycle", "false");
                    world.setGameRuleValue("doMobSpawning", "false");
                    world.setGameRuleValue("doImmediateRespawn", "true");
                    world.setGameRuleValue("showDeathMessages", "false");
                }

		Plugin placeholderAPI = getServer().getPluginManager().getPlugin("PlaceholderAPI");
                if (placeholderAPI != null && placeholderAPI.isEnabled()) {
                   new PlaceholderAdapter().register();
                   placeholder = true;
                }

		Plugin advancedReplays = getServer().getPluginManager().getPlugin("Replays");
                if (advancedReplays != null && advancedReplays.isEnabled()) {
                   replay = true;
                }

                Plugin apollo = getServer().getPluginManager().getPlugin("Apollo-Bukkit");
                if (apollo != null && apollo.isEnabled()) {
                   lunarClient();
                   lunar = true;
                }

        sendMessage(CC.translate("&7&m-----------------------------------------"));
        sendMessage(CC.translate("&b&l███╗   ███╗ █████╗  █████╗ ███╗  ██╗"));
        sendMessage(CC.translate("&b&l████╗ ████║██╔══██╗██╔══██╗████╗ ██║"));
        sendMessage(CC.translate("&b&l██╔████╔██║██║  ██║██║  ██║██╔██╗██║"));
        sendMessage(CC.translate("&b&l██║╚██╔╝██║██║  ██║██║  ██║██║╚████║"));
        sendMessage(CC.translate("&b&l██║ ╚═╝ ██║╚█████╔╝╚█████╔╝██║ ╚███║"));
        sendMessage(CC.translate("&b&l╚═╝     ╚═╝ ╚════╝  ╚════╝ ╚═╝  ╚══╝"));
        sendMessage(CC.translate(" "));
        sendMessage(CC.translate("&7> &fVersion: &b" + get().getDescription().getVersion()));
        sendMessage(CC.translate("&7> &fAuthors: &b" + get().getDescription().getAuthors().toString().replace("[", "").replace("]", "")));
        sendMessage(CC.translate("&7> &fSpigot: &b" + SpigotManager.getServerSpigot()));
        sendMessage(CC.translate(" "));
        sendMessage(CC.translate("&7> &fKits: &b" + Kit.getKits().size()));
        sendMessage(CC.translate("&7> &fArenas: &b" + Arena.getArenas().size()));
        sendMessage(CC.translate("&7> &fPlaceholderAPI: " + (placeholder ? "&aEnabled" : "&cDisabled")));
        sendMessage(CC.translate("&7> &fReplays: " + (replay ? "&aEnabled" : "&cDisabled")));
        sendMessage(CC.translate("&7> &fLunarAPI: " + (lunar ? "&aEnabled" : "&cDisabled")));
        sendMessage(CC.translate(" "));
        sendMessage(CC.translate("&7> &fPlugin sucessfully loaded in: &b" + (System.currentTimeMillis() - oldTime) + "ms"));
        sendMessage(CC.translate(" "));
        sendMessage(CC.translate("&7&m-----------------------------------------"));
        System.gc();
	}

	private void loadCommandManager() {
        paperCommandManager = new PaperCommandManager(get());
        this.paperCommandManager.enableBrigadier();
        loadCommandCompletions();
        registerCommands();
    }

    private void registerCommands() {

        Arrays.asList(
		new KitCommand(),
        new ArenaCommand(),
	    new PracticeCommand(),
		new PartySettingsCommand(),
		new RenameCommand(),
		new MsgCommand(),
		new CoinShopCommand(),
		new CosmeticsCommand(),
		new StatsCommand(),
		new DivisionsCommand(),
		new HostCommand(),
        new EventTokensCommand(),
		new TrollCommand(),
        new DuelCommand(),
		new RematchCommand(),
        new SuicideCommand(),
        new FlyCommand(),
        new SetSpawnCommand(),
        new SetHoloCommand(),
		new SpectateCommand(),
		new StopSpectatingCommand(),
        new ViewInventoryCommand(),
        new MatchTestCommand(),
        new LeaveCommand(),
        new CoinsCommand(),
        new SudoCommand(),
		new ProfileSettingsCommand(),
        new SettingsCommand(),
		new FollowCommand(),
		new LeaderboardsCommand(),
        new QueueCommand(),
		new RankedCommand(),
		new TournamentCommand()
        ).forEach(command -> paperCommandManager.registerCommand(command));
    }

	private void loadCommandCompletions() {
        CommandCompletions<BukkitCommandCompletionContext> commandCompletions = getPaperCommandManager().getCommandCompletions();
        commandCompletions.registerCompletion("players", c -> Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList()));
        commandCompletions.registerCompletion("arenas", c -> Arena.getArenas().stream().map(Arena::getName).collect(Collectors.toList()));
        commandCompletions.registerCompletion("kits", c -> Kit.getKits().stream().map(Kit::getName).collect(Collectors.toList()));
    }

    public void clearStuffs() {
        for (World world : Moon.getServer().getWorlds()) {
            for (Entity entity : world.getEntities()) {
                if (!(entity.getType() == EntityType.PLAYER)) {
                    continue;
                }
                if (entity.getType().equals(EntityType.DROPPED_ITEM)) {
                    entity.remove();
                }
            }
        }
    }

	@Override
	public void onDisable() {
        clearStuffs();
		Match.cleanup();
	}

	private void loadMongo() {
        if (databaseConfig.getBoolean("MONGO.URI-MODE")) {
            MongoClientURI uri = new MongoClientURI(databaseConfig.getString("MONGO.URI.CONNECTION_STRING"));
            mongoClient = new MongoClient(uri);
            mongoDatabase = mongoClient.getDatabase(uri.getDatabase());
        } else {
            if (databaseConfig.getBoolean("MONGO.NORMAL.AUTHENTICATION.ENABLED")) {
                mongoClient = new MongoClient(
                        new ServerAddress(
                                databaseConfig.getString("MONGO.NORMAL.HOST"),
                                databaseConfig.getInteger("MONGO.NORMAL.PORT")
                        ),
                        MongoCredential.createCredential(
                                databaseConfig.getString("MONGO.NORMAL.AUTHENTICATION.USERNAME"),
                                databaseConfig.getString("MONGO.NORMAL.AUTHENTICATION.ADMIN"),
                                databaseConfig.getString("MONGO.NORMAL.AUTHENTICATION.PASSWORD").toCharArray()
                        ),
                        MongoClientOptions.builder().build()
                );
             } else {
                 mongoClient = new MongoClient(databaseConfig.getString("MONGO.NORMAL.HOST"), databaseConfig.getInteger("MONGO.NORMAL.PORT"));
             }
             mongoDatabase = mongoClient.getDatabase(databaseConfig.getString("MONGO.NORMAL.DATABASE"));
         }
    }

    public static void sendMessage(String message) {
        Bukkit.getServer().getConsoleSender().sendMessage(message);
    }

    public void broadcastMessage(String message) {
        for (Player player : getServer().getOnlinePlayers()) {
            player.sendMessage(CC.translate(message));
        }
    }

	public static Moon get() {
		return Moon;
	}

}