package fr.devsylone.fallenkingdom.fkboard.websocket;

import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Base64;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.bukkit.Bukkit;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.fkboard.websocket.commands.CommandsManager;
import fr.devsylone.fallenkingdom.fkboard.websocket.responses.Identification;
import fr.devsylone.fallenkingdom.fkboard.websocket.responses.ServerInfo;
import fr.devsylone.fallenkingdom.fkboard.websocket.responses.TeamsList;
import fr.devsylone.fkpi.FkPI;

public class FkBoardWebSocket extends WebSocketClient {
    private final Fk plugin;
    private final FkPI fkpi;
    private final CommandsManager commandsManager = new CommandsManager();
    private final SecretKey secretKey;
    private final String boardId;
    private final Runnable invalidDatasCallBack;

    private Random random;
    private Cipher cipher;


    public static final int AES_KEY_SIZE = 128;
    public static final int GCM_IV_LENGTH = 96;
    public static final int GCM_TAG_LENGTH = 128;

    private final static int CODE_BOUND = 951;
    private final static int CODE_WAITING_FOR_BIND = 952;

    public FkBoardWebSocket(URI serverUri, String datasB64, Runnable invalidDatasCallBack) {
        super(serverUri);

        byte[] datas = Base64.getDecoder().decode(datasB64);

        this.secretKey = new SecretKeySpec(datas, 0, AES_KEY_SIZE / 8, "AES");
        this.boardId = Base64.getEncoder().encodeToString(Arrays.copyOfRange(datas, AES_KEY_SIZE / 8, datas.length));
        this.plugin = Fk.getInstance();
        this.fkpi = this.plugin.getFkPI();
        this.invalidDatasCallBack = invalidDatasCallBack;

        try {
            random = new Random();
            cipher = Cipher.getInstance("AES/GCM/NoPadding", "SunJCE");
        } catch (Exception e) {
            e.printStackTrace();
        }

        connect();
    }

    @Override
    public void onOpen(ServerHandshake handshake) {
        send(new Identification(boardId).toJSON());
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        plugin.getLogger().info(String.format("Websocket disconnected. (Code=%d, Reason=%s, remote=%b)", code, reason, remote));
        plugin.onFkBoardWebSocketClose();
    }

    @Override
    public void onMessage(ByteBuffer bytes) {
        System.out.println(Arrays.toString(bytes.array()));
        byte[] iv = new byte[GCM_IV_LENGTH / 8];
        bytes.get(iv);
        
        byte[] cipherText = new byte[bytes.array().length - iv.length];
        bytes.get(cipherText);
        
        System.out.println(Arrays.toString(iv));
        System.out.println(cipherText);
        
        GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        try {
            cipher.init(Cipher.DECRYPT_MODE, secretKey, spec);
            byte[] plainText = cipher.doFinal(cipherText);
            onMessage(new String(plainText));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMessage(String message) {
        JsonElement element;
        try {
            element = new JsonParser().parse(message);
        } catch (JsonSyntaxException exception) {
            plugin.getLogger().warning("Got malformated message : " + message);
            return;
        }

        JsonObject json = element.getAsJsonObject();
        String action = json.has("action") ? json.get("action").getAsString() : "";

        int code = json.get("code").getAsInt();

        if (code == CODE_BOUND) {
            plugin.getLogger().info("Sucessfully bound to the fkboard app via proxy");
            sendWithEncryption(new ServerInfo(plugin).toJSON());
            sendWithEncryption(new TeamsList(FkPI.getInstance().getTeamManager().getTeams(), plugin.getPlayerStatus()).toJSON());
            return;
        }

        else if (code == CODE_WAITING_FOR_BIND) {
            plugin.getLogger().info("No fkboard is waiting for bind with this id ! Closing socket...");
            close();
            Bukkit.getScheduler().runTask(Fk.getInstance(), invalidDatasCallBack);
            return;
        }

        plugin.getLogger().info("Got message: " + message);
        commandsManager.executeCommand(plugin, fkpi, this, action, json);
    }

    public void sendWithEncryption(String text) {
        try {
            System.out.println(getReadyState());
            final byte[] iv = new byte[GCM_IV_LENGTH / 8];
            random.nextBytes(iv);

            GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, spec);

            byte[] cipherText = cipher.doFinal(text.getBytes());

            ByteArrayOutputStream payload = new ByteArrayOutputStream();
            payload.write(iv);
            payload.write(cipherText);

            send(payload.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onError(Exception ex) {
        plugin.getLogger().warning("Got Error: ");
        ex.printStackTrace();
    }

    public void runSync(Runnable task) {
        plugin.getServer().getScheduler().runTaskLater(plugin, task, 1L);
    }

}
