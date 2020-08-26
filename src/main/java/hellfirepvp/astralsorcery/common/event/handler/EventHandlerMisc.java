/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 *  All rights reserved.
 *  The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 *  For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.event.handler;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.GuiType;
import hellfirepvp.astralsorcery.common.constellation.SkyHandler;
import hellfirepvp.astralsorcery.common.constellation.world.WorldContext;
import hellfirepvp.astralsorcery.common.effect.EffectDropModifier;
import hellfirepvp.astralsorcery.common.item.ItemTome;
import hellfirepvp.astralsorcery.common.lib.CapabilitiesAS;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.LecternTileEntity;
import net.minecraft.world.IWorld;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunk;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.eventbus.api.IEventBus;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EventHandlerMisc
 * Created by HellFirePvP
 * Date: 23.02.2020 / 19:30
 */
//Event handler to fix/prevent lots of random stuff
public class EventHandlerMisc {

    public static void attachListeners(IEventBus bus) {
        bus.addListener(EventHandlerMisc::onSpawnEffectCloud);
        bus.addListener(EventHandlerMisc::onPlayerSleepEclipse);
        bus.addListener(EventHandlerMisc::onChunkLoad);
        bus.addListener(EventHandlerMisc::onLecternOpen);
    }

    private static void onLecternOpen(PlayerInteractEvent.RightClickBlock event) {
        if (event.getWorld().isRemote()) {
            return;
        }
        LecternTileEntity lectern = MiscUtils.getTileAt(event.getWorld(), event.getPos(), LecternTileEntity.class, false);
        if (lectern != null) {
            ItemStack contained = lectern.getBook();
            if (contained.getItem() instanceof ItemTome) {
                event.setCanceled(true);
                AstralSorcery.getProxy().openGui(event.getPlayer(), GuiType.TOME);
            }
        }
    }

    private static void onChunkLoad(ChunkEvent.Load event) {
        IChunk ch = event.getChunk();
        if (ch instanceof Chunk && !event.getWorld().isRemote()) {
            ((Chunk) ch).getCapability(CapabilitiesAS.CHUNK_FLUID).ifPresent(entry -> {
                if (!entry.isInitialized()) {
                    IWorld w = event.getWorld();
                    long seed = w.getSeed();
                    long chX = event.getChunk().getPos().x;
                    long chZ = event.getChunk().getPos().z;
                    seed ^= chX << 32;
                    seed ^= chZ;
                    entry.generate(seed);
                    ((Chunk) ch).markDirty();
                }
            });
        }
    }

    private static void onPlayerSleepEclipse(PlayerSleepInBedEvent event) {
        WorldContext ctx = SkyHandler.getContext(event.getEntityLiving().getEntityWorld());
        if (ctx != null && ctx.getCelestialHandler().isSolarEclipseActive()) {
            if (event.getResultStatus() == null) {
                event.setResult(PlayerEntity.SleepResult.NOT_POSSIBLE_NOW);
            }
        }
    }

    private static void onSpawnEffectCloud(EntityJoinWorldEvent event) {
        if (event.getEntity() instanceof AreaEffectCloudEntity &&
                MiscUtils.contains(((AreaEffectCloudEntity) event.getEntity()).effects, effect -> effect.getPotion() instanceof EffectDropModifier)) {
            event.setCanceled(true);
        }
    }
}
