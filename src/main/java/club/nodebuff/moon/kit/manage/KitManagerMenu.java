package club.nodebuff.moon.kit.manage;

import lombok.AllArgsConstructor;
import club.nodebuff.moon.Moon;
import club.nodebuff.moon.kit.Kit;
import club.nodebuff.moon.kit.meta.KitGameRules;
import club.nodebuff.moon.util.Constants;
import club.nodebuff.moon.util.ItemBuilder;
import club.nodebuff.moon.util.menu.Button;
import club.nodebuff.moon.util.menu.Menu;
import club.nodebuff.moon.util.CC;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
public class KitManagerMenu extends Menu {

    private Kit kit;

    @Override
    public String getTitle(Player player) {
        return "&8Managing " + kit.getName();
    }

    @Override
    public int size(Map<Integer, Button> buttons) {
        return 36;
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        for (int k = 0; k < 36; k++) {
            buttons.put(k, Constants.BLACK_PANE);
        }
        int i = 10;
        buttons.put(i++, new BuildButton(kit, "Build Mode"));
        buttons.put(i++, new SpleefButton(kit, "Spleef Kit"));
        buttons.put(i++, new SumoButton(kit, "Sumo Kit"));
        buttons.put(i++, new BoxingButton(kit, "Boxing Kit"));
        buttons.put(i++, new ParkourButton(kit, "Parkour Kit"));
        buttons.put(i++, new HealthRegenButton(kit, "Health Regeneration"));
        buttons.put(i++, new ShowHealthButton(kit, "Show Health"));

		i = 19;
		buttons.put(i++, new BedfightButton(kit, "BedFight Kit"));
        buttons.put(i++, new EggwarsButton(kit, "Eggwars Kit"));
        buttons.put(i++, new BridgeButton(kit, "Bridge Kit"));
        buttons.put(i++, new BattleRushButton(kit, "BattleRush Kit"));
        buttons.put(i++, new StickfightButton(kit, "StickFight Kit"));
        buttons.put(i++, new HcfButton(kit, "HCF Kit"));
        buttons.put(i++, new NoDamageButton(kit, "No Damage Kit"));

        i = 30;
        buttons.put(i++, new RankedButton(kit, "Ranked Kit"));
        buttons.put(i++, new EditableButton(kit, "Editable Kit"));
        buttons.put(i, new HitDelayButton(kit, "Hit Delay"));
        return buttons;
    }

    @AllArgsConstructor
    private static class BuildButton extends Button {

        private Kit kit;
        private String optName;

        @Override
        public boolean shouldUpdate(Player player, ClickType clickType) {
            return true;
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            List<String> lore = new ArrayList<>();
            lore.add("");
            lore.add(CC.translate("&fCurrent Value: " + "&b" + kit.getGameRules().isBuild()));
            lore.add("");
            lore.add(CC.translate("&aClick to toggle."));
            return new ItemBuilder(Material.WOOL).durability(kit.getGameRules().isBuild() ? 13 : 14).name(CC.translate((kit.getGameRules().isBuild() ? "&a" : "&c") + optName)).lore(lore).build();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            kit.getGameRules().setBuild(!kit.getGameRules().isBuild());
            kit.save();
        }
    }

    @AllArgsConstructor
    private static class SpleefButton extends Button {

        private Kit kit;
        private String optName;

        @Override
        public boolean shouldUpdate(Player player, ClickType clickType) {
            return true;
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            List<String> lore = new ArrayList<>();
            lore.add("");
            lore.add(CC.translate("&fCurrent Value: " + "&b" + kit.getGameRules().isSpleef()));
            lore.add("");
            lore.add(CC.translate("&aClick to toggle."));
            return new ItemBuilder(Material.WOOL).durability(kit.getGameRules().isSpleef() ? 13 : 14).lore(lore).name(CC.translate((kit.getGameRules().isSpleef() ? "&a" : "&c") + optName)).build();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            kit.getGameRules().setSpleef(!kit.getGameRules().isSpleef());
            kit.save();
        }
    }

    @AllArgsConstructor
    private static class SumoButton extends Button {

        private Kit kit;
        private String optName;

