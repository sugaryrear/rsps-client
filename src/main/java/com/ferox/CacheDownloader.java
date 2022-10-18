package com.ferox;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import javax.swing.JOptionPane;

import com.ferox.sign.SignLink;
import com.ferox.util.Unzip;

public class CacheDownloader {

    private static final String CACHE_DOWNLOAD_LINK = "https://dl.dropboxusercontent.com/s/ibsi16b4syvgtd2/.Ferox.zip";
    private static final String CACHE_VERSION_DOWNLOAD_LINK = "https://dl.dropboxusercontent.com/s/y5j0f8fkqu2yfgo/version.txt";
    private static final String CACHE_NAME = ".Ferox";

    public static void init(boolean force) {
        String current = getCurrentCacheVersion();
        String latest = getLatestCacheVersion();
        if (!latest.equals(current) || force) {
            try {

                /*
                 * Download latest cache
                 */
                download();

                /*
                 * Make the directories for the cache
                 */
                File folder = new File(SignLink.findCacheDir() + CACHE_NAME);
                if (!folder.exists()) {
                    folder.mkdir();
                }
                File folderOSRSData = new File(SignLink.findCacheDir() + CACHE_NAME + "./data");
                if (!folderOSRSData.exists()) {
                    folderOSRSData.mkdir();
                }
                File folderOsrs = new File(SignLink.findCacheDir() + CACHE_NAME + "./data/osrs");
                if (!folderOsrs.exists()) {
                    folderOsrs.mkdir();
                }
                /*
                 * Unzip the downloaded cache file
                 */
                Unzip.unZipIt(SignLink.findCacheDir() + CACHE_NAME, SignLink.findCacheDir(), true);

                /*
                 * Write new version
                 */
                File version = new File(SignLink.findDataDir() + "cacheversion.txt");
                try (FileWriter f = new FileWriter(version)) {
                    f.write("" + latest + "");
                }

            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Cache could not be downloaded.\nPlease try again later.");
            }
        }
    }

    private static void download() {
        try {
            URL url = new URL(CACHE_DOWNLOAD_LINK);
            URLConnection conn = url.openConnection();
            BufferedInputStream in = new BufferedInputStream(conn.getInputStream());
            FileOutputStream out = new FileOutputStream(SignLink.findCacheDir() + File.separator + CACHE_NAME);

            int length;
            long downloaded = 0;
            int total = conn.getContentLength();
            byte[] bytes = new byte[1024];
            long startTime = System.currentTimeMillis();
            while ((length = in.read(bytes)) != -1) {
                out.write(bytes, 0, length);
                downloaded += length;
                int percentage = (int) ((downloaded / (double) total * 100));
                int downloadSpeed = (int) ((downloaded / 1024) / (1 + ((System.currentTimeMillis() - startTime) / 1000)));
                Client.singleton.draw_loadup(percentage, "Downloading cache " + percentage + "% @ " + downloadSpeed + "Kb/s");
            }

            in.close();
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
            Client.addReportToServer("Cache download error:");
            Client.addReportToServer("" + e.getMessage());
        }
    }

    private static String getCurrentCacheVersion() {
        String version = "";
        try {
            File file = new File(SignLink.findDataDir() + "cacheversion.txt");
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            version = br.readLine();
            br.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            Client.addReportToServer(ex.getMessage());
        }
        return version;
    }

    private static String getLatestCacheVersion() {
        String version = "";
        try {
            URL url = new URL(CACHE_VERSION_DOWNLOAD_LINK);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.addRequestProperty("User-Agent", "Mozilla/4.76");
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            version = br.readLine();
            br.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            Client.addReportToServer(ex.getMessage());
        }
        return version;
    }

}
