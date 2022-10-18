package com.ferox.util;

import java.util.HashMap;
import java.util.Map;

public enum MemberRights {

    NONE(null),
    MEMBER(ChatCrown.MEMBER),
    SUPER_MEMBER(ChatCrown.SUPER_MEMBER),
    ELITE_MEMBER(ChatCrown.ELITE_MEMBER),
    EXTREME_MEMBER(ChatCrown.EXTREME_MEMBER),
    LEGENDARY_MEMBER(ChatCrown.LEGENDARY_MEMBER),
    VIP(ChatCrown.VIP),
    SPONSOR_MEMBER(ChatCrown.SPONSOR_MEMBER),
    ;

    private final ChatCrown crown;

    MemberRights(ChatCrown crown) {
        this.crown = crown;
    }

    public ChatCrown getCrown() {
        return crown;
    }

    private static final Map<Integer, MemberRights> rights = new HashMap<>();
    static {
        for (MemberRights r : MemberRights.values()) {
            rights.put(r.ordinal(), r);
        }
    }

    public static MemberRights get(int ordinal) {
        return rights.getOrDefault(ordinal, MemberRights.NONE);
    }
}
