package ru.lodes.lincore.network.packets;

import com.google.common.reflect.ClassPath;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import static ru.lodes.lincore.utils.GlobalConstants.MARKER_NETWORK;

@Slf4j
public class State {

    private static final Map<Integer, Class<? extends Packet>> packets = new HashMap<>();

    public static int getPacketId(Packet<?> packet) {
        Map<Class<? extends Packet>, Integer> inverse = new HashMap<>();
        packets.forEach((key, value) -> inverse.put(value, key));
        if (!packets.containsValue(packet.getClass())
                && (inverse.get(packet.getClass()) == null
                || !inverse.containsKey(packet.getClass()))) {
            log.warn(MARKER_NETWORK, "Пакета {} нет в списке!", packet.getClass());
            log.info(MARKER_NETWORK, "Загружаю пакет {}", packet.getClass());
            addPacket(packet.getClass());
            return getPacketId(packet);
        }
        return inverse.get(packet.getClass());
    }

    public static Packet<?> getPacket(int i) throws InstantiationException, IllegalAccessException {
        Class<? extends Packet> oclass = packets.get(i);
        return oclass == null ? null : (Packet<?>) oclass.newInstance();
    }

    @SuppressWarnings("unchecked")
    public static void addPacket(Class pack) {
        Integer id = namePacketToNum(pack.getSimpleName());
        if (!packets.containsKey(id)) {
            packets.put(id, pack);
        } else {
            log.warn(MARKER_NETWORK, "-------------------------------");
            log.warn(MARKER_NETWORK, "New packet now exists! Check name your module! Packet name: {}", pack.toString());
            log.warn(MARKER_NETWORK, "-------------------------------");
        }
    }

    private static Integer namePacketToNum(String name) {
        byte[] b = name.getBytes();
        int deced = 0;
        for (int i = 0;i < b.length;i++) {
            deced = deced + (b[i] & 0xff) * i;
        }
        return deced;
    }

    static {
        try {
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            for (final ClassPath.ClassInfo info : ClassPath.from(loader).getTopLevelClasses()) {
                if (info.getName().startsWith("ru.lodes.lincore.network.packets")) {
                    final Class<?> clazz = info.load();
                    if (!clazz.equals(State.class) && !clazz.equals(Packet.class) && !clazz.equals(CallbackPacket.class)) {
                        addPacket(clazz);
                    }
                }
            }
        } catch (IOException ex) {
            log.warn(MARKER_NETWORK, "-------------------------------");
            log.warn(MARKER_NETWORK, "", ex);
            log.warn(MARKER_NETWORK, "-------------------------------");
        }
    }
}
