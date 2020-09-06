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
import hellfirepvp.astralsorcery.common.container.factory.ContainerTomeProvider;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchHelper;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import hellfirepvp.astralsorcery.common.item.base.PerkExperienceRevealer;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;
import net.minecraft.block.BlockState;
import net.minecraft.block.LecternBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.LogicalSide;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemTome
 * Created by HellFirePvP
 * Date: 09.08.2019 / 21:12
 */
public class ItemTome extends Item implements PerkExperienceRevealer {

    public ItemTome() {
        super(new Properties()
                .maxStackSize(1)
                .group(CommonProxy.ITEM_GROUP_AS));
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        if (world.isRemote() && !player.isSneaking()) {
            AstralSorcery.getProxy().openGui(player, GuiType.TOME);
        } else if (!world.isRemote() && player.isSneaking() && hand == Hand.MAIN_HAND && player instanceof ServerPlayerEntity) {
            new ContainerTomeProvider(player.getHeldItem(hand), player.inventory.currentItem)
                    .openFor((ServerPlayerEntity) player);
        }
        return ActionResult.resultSuccess(player.getHeldItem(hand));
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        World world = context.getWorld();
        BlockState blockstate = world.getBlockState(context.getPos());
        if (blockstate.getBlock() instanceof LecternBlock) {
            return LecternBlock.tryPlaceBook(world, context.getPos(), blockstate, context.getItem()) ? ActionResultType.SUCCESS : ActionResultType.PASS;
        } else {
            return ActionResultType.PASS;
        }
    }

    public static IInventory getTomeStorage(ItemStack stack, PlayerEntity player) {
        Inventory inventory = new Inventory(27);
        getStoredConstellations(stack, player).stream().map(cst -> {
            ItemStack cstPaper = new ItemStack(ItemsAS.CONSTELLATION_PAPER);
            if (cstPaper.getItem() instanceof ConstellationBaseItem) {
                ((ConstellationBaseItem) cstPaper.getItem()).setConstellation(cstPaper, cst);
            }
            return cstPaper;
        }).forEach(inventory::addItem);
        return inventory;
    }

    public static List<IConstellation> getStoredConstellations(ItemStack stack, PlayerEntity player) {
        LinkedList<IConstellation> out = new LinkedList<>();

        PlayerProgress prog = ResearchHelper.getProgress(player, player.getEntityWorld().isRemote() ? LogicalSide.CLIENT : LogicalSide.SERVER);
        if (prog.isValid()) {
            prog.getStoredConstellationPapers().stream()
                    .map(ConstellationRegistry::getConstellation)
                    .filter(Objects::nonNull)
                    .forEach(out::add);
        }

        //Legacy, copy constellations from tome items into player-progress
        //TODO remove this after alpha 1.15.2
        CompoundNBT cmp = NBTHelper.getPersistentData(stack);
        if (cmp.contains("constellations", Constants.NBT.TAG_LIST)) {
            ListNBT constellationPapers = cmp.getList("constellations", Constants.NBT.TAG_STRING);
            for (int i = 0; i < constellationPapers.size(); i++) {
                IConstellation c = RegistriesAS.REGISTRY_CONSTELLATIONS.getValue(new ResourceLocation(constellationPapers.getString(i)));
                if (c != null) {
                    out.add(c);
                }
            }
            ResearchManager.updateConstellationPapers(out, player);
            cmp.remove("constellations");
        }

        return out;
    }
}
