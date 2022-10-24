package com.ferox.net.requester;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.zip.CRC32;
import java.util.zip.GZIPInputStream;

import com.ferox.Client;
import com.ferox.ClientConstants;
import com.ferox.cache.Archive;
import com.ferox.collection.LinkedList;
import com.ferox.collection.Queue;
import com.ferox.io.Buffer;
import com.ferox.sign.SignLink;
import com.ferox.util.FileUtils;

public final class ResourceProvider extends Provider implements Runnable {

    private int totalFiles;
    private final LinkedList requested;
    private int maximumPriority;
    public String loadingMessage;
    private int deadTime;
    private long lastRequestTime;
    private int[] landscapes;
    private final byte[] payload;
    public int tick;
    private final byte[][] fileStatus;
    private Client clientInstance;
    private final LinkedList extras;
    private int completedSize;
    private int remainingData;
    private int[] musicPriorities;
    public int errors;
    private int[] mapFiles;
    private int filesLoaded;
    private boolean running;
    private OutputStream outputStream;
    private int[] membersArea;
    private boolean expectingData;
    private final LinkedList complete;
    private final byte[] gzipInputBuffer;
    private int[] anIntArray1360;
    private final Queue requests;
    private InputStream inputStream;
    private Socket socket;
    private final int[][] versions;
    private int uncompletedCount;
    private int completedCount;
    private final LinkedList unrequested;
    private Resource current;
    private final LinkedList mandatoryRequests;
    private int[] areas;
    private byte[] modelIndices;
    private int idleTime;
    private final CRC32 crc32;

    public String currentDownload = "";

    private String forId(int type) {
        switch (type) {
            case 1:
                return "Model";
            case 2:
                return "Animation";
            case 3:
                return "Sound";
            case 4:
                return "Map";
        }
        return "";
    }

    public ResourceProvider() {
        requested = new LinkedList();
        loadingMessage = "";
        payload = new byte[500];
        fileStatus = new byte[4][];
        extras = new LinkedList();
        running = true;
        expectingData = false;
        complete = new LinkedList();
        gzipInputBuffer = new byte[0x71868];
        requests = new Queue();
        versions = new int[4][];
        unrequested = new LinkedList();
        mandatoryRequests = new LinkedList();
        crc32 = new CRC32();
    }

    private void respond() {
        try {
            int available = inputStream.available();
            if (remainingData == 0 && available >= 10) {
                expectingData = true;
                for (int skip = 0; skip < 10; skip += inputStream.read(payload, skip, 10 - skip))
                    ;
                int type = payload[0] & 0xff;
                int file = ((payload[1] & 0xff) << 16) + ((payload[2] & 0xff) << 8) + (payload[3] & 0xff);
                int length = ((payload[4] & 0xff) << 32) + ((payload[5] & 0xff) << 16) + ((payload[6] & 0xff) << 8) + (payload[7] & 0xff);
                int sector = ((payload[8] & 0xff) << 8) + (payload[9] & 0xff);
                current = null;
                for (Resource resource = (Resource) requested.first(); resource != null; resource = (Resource) requested.next()) {
                    if (resource.dataType == type && resource.ID == file)
                        current = resource;
                    if (current != null)
                        resource.loopCycle = 0;
                }

                if (current != null) {
                    currentDownload = "Downloading " + forId(current.dataType + 1) + " " + current.ID + "";
                    idleTime = 0;
                    if (length == 0) {
                        SignLink.reporterror("Rej: " + type + "," + file);
                        current.buffer = null;
                        if (current.incomplete)
                            synchronized (complete) {
                                complete.insertBack(current);
                            }
                        else {
                            current.remove();
                        }
                        current = null;
                    } else {
                        if (current.buffer == null && sector == 0)
                            current.buffer = new byte[length];
                        if (current.buffer == null && sector != 0)
                            throw new IOException("missing start of file");
                    }
                }
                completedSize = sector * 500;
                remainingData = 500;
                if (remainingData > length - sector * 500)
                    remainingData = length - sector * 500;
            }
            if (remainingData > 0 && available >= remainingData) {
                expectingData = true;
                byte data[] = payload;
                int read = 0;
                if (current != null) {
                    data = current.buffer;
                    read = completedSize;
                }
                for (int skip = 0; skip < remainingData; skip += inputStream.read(data, skip + read, remainingData - skip))
                    ;
                if (remainingData + completedSize >= data.length && current != null) {
                    if (clientInstance.indices[0] != null)
                        clientInstance.indices[current.dataType + 1].writeFile(data.length, data, current.ID);
                    if (!current.incomplete && current.dataType == 3) {
                        current.incomplete = true;
                        current.dataType = 93;
                    }
                    if (current.incomplete)
                        synchronized (complete) {
                            complete.insertBack(current);
                        }
                    else {
                        current.remove();
                    }
                }
                remainingData = 0;
            }
        } catch (IOException ex) {
            try {
                socket.close();
            } catch (Exception _ex) {
                _ex.printStackTrace();
            }
            socket = null;
            inputStream = null;
            outputStream = null;
            remainingData = 0;
        }
    }