        @Override
        public boolean shouldUpdate(Player player, ClickType clickType) {
            return true;
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            List<String> lore = new ArrayList<>();
            lore.add("");
            lore.add(CC.translate("&fCurrent Value: " + "&b" + kit.getGameRules().isSumo()));
            lore.add("");
            lore.add(CC.translate("&aClick to toggle."));
            return new ItemBuilder(Material.WOOL).durability(kit.getGameRules().isSumo() ? 13 : 14).lore(lore).name(CC.translate((kit.getGameRules().isSumo() ? "&a" : "&c") + optName)).build();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            kit.getGameRules().setSumo(!kit.getGameRules().isSumo());
            kit.save();
        }
    }

    @AllArgsConstructor
    private static class BoxingButton extends Button {

        private Kit kit;
        private String optName;

        @Override
        public boolean shouldUpdate(Player player, ClickType clickType) {
            return true;
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            List<String> lore = new ArrayList<>();
            lore.add("");
            lore.add(CC.translate("&fCurrent Value: " + "&b" + kit.getGameRules().isBoxing()));
            lore.add("");
            lore.add(CC.translate("&aClick to toggle."));
            return new ItemBuilder(Material.WOOL).durability(kit.getGameRules().isBoxing() ? 13 : 14).lore(lore).name(CC.translate((kit.getGameRules().isBoxing() ? "&a" : "&c") + optName)).build();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            kit.getGameRules().setBoxing(!kit.getGameRules().isBoxing());
            kit.save();
        }
    }

    @AllArgsConstructor
    private static class ParkourButton extends Button {

        private Kit kit;
        private String optName;

        @Override
        public boolean shouldUpdate(Player player, ClickType clickType) {
            return true;
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            List<String> lore = new ArrayList<>();
            lore.add("");
            lore.add(CC.translate("&fCurrent Value: " + "&b" + kit.getGameRules().isParkour()));
            lore.add("");
            lore.add(CC.translate("&aClick to toggle."));
            return new ItemBuilder(Material.WOOL).durability(kit.getGameRules().isParkour() ? 13 : 14).lore(lore).name(CC.translate((kit.getGameRules().isParkour() ? "&a" : "&c") + optName)).build();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            kit.getGameRules().setParkour(!kit.getGameRules().isParkour());
            kit.save();
        }
    }

    @AllArgsConstructor
    private static class HealthRegenButton extends Button {

        private Kit kit;
        private String optName;

        @Override
        public boolean shouldUpdate(Player player, ClickType clickType) {
            return true;
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            List<String> lore = new ArrayList<>();
            lore.add("");
            lore.add(CC.translate("&fCurrent Value: " + "&b" + kit.getGameRules().isHealthRegeneration()));
            lore.add("");
            lore.add(CC.translate("&aClick to toggle."));
            return new ItemBuilder(Material.WOOL).durability(kit.getGameRules().isHealthRegeneration() ? 13 : 14).lore(lore).name(CC.translate((kit.getGameRules().isHealthRegeneration() ? "&a" : "&c") + optName)).build();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            kit.getGameRules().setHealthRegeneration(!kit.getGameRules().isHealthRegeneration());
            kit.save();
        }
    }

    @AllArgsConstructor
    private static class ShowHealthButton extends Button {

        private Kit kit;
        private String optName;

        @Override
        public boolean shouldUpdate(Player player, ClickType clickType) {
            return true;
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            List<String> lore = new ArrayList<>();
            lore.add("");
            lore.add(CC.translate("&fCurrent Value: " + "&b" + kit.getGameRules().isShowHealth()));
            lore.add("");
            lore.add(CC.translate("&aClick to toggle."));
            return new ItemBuilder(Material.WOOL).durability(kit.getGameRules().isShowHealth() ? 13 : 14).lore(lore).name(CC.translate((kit.getGameRules().isShowHealth() ? "&a" : "&c") + optName)).build();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            kit.getGameRules().setShowHealth(!kit.getGameRules().isShowHealth());
            kit.save();
        }
    }

    @AllArgsConstructor
    private static class RankedButton extends Button {

        private Kit kit;
        private String optName;

