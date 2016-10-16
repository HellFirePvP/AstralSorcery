package hellfirepvp.astralsorcery.common.tile;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.client.ClientScheduler;
import hellfirepvp.astralsorcery.common.block.network.BlockAltar;
import hellfirepvp.astralsorcery.common.constellation.CelestialHandler;
import hellfirepvp.astralsorcery.common.constellation.Constellation;
import hellfirepvp.astralsorcery.common.crafting.altar.AbstractAltarRecipe;
import hellfirepvp.astralsorcery.common.crafting.altar.ActiveCraftingTask;
import hellfirepvp.astralsorcery.common.crafting.altar.AltarRecipeRegistry;
import hellfirepvp.astralsorcery.common.crafting.altar.recipes.AttenuationRecipe;
import hellfirepvp.astralsorcery.common.crafting.helper.ShapeMap;
import hellfirepvp.astralsorcery.common.crafting.helper.ShapedRecipeSlot;
import hellfirepvp.astralsorcery.common.item.base.IWandInteract;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.starlight.transmission.ITransmissionReceiver;
import hellfirepvp.astralsorcery.common.starlight.transmission.base.SimpleTransmissionReceiver;
import hellfirepvp.astralsorcery.common.starlight.transmission.registry.TransmissionClassRegistry;
import hellfirepvp.astralsorcery.common.tile.base.TileReceiverBaseInventory;
import hellfirepvp.astralsorcery.common.util.ItemUtils;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.struct.PatternBlockArray;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Random;
import java.util.UUID;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TileAltar
 * Created by HellFirePvP
 * Date: 11.05.2016 / 18:18
 */
public class TileAltar extends TileReceiverBaseInventory implements IWandInteract {

    private static final Random rand = new Random();

    private ActiveCraftingTask craftingTask = null;

    private AltarLevel level = AltarLevel.DISCOVERY;
    private boolean doesSeeSky = false;
    private boolean mbState = false;
    private int experience = 0;
    private int starlightStored = 0;

    public TileAltar() {
        super(13);
    }

    public TileAltar(AltarLevel level) {
        super(13);
        this.level = level;
    }

    private void receiveStarlight(Constellation type, double amount) {
        if(amount <= 0.001) return;

        starlightStored = Math.min(getMaxStarlightStorage(), (int) (starlightStored + (amount * 100D)));
        markForUpdate();
    }

    @Override
    public void update() {
        super.update();

        if((ticksExisted & 15) == 0) {
            updateSkyState(worldObj.canSeeSky(getPos()));
        }

        if(!worldObj.isRemote) {
            boolean needUpdate = false;

            needUpdate = starlightPassive(needUpdate);
            needUpdate = doTryCraft(needUpdate);

            if((ticksExisted & 15) == 0) {
                needUpdate = matchLevel(needUpdate);
            }

            if(needUpdate) {
                markForUpdate();
            }
        } else {
            if(getActiveCraftingTask() != null) {
                doCraftEffects();
            }
        }
    }

    @SideOnly(Side.CLIENT)
    private void doCraftEffects() {
        craftingTask.getRecipeToCraft().onCraftClientTick(this, ClientScheduler.getClientTick(), rand);
    }

    private boolean matchLevel(boolean needUpdate) {
        AltarLevel al = getAltarLevel();
        boolean matches = al.getMatcher().mbAllowsForCrafting(this);
        if(matches != mbState) {
            mbState = matches;
            needUpdate = true;
        }
        return needUpdate;
    }


    private boolean doTryCraft(boolean needUpdate) {
        if(craftingTask == null) return needUpdate;
        AbstractAltarRecipe altarRecipe = craftingTask.getRecipeToCraft();
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
        return needUpdate;
    }

