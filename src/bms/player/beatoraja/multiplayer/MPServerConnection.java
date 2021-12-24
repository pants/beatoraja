package bms.player.beatoraja.multiplayer;

import bms.player.beatoraja.MainController;
import bms.player.beatoraja.ScoreData;
import bms.player.beatoraja.multiplayer.packets.Packet;
import bms.player.beatoraja.multiplayer.packets.UserAuth;
import bms.player.beatoraja.multiplayer.packets.out.RoomScoreFinal;
import bms.player.beatoraja.multiplayer.packets.out.RoomScoreUpdate;
import bms.player.beatoraja.multiplayer.packets.out.TopicPacket;
import bms.player.beatoraja.multiplayer.types.RoomType;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class MPServerConnection implements Runnable {

    private final MainController main;
    private Socket serverSocket;
    private DataOutputStream outputStream;
    private DataInputStream inputStream;

    private String host;
    private int port;
    private boolean connected = false;

    private long lastScoreUpdateMs = 0L;
    private int refreshRate = 500;
    private String userId = null;

    private final List<RoomType> availableRooms = new ArrayList<>();

    private final PacketProcessor packetProcessor;
    private final RoomData roomData;

    public String pendingChartUpdateHash = null;
    public boolean pendingGameStart = false;
    public boolean joinRoom = false;
    public boolean syncedReady = false;
    private boolean gameStarted;

    private Thread serverThread;

    public MPServerConnection(MainController main) {
        this.main = main;
        this.packetProcessor = new PacketProcessor(main, this);
        this.roomData = new RoomData(this);
    }

    public boolean connect(String host, int port) {
        this.host = host;
        this.port = port;
        try {
            serverSocket = new Socket(host, port);
            outputStream = new DataOutputStream(serverSocket.getOutputStream());
            inputStream = new DataInputStream(serverSocket.getInputStream());

            serverThread = new Thread(this);
            serverThread.start();

            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean isConnected() {
        return connected;
    }

    @Override
    public void run() {
        try {
            while (!serverSocket.isClosed() && !serverThread.isInterrupted()) {
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
            String jsonStr = json.toJson(packet);
            bos.write(jsonStr.getBytes());
            bos.write((byte) '\n');

            outputStream.write(bos.toByteArray());
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendTopicPacket(String topic) {
        sendPacket(new TopicPacket(topic));
    }

    public void leaveRoom() {
        getRoomData().setRoomInfo(null);
        getRoomData().updateRoom(null);
        sendTopicPacket("room.leave");
    }

    public void updateScore(int time, int score) {
        if (System.currentTimeMillis() - lastScoreUpdateMs > refreshRate) {
            lastScoreUpdateMs = System.currentTimeMillis();
            sendPacket(new RoomScoreUpdate(time, score));
        }
    }

    public void submitFinalScore() {
        final ScoreData scoreData = main.getPlayerResource().getScoreData();
        sendPacket(new RoomScoreFinal(scoreData.getExscore(), scoreData.getCombo(), scoreData.getClear()));
    }

    public void syncReady() {
        syncedReady = true;
        sendPacket(new TopicPacket("room.sync.ready"));
    }

    public void disconnect() {
        connected = false;
        serverThread.interrupt();

        try {
            inputStream.close();
            outputStream.close();
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public RoomData getRoomData() {
        return roomData;
    }

    public int getRefreshRate() {
        return refreshRate;
    }

    public void setRefreshRate(int refreshRate) {
        this.refreshRate = refreshRate;
    }

    public List<RoomType> getAvailableRooms() {
        return availableRooms;
    }

    public boolean isUserInRoom() {
        return isConnected() && getRoomData().getRoomInfo() != null;
    }

    public boolean isGameStarted() {
        return gameStarted;
    }

    public void setGameStarted(boolean gameStarted) {
        this.gameStarted = gameStarted;
    }
}
