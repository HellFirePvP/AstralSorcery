/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk.node.socket;

import hellfirepvp.astralsorcery.common.data.research.PlayerPerkData;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchHelper;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import hellfirepvp.astralsorcery.common.perk.AbstractPerk;
import hellfirepvp.astralsorcery.common.util.item.ItemUtils;
import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.LogicalSide;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: GemSocketPerk
 * Created by HellFirePvP
 * Date: 09.08.2019 / 07:23
 */
public interface GemSocketPerk {

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

    default public <T extends AbstractPerk & GemSocketPerk> boolean setContainedItem(PlayerEntity player, LogicalSide side, @Nullable CompoundNBT dataOvr, ItemStack stack) {
        if (!(this instanceof AbstractPerk)) {
            throw new UnsupportedOperationException("Cannot do perk-specific socketing logic on something that's not a perk!");
        }
        T thisPerk = (T) this;
        PlayerProgress prog = ResearchHelper.getProgress(player, side);
        if (!prog.getPerkData().hasPerkEffect(thisPerk)) {
            return false;
        }
        boolean updateData = dataOvr == null;
        CompoundNBT data = dataOvr != null ? dataOvr : ((AbstractPerk) this).getPerkData(player, side);
        if (data == null) {
            return false;
        }
        CompoundNBT prev = data.copy();

        if (stack.isEmpty()) {
            ItemStack existing = NBTHelper.getStack(data, SOCKET_DATA_KEY);
            if (!existing.isEmpty() && existing.getItem() instanceof GemSocketItem) {
                ((GemSocketItem) existing.getItem()).onExtract(existing, thisPerk, player, prog);
            }
            data.remove(SOCKET_DATA_KEY);
        } else {
            if (stack.getItem() instanceof GemSocketItem) {
                ((GemSocketItem) stack.getItem()).onInsert(stack, thisPerk, player, prog);
            }
            NBTHelper.setStack(data, SOCKET_DATA_KEY, stack);
        }

        if (updateData) {
            ResearchManager.setPerkData(player, thisPerk, prev, data);
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
    default public <T extends AbstractPerk & GemSocketPerk> void addTooltipInfo(Collection<IFormattableTextComponent> tooltip) {
        if (!(this instanceof AbstractPerk)) {
            return;
        }
        T thisPerk = (T) this;
        PlayerProgress prog = ResearchHelper.getClientProgress();
        if (!prog.isValid()) {
            return;
        }
        PlayerPerkData perkData = prog.getPerkData();

        ItemStack contained = getContainedItem(Minecraft.getInstance().player, LogicalSide.CLIENT);
        if (contained.isEmpty()) {
            tooltip.add(new TranslationTextComponent("perk.info.astralsorcery.gem.empty").mergeStyle(TextFormatting.GRAY));
            if (perkData.hasPerkEffect(thisPerk)) {
                tooltip.add(new TranslationTextComponent("perk.info.astralsorcery.gem.content.empty").mergeStyle(TextFormatting.GRAY));

                boolean has = !ItemUtils.findItemsIndexedInPlayerInventory(Minecraft.getInstance().player, stack -> {
                    if (stack.isEmpty() || !(stack.getItem() instanceof GemSocketItem)) {
                        return false;
                    }
                    GemSocketItem item = (GemSocketItem) stack.getItem();
                    return item.canBeInserted(stack, thisPerk, Minecraft.getInstance().player, ResearchHelper.getClientProgress(), LogicalSide.CLIENT);
                }).isEmpty();
                if (!has) {
                    tooltip.add(new TranslationTextComponent("perk.info.astralsorcery.gem.content.empty.none")
                            .mergeStyle(TextFormatting.RED));
                }
            }
        } else {
            if (contained.getItem() instanceof GemSocketItem) {
                GemSocketItem item = (GemSocketItem) contained.getItem();
                List<IFormattableTextComponent> additionalToolTip = new ArrayList<>();
                item.addTooltip(contained, thisPerk, additionalToolTip);
                if (!additionalToolTip.isEmpty()) {
                    tooltip.addAll(additionalToolTip);
                    tooltip.add(new StringTextComponent(""));
                }
            }

            tooltip.add(new TranslationTextComponent("perk.info.astralsorcery.gem.content.item", contained.getDisplayName())
                    .mergeStyle(TextFormatting.GRAY));
            if (perkData.hasPerkEffect(thisPerk)) {
                tooltip.add(new TranslationTextComponent("perk.info.astralsorcery.gem.remove").mergeStyle(TextFormatting.GRAY));
            }
        }
    }

}
