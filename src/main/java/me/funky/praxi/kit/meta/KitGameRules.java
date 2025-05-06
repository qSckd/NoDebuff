package me.funky.praxi.kit.meta;

import lombok.Getter;
import lombok.Setter;

public class KitGameRules {

	@Getter @Setter private boolean build;
	@Getter @Setter private boolean ranked;
	@Getter @Setter private boolean editable;
	@Getter @Setter private boolean spleef;
	@Getter @Setter private boolean sumo;
	@Getter @Setter private boolean parkour;
	@Getter @Setter private boolean healthRegeneration;
	@Getter @Setter private boolean showHealth;
	@Getter @Setter private boolean boxing;
	@Getter @Setter private boolean onehit;
	@Getter @Setter private boolean bedfight;
    @Getter @Setter private boolean bot;
    @Getter @Setter private boolean stickFight;
	@Getter @Setter private boolean eggwars;
	@Getter @Setter private boolean bridge;
    @Getter @Setter private boolean battleRush;
    @Getter @Setter private boolean fireballRoyale;
    @Getter @Setter private boolean hcf;
	@Getter @Setter private boolean noDamage;
	@Getter @Setter private int hitDelay = 20;
	@Getter @Setter private String kbProfile;
	@Getter @Setter private boolean bowboost;

}
