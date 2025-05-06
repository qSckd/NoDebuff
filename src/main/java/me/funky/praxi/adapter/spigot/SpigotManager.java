package me.funky.praxi.adapter.spigot;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.UtilityClass;
import me.funky.praxi.Praxi;
import me.funky.praxi.adapter.spigot.impl.Default;
import me.funky.praxi.adapter.spigot.impl.CarbonSpigot;
//import me.funky.praxi.adapter.spigot.impl.PolarSpigot;

@UtilityClass
public class SpigotManager {

    @Getter @Setter public Spigot spigot;
    @Getter @Setter public String serverSpigot;

    public void init() {
         switch (Praxi.get().getServer().getName()) {
             case "Carbon": case "CarbonSpigot":
                spigot = new CarbonSpigot();
                setServerSpigot("CarbonSpigot");
                break;
             /*case "PolarSpigot":
                spigot = new PolarSpigot();
                setServerSpigot("PolarSpigot");
                break;*/
            default:
                spigot = new Default();
                setServerSpigot("Default");
                break;
        }
    }

}
