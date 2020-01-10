package com.github.julyss2019.mcsp.julyguild.request;

import com.github.julyss2019.mcsp.julyguild.JulyGuild;
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

    public boolean isValid(Request request) {
        return isLoaded(request.getUuid());
    }

    public Collection<Request> getRequests() {
        return new HashSet<>(requestMap.values());
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
        sentMap.get(request.getSender().getUuid()).remove(request);
        receiveMap.get(request.getReceiver().getUuid()).remove(request);
    }

    /**
     * 发送请求
     * @param request
     */
    public void sendRequest(Request request) {
        File file = new File(plugin.getDataFolder(), "data" + File.separator + "requests" + File.separator + request.getUuid() + ".yml");
        YamlConfiguration yml = YamlUtil.loadYaml(file, StandardCharsets.UTF_8);

        request.save(yml);
        YamlUtil.saveYaml(yml, file, StandardCharsets.UTF_8);
        handleRequest(request);
    }

    /**
     * 摧毁请求
     * @param request
     */
    public void deleteRequest(Request request) {
        if (!isLoaded(request.getUuid())) {
            throw new RuntimeException("该请求未被载入");
        }

        File file = new File(plugin.getDataFolder(), "data" + File.separator + "requests" + File.separator + request.getUuid() + ".yml");

        if (!file.delete()) {
            throw new RuntimeException("文件删除失败: " + file.getAbsolutePath());
        }

        unloadRequest(request);
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
            request = type.getClazz().newInstance();
        } catch (IllegalAccessException | InstantiationException e) {
            throw new RuntimeException(e);
        }

        request.load(yml);

        if (isLoaded(request.getUuid())) {
            throw new RuntimeException("请求已载入");
        }

        handleRequest(request);
    }

    /**
     * 潮衣库请求
     * @param request
     */
    private void handleRequest(Request request) {
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
        requestMap.put(request.getUuid(), request);
    }

    /**
     * 得到发送的请求
     * @param sender
     * @return
     */
    public List<Request> getSentRequests(Sender sender) {
        return sentMap.getOrDefault(sender.getUuid(), new ArrayList<>());
    }

    /**
     * 得到接收的请求
     * @param receiver
     * @return
     */
    public List<Request> getReceivedRequests(Receiver receiver) {
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
