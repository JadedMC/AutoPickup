package net.jadedmc.autopickup.utils;

import org.bukkit.Material;

public class FoodUtils {

    /**
     * It gives the cooked version of the items according to their type.
     * For now, only animal species have been added actively.
     */
    public static Material getCookedVersion(Material rawMaterial) {
        switch (rawMaterial) {
            case BEEF:
                return Material.COOKED_BEEF;
            case CHICKEN:
                return Material.COOKED_CHICKEN;
            case PORKCHOP:
                return Material.COOKED_PORKCHOP;
            case MUTTON:
                return Material.COOKED_MUTTON;
            case RABBIT:
                return Material.COOKED_RABBIT;
            case COD:
                return Material.COOKED_COD;
            case SALMON:
                return Material.COOKED_SALMON;
            default:
                return null;
        }
    }

}