    public int[] file_amounts = new int[4];

    private final String crcNames[] = {"model_crc", "anim_crc", "midi_crc", "map_crc"};
    private final int[][] crcs = new int[crcNames.length][];
    private boolean debugCheapHaxValues = false;

    public void initialize(Archive archive, Client client) {

        for (int i = 0; i < crcNames.length; i++) {
            byte[] crc_file = archive.get(crcNames[i]);
            int length = 0;

            if (crc_file != null) {
                length = crc_file.length / 4;
                Buffer crcStream = new Buffer(crc_file);
                crcs[i] = new int[length];
                fileStatus[i] = new byte[length];
                for (int ptr = 0; ptr < length; ptr++) {
                    crcs[i][ptr] = crcStream.readInt();
                }
            }
        }


        byte[] data = ClientConstants.LOAD_OSRS_DATA_FROM_CACHE_DIR ? FileUtils.read(ClientConstants.DATA_DIR + "/maps/map_index") : archive.get("map_index");
        Buffer stream = new Buffer(data);
        int j1 = stream.readUShort();//data.length / 6;
        areas = new int[j1];
        mapFiles = new int[j1];
        landscapes = new int[j1];
        file_amounts[3] = data.length;
        for (int i2 = 0; i2 < j1; i2++) {
            areas[i2] = stream.readUShort();
            mapFiles[i2] = stream.readUShort();
            landscapes[i2] = stream.readUShort();
            //The cheapHaxValues regions seem to be tutorial island, and a couple quest areas, and sorceress's garden
        }

        System.out.println(String.format("Loaded %d maps loading OSRS version %d and SUB version %d", file_amounts[3], ClientConstants.OSRS_DATA_VERSION, ClientConstants.OSRS_DATA_SUB_VERSION));

        data = archive.get("midi_index");
        stream = new Buffer(data);
        j1 = data.length;
        file_amounts[2] = j1;
        musicPriorities = new int[j1];
        for (int k2 = 0; k2 < j1; k2++)
            musicPriorities[k2] = stream.readUByte();
        System.out.println(String.format("Loaded %d sounds loading OSRS version %d and SUB version %d", file_amounts[2], ClientConstants.OSRS_DATA_VERSION, ClientConstants.OSRS_DATA_SUB_VERSION));

        //For some reason, model_index = anim_index and vice versa
        data = archive.get("model_index");
        file_amounts[1] = data.length;

        data = archive.get("anim_index");
        file_amounts[0] = data.length;
        System.out.println(String.format("Loaded %d models loading OSRS version %d and SUB version %d", file_amounts[0], ClientConstants.OSRS_DATA_VERSION, ClientConstants.OSRS_DATA_SUB_VERSION));

        clientInstance = client;
        running = true;
        clientInstance.startRunnable(this, 2);
    }

    public int remaining() {
        synchronized (requests) {
            return requests.size();
        }
    }

    public void disable() {
        running = false;
    }

