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
import hellfirepvp.astralsorcery.client.effect.function.VFXAlphaFunction;
import hellfirepvp.astralsorcery.client.effect.function.VFXColorFunction;
import hellfirepvp.astralsorcery.client.effect.handler.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.vfx.FXColorEffectSphere;
import hellfirepvp.astralsorcery.client.event.effect.GatewayUIRenderHandler;
import hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS;
import hellfirepvp.astralsorcery.common.auxiliary.link.LinkableTileEntity;
import hellfirepvp.astralsorcery.common.data.world.GatewayCache;
import hellfirepvp.astralsorcery.common.lib.ColorsAS;
import hellfirepvp.astralsorcery.common.lib.DataAS;
import hellfirepvp.astralsorcery.common.lib.StructureTypesAS;
import hellfirepvp.astralsorcery.common.lib.TileEntityTypesAS;
import hellfirepvp.astralsorcery.common.network.play.server.PktPlayEffect;
import hellfirepvp.astralsorcery.common.structure.types.StructureType;
import hellfirepvp.astralsorcery.common.tile.base.TileEntityTick;
import hellfirepvp.astralsorcery.common.tile.base.TileOwned;
import hellfirepvp.astralsorcery.common.util.ColorUtils;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.PlayerReference;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.PointOfView;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.INameable;
import net.minecraft.util.Tuple;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.List;
import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TileCelestialGateway
 * Created by HellFirePvP
 * Date: 10.09.2020 / 17:07
 */
public class TileCelestialGateway extends TileEntityTick implements INameable, TileOwned, LinkableTileEntity {

    private static final BlockPos[] OFFSETS_ALLOWED_PREVIEW = new BlockPos[] {
            new BlockPos(-3, 0, -2),
            new BlockPos(-3, 0, -1),
            new BlockPos(-3, 0,  0),
            new BlockPos(-3, 0,  1),
            new BlockPos(-3, 0,  2),

            new BlockPos(-2, 0,  3),
            new BlockPos(-1, 0,  3),
            new BlockPos( 0, 0,  3),
            new BlockPos( 1, 0,  3),
            new BlockPos( 2, 0,  3),

            new BlockPos( 3, 0,  2),
            new BlockPos( 3, 0,  1),
            new BlockPos( 3, 0,  0),
            new BlockPos( 3, 0, -1),
            new BlockPos( 3, 0, -2),

            new BlockPos( 2, 0, -3),
            new BlockPos( 1, 0, -3),
            new BlockPos( 0, 0, -3),
            new BlockPos(-1, 0, -3),
            new BlockPos(-2, 0, -3)
    };

    private boolean networkRegistered = false;
    private ITextComponent displayText = null;
    private DyeColor color = null;

    private boolean locked = false;
    private PlayerReference owner = null;
    private final Map<Integer, PlayerReference> allowedUsers = new HashMap<>();

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
                    GatewayCache cache = DataAS.DOMAIN_AS.getData(world, DataAS.KEY_GATEWAY_CACHE);
                    if (cache.offerPosition(world, getPos())) {
                        cache.updateGatewayNode(getPos(), node -> {
                            node.setDisplayName(this.displayText);
                            node.setColor(this.color);
                        });
                        this.updateAccessInformation();
                        networkRegistered = true;
                        markForUpdate();
                    }
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
            Minecraft.getInstance().gameSettings.setPointOfView(PointOfView.FIRST_PERSON);
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

        Color gatewayColor = ColorUtils.flareColorFromDye(this.getColor().orElse(DyeColor.YELLOW));
        for (int i = 0; i < 3; i++) {
            Vector3 offset = new Vector3(this).add(-2, 0.05, -2);
            if (rand.nextBoolean()) {
                offset.add(5 * (rand.nextBoolean() ? 1 : 0), 0, rand.nextFloat() * 5);
            } else {
                offset.add(rand.nextFloat() * 5, 0, 5 * (rand.nextBoolean() ? 1 : 0));
            }

            Color c = MiscUtils.eitherOf(rand, Color.WHITE, gatewayColor, gatewayColor.brighter());
            EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                    .spawn(offset)
                    .setGravityStrength(-0.0001F)
                    .color(VFXColorFunction.constant(c))
                    .setScaleMultiplier(0.25F + rand.nextFloat() * 0.15F)
                    .setMaxAge(30 + rand.nextInt(30));
        }
        for (int i = 0; i < 2; i++) {
            Vector3 offset = new Vector3();
            MiscUtils.applyRandomOffset(offset, rand, 3F);
            offset.add(new Vector3(this)).add(0.5, 0, 0.5).setY(this.getPos().getY() + 0.05);

            Color c = MiscUtils.eitherOf(rand, Color.WHITE, gatewayColor, gatewayColor.brighter());
            EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                    .spawn(offset)
                    .setGravityStrength(-0.00004F)
                    .color(VFXColorFunction.constant(c))
                    .setScaleMultiplier(0.15F + rand.nextFloat() * 0.1F)
                    .setMaxAge(15 + rand.nextInt(10));
        }

