package hellfirepvp.astralsorcery.common.tile;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.client.ClientScheduler;
import hellfirepvp.astralsorcery.client.effect.EffectHandler;
import hellfirepvp.astralsorcery.client.effect.controller.OrbitalEffectController;
import hellfirepvp.astralsorcery.client.effect.controller.OrbitalPropertiesInfuser;
import hellfirepvp.astralsorcery.common.constellation.IMajorConstellation;
import hellfirepvp.astralsorcery.common.crafting.IGatedRecipe;
import hellfirepvp.astralsorcery.common.crafting.infusion.AbstractInfusionRecipe;
import hellfirepvp.astralsorcery.common.crafting.infusion.ActiveInfusionTask;
import hellfirepvp.astralsorcery.common.crafting.infusion.InfusionRecipeRegistry;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import hellfirepvp.astralsorcery.common.item.base.IWandInteract;
import hellfirepvp.astralsorcery.common.item.tool.ItemWand;
import hellfirepvp.astralsorcery.common.lib.MultiBlockArrays;
import hellfirepvp.astralsorcery.common.starlight.transmission.ITransmissionReceiver;
import hellfirepvp.astralsorcery.common.starlight.transmission.base.SimpleTransmissionReceiver;
import hellfirepvp.astralsorcery.common.starlight.transmission.registry.TransmissionClassRegistry;
import hellfirepvp.astralsorcery.common.tile.base.TileReceiverBase;
import hellfirepvp.astralsorcery.common.util.ItemUtils;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
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
import java.util.UUID;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TileStarlightInfuser
 * Created by HellFirePvP
 * Date: 11.12.2016 / 17:11
 */
public class TileStarlightInfuser extends TileReceiverBase implements IWandInteract {

    private static final BlockPos[] offsetsLiquidStarlight = new BlockPos[] {
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

    private ItemStack stack = null;
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
        if(out != null) {
            out = ItemUtils.copyStackWithSize(out, out.stackSize);
        }
        this.stack = null;
        if(out != null) {
            if(out.stackSize > 0) {
                ItemUtils.dropItem(world, pos.getX() + 0.5, pos.getY() + 1.3, pos.getZ() + 0.5, out).setNoDespawn();
            }
        }
        for (BlockPos offset : offsetsLiquidStarlight) {
            if(world.rand.nextFloat() < craftingTask.getRecipeToCraft().getLiquidStarlightConsumptionChance()) {
                world.setBlockToAir(getPos().add(offset));
            }
        }
        craftingTask.getRecipeToCraft().onCraftServerFinish(this, rand);
        ResearchManager.informCraftingInfusionCompletion(this, craftingTask);
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

        if(this.stack != null) {
            NBTHelper.setStack(compound, "stack", stack);
        }
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
        return "tile.BlockStarlightInfuser.name";
    }

    private void receiveStarlight(IMajorConstellation type, double amount) {}

    @Override
    public ITransmissionReceiver provideEndpoint(BlockPos at) {
        return new TransmissionReceiverStarlightInfuser(at);
    }

    public void onInteract(EntityPlayer playerIn, EnumHand heldHand, @Nullable ItemStack heldItem) {
        if(!playerIn.getEntityWorld().isRemote) {
            if(playerIn.isSneaking()) {
                if(stack != null) {
                    ItemUtils.dropItemNaturally(playerIn.getEntityWorld(),
                            getPos().getX() + 0.5,
                            getPos().getY() + 0.9,
                            getPos().getZ() + 0.5,
                            stack);
                    stack = null;
                    world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.5F, world.rand.nextFloat() * 0.2F + 0.8F);
                    markForUpdate();
                }
            } else {
                if(heldItem != null) {
                    if(stack == null) {
                        heldItem.stackSize--;
                        this.stack = ItemUtils.copyStackWithSize(heldItem, 1);
                        if(heldItem.stackSize <= 0) {
                            playerIn.setHeldItem(heldHand, null);
                        }
                        world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.5F, world.rand.nextFloat() * 0.2F + 0.8F);
                        markForUpdate();
                    }/* else if(heldItem.getItem() instanceof ItemWand) {
                        findRecipe(playerIn);
                    }*/
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

        public TransmissionReceiverStarlightInfuser(@Nonnull BlockPos thisPos) {
            super(thisPos);
        }

        @Override
        public void onStarlightReceive(World world, boolean isChunkLoaded, IMajorConstellation type, double amount) {
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
