package club.nodebuff.moon.arena;

import club.nodebuff.moon.Moon;
import club.nodebuff.moon.arena.selection.Selection;
import club.nodebuff.moon.match.MatchState;
import club.nodebuff.moon.match.Match;
import club.nodebuff.moon.util.CC;
import org.bukkit.Difficulty;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.inventory.ItemStack;

public class ArenaListener implements Listener {

    /**
     * Listens for {@link PlayerInteractEvent}s to handle the wand selection item.
     * <p>
     * If the player is holding the selection wand and right-clicks on a block, the position of the right-clicked block
     * is set as the second location in the selection cuboid. If the player left-clicks, the position of the left-clicked
     * block is set as the first location in the selection cuboid.
     * <p>
     * The event is then cancelled and the {@link Event#getUseItemInHand()} and {@link Event#getUseInteractedBlock()}
     * are set to {@link Event.Result#DENY} to prevent the player from placing or breaking blocks.
     * <p>
     * Finally, a message is sent to the player with the location of the block they clicked, and if the selection is a
     * full cuboid, it also sends the volume of the cuboid.
     */
    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        if (!(event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
            return;
        }

        ItemStack item = event.getItem();

        if (item != null && item.equals(Selection.SELECTION_WAND)) {
            Player player = event.getPlayer();
            Block clicked = event.getClickedBlock();
            int location = 0;

            Selection selection = Selection.createOrGetSelection(player);

            if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                selection.setPoint2(clicked.getLocation());
                location = 2;
            } else if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
                selection.setPoint1(clicked.getLocation());
                location = 1;
            }

            event.setCancelled(true);
            event.setUseItemInHand(Event.Result.DENY);
            event.setUseInteractedBlock(Event.Result.DENY);

            String message = CC.AQUA + (location == 1 ? "First" : "Second") +
                    " location " + CC.YELLOW + "(" + CC.GREEN +
                    clicked.getX() + CC.YELLOW + ", " + CC.GREEN +
                    clicked.getY() + CC.YELLOW + ", " + CC.GREEN +
                    clicked.getZ() + CC.YELLOW + ")" + CC.AQUA + " has been set!";

            if (selection.isFullObject()) {
                message += CC.RED + " (" + CC.YELLOW + selection.getCuboid().volume() + CC.AQUA + " blocks" +
                        CC.RED + ")";
            }

            player.sendMessage(message);
        }
    }

    /**
     * Listens for {@link BlockFromToEvent}s to handle block placement and removal during a match.
     * <p>
     * If the event occurs in a {@link Arena} of type {@link ArenaType#STANDALONE} or
     * {@link ArenaType#DUPLICATE} and is active, it will then loop through all the matches in the cache and if the
     * arena of the match is the same as the found arena, it will add the location of the block to the list of placed
     * blocks if the match is in the {@link MatchState#PLAYING_ROUND} state. If the match is not found, it will break
     * out of the loop.
     */
    @EventHandler
    public void onBlockFromTo(BlockFromToEvent event) {
        final int x = event.getBlock().getX();
        final int y = event.getBlock().getY();
        final int z = event.getBlock().getZ();

        Arena foundArena = null;

        for (Arena arena : Arena.getArenas()) {
            if (!(arena.getType() == ArenaType.STANDALONE || arena.getType() == ArenaType.DUPLICATE)) {
                continue;
            }

            if (!arena.isActive()) {
                continue;
            }

            if (x >= arena.getX1() && x <= arena.getX2() && y >= arena.getY1() && y <= arena.getY2() &&
                    z >= arena.getZ1() && z <= arena.getZ2()) {
                foundArena = arena;
                break;
            }
        }

        if (foundArena == null) {
            return;
        }

        for (Match match : Moon.get().getCache().getMatches()) {
            if (match.getArena().equals(foundArena)) {
                if (match.getState() == MatchState.PLAYING_ROUND) {
                    match.getPlacedBlocks().add(event.getToBlock().getLocation());
                }

                break;
            }
        }
    }

    /**
     * Handles {@link CreatureSpawnEvent}s to prevent creatures from spawning.
     * <p>
     * This method cancels the spawn event to ensure that no creatures are spawned
     * within the arena, maintaining the intended gameplay environment.
     */
    @EventHandler
    public void onCreatureSpawnEvent(CreatureSpawnEvent event) {
        event.setCancelled(true);
    }

    /**
     * Listens for {@link WorldLoadEvent}s to reset the entities in the world
     * and set the difficulty to {@link Difficulty#HARD}.
     * <p>
     * This is used to ensure that the world is reset to a clean state
     * when it is loaded.
     */
    @EventHandler
    public void onWorldLoad(WorldLoadEvent event) {
        event.getWorld().getEntities().clear();
        event.getWorld().setDifficulty(Difficulty.HARD);
    }

    /**
     * Handles {@link WeatherChangeEvent}s to prevent weather changes.
     * <p>
     * This method cancels the weather change event to ensure that the weather
     * remains consistent throughout the arena, maintaining the intended
     * gameplay environment.
     */
    @EventHandler
    public void onWeatherChange(WeatherChangeEvent event) {
        event.setCancelled(true);
    }

    /**
     * Handles {@link CreatureSpawnEvent}s to prevent creatures from spawning.
     * <p>
     * This method cancels the spawn event to ensure that no creatures are spawned
     * within the arena, maintaining the intended gameplay environment.
     */
    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        event.setCancelled(true);
    }

    /**
     * Handles {@link BlockIgniteEvent}s to prevent certain types of block ignitions.
     * <p>
     * This method cancels the block ignition event if the cause is
     * {@link BlockIgniteEvent.IgniteCause#LIGHTNING}, {@link BlockIgniteEvent.IgniteCause#FIREBALL},
     * or {@link BlockIgniteEvent.IgniteCause#EXPLOSION} to ensure that the arena
     * remains in a consistent state.
     */
    @EventHandler
    public void onBlockIgnite(BlockIgniteEvent event) {
        if (event.getCause() == BlockIgniteEvent.IgniteCause.LIGHTNING || event.getCause() == BlockIgniteEvent.IgniteCause.FIREBALL || event.getCause() == BlockIgniteEvent.IgniteCause.EXPLOSION) {
            event.setCancelled(true);
        }
    }

/**
 * Handles {@link BlockExplodeEvent}s to prevent block explosions.
 * <p>
 * This method cancels the block explosion event to ensure that
 * the arena remains intact and consistent, maintaining the intended
 * gameplay environment.
 */
    @EventHandler
    public void onBlockExplosion(BlockExplodeEvent event) {
        event.setCancelled(true);
    }

    /**
     * Handles {@link LeavesDecayEvent}s to prevent leaves from decaying.
     * <p>
     * This method cancels the leaves decay event to ensure that the arena
     * remains intact and consistent, maintaining the intended gameplay environment.
     */
    @EventHandler
    public void onLeavesDecay(LeavesDecayEvent event) {
        event.setCancelled(true);
    }

    /**
     * Handles {@link HangingBreakEvent}s to prevent hanging entities from breaking.
     * <p>
     * This method cancels the hanging break event to ensure that the arena
     * remains intact and consistent, maintaining the intended gameplay environment.
     */
    @EventHandler
    public void onHangingBreak(HangingBreakEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockBurn(BlockBurnEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockSpread(BlockSpreadEvent event) {
        event.setCancelled(true);
    }
}