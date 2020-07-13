/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.tool;

import com.google.common.collect.Multimap;
import hellfirepvp.astralsorcery.common.CommonProxy;
import hellfirepvp.astralsorcery.common.crystal.CalculationContext;
import hellfirepvp.astralsorcery.common.crystal.CrystalAttributeItem;
import hellfirepvp.astralsorcery.common.crystal.CrystalAttributes;
import hellfirepvp.astralsorcery.common.crystal.CrystalCalculations;
import hellfirepvp.astralsorcery.common.lib.CrystalPropertiesAS;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemCrystalTierItem
 * Created by HellFirePvP
 * Date: 17.08.2019 / 16:10
 */
public abstract class ItemCrystalTierItem extends Item implements CrystalAttributeItem {

    private final Set<Material> effectiveMaterials;
    private final ToolType toolType;

    protected ItemCrystalTierItem(@Nullable ToolType toolType, Properties prop, Set<Material> effectiveMaterials) {
        super(withTool(toolType, prop
                .maxDamage(CrystalToolTier.getInstance().getMaxUses())
                .setNoRepair()
                .group(CommonProxy.ITEM_GROUP_AS)));
        this.effectiveMaterials = effectiveMaterials;
        this.toolType = toolType;
    }

    private static Item.Properties withTool(@Nullable ToolType tool, Item.Properties prop) {
        if (tool != null) {
            prop.addToolType(tool, CrystalToolTier.getInstance().getHarvestLevel());
        }
        return prop;
    }

    abstract double getAttackDamage();

    abstract double getAttackSpeed();

    @Override
    @OnlyIn(Dist.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
        CrystalAttributes attr = getAttributes(stack);
        if (attr != null) {
            attr.addTooltip(tooltip, CalculationContext.Builder.newBuilder()
                    .addUsage(CrystalPropertiesAS.Usages.USE_TOOL_DURABILITY)
                    .addUsage(CrystalPropertiesAS.Usages.USE_TOOL_EFFECTIVENESS)
                    .build());
        }
        super.addInformation(stack, world, tooltip, flag);
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        CrystalAttributes attr = getAttributes(stack);
        if (attr != null) {
            return CrystalCalculations.getToolDurability(super.getMaxDamage(stack), stack);
        }
        return super.getMaxDamage(stack);
    }

    @Override
    public boolean canHarvestBlock(ItemStack stack, BlockState state) {
        int i = CrystalToolTier.getInstance().getHarvestLevel();
        if (this.toolType != null && state.getHarvestTool() == this.toolType) {
            return i >= state.getHarvestLevel();
        }
        return this.effectiveMaterials.contains(state.getMaterial());
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        float str = super.getDestroySpeed(stack, state);
        if (getToolTypes(stack).stream().noneMatch(state::isToolEffective) &&
                !isToolEfficientAgainst(state) &&
                !this.effectiveMaterials.contains(state.getMaterial())) {
            return str;
        }
        str *= CrystalToolTier.getInstance().getEfficiency();
        CrystalAttributes attr = getAttributes(stack);
        if (attr != null) {
            return CrystalCalculations.getToolEfficiency(str, stack);
        }
        return str;
    }

    protected boolean isToolEfficientAgainst(BlockState state) {
        return false;
    }

    @Override
    public boolean hitEntity(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        stack.damageItem(1, attacker, (entity) -> entity.sendBreakAnimation(EquipmentSlotType.MAINHAND));
        return true;
    }

    @Override
    public boolean onBlockDestroyed(ItemStack stack, World worldIn, BlockState state, BlockPos pos, LivingEntity entityLiving) {
        if (!worldIn.isRemote && state.getBlockHardness(worldIn, pos) != 0.0F) {
            stack.damageItem(1, entityLiving, (p_220038_0_) -> p_220038_0_.sendBreakAnimation(EquipmentSlotType.MAINHAND));
        }
        return true;
    }

    @Nullable
    @Override
    public CrystalAttributes getAttributes(ItemStack stack) {
        return CrystalAttributes.getCrystalAttributes(stack);
    }

    @Override
    public void setAttributes(ItemStack stack, @Nullable CrystalAttributes attributes) {
        if (attributes != null) {
            attributes.store(stack);
        } else {
            CrystalAttributes.storeNull(stack);
        }
    }

    @Override
    public boolean isRepairable(ItemStack stack) {
        return false;
    }

    @Override
    public boolean getIsRepairable(ItemStack p_82789_1_, ItemStack p_82789_2_) {
        return false;
    }

    @Override
    public int getItemEnchantability(ItemStack stack) {
        return CrystalToolTier.getInstance().getEnchantability();
    }

    @Override
    public Multimap<String, AttributeModifier> getAttributeModifiers(EquipmentSlotType slot, ItemStack stack) {
        Multimap<String, AttributeModifier> multimap = super.getAttributeModifiers(slot, stack);
        if (slot == EquipmentSlotType.MAINHAND) {
            multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Tool modifier", this.getAttackDamage(), AttributeModifier.Operation.ADDITION));
            multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Tool modifier", this.getAttackSpeed(), AttributeModifier.Operation.ADDITION));
        }
        return multimap;
    }
}
