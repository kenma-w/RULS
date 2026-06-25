package com.chat.ruls.model;

public class ServerNode {
    private String serverId;
    private int currentLoad;
    private boolean active;

    public ServerNode() {
    }
    public ServerNode(String serverId, int currentLoad, boolean active) {
        this.serverId = serverId;
        this.currentLoad = currentLoad;
        this.active = active;
    }

    // Getters and Setters
    public String getServerId() {
        return serverId;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    public int getCurrentLoad() {
        return currentLoad;
    }

    public void setCurrentLoad(int currentLoad) {
        this.currentLoad = currentLoad;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}

