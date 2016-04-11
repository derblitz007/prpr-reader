package org.mewx.projectprpr.global;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.io.File;

/**
 * This class stores all the global values.
 * Static for all classes to call.
 */
@SuppressWarnings("unused")
public class YBL {
    // global constant strings
    public static final String PROJECT_FOLDER = "prpr";
    public static final String PROJECT_FOLDER_CACHE = PROJECT_FOLDER + File.separator + "cache";
    public static final String PROJECT_FOLDER_DOWNLOAD = PROJECT_FOLDER + File.separator + "downloads";
    private static final String PROJECT_FOLDER_NETNOVEL = PROJECT_FOLDER_DOWNLOAD + File.separator + "netnovel";

    public static final String STANDARD_CHARSET = "UTF-8";

    public static String getFileFullPath(String folder, String fileName) {
        // still do a check procedure
        return folder.charAt(folder.length() - 1) == File.separatorChar ?
                folder + fileName : folder + File.separator + fileName;
    }

    public static String getProjectFolderNetnovel(String dataSourceTag) {
        // get folder path, without back-leading separator.
        return PROJECT_FOLDER_NETNOVEL + File.separator + dataSourceTag;
    }


    // global common variables
}
