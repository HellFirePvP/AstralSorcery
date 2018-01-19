/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.client.ClientScheduler;
import hellfirepvp.astralsorcery.client.effect.EffectHandler;
import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import hellfirepvp.astralsorcery.client.util.PositionedLoopSound;
import hellfirepvp.astralsorcery.client.util.SpriteLibrary;
import hellfirepvp.astralsorcery.common.block.network.BlockAltar;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
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
import hellfirepvp.astralsorcery.common.crafting.altar.recipes.TraitRecipe;
import hellfirepvp.astralsorcery.common.crafting.helper.ShapeMap;
import hellfirepvp.astralsorcery.common.crafting.helper.ShapedRecipeSlot;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import hellfirepvp.astralsorcery.common.entities.EntityFlare;
import hellfirepvp.astralsorcery.common.item.base.ItemConstellationFocus;
import hellfirepvp.astralsorcery.common.item.base.IWandInteract;
import hellfirepvp.astralsorcery.common.item.block.ItemBlockAltar;
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
import hellfirepvp.astralsorcery.common.util.SkyCollectionHelper;
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
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TileAltar
 * Created by HellFirePvP
 * Date: 11.05.2016 / 18:18
 */
public class TileAltar extends TileReceiverBaseInventory implements IWandInteract, IMultiblockDependantTile {

    private static final Random rand = new Random();

    private float posDistribution = -1;

    private ActiveCraftingTask craftingTask = null;
    private Object clientCraftSound = null;

    private ItemStack focusItem = ItemStack.EMPTY;
    private AltarLevel level = AltarLevel.DISCOVERY;
    private boolean doesSeeSky = false;
    private boolean mbState = false;
    private int starlightStored = 0;

    public TileAltar() {
        super(25);
    }

    public TileAltar(AltarLevel level) {
        super(25, EnumFacing.UP);
        this.level = level;
    }

    @Override
    protected ItemHandlerTile createNewItemHandler() {
        return new ItemHandlerTileFiltered(this) {
            @Override
            public boolean canInsertItem(int slot, ItemStack toAdd, @Nonnull ItemStack existing) {
                if(!super.canInsertItem(slot, toAdd, existing)) {
                    return false;
                }
                AltarLevel al = TileAltar.this.getAltarLevel();
                if(al == null) {
                    al = AltarLevel.DISCOVERY;
                }
                int allowed = al.getAccessibleInventorySize();
                return slot >= 0 && slot < allowed;
            }
        };
    }

