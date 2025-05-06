package me.funky.praxi.kit;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.funky.praxi.profile.Profile;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class KitLeaderboards {

    private String name;
    private int elo;
    private int wins;
    private int winStreak;
}
