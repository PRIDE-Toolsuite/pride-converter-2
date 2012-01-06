package uk.ac.ebi.pride.tools.converter.utils.memory;

public class MemoryUsage {

    private static final long MEGA_BYTE = 1024l * 1024l;

    private static final long XMX = Runtime.getRuntime().maxMemory();

    public static String getMessage() {
        Runtime rt = Runtime.getRuntime();

        long free = rt.freeMemory();
        long heapsize = rt.totalMemory();
        long used = (heapsize - free);

        return new StringBuilder()
                .append("Used: ").append(used / MEGA_BYTE)
                .append(" Free: ").append(free / MEGA_BYTE)
                .append(" Heap size: ").append(heapsize / MEGA_BYTE)
                .append(" Xmx: ").append(XMX / MEGA_BYTE)
                .toString();

    }

}
