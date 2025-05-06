package club.nodebuff.moon.divisions.menu;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import club.nodebuff.moon.Moon;
import club.nodebuff.moon.profile.managers.DivisionsManager;
import club.nodebuff.moon.profile.Profile;
import club.nodebuff.moon.divisions.ProfileDivision;
import club.nodebuff.moon.divisions.menu.buttons.ProfileELODivisionsButton;
import club.nodebuff.moon.divisions.menu.buttons.ProfileXPDivisionsButton;
import club.nodebuff.moon.util.menu.Button;
import club.nodebuff.moon.util.menu.pagination.PaginatedMenu;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class ProfileDivisionsMenu extends PaginatedMenu {

    private static final String KEY = "PROFILE_DIVISIONS.";
    private final Profile target;

    //@Override
    public String getPrePaginatedTitle(Player player) {
        return Moon.get().getMenusConfig().getString(KEY + "TITLE");
    }

    @Override
    public int getMaxItemsPerPage(Player player) {
        return Moon.get().getMenusConfig().getInteger(KEY + "SIZE");
    }

    //@Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        DivisionsManager divisionManager = Moon.get().getDivisionsManager();
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
