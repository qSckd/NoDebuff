package club.nodebuff.moon.util;

import club.nodebuff.moon.util.ItemBuilder;
import club.nodebuff.moon.util.menu.Button;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class Constants {

    @Getter private static Random random = new Random();

    public static final Button BLACK_PANE = new Button() {
        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(Material.STAINED_GLASS_PANE)
                    .durability(8) //7
                    .name(" ")
                    .build();
        }
    };

}