    private void finishCrafting() {
        if(craftingTask == null) return; //Wtf

        AbstractAltarRecipe recipe = craftingTask.getRecipeToCraft();
        ShapeMap current = copyGetCurrentCraftingGrid();
        ItemStack out = recipe.getOutput(current); //Central item helps defining output - probably, eventually.
        if(out != null) {
            out = ItemUtils.copyStackWithSize(out, out.stackSize);
        }

        for (int i = 0; i < 9; i++) {
            ShapedRecipeSlot slot = ShapedRecipeSlot.getByRowColumnIndex(i % 3, i / 3);
            if(recipe.mayDecrement(this, slot)) {
                ItemUtils.decrStackInInventory(inv, i);
            }
        }

        for (AttenuationRecipe.AltarSlot slot : AttenuationRecipe.AltarSlot.values()) {
            int slotId = slot.slotId;
            if(recipe.mayDecrement(this, slot)) {
                ItemUtils.decrStackInInventory(inv, slotId);
            }
        }

        if(out != null) {
            for (EnumFacing dir : EnumFacing.VALUES) {
                if(dir == EnumFacing.UP) continue;
                IInventory i = MiscUtils.getTileAt(worldObj, pos.offset(dir), IInventory.class, true);
                if(i != null) {
                    if(ItemUtils.tryPlaceItemInInventory(out, i)) {
                        if(out.stackSize == 0) {
                            break;
                        }
                    }
                }
            }
            if(out.stackSize > 0) {
                ItemUtils.dropItem(worldObj, pos.getX() + 0.5, pos.getY() + 1.3, pos.getZ() + 0.5, out);
            }
        }

        addExpAndTryLevel((int) (recipe.getCraftExperience() * recipe.getCraftExperienceMultiplier()));
        starlightStored = Math.max(0, starlightStored - recipe.getPassiveStarlightRequired());

        if (!recipe.allowsForChaining() || !recipe.matches(this)) {
            craftingTask.getRecipeToCraft().onCraftServerFinish(this, rand);
            craftingTask = null;
        }
        markForUpdate();
    }

    public ShapeMap copyGetCurrentCraftingGrid() {
        ShapeMap current = new ShapeMap();
        for (int i = 0; i < 9; i++) {
            ShapedRecipeSlot slot = ShapedRecipeSlot.values()[i];
            ItemStack stack = inv[i];
            if(stack != null) {
                current.put(slot, ItemUtils.copyStackWithSize(stack, 1));
            }
        }
        return current;
    }

    private void addExpAndTryLevel(int exp) {
        if(level != AltarLevel.ENDGAME) {
            experience += exp;
            AltarLevel next = level.tryLevelUp(this);
            if(next.ordinal() > level.ordinal()) {
                levelUnsafe(next);
            }
        } else {
            experience = Integer.MAX_VALUE;
        }
        markForUpdate();
    }

    public boolean tryForceLevelUp(AltarLevel to, boolean doLevelUp) {
        int curr = getAltarLevel().ordinal();
        if(curr >= to.ordinal()) return false;
        if(getAltarLevel().next() != to) return false;

        if(!doLevelUp) return true;
        levelUnsafe(getAltarLevel().next());
        return true;
    }

    private void levelUnsafe(AltarLevel to) {
        onLevelUp(level, to);
        level = to;
        experience = 0;
        mbState = false;
        worldObj.setBlockState(getPos(), BlocksAS.blockAltar.getDefaultState().withProperty(BlockAltar.ALTAR_TYPE, level.getCorrespondingAltarType()));
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
        return oldState.getBlock() != newSate.getBlock();
    }

    private void onLevelUp(AltarLevel current, AltarLevel next) {

    }

    private void abortCrafting() {
        this.craftingTask = null;
        markForUpdate();
    }

    private boolean starlightPassive(boolean needUpdate) {
        if(starlightStored > 0) needUpdate = true;
        starlightStored *= getAltarLevel().ordinal() != 0 ? 0.995 : 0.95;

        if(doesSeeSky()) {
            int collect = getAltarLevel().ordinal() != 0 ? 60 : getPos().getY() / 2;
            double perc =  0.2 + (0.8 * CelestialHandler.calcDaytimeDistribution(worldObj));
            starlightStored = Math.min(getMaxStarlightStorage(), (int) (starlightStored + (collect * perc)));
            return true;
        }
        return needUpdate;
    }

