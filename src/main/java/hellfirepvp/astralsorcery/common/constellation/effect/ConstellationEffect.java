/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.effect;

import hellfirepvp.astralsorcery.client.effect.function.VFXAlphaFunction;
import hellfirepvp.astralsorcery.client.effect.function.VFXColorFunction;
import hellfirepvp.astralsorcery.client.effect.handler.EffectHelper;
import hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.IMinorConstellation;
import hellfirepvp.astralsorcery.common.constellation.IWeakConstellation;
import hellfirepvp.astralsorcery.common.data.config.base.ConfigEntry;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.play.server.PktPlayEffect;
import hellfirepvp.astralsorcery.common.tile.TileRitualLink;
import hellfirepvp.astralsorcery.common.tile.TileRitualPedestal;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.block.ILocatable;
import hellfirepvp.astralsorcery.common.util.data.ByteBufUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeConfigSpec;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ConstellationEffect
 * Created by HellFirePvP
 * Date: 05.06.2019 / 22:07
 */
public abstract class ConstellationEffect {

    protected static final Random rand = new Random();
    protected static final AxisAlignedBB BOX = new AxisAlignedBB(0, 0, 0, 1, 1, 1);

    private final IWeakConstellation cst;
    private final ILocatable pos;
    private boolean needsChunkToBeLoaded = false;

    protected ConstellationEffect(@Nonnull ILocatable origin, @Nonnull IWeakConstellation cst) {
        this.cst = cst;
        this.pos = origin;
    }

    protected void setChunkNeedsToBeLoaded() {
        this.needsChunkToBeLoaded = true;
    }

    public boolean needsChunkToBeLoaded() {
        return this.needsChunkToBeLoaded;
    }

    @OnlyIn(Dist.CLIENT)
    public abstract void playClientEffect(World world, BlockPos pos, TileRitualPedestal pedestal, float alphaMultiplier, boolean extended);

    public abstract boolean playEffect(World world, BlockPos pos, ConstellationEffectProperties properties, @Nullable IMinorConstellation trait) ;

    @Nullable
    public TileRitualPedestal getPedestal(World world, BlockPos pos) {
        TileEntity te = MiscUtils.getTileAt(world, pos, TileEntity.class, false);
        if (te instanceof TileRitualLink) {
            TileRitualLink link = (TileRitualLink) te;
            pos = link.getLinkedTo();
            if (pos != null) {
                pos = pos.subtract(TileRitualPedestal.RITUAL_ANCHOR_OFFEST);
                return MiscUtils.getTileAt(world, pos, TileRitualPedestal.class, false);
            }
        }
        return te instanceof TileRitualPedestal ? (TileRitualPedestal) te : null;
    }

    public ConstellationEffectProperties createProperties(int mirrors) {
        return new ConstellationEffectProperties(getConfig().range.get() + mirrors * getConfig().rangePerLens.get());
    }

    public abstract Config getConfig();

    @Nonnull
    public IWeakConstellation getConstellation() {
        return cst;
    }

    @Nonnull
    public ILocatable getPos() {
        return pos;
    }

    public void clearCache() {}

    public void readFromNBT(CompoundNBT cmp) {}

    public void writeToNBT(CompoundNBT cmp) {}

    @Nullable
    public PlayerEntity getOwningPlayerInWorld(World world, BlockPos pos) {
        TileRitualPedestal pedestal = getPedestal(world, pos);
        if (pedestal != null) {
            return pedestal.getOwner();
        }
        return null;
    }

    public void sendConstellationPing(IWorld world, Vector3 at) {
        sendConstellationPing(world, at, this.getConstellation());
    }

    public static void sendConstellationPing(IWorld world, Vector3 at, IConstellation cst) {
        PktPlayEffect pkt = new PktPlayEffect(PktPlayEffect.Type.CONSTELLATION_EFFECT_PING)
                .addData(buf -> {
                    ByteBufUtils.writeVector(buf, at);
                    ByteBufUtils.writeRegistryEntry(buf, cst);
                });
        PacketChannel.CHANNEL.sendToAllAround(pkt, PacketChannel.pointFromPos(world, at.toBlockPos(), 32));
    }

    @OnlyIn(Dist.CLIENT)
    public static void playConstellationPing(PktPlayEffect pktPlayEffect) {
        Vector3 at = ByteBufUtils.readVector(pktPlayEffect.getExtraData());
        IConstellation cst = ByteBufUtils.readRegistryEntry(pktPlayEffect.getExtraData());

        for (int i = 0; i < 6; i++) {
            EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                    .spawn(at.clone().add(Vector3.random().multiply(0.25F)))
                    .setMotion(Vector3.random().multiply(0.015F))
                    .alpha(VFXAlphaFunction.FADE_OUT)
                    .color(VFXColorFunction.constant(cst.getConstellationColor().brighter()))
                    .setScaleMultiplier(0.25F + rand.nextFloat() * 0.15F)
                    .setMaxAge(35 + rand.nextInt(20));
        }
    }

    public static abstract class Config extends ConfigEntry {

        private final boolean defaultEnabled = true;
        private final double  defaultRange;
        private final double  defaultRangePerLens;

        public ForgeConfigSpec.BooleanValue enabled;
        public ForgeConfigSpec.DoubleValue  range;
        public ForgeConfigSpec.DoubleValue  rangePerLens;

        public Config(String constellationName, double defaultRange, double defaultRangePerLens) {
            super(String.format("constellation.effect.%s", constellationName));
            this.defaultRange = defaultRange;
            this.defaultRangePerLens = defaultRangePerLens;
        }

        @Override
        public void createEntries(ForgeConfigSpec.Builder cfgBuilder) {
            this.enabled = cfgBuilder
                    .comment("Set this to false to disable this ritual effect")
                    .translation(translationKey("enabled"))
                    .define("enabled", this.defaultEnabled);
            this.range = cfgBuilder
                    .comment("Defines the radius (in blocks) in which the ritual will allow the players to fly in.")
                    .translation(translationKey("range"))
                    .defineInRange("range", this.defaultRange, 1, 512);
            this.rangePerLens = cfgBuilder
                    .comment("Defines the increase in radius the ritual will get per active lens enhancing the ritual.")
                    .translation(translationKey("rangePerLens"))
                    .defineInRange("rangePerLens", this.defaultRangePerLens, 0, 128);
        }
    }
}
