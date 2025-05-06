package me.funky.praxi.match.menu;

import lombok.AllArgsConstructor;
import me.funky.praxi.Locale;
import me.funky.praxi.Praxi;
import me.funky.praxi.match.MatchSnapshot;
import me.funky.praxi.util.InventoryUtil;
import me.funky.praxi.util.ItemBuilder;
import me.funky.praxi.util.PlayerUtil;
import me.funky.praxi.util.PotionUtil;
import me.funky.praxi.util.TimeUtil;
import me.funky.praxi.util.menu.Button;
import me.funky.praxi.util.menu.Menu;
import me.funky.praxi.util.menu.button.DisplayButton;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.apache.commons.lang.StringEscapeUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@AllArgsConstructor
public class MatchDetailsMenu extends Menu {

    private MatchSnapshot snapshot;
    private MatchSnapshot opponent;

    @Override
    public String getTitle(Player player) {
        return Praxi.get().getMenusConfig().getString("MATCH.DETAILS-MENU.TITLE");
    }

    public boolean getFixedPositions() {
        return false;
    }

    public boolean resetCursor() {
        return false;
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        ItemStack[] fixedContents = InventoryUtil.fixInventoryOrder(snapshot.getContents());

        for (int i = 0; i < fixedContents.length; i++) {
            ItemStack itemStack = fixedContents[i];

            if (itemStack != null && itemStack.getType() != Material.AIR) {
                buttons.put(i, new DisplayButton(itemStack, true));
            }
        }


        int pos = 47;

        buttons.put(pos++, new HealthButton(snapshot.getHealth()));
        buttons.put(pos++, new HungerButton(snapshot.getHunger()));
        buttons.put(pos++, new EffectsButton(snapshot.getEffects()));

        if (snapshot.shouldDisplayRemainingPotions()) {
            buttons.put(pos++, new PotionsButton(snapshot.getUsername(), snapshot.getRemainingPotions()));
        }

        buttons.put(pos, new StatisticsButton(snapshot));

        if (this.snapshot.getSwitchTo() != null) {
            buttons.put(53, new SwitchInventoryButton(this.snapshot.getSwitchTo()));
            buttons.put(45, new SwitchInventoryButton(this.snapshot.getSwitchTo()));
        }

        return buttons;
    }

    @AllArgsConstructor
    private class HealthButton extends Button {

        private double health;

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(PlayerUtil.getPlayerHead(snapshot.getUuid()))
                    .name(Praxi.get().getMenusConfig().getString("MATCH.DETAILS-MENU.HEALTH-BUTTON.NAME").replace("{health}", String.valueOf(health)))
                    .amount((int) (health == 0 ? 1 : health))
                    .clearFlags()
                    .build();
        }

    }

    @AllArgsConstructor
    private class EffectsButton extends Button {

        private Collection<PotionEffect> effects;

        @Override
        public ItemStack getButtonItem(Player player) {
            ItemBuilder builder = new ItemBuilder(Material.BREWING_STAND_ITEM).name(Praxi.get().getMenusConfig().getString("MATCH.DETAILS-MENU.EFFECTS-BUTTON.NAME"));

            if (effects.isEmpty()) {
                builder.lore("&fNo potion effects");
            } else {
                List<String> lore = new ArrayList<>();

                effects.forEach(effect -> {
                    String name = PotionUtil.getName(effect.getType()) + " " + (effect.getAmplifier() + 1);
                    String duration = " (" + TimeUtil.millisToTimer((effect.getDuration() / 20) * 1000L) + ")";
					lore.add(Praxi.get().getMenusConfig().getString("MATCH.DETAILS-MENU.EFFECTS-BUTTON.LORE").replace("{duration}", String.valueOf(duration)).replace("{potion_name}", name));
                });

                builder.lore(lore, player);
            }

            return builder.build();
        }

    }

    @AllArgsConstructor
    private class PotionsButton extends Button {

        private String name;
        private int potions;

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(Material.POTION)
                    .durability(16421)
					.amount(potions == 0 ? 1 : potions)
                    .name(Praxi.get().getMenusConfig().getString("MATCH.DETAILS-MENU.POTIONS-BUTTON.NAME"))
                    .lore("&b" + name + " &fhad &b" + potions + " &fpotion" + (potions == 1 ? "" : "s") + " left.")
                    .clearFlags()
                    .build();
        }

    }

    @AllArgsConstructor
    private class StatisticsButton extends Button {

        private MatchSnapshot snapshot;

        @Override
        public ItemStack getButtonItem(Player player) {
            int totalHits = snapshot.getTotalHits();
            return new ItemBuilder(Material.PAPER)
                    .name(Praxi.get().getMenusConfig().getString("MATCH.DETAILS-MENU.STATS-BUTTON.NAME"))
                    .lore(Arrays.asList(
                            "&7• &fHits: &b" + snapshot.getTotalHits(),
                            "&7• &fLongest Combo: &b" + snapshot.getLongestCombo(),
                            "",
                            "&bPotions: ",
                            "&7• &fPotions Thrown: &b" + snapshot.getPotionsThrown(),
                            "&7• &fPotions Missed: &b" + snapshot.getPotionsMissed(),
                            "&7• &fPotion Accuracy: &b" + snapshot.getPotionAccuracy()
                    ), player)
                    .clearFlags()
                    .build();
        }

    }

    @AllArgsConstructor
    private class SwitchInventoryButton extends Button {

        private Player opponent;

        @Override
        public ItemStack getButtonItem(Player player) {
            MatchSnapshot snapshot = MatchSnapshot.getByUuid(opponent.getUniqueId());

            if (snapshot != null) {
                return new ItemBuilder(Material.LEVER)
                        .name(Praxi.get().getMenusConfig().getString("MATCH.DETAILS-MENU.SWITCH-INVENTORY-BUTTON.NAME"))
                        .lore(Praxi.get().getMenusConfig().getString("MATCH.DETAILS-MENU.SWITCH-INVENTORY-BUTTON.LORE").replace("{player}", opponent.getName()))
                        .clearFlags()
                        .build();
            } else {
                return new ItemStack(Material.AIR);
            }
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            player.chat("/viewinv " + opponent.getUniqueId().toString());
        }
    }

    @AllArgsConstructor
    private class HungerButton extends Button {

        private int hunger;

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(Material.COOKED_BEEF)
					.name(Praxi.get().getMenusConfig().getString("MATCH.DETAILS-MENU.HUNGER-BUTTON.NAME").replace("{hunger}", String.valueOf(hunger)))
                    .amount(hunger == 0 ? 1 : hunger)
                    .clearFlags()
                    .build();
        }

    }

}
