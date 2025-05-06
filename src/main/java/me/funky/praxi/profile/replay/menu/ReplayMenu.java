package me.funky.praxi.profile.replay.menu;

import lombok.AllArgsConstructor;
import me.jumper251.replay.api.ReplayAPI;
import me.jumper251.replay.replaysystem.Replay;
import me.funky.praxi.Praxi;
import me.funky.praxi.util.CC;
import me.funky.praxi.util.ItemBuilder;
import me.funky.praxi.util.TextSplitter;
import me.funky.praxi.util.menu.Button;
import me.funky.praxi.util.menu.Menu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReplayMenu extends Menu {
    private final ReplayAPI replayAPI = ReplayAPI.getInstance();

    @Override
    public String getTitle(Player player) {
        return Praxi.get().getMenusConfig().getString("REPLAY-MENU.TITLE");
    }

    @Override
    public int getSize() {
        return Praxi.get().getMenusConfig().getInteger("REPLAY-MENU.SIZE");
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

		buttons.put(13, new ReplayButton());

        return buttons;
    }

    @AllArgsConstructor
    private class ReplayButton extends Button {

        @Override
        public ItemStack getButtonItem(Player player) {
            List<String> lore = new ArrayList<>();
            //String replayName = player.getUniqueId();
            //Replay replay = replayAPI.getReplay(replayName);
            lore.add(CC.translate("&fRival: &bnull"));
            lore.add(CC.translate("&fKit: &bnull"));
            lore.add(CC.translate(""));
            lore.add(CC.translate("&aClick to replay."));

            return new ItemBuilder(Material.PAPER)
                    .name("&cMatch Replay")
                    .lore(lore)
                    .clearFlags()
                    .build();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            player.chat("/replay play " + player.getUniqueId().toString());
        }
    }
}