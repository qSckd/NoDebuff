package me.funky.praxi;

import com.bizarrealex.aether.Aether;
import com.bizarrealex.aether.AetherOptions;
import co.aikar.commands.BukkitCommandCompletionContext;
import co.aikar.commands.CommandCompletions;
import co.aikar.commands.PaperCommandManager;
import me.funky.praxi.adapter.spigot.SpigotManager;
import me.funky.praxi.adapter.lunar.*;
import me.funky.praxi.adapter.holograms.HologramAdapter;
import me.funky.praxi.divisions.command.DivisionsCommand;
import me.funky.praxi.adapter.papi.PlaceholderAdapter;
import me.funky.praxi.adapter.board.ScoreboardAdapter;
import me.funky.praxi.arena.Arena;
import me.funky.praxi.arena.ArenaListener;
import me.funky.praxi.arena.ArenaType;
import me.funky.praxi.arena.ArenaTypeAdapter;
import me.funky.praxi.arena.ArenaTypeTypeAdapter;
import me.funky.praxi.arena.command.ArenaCommand;
import me.funky.praxi.queue.command.RankedCommand;
import me.funky.praxi.queue.command.QueueCommand;
import me.funky.praxi.commands.admin.general.SetSpawnCommand;
import me.funky.praxi.commands.admin.general.SetHoloCommand;
import me.funky.praxi.commands.admin.general.RenameCommand;
import me.funky.praxi.commands.admin.general.SudoCommand;
import me.funky.praxi.commands.admin.general.FollowCommand;
import me.funky.praxi.tournament.command.TournamentCommand;
import me.funky.praxi.commands.user.gamer.SuicideCommand;
import me.funky.praxi.commands.user.gamer.TrollCommand;
import me.funky.praxi.profile.coinshop.command.CoinsCommand;
import me.funky.praxi.essentials.Essentials;
import me.funky.praxi.event.Event;
import me.funky.praxi.event.EventTypeAdapter;
import me.funky.praxi.commands.event.admin.EventAdminCommand;
import me.funky.praxi.commands.event.admin.EventHelpCommand;
import me.funky.praxi.commands.event.admin.EventSetLobbyCommand;
import me.funky.praxi.event.game.EventGameListener;
import me.funky.praxi.commands.event.user.EventCancelCommand;
import me.funky.praxi.commands.event.user.EventClearCooldownCommand;
import me.funky.praxi.commands.event.user.EventForceStartCommand;
import me.funky.praxi.event.command.HostCommand;
import me.funky.praxi.event.command.EventTokensCommand;
import me.funky.praxi.commands.event.user.EventInfoCommand;
import me.funky.praxi.commands.event.user.EventJoinCommand;
import me.funky.praxi.commands.event.user.EventLeaveCommand;
import me.funky.praxi.commands.event.admin.EventsCommand;
import me.funky.praxi.event.game.map.EventGameMap;
import me.funky.praxi.event.game.map.EventGameMapTypeAdapter;
import me.funky.praxi.commands.event.map.EventMapCreateCommand;
import me.funky.praxi.commands.event.map.EventMapDeleteCommand;
import me.funky.praxi.commands.event.map.EventMapSetSpawnCommand;
import me.funky.praxi.commands.event.map.EventMapStatusCommand;
import me.funky.praxi.commands.event.map.EventMapsCommand;
import me.funky.praxi.commands.event.admin.EventAddMapCommand;
import me.funky.praxi.commands.event.admin.EventRemoveMapCommand;
import me.funky.praxi.commands.event.vote.EventMapVoteCommand;
import me.funky.praxi.commands.admin.match.MatchTestCommand;
import me.funky.praxi.commands.user.match.ViewInventoryCommand;
import me.funky.praxi.commands.user.match.LeaveCommand;
import me.funky.praxi.commands.user.duels.DuelCommand;
import me.funky.praxi.commands.user.duels.RematchCommand;
import me.funky.praxi.kit.Kit;
import me.funky.praxi.kit.KitTypeAdapter;
import me.funky.praxi.kit.command.KitCommand;
import me.funky.praxi.kit.KitEditorListener;
import me.funky.praxi.match.Match;
import me.funky.praxi.commands.user.match.SpectateCommand;
import me.funky.praxi.commands.user.match.StopSpectatingCommand;
import me.funky.praxi.match.MatchListener;
import me.funky.praxi.party.Party;
import me.funky.praxi.commands.user.party.PartyChatCommand;
import me.funky.praxi.commands.user.party.PartyCloseCommand;
import me.funky.praxi.commands.user.party.PartyCreateCommand;
import me.funky.praxi.commands.user.party.PartyDisbandCommand;
import me.funky.praxi.commands.user.party.PartyHelpCommand;
import me.funky.praxi.commands.user.party.PartyInfoCommand;
import me.funky.praxi.commands.user.party.PartyInviteCommand;
import me.funky.praxi.commands.user.party.PartyJoinCommand;
import me.funky.praxi.commands.user.party.PartyKickCommand;
import me.funky.praxi.commands.user.party.PartyLeaderCommand;
import me.funky.praxi.commands.user.party.PartyLeaveCommand;
import me.funky.praxi.commands.user.party.PartyOpenCommand;
import me.funky.praxi.commands.user.party.PartySettingsCommand;
import me.funky.praxi.party.PartyListener;
import me.funky.praxi.profile.Profile;
import me.funky.praxi.profile.managers.DivisionsManager;
import me.funky.praxi.commands.donater.FlyCommand;
import me.funky.praxi.profile.ProfileListener;
import me.funky.praxi.profile.hotbar.Hotbar;
import me.funky.praxi.profile.option.trail.listener.TrailListener;
import me.funky.praxi.commands.user.settings.ToggleDuelRequestsCommand;
import me.funky.praxi.commands.user.settings.ToggleScoreboardCommand;
import me.funky.praxi.commands.user.settings.ToggleSpectatorsCommand;
import me.funky.praxi.commands.user.settings.ProfileSettingsCommand;
import me.funky.praxi.commands.user.settings.SettingsCommand;
import me.funky.praxi.commands.user.general.leaderboards.LeaderboardsCommand;
import me.funky.praxi.commands.user.general.PracticeCommand;
import me.funky.praxi.commands.user.general.MsgCommand;
import me.funky.praxi.commands.user.general.CoinShopCommand;
import me.funky.praxi.commands.user.general.CosmeticsCommand;
import me.funky.praxi.commands.user.general.StatsCommand;
import me.funky.praxi.queue.QueueListener;
import me.funky.praxi.queue.QueueThread;
import me.funky.praxi.leaderboard.Leaderboard;
//import me.funky.praxi.util.ConfigManager;
import me.funky.praxi.util.InventoryUtil;
import me.funky.praxi.util.command.Honcho;
import me.funky.praxi.util.config.BasicConfigurationFile;
import me.funky.praxi.util.hologram.HologramHandler;
import me.funky.praxi.util.menu.MenuListener;
import me.funky.praxi.util.CC;
import me.funky.praxi.util.Cache;
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

public class Praxi extends JavaPlugin {

	private static Praxi praxi;

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
		praxi = this;
		configsLoad();
		loadMongo();
		getServer().getPluginManager().registerEvents(new MenuListener(), this);
		honcho = new Honcho(this);
		this.essentials = new Essentials(this);

        cache = new Cache();
		hotbar = new Hotbar();
		Praxi.get().getHotbar().init();

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
        for (World world : praxi.getServer().getWorlds()) {
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

	public static Praxi get() {
		return praxi;
	}

}