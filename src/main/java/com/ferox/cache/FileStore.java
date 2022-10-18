package com.ferox.cache;

import java.io.*;

public final class FileStore {

    public static enum Store {

        ARCHIVE(0),

        MODEL(1),

        ANIMATION(2),

        MUSIC(3),

        MAP(4);

        private int index;

        private Store(int index) {
            this.index = index;
        }

        public int getIndex() {
            return this.index;
        }

    }

    private static final byte[] buffer = new byte[520];
    private final RandomAccessFile dataFile;
    private final RandomAccessFile indexFile;
    private final int storeIndex;

    public FileStore(RandomAccessFile data, RandomAccessFile index, int storeIndex) {
        this.storeIndex = storeIndex;
        dataFile = data;
        indexFile = index;
    }

    public synchronized byte[] decompress(int id) {
        try {
            seek(indexFile, id * 6);
            for (int in = 0, read = 0; read < 6; read += in) {
                in = indexFile.read(buffer, read, 6 - read);

                if (in == -1) {
                    return null;
                }

            }

            int size = ((buffer[0] & 0xff) << 16) + ((buffer[1] & 0xff) << 8) + (buffer[2] & 0xff);
            int sector = ((buffer[3] & 0xff) << 16) + ((buffer[4] & 0xff) << 8) + (buffer[5] & 0xff);

            if (sector <= 0 || (long) sector > dataFile.length() / 520L) {
                return null;
            }

            byte[] buf = new byte[size];

            int totalRead = 0;

            int chunkLength = id <= 0xffff ? 512 : 510;
            int headerLength = id <= 0xffff ? 8 : 10;

            for (int part = 0; totalRead < size; part++) {

                if (sector == 0) {
                    return null;
                }

                seek(dataFile, sector * 520);

                int unread = size - totalRead;

                if (unread > chunkLength) {
                    unread = chunkLength;
                }

                for (int in = 0, read = 0; read < unread + headerLength; read += in) {
                    in = dataFile.read(buffer, read, (unread + headerLength) - read);

                    if (in == -1) {
                        return null;
                    }
                }

                int currentIndex;
                int currentPart;
                int nextSector;
                int currentFile;

                if(id <= 0xffff) {
                    currentIndex = ((buffer[0] & 0xff) << 8) + (buffer[1] & 0xff);//Short
                    currentPart = ((buffer[2] & 0xff) << 8) + (buffer[3] & 0xff);//Short
                    nextSector = ((buffer[4] & 0xff) << 16) + ((buffer[5] & 0xff) << 8) + (buffer[6] & 0xff);//Medium
                    currentFile = buffer[7] & 0xff;//Byte
                } else {
                    currentIndex = ((buffer[0] & 0xff) << 24) + ((buffer[1] & 0xff) << 16) + ((buffer[2] & 0xff) << 8) + (buffer[3] & 0xff);//Int
                    currentPart = ((buffer[4] & 0xff) << 8) + (buffer[5] & 0xff);//Short
                    nextSector = ((buffer[6] & 0xff) << 16) + ((buffer[7] & 0xff) << 8) + (buffer[8] & 0xff);//Medium
                    currentFile = buffer[9] & 0xff;//Byte
                }

                if (currentIndex != id || currentPart != part || currentFile != storeIndex) {
                    return null;
                }

                if (nextSector < 0 || (long) nextSector > dataFile.length() / 520L) {
                    return null;
                }

                for (int i = 0; i < unread; i++) {
                    buf[totalRead++] = buffer[i + headerLength];
                }

                sector = nextSector;
            }

            return buf;
        } catch (IOException _ex) {
            return null;
        }
    }

    public synchronized boolean writeFile(int length, byte[] data, int index) {
        return writeFile(data, index, length, true) || writeFile(data, index, length, false);
    }