    public void preloadMaps(boolean members) {
        for (int area = 0; area < areas.length; area++) {
            if (members || membersArea[area] != 0) {
                requestExtra((byte) 2, 3, landscapes[area]);
                requestExtra((byte) 2, 3, mapFiles[area]);
            }
        }
    }

    public int getVersionCount(int index) {
        return versions[index].length;
    }

    private void request(Resource resource) {
       /* try {

            if (socket == null || !socket.isConnected()) {
                socket = Client.instance.openSocket(JagGrabConstants.FILE_SERVER_PORT);
                inputStream = socket.getInputStream();
                outputStream = socket.getOutputStream();
            }

            //Store opcode
            payload[0] = JagGrabConstants.ONDEMAND_REQUEST_OPCODE;

            //Store data type as byte
            payload[1] = (byte) resource.dataType;

            //Store file id as int
            payload[2] = (byte) (resource.ID >> 24);
            payload[3] = (byte) (resource.ID >> 16);
            payload[4] = (byte) (resource.ID >> 8);
            payload[5] = (byte) resource.ID;

            //Write the buffer
            outputStream.write(payload, 0, 6);

            deadTime = 0;
            errors = -10000;
            return;

        } catch (IOException ex) {
            //ex.printStackTrace();
        }
        try {
            socket.close();
        } catch (Exception ex) {
            //ex.printStackTrace();
        }
        socket = null;
        inputStream = null;
        outputStream = null;
        remainingData = 0;
        errors++;*/
    }

    public int getAnimCount() {
        return 33568;
    }

    public int getModelCount() {
        return 120000;
    }

    @Override
    public final void provide(int file) {
        provide(0, file);
    }

    public void provide(int type, int file) {
        if (type < 0 || file < 0)
            return;
        synchronized (requests) {
            for (Resource resource = (Resource) requests.reverseGetFirst(); resource != null; resource = (Resource) requests.reverseGetNext())
                if (resource.dataType == type && resource.ID == file) {
                    return;
                }

            Resource resource = new Resource();
            resource.dataType = type;
            resource.ID = file;
            resource.incomplete = true;
            synchronized (mandatoryRequests) {
                mandatoryRequests.insertBack(resource);
            }
            requests.insertHead(resource);
        }
    }

    public int getModelIndex(int i) {
        return modelIndices[i] & 0xff;
    }

