/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.mantle.effect;

import hellfirepvp.astralsorcery.client.effect.function.VFXScaleFunction;
import hellfirepvp.astralsorcery.client.effect.handler.EffectHelper;
import hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS;
import hellfirepvp.astralsorcery.common.constellation.mantle.MantleEffect;
import hellfirepvp.astralsorcery.common.lib.ConstellationsAS;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.block.BlockDiscoverer;
import hellfirepvp.astralsorcery.common.util.block.BlockPredicate;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.item.ItemUtils;
import net.minecraft.block.AirBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;
import java.util.Set;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: MantleEffectMineralis
 * Created by HellFirePvP
 * Date: 21.02.2020 / 20:14
 */
public class MantleEffectMineralis extends MantleEffect {

    public static MineralisConfig CONFIG = new MineralisConfig();

    public MantleEffectMineralis() {
        super(ConstellationsAS.mineralis);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    protected void tickClient(PlayerEntity player) {
        super.tickClient(player);

        this.playCapeSparkles(player, 0.15F);

        if (rand.nextBoolean()) {
            this.playBlockHighlight(player);
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void playBlockHighlight(PlayerEntity player) {
        BlockState state = null;
        if (!player.getHeldItem(Hand.MAIN_HAND).isEmpty()) {
            state = ItemUtils.createBlockState(player.getHeldItem(Hand.MAIN_HAND));
        }
        if (!player.getHeldItem(Hand.OFF_HAND).isEmpty()) {
            state = ItemUtils.createBlockState(player.getHeldItem(Hand.OFF_HAND));
        }
        if (state == null || state.getBlock() instanceof AirBlock) {
            return;
        }
        BlockState fState = state;

        BlockPredicate search = (world, pos, foundState) -> foundState == fState;
        List<BlockPos> positions = BlockDiscoverer.searchForBlocksAround(player.getEntityWorld(), player.getPosition(), CONFIG.highlightRange.get(), search);
        if (positions.isEmpty()) {
            return;
        }
        int index = positions.size() > 10 ? rand.nextInt(positions.size()) : rand.nextInt(10);
        if (index >= positions.size()) {
            return;
        }

        BlockPos at = positions.get(index);
        BlockState displayState = player.getEntityWorld().getBlockState(at);

        EffectHelper.of(EffectTemplatesAS.BLOCK_TRANSLUCENT_IGNORE_DEPTH)
                .setOwner(player.getUniqueID())
                .spawn(new Vector3(at))
                .tumble()
                .setBlockState(displayState)
                .setMotion(new Vector3(0, 0.035, 0))
                .scale(VFXScaleFunction.SHRINK_EXP)
                .setMaxAge(40 + rand.nextInt(10));
    }

    @Override
    public Config getConfig() {
        return CONFIG;
    }

    @Override
    protected boolean usesTickMethods() {
        return true;
    }

    public static class MineralisConfig extends Config {

        private final int defaultHighlightRange = 10;

        public ForgeConfigSpec.IntValue highlightRange;

        public MineralisConfig() {
            super("mineralis");
        }

        @Override
        public void createEntries(ForgeConfigSpec.Builder cfgBuilder) {
            super.createEntries(cfgBuilder);

            this.highlightRange = cfgBuilder
                    .comment("Sets the highlight radius in which the cape effect will search for the block you're holding. Set to 0 to disable this effect.")
                    .translation(translationKey("range"))
                    .defineInRange("range", this.defaultHighlightRange, 0, 32);
        }

    }

}
