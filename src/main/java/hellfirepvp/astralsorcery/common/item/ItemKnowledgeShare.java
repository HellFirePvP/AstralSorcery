/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item;

import hellfirepvp.astralsorcery.common.CommonProxy;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ProgressionTier;
import hellfirepvp.astralsorcery.common.data.research.ResearchHelper;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.play.server.PktProgressionUpdate;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.*;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.LogicalSide;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemKnowledgeShare
 * Created by HellFirePvP
 * Date: 16.08.2019 / 20:10
 */
public class ItemKnowledgeShare extends Item {

    public ItemKnowledgeShare() {
        super(new Properties()
                .maxStackSize(1)
                .group(CommonProxy.ITEM_GROUP_AS));

        this.addPropertyOverride(new ResourceLocation("written"),
                (stack, world, entity) -> isCreative(stack) || getKnowledge(stack) != null ? 1 : 0);
    }

    @Override
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
        if (this.isInGroup(group)) {
            items.add(new ItemStack(this));

            ItemStack creative = new ItemStack(this);
            setCreative(creative);
            items.add(creative);
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
        if (isCreative(stack)) {
            tooltip.add(new TranslationTextComponent("astralsorcery.misc.knowledge.inscribed.creative").setStyle(new Style().setColor(TextFormatting.LIGHT_PURPLE)));
            return;
        }
        if (getKnowledge(stack) == null) {
            tooltip.add(new TranslationTextComponent("astralsorcery.misc.knowledge.missing").setStyle(new Style().setColor(TextFormatting.GRAY)));
        } else {
            String name = getKnowledgeOwnerName(stack);
            if (name != null) {
                tooltip.add(new TranslationTextComponent("astralsorcery.misc.knowledge.inscribed", name).setStyle(new Style().setColor(TextFormatting.BLUE)));
            }
        }
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        ItemStack held = player.getHeldItem(hand);
        if (held.isEmpty() || world.isRemote() || !(held.getItem() instanceof ItemKnowledgeShare)) {
            return ActionResult.resultSuccess(held);
        }
        if (!isCreative(held) && (player.isSneaking() || getKnowledge(held) == null)) {
            tryInscribeKnowledge(held, player);
        } else {
            tryGiveKnowledge(held, player);
        }
        return ActionResult.resultSuccess(held);
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        ItemStack stack = context.getItem();
        PlayerEntity player = context.getPlayer();
        if (stack.isEmpty() || player == null || context.getWorld().isRemote() || !(stack.getItem() instanceof ItemKnowledgeShare)) {
            return ActionResultType.SUCCESS;
        }
        if (!isCreative(stack) && (player.isSneaking() || getKnowledge(stack) == null)) {
            tryInscribeKnowledge(stack, player);
        } else {
            tryGiveKnowledge(stack, player);
        }
        return ActionResultType.SUCCESS;
    }

    private void tryGiveKnowledge(ItemStack stack, PlayerEntity player) {
        if (player instanceof ServerPlayerEntity && MiscUtils.isPlayerFakeMP((ServerPlayerEntity) player)) {
            return;
        }

        if (isCreative(stack)) {
            ResearchManager.forceMaximizeAll(player);
            return;
        }
        if (canInscribeKnowledge(stack, player)) return; //Means it's either empty or the player that has incsribed the knowledge is trying to use it.
        PlayerProgress progress = getKnowledge(stack);
        if (progress == null) return;
        ProgressionTier prev = progress.getTierReached();
        if (ResearchHelper.mergeApplyPlayerprogress(progress, player) && progress.getTierReached().isThisLater(prev)) {
            PktProgressionUpdate pkt = new PktProgressionUpdate(progress.getTierReached());
            PacketChannel.CHANNEL.sendToPlayer(player, pkt);
        }
    }

    private void tryInscribeKnowledge(ItemStack stack, PlayerEntity player) {
        if (canInscribeKnowledge(stack, player)) {
            setKnowledge(stack, player, ResearchHelper.getProgress(player, LogicalSide.SERVER));
        }
    }

    @Nullable
    public PlayerEntity getKnowledgeOwner(ItemStack stack, MinecraftServer server) {
        if (isCreative(stack)) return null;

        CompoundNBT compound = NBTHelper.getPersistentData(stack);
        if (!compound.hasUniqueId("knowledgeOwnerUUID")) {
            return null;
        }
        UUID owner = compound.getUniqueId("knowledgeOwnerUUID");
        return server.getPlayerList().getPlayerByUUID(owner);
    }

    @Nullable
    public String getKnowledgeOwnerName(ItemStack stack) {
        if (isCreative(stack)) return null;

        CompoundNBT compound = NBTHelper.getPersistentData(stack);
        if (!compound.contains("knowledgeOwnerName")) {
            return null;
        }
        return compound.getString("knowledgeOwnerName");
    }

    @Nullable
    public PlayerProgress getKnowledge(ItemStack stack) {
        if (isCreative(stack)) return null;

        CompoundNBT compound = NBTHelper.getPersistentData(stack);
        if (!compound.contains("knowledgeTag")) {
            return null;
        }
        CompoundNBT tag = compound.getCompound("knowledgeTag");
        try {
            PlayerProgress progress = new PlayerProgress();
            progress.loadKnowledge(tag);
            return progress;
        } catch (Exception ignored) {
            return null;
        }
    }

    public boolean canInscribeKnowledge(ItemStack stack, PlayerEntity player) {
        if (isCreative(stack)) return false;

        CompoundNBT compound = NBTHelper.getPersistentData(stack);
        if (!compound.hasUniqueId("knowledgeOwnerUUID")) {
            return true;
        }
        UUID owner = compound.getUniqueId("knowledgeOwnerUUID");
        return player.getUniqueID().equals(owner);
    }

    public void setKnowledge(ItemStack stack, PlayerEntity player, PlayerProgress progress) {
        if (isCreative(stack) || !progress.isValid()) return;

        CompoundNBT knowledge = new CompoundNBT();
        progress.storeKnowledge(knowledge);
        CompoundNBT compound = NBTHelper.getPersistentData(stack);
        compound.putString("knowledgeOwnerName", player.getDisplayName().getFormattedText());
        compound.putUniqueId("knowledgeOwnerUUID", player.getUniqueID());
        compound.put("knowledgeTag", knowledge);
    }

    public boolean isCreative(ItemStack stack) {
        CompoundNBT cmp = NBTHelper.getPersistentData(stack);
        if (!cmp.contains("creativeKnowledge")) {
            return false;
        }
        return cmp.getBoolean("creativeKnowledge");
    }

    private void setCreative(ItemStack stack) {
        NBTHelper.getPersistentData(stack).putBoolean("creativeKnowledge", true);
    }
}
