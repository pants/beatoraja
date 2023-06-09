package bms.player.beatoraja.multiplayer.packets.out;

import bms.player.beatoraja.multiplayer.packets.Packet;

public class RoomSetSong implements Packet {
    private final String topic = "room.setsong";
    private final String song;
    private final int diff;
    private final int level;
    private final String hash = null;
    private final String audio_hash = null;
    private final String chart_hash;

    public RoomSetSong(String song, int diff, int level, String chart_hash) {
        this.song = song;
        this.diff = diff;
        this.level = level;
        this.chart_hash = chart_hash;
    }
}
