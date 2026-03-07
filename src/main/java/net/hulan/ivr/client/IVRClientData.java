package net.hulan.ivr.client;

public class IVRClientData {

    public static final IVRClientCache DATA_CACHE = new IVRClientCache();

    public static void receivePacket() {
        DATA_CACHE.sync();
        DATA_CACHE.refreshDynamicResources();
    }
}
