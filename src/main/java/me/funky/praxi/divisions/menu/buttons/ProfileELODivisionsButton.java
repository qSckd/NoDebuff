package club.nodebuff.moon.divisions.menu.buttons;

import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import club.nodebuff.moon.Moon;
import club.nodebuff.moon.profile.Profile;
import club.nodebuff.moon.divisions.ProfileDivision;
import club.nodebuff.moon.util.ProgressBar;
import club.nodebuff.moon.util.ItemBuilderDev;
import club.nodebuff.moon.util.menu.Button;

import java.util.stream.Collectors;

@RequiredArgsConstructor
public class ProfileELODivisionsButton extends Button {

    private static final String KEY = "PROFILE_DIVISIONS.";

    private final Profile profile;
    private final ProfileDivision division;

    //@Override
    public ItemStack getButtonItem(Player player) {
        ProfileDivision profileDivision = profile.getDivision();

        boolean equipped = division.equals(profileDivision);
        boolean unlocked = division.getMaxElo() < profile.getGlobalElo();

        String key = KEY + "ELO.KEY.";

        ItemBuilderDev itemBuilder = new ItemBuilderDev(Material.PAPER);

        if (equipped || unlocked) {
            itemBuilder.name(Moon.get().getMenusConfig().getString(key + "NAME").replace("KEY", equipped ? "EQUIPPED" : "UNLOCKED").replace("{division_display_name}", division.getDisplayName()));
            itemBuilder.lore(Moon.get().getMenusConfig().getStringList(KEY + "ELO.UNLOCKED.LORE")
                    .stream()
                    .map(s -> {
                         s = s.replace("{division_bar}", ProgressBar.getBarMenu(5, 5))
                              .replace("{division_min_elo}", String.valueOf(division.getMinElo()));
                         return s;
            }).collect(Collectors.toList()));
            if (equipped) itemBuilder.enchantment(Enchantment.DURABILITY, 10);
            itemBuilder.clearFlags();
            return itemBuilder.build();
        }

        itemBuilder.name(Moon.get().getMenusConfig().getString(KEY + "ELO.LOCKED.NAME").replace("{division_display_name}", division.getDisplayName()));
        itemBuilder.lore(Moon.get().getMenusConfig().getStringList(KEY + "ELO.LOCKED.LORE")
                .stream()
                .map(s -> {
                     s = s.replace("{division_bar}", ProgressBar.getBarMenu(profile.getGlobalElo(), division.getMinElo()))
                          .replace("{division_min_elo}", String.valueOf(division.getMinElo()));
                     return s;
        }).collect(Collectors.toList()));
        return itemBuilder.build();
    }
}
