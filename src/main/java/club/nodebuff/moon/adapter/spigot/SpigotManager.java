package club.nodebuff.moon.adapter.spigot;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.UtilityClass;
import club.nodebuff.moon.Moon;
import club.nodebuff.moon.adapter.spigot.impl.Default;
import club.nodebuff.moon.adapter.spigot.impl.CarbonSpigot;
//import club.nodebuff.moon.adapter.spigot.impl.PolarSpigot;

@UtilityClass
public class SpigotManager {

    @Getter @Setter public Spigot spigot;
    @Getter @Setter public String serverSpigot;

    public void init() {
         switch (Moon.get().getServer().getName()) {
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
