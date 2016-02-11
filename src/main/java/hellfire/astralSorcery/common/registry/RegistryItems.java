package hellfire.astralSorcery.common.registry;

import hellfire.astralSorcery.common.AstralSorcery;
import hellfire.astralSorcery.common.items.ItemConstellationPaper;
import hellfire.astralSorcery.common.lib.LibConstants;
import hellfire.astralSorcery.common.lib.LibMisc;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.LoadController;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * This class is part of the Astral Sorcery Mod
 * <p/>
 * Created by HellFirePvP @ 24.01.2016 21:15
 */
public class RegistryItems {

    public static ItemConstellationPaper constellationPaper;

    public static void init() {
        registerItems();
    }

    private static void registerItems() {
        constellationPaper = registerItem(new ItemConstellationPaper());
    }

    private static <T extends Item> T registerItem(T item, String name) {
        GameRegistry.registerItem(item, name);
        return item;
    }

    private static <T extends Item> T registerItem(T item) {
        return registerItem(item, item.getClass().getSimpleName());
    }

    private static <T extends Item> T registerItem(String modId, T item) {
        return registerItem(modId, item, item.getClass().getSimpleName());
    }

    private static <T extends Item> T registerItem(String modId, T item, String name) {
        try {
            LoadController modController = (LoadController) Loader.class.getField("modController").get(Loader.instance());
            Object oldMod = modController.getClass().getField("activeContainer").get(modController);
            modController.getClass().getField("activeContainer").set(modController, Loader.instance().getIndexedModList().get(modId));

            GameRegistry.registerItem(item, name);

            modController.getClass().getField("activeContainer").set(modController, oldMod);
            return item;
        } catch (Exception exc) {
            AstralSorcery.log.error("Could not register item with name " + name);
            return null;
        }
    }

}
