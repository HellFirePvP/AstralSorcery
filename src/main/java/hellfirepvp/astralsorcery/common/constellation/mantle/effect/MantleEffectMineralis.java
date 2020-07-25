/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.mantle.effect;

import hellfirepvp.astralsorcery.client.util.MiscPlayEffect;
import hellfirepvp.astralsorcery.common.auxiliary.charge.AlignmentChargeHandler;
import hellfirepvp.astralsorcery.common.constellation.mantle.MantleEffect;
import hellfirepvp.astralsorcery.common.item.armor.ItemMantle;
import hellfirepvp.astralsorcery.common.lib.ConstellationsAS;
import hellfirepvp.astralsorcery.common.util.block.BlockDiscoverer;
import hellfirepvp.astralsorcery.common.util.block.BlockPredicate;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.item.ItemUtils;
import net.minecraft.block.AirBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.LogicalSide;

import java.util.List;

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
    protected void attachEventListeners(IEventBus bus) {
        super.attachEventListeners(bus);
        bus.addListener(EventPriority.LOWEST, this::onBreak);
    }

    private void onBreak(BlockEvent.BreakEvent event) {
        PlayerEntity player = event.getPlayer();
        if (ItemMantle.getEffect(player, ConstellationsAS.mineralis) != null) {
            LogicalSide side = player.getEntityWorld().isRemote() ? LogicalSide.CLIENT : LogicalSide.SERVER;
            if (side.isServer()) {
                float charge = Math.min(AlignmentChargeHandler.INSTANCE.getCurrentCharge(player, side), CONFIG.chargeCostPerBreak.get());
                AlignmentChargeHandler.INSTANCE.drainCharge(player, side, charge, false);
            }
        }
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
        MiscPlayEffect.playSingleBlockTumbleDepthEffect(new Vector3(at).add(0.5, 0.5, 0.5), displayState);
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

        private final int defaultChargeCostPerBreak = 2;

        public ForgeConfigSpec.IntValue highlightRange;

        public ForgeConfigSpec.IntValue chargeCostPerBreak;

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

            this.chargeCostPerBreak = cfgBuilder
                    .comment("Set the amount alignment charge consumed per block break enhanced by the mantle effect")
                    .translation(translationKey("chargeCostPerBreak"))
                    .defineInRange("chargeCostPerBreak", this.defaultChargeCostPerBreak, 0, 1000);
        }

    }

}
