package ru.lodes.lincore.api.sheduler;

import ru.lodes.lincore.plugin.loader.Plugin;

import java.util.concurrent.ThreadFactory;

@Deprecated
public class GroupedThreadFactory implements ThreadFactory {

    private final ThreadGroup group;

    public static class BungeeGroup extends ThreadGroup {

        private BungeeGroup(String name) {
            super(name);
        }

    }

    public GroupedThreadFactory(Plugin plugin, String name) {
        this.group = new BungeeGroup(name);
    }

    @Override
    public Thread newThread(Runnable r) {
        return new Thread(group, r);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof GroupedThreadFactory)) {
            return false;
        }
        GroupedThreadFactory other = (GroupedThreadFactory) o;
        if (!other.canEqual(this)) {
            return false;
        }
        Object this$group = getGroup();
        Object other$group = other.getGroup();
        return this$group == null ? other$group == null : this$group.equals(other$group);
    }

    protected boolean canEqual(Object other) {
        return other instanceof GroupedThreadFactory;
    }

    @Override
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Object $group = getGroup();
        result = result * 59 + ($group == null ? 43 : $group.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return "GroupedThreadFactory(group=" + getGroup() + ")";
    }

    public ThreadGroup getGroup() {
        return this.group;
    }
}
