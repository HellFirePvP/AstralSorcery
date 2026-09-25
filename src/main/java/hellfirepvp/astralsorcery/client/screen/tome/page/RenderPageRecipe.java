/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.screen.tome.page;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat;
import hellfirepvp.astralsorcery.client.ClientProxy;
import hellfirepvp.astralsorcery.client.effect.function.FXAlphaFunction;
import hellfirepvp.astralsorcery.client.effect.function.FXColorFunction;
import hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS;
import hellfirepvp.astralsorcery.client.lib.TexturesAS;
import hellfirepvp.astralsorcery.client.resource.AbstractRenderTexture;
import hellfirepvp.astralsorcery.client.resource.AtlasTexture;
import hellfirepvp.astralsorcery.client.screen.effect.ScreenEffectContainer;
import hellfirepvp.astralsorcery.client.screen.effect.ScreenEffectTicketManager;
import hellfirepvp.astralsorcery.client.screen.effect.ticket.StaticIdentifierTicket;
import hellfirepvp.astralsorcery.client.screen.tome.TomePagesScreen;
import hellfirepvp.astralsorcery.client.util.*;
import hellfirepvp.astralsorcery.client.util.tooltip.TooltipUtil;
import hellfirepvp.astralsorcery.common.constellation.BaseConstellation;
import hellfirepvp.astralsorcery.common.ingredient.IngredientBridge;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import hellfirepvp.astralsorcery.common.lumen.Lumen;
import hellfirepvp.astralsorcery.common.research.ResearchManager;
import hellfirepvp.astralsorcery.common.research.ResearchNode;
import hellfirepvp.astralsorcery.common.research.condition.ResearchNodeVisibility;
import hellfirepvp.astralsorcery.common.research.data.ResearchNodeLoader;
import hellfirepvp.astralsorcery.common.research.tome.TomePage;
import hellfirepvp.astralsorcery.common.util.ColorUtil;
import hellfirepvp.astralsorcery.common.util.IngredientUtil;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import hellfirepvp.astralsorcery.common.util.data.ColorWrapper;
import hellfirepvp.astralsorcery.common.util.data.IntRectangle;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