    private synchronized boolean writeFile(byte[] bytes, int position, int length, boolean exists) {
        try {
            int sector;
            if (exists) {

                seek(indexFile, position * 6);

                for (int in = 0, read = 0; read < 6; read += in) {
                    in = indexFile.read(buffer, read, 6 - read);

                    if (in == -1) {
                        return false;
                    }

                }
                sector = ((buffer[3] & 0xff) << 16) + ((buffer[4] & 0xff) << 8) + (buffer[5] & 0xff);

                if (sector <= 0 || (long) sector > dataFile.length() / 520L) {
                    return false;
                }

            } else {
                sector = (int) ((dataFile.length() + 519L) / 520L);
                if (sector == 0) {
                    sector = 1;
                }
            }
            buffer[0] = (byte) (length >> 16);
            buffer[1] = (byte) (length >> 8);
            buffer[2] = (byte) length;
            buffer[3] = (byte) (sector >> 16);
            buffer[4] = (byte) (sector >> 8);
            buffer[5] = (byte) sector;
            seek(indexFile, position * 6);
            indexFile.write(buffer, 0, 6);

            int chunkLength = position <= 0xffff ? 512 : 510;
            int headerLength = position <= 0xffff ? 8 : 10;

            for (int part = 0, written = 0; written < length; part++) {

                int nextSector = 0;

                if (exists) {
                    seek(dataFile, sector * 520);

                    int read = 0;

                    for (int in = 0; read < headerLength; read += in) {

                        in = dataFile.read(buffer, read, headerLength - read);

                        if (in == -1) {
                            break;
                        }
                    }

                    if (read == headerLength) {
                        int currentIndex;
                        int currentPart;
                        int currentFile;

                        if(position <= 0xffff) {
                            currentIndex = ((buffer[0] & 0xff) << 8) + (buffer[1] & 0xff);//Short
                            currentPart = ((buffer[2] & 0xff) << 8) + (buffer[3] & 0xff);//Short
                            nextSector = ((buffer[4] & 0xff) << 16) + ((buffer[5] & 0xff) << 8) + (buffer[6] & 0xff);//Medium
                            currentFile = buffer[7] & 0xff;//Byte
                        } else {
                            currentIndex = ((buffer[0] & 0xff) << 24) + ((buffer[1] & 0xff) << 16) + ((buffer[2] & 0xff) << 8) + (buffer[3] & 0xff);//Int
                            currentPart = ((buffer[4] & 0xff) << 8) + (buffer[5] & 0xff);//Short
                            nextSector = ((buffer[6] & 0xff) << 16) + ((buffer[7] & 0xff) << 8) + (buffer[8] & 0xff);//Medium
                            currentFile = buffer[9] & 0xff;//Byte
                        }

                        if (currentIndex != position || currentPart != part || currentFile != storeIndex) {
                            return false;
                        }

                        if (nextSector < 0 || (long) nextSector > dataFile.length() / 520L) {
                            return false;
                        }
                    }
                }
                if (nextSector == 0) {
                    exists = false;
                    nextSector = (int) ((dataFile.length() + 519L) / 520L);

                    if (nextSector == 0) {
                        nextSector++;
                    }

                    if (nextSector == sector) {
                        nextSector++;
                    }

                }

                if (length - written <= chunkLength) {
                    nextSector = 0;
                }

                if(position <= 0xffff) {
                    buffer[0] = (byte) (position >> 8);//Short
                    buffer[1] = (byte) position;
                    buffer[2] = (byte) (part >> 8);//Short
                    buffer[3] = (byte) part;
                    buffer[4] = (byte) (nextSector >> 16);//Medium
                    buffer[5] = (byte) (nextSector >> 8);
                    buffer[6] = (byte) nextSector;
                    buffer[7] = (byte) storeIndex;//Byte
                } else {
                    buffer[0] = (byte) (position >> 24);//Int
                    buffer[1] = (byte) (position >> 16);
                    buffer[2] = (byte) (position >> 8);
                    buffer[3] = (byte) position;
                    buffer[4] = (byte) (part >> 8);//Short
                    buffer[5] = (byte) part;
                    buffer[6] = (byte) (nextSector >> 16);//Medium
                    buffer[7] = (byte) (nextSector >> 8);
                    buffer[8] = (byte) nextSector;
                    buffer[9] = (byte) storeIndex;//Byte
                }
                seek(dataFile, sector * 520);
                dataFile.write(buffer, 0, headerLength);

                int unwritten = length - written;

                if (unwritten > chunkLength) {
                    unwritten = chunkLength;
                }

                dataFile.write(bytes, written, unwritten);
                written += unwritten;
                sector = nextSector;
            }

            return true;
        } catch (IOException ex) {
            return false;
        }
    }

    private synchronized void seek(RandomAccessFile file, int position) throws IOException {
        try {
            file.seek(position);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the number of files in the cache index.
     * @return
     */
    public long getFileCount() {
        try {
            if (indexFile != null) {
                return (indexFile.length() / 6);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }
}
