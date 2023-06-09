package bms.player.beatoraja.multiplayer.packets.in;

import bms.player.beatoraja.multiplayer.packets.Packet;

public class ServerInfo implements Packet {
    private int refresh_rate;
    private String topic;
    private String userid;
    private String version;

    public int getRefreshRate() {
        return refresh_rate;
    }

    public String getUserid() {
        return userid;
    }

    public String getVersion() {
        return version;
    }
}
