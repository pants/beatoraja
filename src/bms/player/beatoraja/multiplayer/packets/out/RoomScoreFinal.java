package bms.player.beatoraja.multiplayer.packets.out;

import bms.player.beatoraja.multiplayer.packets.Packet;

public class RoomScoreFinal implements Packet {
    public String topic = "room.score.final";
    private int score;
    private int combo;
    private int clear;

    public RoomScoreFinal(int score, int combo, int clear) {
        this.score = score;
        this.combo = combo;
        this.clear = clear;
    }
}
