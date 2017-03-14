/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.client.ClientScheduler;
import hellfirepvp.astralsorcery.client.effect.EffectHandler;
import hellfirepvp.astralsorcery.client.effect.controller.OrbitalEffectController;
import hellfirepvp.astralsorcery.client.effect.controller.OrbitalPropertiesInfuser;
import hellfirepvp.astralsorcery.common.constellation.IWeakConstellation;
import hellfirepvp.astralsorcery.common.crafting.IGatedRecipe;
import hellfirepvp.astralsorcery.common.crafting.infusion.AbstractInfusionRecipe;
import hellfirepvp.astralsorcery.common.crafting.infusion.ActiveInfusionTask;
import hellfirepvp.astralsorcery.common.crafting.infusion.InfusionRecipeRegistry;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import hellfirepvp.astralsorcery.common.entities.EntityFlare;
import hellfirepvp.astralsorcery.common.item.base.IWandInteract;
import hellfirepvp.astralsorcery.common.lib.MultiBlockArrays;
import hellfirepvp.astralsorcery.common.lib.Sounds;
import hellfirepvp.astralsorcery.common.starlight.transmission.ITransmissionReceiver;
import hellfirepvp.astralsorcery.common.starlight.transmission.base.SimpleTransmissionReceiver;
import hellfirepvp.astralsorcery.common.starlight.transmission.registry.TransmissionClassRegistry;
import hellfirepvp.astralsorcery.common.tile.base.TileReceiverBase;
import hellfirepvp.astralsorcery.common.util.ItemUtils;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.SoundHelper;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TileStarlightInfuser
 * Created by HellFirePvP
 * Date: 11.12.2016 / 17:11
 */
public class TileStarlightInfuser extends TileReceiverBase implements IWandInteract {

    public static final BlockPos[] offsetsLiquidStarlight = new BlockPos[] {
            new BlockPos(-2, -1, -1),
            new BlockPos(-2, -1,  0),
            new BlockPos(-2, -1,  1),
            new BlockPos( 2, -1, -1),
            new BlockPos( 2, -1,  0),
            new BlockPos( 2, -1,  1),
            new BlockPos(-1, -1, -2),
            new BlockPos( 0, -1, -2),
            new BlockPos( 1, -1, -2),
            new BlockPos(-1, -1,  2),
            new BlockPos( 0, -1,  2),
            new BlockPos( 1, -1,  2)};

    private ActiveInfusionTask craftingTask = null;

    private Object clientOrbitalCrafting = null;
    private Object clientOrbitalCraftingMirror = null;

    private ItemStack stack = ItemStack.EMPTY;
    private boolean hasMultiblock = false, doesSeeSky = false;

    @Override
    public void update() {
        super.update();

        if((ticksExisted & 15) == 0) {
            updateSkyState();
        }

        if((ticksExisted & 31) == 0) {
            updateMultiblockState();
        }

        if(!world.isRemote) {
            if(doTryCraft()) {
                markForUpdate();
            }
        } else {
            if(craftingTask != null) {
                doClientCraftEffects();
            }
        }
    }

    @SideOnly(Side.CLIENT)
    private void doClientCraftEffects() {
        craftingTask.getRecipeToCraft().onCraftClientTick(this, ClientScheduler.getClientTick(), rand);

        if(clientOrbitalCrafting == null || ((OrbitalEffectController) clientOrbitalCrafting).isRemoved()) {
            OrbitalPropertiesInfuser prop = new OrbitalPropertiesInfuser(this, false);
            OrbitalEffectController ctrl = EffectHandler.getInstance().orbital(prop, prop, null);
            ctrl.setOffset(new Vector3(this).add(0.5, 0, 0.5));
            ctrl.setOrbitAxis(Vector3.RotAxis.Y_AXIS);
            ctrl.setOrbitRadius(2);
            ctrl.setTicksPerRotation(80);
            clientOrbitalCrafting = ctrl;
        }
        if(clientOrbitalCraftingMirror == null || ((OrbitalEffectController) clientOrbitalCraftingMirror).isRemoved()) {
            OrbitalPropertiesInfuser prop = new OrbitalPropertiesInfuser(this, true);
            OrbitalEffectController ctrl = EffectHandler.getInstance().orbital(prop, prop, null);
            ctrl.setOffset(new Vector3(this).add(0.5, 0, 0.5));
            ctrl.setOrbitAxis(Vector3.RotAxis.Y_AXIS);
            ctrl.setTicksPerRotation(80);
            ctrl.setTickOffset(ctrl.getMaxAge() / 2);
            ctrl.setOrbitRadius(2);
            clientOrbitalCraftingMirror = ctrl;
        }
    }

