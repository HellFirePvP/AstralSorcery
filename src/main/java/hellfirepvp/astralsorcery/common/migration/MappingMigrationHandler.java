/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.migration;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.datafix.FixTypes;
import net.minecraft.util.datafix.IFixType;
import net.minecraft.util.datafix.IFixableData;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ModFixs;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.LinkedList;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: MappingMigrationHandler
 * Created by HellFirePvP
 * Date: 03.07.2017 / 12:50
 */
public class MappingMigrationHandler {

    //this is not generified/abstracted yet due to lazyness and it's just 1 thing to migrate so........

    private static final int DATA_FIXER_VERSION = 1;
    private static final ResourceLocation ILLUMINATION_POWDER_KEY = new ResourceLocation(AstralSorcery.MODID, "itemilluminationpowder");

    private static LinkedList<String> migrationTileNames = new LinkedList<>();

    public static void init() {
        MappingMigrationHandler instance = new MappingMigrationHandler();

        MinecraftForge.EVENT_BUS.register(instance);

        ModFixs fixes = FMLCommonHandler.instance().getDataFixer().init(AstralSorcery.MODID, DATA_FIXER_VERSION);
        fixes.registerFix(FixTypes.BLOCK_ENTITY, new IFixableData() {
            @Override
            public int getFixVersion() {
                return 1;
            }

            @Override
            public NBTTagCompound fixTagCompound(NBTTagCompound compound) {
                ResourceLocation tileId = new ResourceLocation(compound.getString("id"));

                if ("minecraft".equals(tileId.getResourceDomain())) {
                    if (migrationTileNames.contains(tileId.getResourcePath())) {
                        compound.setString("id", new ResourceLocation(AstralSorcery.MODID, tileId.getResourcePath()).toString());
                    }
                }

                return compound;
            }
        });
    }

    @SubscribeEvent
    public void onMissingMapping(RegistryEvent.MissingMappings<Item> event) {
        for (RegistryEvent.MissingMappings.Mapping<Item> mapping : event.getMappings()) {
            if(mapping.key.equals(ILLUMINATION_POWDER_KEY)) {
                mapping.remap(ItemsAS.useableDust);
            }
        }
    }

    public static void listenTileMigration(String name) {
        migrationTileNames.add(name);
    }
}
