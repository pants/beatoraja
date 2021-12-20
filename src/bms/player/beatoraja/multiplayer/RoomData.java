package bms.player.beatoraja.multiplayer;

import bms.player.beatoraja.multiplayer.packets.in.RoomUpdate;
import bms.player.beatoraja.multiplayer.types.RoomType;

public class RoomData {
    private RoomType roomInfo = null;
    private RoomUpdate properties = null;

    public RoomType getRoomInfo() {
        return roomInfo;
    }

    public void setRoomInfo(RoomType roomInfo) {
        this.roomInfo = roomInfo;
    }

    public RoomUpdate getProperties() {
        return properties;
    }

    public void updateRoom(RoomUpdate lastRoomUpdate) {
        this.properties = lastRoomUpdate;
    }
}
