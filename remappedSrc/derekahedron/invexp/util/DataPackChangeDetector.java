package derekahedron.invexp.util;

/**
 * Allow storing data that is reliant on the current data packs.
 * When data packs are changed, the global sync id is changed, which
 * causes objects to re-evaluate their data when sync() is called.
 * This is so we do not have to re-check for validity each time we want to look at
 * DataPack reliant values and instead can just assume that they are still valid.
 */
public abstract class DataPackChangeDetector {
    private static int globalSyncId = 0;
    private int syncId;

    /**
     * Creates a new DataPackChangeDetector and evaluates its data from the current
     * data packs
     */
    public DataPackChangeDetector() {
        evaluate();
        syncId = globalSyncId;
    }

    /**
     * If out of sync, re-evaluate data
     */
    public void sync() {
        if (syncId != globalSyncId) {
            evaluate();
            syncId = globalSyncId;
        }
    }

    /**
     * This is called when syncing data with new DataPack values. This should re-evaluate DataPack
     * reliant values.
     */
    abstract public void evaluate();

    /**
     * Called when DataPack data changes to signal that existing instances are out of date.
     */
    public static void markDirty() {
        globalSyncId++;
    }
}
