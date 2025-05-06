package me.funky.praxi.profile.option.killmessages;

import me.funky.praxi.Praxi;
import me.funky.praxi.profile.Profile;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Getter
public enum KillMessages {
    NONE("None", Material.RECORD_11, "", new KillMessageCallable(){

        @Override
        public String getFormatted(String player, String other, boolean otherPlayerExists) {
            if (!otherPlayerExists) {
                return ChatColor.RED + player + ChatColor.WHITE + " was killed.";
            }
            return ChatColor.RED + player + ChatColor.WHITE + " was killed by " + ChatColor.GREEN + other + ChatColor.WHITE + ".";
        }

        @Override
        public List<String> getMessages() {
            return Collections.singletonList("killed");
        }

        @Override
        public List<String> getDescription() {
            return new ArrayList<String>();
        }
    }),
    NERD("Nerd", Material.BOOK, "nerd", new KillMessageCallable(){

        @Override
        public String getFormatted(String killed, String killer, boolean otherPlayerExists) {
            String randomMessage = "&b" + this.getMessages().get(ThreadLocalRandom.current().nextInt(this.getMessages().size())) + ChatColor.WHITE;
            if (!otherPlayerExists) {
                return ChatColor.RED + killed + ChatColor.WHITE + " was " + randomMessage + ".";
            }
            return ChatColor.RED + killed + ChatColor.WHITE + " was " + randomMessage + " by " + ChatColor.GREEN + killer + ChatColor.WHITE + ".";
        }

        @Override
        public List<String> getMessages() {
            return Arrays.asList("ALT+F4'd", "deleted", "crashed", "ratted", "hacked", "over-heated");
        }

        @Override
        public List<String> getDescription() {
            return Arrays.asList(ChatColor.WHITE + "Nerdy computer terms", ChatColor.WHITE + "to trigger your opponents.");
        }
    }, 1000),
    GAMER("Gamer", Material.DIAMOND_SWORD, "gamer", new KillMessageCallable(){

        @Override
        public String getFormatted(String killed, String killer, boolean otherPlayerExists) {
            String randomMessage = "&b" + this.getMessages().get(ThreadLocalRandom.current().nextInt(this.getMessages().size())) + ChatColor.WHITE;
            if (!otherPlayerExists) {
                return ChatColor.RED + killed + ChatColor.WHITE + " was " + randomMessage + ".";
            }
            return ChatColor.RED + killed + ChatColor.WHITE + " was " + randomMessage + " by " + ChatColor.GREEN + killer + ChatColor.WHITE + ".";
        }

        @Override
        public List<String> getMessages() {
            return Arrays.asList("destroyed", "locked", "shoved into a locker", "clapped");
        }

        @Override
        public List<String> getDescription() {
            return Arrays.asList(ChatColor.WHITE + "Trigger your opponents", ChatColor.WHITE + "using slightly toxic phrases.");
        }
    }, 1500),
	RICKROLL("Rickroll", Material.REDSTONE, "rickroll", new KillMessageCallable(){

        @Override
        public String getFormatted(String killed, String killer, boolean otherPlayerExists) {
            String randomMessage = "&b" + this.getMessages().get(ThreadLocalRandom.current().nextInt(this.getMessages().size())) + ChatColor.WHITE;
            if (!otherPlayerExists) {
                return ChatColor.RED + killed + ChatColor.WHITE + " was " + randomMessage + ".";
            }
            return ChatColor.RED + killed + ChatColor.WHITE + " was " + randomMessage + " by " + ChatColor.GREEN + killer + ChatColor.WHITE + ".";
        }

        @Override
        public List<String> getMessages() {
            return Arrays.asList("rickrolled", "given up", "given down", "bombaclated");
        }

        @Override
        public List<String> getDescription() {
            return Arrays.asList(ChatColor.WHITE + "Rickroll your opponents", ChatColor.WHITE + "using his music.");
        }
    }, 1500),
    MEMES("Memes", Material.JUKEBOX, "memes", new KillMessageCallable(){

        @Override
        public String getFormatted(String killed, String killer, boolean otherPlayerExists) {
            String randomMessage = "&f" + this.getMessages().get(ThreadLocalRandom.current().nextInt(this.getMessages().size())) + ChatColor.WHITE; //TODO: make color editable
            if (!otherPlayerExists) {
                return ChatColor.RED + killed + ChatColor.WHITE + " was " + randomMessage + ".";
            }
            return ChatColor.RED + killed + ChatColor.WHITE + " was " + randomMessage + " by " + ChatColor.GREEN + killer + ChatColor.WHITE + ".";
        }

        @Override
        public List<String> getMessages() {
            return Arrays.asList("rickrolled", "memed", "420 noscoped", "trolled", "bamboozled");
        }

        @Override
        public List<String> getDescription() {
            return Arrays.asList(ChatColor.WHITE + "Trigger your opponents", ChatColor.WHITE + "using \"funny\" meme phrases.");
        }
    }, 2000);

    private final String name;
    private final Material icon;
    private final String permission;
    private final KillMessageCallable callable;
    private int price;

    KillMessages(String name, Material icon, String permission, KillMessageCallable callable) {
        this.name = name;
        this.icon = icon;
        this.permission = permission;
        this.callable = callable;
        this.price = 0;
    }

    KillMessages(String name, Material icon, String permission, KillMessageCallable callable, int p) {
        this.name = name;
        this.icon = icon;
        this.permission = permission;
        this.callable = callable;
        this.price = p;
    }

    public void setPrice(int p) {
        this.price = p;
    }

    public static KillMessages getByName(String input) {
        for (KillMessages type : KillMessages.values()) {
            if (!type.name().equalsIgnoreCase(input) && !type.getName().equalsIgnoreCase(input)) continue;
            return type;
        }
        return null;
    }

    public boolean hasPermission(Player player) {
        Profile profile = Profile.getByUuid(player.getUniqueId());
        return profile.hasPermission(this.permission) || this.permission.isEmpty();
    }
}
