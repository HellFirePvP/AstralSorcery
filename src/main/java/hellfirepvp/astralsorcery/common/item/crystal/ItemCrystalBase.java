/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.crystal;

import hellfirepvp.astralsorcery.common.registry.RegistryItems;
import hellfirepvp.astralsorcery.common.util.crystal.CrystalProperties;
import hellfirepvp.astralsorcery.common.util.crystal.CrystalPropertyItem;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemCrystalBase
 * Created by HellFirePvP
 * Date: 21.07.2019 / 12:58
 */
public abstract class ItemCrystalBase extends Item implements CrystalPropertyItem {

    public ItemCrystalBase(Properties prop) {
        super(prop
                .maxStackSize(1)
                .maxDamage(0));
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean isSelected) {
        CrystalProperties prop = getProperties(stack);
        if (prop == null) {
            this.applyProperties(stack, this.generateRandom(random));
        } else {
            if (prop.getFracturation() >= 100) {
                stack.setCount(0);
                entity.playSound(SoundEvents.ENTITY_ITEM_BREAK, 0.5F, random.nextFloat() * 0.2F + 0.8F);
            }
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> toolTip, ITooltipFlag flag) {
        super.addInformation(stack, world, toolTip, flag);
        this.appendPropertyTooltip(stack, toolTip);
    }

    public abstract Item getAttunedVariant();

    public abstract CrystalProperties generateRandom(Random rand);

    @Nullable
    @Override
    public CrystalProperties getProperties(ItemStack stack) {
        return CrystalProperties.getCrystalProperties(stack);
    }

    public void applyProperties(ItemStack stack, CrystalProperties properties) {
        CrystalProperties.applyCrystalProperties(stack, properties);
    }

    @OnlyIn(Dist.CLIENT)
    public Optional<Boolean> appendPropertyTooltip(ItemStack stack, List<ITextComponent> tooltip) {
        return CrystalProperties.addPropertyTooltip(CrystalProperties.getCrystalProperties(stack), tooltip, getMaxPropertySize(stack));
    }

}
