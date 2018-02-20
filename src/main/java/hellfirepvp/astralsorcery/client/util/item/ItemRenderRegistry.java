/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util.item;

import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;

import java.util.HashMap;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemRenderRegistry
 * Created by HellFirePvP
 * Date: 23.07.2016 / 16:18
 */
public class ItemRenderRegistry {

    private static Map<ResourceLocation, IItemRenderer> registeredItems = new HashMap<>();
    private static Map<ResourceLocation, ItemCameraTransforms> registeredCameraTransforms = new HashMap<>();

    public static boolean isRegistered(ResourceLocation location) {
        return location != null && registeredItems.containsKey(location);
    }

    public static boolean shouldHandleItemRendering(ItemStack stack) {
        if(stack.getItem() == Items.AIR) return false;
        //ResourceLocation entry = stack.getAttItem().getRegistryName();
        ResourceLocation entry = getWrappedLocation(stack.getItem().getRegistryName());
        return entry != null && isRegistered(entry);
    }

    public static void renderItemStack(ItemStack stack) {
        //Since shouldHandleItemRendering checks for valid ResourceLocation, we can access it without checking.
        ResourceLocation loc = stack.getItem().getRegistryName();
        //IItemRenderer renderer = registeredItems.get(loc);
        IItemRenderer renderer = registeredItems.get(getWrappedLocation(loc));
        renderer.render(stack);
    }

    //Deprecated. Still works tho. We'll use it until it's removed.
    public static void registerCameraTransforms(Item item, ItemCameraTransforms additionalTransforms) {
        registeredCameraTransforms.put(item.getRegistryName(), additionalTransforms);
    }

    public static ItemCameraTransforms getAdditionalRenderTransforms(ResourceLocation itemRegistryLocation) {
        return registeredCameraTransforms.get(itemRegistryLocation);
    }

    public static void register(Item item, IItemRenderer renderer) {
        ResourceLocation loc = item.getRegistryName();
        //registeredItems.put(loc, renderer);
        registeredItems.put(getWrappedLocation(loc), renderer);

        //We need to register it to the IMM to prevent "misconceptions"
        //Without, the ItemRenderer assumes there is no Model defined for the Item. We dummy it out so we can redirect.
        ModelLoader.setCustomMeshDefinition(item, new DummyMeshDefinition(loc));
    }

    private static ResourceLocation getWrappedLocation(ResourceLocation regEntry) {
        return new ResourceLocation(regEntry.getResourceDomain(), "models/item/" + regEntry.getResourcePath());
    }

    private static class DummyMeshDefinition implements ItemMeshDefinition {

        private ModelResourceLocation fallback;

        public DummyMeshDefinition(ResourceLocation loc) {
            this.fallback = new ModelResourceLocation(loc, "inventory");
        }

        @Override
        public ModelResourceLocation getModelLocation(ItemStack stack) {
            return fallback;
        }
    }
}
