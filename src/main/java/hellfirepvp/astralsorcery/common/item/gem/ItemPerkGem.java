/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.gem;

import hellfirepvp.astralsorcery.common.CommonProxy;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.perk.AbstractPerk;
import hellfirepvp.astralsorcery.common.perk.DynamicModifierHelper;
import hellfirepvp.astralsorcery.common.perk.modifier.DynamicAttributeModifier;
import hellfirepvp.astralsorcery.common.perk.node.socket.GemSocketItem;
import hellfirepvp.astralsorcery.common.perk.node.socket.GemSocketPerk;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.LogicalSide;

import javax.annotation.Nullable;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemPerkGem
 * Created by HellFirePvP
 * Date: 09.08.2019 / 07:25
 */
public abstract class ItemPerkGem extends Item implements GemSocketItem {

    private final GemType type;

    public ItemPerkGem(GemType type) {
        super(new Properties()
                .maxStackSize(1)
                .group(CommonProxy.ITEM_GROUP_AS));
        this.type = type;
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
        if (world.isRemote()) {
            return;
        }

        if (DynamicModifierHelper.getStaticModifiers(stack).isEmpty()) {
            GemAttributeHelper.rollGem(stack);
        }
    }

    @Nullable
    public static GemType getGemType(ItemStack gem) {
        if (gem.isEmpty() || !(gem.getItem() instanceof ItemPerkGem)) {
            return null;
        }
        return ((ItemPerkGem) gem.getItem()).type;
    }

    @Override
    public <T extends AbstractPerk & GemSocketPerk> boolean canBeInserted(ItemStack stack, T perk, PlayerEntity player, PlayerProgress progress, LogicalSide side) {
        return !this.getModifiers(stack, perk, player, side).isEmpty();
    }

    @Override
    public <T extends AbstractPerk & GemSocketPerk> List<DynamicAttributeModifier> getModifiers(ItemStack stack, T perk, PlayerEntity player, LogicalSide side) {
        return DynamicModifierHelper.getStaticModifiers(stack);
    }
}
