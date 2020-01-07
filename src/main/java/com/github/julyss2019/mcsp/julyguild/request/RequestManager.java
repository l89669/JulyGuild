package com.github.julyss2019.mcsp.julyguild.request;

import com.github.julyss2019.mcsp.julyguild.JulyGuild;
import com.github.julyss2019.mcsp.julyguild.player.GuildPlayer;
import com.github.julyss2019.mcsp.julylibrary.utils.YamlUtil;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class RequestManager {
    private final JulyGuild plugin = JulyGuild.getInstance();
    private Map<UUID, Request> requestMap = new HashMap<>();
    private Map<UUID, List<Request>> sentMap = new HashMap<>();
    private Map<UUID, List<Request>> receiveMap = new HashMap<>();

    public RequestManager() {

    }

    public Collection<Request> getRequests() {
        return new HashSet<>(requestMap.values());
    }

    /**
     * 载入所有请求
     */
    public void loadRequests() {
        File[] files = new File(plugin.getDataFolder(), "data" + File.separator + "requests").listFiles();

        if (files != null) {
            for (File file : files) {
                loadRequest(file);
            }
        }
    }

    /**
     * 卸载请求
     * @param request
     */
    public void unloadRequest(Request request) {
        if (!isLoaded(request.getUuid())) {
            throw new RuntimeException("请求未载入");
        }

        requestMap.remove(request.getUuid());
    }

    /**
     * 发送请求
     * @param request
     */
    public void sendRequest(Request request) {
        File file = new File(plugin.getDataFolder(), "data" + File.separator + "requests" + File.separator + request.getUuid() + ".yml");
        YamlConfiguration yml = YamlUtil.loadYaml(file, StandardCharsets.UTF_8);

        request.onSave(yml);
        YamlUtil.saveYaml(yml, file, StandardCharsets.UTF_8);
        handleRequest(request);
    }

    /**
     * 是否已载入请求
     * @param uuid
     * @return
     */
    public boolean isLoaded(UUID uuid) {
        return requestMap.containsKey(uuid);
    }

    /**
     * 载入请求
     * @param file
     */
    public void loadRequest(File file) {
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
        Request.Type type = Request.Type.valueOf(yml.getString("type"));
        Request request;

        try {
            request = (Request) Class.forName("com.github.julyss2019.mcsp.julyguild.request.entities." + type.getClassName()).newInstance();
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            throw new RuntimeException(e);
        }

        request.onRead(yml);



        if (isLoaded(request.getUuid())) {
            throw new RuntimeException("请求已载入");
        }

        handleRequest(request);
    }

    private void handleRequest(Request request) {
        requestMap.put(Optional.ofNullable(request.getUuid()).orElseThrow(() -> new RuntimeException("UUID不能为null")), request);

        UUID senderUuid = request.getSender().getUuid();
        UUID receiverUuid = request.getReceiver().getUuid();

        if (!sentMap.containsKey(senderUuid)) {
            sentMap.put(senderUuid, new ArrayList<>());
        }

        if (!receiveMap.containsKey(receiverUuid)) {
            receiveMap.put(receiverUuid, new ArrayList<>());
        }

        sentMap.get(senderUuid).add(request);
        receiveMap.get(receiverUuid).add(request);
    }

    public Collection<Request> getSentRequests(Sender sender) {
        return sentMap.getOrDefault(sender.getUuid(), new ArrayList<>());
    }

    public Collection<Request> getReceivedRequests(Receiver receiver) {
        return receiveMap.getOrDefault(receiver.getUuid(), new ArrayList<>());
    }

    /**
     * 得到请求
     * @param uuid
     * @return
     */
    public Request getRequest(UUID uuid) {
        return requestMap.get(uuid);
    }
}
