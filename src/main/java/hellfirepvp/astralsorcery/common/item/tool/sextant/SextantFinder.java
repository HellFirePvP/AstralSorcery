/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.tool.sextant;

import com.google.common.collect.Iterables;
import hellfirepvp.astralsorcery.client.util.image.ColorThief;
import hellfirepvp.astralsorcery.client.util.resource.AbstractRenderableTexture;
import hellfirepvp.astralsorcery.client.util.resource.AssetLoader;
import hellfirepvp.astralsorcery.client.util.resource.TextureQuery;
import hellfirepvp.astralsorcery.common.data.world.data.StructureGenBuffer;
import hellfirepvp.astralsorcery.common.util.StructureFinder;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: SextantFinder
 * Created by HellFirePvP
 * Date: 25.02.2018 / 15:50
 */
public class SextantFinder {

    private static boolean initialized = false;
    private static List<TargetObject> selectableTargets = new LinkedList<>();

    public static void register(TargetObject object) {
        for (TargetObject to : selectableTargets) {
            if (to.getRegistryName().equalsIgnoreCase(object.getRegistryName())) {
                throw new IllegalArgumentException("Duplicate Sextant Target: " + to.getRegistryName() + " - tried to add: " + object.getRegistryName());
            }
        }
        selectableTargets.add(object);
    }

    public static void initialize() {
        if(initialized) return;

        register(SextantTargets.TARGET_MOUNTAIN_SHRINE);
        register(SextantTargets.TARGET_DESERT_SHRINE);
        register(SextantTargets.TARGET_SMALL_SHRINE);

        register(SextantTargets.TARGET_VANILLA_VILLAGE);
        register(SextantTargets.TARGET_VANILLA_TEMPLE);
        register(SextantTargets.TARGET_VANILLA_MONUMENT);
        register(SextantTargets.TARGET_VANILLA_FORTRESS);
        register(SextantTargets.TARGET_VANILLA_ENDCITY);

        register(SextantTargets.TARGET_BIOME_PLAINS);
        register(SextantTargets.TARGET_BIOME_DESERT);
        register(SextantTargets.TARGET_BIOME_COLD);
        register(SextantTargets.TARGET_BIOME_FOREST);
        register(SextantTargets.TARGET_BIOME_JUNGLE);
        register(SextantTargets.TARGET_BIOME_MESA);
        register(SextantTargets.TARGET_BIOME_OCEAN);
        register(SextantTargets.TARGET_BIOME_MOUNTAINS);

        MinecraftForge.EVENT_BUS.post(new RegisterEvent());

        initialized = true;
    }

    @Nonnull
    public static TargetObject getOrReturnFirst(String registryName) {
        TargetObject to = getByName(registryName);
        if(to != null) {
            return to;
        }
        return getFirst();
    }

    @Nullable
    public static TargetObject getByName(String registryName) {
        for (TargetObject to : selectableTargets) {
            if (to.getRegistryName().equalsIgnoreCase(registryName)) {
                return to;
            }
        }
        return null;
    }

    @Nonnull
    public static TargetObject getFirst() {
        for (TargetObject to : selectableTargets) {
            if (to instanceof ASStructure && !((ASTargetObject) to).advanced) {
                return to;
            }
        }
        TargetObject first = Iterables.getFirst(selectableTargets, null);
        if (first == null) {
            throw new IllegalStateException("There has to be at least ONE sextant target!");
        }
        return first;
    }

    public static List<TargetObject> getSelectableTargets() {
        return selectableTargets;
    }

    public static class RegisterEvent extends Event {}

    public static abstract class TargetObject {

        public abstract String getRegistryName();

        public abstract boolean isSelectable(ItemStack stack);

        @Nonnull
        @SideOnly(Side.CLIENT)
        public abstract AbstractRenderableTexture getRenderable();

        @SideOnly(Side.CLIENT)
        public abstract int getColorTheme();

        @Nullable
        public abstract BlockPos searchFor(WorldServer world, BlockPos searchPos);

    }

    public static abstract class ASTargetObject extends TargetObject {

        private final TextureQuery query;
        private final boolean advanced;
        private final String name;
        private final int color;

        public ASTargetObject(String iconName, boolean advanced, int color) {
            this.query = new TextureQuery(AssetLoader.TextureLocation.MISC, iconName);
            this.advanced = advanced;
            this.name = iconName;
            this.color = color;
        }

        @Override
        public String getRegistryName() {
            return name;
        }

        @Override
        public boolean isSelectable(ItemStack stack) {
            return !advanced || ItemSextant.isAdvanced(stack);
        }

        @Nonnull
        @Override
        @SideOnly(Side.CLIENT)
        public AbstractRenderableTexture getRenderable() {
            return query.resolve();
        }

        @Override
        public int getColorTheme() {
            return this.color;
        }
    }

    public static class ASStructure extends ASTargetObject {

        private final StructureGenBuffer.StructureType structureType;

        public ASStructure(String iconName, int color, boolean advanced, StructureGenBuffer.StructureType type) {
            super(iconName, advanced, color);
            this.structureType = type;
        }

        @Nullable
        @Override
        public BlockPos searchFor(WorldServer world, BlockPos searchPos) {
            return StructureFinder.tryFindClosestAstralSorceryStructure(world, searchPos, structureType);
        }
    }

    public static class Structure extends ASTargetObject {

        private final String structureName;

        public Structure(String iconName, int color, boolean advanced, String structureName) {
            super(iconName, advanced, color);
            this.structureName = structureName;
        }

        @Nullable
        @Override
        public BlockPos searchFor(WorldServer world, BlockPos searchPos) {
            return StructureFinder.tryFindClosestVanillaStructure(world, searchPos, structureName);
        }
    }

    public static class Biome extends ASTargetObject {

        private final BiomeDictionary.Type biomeType;

        public Biome(String iconName, int color, boolean advanced, BiomeDictionary.Type biomeType) {
            super(iconName, advanced, color);
            this.biomeType = biomeType;
        }

        @Nullable
        @Override
        public BlockPos searchFor(WorldServer world, BlockPos searchPos) {
            return StructureFinder.tryFindClosestBiomeType(world, searchPos, biomeType);
        }

    }
}