    @Nullable
    public ActiveCraftingTask getActiveCraftingTask() {
        return craftingTask;
    }

    public int getExperience() {
        return experience;
    }

    public boolean getMultiblockState() {
        return mbState;
    }

    public float getAmbientStarlightPercent() {
        return ((float) starlightStored) / ((float) getMaxStarlightStorage());
    }

    public int getStarlightStored() {
        return starlightStored;
    }

    public int getMaxStarlightStorage() {
        return getAltarLevel().ordinal() != 0 ? 4000 : 1000;
    }

    @Override
    public void onInteract(World world, BlockPos pos, EntityPlayer player, EnumFacing side, boolean sneaking) {
        if(!worldObj.isRemote) {
            if(getActiveCraftingTask() != null) {
                AbstractAltarRecipe altarRecipe = craftingTask.getRecipeToCraft();
                if(!altarRecipe.matches(this)) {
                    abortCrafting();
                }
            }

            findRecipe(player);
        }
    }

    private void findRecipe(EntityPlayer crafter) {
        if(!mbState) return;

        AbstractAltarRecipe recipe = AltarRecipeRegistry.findMatchingRecipe(this);
        //System.out.println(recipe == null ? "didn't find recipe" : "found recipe");
        if(recipe != null) {
            this.craftingTask = new ActiveCraftingTask(recipe, crafter.getUniqueID());
            markForUpdate();
        }
    }

    protected void updateSkyState(boolean seesSky) {
        boolean update = doesSeeSky != seesSky;
        this.doesSeeSky = seesSky;
        if(update) {
            markForUpdate();
        }
    }

    public boolean doesSeeSky() {
        return doesSeeSky;
    }

    public AltarLevel getAltarLevel() {
        return level;
    }

    public int getCraftingRecipeWidth() {
        return 3;
    }

    public int getCraftingRecipeHeight() {
        return 3;
    }

    @Override
    public void readCustomNBT(NBTTagCompound compound) {
        super.readCustomNBT(compound);

        this.level = AltarLevel.values()[compound.getInteger("level")];
        this.experience = compound.getInteger("exp");
        this.starlightStored = compound.getInteger("starlight");
        this.mbState = compound.getBoolean("mbState");

        this.craftingTask = null;
        if(compound.hasKey("recipeId") && compound.hasKey("recipeTick")) {
            int recipeId = compound.getInteger("recipeId");
            AbstractAltarRecipe recipe = AltarRecipeRegistry.getRecipe(recipeId);
            if(recipe == null) {
                AstralSorcery.log.info("Recipe with unknown/invalid ID found: " + recipeId + " for Altar at " + getPos());
            } else {
                UUID uuidCraft = compound.getUniqueId("crafterUUID");
                int tick = compound.getInteger("recipeTick");
                this.craftingTask = new ActiveCraftingTask(recipe, uuidCraft);
                this.craftingTask.forceTick(tick);
            }
        }
    }

    @Override
    public void writeCustomNBT(NBTTagCompound compound) {
        super.writeCustomNBT(compound);

        compound.setInteger("level", level.ordinal());
        compound.setInteger("exp", experience);
        compound.setInteger("starlight", starlightStored);
        compound.setBoolean("mbState", mbState);

        if(craftingTask != null) {
            compound.setInteger("recipeId", craftingTask.getRecipeToCraft().getUniqueRecipeId());
            compound.setInteger("recipeTick", craftingTask.getTicksCrafting());
            compound.setUniqueId("crafterUUID", craftingTask.getPlayerCraftingUUID());
        }
    }

