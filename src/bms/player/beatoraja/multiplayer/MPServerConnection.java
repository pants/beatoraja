package bms.player.beatoraja.multiplayer;

import bms.player.beatoraja.multiplayer.packets.Packet;
import bms.player.beatoraja.multiplayer.packets.UserAuth;
import bms.player.beatoraja.multiplayer.packets.in.RoomUpdate;
import bms.player.beatoraja.multiplayer.packets.in.ServerInfo;
import bms.player.beatoraja.multiplayer.packets.in.ServerRoomJoined;
import bms.player.beatoraja.multiplayer.types.RoomType;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.function.Consumer;
import java.util.logging.Logger;

public class MPServerConnection extends Thread {

    private Socket serverSocket;
    private DataOutputStream outputStream;
    private DataInputStream inputStream;

    private String host;
    private int port;
    private boolean connected = false;

    private int refreshRate = 500;
    private String userId = null;

    private HashMap<String, Consumer<Packet>> packetSubscriptions = new HashMap<>();
    private PacketProcessor packetProcessor;
    private RoomType roomInfo = null;

    public boolean joinRoom = false;

    private RoomUpdate lastRoomUpdate = null;

    public MPServerConnection() {
        this.packetProcessor = new PacketProcessor(this);
    }

    public void connect(String host, int port) {
        this.host = host;
        this.port = port;
        try {
            serverSocket = new Socket(host, port);
            outputStream = new DataOutputStream(serverSocket.getOutputStream());
            inputStream = new DataInputStream(serverSocket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.start();
    }

    public boolean isConnected() {
        return connected;
    }

    @Override
    public void run() {
        subscribeToPacket("server.info", this::onServerInfo);
        subscribeToPacket("server.room.joined", this::onServerRoomJoined);
        subscribeToPacket("room.update", this::onRoomUpdate);
        try {
            while (!serverSocket.isClosed()) {
                final byte packetMode = inputStream.readByte();
                final ByteArrayOutputStream bos = new ByteArrayOutputStream();

                byte b;
                while ((b = inputStream.readByte()) != '\n') {
                    bos.write(b);
                }

                final String jsonPacket = new String(bos.toByteArray(), StandardCharsets.UTF_8);
                JsonValue jsonValue = new JsonReader().parse(jsonPacket);
                if (jsonValue.has("topic")) {
                    final String topic = jsonValue.getString("topic");
                    Logger.getGlobal().info("Received packet: " + jsonValue.getString("topic"));
                    Logger.getGlobal().info("Data: " + jsonPacket);
                    connected = true;
                    packetProcessor.processPacket(topic, jsonPacket);
                } else {
                    connected = false;
                    Logger.getGlobal().warning("Server sent an invalid packet: " + jsonPacket);
                }

                // json.fromJson()
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void authenticate(String name, String password, String version) {
        sendPacket(new UserAuth(password, name, version));
    }

    public void sendPacket(Packet packet) {
        try {
            Json json = new Json();
            json.setOutputType(JsonWriter.OutputType.json);

            final ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bos.write(1);
            bos.write(json.toJson(packet).getBytes());
            bos.write((byte) '\n');

            outputStream.write(bos.toByteArray());
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void disconnect() {
        try {
            inputStream.close();
            outputStream.close();
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void notifySubscriber(String topic, Packet packet) {
        if (packetSubscriptions.containsKey(topic)) {
            packetSubscriptions.get(topic).accept(packet);
        }
    }

    public void subscribeToPacket(String topic, Consumer<Packet> callback) {
        this.packetSubscriptions.put(topic, callback);
    }

    public void unsubscribeFromPacket(String topic, Consumer<Packet> callback) {
        this.packetSubscriptions.remove(topic);
    }

    private void onServerRoomJoined(Packet packet) {
        this.roomInfo = ((ServerRoomJoined)packet).getRoom();
        this.joinRoom = true;
    }

    private void onRoomUpdate(Packet packet) {
        RoomUpdate roomUpdate = (RoomUpdate) packet;
        this.lastRoomUpdate = roomUpdate;
    }

    public void onServerInfo(Packet packet) {
        ServerInfo info = (ServerInfo) packet;
        this.refreshRate = info.getRefreshRate();
        this.userId = info.getUserid();
    }

    public String getUserId() {
        return userId;
    }

    public RoomType getRoomInfo() {
        return roomInfo;
    }

    public RoomUpdate getLastRoomUpdate() {
        return lastRoomUpdate;
    }
}
