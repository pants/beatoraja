package bms.player.beatoraja.multiplayer.packets.out;

import bms.player.beatoraja.multiplayer.packets.Packet;

public class RoomScoreUpdate implements Packet {
    private String topic = "room.score.update";
    private final int time;
    private final int score;

    public RoomScoreUpdate(int time, int score) {
        this.time = time;
        this.score = score;
    }
}
