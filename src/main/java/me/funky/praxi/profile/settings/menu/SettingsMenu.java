package me.funky.praxi.profile.settings.menu;

import me.funky.praxi.Praxi;
import me.funky.praxi.Locale;
import me.funky.praxi.profile.Profile;
import me.funky.praxi.profile.coinshop.CoinShopMenu;
import me.funky.praxi.profile.settings.Settings;
import me.funky.praxi.profile.visibility.VisibilityLogic;
import me.funky.praxi.profile.themes.Themes;
import me.funky.praxi.profile.times.Times;
import me.funky.praxi.util.ItemBuilder;
import me.funky.praxi.util.Constants;
import me.funky.praxi.util.menu.Button;
import me.funky.praxi.util.menu.Menu;
import me.funky.praxi.util.CC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SettingsMenu extends Menu {
    @Override
    public String getTitle(Player player) {
        return Praxi.get().getMenusConfig().getString("SETTINGS.TITLE");
    }

    @Override
    public int getSize() {
        return Praxi.get().getMenusConfig().getInteger("SETTINGS.SIZE");
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        HashMap<Integer, Button> buttons = new HashMap<>();
        int i = 10;

		for (int j = 0; j < getSize(); ++j) {
			buttons.put(j, Constants.BLACK_PANE);
		}
        for (Settings settings : Settings.values()) {
            while (i == 17 || i == 18 || i == 27 || i == 36) {
                i++;
            }
            buttons.put(i++, new SettingsButton(settings));
        }
        return buttons;
    }

    public SettingsMenu() {
        this.setUpdateAfterClick(false);
    }

    private class SettingsButton extends Button {
        private final Settings settings;

        @Override
        public ItemStack getButtonItem(Player player) {
            ArrayList<String> lore = new ArrayList<>();
            Profile profile = Profile.getByUuid(player.getUniqueId());
            lore.add("&7" + this.settings.getDescription());
            lore.add(" ");
            switch (this.settings) {
                case SHOW_SCOREBOARD: {
					lore.add(profile.getOptions().showScoreboard() ? " &a■ &fShow Scoreboard" : " &8■ &fShow Scoreboard");
                    lore.add(!profile.getOptions().showScoreboard() ? " &c■ &fDon't Show Scoreboard" : " &8■ &fDon't Show Scoreboard");
                    lore.add(" ");
					lore.add("&aClick to toggle.");
                    break;
                }
                case ALLOW_DUELS: {
					lore.add(profile.getOptions().receiveDuelRequests() ? " &a■ &fAllow Duels" : " &8■ &fAllow Duels");
                    lore.add(!profile.getOptions().receiveDuelRequests() ? " &c■ &fDon't Allow Duels" : " &8■ &fDon't Allow Duels");
                    lore.add(" ");
                    lore.add("&aClick to toggle.");
                    break;
				}
                case ALLOW_SPECTATORS: {
					lore.add(profile.getOptions().allowSpectators() ? " &a■ &fAllow Spectators" : " &8■ &fAllow Spectators");
                    lore.add(!profile.getOptions().allowSpectators() ? " &c■ &fDon't Allow Spectators" : " &8■ &fDon't Allow Spectators");
                    lore.add(" ");
                    lore.add("&aClick to toggle.");
                    break;
                }
                case SPECTATOR_MESSAGES: {
					lore.add(profile.getOptions().spectatorMessages() ? " &a■ &fShow Spectator Messages" : " &8■ &fShow Spectator Messages");
                    lore.add(!profile.getOptions().spectatorMessages() ? " &c■ &fDon't Show Spectator Messages" : " &8■ &fDon't Show Spectator Messages");
                    lore.add(" ");
                    lore.add("&aClick to toggle.");
                    break;
                }
				case TIME_CHANGE: {
                    lore.add(profile.getOptions().time().equals(Times.DAY) ? " &a■ &fDay" : " &8■ &fDay");
                    lore.add(profile.getOptions().time().equals(Times.NIGHT) ? " &a■ &fNight" : " &8■ &fNight");
                    lore.add(profile.getOptions().time().equals(Times.SUNRISE) ? " &a■ &fSunrise" : " &8■ &fSunrise");
                    lore.add(profile.getOptions().time().equals(Times.SUNSET) ? " &a■ &fSunset" : " &8■ &fSunset");
                    lore.add("");
                    lore.add("&aClick to change.");
                    break;
                }
				case SHOW_PLAYERS: {
                    lore.add(profile.getOptions().showPlayers() ? " &a■ &fShow Players" : " &8■ &fShow Players");
                    lore.add(!profile.getOptions().showPlayers() ? " &c■ &fDon't Show Players" : " &8■ &fDon't Show Players");
                    lore.add(" ");
                    lore.add("&aClick to toggle.");
                    break;
                }
				case THEME: {
                    lore.add(profile.getOptions().theme().equals(Themes.AQUA) ? " &a■ &fAqua" : " &8■ &fAqua");
                    lore.add(profile.getOptions().theme().equals(Themes.GREEN) ? " &a■ &fGreen" : " &8■ &fGreen");
                    lore.add(profile.getOptions().theme().equals(Themes.RED) ? " &a■ &fRed" : " &8■ &fRed");
                    lore.add(profile.getOptions().theme().equals(Themes.YELLOW) ? " &a■ &fYellow" : " &8■ &fYellow");
                    lore.add(profile.getOptions().theme().equals(Themes.PINK) ? " &a■ &fPink" : " &8■ &fPink");
					lore.add(profile.getOptions().theme().equals(Themes.PURPLE) ? " &a■ &fPurple" : " &8■ &fPurple");
                    lore.add(profile.getOptions().theme().equals(Themes.GOLD) ? " &a■ &fGold" : " &8■ &fGold");
                    lore.add(profile.getOptions().theme().equals(Themes.BLACK) ? " &a■ &fBlack" : " &8■ &fBlack");
                    lore.add("");
                    if (player.hasPermission("moon.donator.themes")) {
                        lore.add("&aClick to change.");
                    } else {
                        lore.add("&aNo Permission.");
                    }
                    break;
                }
            }
            return new ItemBuilder(this.settings.getMaterial()).name(CC.translate("&" + profile.getOptions().theme().getColor().getChar()) + this.settings.getName()).lore(lore).clearEnchantments().build();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            Profile profile = Profile.getByUuid(player.getUniqueId());
            switch (this.settings) {
				case SHOW_SCOREBOARD: {
                    profile.getOptions().showScoreboard(!profile.getOptions().showScoreboard());
                    break;
                }
                case ALLOW_DUELS: {
                    profile.getOptions().receiveDuelRequests(!profile.getOptions().receiveDuelRequests());
                    break;
                }
                case ALLOW_SPECTATORS: {
                    profile.getOptions().allowSpectators(!profile.getOptions().allowSpectators());
                }
                case SPECTATOR_MESSAGES: {
                    profile.getOptions().spectatorMessages(!profile.getOptions().spectatorMessages());
                }
				case SHOW_PLAYERS: {
                    profile.getOptions().showPlayers(!profile.getOptions().showPlayers());
                    VisibilityLogic.handle(player);
                    break;
                }
				case TIME_CHANGE: {
                    switch (profile.getOptions().time()) {
                        case DAY:
                            profile.getOptions().time(Times.NIGHT);
                            break;
                        case NIGHT:
                            profile.getOptions().time(Times.SUNRISE);
                            break;
                        case SUNRISE:
                            profile.getOptions().time(Times.SUNSET);
                            break;
                        case SUNSET:
                            profile.getOptions().time(Times.DAY);
                            break;
                    }
                    player.setPlayerTime(profile.getOptions().time().getTime(), false);
                    break;
                }
				case THEME: {
                    if (!player.hasPermission("moon.donator.themes")) {
                        player.sendMessage(CC.translate("&cYou need a Donator Rank for this"));
                        break;
                    }
                    switch (profile.getOptions().theme()) {
                        case AQUA:
                            profile.getOptions().theme(Themes.GREEN);
                            break;
                        case GREEN:
                            profile.getOptions().theme(Themes.RED);
                            break;
                        case RED:
                            profile.getOptions().theme(Themes.YELLOW);
                            break;
                        case YELLOW:
                            profile.getOptions().theme(Themes.PINK);
                            break;
                        case PINK:
                            profile.getOptions().theme(Themes.PURPLE);
                            break;
						case PURPLE:
                            profile.getOptions().theme(Themes.GOLD);
                            break;
						case GOLD:
                            profile.getOptions().theme(Themes.BLACK);
                            break;
                        case BLACK:
                            profile.getOptions().theme(Themes.AQUA);
                            break;
                       }
                   }
            }
	        Button.playNeutral(player);
            new SettingsMenu().openMenu(player);
            player.updateInventory();
        }

        public SettingsButton(Settings settings) {
            this.settings = settings;
        }
    }
}
