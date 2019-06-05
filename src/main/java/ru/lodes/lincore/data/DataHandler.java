package ru.lodes.lincore.data;

import io.netty.channel.Channel;
import java.util.*;
import lombok.extern.slf4j.Slf4j;
import static ru.lodes.lincore.utils.GlobalConstants.MARKER_NETWORK;

@Slf4j
public class DataHandler {

    private static final Map<String, CoreUser> players = new HashMap<>();
    private static final Map<String, CoreServer> servers = new HashMap<>();
    private static final Map<String, CoreServer> proxys = new HashMap<>();
    private static final Map<Channel, String> channels = new HashMap<>();

    public void addUser(CoreUser player) {
        players.put(player.getName(), player);
    }

    public void removeUsers(Collection<CoreUser> collection) {
        collection.forEach((player) -> {
            if (!players.containsKey(player.getName())) {
                return;
            }
            players.remove(player.getName());
        });
    }

    public List<CoreUser> getUsers() {
        return new ArrayList<>(players.values());
    }

    public CoreUser getUser(String name) {
        try {
            return players.get(name);
        } catch (Exception e) {
            log.warn(MARKER_NETWORK, "", e);
            return null;
        }
    }

    public void removeUser(String name) {
        try {
            players.remove(name);
        } catch (Exception e) {
            log.warn(MARKER_NETWORK, "", e);
        }
    }

    public void removeServers(Collection<CoreServer> collection) {
        remove(collection);
    }

    public void addServer(CoreServer server) {
        servers.put(server.getName(), server);
        channels.put(server.getChannel(), server.getName());
    }

    public List<CoreServer> getServersList() {
        return new ArrayList<>(servers.values());
    }

    public Map<String, CoreServer> getServersMap() {
        return servers;
    }

    public CoreServer getServer(String name) {
        try {
            return servers.get(name);
        } catch (Exception e) {
            log.warn(MARKER_NETWORK, "", e);
            return null;
        }
    }

    public CoreServer getServer(int id) {
        try {
            for (HashMap.Entry<String, CoreServer> entry : servers.entrySet()) {
                if (entry.getValue().getId() == id) {
                    return entry.getValue();
                }
            }
            return null;
        } catch (Exception e) {
            log.warn(MARKER_NETWORK, "", e);
            return null;
        }
    }

    public void removeServer(String name) {
        try {
            channels.remove(servers.get(name).getChannel());
            servers.remove(name);
        } catch (Exception e) {
            log.warn(MARKER_NETWORK, "", e);
        }
    }

    public void removeServer(Channel ch) {
        try {
            servers.remove(this.getServerStr(ch));
            channels.remove(ch);
        } catch (Exception e) {
            log.warn(MARKER_NETWORK, "", e);
        }
    }

    public String getServerStr(Channel ch) {
        try {
            return channels.get(ch);
        } catch (Exception e) {
            log.warn(MARKER_NETWORK, "", e);
            return null;
        }
    }

    public CoreServer getServer(Channel ch) {
        try {
            return servers.get(this.getServerStr(ch));
        } catch (Exception e) {
            log.warn(MARKER_NETWORK, "", e);
            return null;
        }
    }

    public void removeProxys(Collection<CoreServer> collection) {
        remove(collection);
    }

    public void addProxy(CoreServer server) {
        proxys.put(server.getName(), server);
        channels.put(server.getChannel(), server.getName());
    }

    public List<CoreServer> getProxysList() {
        return new ArrayList<>(proxys.values());
    }

    public Map<String, CoreServer> getProxysMap() {
        return proxys;
    }

    public CoreServer getProxy(String name) {
        try {
            return proxys.get(name);
        } catch (Exception e) {
            log.warn(MARKER_NETWORK, "", e);
            return null;
        }
    }

    public void removeProxy(String name) {
        try {
            channels.remove(proxys.get(name).getChannel());
            proxys.remove(name);
        } catch (Exception e) {
            log.warn(MARKER_NETWORK, "", e);
        }
    }

    public void removeProxy(Channel ch) {
        try {
            proxys.remove(this.getServerStr(ch));
            channels.remove(ch);
        } catch (Exception e) {
            log.warn(MARKER_NETWORK, "", e);
        }
    }

    public String getProxyStr(Channel ch) {
        try {
            return channels.get(ch);
        } catch (Exception e) {
            log.warn(MARKER_NETWORK, "", e);
            return null;
        }
    }

    public CoreServer getProxy(Channel ch) {
        try {
            return proxys.get(this.getServerStr(ch));
        } catch (Exception e) {
            log.warn(MARKER_NETWORK, "", e);
            return null;
        }
    }

    private void remove(Collection<CoreServer> collection) {
        collection.forEach((server) -> {
            try {
                if (servers.containsKey(server.getName())) {
                    servers.remove(server.getName());
                    return;
                }
                if (proxys.containsKey(server.getName())) {
                    proxys.remove(server.getName());
                    return;
                }
                throw new Exception("CoreServer " + server.getName() + " not found!");
            } catch (Exception e) {
                log.warn(MARKER_NETWORK, "", e);
            }
        });
    }
}