        if (this.isLocked() && this.getOwner() != null) {
            Vector3 center = new Vector3(this).add(0.5, 0.2, 0.5);
            for (int i = 0; i < rand.nextInt(5) + 2; i++) {
                Vector3 pos = MiscUtils.getRandomCirclePosition(center, Vector3.RotAxis.Y_AXIS, 1.7);
                MiscUtils.applyRandomOffset(pos, rand, 0.05F);
                Color c = MiscUtils.eitherOf(rand, Color.WHITE, ColorsAS.EFFECT_BLUE_LIGHT, ColorsAS.EFFECT_BLUE_DARK);
                EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                        .spawn(pos)
                        .color(VFXColorFunction.constant(c))
                        .setScaleMultiplier(0.25F + rand.nextFloat() * 0.15F)
                        .alpha(VFXAlphaFunction.FADE_OUT)
                        .setMaxAge(15 + rand.nextInt(10));
            }
            for (int i = 0; i < rand.nextInt(3) + 1; i++) {
                Vector3 pos = MiscUtils.getRandomCirclePosition(center, Vector3.RotAxis.Y_AXIS, 1.1).addY(0.3);
                MiscUtils.applyRandomOffset(pos, rand, 0.05F);
                Color c = MiscUtils.eitherOf(rand, Color.WHITE, ColorsAS.EFFECT_BLUE_LIGHT, ColorsAS.EFFECT_BLUE_DARK);
                EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                        .spawn(pos)
                        .color(VFXColorFunction.constant(c))
                        .setScaleMultiplier(0.25F + rand.nextFloat() * 0.15F)
                        .alpha(VFXAlphaFunction.FADE_OUT)
                        .setMaxAge(15 + rand.nextInt(10));
            }
        }
    }

    public boolean isLocked() {
        return this.locked;
    }

    public boolean lock() {
        if (this.isLocked() || this.getOwner() == null) {
            return false;
        }
        this.locked = true;
        this.updateAccessInformation();
        return true;
    }

    public boolean unlock() {
        if (!this.isLocked()) {
            return false;
        }
        this.locked = false;
        this.updateAccessInformation();
        return true;
    }

    @Nullable
    @Override
    public PlayerReference getOwner() {
        return this.owner;
    }

    @Nullable
    @Override
    public PlayerReference setOwner(@Nullable PlayerReference player) {
        PlayerReference prevOwner = this.owner;
        this.owner = player;
        this.updateAccessInformation();
        return prevOwner;
    }

    public boolean canAddAllowedUser(PlayerEntity otherUser) {
        return this.canAddAllowedUser(PlayerReference.of(otherUser));
    }

    public boolean canAddAllowedUser(PlayerReference otherUser) {
        if (this.getOwner() == null) {
            return false;
        }
        if (this.allowedUsers.size() >= OFFSETS_ALLOWED_PREVIEW.length) {
            return false;
        }
        return !this.allowedUsers.containsValue(otherUser);
    }

    public boolean addAllowedUser(PlayerEntity otherUser) {
        return this.addAllowedUser(PlayerReference.of(otherUser));
    }

    public boolean addAllowedUser(PlayerReference otherUser) {
        if (!this.canAddAllowedUser(otherUser)) {
            return false;
        }
        List<Integer> availableIndices = new ArrayList<>();
        for (int i = 0; i < OFFSETS_ALLOWED_PREVIEW.length; i++) {
            if (!this.allowedUsers.containsKey(i)) {
                availableIndices.add(i);
            }
        }
        if (availableIndices.isEmpty()) {
            return false;
        }

        Collections.shuffle(availableIndices);
        this.allowedUsers.put(MiscUtils.getRandomEntry(availableIndices, rand), otherUser);
        this.updateAccessInformation();
        return true;
    }

    @Nullable
    public PlayerReference removeAllowedUser(UUID otherUser) {
        if (this.allowedUsers.isEmpty()) {
            return null;
        }
        for (Map.Entry<Integer, PlayerReference> entry : this.allowedUsers.entrySet()) {
            if (entry.getValue().getPlayerUUID().equals(otherUser)) {
                this.allowedUsers.remove(entry.getKey());
                this.updateAccessInformation();
                return entry.getValue();
            }
        }
        return null;
    }

    public Map<Integer, PlayerReference> getAllowedUsers() {
        if (this.getOwner() == null) {
            return Collections.emptyMap();
        }
        return Collections.unmodifiableMap(this.allowedUsers);
    }

    private void updateAccessInformation() {
        DataAS.DOMAIN_AS.getData(world, DataAS.KEY_GATEWAY_CACHE).updateGatewayNode(this.getPos(), node -> {
            node.setLocked(this.isLocked());
            node.setOwner(this.getOwner());
            node.setAllowedUsers(this.allowedUsers);
        });
        this.markForUpdate();
    }

    @OnlyIn(Dist.CLIENT)
    public static void playAccessRevokeEffect(PktPlayEffect pktPlayEffect) {

    }

    public static BlockPos getAllowedUserOffset(int index) {
        return OFFSETS_ALLOWED_PREVIEW[MathHelper.clamp(index, 0, OFFSETS_ALLOWED_PREVIEW.length - 1)];
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
    public boolean seesSkyInNoSkyWorlds() {
        return true;
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

    public Optional<DyeColor> getColor() {
        return Optional.ofNullable(this.color);
    }

    @Override
    public void readCustomNBT(CompoundNBT compound) {
        super.readCustomNBT(compound);

        this.networkRegistered = compound.getBoolean("networkRegistered");
        this.displayText = compound.contains("displayText") ? ITextComponent.Serializer.getComponentFromJson(compound.getString("displayText")) : null;
        this.color = compound.contains("color") ? NBTHelper.readEnum(compound, "color", DyeColor.class) : null;

        this.locked = compound.getBoolean("locked");
        this.owner = NBTHelper.readOptional(compound, "owningPlayer", PlayerReference::deserialize);
        this.allowedUsers.clear();
        NBTHelper.readList(compound, "allowedUsers", Constants.NBT.TAG_COMPOUND, nbt -> {
            CompoundNBT tag = (CompoundNBT) nbt;
            return new Tuple<>(tag.getInt("index"), PlayerReference.deserialize(tag.getCompound("player")));
        }).forEach(tpl -> this.allowedUsers.put(tpl.getA(), tpl.getB()));
    }

    @Override
    public void writeCustomNBT(CompoundNBT compound) {
        super.writeCustomNBT(compound);

        compound.putBoolean("networkRegistered", this.networkRegistered);
        if (this.displayText != null) {
            compound.putString("displayText", ITextComponent.Serializer.toJson(this.displayText));
        }
        if (this.color != null) {
            NBTHelper.writeEnum(compound, "color", this.color);
        }

        compound.putBoolean("locked", this.locked);
        NBTHelper.writeOptional(compound, "owningPlayer", this.owner, (tag, playerRef) -> playerRef.writeToNBT(tag));
        NBTHelper.writeList(compound, "allowedUsers", this.allowedUsers.entrySet(), entry -> {
            CompoundNBT tag = new CompoundNBT();
            tag.putInt("index", entry.getKey());
            tag.put("player", entry.getValue().serialize());
            return tag;
        });
    }

    @Override
    public void onBlockLinkCreate(PlayerEntity player, BlockPos other) {}

    @Override
    public void onEntityLinkCreate(PlayerEntity player, LivingEntity linked) {
        if (linked instanceof PlayerEntity) {
            if (this.addAllowedUser((PlayerEntity) linked)) {

                ITextComponent accessGrantedMessage = new TranslationTextComponent(
                        "astralsorcery.misc.link.gateway.link",
                        linked.getDisplayName())
                        .mergeStyle(TextFormatting.GREEN);
                player.sendMessage(accessGrantedMessage, Util.DUMMY_UUID);
                linked.sendMessage(accessGrantedMessage, Util.DUMMY_UUID);
            }
        }
    }

    @Override
    public boolean tryLinkBlock(PlayerEntity player, BlockPos other) {
        return false;
    }

    @Override
    public boolean tryLinkEntity(PlayerEntity player, LivingEntity other) {
        return other instanceof PlayerEntity && this.canAddAllowedUser((PlayerEntity) other);
    }

    @Override
    public boolean tryUnlink(PlayerEntity player, BlockPos other) {
        return false;
    }

    @Override
    public List<BlockPos> getLinkedPositions() {
        return Collections.emptyList();
    }

    @Override
    public boolean onSelect(PlayerEntity player) {
        return false;
    }
}
