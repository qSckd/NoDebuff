package club.nodebuff.moon.match;

import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import club.nodebuff.moon.kit.Kit;
import club.nodebuff.moon.arena.Arena;

import java.util.UUID;

@Getter
@Setter
public class MatchInfo {

    private final String winningParticipant;
    private final String losingParticipant;
    private final Kit kit;
    private final Arena arena;
    private final long duration;
    private final String date;
    private final UUID uuid;
    private final String type;

    public MatchInfo(String winningParticipant, String losingParticipant, Kit kit, Arena arena, long duration, String date, UUID uuid, String type) {
        this.winningParticipant = winningParticipant;
        this.losingParticipant = losingParticipant;
        this.kit = kit;
        this.arena = arena;
        this.duration = duration;
        this.date = date;
        this.uuid = uuid;
        this.type = type;
    }
}