        @Override
        public boolean shouldUpdate(Player player, ClickType clickType) {
            return true;
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            List<String> lore = new ArrayList<>();
            lore.add("");
            lore.add(CC.translate("&fCurrent Value: " + "&b" + kit.getGameRules().isRanked()));
            lore.add("");
            lore.add(CC.translate("&aClick to toggle."));
            return new ItemBuilder(Material.WOOL).durability(kit.getGameRules().isRanked() ? 13 : 14).lore(lore).name(CC.translate((kit.getGameRules().isRanked() ? "&a" : "&c") + optName)).build();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            kit.getGameRules().setRanked(!kit.getGameRules().isRanked());
            kit.save();
        }
    }

    @AllArgsConstructor
    private static class BedfightButton extends Button {

        private Kit kit;
        private String optName;

        @Override
        public boolean shouldUpdate(Player player, ClickType clickType) {
            return true;
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            List<String> lore = new ArrayList<>();
            lore.add("");
            lore.add(CC.translate("&fCurrent Value: " + "&b" + kit.getGameRules().isBedfight()));
            lore.add("");
            lore.add(CC.translate("&aClick to toggle."));
            return new ItemBuilder(Material.WOOL).durability(kit.getGameRules().isBedfight() ? 13 : 14).lore(lore).name(CC.translate((kit.getGameRules().isBedfight() ? "&a" : "&c") + optName)).build();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            kit.getGameRules().setBedfight(!kit.getGameRules().isBedfight());
            kit.save();
        }
    }

    @AllArgsConstructor
    private static class EggwarsButton extends Button {

        private Kit kit;
        private String optName;

        @Override
        public boolean shouldUpdate(Player player, ClickType clickType) {
            return true;
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            List<String> lore = new ArrayList<>();
            lore.add("");
            lore.add(CC.translate("&fCurrent Value: " + "&b" + kit.getGameRules().isEggwars()));
            lore.add("");
            lore.add(CC.translate("&aClick to toggle."));
            return new ItemBuilder(Material.WOOL).durability(kit.getGameRules().isEggwars() ? 13 : 14).lore(lore).name(CC.translate((kit.getGameRules().isEggwars() ? "&a" : "&c") + optName)).build();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            kit.getGameRules().setEggwars(!kit.getGameRules().isEggwars());
            kit.save();
        }
    }

    @AllArgsConstructor
    private static class BridgeButton extends Button {

        private Kit kit;
        private String optName;

        @Override
        public boolean shouldUpdate(Player player, ClickType clickType) {
            return true;
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            List<String> lore = new ArrayList<>();
            lore.add("");
            lore.add(CC.translate("&fCurrent Value: " + "&b" + kit.getGameRules().isBridge()));
            lore.add("");
            lore.add(CC.translate("&aClick to toggle."));
            return new ItemBuilder(Material.WOOL).durability(kit.getGameRules().isBridge() ? 13 : 14).lore(lore).name(CC.translate((kit.getGameRules().isBridge() ? "&a" : "&c") + optName)).build();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            kit.getGameRules().setBridge(!kit.getGameRules().isBridge());
            kit.save();
        }
    }

    @AllArgsConstructor
    private static class BattleRushButton extends Button {

        private Kit kit;
        private String optName;

        @Override
        public boolean shouldUpdate(Player player, ClickType clickType) {
            return true;
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            List<String> lore = new ArrayList<>();
            lore.add("");
            lore.add(CC.translate("&fCurrent Value: " + "&b" + kit.getGameRules().isBattleRush()));
            lore.add("");
            lore.add(CC.translate("&aClick to toggle."));
            return new ItemBuilder(Material.WOOL).durability(kit.getGameRules().isBattleRush() ? 13 : 14).lore(lore).name(CC.translate((kit.getGameRules().isBattleRush() ? "&a" : "&c") + optName)).build();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            kit.getGameRules().setBattleRush(!kit.getGameRules().isBattleRush());
            kit.save();
        }
    }

    @AllArgsConstructor
    private static class StickfightButton extends Button {

        private Kit kit;
        private String optName;

