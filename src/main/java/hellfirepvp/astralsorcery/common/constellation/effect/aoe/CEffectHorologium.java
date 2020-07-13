/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.effect.aoe;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.client.effect.function.VFXAlphaFunction;
import hellfirepvp.astralsorcery.client.effect.function.VFXColorFunction;
import hellfirepvp.astralsorcery.client.effect.handler.EffectHelper;
import hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS;
import hellfirepvp.astralsorcery.common.constellation.IMinorConstellation;
import hellfirepvp.astralsorcery.common.constellation.effect.ConstellationEffectProperties;
import hellfirepvp.astralsorcery.common.constellation.effect.base.CEffectAbstractList;
import hellfirepvp.astralsorcery.common.constellation.effect.base.ListEntries;
import hellfirepvp.astralsorcery.common.data.config.registry.TileAccelerationBlacklistRegistry;
import hellfirepvp.astralsorcery.common.lib.ColorsAS;
import hellfirepvp.astralsorcery.common.lib.ConstellationsAS;
import hellfirepvp.astralsorcery.common.tile.TileRitualPedestal;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.block.ILocatable;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.time.TimeStopController;
import hellfirepvp.astralsorcery.common.util.time.TimeStopZone;
import net.minecraft.client.renderer.texture.ITickable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;

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
    @OnlyIn(Dist.CLIENT)
    public void playClientEffect(World world, BlockPos pos, TileRitualPedestal pedestal, float alphaMultiplier, boolean extended) {
        ConstellationEffectProperties prop = this.createProperties(pedestal.getMirrorCount());

        for (int i = 0; i < 2; i++) {
            Color c = MiscUtils.eitherOf(rand,
                    () -> Color.WHITE,
                    () -> ColorsAS.CONSTELLATION_HOROLOGIUM);
            Vector3 at = Vector3.random().normalize().multiply(rand.nextFloat() * prop.getSize()).add(pos).add(0.5, 0.5, 0.5);
            EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                    .spawn(at)
                    .alpha(VFXAlphaFunction.FADE_OUT)
                    .color(VFXColorFunction.constant(c))
                    .setScaleMultiplier(0.3F + rand.nextFloat() * 0.5F)
                    .setMaxAge(40 + rand.nextInt(20));
        }

        if (rand.nextInt(8) == 0) {
            Vector3 rand1 = Vector3.random().normalize().multiply(rand.nextFloat() * prop.getSize()).add(pos).add(0.5, 0.5, 0.5);
            Vector3 rand2 = Vector3.random().normalize().multiply(rand.nextFloat() * prop.getSize()).add(pos).add(0.5, 0.5, 0.5);
            EffectHelper.of(EffectTemplatesAS.LIGHTNING)
                    .spawn(rand1)
                    .makeDefault(rand2)
                    .color(VFXColorFunction.WHITE);
        }
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
                TileEntity tile = MiscUtils.getTileAt(world, entry.getPos(), TileEntity.class, true);
                if (tile != null && TileAccelerationBlacklistRegistry.INSTANCE.canBeAccelerated(tile)) {
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
