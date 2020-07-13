/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile;

import com.google.common.collect.ImmutableSet;
import hellfirepvp.astralsorcery.client.effect.function.VFXAlphaFunction;
import hellfirepvp.astralsorcery.client.effect.handler.EffectHelper;
import hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS;
import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.client.util.sound.PositionedLoopSound;
import hellfirepvp.astralsorcery.common.crafting.recipe.LiquidInfusion;
import hellfirepvp.astralsorcery.common.crafting.recipe.LiquidInfusionContext;
import hellfirepvp.astralsorcery.common.crafting.recipe.infusion.ActiveLiquidInfusionRecipe;
import hellfirepvp.astralsorcery.common.entity.EntityFlare;
import hellfirepvp.astralsorcery.common.item.wand.WandInteractable;
import hellfirepvp.astralsorcery.common.lib.RecipeTypesAS;
import hellfirepvp.astralsorcery.common.lib.SoundsAS;
import hellfirepvp.astralsorcery.common.lib.StructureTypesAS;
import hellfirepvp.astralsorcery.common.lib.TileEntityTypesAS;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.play.server.PktPlayEffect;
import hellfirepvp.astralsorcery.common.structure.types.StructureType;
import hellfirepvp.astralsorcery.common.tile.base.TileEntityTick;
import hellfirepvp.astralsorcery.common.util.ColorUtils;
import hellfirepvp.astralsorcery.common.util.MapStream;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.data.ByteBufUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.sound.CategorizedSoundEvent;
import hellfirepvp.astralsorcery.common.util.sound.SoundHelper;
import hellfirepvp.astralsorcery.common.util.tile.TileInventory;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.LogicalSide;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.Map;
import java.util.Set;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TileInfuser
 * Created by HellFirePvP
 * Date: 09.11.2019 / 11:18
 */
public class TileInfuser extends TileEntityTick implements WandInteractable {

    private static final Set<BlockPos> LIQUID_OFFSETS = ImmutableSet.of(
            new BlockPos( 1, -1,  2),
            new BlockPos( 0, -1,  2),
            new BlockPos(-1, -1,  2),

            new BlockPos( 1, -1, -2),
            new BlockPos( 0, -1, -2),
            new BlockPos(-1, -1, -2),

            new BlockPos( 2, -1,  1),
            new BlockPos( 2, -1,  0),
            new BlockPos( 2, -1, -1),

            new BlockPos(-2, -1,  1),
            new BlockPos(-2, -1,  0),
            new BlockPos(-2, -1, -1)
    );

    private TileInventory inventory;

    private ActiveLiquidInfusionRecipe activeRecipe = null;

    private Object clientCraftSound = null;

    public TileInfuser() {
        super(TileEntityTypesAS.INFUSER);
        this.inventory = new TileInventory(this, () -> 1);
    }

