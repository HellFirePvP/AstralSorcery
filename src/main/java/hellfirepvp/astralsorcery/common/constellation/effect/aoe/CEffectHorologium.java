/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.effect.aoe;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.constellation.IMinorConstellation;
import hellfirepvp.astralsorcery.common.constellation.IWeakConstellation;
import hellfirepvp.astralsorcery.common.constellation.effect.ConstellationEffectProperties;
import hellfirepvp.astralsorcery.common.constellation.effect.base.CEffectAbstractList;
import hellfirepvp.astralsorcery.common.constellation.effect.base.ListEntries;
import hellfirepvp.astralsorcery.common.data.config.registry.TileAccelerationBlacklistRegistry;
import hellfirepvp.astralsorcery.common.lib.ConstellationsAS;
import hellfirepvp.astralsorcery.common.tile.TileRitualPedestal;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.block.BlockPredicate;
import hellfirepvp.astralsorcery.common.util.block.ILocatable;
import hellfirepvp.astralsorcery.common.util.time.TimeStopController;
import hellfirepvp.astralsorcery.common.util.time.TimeStopZone;
import net.minecraft.client.renderer.texture.ITickable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CEffectHorologium
 * Created by HellFirePvP
 * Date: 18.01.2020 / 22:41
 */
public class CEffectHorologium extends CEffectAbstractList<ListEntries.PosEntry> {

    public static HorologiumConfig CONFIG = new HorologiumConfig();

    public CEffectHorologium(@Nonnull ILocatable origin) {
        super(origin, ConstellationsAS.horologium, CONFIG.maxAmount.get(), (world, pos, state) -> TileAccelerationBlacklistRegistry.INSTANCE.canBeAccelerated(world.getTileEntity(pos)));
    }

    @Nullable
    @Override
    public ListEntries.PosEntry recreateElement(CompoundNBT tag, BlockPos pos) {
        return new ListEntries.PosEntry(pos);
    }

    @Nullable
    @Override
    public ListEntries.PosEntry createElement(World world, BlockPos pos) {
        return new ListEntries.PosEntry(pos);
    }

    @Override
    public void playClientEffect(World world, BlockPos pos, TileRitualPedestal pedestal, float alphaMultiplier, boolean extended) {

    }

    @Override
    public boolean playEffect(World world, BlockPos pos, ConstellationEffectProperties properties, @Nullable IMinorConstellation trait) {
        boolean changed = false;

        if (properties.isCorrupted()) {
            TimeStopZone zone = TimeStopController.tryGetZoneAt(world, pos);
            if (zone == null) {
                zone = TimeStopController.freezeWorldAt(TimeStopZone.EntityTargetController.noPlayers(), world, pos, (float) properties.getSize(), 100);
            }
            zone.setTicksToLive(100);
            return true;
        }

        ListEntries.PosEntry entry = this.getRandomElementChanced();
        if (entry != null) {
            if (MiscUtils.executeWithChunk(world, entry.getPos(), () -> {
                TileEntity tile = world.getTileEntity(entry.getPos());
                if (TileAccelerationBlacklistRegistry.INSTANCE.canBeAccelerated(tile)) {
                    try {
                        long startNs = System.nanoTime();
                        int times = 2 + rand.nextInt(4);
                        while (times > 0) {
                            ((ITickable) tile).tick();
                            if((System.nanoTime() - startNs) >= 80_000) {
                                break;
                            }
                            times--;
                        }
                    } catch (Exception exc) {
                        TileAccelerationBlacklistRegistry.INSTANCE.addErrored(tile);
                        this.removeElement(entry);
                        AstralSorcery.log.warn("Couldn't accelerate TileEntity " + tile.getClass().getName() + ".");
                        AstralSorcery.log.warn("Temporarily blacklisting that class. Consider adding that to the blacklist if it persists?");
                        exc.printStackTrace();
                    }
                } else {
                    this.removeElement(entry);
                    return true;
                }
                return false;
            }, false)) {
                changed = true;
            }
        }

        if (this.findNewPosition(world, pos, properties) != null) {
            changed = true;
        }

        return changed;
    }

    @Override
    public Config getConfig() {
        return CONFIG;
    }

    private static class HorologiumConfig extends CountConfig {

        public HorologiumConfig() {
            super("horologium", 6D, 3D, 32);
        }
    }
}
