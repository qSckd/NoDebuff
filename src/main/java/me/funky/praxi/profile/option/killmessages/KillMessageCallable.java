
package me.funky.praxi.profile.option.killmessages;

import java.util.ArrayList;
import java.util.List;

import me.funky.praxi.Praxi;
import org.bukkit.ChatColor;

public interface KillMessageCallable {
    public String getFormatted(String var1, String var2, boolean var3);

    public List<String> getMessages();

    public List<String> getDescription();

    default public List<String> getFormattedLore() {
        ArrayList<String> stringList = new ArrayList<String>(this.getDescription());
        stringList.add(" ");
        this.getMessages().forEach(message -> stringList.add(ChatColor.GRAY + "... was " + "&b" + message + ChatColor.GRAY + " by ..."));
        stringList.add(" ");
        stringList.add(ChatColor.WHITE.toString() + "One of these messages will");
        stringList.add(ChatColor.WHITE.toString() + "appear when you kill someone.");
        return stringList;
    }
}
