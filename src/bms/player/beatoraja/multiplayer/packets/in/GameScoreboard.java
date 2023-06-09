package bms.player.beatoraja.multiplayer.packets.in;

import bms.player.beatoraja.multiplayer.packets.Packet;
import bms.player.beatoraja.multiplayer.types.ScoreType;

public class GameScoreboard implements Packet {
    private String topic = "game.scoreboard";
    private ScoreType[] users;
}
