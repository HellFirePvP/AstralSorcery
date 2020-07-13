/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.CommonProxy;
import hellfirepvp.astralsorcery.common.GuiType;
import hellfirepvp.astralsorcery.common.constellation.ConstellationBaseItem;
import hellfirepvp.astralsorcery.common.constellation.ConstellationRegistry;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchHelper;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import hellfirepvp.astralsorcery.common.entity.item.EntityItemExplosionResistant;
import hellfirepvp.astralsorcery.common.item.base.render.ItemDynamicColor;
import hellfirepvp.astralsorcery.common.lib.EntityTypesAS;
import hellfirepvp.astralsorcery.common.lib.SoundsAS;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;
import hellfirepvp.astralsorcery.common.util.sound.SoundHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.LogicalSide;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemConstellationPaper
 * Created by HellFirePvP
 * Date: 21.07.2019 / 14:47
 */
public class ItemConstellationPaper extends Item implements ItemDynamicColor, ConstellationBaseItem {

    public ItemConstellationPaper() {
        super(new Properties()
                .maxStackSize(1)
                .group(CommonProxy.ITEM_GROUP_AS_PAPERS));
    }

    @Override
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
        if (this.isInGroup(group)) {
            items.add(new ItemStack(this, 1));

            for (IConstellation c : ConstellationRegistry.getAllConstellations()) {
                ItemStack cPaper = new ItemStack(this, 1);
                setConstellation(cPaper, c);
                items.add(cPaper);
            }
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> toolTip, ITooltipFlag flag) {
        IConstellation c = getConstellation(stack);
        if (c != null && c.canDiscover(Minecraft.getInstance().player, ResearchHelper.getClientProgress())) {
            toolTip.add(c.getConstellationName().applyTextStyle(TextFormatting.BLUE));
        } else {
            toolTip.add(new TranslationTextComponent("astralsorcery.misc.noinformation").applyTextStyle(TextFormatting.GRAY));
        }
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        ItemStack held = player.getHeldItem(hand);
        if (held.isEmpty()) {
            return ActionResult.resultSuccess(held);
        }
        if (world.isRemote() && getConstellation(held) != null) {
            SoundHelper.playSoundClient(SoundsAS.GUI_JOURNAL_PAGE, 1F, 1F);
            AstralSorcery.getProxy().openGui(player, GuiType.CONSTELLATION_PAPER, getConstellation(held));
        }
        return ActionResult.resultSuccess(held);
    }

    @Override
    public boolean hasCustomEntity(ItemStack stack) {
        return true;
    }

    @Nullable
    @Override
    public Entity createEntity(World world, Entity location, ItemStack itemstack) {
        EntityItemExplosionResistant res = new EntityItemExplosionResistant(EntityTypesAS.ITEM_EXPLOSION_RESISTANT, world, location.getPosX(), location.getPosY(), location.getPosZ(), itemstack);
        res.setPickupDelay(20);
        res.setMotion(location.getMotion());
        if (location instanceof ItemEntity) {
            res.setThrowerId(((ItemEntity) location).getThrowerId());
            res.setOwnerId(((ItemEntity) location).getOwnerId());
        }
        if (itemstack.getItem() instanceof ItemConstellationPaper) {
            IConstellation cst = getConstellation(itemstack);
            if (cst != null) {
                res.applyColor(cst.getConstellationColor());
            }
        }
        return res;
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean isSelected) {
        if (world.isRemote || !(entity instanceof PlayerEntity)) {
            return;
        }

        IConstellation cst = getConstellation(stack);
        if (cst == null) {
            PlayerProgress progress = ResearchHelper.getProgress((PlayerEntity) entity, LogicalSide.SERVER);

            List<IConstellation> constellations = new ArrayList<>();
            for (IConstellation c : ConstellationRegistry.getAllConstellations()) {
                if (c.canDiscover((PlayerEntity) entity, progress)) {
                    constellations.add(c);
                }
            }

            for (ResourceLocation strConstellation : progress.getKnownConstellations()) {
                IConstellation c = ConstellationRegistry.getConstellation(strConstellation);
                if (c != null) {
                    constellations.remove(c);
                }
            }
            for (ResourceLocation strConstellation : progress.getSeenConstellations()) {
                IConstellation c = ConstellationRegistry.getConstellation(strConstellation);
                if (c != null) {
                    constellations.remove(c);
                }
            }

            IConstellation constellation = MiscUtils.getRandomEntry(constellations, world.rand);
            if (constellation != null) {
                setConstellation(stack, constellation);
            }
        }


        cst = getConstellation(stack);
        if (cst != null) {
            PlayerProgress progress = ResearchHelper.getProgress((PlayerEntity) entity, LogicalSide.SERVER);

            boolean has = false;
            for (ResourceLocation strConstellation : progress.getSeenConstellations()) {
                IConstellation c = ConstellationRegistry.getConstellation(strConstellation);
                if (c != null && c.equals(cst)) {
                    has = true;
                    break;
                }
            }
            if (!has) {
                if (cst.canDiscover((PlayerEntity) entity, progress) && ResearchManager.memorizeConstellation(cst, (PlayerEntity) entity)) {
                    entity.sendMessage(
                            new TranslationTextComponent("astralsorcery.progress.constellation.seen.chat",
                                    cst.getConstellationName().applyTextStyle(TextFormatting.GRAY))
                                    .applyTextStyle(TextFormatting.BLUE));
                    if (ResearchHelper.getClientProgress().getSeenConstellations().size() == 1) {
                        entity.sendMessage(
                                new TranslationTextComponent("astralsorcery.progress.constellation.seen.track")
                                        .applyTextStyle(TextFormatting.BLUE));
                    }
                }
            }
        }

        super.inventoryTick(stack, world, entity, slot, isSelected);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public int getColor(ItemStack stack, int tintIndex) {
        if (tintIndex != 1) {
            return 0xFFFFFFFF;
        }
        IConstellation c = getConstellation(stack);
        if (c != null) {
            if (ResearchHelper.getClientProgress().hasConstellationDiscovered(c)) {
                return 0xFF000000 | c.getConstellationColor().getRGB();
            }
        }
        return 0xFF595959;
    }

    @Nullable
    public IConstellation getConstellation(ItemStack stack) {
        return IConstellation.readFromNBT(NBTHelper.getPersistentData(stack));
    }

    public boolean setConstellation(ItemStack stack, @Nullable IConstellation constellation) {
        constellation.writeToNBT(NBTHelper.getPersistentData(stack));
        return true;
    }
}
