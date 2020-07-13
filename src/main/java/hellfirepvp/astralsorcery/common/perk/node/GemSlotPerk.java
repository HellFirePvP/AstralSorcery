/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk.node;

import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchHelper;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import hellfirepvp.astralsorcery.common.item.gem.ItemPerkGem;
import hellfirepvp.astralsorcery.common.perk.AbstractPerk;
import hellfirepvp.astralsorcery.common.perk.DynamicModifierHelper;
import hellfirepvp.astralsorcery.common.util.item.ItemUtils;
import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.LogicalSide;

import javax.annotation.Nullable;
import java.util.Collection;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: GemSlotPerk
 * Created by HellFirePvP
 * Date: 09.08.2019 / 07:23
 */
public interface GemSlotPerk {

    public static final String SOCKET_DATA_KEY = "socketedItem";

    default public boolean hasItem(PlayerEntity player, LogicalSide side) {
        return hasItem(player, side, null);
    }

    default public boolean hasItem(PlayerEntity player, LogicalSide side, @Nullable CompoundNBT data) {
        return !getContainedItem(player, side, data).isEmpty();
    }

    default public ItemStack getContainedItem(PlayerEntity player, LogicalSide side) {
        return getContainedItem(player, side, null);
    }

    default public ItemStack getContainedItem(PlayerEntity player, LogicalSide side, @Nullable CompoundNBT dataOvr) {
        if (!(this instanceof AbstractPerk)) {
            throw new UnsupportedOperationException("Cannot do perk-specific socketing logic on something that's not a perk!");
        }
        CompoundNBT data = dataOvr != null ? dataOvr : ((AbstractPerk) this).getPerkData(player, side);
        if (data == null) {
            return ItemStack.EMPTY;
        }

        ItemStack stack = NBTHelper.getStack(data, SOCKET_DATA_KEY);
        return stack != null ? stack : ItemStack.EMPTY;
    }

    default public boolean setContainedItem(PlayerEntity player, LogicalSide side, ItemStack stack) {
        return setContainedItem(player, side, null, stack);
    }

    default public boolean setContainedItem(PlayerEntity player, LogicalSide side, @Nullable CompoundNBT dataOvr, ItemStack stack) {
        if (!(this instanceof AbstractPerk)) {
            throw new UnsupportedOperationException("Cannot do perk-specific socketing logic on something that's not a perk!");
        }
        PlayerProgress prog = ResearchHelper.getProgress(player, side);
        if (!prog.hasPerkUnlocked((AbstractPerk) this)) {
            return false;
        }
        boolean updateData = dataOvr == null;
        CompoundNBT data = dataOvr != null ? dataOvr : ((AbstractPerk) this).getPerkData(player, side);
        if (data == null) {
            return false;
        }
        CompoundNBT prev = data.copy();

        if (stack.isEmpty()) {
            data.remove(SOCKET_DATA_KEY);
        } else {
            NBTHelper.setStack(data, SOCKET_DATA_KEY, stack);
        }

        if (updateData) {
            ResearchManager.setPerkData(player, (AbstractPerk) this, prev, data);
        }
        return true;
    }

    default public void dropItemToPlayer(PlayerEntity player) {
        dropItemToPlayer(player, null);
    }

    default public void dropItemToPlayer(PlayerEntity player, @Nullable CompoundNBT data) {
        if (!(this instanceof AbstractPerk)) {
            throw new UnsupportedOperationException("Cannot do perk-specific socketing logic on something that's not a perk!");
        }

        if (player.getEntityWorld().isRemote()) {
            return;
        }

        boolean updateData = data == null;
        if (updateData) {
            data = ((AbstractPerk) this).getPerkData(player, LogicalSide.SERVER);
        }
        if (data == null) {
            return;
        }
        CompoundNBT prev = data.copy();

        ItemStack contained = getContainedItem(player, LogicalSide.SERVER, data);
        if (!contained.isEmpty()) {
            if (!player.addItemStackToInventory(contained)) {
                ItemUtils.dropItem(player.getEntityWorld(), player.getPosX(), player.getPosY(), player.getPosZ(), contained);
            }
        }
        setContainedItem(player, LogicalSide.SERVER, data, ItemStack.EMPTY);

        if (updateData) {
            ResearchManager.setPerkData(player, (AbstractPerk) this, prev, data);
        }
    }

    @OnlyIn(Dist.CLIENT)
    default public void addTooltipInfo(Collection<ITextComponent> tooltip) {
        if (!(this instanceof AbstractPerk)) {
            return;
        }
        PlayerProgress prog = ResearchHelper.getClientProgress();
        if (!prog.isValid()) {
            return;
        }
        Style gray = new Style().setColor(TextFormatting.GRAY);

        ItemStack contained = getContainedItem(Minecraft.getInstance().player, LogicalSide.CLIENT);
        if (contained.isEmpty()) {
            tooltip.add(new TranslationTextComponent("perk.info.astralsorcery.gem.empty").setStyle(gray));
            if (prog.hasPerkEffect((AbstractPerk) this)) {
                tooltip.add(new TranslationTextComponent("perk.info.astralsorcery.gem.content.empty").setStyle(gray));

                boolean has = !ItemUtils.findItemsIndexedInPlayerInventory(Minecraft.getInstance().player,
                        s -> !s.isEmpty() && s.getItem() instanceof ItemPerkGem && !DynamicModifierHelper.getStaticModifiers(s).isEmpty()).isEmpty();
                if (!has) {
                    tooltip.add(new TranslationTextComponent("perk.info.astralsorcery.gem.content.empty.none")
                            .setStyle(new Style().setColor(TextFormatting.RED)));
                }
            }
        } else {
            tooltip.add(new TranslationTextComponent("perk.info.astralsorcery.gem.content.item", contained.getDisplayName())
                    .setStyle(gray));
            if (prog.hasPerkEffect((AbstractPerk) this)) {
                tooltip.add(new TranslationTextComponent("perk.info.astralsorcery.gem.remove").setStyle(gray));
            }
        }
    }

}
