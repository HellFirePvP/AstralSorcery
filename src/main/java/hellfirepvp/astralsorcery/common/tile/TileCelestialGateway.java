/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile;

import hellfirepvp.astralsorcery.client.effect.EntityVisualFX;
import hellfirepvp.astralsorcery.client.effect.function.RefreshFunction;
import hellfirepvp.astralsorcery.client.effect.function.VFXColorFunction;
import hellfirepvp.astralsorcery.client.effect.handler.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.vfx.FXColorEffectSphere;
import hellfirepvp.astralsorcery.client.event.effect.GatewayUIRenderHandler;
import hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS;
import hellfirepvp.astralsorcery.common.lib.ColorsAS;
import hellfirepvp.astralsorcery.common.lib.DataAS;
import hellfirepvp.astralsorcery.common.lib.StructureTypesAS;
import hellfirepvp.astralsorcery.common.lib.TileEntityTypesAS;
import hellfirepvp.astralsorcery.common.structure.types.StructureType;
import hellfirepvp.astralsorcery.common.tile.base.TileEntityTick;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.item.DyeColor;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.INameable;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.UUID;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TileCelestialGateway
 * Created by HellFirePvP
 * Date: 10.09.2020 / 17:07
 */
public class TileCelestialGateway extends TileEntityTick implements INameable {

    private UUID owner = null;
    private boolean networkRegistered = false;
    private ITextComponent displayText = null;
    private DyeColor color = null;

    private Object clientGatewaySphereEffect = null;

    public TileCelestialGateway() {
        super(TileEntityTypesAS.GATEWAY);
    }

    @Override
    public void tick() {
        super.tick();

        if (world.isRemote()) {
            playEffects();
        } else {
            boolean complete = this.hasMultiblock() & this.doesSeeSky();
            if (complete) {
                if (!networkRegistered) {
                    DataAS.DOMAIN_AS.getData(world, DataAS.KEY_GATEWAY_CACHE)
                            .offerPosition(world, getPos(), this.displayText, this.color);
                    networkRegistered = true;
                    markForUpdate();
                }
            } else if (networkRegistered) {
                DataAS.DOMAIN_AS.getData(world, DataAS.KEY_GATEWAY_CACHE).removePosition(world, getPos());
                networkRegistered = false;
                markForUpdate();
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void playEffects() {
        setupGatewaySphere();
        playGatewayParticles();
    }

    @OnlyIn(Dist.CLIENT)
    private void setupGatewaySphere() {
        if (!this.hasMultiblock() || !this.doesSeeSky()) {
            if (clientGatewaySphereEffect != null) {
                ((EntityVisualFX) clientGatewaySphereEffect).requestRemoval();
                clientGatewaySphereEffect = null;
            }
            return;
        }

        Vector3 at = new Vector3(this).add(0.5, 1.7, 0.5);
        double distance = Vector3.atEntityCorner(Minecraft.getInstance().player).distance(at);
        if (clientGatewaySphereEffect == null) {
            clientGatewaySphereEffect = EffectHelper.of(EffectTemplatesAS.COLOR_SPHERE)
                    .spawn(at)
                    .setupSphere(Vector3.RotAxis.Y_AXIS, 6)
                    .setRemoveIfInvisible(true)
                    .setAlphaFadeDistance(4)
                    .setAlphaMultiplier(1)
                    .color(VFXColorFunction.BLACK)
                    .refresh(RefreshFunction.tileExistsAnd(this, (te, fx) -> te.doesSeeSky() && te.hasMultiblock()));
        } else if (((EntityVisualFX) clientGatewaySphereEffect).isRemoved() && distance < 5) {
            EffectHelper.refresh(((FXColorEffectSphere) clientGatewaySphereEffect), EffectTemplatesAS.COLOR_SPHERE);
        }

        if (distance < 5.5) {
            Minecraft.getInstance().gameSettings.thirdPersonView = 0;
        }
        if (distance < 2.5) {
            GatewayUIRenderHandler.getInstance().getOrCreateUI(this.getWorld(), this.getPos(), at);
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void playGatewayParticles() {
        if (!this.hasMultiblock() || !this.doesSeeSky()) {
            return;
        }

        for (int i = 0; i < 3; i++) {
            Vector3 offset = new Vector3(this).add(-2, 0.05, -2);
            if (rand.nextBoolean()) {
                offset.add(5 * (rand.nextBoolean() ? 1 : 0), 0, rand.nextFloat() * 5);
            } else {
                offset.add(rand.nextFloat() * 5, 0, 5 * (rand.nextBoolean() ? 1 : 0));
            }

            Color c = MiscUtils.eitherOf(rand,
                    Color.WHITE,
                    ColorsAS.DEFAULT_GENERIC_PARTICLE,
                    ColorsAS.DEFAULT_GENERIC_PARTICLE.brighter());

            EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                    .spawn(offset)
                    .setGravityStrength(-0.0001F)
                    .color(VFXColorFunction.constant(c))
                    .setScaleMultiplier(0.25F + rand.nextFloat() * 0.15F)
                    .setMaxAge(30 + rand.nextInt(30));
        }
    }

    @Nullable
    public UUID getOwner() {
        return owner;
    }

    public void setOwner(@Nullable UUID owner) {
        this.owner = owner;
    }

    public void setDisplayText(@Nullable ITextComponent displayText) {
        this.displayText = displayText;
    }

    public void setColor(@Nullable DyeColor color) {
        this.color = color;
    }

    @Nullable
    @Override
    public StructureType getRequiredStructureType() {
        return StructureTypesAS.PTYPE_CELESTIAL_GATEWAY;
    }

    @Override
    public ITextComponent getName() {
        return this.displayText != null ? this.displayText : new TranslationTextComponent("block.astralsorcery.celestial_gateway");
    }

    @Override
    public boolean hasCustomName() {
        return this.displayText != null;
    }

    @Nullable
    @Override
    public ITextComponent getCustomName() {
        return getName();
    }

    @Nullable
    public DyeColor getColor() {
        return this.color;
    }

    @Override
    public void readCustomNBT(CompoundNBT compound) {
        super.readCustomNBT(compound);

        this.owner = compound.hasUniqueId("owner") ? compound.getUniqueId("owner") : null;
        this.networkRegistered = compound.getBoolean("networkRegistered");
        this.displayText = compound.contains("displayText") ? ITextComponent.Serializer.fromJson(compound.getString("displayText")) : null;
        this.color = compound.contains("color") ? NBTHelper.readEnum(compound, "color", DyeColor.class) : null;
    }

    @Override
    public void writeCustomNBT(CompoundNBT compound) {
        super.writeCustomNBT(compound);

        if (this.owner != null) {
            compound.putUniqueId("owner", this.owner);
        }
        compound.putBoolean("networkRegistered", this.networkRegistered);
        if (this.displayText != null) {
            compound.putString("displayText", ITextComponent.Serializer.toJson(this.displayText));
        }
        if (this.color != null) {
            NBTHelper.writeEnum(compound, "color", this.color);
        }
    }
}