import javax.annotation.Nullable;
import java.text.DecimalFormat;
import java.util.*;
import java.util.function.Supplier;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: RenderPageRecipe
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public abstract class RenderPageRecipe<T extends Recipe<?>> extends RenderPage {

    private static final DecimalFormat FLUID_BUCKET_FORMAT = new DecimalFormat("#.###");
    private static final Map<ResourceKey<RecipeType<?>>, RenderPageRecipe.Factory<?>> PAGE_FACTORIES = new HashMap<>();

    protected SequencedMap<IntRectangle, TooltipInfo> thisFrameHovers = new LinkedHashMap<>();
    protected List<IntRectangle> recipeIdCopyHovers = new ArrayList<>();

    private final Supplier<RecipeType<T>> recipeType;
    private final ResourceLocation recipeId;

    public RenderPageRecipe(@Nullable ResearchNode node, int nodePage, Supplier<RecipeType<T>> recipeType, ResourceLocation recipeId) {
        super(node, nodePage);
        this.recipeType = recipeType;
        this.recipeId = recipeId;
    }

    public static Factory<?> registerPageFactory(Holder<RecipeType<?>> type, Factory<?> factory) {
        return registerPageFactory(type.getKey(), factory);
    }

    public static Factory<?> registerPageFactory(ResourceKey<RecipeType<?>> type, Factory<?> factory) {
        PAGE_FACTORIES.put(type, factory);
        return factory;
    }

    public static Optional<Factory<?>> getFactoryForType(ResourceKey<RecipeType<?>> type) {
        return Optional.ofNullable(PAGE_FACTORIES.get(type));
    }

    protected void clearFrame() {
        this.thisFrameHovers.clear();
        this.recipeIdCopyHovers.clear();
    }

    public Optional<RecipeHolder<T>> resolveRecipe() {
        return Optional.ofNullable(Minecraft.getInstance().getConnection())
                .map(ClientPacketListener::getRecipeManager)
                .flatMap(recipeManager -> recipeManager.byKey(this.recipeId))
                .filter(recipe -> recipe.value().getType() == this.recipeType.get())
                .map(MiscUtil::cast);
    }

    public Optional<RecipeHolder<T>> resolveRecipeOrWriteError(GuiGraphics graphics, int x, int y) {
        Optional<RecipeHolder<T>> recipeOpt = this.resolveRecipe();
        if (recipeOpt.isEmpty()) {
            Component errorMsg = Component.translatable("tome.research.info.missing_recipe", this.recipeId)
                    .withStyle(ChatFormatting.RED);
            graphics.drawString(Minecraft.getInstance().font, errorMsg, x, y, ColorWrapper.WHITE.getColor(), true);
        }
        return recipeOpt;
    }

    public StaticIdentifierTicket.Container getEffectContainer() {
        return ScreenEffectTicketManager.getInstance().refreshOrCreate(StaticIdentifierTicket.ofIdentifier(this.recipeId));
    }

    public void renderEffects(GuiGraphics graphics, float pTicks) {
        this.getEffectContainer().renderAll(graphics, pTicks);
    }

    public void renderHoverTooltips(GuiGraphics graphics, float mouseX, float mouseY) {
        graphics.pose().pushPose();
        graphics.pose().translate(0, 0, 400);
        this.thisFrameHovers.reversed().entrySet().stream()
                .filter(entry -> entry.getKey().contains(mouseX, mouseY))
                .findFirst()
                .map(Map.Entry::getValue)
                .ifPresent(tooltip ->
                        TooltipUtil.blueColor(() -> {
                            ItemStack firstStack = tooltip.stack();
                            List<Component> components = new ArrayList<>(tooltip.lines());
                            if (!firstStack.isEmpty()) {
                                Optional<ResearchNode> source = ResearchNodeLoader.getInstance().lookupNode(firstStack.getItem());
                                if (source.isPresent() &&
                                        !source.get().equals(this.getResearchNode()) &&
                                        source.get().getVisibility(ResearchManager.getClientProgress()).getType() == ResearchNodeVisibility.Type.VISIBLE) {
                                    components.add(Component.empty());
                                    components.add(Component.translatable("screen.astralsorcery.element.recipe_lookup").withStyle(ChatFormatting.GRAY));
                                }
                            }
                            graphics.renderComponentTooltip(Minecraft.getInstance().font, components, Mth.floor(mouseX), Mth.floor(mouseY), firstStack);
                }));
        graphics.pose().popPose();
    }

    @Override
    public boolean propagateMouseClick(double mouseX, double mouseZ) {
        return this.handleRecipeIdCopyClick(Mth.floor(mouseX), Mth.floor(mouseZ)) ||
                this.handleLookupClick(Mth.floor(mouseX), Mth.floor(mouseZ));
    }

    public boolean handleRecipeIdCopyClick(int mouseX, int mouseY) {
        if (Minecraft.getInstance().getDebugOverlay().showDebugScreen() && Screen.hasControlDown() && Minecraft.getInstance().player != null)  {
            for (IntRectangle rect : this.recipeIdCopyHovers) {
                if (rect.contains(mouseX, mouseY)) {
                    ResourceLocation id = this.resolveRecipe().map(RecipeHolder::id).orElse(null);
                    if (id != null) {
                        Component copyMsg = Component.translatable("screen.astralsorcery.element.recipe_debug.ctrl_copy.copied", id.toString());

                        Minecraft.getInstance().keyboardHandler.setClipboard(id.toString());
                        Minecraft.getInstance().player.sendSystemMessage(copyMsg);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean handleLookupClick(int mouseX, int mouseY) {
        for (Map.Entry<IntRectangle, TooltipInfo> entry : this.thisFrameHovers.entrySet()) {
            if (entry.getKey().contains(mouseX, mouseY)) {
                ItemStack stack = entry.getValue().stack();
                if (!stack.isEmpty()) {
                    Optional<ResearchNode> source = ResearchNodeLoader.getInstance().lookupNode(stack.getItem());
                    if (source.isPresent() &&
                            !source.get().equals(this.getResearchNode()) &&
                            source.get().getVisibility(ResearchManager.getClientProgress()).getType() == ResearchNodeVisibility.Type.VISIBLE) {
                        Minecraft.getInstance().setScreen(TomePagesScreen.fromResearchNode(source.get(), 0));
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void renderConstellation(GuiGraphics graphics, int offsetX, int offsetY, int width, int height, BaseConstellation cst) {
        ColorWrapper color = ColorUtil.blendColors(cst.getConstellationColor(), ColorWrapper.opaque(0x555555), 0.3F);
        RenderSystem.enableBlend();
        Blending.DEFAULT.apply();

        RenderConstellationUtil.drawConstellationUI(
                color,
                cst,
                graphics.pose(),
                offsetX, offsetY,
                width, height,
                3F,
                () -> 0.8F,
                true,
                false);

        RenderSystem.disableBlend();
    }

    public void renderRecipeHeader(GuiGraphics graphics, int pageX, int pageY, boolean needsNightTime) {
        this.renderRecipeHeader(graphics, pageX, pageY, needsNightTime, Collections.emptyList());
    }

    public void renderRecipeHeader(GuiGraphics graphics, int pageX, int pageY, boolean needsNightTime, List<Component> hoverTooltips) {
        AbstractRenderTexture tex = needsNightTime ? TexturesAS.SCREEN_TOME_PAGE_HEADER_NIGHT : TexturesAS.SCREEN_TOME_PAGE_HEADER_NO_NIGHT;
        int offsetX = pageX + (TomePage.DEFAULT_WIDTH / 2 - 80 / 2);
        int offsetY = pageY + 2;
        if (needsNightTime) {
            hoverTooltips = new ArrayList<>(hoverTooltips);
            hoverTooltips.add(Component.translatable("tome.research.info.recipe_night").withStyle(ChatFormatting.GRAY));
        }

        RenderSystem.enableBlend();
        Blending.DEFAULT.apply();
        tex.bindTexture();
        RenderUtil.draw(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR, GameRenderer::getPositionTexColorShader, buf -> {
            RenderQuadUtil.rect(buf, graphics.pose(), offsetX, offsetY, 80, 13)
                    .draw();
        });
        RenderSystem.disableBlend();

        if (!hoverTooltips.isEmpty()) {
            IntRectangle rct = new IntRectangle(offsetX, offsetY, 80, 13);
            this.thisFrameHovers.put(rct, new TooltipInfo(hoverTooltips));
        }
    }

    public void renderInput(GuiGraphics graphics, int offsetX, int offsetY, IngredientBridge ingredient) {
        long tick = ClientProxy.getClientTick() + offsetX * 20L + offsetY * 20L;
        this.renderInput(graphics, offsetX, offsetY, tick, ingredient);
    }

    public void renderInput(GuiGraphics graphics, int offsetX, int offsetY, long tickOffset, IngredientBridge ingredient) {
        switch (ingredient.getIngredientType()) {
            case ITEM -> this.renderInput(graphics, offsetX, offsetY, tickOffset, ingredient.getIngredient());
            case FLUID -> this.renderInput(graphics, offsetX, offsetY, tickOffset, ingredient.getFluidIngredient());
        }
    }

    public void renderInput(GuiGraphics graphics, int offsetX, int offsetY, Ingredient ingredient) {
        long tick = ClientProxy.getClientTick() + offsetX * 20L + offsetY * 20L;
        this.renderInput(graphics, offsetX, offsetY, tick, ingredient);
    }

    public void renderInput(GuiGraphics graphics, int offsetX, int offsetY, long tickOffset, Ingredient ingredient) {
        ItemStack display = IngredientUtil.getRandomDisplayStack(ingredient, tickOffset);
        if (!display.isEmpty()) {
            this.renderScaledItem(graphics, offsetX, offsetY, display, 1F);
            this.addHoverInfos(offsetX, offsetY, new TooltipInfo(display, this.getTooltip(display, ingredient)));
        }
    }

    public void renderInput(GuiGraphics graphics, int offsetX, int offsetY, List<ItemStack> inputs) {
        long tick = ClientProxy.getClientTick() + offsetX * 20L + offsetY * 20L;
        this.renderInput(graphics, offsetX, offsetY, tick, inputs);
    }

    public void renderInput(GuiGraphics graphics, int offsetX, int offsetY, long tickOffset, List<ItemStack> inputs) {
        ItemStack display = IngredientUtil.getRandomDisplayStack(inputs, tickOffset);
        if (!display.isEmpty()) {
            this.renderScaledItem(graphics, offsetX, offsetY, display, 1F);
            this.addHoverInfos(offsetX, offsetY, new TooltipInfo(display, this.getTooltip(display, (Ingredient) null)));
        }
    }

    public void renderInput(GuiGraphics graphics, int offsetX, int offsetY, SizedFluidIngredient ingredient) {
        long tick = ClientProxy.getClientTick() + offsetX * 20L + offsetY * 20L;
        this.renderInput(graphics, offsetX, offsetY, tick, ingredient);
    }

    public void renderInput(GuiGraphics graphics, int offsetX, int offsetY, long tickOffset, SizedFluidIngredient ingredient) {
        ItemStack display = IngredientUtil.getRandomDisplayStack(ingredient, tickOffset);
        if (!display.isEmpty()) {
            this.renderScaledItem(graphics, offsetX, offsetY, display, 1F);
            this.addHoverInfos(offsetX, offsetY, new TooltipInfo(display, this.getTooltip(display, ingredient)));
        }
    }

    public void renderLiquidInput(GuiGraphics graphics, int offsetX, int offsetY, FluidStack fluidStack) {
        TextureAtlasSprite sprite = RenderSpriteUtil.getTexture(fluidStack);
        int tint = IClientFluidTypeExtensions.of(fluidStack.getFluid()).getTintColor(fluidStack);
        ColorWrapper color = ColorWrapper.opaque(tint);
        PoseStack pose = graphics.pose();

        RenderSystem.enableBlend();
        Blending.DEFAULT.apply();
        AtlasTexture.getBlockAtlas().bindTexture();

        pose.pushPose();
        pose.translate(0, 0, 300);
        RenderUtil.draw(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR, GameRenderer::getPositionTexColorShader, buf -> {
            RenderQuadUtil.rect(buf, graphics.pose(), offsetX, offsetY, 16, 16)
                    .color(color)
                    .tex(sprite)
                    .draw();
        });
        pose.popPose();

        RenderSystem.disableBlend();

        this.addHoverInfos(offsetX, offsetY, new TooltipInfo(this.getInputTooltip(fluidStack)));
    }

    public void renderLiquidOutput(GuiGraphics graphics, int offsetX, int offsetY, FluidStack fluidStack) {
        TextureAtlasSprite sprite = RenderSpriteUtil.getTexture(fluidStack);
        int tint = IClientFluidTypeExtensions.of(fluidStack.getFluid()).getTintColor(fluidStack);
        ColorWrapper color = ColorWrapper.opaque(tint);
        PoseStack pose = graphics.pose();

        RenderSystem.enableBlend();
        Blending.DEFAULT.apply();
        AtlasTexture.getBlockAtlas().bindTexture();

        pose.pushPose();
        pose.translate(0, 0, 300);
        RenderUtil.draw(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR, GameRenderer::getPositionTexColorShader, buf -> {
            RenderQuadUtil.rect(buf, graphics.pose(), offsetX, offsetY, 16, 16)
                    .color(color)
                    .tex(sprite)
                    .draw();
        });
        pose.popPose();

        RenderSystem.disableBlend();

        List<Component> tooltip = new ArrayList<>();
        this.addTooltip(fluidStack, tooltip);
        if (Minecraft.getInstance().getDebugOverlay().showDebugScreen()) {
            this.resolveRecipe().ifPresent(recipe -> {
                tooltip.add(Component.empty());
                tooltip.add(Component.translatable("screen.astralsorcery.element.recipe_debug.registry_name", recipe.id().toString())
                        .withStyle(ChatFormatting.LIGHT_PURPLE).withStyle(ChatFormatting.ITALIC));
                tooltip.add(Component.translatable("screen.astralsorcery.element.recipe_debug.ctrl_copy")
                        .withStyle(ChatFormatting.LIGHT_PURPLE).withStyle(ChatFormatting.ITALIC));

            });
        }
        this.addHoverInfos(offsetX, offsetY, new TooltipInfo(tooltip));
        this.recipeIdCopyHovers.add(new IntRectangle(offsetX, offsetY, 16, 16));
    }

    public void renderLumen(int offsetX, int offsetY, Lumen lumen) {
        ColorWrapper color = lumen.getColor(ClientProxy.getClientTick());
        MutableComponent name = lumen.getHoverName().copy()
                .withColor(color.getColor());
        this.addHoverInfos(offsetX - 8, offsetY - 8, 32, 32, new TooltipInfo(name));
    }

    public void renderOutput(GuiGraphics graphics, int offsetX, int offsetY, int width, int height, ItemStack output) {
        if (output.isEmpty()) return;

        this.renderScaledItem(graphics, offsetX, offsetY, output, 1F);

        List<Component> tooltip = new ArrayList<>();
        this.addTooltip(output, tooltip);
        if (Minecraft.getInstance().getDebugOverlay().showDebugScreen()) {
            this.resolveRecipe().ifPresent(recipe -> {
                tooltip.add(Component.empty());
                tooltip.add(Component.translatable("screen.astralsorcery.element.recipe_debug.registry_name", recipe.id().toString())
                        .withStyle(ChatFormatting.LIGHT_PURPLE).withStyle(ChatFormatting.ITALIC));
                tooltip.add(Component.translatable("screen.astralsorcery.element.recipe_debug.ctrl_copy")
                        .withStyle(ChatFormatting.LIGHT_PURPLE).withStyle(ChatFormatting.ITALIC));

            });
        }
        this.addHoverInfos(offsetX, offsetY, width, height, new TooltipInfo(output, tooltip));
        this.recipeIdCopyHovers.add(new IntRectangle(offsetX, offsetY, width, height));
    }

    public void renderScaledItem(GuiGraphics graphics, float offsetX, float offsetY, ItemStack stack, float scale) {
        PoseStack pose = graphics.pose();

        Lighting.setupForEntityInInventory();

        pose.pushPose();
        pose.translate(offsetX + 8F, offsetY + 8F, 0);
        if (!(stack.getItem() instanceof BlockItem)) {
            pose.translate(0, 0, 50); //Avoid clipping into other items
        }
        pose.scale(scale, scale, 1);
        pose.translate(-8F, -8F, 0);
        graphics.renderItem(stack, 0, 0);
        graphics.renderItemDecorations(Minecraft.getInstance().font, stack, 0, 0);
        pose.popPose();

        Lighting.setupFor3DItems();
    }

    protected void addHoverInfos(int offsetX, int offsetY, TooltipInfo tooltip) {
        this.addHoverInfos(offsetX, offsetY, 16, 16, tooltip);
    }

    protected void addHoverInfos(int offsetX, int offsetY, int width, int height, TooltipInfo tooltip) {
        this.thisFrameHovers.put(new IntRectangle(offsetX, offsetY, width, height), tooltip);
    }

    protected List<Component> getInputTooltip(FluidStack fluidStack) {
        List<Component> tooltip = new ArrayList<>();
        this.addTooltip(fluidStack, tooltip);

        Component countCmp;
        int neededCount = fluidStack.getAmount();
        if (neededCount >= 1000) {
            String bucketFormat = FLUID_BUCKET_FORMAT.format(neededCount / 1000.0D);
            countCmp = Component.translatable("ingredient.astralsorcery.fluid.description.bucket", bucketFormat);
        } else {
            countCmp = Component.translatable("ingredient.astralsorcery.fluid.description.milli_bucket", neededCount);
        }

        tooltip.add(Component.empty());
        tooltip.add(Component.translatable("ingredient.astralsorcery.fluid.description", countCmp)
                .withStyle(ChatFormatting.GRAY));
        return tooltip;
    }

    protected List<Component> getTooltip(ItemStack stack, @Nullable SizedFluidIngredient ingredient) {
        List<Component> tooltip = new ArrayList<>();
        this.addTooltip(stack, tooltip);

        if (ingredient != null && ingredient.ingredient().isSimple()) {
            List<Fluid> fluids = IngredientUtil.guessFluids(ingredient.ingredient());
            if (fluids.size() == 1) { //More would indicate some larger shenanigans; would probably mean there's more complexity
                // And no, i'm not going around figuring it out
                // cause the whole stuff around CompoundIngredients and difference, intersection, ... ingredients is incredibly miserable
                // to find out how to formulate it into words.
                // God i wish there was some way to ACTUALLY find out what an ingredient does and form it into human-readable words
                // or literally anything to display it in some concise way. It's incredbile that it's so extremely generic in its classes and stuff
                // but impossible to tell what it actaully expects from the user what's configured in a DATAPACK.
                Fluid fluid = fluids.getFirst();

                Component countCmp;
                int neededCount = ingredient.amount();
                if (neededCount >= 1000) {
                    String bucketFormat = FLUID_BUCKET_FORMAT.format(neededCount / 1000.0D);
                    countCmp = Component.translatable("ingredient.astralsorcery.fluid.description.bucket", bucketFormat);
                } else {
                    countCmp = Component.translatable("ingredient.astralsorcery.fluid.description.milli_bucket", neededCount);
                }

                tooltip.add(Component.empty());
                tooltip.add(Component.translatable("ingredient.astralsorcery.fluid_container.description", fluid.getFluidType().getDescription(), countCmp)
                        .withStyle(ChatFormatting.GRAY));
            }
        }
        return tooltip;
    }

    protected List<Component> getTooltip(ItemStack stack, @Nullable Ingredient ingredient) {
        List<Component> tooltip = new ArrayList<>();
        this.addTooltip(stack, tooltip);

        if (ingredient != null && ingredient.isSimple()) {
            // Same thing here btw as with the fluid ingredient. Different classes, same concept. Dont' you think this is better.
            TagKey<Item> guessedKey = IngredientUtil.guessIngredientTag(ingredient);
            if (guessedKey != null) {
                tooltip.add(Component.empty());
                tooltip.add(Component.translatable("ingredient.astralsorcery.tag.description", guessedKey.location().toString())
                        .withStyle(ChatFormatting.GRAY));
            }
        }
        return tooltip;
    }

    protected void addTooltip(ItemStack stack, List<Component> out) {
        try {
            TooltipFlag flag = Minecraft.getInstance().options.advancedItemTooltips ? TooltipFlag.Default.ADVANCED : TooltipFlag.Default.NORMAL;
            Item.TooltipContext ctx = Item.TooltipContext.of(Minecraft.getInstance().level);
            out.addAll(stack.getTooltipLines(ctx, Minecraft.getInstance().player, flag));
        } catch (Exception exc) {
            out.add(Component.translatable("ingredient.astralsorcery.tooltip.error").withStyle(ChatFormatting.RED));
        }
    }

    protected void addTooltip(FluidStack stack, List<Component> out) {
        try {
            Component displayName = stack.getHoverName();
            out.add(displayName);

            if (Minecraft.getInstance().options.advancedItemTooltips) {
                ResourceLocation regName = BuiltInRegistries.FLUID.getKey(stack.getFluid());
                if (regName != BuiltInRegistries.FLUID.getDefaultKey()) {
                    out.add(Component.literal(regName.toString()).withStyle(ChatFormatting.DARK_GRAY));
                }
            }
        } catch (Exception exc) {
            out.add(Component.translatable("ingredient.astralsorcery.tooltip.error").withStyle(ChatFormatting.RED));
        }
    }

    protected void playLumenEffect(ScreenEffectContainer<?, ?> container, int offsetX, int offsetY, Lumen lumen) {
        RandomSource rand = RandomSource.create();
        long tick = ClientProxy.getClientTick();

        float scale = 15F + rand.nextFloat() * 10F;
        int age = 30 + rand.nextInt(10);
        Vector3 mot = Vector3.random(rand).setZ(0).normalize().multiply(0.35F + rand.nextFloat() * 0.3F);

        container.createParticle(EffectTemplatesAS.SCREEN_PLANE_PARTICLE, offsetX, offsetY)
                .color(rand.nextInt(5) == 0 ? FXColorFunction.WHITE : FXColorFunction.constant(lumen.getColor(tick)))
                .alpha(FXAlphaFunction.fadeIn(10).andThen(FXAlphaFunction.FADE_OUT))
                .setScale(scale)
                .setMotion(mot)
                .setMaxAge(age);

        container.createParticle(EffectTemplatesAS.SCREEN_PLANE_PARTICLE, offsetX, offsetY)
                .alpha(FXAlphaFunction.fadeIn(10).andThen(FXAlphaFunction.FADE_OUT))
                .setScale(scale * 0.3F)
                .setMotion(mot)
                .setMaxAge(Mth.ceil(age * 0.7F));

        if (rand.nextInt(14) == 0) {
            Vector3 rPos = Vector3.random(rand).setZ(0).multiply(20F).add(offsetX, offsetY, 0);
            container.createParticle(EffectTemplatesAS.SCREEN_LUMEN_PARTICLE, rPos.getX(), rPos.getY())
                    .setSprite(TexturesAS.ATLAS_LUMEN, RegistriesAS.REGISTRY_LUMEN.getKey(lumen))
                    .alpha(FXAlphaFunction.PYRAMID)
                    .color(FXColorFunction.constant(lumen.getColor(tick)))
                    .setMaxAge(40 + rand.nextInt(20));
        }
    }

    protected void playLumenConnectionEffect(ScreenEffectContainer<?, ?> container, int fromX, int fromY, int toX, int toY, Lumen lumen, ConnectionShape shape) {
        RandomSource rand = RandomSource.create();
        long tick = ClientProxy.getClientTick();

        int cycleLength = 35;
        float cycleTravel = cycleLength - 15;
        long cycle = tick % cycleLength;
        if (cycle >= cycleTravel) return;

        float cyclePart = cycle / cycleTravel;
        float spawnX, spawnY;

        switch (shape) {
            case BEZIER -> {
                long cycleNum = tick / cycleLength;
                long seed = cycleNum * 0x9E3779B97F4A7C15L ^ ((long) fromX * 73856093L) ^ ((long) fromY * 19349663L);
                RandomSource seededRand = RandomSource.create(seed);
                float randomOffset = (0.15F + seededRand.nextFloat() * 0.5F) * (seededRand.nextBoolean() ? 1 : -1);

                float dirX = toX - fromX;
                float dirY = toY - fromY;
                float length = (float) Math.sqrt(dirX * dirX + dirY * dirY);

                float perpX = -dirY / length;
                float perpY =  dirX / length;

                float offset = randomOffset * length * 0.8F;
                float ctrlX = (fromX + toX) * 0.5F + perpX * offset;
                float ctrlY = (fromY + toY) * 0.5F + perpY * offset;

                float inv = 1F - cyclePart;
                spawnX = inv * inv * fromX + 2F * inv * cyclePart * ctrlX + cyclePart * cyclePart * toX;
                spawnY = inv * inv * fromY + 2F * inv * cyclePart * ctrlY + cyclePart * cyclePart * toY;
            }
            case STRAIGHT ->  {
                spawnX = Mth.lerp(cyclePart, fromX, toX);
                spawnY = Mth.lerp(cyclePart, fromY, toY);
            }
            default -> {
                return;
            }
        }

        for (int i = 0; i < 3; i++) {
            float scale = 10F + rand.nextFloat() * 3F;
            int age = 30 + rand.nextInt(15);
            Vector3 mot = Vector3.random(rand).setZ(0).normalize().multiply(0.08F + rand.nextFloat() * 0.02F);
            float x = rand.nextFloat();
            float y = rand.nextFloat();

            container.createParticle(EffectTemplatesAS.SCREEN_PLANE_PARTICLE, spawnX + x, spawnY + y)
                    .color(FXColorFunction.constant(lumen.getColor(tick)))
                    .alpha(FXAlphaFunction.fadeIn(6).andThen(FXAlphaFunction.FADE_OUT))
                    .setScale(scale)
                    .setMotion(mot)
                    .setMaxAge(age);

            container.createParticle(EffectTemplatesAS.SCREEN_PLANE_PARTICLE, spawnX + x, spawnY + y)
                    .alpha(FXAlphaFunction.fadeIn(6).andThen(FXAlphaFunction.FADE_OUT))
                    .setScale(scale * 0.3F)
                    .setMotion(mot)
                    .setMaxAge(Mth.ceil(age * 0.7F));
        }
    }

    public enum ConnectionShape {

        STRAIGHT,
        BEZIER

    }

    public record TooltipInfo(ItemStack stack, List<Component> lines) {

        public TooltipInfo(List<Component> lines) {
            this(ItemStack.EMPTY, lines);
        }

        public TooltipInfo(Component singleLine) {
            this(List.of(singleLine));
        }
    }

    public interface Factory<T extends RenderPage> {

        T create(@Nullable ResearchNode node, int nodePage, ResourceLocation recipeId);

    }
}