    public void run() {
        try {
            while (running) {
                tick++;
                int sleepTime = 20;
                if (maximumPriority == 0 && clientInstance.indices[0] != null)
                    sleepTime = 50;
                try {
                    Thread.sleep(sleepTime);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                expectingData = true;
                for (int index = 0; index < 100; index++) {
                    if (!expectingData)
                        break;
                    expectingData = false;
                    loadMandatory();
                    requestMandatory();
                    if (uncompletedCount == 0 && index >= 5)
                        break;
                    passive_request();
                    if (inputStream != null)
                        respond();
                }

                boolean idle = false;
                for (Resource resource = (Resource) requested.first(); resource != null; resource = (Resource) requested.next())
                    if (resource.incomplete) {
                        idle = true;
                        resource.loopCycle++;
                        if (resource.loopCycle > 50) {
                            resource.loopCycle = 0;
                            request(resource);
                        }
                    }

                if (!idle) {
                    for (Resource resource = (Resource) requested.first(); resource != null; resource = (Resource) requested.next()) {
                        idle = true;
                        resource.loopCycle++;
                        if (resource.loopCycle > 50) {
                            resource.loopCycle = 0;
                            request(resource);
                        }
                    }

                }
                if (idle) {
                    idleTime++;
                    if (idleTime > 750) {
                        try {
                            socket.close();
                        } catch (Exception _ex) {
                        }
                        socket = null;
                        inputStream = null;
                        outputStream = null;
                        remainingData = 0;
                    }
                } else {
                    idleTime = 0;
                    loadingMessage = "";
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            SignLink.reporterror("od_ex " + exception.getMessage());
        }
    }

    public void passive_request(int type, int file) {
        if (clientInstance.indices[0] == null) {
            return;
        } else if (maximumPriority == 0) {
            return;
        }
        Resource resource = new Resource();
        resource.dataType = file;
        resource.ID = type;
        resource.incomplete = false;
        synchronized (extras) {
            extras.insertBack(resource);
        }
    }

    public Resource next() {
        Resource resource;
        synchronized (complete) {
            resource = (Resource) complete.pop();
        }
        if (resource == null)
            return null;
        synchronized (requests) {
            resource.unlinkCacheable();
        }
        if (resource.buffer == null)
            return resource;
        int read = 0;
        try {
            GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(resource.buffer));
            do {
                if (read == gzipInputBuffer.length)
                    throw new RuntimeException("buffer overflow!");
                int in = gis.read(gzipInputBuffer, read, gzipInputBuffer.length - read);
                if (in == -1)
                    break;
                read += in;
            } while (true);
        } catch (IOException _ex) {
            System.err.println("Failed to unzip model [" + resource.ID + "] type = " + resource.dataType);
            _ex.printStackTrace();
            return null;
        }
        resource.buffer = new byte[read];
        System.arraycopy(gzipInputBuffer, 0, resource.buffer, 0, read);

        return resource;
    }

    public int getMapIdForRegions(int landscapeOrObject, int regionY, int regionX) {
        //This code variable initializer and for loop were commented out, why?
        //Start of originally commented out code uncommented for OSRS data 190
        int code = (regionX << 8) + regionY;
        for (int area = 0; area < areas.length; area++) {
            if (areas[area] == code) {
                if (landscapeOrObject == 0) {
                    return mapFiles[area] > 9999 ? -1 : mapFiles[area];
                } else {
                    return landscapes[area] > 9999 ? -1 : landscapes[area];
                }
            }
        }
        return -1;
    }

    public void requestExtra(byte priority, int type, int file) {
        if (clientInstance.indices[0] == null)
            return;
        //if (versions[type][file] == 0)
        //    return;
        byte[] data = clientInstance.indices[type + 1].decompress(file);
        if (crcMatches(crcs[type][file], data))
            return;
        fileStatus[type][file] = priority;
        if (priority > maximumPriority)
            maximumPriority = priority;
        totalFiles++;
    }

    public boolean landscapePresent(int landscape) {
        for (int index = 0; index < areas.length; index++)
            if (landscapes[index] == landscape)
                return true;
        return false;
    }

    private void requestMandatory() {
        uncompletedCount = 0;
        completedCount = 0;
        for (Resource resource = (Resource) requested.first(); resource != null; resource = (Resource) requested.next())
            if (resource.incomplete) {
                uncompletedCount++;
                if (!ClientConstants.JAGCACHED_ENABLED) {
                    System.err.println("Error: model is incomplete or missing  [ type = " + resource.dataType + "]  [id = " + resource.ID + "]");
                }
            } else
                completedCount++;

        while (uncompletedCount < 10) { // 10
            Resource request = (Resource) unrequested.pop();
            if (request == null) {
                break;
            }
            try {
                //System.out.println("Request.dataType" + request.dataType);
                if (fileStatus[request.dataType][request.ID] != 0) {
                    filesLoaded++;
                }
                fileStatus[request.dataType][request.ID] = 0;
                requested.insertBack(request);
                uncompletedCount++;
                request(request);
                expectingData = true;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public void clearExtras() {
        synchronized (extras) {
            extras.clear();
        }
    }

    private void loadMandatory() {
        Resource resource;
        synchronized (mandatoryRequests) {
            resource = (Resource) mandatoryRequests.pop();
        }
        while (resource != null) {
            expectingData = true;
            byte data[] = null;

            if (clientInstance.indices[0] != null)
                data = clientInstance.indices[resource.dataType + 1].decompress(resource.ID);

            //CRC MATCHING
            if (ClientConstants.JAGCACHED_ENABLED) {
                if (!crcMatches(crcs[resource.dataType][resource.ID], data)) {
                    data = null;
                }
            }

            synchronized (mandatoryRequests) {
                if (data == null) {
                    unrequested.insertBack(resource);
                } else {
                    resource.buffer = data;
                    synchronized (complete) {
                        complete.insertBack(resource);
                    }
                }
                resource = (Resource) mandatoryRequests.pop();
            }
        }
    }


    private void passive_request() {
        while (uncompletedCount == 0 && completedCount < 10) {
            if (maximumPriority == 0)
                break;
            Resource resource;
            synchronized (extras) {
                resource = (Resource) extras.pop();
            }
            while (resource != null) {
                if (fileStatus[resource.dataType][resource.ID] != 0) {
                    fileStatus[resource.dataType][resource.ID] = 0;
                    requested.insertBack(resource);
                    request(resource);
                    expectingData = true;
                    if (filesLoaded < totalFiles)
                        filesLoaded++;
                    loadingMessage = "Loading extra files - " + (filesLoaded * 100) / totalFiles + "%";
                    completedCount++;
                    if (completedCount == 10)
                        return;
                }
                synchronized (extras) {
                    resource = (Resource) extras.pop();
                }
            }
            for (int type = 0; type < 4; type++) {
                byte data[] = fileStatus[type];
                int size = data.length;
                for (int file = 0; file < size; file++)
                    if (data[file] == maximumPriority) {
                        data[file] = 0;
                        Resource newResource = new Resource();
                        newResource.dataType = type;
                        newResource.ID = file;
                        newResource.incomplete = false;
                        requested.insertBack(newResource);
                        request(newResource);
                        expectingData = true;
                        if (filesLoaded < totalFiles)
                            filesLoaded++;
                        loadingMessage = "Loading extra files - " + (filesLoaded * 100) / totalFiles + "%";
                        completedCount++;
                        if (completedCount == 10)
                            return;
                    }
            }
            maximumPriority--;
        }
    }

    public boolean highPriorityMusic(int file) {
        return musicPriorities[file] == 1;
    }

    /**
     * Grabs the checksum of a file from the cache.
     *
     * @param type The type of file (0 = model, 1 = anim, 2 = midi, 3 = map).
     * @param id   The id of the file.
     * @return
     */
    private boolean crcMatches(int expectedValue, byte crcData[]) {
        if (crcData == null || crcData.length < 2)
            return false;
        int length = crcData.length - 2;
        crc32.reset();
        crc32.update(crcData, 0, length);
        int crcValue = (int) crc32.getValue();
        return crcValue == expectedValue;
    }

    public void writeAll() {
        for (int i = 0; i < crcs.length; i++) {
            writeChecksumList(i);
            writeVersionList(i);
        }
    }

    public int getChecksum(int type, int id) {
        int crc = -1;
        byte[] data = clientInstance.indices[type + 1].decompress(id);
        if (data != null) {
            int length = data.length - 2;
            crc32.reset();
            crc32.update(data, 0, length);
            crc = (int) crc32.getValue();
        }
        return crc;
    }

    public int getVersion(int type, int id) {
        int version = -1;
        byte[] data = clientInstance.indices[type + 1].decompress(id);
        if (data != null) {
            int length = data.length - 2;
            version = ((data[length] & 0xff) << 8) + (data[length + 1] & 0xff);
        }
        return version;
    }

    public void writeChecksumList(int type) {
        try {
            DataOutputStream out = new DataOutputStream(new FileOutputStream(SignLink.findCacheDir() + type + "_crc.dat"));
            int total = 0;
            for (int index = 0; index < clientInstance.indices[type + 1].getFileCount(); index++) {
                out.writeInt(getChecksum(type, index));
                total++;
            }
            System.out.println(type + "-" + total);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void writeVersionList(int type) {
        try {
            DataOutputStream out = new DataOutputStream(new FileOutputStream(SignLink.findCacheDir() + type + "_version.dat"));
            for (int index = 0; index < clientInstance.indices[type + 1].getFileCount(); index++) {
                out.writeShort(getVersion(type, index));
            }
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
