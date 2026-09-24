/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.tool;

import hellfirepvp.astralsorcery.common.component.DynamicModifiersComponent;
import hellfirepvp.astralsorcery.common.lib.DataComponentsAS;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.lib.PerksAS;
import hellfirepvp.astralsorcery.common.perk.modifier.DynamicAttributeModifier;
import hellfirepvp.astralsorcery.common.perk.type.base.ModifierType;
import hellfirepvp.astralsorcery.common.util.BlockFinder;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import hellfirepvp.astralsorcery.common.visual.type.OreFinderEffect;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.Tags;

import java.util.List;
import java.util.UUID;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: IridescentCrystalPickaxeItem
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class IridescentCrystalPickaxeItem extends CrystalPickaxeItem {

    private static final UUID MODIFIER_ID = UUID.fromString("7cdd0c5a-0129-48a4-9676-2ee22aef0369");
    private static final DynamicAttributeModifier MINING_SIZE_MODIFIER =
            new DynamicAttributeModifier(MODIFIER_ID.toString(), PerksAS.AttributeTypes.MINING_SIZE, ModifierType.ADDITION, 1F);

    public IridescentCrystalPickaxeItem() {
        super(ItemsAS.CRYSTAL_TOOL_TIER, new Properties()
                .setNoRepair()
                .component(DataComponentsAS.DYNAMIC_MODIFIERS, new DynamicModifiersComponent(List.of(MINING_SIZE_MODIFIER)))
                .attributes(pickaxeAttributes()));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack held = player.getItemInHand(usedHand);
        if (this.findOres(level, player.blockPosition(), player, held)) {
            return InteractionResultHolder.success(held);
        }
        return InteractionResultHolder.pass(player.getItemInHand(usedHand));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        if (player != null) {
            ItemStack held = player.getItemInHand(context.getHand());
            if (this.findOres(context.getLevel(), context.getClickedPos(), player, held)) {
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }

    protected boolean findOres(Level level, BlockPos clickedPos, Player player, ItemStack held) {
        if (!(player instanceof ServerPlayer sPlayer) || MiscUtil.isPlayerFake(sPlayer)) return false;
        if (sPlayer.getCooldowns().isOnCooldown(held.getItem())) return false;

        List<BlockPos> positions = BlockFinder.findNearbyBlocks(level, clickedPos, 16, state -> state.is(Tags.Blocks.ORES));
        if (!positions.isEmpty()) {
            OreFinderEffect.create(level, positions).sendEffect(sPlayer);
            sPlayer.getCooldowns().addCooldown(held.getItem(), 20 * 30);
        } else {
            sPlayer.getCooldowns().addCooldown(held.getItem(), 20 * 3);
        }
        return false;
    }
}
