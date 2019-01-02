package com.wanfajie.microblog.util;

import java.io.File;
import java.util.UUID;

public class FileUtil {

    private FileUtil() {
        throw new UnsupportedOperationException();
    }

    public static String getPwd() {
        return System.getenv("PWD");
    }

    public static boolean isAbsPath(String path) {
        File file = new File(path);
        return file.isAbsolute();
    }

    public static String splitStringToPath(String str) {
        return splitString(str, File.separator, 2, 2);
    }

    public static String uuidPath() {
        return uuidPath(UUID.randomUUID(),2, 2);
    }

    public static String uuidPath(UUID uuid, int charCount, int deep) {
        return splitString(uuid.toString(), File.separator, charCount, deep);
    }

    public static String splitString(String uuid, String sep, int charCount, int deep) {
        uuid = uuid.replace("-", "").toLowerCase();
        int len = uuid.length(), i;
        StringBuilder builder = new StringBuilder();

        for (i = 0; i < deep * charCount && i < len - 2; i+=charCount) {
            String part = uuid.substring(i, i + charCount);
            builder.append(part);
            builder.append(sep);
        }

        String lastPart = uuid.substring(i);
        if (lastPart.isEmpty()) {
            builder.deleteCharAt(builder.length() - 1);
        } else {
            builder.append(lastPart);
        }

        return builder.toString();
    }

    public static boolean mkdirs(File file) {
        File parent = file.getParentFile();

        return !parent.exists() && parent.mkdirs();
    }

    public static boolean mkdirs(String fullPath) {
        File file = new File(fullPath);

        return mkdirs(file);
    }

    public static String[] splitFileName(String fileName) {
        int dotIdx = fileName.indexOf(".");
        if (dotIdx ==  -1) return null;

        String name = fileName.substring(0, dotIdx);
        String suffix = fileName.substring(dotIdx+1);

        return new String[] {name, suffix};
    }
}
