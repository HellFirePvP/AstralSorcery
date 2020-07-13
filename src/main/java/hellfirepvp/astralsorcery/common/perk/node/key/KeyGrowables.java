/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk.node.key;

import hellfirepvp.astralsorcery.common.auxiliary.CropHelper;
import hellfirepvp.astralsorcery.common.data.config.base.ConfigEntry;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchHelper;
import hellfirepvp.astralsorcery.common.lib.PerkAttributeTypesAS;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.play.server.PktPlayEffect;
import hellfirepvp.astralsorcery.common.perk.PerkAttributeHelper;
import hellfirepvp.astralsorcery.common.perk.node.KeyPerk;
import hellfirepvp.astralsorcery.common.perk.tick.PlayerTickPerk;
import hellfirepvp.astralsorcery.common.util.data.ByteBufUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.LogicalSide;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: KeyGrowables
 * Created by HellFirePvP
 * Date: 31.08.2019 / 17:48
 */
public class KeyGrowables extends KeyPerk implements PlayerTickPerk {

    private static final float defaultChanceToBonemeal = 0.3F;
    private static final int defaultRadius = 3;

    private final Config config;

    public KeyGrowables(ResourceLocation name, int x, int y) {
        super(name, x, y);
        this.config = new Config(name.getPath());
    }

    @Override
    public void onPlayerTick(PlayerEntity player, LogicalSide side) {
        if (!side.isServer()) {
            return;
        }

        PlayerProgress prog = ResearchHelper.getProgress(player, side);
        float cChance = PerkAttributeHelper.getOrCreateMap(player, side)
                .modifyValue(player, prog, PerkAttributeTypesAS.ATTR_TYPE_INC_PERK_EFFECT, (float) this.applyMultiplierD(this.config.chanceToBonemeal.get()));
        if (rand.nextFloat() < cChance) {
            float fRadius = PerkAttributeHelper.getOrCreateMap(player, side)
                    .modifyValue(player, prog, PerkAttributeTypesAS.ATTR_TYPE_INC_PERK_EFFECT, this.applyMultiplierI(this.config.radius.get()));
            int rRadius = Math.max(MathHelper.floor(fRadius), 1);

            BlockPos pos = player.getPosition().add(
                    rand.nextInt(rRadius * 2) + 1 - rRadius,
                    rand.nextInt(rRadius * 2) + 1 - rRadius,
                    rand.nextInt(rRadius * 2) + 1 - rRadius);
            World w = player.getEntityWorld();
            CropHelper.GrowablePlant plant = CropHelper.wrapPlant(w, pos);
            PktPlayEffect pkt = null;
            if (plant != null) {
                if (plant.tryGrow(w, rand)) {
                    pkt = new PktPlayEffect(PktPlayEffect.Type.CROP_GROWTH)
                        .addData(buf -> ByteBufUtils.writeVector(buf, new Vector3(pos)));
                }
            } else {
                BlockState at = w.getBlockState(pos);
                if (at.getBlock().equals(Blocks.DIRT)) {
                    if (w.setBlockState(pos, Blocks.GRASS_BLOCK.getDefaultState())) {
                        pkt = new PktPlayEffect(PktPlayEffect.Type.CROP_GROWTH)
                                .addData(buf -> ByteBufUtils.writeVector(buf, new Vector3(pos)));
                    }
                }
            }
            if (pkt != null) {
                PacketChannel.CHANNEL.sendToAllAround(pkt, PacketChannel.pointFromPos(w, pos, 16));
            }
        }
    }

    @Nullable
    @Override
    protected ConfigEntry addConfig() {
        return this.config;
    }

    public static class Config extends ConfigEntry {

        private ForgeConfigSpec.DoubleValue chanceToBonemeal;
        private ForgeConfigSpec.IntValue radius;

        private Config(String section) {
            super(section);
        }

        @Override
        public void createEntries(ForgeConfigSpec.Builder cfgBuilder) {
            this.chanceToBonemeal = cfgBuilder
                    .comment("Sets the chance to try to see if a random plant near the player gets bonemeal'd.")
                    .translation(translationKey("chanceToBonemeal"))
                    .defineInRange("chanceToBonemeal", defaultChanceToBonemeal, 0F, 1F);
            this.radius = cfgBuilder
                    .comment("Defines the radius around which the perk effect should apply around the player.")
                    .translation(translationKey("radius"))
                    .defineInRange("radius", defaultRadius, 1, 16);
        }
    }
}
