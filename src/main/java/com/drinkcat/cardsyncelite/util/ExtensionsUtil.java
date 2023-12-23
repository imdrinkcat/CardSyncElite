package com.drinkcat.cardsyncelite.util;

import java.util.List;

public class ExtensionsUtil {
    public static final ExtensionsUtil photoExtensions =
            new ExtensionsUtil("图像文件: ", List.of(
                    ".jpg", ".jpeg", ".png", ".gif", ".bmp", ".tiff", ".webp",
                    ".raw", ".arw", ".cr2", ".nef", ".dng", ".orf", ".raf", ".sr2",
                    ".ai", ".eps", ".svg",
                    ".ico", ".icns", ".tif", ".tga"
            ));

    public static final ExtensionsUtil videoExtensions =
            new ExtensionsUtil("视频文件: ", List.of(
                    ".mp4", ".mkv", ".avi", ".mov", ".wmv", ".flv", ".webm",
                    ".m4v", ".mpg", ".mpeg", ".3gp", ".asf", ".rm", ".swf", ".vob",
                    ".m2ts", ".ts", ".divx", ".rmvb", ".ogv", ".ogg", ".mxf", ".mts"
            ));

    public static final ExtensionsUtil allExtensions =
            new ExtensionsUtil("全部文件: ", List.of(
                    ".*"
            ));
    public ExtensionsUtil() {
    }

    public ExtensionsUtil(String description, List<String> extensions) {
        this.description = description;
        this.extensions = extensions;
    }

    @Override
    public String toString() {
        return description + extensions;
    }


    public String description;
    public List<String> extensions;
}
