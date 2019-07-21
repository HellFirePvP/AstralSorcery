/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item;

import hellfirepvp.astralsorcery.common.constellation.ConstellationRegistry;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchHelper;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import hellfirepvp.astralsorcery.common.item.base.render.ItemDynamicColor;
import hellfirepvp.astralsorcery.common.registry.RegistryItems;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.*;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

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
public class ItemConstellationPaper extends Item implements ItemDynamicColor {

    public ItemConstellationPaper() {
        super(new Properties()
                .maxStackSize(1)
                .group(RegistryItems.ITEM_GROUP_AS_PAPERS));
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
            toolTip.add(new StringTextComponent(TextFormatting.BLUE + I18n.format(c.getUnlocalizedName())));
        } else {
            toolTip.add(new StringTextComponent(TextFormatting.GRAY + I18n.format("constellation.noInformation")));
        }
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean isSelected) {
        if (world.isRemote || !(entity instanceof PlayerEntity)) {
            return;
        }

        IConstellation cst = getConstellation(stack);
        if (cst == null) {
            PlayerProgress progress = ResearchHelper.getProgress((PlayerEntity) entity, Dist.DEDICATED_SERVER);

            List<IConstellation> constellations = new ArrayList<>();
            for (IConstellation c : ConstellationRegistry.getAllConstellations()) {
                if (c.canDiscover((PlayerEntity) entity, progress)) {
                    constellations.add(c);
                }
            }

            for (ResourceLocation strConstellation : progress.getKnownConstellations()) {
                IConstellation c = ConstellationRegistry.getConstellation(strConstellation);
                if(c != null) {
                    constellations.remove(c);
                }
            }
            for (ResourceLocation strConstellation : progress.getSeenConstellations()) {
                IConstellation c = ConstellationRegistry.getConstellation(strConstellation);
                if(c != null) {
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
            PlayerProgress progress = ResearchHelper.getProgress((PlayerEntity) entity, Dist.DEDICATED_SERVER);

            boolean has = false;
            for (ResourceLocation strConstellation : progress.getSeenConstellations()) {
                IConstellation c = ConstellationRegistry.getConstellation(strConstellation);
                if (c != null && c.equals(cst)) {
                    has = true;
                    break;
                }
            }
            if (!has) {
                if (ResearchManager.memorizeConstellation(cst, (PlayerEntity) entity)) {
                    entity.sendMessage(
                            new TranslationTextComponent("progress.seen.constellation.chat",
                                    new TranslationTextComponent(cst.getUnlocalizedName())
                                            .setStyle(new Style().setColor(TextFormatting.GRAY)))
                                    .setStyle(new Style().setColor(TextFormatting.BLUE)));
                    if (ResearchHelper.getClientProgress().getSeenConstellations().size() == 1) {
                        entity.sendMessage(
                                new TranslationTextComponent("progress.seen.constellation.first.chat")
                                        .setStyle(new Style().setColor(TextFormatting.BLUE)));
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

    public static IConstellation getConstellation(ItemStack stack) {
        Item i = stack.getItem();
        if (!(i instanceof ItemConstellationPaper)) {
            return null;
        }
        return IConstellation.readFromNBT(NBTHelper.getPersistentData(stack));
    }

    public static void setConstellation(ItemStack stack, IConstellation constellation) {
        Item i = stack.getItem();
        if (!(i instanceof ItemConstellationPaper)) {
            return;
        }
        constellation.writeToNBT(NBTHelper.getPersistentData(stack));
    }
}