    private boolean doTryCraft() {
        if(craftingTask == null) return false;

        AbstractInfusionRecipe altarRecipe = craftingTask.getRecipeToCraft();
        if(!altarRecipe.matches(this)) {
            abortCrafting();
            return true;
        }
        if(craftingTask.isFinished()) {
            finishCrafting();
            return true;
        }
        craftingTask.tick(this);
        craftingTask.getRecipeToCraft().onCraftServerTick(this, craftingTask.getTicksCrafting(), rand);
        return false;
    }

    private void finishCrafting() {
        if(craftingTask == null) return;

        AbstractInfusionRecipe altarRecipe = craftingTask.getRecipeToCraft();
        ItemStack out = altarRecipe.getOutput(this);
        if(!out.isEmpty()) {
            out = ItemUtils.copyStackWithSize(out, out.getCount());
        }

        if(altarRecipe.mayDeleteInput(this)) {
            this.stack = ItemStack.EMPTY;
        } else {
            altarRecipe.handleInputDecrement(this);
        }

        if(!out.isEmpty()) {
            if(out.getCount() > 0) {
                ItemUtils.dropItem(world, pos.getX() + 0.5, pos.getY() + 1.3, pos.getZ() + 0.5, out).setNoDespawn();
            }
        }
        int size = offsetsLiquidStarlight.length;
        List<Integer> indexes = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            indexes.add(i);
        }
        Collections.shuffle(indexes);
        while (size > 0) {
            BlockPos offset = offsetsLiquidStarlight[indexes.get(size - 1)];
            size--;
            if(world.rand.nextFloat() < craftingTask.getRecipeToCraft().getLiquidStarlightConsumptionChance()) {
                world.setBlockToAir(getPos().add(offset));
                EntityFlare.spawnAmbient(world, new Vector3(this).add(-3 + rand.nextFloat() * 7, 0.6, -3 + rand.nextFloat() * 7));
                if(!altarRecipe.doesConsumeMultiple()) break;
            }
        }
        craftingTask.getRecipeToCraft().onCraftServerFinish(this, rand);
        ResearchManager.informCraftingInfusionCompletion(this, craftingTask);
        SoundHelper.playSoundAround(Sounds.craftFinish, world, getPos(), 1F, 1.7F);
        EntityFlare.spawnAmbient(world, new Vector3(this).add(-3 + rand.nextFloat() * 7, 0.6, -3 + rand.nextFloat() * 7));
        craftingTask = null;
    }

    private void updateMultiblockState() {
        boolean found = MultiBlockArrays.patternStarlightInfuser.matches(world, getPos());
        boolean update = hasMultiblock != found;
        this.hasMultiblock = found;
        if(update) {
            markForUpdate();
        }
    }

    private void updateSkyState() {
        boolean seesSky = world.canSeeSky(getPos());
        boolean update = doesSeeSky != seesSky;
        this.doesSeeSky = seesSky;
        if(update) {
            markForUpdate();
        }
    }

    @Nonnull
    public ItemStack getInputStack() {
        return stack;
    }

    public boolean hasMultiblock() {
        return hasMultiblock;
    }

    public boolean doesSeeSky() {
        return doesSeeSky;
    }

    private void findRecipe(EntityPlayer crafter) {
        if(craftingTask != null) return;

        AbstractInfusionRecipe recipe = InfusionRecipeRegistry.findMatchingRecipe(this);
        if(recipe instanceof IGatedRecipe) {
            if(!((IGatedRecipe) recipe).hasProgressionServer(crafter)) return;
        }
        if(recipe != null) {
            this.craftingTask = new ActiveInfusionTask(recipe, crafter.getUniqueID());
            markForUpdate();
        }
    }

    public void abortCrafting() {
        this.craftingTask = null;
        markForUpdate();
    }

    public ActiveInfusionTask getCraftingTask() {
        return craftingTask;
    }

    @SideOnly(Side.CLIENT)
    public OrbitalEffectController getClientOrbitalCrafting() {
        if(clientOrbitalCrafting == null) return null;
        return (OrbitalEffectController) clientOrbitalCrafting;
    }

    @SideOnly(Side.CLIENT)
    public OrbitalEffectController getClientOrbitalCraftingMirror() {
        if(clientOrbitalCraftingMirror == null) return null;
        return (OrbitalEffectController) clientOrbitalCraftingMirror;
    }

    public boolean canCraft() {
        return hasMultiblock() && !isInvalid() && doesSeeSky();
    }

    @Override
    public void readCustomNBT(NBTTagCompound compound) {
        super.readCustomNBT(compound);

        this.stack = NBTHelper.getStack(compound, "stack");
        this.hasMultiblock = compound.getBoolean("mbState");
        this.doesSeeSky = compound.getBoolean("seesSky");

        boolean wasNull = this.craftingTask == null;
        this.craftingTask = null;
        if(compound.hasKey("recipeId") && compound.hasKey("recipeTick")) {
            int recipeId = compound.getInteger("recipeId");
            AbstractInfusionRecipe recipe = InfusionRecipeRegistry.getRecipe(recipeId);
            if(recipe == null) {
                AstralSorcery.log.info("Recipe with unknown/invalid ID found: " + recipeId + " for Starlight Infuser at " + getPos());
            } else {
                UUID uuidCraft = compound.getUniqueId("crafterUUID");
                int tick = compound.getInteger("recipeTick");
                this.craftingTask = new ActiveInfusionTask(recipe, uuidCraft);
                this.craftingTask.forceTick(tick);
            }
        }
        if(!wasNull && this.craftingTask == null) {
            clientOrbitalCrafting = null;
        }
    }

    @Override
    public void writeCustomNBT(NBTTagCompound compound) {
        super.writeCustomNBT(compound);

        NBTHelper.setStack(compound, "stack", stack);
        compound.setBoolean("mbState", hasMultiblock);
        compound.setBoolean("seesSky", doesSeeSky);

        if(craftingTask != null) {
            compound.setInteger("recipeId", craftingTask.getRecipeToCraft().getUniqueRecipeId());
            compound.setInteger("recipeTick", craftingTask.getTicksCrafting());
            compound.setUniqueId("crafterUUID", craftingTask.getPlayerCraftingUUID());
        }
    }

    @Nullable
    @Override
    public String getUnLocalizedDisplayName() {
        return "tile.blockstarlightinfuser.name";
    }

    private void receiveStarlight(IWeakConstellation type, double amount) {}

    @Override
    @Nonnull
    public ITransmissionReceiver provideEndpoint(BlockPos at) {
        return new TransmissionReceiverStarlightInfuser(at);
    }

    public void onInteract(EntityPlayer playerIn, EnumHand heldHand, ItemStack heldItem) {
        if(!playerIn.getEntityWorld().isRemote) {
            if(playerIn.isSneaking()) {
                if(!stack.isEmpty()) {
                    ItemUtils.dropItemNaturally(playerIn.getEntityWorld(),
                            getPos().getX() + 0.5,
                            getPos().getY() + 1,
                            getPos().getZ() + 0.5,
                            stack);
                    stack = ItemStack.EMPTY;
                    world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.5F, world.rand.nextFloat() * 0.2F + 0.8F);
                    markForUpdate();
                }
            } else {
                if(!heldItem.isEmpty()) {
                    if(stack.isEmpty()) {
                        this.stack = ItemUtils.copyStackWithSize(heldItem, 1);
                        if(!playerIn.isCreative()) {
                            heldItem.setCount(heldItem.getCount() - 1);
                        }
                        if(heldItem.getCount() <= 0) {
                            playerIn.setHeldItem(heldHand, ItemStack.EMPTY);
                        }
                        world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.5F, world.rand.nextFloat() * 0.2F + 0.8F);
                        markForUpdate();
                    }
                }
            }
        }
    }

    @Override
    public void onInteract(World world, BlockPos pos, EntityPlayer player, EnumFacing side, boolean sneak) {
        if(!world.isRemote) {
            findRecipe(player);
        }
    }

    public static class TransmissionReceiverStarlightInfuser extends SimpleTransmissionReceiver {

        public TransmissionReceiverStarlightInfuser(BlockPos thisPos) {
            super(thisPos);
        }

        @Override
        public void onStarlightReceive(World world, boolean isChunkLoaded, IWeakConstellation type, double amount) {
            if(isChunkLoaded) {
                TileStarlightInfuser ta = MiscUtils.getTileAt(world, getPos(), TileStarlightInfuser.class, false);
                if(ta != null) {
                    ta.receiveStarlight(type, amount);
                }
            }
        }

        @Override
        public TransmissionClassRegistry.TransmissionProvider getProvider() {
            return new StarlightInfuserReceiverProvider();
        }

    }

    public static class StarlightInfuserReceiverProvider implements TransmissionClassRegistry.TransmissionProvider {

        @Override
        public TransmissionReceiverStarlightInfuser provideEmptyNode() {
            return new TransmissionReceiverStarlightInfuser(null);
        }

        @Override
        public String getIdentifier() {
            return AstralSorcery.MODID + ":TransmissionReceiverStarlightInfuser";
        }

    }
}
