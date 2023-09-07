package io.github.goldeneas.cosmicmining.utils;

import org.bukkit.Material;

public class StringUtils {

    public static String fixMaterialName(Material material) {
        StringBuilder sb = new StringBuilder("&r");

        String[] base = material.toString().toLowerCase().split("_");
        for (String s : base) {
            char[] word = s.toCharArray();
            sb.append(Character.toUpperCase(word[0]));
            sb.append(s.substring(1));
            sb.append(" ");
        }

        return sb.toString();
    }

}
