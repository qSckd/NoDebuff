package me.funky.praxi.divisions;

import org.bukkit.Material;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter @Setter
@RequiredArgsConstructor
public class ProfileDivision {

    private final String name;
    private String displayName = "&aDefault";
    private Material icon;
    private int Durability;
    private String miniLogo;
    private boolean defaultDivision;

    private int minElo = 0;
    private int maxElo = 0;

    private String xpLevel = "&70";
	private int priority = 0;
    private int experience = 0;
}