    public void receiveStarlight(@Nullable IWeakConstellation type, double amount) {
        if(amount <= 0.001) return;

        starlightStored = Math.min(getMaxStarlightStorage(), (int) (starlightStored + (amount * 200D)));
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
            if(getAltarLevel() != null &&
                    getAltarLevel().ordinal() >= AltarLevel.TRAIT_CRAFT.ordinal() &&
                    getMultiblockState()) {
                playAltarEffects();
            }
        }
    }

    @SideOnly(Side.CLIENT)
    private void playAltarEffects() {
        if(Minecraft.isFancyGraphicsEnabled() && rand.nextBoolean()) {
            EntityFXFacingParticle p = EffectHelper.genericFlareParticle(
                    getPos().getX() + 0.5,
                    getPos().getY() + 4.4,
                    getPos().getZ() + 0.5);
            p.motion((rand.nextFloat() * 0.03F) * (rand.nextBoolean() ? 1 : -1),
                    (rand.nextFloat() * 0.03F) * (rand.nextBoolean() ? 1 : -1),
                    (rand.nextFloat() * 0.03F) * (rand.nextBoolean() ? 1 : -1));
            p.scale(0.15F).setColor(Color.WHITE).setMaxAge(25);
        }
    }

    @SideOnly(Side.CLIENT)
    private void doCraftSound() {
        if(Minecraft.getMinecraft().gameSettings.getSoundLevel(SoundCategory.MASTER) > 0) {
            if(clientCraftSound == null || ((PositionedLoopSound) clientCraftSound).hasStoppedPlaying()) {
                clientCraftSound = SoundHelper.playSoundLoopClient(Sounds.attunement, new Vector3(this), 0.3F, 1F,
                        () -> isInvalid() ||
                                Minecraft.getMinecraft().gameSettings.getSoundLevel(SoundCategory.MASTER) <= 0 ||
                                craftingTask == null);
            }
        } else {
            clientCraftSound = null;
        }
    }

    @Nullable
    public IConstellation getFocusedConstellation() {
        if (!focusItem.isEmpty() && focusItem.getItem() instanceof ItemConstellationFocus) {
            return ((ItemConstellationFocus) focusItem.getItem()).getFocusConstellation(focusItem);
        }
        return null;
    }

    @Nonnull
    public ItemStack getFocusItem() {
        return focusItem;
    }

    public void setFocusStack(@Nonnull ItemStack stack) {
        this.focusItem = stack;
        markForUpdate();
    }

    @Override
    public void onBreak() {
        super.onBreak();

        if (!world.isRemote && !focusItem.isEmpty()) {
            ItemUtils.dropItemNaturally(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, focusItem);
            this.focusItem = ItemStack.EMPTY;
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
        AxisAlignedBB box = super.getRenderBoundingBox().expand(0, 5, 0);
        if(level != null && level.ordinal() >= AltarLevel.TRAIT_CRAFT.ordinal()) {
            box = box.grow(3, 0, 3);
        }
        return box;
    }

    @SideOnly(Side.CLIENT)
    private void doCraftEffects() {
        craftingTask.getRecipeToCraft().onCraftClientTick(this, craftingTask.getState(), ClientScheduler.getClientTick(), rand);
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
        if(!doesRecipeMatch(altarRecipe, true)) {
            abortCrafting();
            return true;
        }
        if(!altarRecipe.fulfillesStarlightRequirement(this)) {
            if(craftingTask.shouldPersist(this)) {
                craftingTask.setState(ActiveCraftingTask.CraftingState.PAUSED);
                return true;
            }
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
        if(!craftingTask.tick(this)) {
            craftingTask.setState(ActiveCraftingTask.CraftingState.WAITING);
            return true;
        }
        ActiveCraftingTask.CraftingState prev = craftingTask.getState();
        craftingTask.setState(ActiveCraftingTask.CraftingState.ACTIVE);
        craftingTask.getRecipeToCraft().onCraftServerTick(this, ActiveCraftingTask.CraftingState.ACTIVE, craftingTask.getTicksCrafting(), rand);
        return (prev != craftingTask.getState()) || needUpdate;
    }

    private void finishCrafting() {
        if(craftingTask == null) return; //Wtf

        AbstractAltarRecipe recipe = craftingTask.getRecipeToCraft();
        ShapeMap current = copyGetCurrentCraftingGrid();
        ItemStack out = recipe.getOutput(current, this); //Central item helps defining output - probably, eventually.
        if(!out.isEmpty()) {
            out = ItemUtils.copyStackWithSize(out, out.getCount());
        }

        for (int i = 0; i < 9; i++) {
            ShapedRecipeSlot slot = ShapedRecipeSlot.getByRowColumnIndex(i / 3, i % 3);
            if(recipe.mayDecrement(this, slot)) {
                ItemUtils.decrStackInInventory(getInventoryHandler(), i);
            } else {
                recipe.handleItemConsumption(this, slot);
            }
        }

        for (AttunementRecipe.AttunementAltarSlot slot : AttunementRecipe.AttunementAltarSlot.values()) {
            int slotId = slot.getSlotId();
            if(recipe.mayDecrement(this, slot)) {
                ItemUtils.decrStackInInventory(getInventoryHandler(), slotId);
            } else {
                recipe.handleItemConsumption(this, slot);
            }
        }

        for (ConstellationRecipe.ConstellationAtlarSlot slot : ConstellationRecipe.ConstellationAtlarSlot.values()) {
            int slotId = slot.getSlotId();
            if(recipe.mayDecrement(this, slot)) {
                ItemUtils.decrStackInInventory(getInventoryHandler(), slotId);
            } else {
                recipe.handleItemConsumption(this, slot);
            }
        }

        for (TraitRecipe.TraitRecipeSlot slot : TraitRecipe.TraitRecipeSlot.values()) {
            int slotId = slot.getSlotId();
            if(recipe.mayDecrement(this, slot)) {
                ItemUtils.decrStackInInventory(getInventoryHandler(), slotId);
            } else {
                recipe.handleItemConsumption(this, slot);
            }
        }

        if(recipe instanceof TraitRecipe) {
            ((TraitRecipe) recipe).consumeOuterInputs(this, craftingTask);
        }

        if(!out.isEmpty() && !(out.getItem() instanceof ItemBlockAltar)) {
            if(out.getCount() > 0) {
                ItemUtils.dropItem(world, pos.getX() + 0.5, pos.getY() + 1.3, pos.getZ() + 0.5, out).setNoDespawn();
            }
        }

        starlightStored = Math.max(0, starlightStored - recipe.getPassiveStarlightRequired());

        if (!recipe.allowsForChaining() || !doesRecipeMatch(recipe, false) || !matchDownMultiblocks(recipe.getNeededLevel())) {
            if(getAltarLevel().ordinal() >= AltarLevel.CONSTELLATION_CRAFT.ordinal()) {
                Vector3 pos = new Vector3(getPos()).add(0.5, 0, 0.5);
                PktParticleEvent ev = new PktParticleEvent(PktParticleEvent.ParticleEventType.CRAFT_FINISH_BURST, pos.getX(), pos.getY() + 0.05, pos.getZ());
                PacketChannel.CHANNEL.sendToAllAround(ev, PacketChannel.pointFromPos(getWorld(), getPos(), 32));
            }
            craftingTask.getRecipeToCraft().onCraftServerFinish(this, rand);

            if(!recipe.getOutputForMatching().isEmpty()) {
                ItemStack match = recipe.getOutputForMatching();
                if(match.getItem() instanceof ItemBlockAltar) {
                    TileAltar.AltarLevel to = TileAltar.AltarLevel.values()[
                            MathHelper.clamp(match.getItemDamage(), 0, AltarLevel.values().length - 1)];
                    tryForceLevelUp(to, true);
                }
            }
            ResearchManager.informCraftingAltarCompletion(this, craftingTask);
            SoundHelper.playSoundAround(Sounds.craftFinish, world, getPos(), 1F, 1.7F);
            EntityFlare.spawnAmbient(world, new Vector3(this).add(-3 + rand.nextFloat() * 7, 0.6, -3 + rand.nextFloat() * 7));
            craftingTask = null;
        }
        markForUpdate();
    }

    public ShapeMap copyGetCurrentCraftingGrid() {
        ShapeMap current = new ShapeMap();
        for (int i = 0; i < 9; i++) {
            ShapedRecipeSlot slot = ShapedRecipeSlot.values()[i];
            ItemStack stack = getInventoryHandler().getStackInSlot(i);
            if(!stack.isEmpty()) {
                current.put(slot, new ItemHandle(ItemUtils.copyStackWithSize(stack, 1)));
            }
        }
        return current;
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
        mbState = false;
        world.setBlockState(getPos(), BlocksAS.blockAltar.getDefaultState().withProperty(BlockAltar.ALTAR_TYPE, level.getCorrespondingAltarType()));
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
        return oldState.getBlock() != newSate.getBlock();
    }

    private void onLevelUp(AltarLevel current, AltarLevel next) {}

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
                float collect = 200;

                float dstr;
                if(yLevel > 120) {
                    dstr = 1F + ((yLevel - 120) / 210);
                } else {
                    dstr = (yLevel - 20) / 100F;
                }

                if(posDistribution == -1) {
                    posDistribution = SkyCollectionHelper.getSkyNoiseDistribution(world, pos);
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

    public boolean getMultiblockState() {
        return mbState;
    }

    @Override
    public PatternBlockArray getRequiredStructure() {
        AltarLevel al = getAltarLevel();
        return al.getMatcher() instanceof PatternAltarMatcher ? ((PatternAltarMatcher) al.getMatcher()).getPattern().getPattern() : null;
    }

    public float getAmbientStarlightPercent() {
        return ((float) starlightStored) / ((float) getMaxStarlightStorage());
    }

    public int getStarlightStored() {
        return starlightStored;
    }

    public int getMaxStarlightStorage() {
        return getAltarLevel().getStarlightMaxStorage();
    }

    public boolean doesRecipeMatch(AbstractAltarRecipe recipe, boolean ignoreStarlightRequirement) {
        if(!recipe.getOutputForMatching().isEmpty()) {
            ItemStack match = recipe.getOutputForMatching();
            if(match.getItem() instanceof ItemBlockAltar) {
                TileAltar.AltarLevel to = TileAltar.AltarLevel.values()[
                        MathHelper.clamp(match.getItemDamage(), 0, AltarLevel.values().length - 1)];
                if(getAltarLevel().ordinal() >= to.ordinal()) {
                    return false;
                }
            }
        }
        return recipe.matches(this, getInventoryHandler(), ignoreStarlightRequirement);
    }

    @Override
    public void onInteract(World world, BlockPos pos, EntityPlayer player, EnumFacing side, boolean sneaking) {
        if(!world.isRemote) {
            if(getActiveCraftingTask() != null) {
                AbstractAltarRecipe altarRecipe = craftingTask.getRecipeToCraft();
                if(!matchDownMultiblocks(altarRecipe.getNeededLevel()) || !doesRecipeMatch(altarRecipe, false)) {
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

        AbstractAltarRecipe recipe = AltarRecipeRegistry.findMatchingRecipe(this, false);
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
        this.starlightStored = compound.getInteger("starlight");
        this.mbState = compound.getBoolean("mbState");

        this.craftingTask = null;
        if(compound.hasKey("craftingTask")) {
            this.craftingTask = ActiveCraftingTask.deserialize(compound.getCompoundTag("craftingTask"));
        }

        this.focusItem = ItemStack.EMPTY;
        if(compound.hasKey("focusItem")) {
            this.focusItem = new ItemStack(compound.getCompoundTag("focusItem"));
        }
    }

    @Override
    public void writeCustomNBT(NBTTagCompound compound) {
        super.writeCustomNBT(compound);

        compound.setInteger("level", level.ordinal());
        compound.setInteger("starlight", starlightStored);
        compound.setBoolean("mbState", mbState);

        if(!focusItem.isEmpty()) {
            NBTTagCompound focusTag = new NBTTagCompound();
            focusItem.writeToNBT(focusTag);
            compound.setTag("focusItem", focusTag);
        }

        if(craftingTask != null) {
            compound.setTag("craftingTask", craftingTask.serialize());
        }
    }

    @Nullable
    @Override
    public String getUnLocalizedDisplayName() {
        return "tile.blockaltar.general.name";
    }

    @Override
    @Nonnull
    public ITransmissionReceiver provideEndpoint(BlockPos at) {
        return new TransmissionReceiverAltar(at);
    }

    public void onPlace(AltarLevel level) {
        this.level = level;
        markForUpdate();
    }

    @SideOnly(Side.CLIENT)
    public static void finishBurst(PktParticleEvent event) {
        EffectHandler.getInstance().textureSpritePlane(SpriteLibrary.spriteCraftBurst, Vector3.RotAxis.Y_AXIS.clone()).setPosition(event.getVec()).setScale(5 + rand.nextInt(2)).setNoRotation(rand.nextInt(360));
    }

    public static enum AltarLevel {

        DISCOVERY          (9, (ta) -> true       ),
        ATTUNEMENT         (13, new PatternAltarMatcher(() -> MultiBlockArrays.patternAltarAttunement)),
        CONSTELLATION_CRAFT(21, new PatternAltarMatcher(() -> MultiBlockArrays.patternAltarConstellation)),
        TRAIT_CRAFT        (25, new PatternAltarMatcher(() -> MultiBlockArrays.patternAltarTrait)),
        ENDGAME            (25, (ta) -> true       );

        private final int maxStarlightStorage;
        private final int accessibleInventorySize;
        private final IAltarMatcher matcher;

        AltarLevel(int invSize, IAltarMatcher matcher) {
            this.matcher = matcher;
            this.accessibleInventorySize = invSize;
            this.maxStarlightStorage = (int) (1000 * Math.pow(2, ordinal()));
        }

        public BlockAltar.AltarType getCorrespondingAltarType() {
            return BlockAltar.AltarType.values()[ordinal()];
        }

        public IAltarMatcher getMatcher() {
            return matcher;
        }

        public int getStarlightMaxStorage() {
            return maxStarlightStorage;
        }

        public int getAccessibleInventorySize() {
            return accessibleInventorySize;
        }

        public BlockAltar.AltarType getType() {
            return BlockAltar.AltarType.values()[ordinal()];
        }

        public AltarLevel next() {
            if(this == ENDGAME) return this;
            return AltarLevel.values()[ordinal() + 1];
        }

    }

    public static class TransmissionReceiverAltar extends SimpleTransmissionReceiver {

        public TransmissionReceiverAltar(BlockPos thisPos) {
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

        private final PatternQuery pba;

        public PatternAltarMatcher(PatternQuery pba) {
            this.pba = pba;
        }

        public PatternQuery getPattern() {
            return pba;
        }

        @Override
        public boolean mbAllowsForCrafting(TileAltar ta) {
            return pba.getPattern().matches(ta.getWorld(), ta.getPos());
        }
    }

    public static interface PatternQuery {

        public PatternBlockArray getPattern();

    }

    public static interface IAltarMatcher {

        public boolean mbAllowsForCrafting(TileAltar ta);

    }

}
