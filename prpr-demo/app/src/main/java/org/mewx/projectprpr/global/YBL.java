package org.mewx.projectprpr.global;

import android.content.ContentValues;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import org.mewx.projectprpr.plugin.component.PluginInfo;
import org.mewx.projectprpr.reader.setting.ReaderSaveBasic;
import org.mewx.projectprpr.toolkit.FigureTool;
import org.mewx.projectprpr.toolkit.FileTool;
import org.w3c.dom.Text;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import okhttp3.OkHttpClient;

/**
 * This class stores all the global values.
 * Static for all classes to call.
 */
@SuppressWarnings("unused")
public class YBL {
    // enumerables
    public enum VERSION_TYPE_ENUM {
        TEST,
        PUBLISH
    }

    public enum SettingItemsBasic {
        version, // (int) 1
        language,
        menu_bg_id, // (int) 1-5 by system, 0 for user
        menu_bg_path, // (String) for user custom
        reader_font_path, // (String) path to ttf, "0" means default
        reader_font_size, // (int) sp (8 - 32)
        reader_line_distance, // (int) dp (0 - 32)
        reader_paragraph_distance, // (int) dp (0 - 48)
        reader_paragraph_edge_distance, // (int) dp (0 - 16)
        reader_background_path, // (String) path to an image, day mode only, "0" means default
    }


    // global constant strings
    public static final String PLUGIN_PACKAGE = "org.mewx.projectprpr.plugin.builtin";
    public static final String FOLDER_NAME_CACHE = "cache";
    public static final String FOLDER_NAME_DOWNLOAD = "downloads";
    public static final String FOLDER_NAME_IMAGE = "imgs";
    public static final String FOLDER_NAME_IMAGE_READER = "imgrd";
    public static final String FOLDER_NAME_NETNOVEL = "netnovel";
    public static final String PROJECT_FOLDER = "prpr";
    public static final String PROJECT_FOLDER_CACHE = PROJECT_FOLDER + File.separator + FOLDER_NAME_CACHE;
    public static final String PROJECT_FOLDER_DOWNLOAD = PROJECT_FOLDER + File.separator + FOLDER_NAME_DOWNLOAD;
    public static final String PROJECT_FOLDER_READER_IMAGES = PROJECT_FOLDER + File.separator + FOLDER_NAME_IMAGE_READER;
    private static final String PROJECT_FOLDER_NETNOVEL = PROJECT_FOLDER_DOWNLOAD + File.separator + FOLDER_NAME_NETNOVEL;
    public static final String FILE_NAME_READER_SETTINGS = "reader_settings.prpr";
    public static final String FILE_NAME_READER_SAVES = "reader_saves.prpr";
    public static final String PROJECT_FILE_READER_SETTINGS = PROJECT_FOLDER + File.separator + FILE_NAME_READER_SETTINGS;

    public static final String STANDARD_CHARSET = "UTF-8";
    public static final String STANDARD_IMAGE_FORMAT = "jpg";
    public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2486.0 Safari/537.36 Edge/14.14295 ProjectPRPR/1.00";

    public static String getStoragePath(String folder) {
        // TODO: set external storage or internal storage
        return Environment.getExternalStorageDirectory() + File.separator + folder;
    }

    public static String generateImageFileFullPathByURL(String url, String extensionName) {
        final char[] HEX_DIGITS = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException expected) {
            expected.printStackTrace();
            return getStoragePath(PROJECT_FOLDER_READER_IMAGES + File.separator + "DEFAULT." + extensionName);
        }
        messageDigest.update(url.getBytes());

        // convert to
        byte[] hash = messageDigest.digest();
        StringBuilder buf = new StringBuilder(hash.length * 2);
        for (int j = 0; j < hash.length; j++) {
            buf.append(HEX_DIGITS[(hash[j] << 4) & 0x0f]);
            buf.append(HEX_DIGITS[hash[j] & 0x0f]);
        }
        Log.e("ImageName", buf.toString());
        return getStoragePath(PROJECT_FOLDER_READER_IMAGES + File.separator + buf.toString() + "." + extensionName);
    }

    public static String getFileFullPath(String folder, String fileName) {
        // still do a check procedure
        return folder.charAt(folder.length() - 1) == File.separatorChar ?
                folder + fileName : folder + File.separator + fileName;
    }

    public static String getProjectFolderNetNovel(String dataSourceTag) {
        // get folder path, without back-leading separator.
        return PROJECT_FOLDER_NETNOVEL + File.separator + dataSourceTag;
    }


    // global constants
    public static final VERSION_TYPE_ENUM VERSION_TYPE = VERSION_TYPE_ENUM.TEST;
    public static final int IMAGE_CACHE_DISK_SIZE = 100 * 1024 * 1024;
    public static final PluginInfo[] BUILTIN_PLUGIN = {new PluginInfo("XsDmzj", PluginInfo.PluginType.BUILTIN, "")};


    // global common variables
    public static OkHttpClient globalOkHttpClient3;
    private static ContentValues allSetting;
    private static ArrayList<ReaderSaveBasic> readSaves = null; // deprecated
//    private static NovelDataSourceBasic novelDataSourceBasic;
    private static boolean skipSplashScreen = false;
    public static String pathPickedSave; // dir picker save path, for temporary
    public static boolean getSkipSplashScreen() {
        return skipSplashScreen;
    }
    public static void setSkipSplashScreen(boolean skip) {
        skipSplashScreen = skip;
    }

