package me.funky.praxi.divisions.menu;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import me.funky.praxi.Praxi;
import me.funky.praxi.profile.managers.DivisionsManager;
import me.funky.praxi.profile.Profile;
import me.funky.praxi.divisions.ProfileDivision;
import me.funky.praxi.divisions.menu.buttons.ProfileELODivisionsButton;
import me.funky.praxi.divisions.menu.buttons.ProfileXPDivisionsButton;
import me.funky.praxi.util.menu.Button;
import me.funky.praxi.util.menu.pagination.PaginatedMenu;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class ProfileDivisionsMenu extends PaginatedMenu {

    private static final String KEY = "PROFILE_DIVISIONS.";
    private final Profile target;

    //@Override
    public String getPrePaginatedTitle(Player player) {
        return Praxi.get().getMenusConfig().getString(KEY + "TITLE");
    }

    @Override
    public int getMaxItemsPerPage(Player player) {
        return Praxi.get().getMenusConfig().getInteger(KEY + "SIZE");
    }

    //@Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        DivisionsManager divisionManager = Praxi.get().getDivisionsManager();
        for (ProfileDivision division : divisionManager.getDivisions()) {
            if (divisionManager.isXPBased()) {
                buttons.put(buttons.size(), new ProfileXPDivisionsButton(target, division));
            } else {
                buttons.put(buttons.size(), new ProfileELODivisionsButton(target, division));
            }
		}
        return buttons;
    }
}
