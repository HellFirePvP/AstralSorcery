/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item;

import hellfirepvp.astralsorcery.common.data.level.RockCrystalData;
import hellfirepvp.astralsorcery.common.network.play.PktPlayStructurePreview;
import hellfirepvp.astralsorcery.common.util.data.ObserverRegistryObject;
import hellfirepvp.astralsorcery.common.visual.type.RockCrystalSparkle;
import hellfirepvp.astralsorcery.common.item.base.InterceptInteractItem;
import hellfirepvp.astralsorcery.common.item.base.ItemCustom;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.DataAS;
import hellfirepvp.astralsorcery.common.structure.observer.CompoundObserverProviderStructure;
import hellfirepvp.astralsorcery.common.tile.base.TileEntityTick;
import hellfirepvp.astralsorcery.common.util.ChunkUtil;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import hellfirepvp.observerlib.api.ObserverProvider;
import hellfirepvp.observerlib.api.structure.MatchableStructure;
import hellfirepvp.observerlib.common.change.ObserverProviderStructure;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.network.PacketDistributor;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: WandItem
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class WandItem extends ItemCustom implements InterceptInteractItem.Block {

    public WandItem() {
        super(new Properties()
                .stacksTo(1));
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (level instanceof ServerLevel sLevel && entity instanceof ServerPlayer sPlayer) {
            if (isSelected || sPlayer.getOffhandItem() == stack) {
                RockCrystalData data = DataAS.DOMAIN_AS.getData(sLevel, DataAS.KEY_ROCK_CRYSTAL_DATA);
                RandomSource rand = sLevel.getRandom();
                ChunkPos chunkPos = new ChunkPos(sPlayer.blockPosition());
                for (BlockPos orePos : data.collectPositions(chunkPos, 8)) {
                    ChunkUtil.executeWithChunk(sLevel, orePos, () -> {
                        BlockState state = sLevel.getBlockState(orePos);
                        if (!state.is(BlocksAS.ROCK_CRYSTAL_ORE)) {
                            data.removeOre(orePos);
                            return;
                        }
                        if (orePos.distSqr(sPlayer.blockPosition()) >= 115 * 115) {
                            return;
                        }

                        if (rand.nextInt(600) == 0) {
                            RockCrystalSparkle.at(orePos).sendEffect(sPlayer);
                        }
                    });
                }
            }
        }
    }

    @Override
    public boolean shouldInterceptBlockInteract(LogicalSide side, Player player, InteractionHand hand, BlockPos pos, BlockHitResult hitResult, Direction blockFace) {
        return true;
    }

    @Override
    public boolean doBlockInteract(LogicalSide side, Player player, InteractionHand hand, BlockPos pos, BlockHitResult hitResult, Direction blockFace) {
        Level level = player.level();
        if (level.isClientSide()) return false;
        if (!(player instanceof ServerPlayer sPlayer)) return false;

        MiscUtil.getTileAt(level, pos, TileEntityTick.class, true).ifPresent(te -> {
            ObserverRegistryObject registryObject = te.getRequiredObserver();
            if (registryObject != null) {
                ObserverProvider<?> observer = registryObject.observer().get();

                boolean isValid = ((TileEntityTick<?>) te).getStructureObserver()
                        .map(sub -> sub.isValid(level))
                        .orElse(false);
                if (isValid && !sPlayer.isShiftKeyDown()) {
                    return;
                }

                MatchableStructure neededStructure = null;
                if (observer instanceof ObserverProviderStructure structureObserver) {
                    neededStructure = structureObserver.getStructure();
                } else if (observer instanceof CompoundObserverProviderStructure compoundStructureObserver) {
                    for (MatchableStructure structure : compoundStructureObserver.getStructures()) {
                        if (!structure.matches(level, pos)) {
                            neededStructure = structure;
                            break;
                        }
                    }
                }
                if (neededStructure != null) {
                    if (player.isCreative() && player.isShiftKeyDown()) {
                        neededStructure.getContents().forEach((offset, state) -> {
                            level.setBlock(pos.offset(offset), state.getDescriptiveState(0), Block.UPDATE_ALL);
                        });
                    } else {
                        PacketDistributor.sendToPlayer(sPlayer, PktPlayStructurePreview.showPreview(pos, registryObject));
                    }
                }
            }
        });
        return false;
    }
}