    @Nullable
    @Override
    public String getUnLocalizedDisplayName() {
        return "tile.BlockAltar.general.name";
    }

    @Override
    public ITransmissionReceiver provideEndpoint(BlockPos at) {
        return new TransmissionReceiverAltar(at);
    }

    @Override
    public String getInventoryName() {
        return getUnLocalizedDisplayName();
    }

    public void onPlace(int exp, AltarLevel level) {
        this.experience = exp;
        this.level = level;
        markForUpdate();
    }

    public static enum AltarLevel {

        DISCOVERY          (100,   (ta) -> true       ), //Default one...
        ATTENUATION        (1000,  (ta) -> true, false),
        CONSTELLATION_CRAFT(4000,  (ta) -> true, false),
        TRAIT_CRAFT        (12000, (ta) -> true, false),
        ENDGAME            (-1,    (ta) -> true       ); //Enhanced version of traitcraft.

        private final int totalExpNeededToLevelUp;
        private final IAltarMatcher matcher;
        private boolean canLevelToByExpGain = true;

        private AltarLevel(int levelExp, IAltarMatcher matcher) {
            this.totalExpNeededToLevelUp = levelExp;
            this.matcher = matcher;
        }

        private AltarLevel(int levelExp, IAltarMatcher matcher, boolean canLevelToByExpGain) {
            this.totalExpNeededToLevelUp = levelExp;
            this.canLevelToByExpGain = canLevelToByExpGain;
            this.matcher = matcher;
        }

        public BlockAltar.AltarType getCorrespondingAltarType() {
            return BlockAltar.AltarType.values()[ordinal()];
        }

        public IAltarMatcher getMatcher() {
            return matcher;
        }

        public int getTotalExpNeededForLevel() {
            return totalExpNeededToLevelUp;
        }

        public boolean hasNextLevel() {
            return totalExpNeededToLevelUp > 0;
        }

        public AltarLevel tryLevelUp(TileAltar ta) {
            if(!hasNextLevel()) return this;
            int current = ta.experience;
            if(ordinal() + 1 >= values().length) return this;
            AltarLevel next = values()[ordinal() + 1];
            if(!next.canLevelToByExpGain) return this;
            if(current >= totalExpNeededToLevelUp) {
                return next;
            }
            return this;
        }

        public AltarLevel next() {
            if(this == ENDGAME) return this;
            return AltarLevel.values()[ordinal() + 1];
        }

    }

    public static class TransmissionReceiverAltar extends SimpleTransmissionReceiver {

        public TransmissionReceiverAltar(@Nonnull BlockPos thisPos) {
            super(thisPos);
        }

        @Override
        public void onStarlightReceive(World world, boolean isChunkLoaded, Constellation type, double amount) {
            if(isChunkLoaded) {
                TileAltar ta = MiscUtils.getTileAt(world, getPos(), TileAltar.class, false);
                if(ta != null) {
                    ta.receiveStarlight(type, amount);
                }
            }
        }

        @Override
        public TransmissionClassRegistry.TransmissionProvider getProvider() {
            return new AltarReceiverProvider();
        }

    }

    public static class AltarReceiverProvider implements TransmissionClassRegistry.TransmissionProvider {

        @Override
        public TransmissionReceiverAltar provideEmptyNode() {
            return new TransmissionReceiverAltar(null);
        }

        @Override
        public String getIdentifier() {
            return AstralSorcery.MODID + ":TransmissionReceiverAltar";
        }

    }

    public static class PatternAltarMatcher implements IAltarMatcher {

        private final PatternBlockArray pba;

        public PatternAltarMatcher(PatternBlockArray pba) {
            this.pba = pba;
        }

        @Override
        public boolean mbAllowsForCrafting(TileAltar ta) {
            return pba.matches(ta.getWorld(), ta.getPos());
        }
    }

    public static interface IAltarMatcher {

        public boolean mbAllowsForCrafting(TileAltar ta);

    }

}
