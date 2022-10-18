package com.ferox.util;

/*import oshi.hardware.HWDiskStore;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.FileSystem;
import oshi.software.os.OSFileStore;
import oshi.software.os.OperatingSystem;*/

/**
 * Created by Jason MK on 2018-08-08 at 11:45 AM
 */
public class UniqueIdentifier {

   /* private static final UniqueIdentifier INSTANCE = new UniqueIdentifier();

    private static final Path PATH = Paths.get(System.getProperty("user.home"), "jagex_c_oldschool_LIVE.dat");

    HardwareAbstractionLayer hardwareAbstractionLayer = null;
    public UniqueIdentifierSet retrieve() {
        UniqueIdentifierSet set = create();
        return set;
    }

    private UniqueIdentifierSet create() {

        ArrayList<String> hardDiskSerial = new ArrayList<>();

        ArrayList<String> fileStoreUuid = new ArrayList<>();

        String baseBoardSerialId = "invalid";

        String serial = "invalid";

        if (Client.getSystemInfo() != null) {
            try {
                hardwareAbstractionLayer = Client.getSystemInfo().getHardware();
                // Hard disk information, can also use instance.getName() to get the exact hard disk.
                for (int index = 0; index < hardwareAbstractionLayer.getDiskStores().length; index++) {
                    HWDiskStore instance = hardwareAbstractionLayer.getDiskStores()[index];
                    hardDiskSerial.add(CreateUID.formatUid(instance.getSerial()));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                baseBoardSerialId = CreateUID.formatUid(hardwareAbstractionLayer.getComputerSystem().getBaseboard().getSerialNumber());
            } catch (Exception e) {
                e.printStackTrace();
            }

            OperatingSystem os = Client.getSystemInfo().getOperatingSystem();
            FileSystem fileSystem = os.getFileSystem();
            try {
                OSFileStore[] fsArray = fileSystem.getFileStores();
                // Hard disk information, can also use fs.getName() to know the exact hard disk.
                for (OSFileStore fs : fsArray) {
                    fileStoreUuid.add(CreateUID.formatUid(fs.getUUID() + "-" + fs.getTotalSpace()));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                serial = CreateUID.formatUid(Client.getSystemInfo().getHardware().getComputerSystem().getSerialNumber());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return new UniqueIdentifierSet(serial, baseBoardSerialId, hardDiskSerial, fileStoreUuid);
    }

    public class UniqueIdentifierSet {

        final String serial;

        final String baseBoardSerialId;

        public ArrayList<String> hardDiskSerial = new ArrayList<>();

        public ArrayList<String> fileStoreUuid = new ArrayList<>();

        private UniqueIdentifierSet(String serial, String baseBoardSerialId, ArrayList<String> hardDiskSerial, ArrayList<String> fileStoreUuid) {
            this.serial = serial;
            this.baseBoardSerialId = baseBoardSerialId;
            this.hardDiskSerial = hardDiskSerial;
            this.fileStoreUuid = fileStoreUuid;
        }

        @Override
        public String toString() {
            return String.format("serial=%s, baseBoardSerialId=%s, hardDiskSerial=%s, fileStoreUuid=%s", serial, baseBoardSerialId, hardDiskSerial, fileStoreUuid);
        }
    }

    public static UniqueIdentifier getInstance() {
        return INSTANCE;
    }*/

}
