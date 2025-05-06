package club.nodebuff.moon.profile.managers;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import club.nodebuff.moon.Moon;
import club.nodebuff.moon.divisions.ProfileDivision;
import club.nodebuff.moon.util.CC;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class DivisionsManager {

    private final Moon plugin;

    private static final List<ProfileDivision> divisions = new ArrayList<>();
    private boolean XPBased;

/**
 * Initializes the divisions by loading them from the configuration.
 *
 * Clears the current list of divisions and loads new divisions from the
 * configuration file. Sets the XP-based flag according to the configuration.
 * For each configured division, it creates a new ProfileDivision object and
 * sets its properties such as display name, icon, durability, mini logo,
 * priority, ELO range, experience amount, and XP level. Ensures that at most
 * one division is set as the default. Adds each created division to the list
 * of divisions.
 */
    public void init() {
        if (!divisions.isEmpty()) divisions.clear();

        ConfigurationSection divisionSection = Moon.get().getDivisionsConfig().getConfigurationSection("DIVISIONS");
        if (divisionSection == null || divisionSection.getKeys(false).isEmpty()) return;
        this.XPBased = divisionSection.getBoolean("XP-BASED");

       ConfigurationSection section = divisionSection.getConfigurationSection("RANKS");
       if (section == null || section.getKeys(false).isEmpty()) return;

       for ( String key : section.getKeys(false) ) {
           String path = key + ".";

           ProfileDivision division = new ProfileDivision(key);
           division.setDisplayName(CC.translate(section.getString(path + "NAME")));
           division.setIcon(Material.valueOf(CC.translate(section.getString(path + "ICON")).toUpperCase()));
           division.setDurability(section.getInt(path + "DURABILITY"));
           division.setMiniLogo(CC.translate(section.getString(path + "MINI-LOGO")));
           division.setPriority(section.getInt(path + "PRIORITY"));
           division.setMinElo(section.getInt(path + "ELO-MIN"));
           division.setMaxElo(section.getInt(path + "ELO-MAX"));
           division.setExperience(section.getInt(path + "XP-AMOUNT"));
           division.setXpLevel(CC.translate(section.getString(path + "XP-LEVEL")));

           //In order to prevent more than one default division
           if (!this.isDefaultPresent()) division.setDefaultDivision(section.getBoolean(path + "DEFAULT"));

           divisions.add(division);
       }
    }

    /**
     * Retrieves the division of a player given their experience points.
     * <p>
     * If the player has more experience points than the highest division, it will return the highest division.
     * Otherwise, it will sort all divisions by their experience points in descending order and return the first
     * division in which the player's experience points are greater than or equal to the division's experience
     * points. If no such division is found, it will return the default division.
     *
     * @param xp the player's experience points
     * @return the division of the player
     */
    public ProfileDivision getDivisionByXP(int xp) {
        if (this.getHighest() != null && xp > this.getHighest().getExperience()) return this.getHighest();

        List<ProfileDivision> xpDivisions = new ArrayList<>(divisions);
        xpDivisions.sort(Comparator.comparing(ProfileDivision::getExperience).reversed());

        return xpDivisions.stream().filter(level -> xp >= level.getExperience()).findFirst().orElse(getDefault());
    }

    /**
     * Retrieves the division of a player given their ELO points.
     * <p>
     * If the player's ELO points exceed the maximum ELO of the highest division, 
     * it returns the highest division. Otherwise, iterates through the list of 
     * divisions and returns the first division where the player's ELO falls 
     * within the minimum and maximum ELO range of that division. If no such 
     * division is found, it returns the default division.
     *
     * @param elo the player's ELO points
     * @return the division of the player
     */
    public ProfileDivision getDivisionByELO(int elo) {
        if (this.getHighest() != null && elo > this.getHighest().getMaxElo()) return this.getHighest();

        for ( ProfileDivision eloRank : divisions) {
            if (elo >= eloRank.getMinElo() && elo <= eloRank.getMaxElo()) {
                return eloRank;
            }
        }
        return getDefault();
    }

    /**
     * Retrieves the next division of a player given their experience points.
     * <p>
     * If the player's experience points are greater than or equal to the experience points of the highest division,
     * it returns {@code null}. Otherwise, iterates through the list of divisions and returns the first division
     * where the player's experience points are less than the division's experience points. If no such division is
     * found, it returns {@code null}.
     *
     * @param xp the player's experience points
     * @return the next division of the player, or {@code null} if it is not found
     */
    public ProfileDivision getNextDivisionByXP(int xp) {
        List<ProfileDivision> xpDivisions = new ArrayList<>(divisions);
        xpDivisions.sort(Comparator.comparingInt(ProfileDivision::getExperience));

        for (ProfileDivision division : xpDivisions) {
            if (xp < division.getExperience()) {
                return division;
            }
        }

        return null;
    }

    /**
     * Retrieves the division for the progress bar based on the player's experience points.
     * <p>
     * Sorts all divisions by their experience points in ascending order. Finds the first division
     * where the player's experience points are less than the division's experience points. Then, it
     * attempts to find a division with a priority one less than the found division and returns it.
     * If no such division is found, it returns {@code null}.
     *
     * @param xp the player's experience points
     * @return the division for the progress bar, or {@code null} if not found
     */
    public ProfileDivision getDivisionForBarByXP(int xp) {
        List<ProfileDivision> xpDivisions = new ArrayList<>(divisions);
        xpDivisions.sort(Comparator.comparingInt(ProfileDivision::getExperience));

        for (ProfileDivision division : xpDivisions) {
            if (xp < division.getExperience()) {
                int targetPriority = division.getPriority() - 1;
                for (ProfileDivision divisionTest : xpDivisions) {
                    if (divisionTest.getPriority() == targetPriority) {
                        return divisionTest;
                    }
                }
            }
        }

        return null;
    }

    /**
     * Retrieves the next division of a player given their ELO points.
     * <p>
     * If the player's ELO points are less than the minimum ELO of any division,
     * it returns the first division where the player's ELO is less than the division's
     * minimum ELO. The divisions are sorted in ascending order of minimum ELO to ensure
     * the correct next division is found. If no such division is found, it returns {@code null}.
     *
     * @param elo the player's ELO points
     * @return the next division of the player, or {@code null} if it is not found
     */
    public ProfileDivision getNextDivisionByELO(int elo) {
        List<ProfileDivision> eloDivisions = new ArrayList<>(divisions);
        eloDivisions.sort(Comparator.comparingInt(ProfileDivision::getMinElo));

        for (ProfileDivision division : eloDivisions) {
            if (elo < division.getMinElo()) {
                return division;
            }
        }

        return null;
    }

    /**
     * Retrieves the default division.
     * <p>
     * If no divisions are configured as default, it returns a new division with the name "Default".
     *
     * @return the default division
     */
    public ProfileDivision getDefault() {
        return divisions.stream().filter(ProfileDivision::isDefaultDivision).findAny().orElse(new ProfileDivision("Default"));
    }

    /**
     * Retrieves the highest division.
     * <p>
     * If the divisions are sorted by experience points, it returns the division with the highest experience points.
     * If the divisions are sorted by ELO points, it returns the division with the highest maximum ELO points.
     * <p>
     * If no divisions are configured, it returns {@code null}.
     *
     * @return the highest division, or {@code null} if no divisions are configured
     */
    public ProfileDivision getHighest() {
        LinkedList<ProfileDivision> newDivisions = new LinkedList<>(divisions);

        if (this.XPBased) {
            newDivisions.sort(Comparator.comparingInt(ProfileDivision::getExperience).reversed());
        } else {
            newDivisions.sort(Comparator.comparingInt(ProfileDivision::getMaxElo).reversed());
        }

        return newDivisions.getFirst();
    }

    /**
     * Determines if a default division is present.
     *
     * @return if a default division is present
     */
    public boolean isDefaultPresent() {
        if (divisions.isEmpty()) return false;
        return divisions.stream().anyMatch(ProfileDivision::isDefaultDivision);
    }

    /**
     * Retrieves the list of all profile divisions.
     * <p>
     * This method returns a static list containing all the divisions
     * that have been initialized. The list is unmodifiable and reflects
     * the current state of divisions.
     *
     * @return an unmodifiable list of all profile divisions
     */
    public static List<ProfileDivision> getDivisions() {
        return divisions;
    }
}
