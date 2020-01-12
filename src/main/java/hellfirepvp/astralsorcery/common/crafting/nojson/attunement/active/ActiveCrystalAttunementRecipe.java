package hellfirepvp.astralsorcery.common.crafting.nojson.attunement.active;

import hellfirepvp.astralsorcery.client.util.sound.FadeLoopSound;
import hellfirepvp.astralsorcery.client.util.sound.PositionedLoopSound;
import hellfirepvp.astralsorcery.common.constellation.ConstellationItem;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.IMinorConstellation;
import hellfirepvp.astralsorcery.common.constellation.IWeakConstellation;
import hellfirepvp.astralsorcery.common.crafting.nojson.attunement.AttuneCrystalRecipe;
import hellfirepvp.astralsorcery.common.crafting.nojson.attunement.AttunementRecipe;
import hellfirepvp.astralsorcery.common.item.crystal.ItemCrystalBase;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import hellfirepvp.astralsorcery.common.lib.SoundsAS;
import hellfirepvp.astralsorcery.common.tile.TileAttunementAltar;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.sound.SoundHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.LogicalSide;

import javax.annotation.Nullable;
import java.util.function.Predicate;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ActiveCrystalAttunementRecipe
 * Created by HellFirePvP
 * Date: 05.12.2019 / 06:33
 */
public class ActiveCrystalAttunementRecipe extends AttunementRecipe.Active<AttuneCrystalRecipe> {

    private static final int DURATION_CRYSTAL_ATTUNEMENT = 500;

    private IConstellation constellation;
    private int entityId;

    //The item attunement sound, FadeLoopSound
    public Object itemAttuneSound;

    public ActiveCrystalAttunementRecipe(AttuneCrystalRecipe recipe, IConstellation constellation, int crystalEntityId) {
        super(recipe);
        this.constellation = constellation;
        this.entityId = crystalEntityId;
    }

    public ActiveCrystalAttunementRecipe(AttuneCrystalRecipe recipe, CompoundNBT nbt) {
        super(recipe);
        this.readFromNBT(nbt);
    }

    @Override
    public boolean matches(TileAttunementAltar altar) {
        if (!super.matches(altar)) {
            return false;
        }
        Entity entity;
        return (entity = altar.getWorld().getEntityByID(this.entityId)) != null &&
                entity.isAlive() &&
                entity instanceof ItemEntity &&
                this.constellation.equals(altar.getActiveConstellation()) &&
                AttuneCrystalRecipe.isApplicableCrystal((ItemEntity) entity, altar.getActiveConstellation());
    }

    @Override
    public void stopCrafting(TileAttunementAltar altar) {

    }

    @Override
    public void finishRecipe(TileAttunementAltar altar) {
        ItemEntity crystal = this.getEntity(altar.getWorld());
        if (crystal != null) {
            ItemStack stack = crystal.getItem();
            if (!(stack.getItem() instanceof ConstellationItem) && stack.getItem() instanceof ItemCrystalBase) {
                CompoundNBT tag = stack.getTag();
                stack = new ItemStack(((ItemCrystalBase) stack.getItem()).getTunedItemVariant(), stack.getCount());
                stack.setTag(tag);
            }
            if (stack.getItem() instanceof ConstellationItem) {
                IWeakConstellation attuned = ((ConstellationItem) stack.getItem()).getAttunedConstellation(stack);
                IMinorConstellation trait = ((ConstellationItem) stack.getItem()).getTraitConstellation(stack);
                if (attuned == null) {
                    if (altar.getActiveConstellation() instanceof IWeakConstellation) {
                        ((ConstellationItem) stack.getItem()).setAttunedConstellation(stack, (IWeakConstellation) altar.getActiveConstellation());
                    }
                } else if (trait == null) {
                    if (altar.getActiveConstellation() instanceof IMinorConstellation) {
                        ((ConstellationItem) stack.getItem()).setTraitConstellation(stack, (IMinorConstellation) altar.getActiveConstellation());
                    }
                }
                crystal.setItem(stack);
            }
        }
    }

    @Override
    public void doTick(LogicalSide side, TileAttunementAltar altar) {
        ItemEntity crystal = this.getEntity(altar.getWorld());
        if (crystal == null) {
            return;
        }

        Vector3 crystalHoverPos = new Vector3(altar).add(0.5, 1.4, 0.5);
        crystal.setPosition(crystalHoverPos.getX(), crystalHoverPos.getY(), crystalHoverPos.getZ());
        crystal.prevPosX = crystalHoverPos.getX();
        crystal.prevPosY = crystalHoverPos.getY();
        crystal.prevPosZ = crystalHoverPos.getZ();
        crystal.setMotion(0, 0, 0);

        if (side.isClient()) {
            doClientTick(altar);
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void doClientTick(TileAttunementAltar altar) {
        Predicate<PositionedLoopSound> activeTest = s ->
                !altar.canPlayConstellationActiveEffects() ||
                        altar.getActiveRecipe() != this;

        if (this.itemAttuneSound == null || ((FadeLoopSound) this.itemAttuneSound).hasStoppedPlaying()) {
            this.itemAttuneSound = SoundHelper.playSoundLoopFadeInClient(SoundsAS.ATTUNEMENT_ATLAR_ITEM_LOOP,
                    new Vector3(altar).add(0.5, 1, 0.5), 1F, 1F, false, activeTest)
                    .setFadeInTicks(20)
                    .setFadeOutTicks(20);
        }

        if (this.getTick() == 0) {
            SoundHelper.playSoundLoopFadeInClient(SoundsAS.ATTUNEMENT_ATLAR_ITEM_START,
                    new Vector3(altar).add(0.5, 1, 0.5), 1F, 1F, false, activeTest)
                    .setFadeOutTicks(10);
        }
    }

    @Override
    public boolean isFinished(TileAttunementAltar altar) {
        return this.getTick() >= DURATION_CRYSTAL_ATTUNEMENT;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void stopEffects(TileAttunementAltar altar) {
        if (isFinished(altar)) {
            SoundHelper.playSoundClientWorld(SoundsAS.ATTUNEMENT_ATLAR_ITEM_FINISH, altar.getPos().up(), 1F, 1F);
        }
    }

    @Nullable
    private ItemEntity getEntity(World world) {
        Entity entity = world.getEntityByID(this.entityId);
        if (entity != null && entity.isAlive() && entity instanceof ItemEntity) {
            return (ItemEntity) entity;
        }
        return null;
    }

    @Override
    public void writeToNBT(CompoundNBT nbt) {
        super.writeToNBT(nbt);

        nbt.putString("constellation", this.constellation.getRegistryName().toString());
        nbt.putInt("entityId", this.entityId);
    }

    @Override
    protected void readFromNBT(CompoundNBT nbt) {
        super.readFromNBT(nbt);

        this.constellation = RegistriesAS.REGISTRY_CONSTELLATIONS.getValue(new ResourceLocation(nbt.getString("constellation")));
        this.entityId = nbt.getInt("entityId");
    }
}
