package bms.player.beatoraja.multiplayer.packets.in;

import bms.player.beatoraja.multiplayer.packets.Packet;
import bms.player.beatoraja.multiplayer.types.UserType;

public class RoomUpdate implements Packet {
    private String topic;
    private UserType[] users;
    private boolean do_rotate;
    private boolean start_soon;
    private String song;
    private Integer diff;
    private Integer level;
    private String hash;
    private String audio_hash;
    private String chart_hash;
    private String host;
    private String join_token;
    private String owner;
    private String replay_id;
    private String replay_name;
    private boolean mirror_mode;
    private boolean hard_mode;

    public UserType[] getUsers() {
        return users;
    }

    public boolean isRotatingHost() {
        return do_rotate;
    }

    public boolean isStartingSoon() {
        return start_soon;
    }

    public String getSong() {
        return song;
    }

    public int getDifficulty() {
        return diff;
    }

    public int getLevel() {
        return level;
    }

    public String getChartHash() {
        return chart_hash;
    }

    public String getHost() {
        return host;
    }

    public boolean isHard_mode() {
        return hard_mode;
    }
}