//    public static void setNovelDataSourceBasic(NovelDataSourceBasic ds) {
//        novelDataSourceBasic = ds;
//    }
//
//    public static NovelDataSourceBasic getNovelDataSourceBasic() {
//        return novelDataSourceBasic;
//    }
//
//    public static isNovelDataSourceBasicSaved


    /* All settings */
    public static void loadAllSetting() {
        allSetting = new ContentValues();
        String h = FileTool.loadFullFileContent(getStoragePath(PROJECT_FILE_READER_SETTINGS));
        String[] sets = h.split("\\|\\|\\|\\|");
        for(String set : sets) {
            String[] temp = set.split("::::");
            if(temp.length != 2 || temp[0] == null || temp[0].length() == 0 || temp[1] == null || temp[1].length() == 0) continue;

            allSetting.put(temp[0], temp[1]);
        }

        if(TextUtils.isEmpty(getFromAllSetting(SettingItemsBasic.version)))
            setToAllSetting(SettingItemsBasic.version, "1");
    }

    public static void saveAllSetting() {
        if(allSetting == null) loadAllSetting();
        String result = "";
        for( String key : allSetting.keySet() ) {
            if(!result.equals("")) result = result + "||||";
            result = result + key + "::::" + allSetting.getAsString(key);
        }

        FileTool.writeFullFileContent(getStoragePath(PROJECT_FILE_READER_SETTINGS), result);
    }

    @Nullable
    public static String getFromAllSetting(SettingItemsBasic name) {
        if(allSetting == null) loadAllSetting();
        return allSetting.getAsString(name.toString());
    }

    public static void setToAllSetting(SettingItemsBasic name, String value) {
        if(allSetting == null) loadAllSetting();
        if(name != null && value != null) {
            allSetting.remove(name.toString());
            allSetting.put(name.toString(), value);
            saveAllSetting();
        }
    }


    /** Read Saves (V1) */
    public static void loadReaderSaveBasic() {
        // Format:
        // cid,,pos,,height||cid,,pos,,height
        // just use split function
        readSaves = new ArrayList<>();

        // read history from file, if not exist, create.
        if(!FileTool.existFile(getStoragePath(PROJECT_FOLDER + File.separator + FILE_NAME_READER_SAVES))) {
            FileTool.saveFile(getStoragePath(PROJECT_FOLDER + File.separator + FILE_NAME_READER_SAVES), "", true);
        }
        String h = FileTool.loadFullFileContent(getStoragePath(PROJECT_FOLDER + File.separator + FILE_NAME_READER_SAVES));

        // split string h
        String[] p = h.split("\\|\\|"); // regular expression
        for (String temp : p) {
            Log.v("MewX", temp);
            String[] parts = temp.split(":"); // \\:
            if (parts.length != 5)
                continue;

            // add to list
            ReaderSaveBasic rs = new ReaderSaveBasic();
            rs.aid = parts[0];
            rs.vid = parts[1];
            rs.cid = parts[2];
            try {
                rs.lineId = Integer.valueOf(parts[3]);
                rs.wordId = Integer.valueOf(parts[4]);
            } catch (Exception expected) {
                expected.printStackTrace();
                continue;
            }
            readSaves.add(rs);
        }
    }

    public static void writeReaderSaveBasic() {
        if (readSaves == null)
            loadReaderSaveBasic();

        String t = "";
        for (int i = 0; i < readSaves.size(); i++) {
            if (i != 0)
                t += "||";
            t += readSaves.get(i).aid + ":" + readSaves.get(i).vid + ":" + readSaves.get(i).cid + ":"
                    + readSaves.get(i).lineId + ":" + readSaves.get(i).wordId;
        }
        FileTool.saveFile(getStoragePath(PROJECT_FOLDER + File.separator + FILE_NAME_READER_SAVES), t, true);
    }

    public static void addReadSavesRecordV1(String aid, String vid, String cid, int lineId, int wordId) {
        if (readSaves == null)
            loadReaderSaveBasic();

        // judge if exist, and if legal, update it
        for (int i = 0; i < readSaves.size(); i ++) {
            if (readSaves.get(i).aid.equals(aid)) {
                // need to update
                readSaves.get(i).vid = vid;
                readSaves.get(i).cid = cid;
                readSaves.get(i).lineId = lineId;
                readSaves.get(i).wordId = wordId;
                writeReaderSaveBasic();
                return;
            }
        }

        // new record
        ReaderSaveBasic rs = new ReaderSaveBasic();
        rs.aid = aid;
        rs.vid = vid;
        rs.cid = cid;
        rs.lineId = lineId;
        rs.wordId = wordId;
        readSaves.add(rs);
        writeReaderSaveBasic();
    }

    public static void removeReadSavesRecordV1(String aid) {
        if (readSaves == null)
            loadReaderSaveBasic();

        int i = 0;
        for( ; i < readSaves.size(); i ++) {
            if(readSaves.get(i).aid.equals(aid)) break;
        }
        if(i < readSaves.size()) readSaves.remove(i);
        writeReaderSaveBasic();
    }

    @Nullable
    public static ReaderSaveBasic getReadSavesRecordV1(String aid) {
        if (readSaves == null)
            loadReaderSaveBasic();

        for (int i = 0; i < readSaves.size(); i ++) {
            if (readSaves.get(i).aid.equals(aid)) return readSaves.get(i);
        }
        return null;
    }
}
