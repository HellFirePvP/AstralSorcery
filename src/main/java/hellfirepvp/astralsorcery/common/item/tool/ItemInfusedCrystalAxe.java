/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.tool;

import hellfirepvp.astralsorcery.common.event.EventFlags;
import hellfirepvp.astralsorcery.common.lib.PerkAttributeTypesAS;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.play.server.PktPlayEffect;
import hellfirepvp.astralsorcery.common.perk.modifier.DynamicAttributeModifier;
import hellfirepvp.astralsorcery.common.perk.modifier.PerkAttributeModifier;
import hellfirepvp.astralsorcery.common.perk.source.provider.equipment.EquipmentAttributeModifierProvider;
import hellfirepvp.astralsorcery.common.perk.type.ModifierType;
import hellfirepvp.astralsorcery.common.util.block.TreeDiscoverer;
import hellfirepvp.astralsorcery.common.util.data.ByteBufUtils;
import hellfirepvp.observerlib.api.util.BlockArray;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.LogicalSide;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemInfusedCrystalAxe
 * Created by HellFirePvP
 * Date: 03.04.2020 / 18:18
 */
public class ItemInfusedCrystalAxe extends ItemCrystalAxe implements EquipmentAttributeModifierProvider {

    private static final DynamicAttributeModifier INFUSED_AXE_MINING_SPEED =
            new DynamicAttributeModifier(UUID.fromString("85c65b91-f44c-4aba-841d-7785eae32831"), PerkAttributeTypesAS.ATTR_TYPE_INC_HARVEST_SPEED, ModifierType.ADDED_MULTIPLY, 0.1F);

    @Override
    public boolean onBlockStartBreak(ItemStack itemstack, BlockPos pos, PlayerEntity player) {
        World world = player.getEntityWorld();
        if (!world.isRemote() &&
                !player.isSneaking() &&
                !player.getCooldownTracker().hasCooldown(itemstack.getItem()) &&
                player instanceof ServerPlayerEntity) {

            EventFlags.CHAIN_MINING.executeWithFlag(() -> {
                BlockArray tree = TreeDiscoverer.findTreeAt(world, pos, true, 9);
                if (!tree.getContents().isEmpty()) {
                    ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;

                    tree.getContents().keySet().forEach(at -> {
                        BlockState currentState = world.getBlockState(at);
                        if (serverPlayer.interactionManager.tryHarvestBlock(at)) {
                            PktPlayEffect ev = new PktPlayEffect(PktPlayEffect.Type.BLOCK_EFFECT)
                                    .addData(buf -> {
                                        ByteBufUtils.writePos(buf, at);
                                        ByteBufUtils.writeBlockState(buf, currentState);
                                    });
                            PacketChannel.CHANNEL.sendToAllAround(ev, PacketChannel.pointFromPos(world, at, 32));
                        }
                    });

                    serverPlayer.getCooldownTracker().setCooldown(itemstack.getItem(), 120);
                }
            });
        }
        return super.onBlockStartBreak(itemstack, pos, player);
    }

    @Override
    public Collection<PerkAttributeModifier> getModifiers(ItemStack stack, PlayerEntity player, LogicalSide side, boolean ignoreRequirements) {
        return Collections.singletonList(INFUSED_AXE_MINING_SPEED);
    }
}
