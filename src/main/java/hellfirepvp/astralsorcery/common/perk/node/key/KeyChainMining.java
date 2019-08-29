/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk.node.key;

import hellfirepvp.astralsorcery.common.data.config.base.ConfigEntry;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchHelper;
import hellfirepvp.astralsorcery.common.lib.PerkAttributeTypesAS;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.play.server.PktPlayEffect;
import hellfirepvp.astralsorcery.common.perk.PerkAttributeHelper;
import hellfirepvp.astralsorcery.common.perk.node.KeyPerk;
import hellfirepvp.astralsorcery.common.util.BlockDropCaptureAssist;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.block.BlockDiscoverer;
import hellfirepvp.astralsorcery.common.util.block.BlockUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.item.ItemUtils;
import hellfirepvp.observerlib.api.util.BlockArray;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.LogicalSide;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: KeyChainMining
 * Created by HellFirePvP
 * Date: 25.08.2019 / 20:13
 */
public class KeyChainMining extends KeyPerk {

    private static final float defaultChainChance = 0.2F;
    private static final int defaultChainLength = 4;
    private static boolean chainOngoing = false;

    private final Config config;

    public KeyChainMining(ResourceLocation name, int x, int y) {
        super(name, x, y);
        this.config = new Config(name.getPath());
    }

    @Override
    protected void attachListeners(IEventBus bus) {
        super.attachListeners(bus);
        bus.addListener(EventPriority.LOW, this::onBlockBreak);
    }

    @Nullable
    @Override
    protected ConfigEntry addConfig() {
        return this.config;
    }

    private void onBlockBreak(BlockEvent.BreakEvent event) {
        PlayerEntity player = event.getPlayer();
        LogicalSide side = this.getSide(player);
        PlayerProgress prog = ResearchHelper.getProgress(player, side);
        if (side == LogicalSide.SERVER &&
                player instanceof ServerPlayerEntity &&
                prog.hasPerkEffect(this) &&
                !MiscUtils.isPlayerFakeMP((ServerPlayerEntity) player) &&
                !player.isSneaking() &&
                event.getWorld() instanceof ServerWorld &&
                !player.isCreative()) {

            if (chainOngoing) {
                return;
            }
            chainOngoing = true;
            try {
                ServerWorld world = (ServerWorld) event.getWorld();
                if(doMiningChain(world, event.getPos(), event.getState(), (ServerPlayerEntity) player, side)) {
                    float doubleChance = PerkAttributeHelper.getOrCreateMap(player, side)
                            .getModifier(player, prog, PerkAttributeTypesAS.ATTR_TYPE_MINING_CHAIN_SUCCESSIVECHAIN);
                    if (rand.nextFloat() < doubleChance) {
                        while (doMiningChain(world, event.getPos(), event.getState(), (ServerPlayerEntity) player, side)) {}
                    }
                }
            } finally {
                chainOngoing = false;
            }
        }
    }

    private boolean doMiningChain(ServerWorld world, BlockPos pos, BlockState state, ServerPlayerEntity player, LogicalSide side) {
        PlayerProgress prog = ResearchHelper.getProgress(player, side);
        double ch = this.multipliedD(this.config.chainChance.get());
        ch = PerkAttributeHelper.getOrCreateMap(player, side)
                .modifyValue(player, prog, PerkAttributeTypesAS.ATTR_TYPE_MINING_CHAIN_CHANCE, (float) ch);
        if (rand.nextFloat() < ch) {
            float length = this.multipliedI(this.config.chainLength.get());
            length = PerkAttributeHelper.getOrCreateMap(player, side)
                    .modifyValue(player, prog, PerkAttributeTypesAS.ATTR_TYPE_MINING_CHAIN_LENGTH, length);
            BlockArray chain = BlockDiscoverer.discoverBlocksWithSameStateAroundChain(world, pos, state, MathHelper.floor(length), null,
                    ((world1, pos1, state1) ->
                            pos1.getY() >= player.getPosition().getY() &&
                                    state1.getBlockHardness(world1, pos1) >= 0 &&
                                    world1.getTileEntity(pos1) == null &&
                                    !world1.isAirBlock(pos1) &&
                                    state1.canHarvestBlock(world1, pos1, player)));

            Set<BlockPos> chainPositions = chain.getContents().keySet();
            if (!chainPositions.isEmpty()) {
                int broken = 0;
                for (BlockPos at : chainPositions) {
                    List<ItemStack> drops;
                    BlockDropCaptureAssist.startCapturing();
                    try {
                        BlockState prevState = world.getBlockState(at);
                        if (BlockUtils.breakBlockWithPlayer(at, player)) {
                            broken++;

                            //TODO particles
                            //PktPlayEffect ev = new PktPlayEffect(PktPlayEffect.Type.ARCHITECT_PLACE, at)
                            //        .addData(buf -> buf.writeInt(Block.getStateId(prevState)));
                            //PacketChannel.CHANNEL.sendToAllAround(ev, PacketChannel.pointFromPos(world, at, 16));
                        }
                    } finally {
                        drops = new ArrayList<>(BlockDropCaptureAssist.getCapturedStacksAndStop());
                    }

                    Vector3 plPos = Vector3.atEntityCenter(player);
                    drops.forEach(drop -> {
                        if (!player.inventory.addItemStackToInventory(drop)) {
                            ItemUtils.dropItemNaturally(player.getEntityWorld(),
                                    plPos.getX() + rand.nextFloat() - rand.nextFloat(),
                                    player.posY,
                                    plPos.getZ() + rand.nextFloat() - rand.nextFloat(),
                                    drop);
                        }
                    });
                }
                return broken >= chainPositions.size() / 2;
            }
        }
        return false;
    }

    private static class Config extends ConfigEntry {

        private ForgeConfigSpec.IntValue chainLength;
        private ForgeConfigSpec.DoubleValue chainChance;

        private Config(String section) {
            super(section);
        }

        @Override
        public void createEntries(ForgeConfigSpec.Builder cfgBuilder) {
            chainLength = cfgBuilder
                    .comment("Defines the maximum length of a single break-chain.")
                    .translation(translationKey("chainLength"))
                    .defineInRange("chainLength", defaultChainLength, 2, 20);

            chainChance = cfgBuilder
                    .comment("Defines the base chance a chain is tried to be built.")
                    .translation(translationKey("chainChance"))
                    .defineInRange("chainChance", defaultChainChance, 0.01, 1);
        }
    }
}
