/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.tool;

import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchHelper;
import hellfirepvp.astralsorcery.common.lib.PerkAttributeTypesAS;
import hellfirepvp.astralsorcery.common.lib.TagsAS;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.play.server.PktOreScan;
import hellfirepvp.astralsorcery.common.perk.modifier.DynamicAttributeModifier;
import hellfirepvp.astralsorcery.common.perk.modifier.PerkAttributeModifier;
import hellfirepvp.astralsorcery.common.perk.source.provider.equipment.EquipmentAttributeModifierProvider;
import hellfirepvp.astralsorcery.common.perk.type.ModifierType;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.block.BlockDiscoverer;
import hellfirepvp.astralsorcery.common.util.block.BlockPredicates;
import hellfirepvp.astralsorcery.common.util.object.CacheReference;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.LogicalSide;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemInfusedCrystalPickaxe
 * Created by HellFirePvP
 * Date: 04.04.2020 / 09:57
 */
public class ItemInfusedCrystalPickaxe extends ItemCrystalPickaxe implements EquipmentAttributeModifierProvider {

    private static final UUID MODIFIER_ID = UUID.fromString("ecf80c60-3da6-4952-90d0-5db5429ea44a");
    private static final CacheReference<DynamicAttributeModifier> MINING_SIZE_MODIFIER =
            new CacheReference<>(() -> new DynamicAttributeModifier(MODIFIER_ID, PerkAttributeTypesAS.ATTR_TYPE_MINING_SIZE, ModifierType.ADDITION, 1F));

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        ItemStack held = player.getHeldItem(hand);
        if (this.doOreScan(world, player.getPosition(), player, held)) {
            return ActionResult.resultSuccess(held);
        }
        return super.onItemRightClick(world, player, hand);
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext ctx) {
        PlayerEntity player = ctx.getPlayer();
        if (player != null) {
            if (this.doOreScan(ctx.getWorld(), ctx.getPos(), player, player.getHeldItem(ctx.getHand()))) {
                return ActionResultType.SUCCESS;
            }
        }
        return super.onItemUse(ctx);
    }

    private boolean doOreScan(World world, BlockPos origin, PlayerEntity player, ItemStack stack) {
        if (!world.isRemote() && player instanceof ServerPlayerEntity && !MiscUtils.isPlayerFakeMP((ServerPlayerEntity) player)) {
            if (stack.getItem() instanceof ItemInfusedCrystalPickaxe && !player.getCooldownTracker().hasCooldown(stack.getItem())) {
                PlayerProgress prog = ResearchHelper.getProgress(player, LogicalSide.SERVER);
                if (prog.doPerkAbilities()) {
                    List<BlockPos> orePositions = BlockDiscoverer.searchForBlocksAround(world, origin, 16, BlockPredicates.isInTag(TagsAS.Blocks.ORES));
                    PacketChannel.CHANNEL.sendToPlayer(player, new PktOreScan(orePositions));

                    player.getCooldownTracker().setCooldown(stack.getItem(), 120);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public Collection<PerkAttributeModifier> getModifiers(ItemStack stack, PlayerEntity player, LogicalSide side, boolean ignoreRequirements) {
        return Collections.singletonList(MINING_SIZE_MODIFIER.get());
    }
}
