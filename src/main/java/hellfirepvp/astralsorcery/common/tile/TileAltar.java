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
import hellfirepvp.astralsorcery.client.util.PositionedLoopSound;
import hellfirepvp.astralsorcery.client.util.SpriteLibrary;
import hellfirepvp.astralsorcery.common.block.network.BlockAltar;
import hellfirepvp.astralsorcery.common.constellation.IMajorConstellation;
import hellfirepvp.astralsorcery.common.constellation.IWeakConstellation;
import hellfirepvp.astralsorcery.common.constellation.distribution.ConstellationSkyHandler;
import hellfirepvp.astralsorcery.common.constellation.distribution.WorldSkyHandler;
import hellfirepvp.astralsorcery.common.crafting.IGatedRecipe;
import hellfirepvp.astralsorcery.common.crafting.ItemHandle;
import hellfirepvp.astralsorcery.common.crafting.altar.AbstractAltarRecipe;
import hellfirepvp.astralsorcery.common.crafting.altar.ActiveCraftingTask;
import hellfirepvp.astralsorcery.common.crafting.altar.AltarRecipeRegistry;
import hellfirepvp.astralsorcery.common.crafting.altar.recipes.AttunementRecipe;
import hellfirepvp.astralsorcery.common.crafting.altar.recipes.ConstellationRecipe;
import hellfirepvp.astralsorcery.common.crafting.helper.ShapeMap;
import hellfirepvp.astralsorcery.common.crafting.helper.ShapedRecipeSlot;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import hellfirepvp.astralsorcery.common.item.base.IWandInteract;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.MultiBlockArrays;
import hellfirepvp.astralsorcery.common.lib.Sounds;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.packet.server.PktParticleEvent;
import hellfirepvp.astralsorcery.common.starlight.transmission.ITransmissionReceiver;
import hellfirepvp.astralsorcery.common.starlight.transmission.base.SimpleTransmissionReceiver;
import hellfirepvp.astralsorcery.common.starlight.transmission.registry.TransmissionClassRegistry;
import hellfirepvp.astralsorcery.common.tile.base.TileReceiverBaseInventory;
import hellfirepvp.astralsorcery.common.util.ItemUtils;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.SkyNoiseCalculator;
import hellfirepvp.astralsorcery.common.util.SoundHelper;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.struct.PatternBlockArray;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
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

    private float posDistribution = -1;

    private ActiveCraftingTask craftingTask = null;
    private Object clientCraftSound = null;

    private AltarLevel level = AltarLevel.DISCOVERY;
    private boolean doesSeeSky = false;
    private boolean mbState = false;
    private int experience = 0;
    private int starlightStored = 0;

    public TileAltar() {
        super(21);
    }

    public TileAltar(AltarLevel level) {
        super(21, EnumFacing.UP);
        this.level = level;
    }

    private void receiveStarlight(IWeakConstellation type, double amount) {
        if(amount <= 0.001) return;

        starlightStored = Math.min(getMaxStarlightStorage(), (int) (starlightStored + (amount * 100D)));
        markForUpdate();
    }

    @Override
    public void update() {
        super.update();

        if((ticksExisted & 15) == 0) {
            updateSkyState(world.canSeeSky(getPos()));
        }

        if((ticksExisted & 15) == 0) {
            if(matchLevel(false)) markForUpdate();
        }

        if(!world.isRemote) {
            boolean needUpdate = false;

            needUpdate = starlightPassive(needUpdate);
            needUpdate = doTryCraft(needUpdate);

            if(needUpdate) {
                markForUpdate();
            }
        } else {
            if(getActiveCraftingTask() != null) {
                doCraftEffects();
                doCraftSound();
            }
            AltarLevel lvl = getAltarLevel();
            if(lvl == AltarLevel.CONSTELLATION_CRAFT && getMultiblockState()) {
                doConstellationRays();
            }
        }
    }

    @SideOnly(Side.CLIENT)
    private void doCraftSound() {
        if(Minecraft.getMinecraft().gameSettings.getSoundLevel(SoundCategory.MASTER) > 0) {
            if(clientCraftSound == null || ((PositionedLoopSound) clientCraftSound).hasStoppedPlaying()) {
                clientCraftSound = SoundHelper.playSoundLoopClient(Sounds.attunement, new Vector3(this), 0.7F, 1F,
                        () -> isInvalid() ||
                                Minecraft.getMinecraft().gameSettings.getSoundLevel(SoundCategory.MASTER) <= 0 ||
                                craftingTask == null);
            }
        } else {
            clientCraftSound = null;
        }
    }

    @Nullable
    public IMajorConstellation getFocusedConstellation() {
        /*int tierNumber = 0;
        if(focusLensStack != null && focusLensStack.getItem() != null && focusLensStack.getItem() instanceof ItemFocusLens) {
            tierNumber = focusLensStack.getItemDamage() + 1;
        }
        Tier tier = ConstellationRegistry.getTier(tierNumber);
        if(tier == null) return null;
        return ((DataActiveCelestials) SyncDataHolder.getDataClient(SyncDataHolder.DATA_CONSTELLATIONS)).getActiveConstellaionForTier(tier);*/
        //return Constellations.orion; //FIXME find another solution
        return null;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
        return super.getRenderBoundingBox().expand(0, 3, 0);
    }

    @SideOnly(Side.CLIENT)
    private void doConstellationRays() {
        IMajorConstellation c = getFocusedConstellation();
        WorldSkyHandler handle = ConstellationSkyHandler.getInstance().getWorldHandler(getWorld());
        if(c != null && handle != null) {
            float alphaDaytime = ConstellationSkyHandler.getInstance().getCurrentDaytimeDistribution(getWorld());
            if(alphaDaytime >= 1.0E-3) {
                if(rand.nextInt(20) == 0) {
                    Vector3 from = new Vector3(getPos()).add(0.5, 3.5, 0.5);
                    Vector3 to = new Vector3(getPos()).add(0.5, -0.1, 0.5);
                    to.add(rand.nextFloat() * 4 * (rand.nextBoolean() ? 1 : -1), 0, rand.nextFloat() * 4 * (rand.nextBoolean() ? 1 : -1));
                    EffectHandler.getInstance().lightbeam(to, from, 0.8);
                }
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
        if(!altarRecipe.matches(this, getInventoryHandler())) {
            abortCrafting();
            return true;
        }
        if((ticksExisted % 5) == 0) {
            if(!matchDownMultiblocks(altarRecipe.getNeededLevel())) {
                abortCrafting();
                return true;
            }
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
        ItemStack out = recipe.getOutput(current, this); //Central item helps defining output - probably, eventually.
        if(out != null) {
            out = ItemUtils.copyStackWithSize(out, out.stackSize);
        }

        for (int i = 0; i < 9; i++) {
            ShapedRecipeSlot slot = ShapedRecipeSlot.getByRowColumnIndex(i % 3, i / 3);
            if(recipe.mayDecrement(this, slot)) {
                ItemUtils.decrStackInInventory(getInventoryHandler(), i);
            } else {
                recipe.handleItemConsumption(this, slot);
            }
        }

        for (AttunementRecipe.AltarSlot slot : AttunementRecipe.AltarSlot.values()) {
            int slotId = slot.slotId;
            if(recipe.mayDecrement(this, slot)) {
                ItemUtils.decrStackInInventory(getInventoryHandler(), slotId);
            } else {
                recipe.handleItemConsumption(this, slot);
            }
        }

        for (ConstellationRecipe.AltarAdditionalSlot slot : ConstellationRecipe.AltarAdditionalSlot.values()) {
            int slotId = slot.getSlotId();
            if(recipe.mayDecrement(this, slot)) {
                ItemUtils.decrStackInInventory(getInventoryHandler(), slotId);
            } else {
                recipe.handleItemConsumption(this, slot);
            }
        }

        if(out != null) {
            /*for (EnumFacing dir : EnumFacing.VALUES) { FIXME Item capability system break here :|
                if(dir == EnumFacing.UP) continue;

                TileEntity te = MiscUtils.getTileAt(world, pos.offset(dir), TileEntity.class, true);
                if(te != null) {
                    IItemHandler handle = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, dir.getOpposite());
                    if(handle != null) {
                        ItemUtils.tryPlaceItemInInventory(out, handle);
                        if(out.stackSize <= 0) {
                            break;
                        }
                    }
                }
            }*/
            if(out.stackSize > 0) {
                ItemUtils.dropItem(world, pos.getX() + 0.5, pos.getY() + 1.3, pos.getZ() + 0.5, out).setNoDespawn();
            }
        }

        addExpAndTryLevel((int) (recipe.getCraftExperience() * recipe.getCraftExperienceMultiplier()));
        starlightStored = Math.max(0, starlightStored - recipe.getPassiveStarlightRequired());

        if (!recipe.allowsForChaining() || !recipe.matches(this, getInventoryHandler()) || !matchDownMultiblocks(recipe.getNeededLevel())) {
            if(getAltarLevel().ordinal() >= AltarLevel.CONSTELLATION_CRAFT.ordinal()) {
                Vector3 pos = new Vector3(getPos()).add(0.5, 0, 0.5);
                PktParticleEvent ev = new PktParticleEvent(PktParticleEvent.ParticleEventType.CRAFT_FINISH_BURST, pos.getX(), pos.getY() + 0.05, pos.getZ());
                PacketChannel.CHANNEL.sendToAllAround(ev, PacketChannel.pointFromPos(getWorld(), getPos(), 32));
            }
            craftingTask.getRecipeToCraft().onCraftServerFinish(this, rand);
            ResearchManager.informCraftingAltarCompletion(this, craftingTask);
            SoundHelper.playSoundAround(Sounds.craftFinish, world, getPos(), 1F, 1.7F);
            craftingTask = null;
        }
        markForUpdate();
    }

    public ShapeMap copyGetCurrentCraftingGrid() {
        ShapeMap current = new ShapeMap();
        for (int i = 0; i < 9; i++) {
            ShapedRecipeSlot slot = ShapedRecipeSlot.values()[i];
            ItemStack stack = getInventoryHandler().getStackInSlot(i);
            if(stack != null) {
                current.put(slot, new ItemHandle(ItemUtils.copyStackWithSize(stack, 1)));
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
        world.setBlockState(getPos(), BlocksAS.blockAltar.getDefaultState().withProperty(BlockAltar.ALTAR_TYPE, level.getCorrespondingAltarType()));
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
        starlightStored *= 0.95;

        WorldSkyHandler handle = ConstellationSkyHandler.getInstance().getWorldHandler(getWorld());
        if(doesSeeSky() && handle != null) {
            int yLevel = getPos().getY();
            if(yLevel > 40) {
                float collect = 60;

                float dstr;
                if(yLevel > 140) {
                    dstr = 1F;
                } else {
                    dstr = (yLevel - 40) / 100F;
                }

                if(posDistribution == -1) {
                    posDistribution = SkyNoiseCalculator.getDistribution(world, pos);
                }

                collect *= dstr;
                collect *= posDistribution;
                collect *= 0.2 + (0.8 * ConstellationSkyHandler.getInstance().getCurrentDaytimeDistribution(getWorld()));

                starlightStored = Math.min(getMaxStarlightStorage(), (int) (starlightStored + collect));
                return true;
            }
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
        switch (getAltarLevel()) {
            case DISCOVERY:
                return 1000;
            case ATTUNEMENT:
                return 4000;
            case CONSTELLATION_CRAFT:
                return 6000;
            case TRAIT_CRAFT:
                return 10000;
            case ENDGAME:
                return 12000;
        }
        return 1000;
    }

    @Override
    public void onInteract(World world, BlockPos pos, EntityPlayer player, EnumFacing side, boolean sneaking) {
        if(!world.isRemote) {
            if(getActiveCraftingTask() != null) {
                AbstractAltarRecipe altarRecipe = craftingTask.getRecipeToCraft();
                if(!matchDownMultiblocks(altarRecipe.getNeededLevel()) || !altarRecipe.matches(this, getInventoryHandler())) {
                    abortCrafting();
                    return;
                }
            }

            findRecipe(player);
        }
    }

    private boolean matchDownMultiblocks(AltarLevel levelDownTo) {
        for (int i = getAltarLevel().ordinal(); i >= levelDownTo.ordinal(); i--) {
            AltarLevel al = AltarLevel.values()[i];
            if(al.getMatcher().mbAllowsForCrafting(this)) return true;
        }
        return false;
    }

    private void findRecipe(EntityPlayer crafter) {
        if(craftingTask != null) return;

        AbstractAltarRecipe recipe = AltarRecipeRegistry.findMatchingRecipe(this);
        if(recipe instanceof IGatedRecipe) {
            if(!((IGatedRecipe) recipe).hasProgressionServer(crafter)) return;
        }
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
    @Nonnull
    public ITransmissionReceiver provideEndpoint(BlockPos at) {
        return new TransmissionReceiverAltar(at);
    }

    public void onPlace(int exp, AltarLevel level) {
        this.experience = exp;
        this.level = level;
        markForUpdate();
    }

    @SideOnly(Side.CLIENT)
    public static void finishBurst(PktParticleEvent event) {
        EffectHandler.getInstance().textureSpritePlane(SpriteLibrary.spriteCraftBurst, Vector3.RotAxis.Y_AXIS.clone()).setPosition(event.getVec()).setScale(5 + rand.nextInt(2)).setNoRotation(rand.nextInt(360));
    }

    public static enum AltarLevel {

        DISCOVERY          (100,   (ta) -> true       ), //Default one...
        ATTUNEMENT         (1000,  new PatternAltarMatcher(MultiBlockArrays.patternAltarAttunement), false),
        CONSTELLATION_CRAFT(4000,  new PatternAltarMatcher(MultiBlockArrays.patternAltarConstellation), false),
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
        public void onStarlightReceive(World world, boolean isChunkLoaded, IWeakConstellation type, double amount) {
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