    @Nullable
    @Override
    public StructureType getRequiredStructureType() {
        return StructureTypesAS.PTYPE_INFUSER;
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.getWorld().isRemote()) {
            this.doCraftingCycle();
        } else {
            if (this.getActiveRecipe() != null) {
                this.getActiveRecipe().tickClient(this);
                this.doCraftSound();
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void doCraftSound() {
        if (SoundHelper.getSoundVolume(SoundCategory.BLOCKS) > 0) {
            if (clientCraftSound == null || ((PositionedLoopSound) clientCraftSound).hasStoppedPlaying()) {
                CategorizedSoundEvent sound = SoundsAS.INFUSER_CRAFT_LOOP;

                clientCraftSound = SoundHelper.playSoundLoopFadeInClient(sound,
                        new Vector3(this).add(0.5, 0.5, 0.5),
                        1F,
                        1F,
                        false,
                        (s) -> isRemoved() ||
                                SoundHelper.getSoundVolume(SoundCategory.BLOCKS) <= 0 ||
                                this.getActiveRecipe() == null)
                        .setFadeInTicks(30)
                        .setFadeOutTicks(20);
            }
        } else {
            clientCraftSound = null;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static void finishCraftingEffects(PktPlayEffect pkt) {
        ResourceLocation recipeName = ByteBufUtils.readResourceLocation(pkt.getExtraData());
        BlockPos at = ByteBufUtils.readPos(pkt.getExtraData());

        World world = Minecraft.getInstance().world;
        if (world == null) {
            return;
        }

        TileInfuser thisInfuser = MiscUtils.getTileAt(world, at, TileInfuser.class, false);
        if (thisInfuser != null) {
            IRecipe<?> recipe = world.getRecipeManager().getRecipes(RecipeTypesAS.TYPE_INFUSION.getType()).get(recipeName);
            if (recipe instanceof LiquidInfusion) {
                FluidStack stack = new FluidStack(((LiquidInfusion) recipe).getLiquidInput(), FluidAttributes.BUCKET_VOLUME);
                Vector3 pos = new Vector3(at).add(0.5, 1, 0.5);
                for (int i = 0; i < 30; i++) {
                    playLiquidFinish(pos, stack);
                }
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    private static void playLiquidFinish(Vector3 at, FluidStack stack) {
        Vector3 motion = new Vector3();
        MiscUtils.applyRandomOffset(motion, rand, 0.05F);

        EffectHelper.of(EffectTemplatesAS.GENERIC_ATLAS_PARTICLE)
                .spawn(at)
                .setSprite(RenderingUtils.getParticleTexture(stack))
                .selectFraction(0.2F)
                .setScaleMultiplier(0.02F + rand.nextFloat() * 0.05F)
                .setMotion(motion)
                .color((fx, pTicks) -> new Color(ColorUtils.getOverlayColor(stack)))
                .alpha(VFXAlphaFunction.FADE_OUT)
                .setMaxAge(40);
    }

    private void doCraftingCycle() {
        if (this.activeRecipe == null) {
            return;
        }

        if (!this.hasMultiblock() || !this.activeRecipe.matches(this)) {
            this.abortCrafting();
            return;
        }

        if (this.activeRecipe.isFinished()) {
            this.finishRecipe();
            return;
        }

        this.activeRecipe.tick();
        this.markForUpdate();
    }

    private void finishRecipe() {
        ResourceLocation recipeName = this.activeRecipe.getRecipeToCraft().getId();

        ForgeHooks.setCraftingPlayer(this.activeRecipe.tryGetCraftingPlayerServer());
        this.activeRecipe.createItemOutputs(this, this::dropItemOnTop);
        this.activeRecipe.consumeInputs(this);
        this.activeRecipe.consumeFluidsInput(this);
        ForgeHooks.setCraftingPlayer(null);
        this.abortCrafting();

        SoundHelper.playSoundAround(SoundsAS.INFUSER_CRAFT_FINISH, this.getWorld(), this.getPos(), 1F, 1F);

        PktPlayEffect pkt = new PktPlayEffect(PktPlayEffect.Type.INFUSER_RECIPE_FINISH)
                .addData(buf -> {
                    ByteBufUtils.writeResourceLocation(buf, recipeName);
                    ByteBufUtils.writePos(buf, this.getPos());
                });
        PacketChannel.CHANNEL.sendToAllAround(pkt, PacketChannel.pointFromPos(this.getWorld(), this.getPos(), 32));

        EntityFlare.spawnAmbientFlare(getWorld(), getPos().add(-3 + rand.nextInt(7), 1 + rand.nextInt(3), -3 + rand.nextInt(7)));
    }

    private void abortCrafting() {
        this.activeRecipe.clearEffects();
        this.activeRecipe = null;
        markForUpdate();
    }

    protected LiquidInfusion findRecipe(PlayerEntity crafter) {
        return RecipeTypesAS.TYPE_INFUSION.findRecipe(new LiquidInfusionContext(this, crafter, LogicalSide.SERVER));
    }

    protected boolean startCrafting(LiquidInfusion recipe, PlayerEntity crafter) {
        if (this.getActiveRecipe() != null) {
            return false;
        }

        this.activeRecipe = new ActiveLiquidInfusionRecipe(getWorld(), getPos(), recipe, crafter.getUniqueID());
        markForUpdate();

        SoundHelper.playSoundAround(SoundsAS.INFUSER_CRAFT_START, SoundCategory.BLOCKS, this.world, new Vector3(this).add(0.5, 0.5, 0.5), 1F, 1F);
        return true;
    }

    @Override
    public boolean onInteract(World world, BlockPos pos, PlayerEntity player, Direction side, boolean sneak) {
        if (!world.isRemote() && this.hasMultiblock() && !this.getItemInput().isEmpty()) {
            if (this.getActiveRecipe() != null) {
                if (this.getActiveRecipe().matches(this)) {
                    return true;
                }

                abortCrafting();
            }
            LiquidInfusion recipe = this.findRecipe(player);
            if (recipe != null) {
                this.startCrafting(recipe, player);
            }
            return true;
        }
        return false;
    }

    @Nonnull
    public ItemStack getItemInput() {
        return this.inventory.getStackInSlot(0);
    }

    @Nonnull
    public ItemStack setItemInput(@Nonnull ItemStack stack) {
        ItemStack prev = this.getItemInput();
        this.inventory.setStackInSlot(0, stack);
        return prev;
    }

    @Nullable
    public ActiveLiquidInfusionRecipe getActiveRecipe() {
        return activeRecipe;
    }

    @Nonnull
    public static Set<BlockPos> getLiquidOffsets() {
        return LIQUID_OFFSETS;
    }

    @Nonnull
    public Map<BlockPos, Fluid> getLiquids() {
        return MapStream.ofKeys(getLiquidOffsets(), pos -> getWorld().getFluidState(getPos().add(pos)).getFluid()).toMap();
    }

    @Nonnull
    public Vector3 getRandomInfuserOffset() {
        Vector3 vec = new Vector3(this).add(0, 0.8, 0);
        switch (rand.nextInt(4)) {
            case 3:
                vec.add(0.5, 0, 0.875);
                break;
            case 2:
                vec.add(0.5, 0, 0.125);
                break;
            case 1:
                vec.add(0.125, 0, 0.5);
                break;
            case 0:
                vec.add(0.875, 0, 0.5);
                break;
        }
        return vec;
    }

    @Override
    public void readCustomNBT(CompoundNBT compound) {
        super.readCustomNBT(compound);

        this.inventory = this.inventory.deserialize(compound.getCompound("inventory"));

        if (compound.contains("activeRecipe", Constants.NBT.TAG_COMPOUND)) {
            this.activeRecipe = ActiveLiquidInfusionRecipe.deserialize(compound.getCompound("activeRecipe"), this.activeRecipe);
        } else {
            if (this.activeRecipe != null) {
                this.activeRecipe.clearEffects();
            }
            this.activeRecipe = null;
        }
    }

    @Override
    public void writeCustomNBT(CompoundNBT compound) {
        super.writeCustomNBT(compound);

        compound.put("inventory", this.inventory.serialize());

        if (this.activeRecipe != null) {
            compound.put("activeRecipe", this.activeRecipe.serialize());
        }
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (this.inventory.hasCapability(cap, side)) {
            return this.inventory.getCapability().cast();
        }
        return super.getCapability(cap, side);
    }
}