        @Override
        public boolean shouldUpdate(Player player, ClickType clickType) {
            return true;
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            List<String> lore = new ArrayList<>();
            lore.add("");
            lore.add(CC.translate("&fCurrent Value: " + "&b" + kit.getGameRules().isStickFight()));
            lore.add("");
            lore.add(CC.translate("&aClick to toggle."));
            return new ItemBuilder(Material.WOOL).durability(kit.getGameRules().isStickFight() ? 13 : 14).lore(lore).name(CC.translate((kit.getGameRules().isStickFight() ? "&a" : "&c") + optName)).build();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            kit.getGameRules().setStickFight(!kit.getGameRules().isStickFight());
            kit.save();
        }
    }

    @AllArgsConstructor
    private static class HitDelayButton extends Button {

        private Kit kit;
        private String optName;

        @Override
        public boolean shouldUpdate(Player player, ClickType clickType) {
            return true;
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            List<String> lore = new ArrayList<>();
            lore.add("");
            lore.add(CC.translate("&fCurrent Value: " + "&b" + kit.getGameRules().getHitDelay()));
            lore.add("");
            lore.add(CC.translate("&aLeft-click to add 1."));
            lore.add(CC.translate("&cRight-click to remove 1."));
            return new ItemBuilder(Material.WOOL).durability(13).name(CC.translate("&a" + optName)).lore(lore).build();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            if (clickType == ClickType.LEFT) {
                kit.getGameRules().setHitDelay(kit.getGameRules().getHitDelay() + 1);
                player.sendMessage(CC.translate("&aIncreased " + kit.getName() + "'s hit delay to " + kit.getGameRules().getHitDelay()));
                kit.save();
            } if (clickType == ClickType.RIGHT) {
                if (kit.getGameRules().getHitDelay() - 1 < 0) return;
                kit.getGameRules().setHitDelay(kit.getGameRules().getHitDelay() - 1);
                kit.save();
                player.sendMessage(CC.translate("&cDecreased " + kit.getName() + "'s hit delay to " + kit.getGameRules().getHitDelay()));
            }
        }
    }

    @AllArgsConstructor
    private static class HcfButton extends Button {

        private Kit kit;
        private String optName;

        @Override
        public boolean shouldUpdate(Player player, ClickType clickType) {
            return true;
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            List<String> lore = new ArrayList<>();
            lore.add("");
            lore.add(CC.translate("&fCurrent Value: " + "&b" + kit.getGameRules().isHcf()));
            lore.add("");
            lore.add(CC.translate("&aClick to toggle."));
            return new ItemBuilder(Material.WOOL).durability(kit.getGameRules().isHcf() ? 13 : 14).lore(lore).name(CC.translate((kit.getGameRules().isHcf() ? "&a" : "&c") + optName)).build();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            kit.getGameRules().setHcf(!kit.getGameRules().isHcf());
            kit.save();
        }
    }

    @AllArgsConstructor
    private static class NoDamageButton extends Button {

        private Kit kit;
        private String optName;

        @Override
        public boolean shouldUpdate(Player player, ClickType clickType) {
            return true;
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            List<String> lore = new ArrayList<>();
            lore.add("");
            lore.add(CC.translate("&fCurrent Value: " + "&b" + kit.getGameRules().isNoDamage()));
            lore.add("");
            lore.add(CC.translate("&aClick to toggle."));
            return new ItemBuilder(Material.WOOL).durability(kit.getGameRules().isNoDamage() ? 13 : 14).lore(lore).name(CC.translate((kit.getGameRules().isNoDamage() ? "&a" : "&c") + optName)).build();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            kit.getGameRules().setNoDamage(!kit.getGameRules().isNoDamage());
            kit.save();
        }
    }

    @AllArgsConstructor
    private static class EditableButton extends Button {

        private Kit kit;
        private String optName;

        @Override
        public boolean shouldUpdate(Player player, ClickType clickType) {
            return true;
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            List<String> lore = new ArrayList<>();
            lore.add("");
            lore.add(CC.translate("&fCurrent Value: " + "&b" + kit.getGameRules().isEditable()));
            lore.add("");
            lore.add(CC.translate("&aClick to toggle."));
            return new ItemBuilder(Material.WOOL).durability(kit.getGameRules().isEditable() ? 13 : 14).lore(lore).name(CC.translate((kit.getGameRules().isEditable() ? "&a" : "&c") + optName)).build();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            kit.getGameRules().setEditable(!kit.getGameRules().isEditable());
            kit.save();
        }
    }
}
