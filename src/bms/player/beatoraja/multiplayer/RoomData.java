package bms.player.beatoraja.multiplayer;

import bms.player.beatoraja.multiplayer.packets.in.RoomUpdate;
import bms.player.beatoraja.multiplayer.packets.out.TopicPacket;
import bms.player.beatoraja.multiplayer.packets.out.UserReadyToggle;
import bms.player.beatoraja.multiplayer.types.RoomType;
import bms.player.beatoraja.multiplayer.types.UserType;

import java.util.Arrays;
import java.util.Optional;

public class RoomData {
    private final MPServerConnection server;
    private RoomType roomInfo = null;
    private RoomUpdate properties = null;

    public RoomData(MPServerConnection server) {
        this.server = server;
    }

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

    public UserType getPlayer() {
        return Arrays.stream(properties.getUsers())
                .filter(user -> user.getId().equals(server.getUserId()))
                .findFirst().orElse(null);
    }

    public boolean isHost() {
        return server.getUserId().equals(getProperties().getHost());
    }

    public boolean isReady() {
        return Optional.ofNullable(getPlayer())
                .map(UserType::isReady)
                .orElse(false);
    }

    public void toggleReady() {
        server.sendPacket(new TopicPacket("user.ready.toggle"));
    }

    public void startGame() {
        server.sendPacket(new TopicPacket("room.game.start"));
    }
}
