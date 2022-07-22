package org.teacon.exhibitiontoolkit.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;
import org.teacon.exhibitiontoolkit.menu.PowerSupplyMenu;
import org.teacon.exhibitiontoolkit.network.ExhibitionToolkitNetworking;
import org.teacon.exhibitiontoolkit.network.UpdatePowerSupplyData;

public final class PowerSupplyScreen extends AbstractContainerScreen<PowerSupplyMenu> {

    private static final ResourceLocation BG_LOCATION = new ResourceLocation("exhibition_toolkit", "textures/gui/power_supply.png");

    private EditBox input;
    private ButtonWithHighlight minus, plus;
    private int status = 1, power = -1;

    public PowerSupplyScreen(PowerSupplyMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.status = menu.dataHolder.status;
        this.power = menu.dataHolder.power;
        this.imageWidth = 170;
        this.imageHeight = 105;
    }

    public void onToggled(Button toggle) {
        this.status = this.status == 0 ? 1 : 0;
        ExhibitionToolkitNetworking.channel.send(PacketDistributor.SERVER.with(null), new UpdatePowerSupplyData(0, this.status));
    }

    @Override
    protected void init() {
        super.init();
        // The minus button
        this.minus = this.addRenderableWidget(new ButtonWithHighlight(this.leftPos + 9, this.topPos + 44, 16, 16, TextComponent.EMPTY,
                btn -> this.input.setValue(Integer.toString(--this.power)), BG_LOCATION, 256, 256, 170, 44, 170, 60, 170, 76));
        // The plus button
        this.plus = this.addRenderableWidget(new ButtonWithHighlight(this.leftPos + 145, this.topPos + 44, 16, 16, TextComponent.EMPTY,
                btn -> this.input.setValue(Integer.toString(++this.power)), BG_LOCATION, 256, 256, 186, 44, 186, 60, 186, 76));
        this.addRenderableWidget(new InvisibleButton(this.leftPos + 125, this.topPos + 20, 32, 13, TextComponent.EMPTY, this::onToggled));
        // The input field
        this.input = new EditBox(this.font, this.leftPos + 32, this.topPos + 48, 100, 16, TextComponent.EMPTY);
        this.input.setCanLoseFocus(false);
        this.input.setTextColor(-1);
        this.input.setTextColorUneditable(-1);
        this.input.setBordered(false);
        this.input.setMaxLength(11);
        this.input.setResponder(newValue -> {
            try {
                this.power = Integer.parseInt(newValue);
                this.input.setTextColor(-1);
                ExhibitionToolkitNetworking.channel.send(PacketDistributor.SERVER.with(null), new UpdatePowerSupplyData(1, this.power));
            } catch (Exception e) {
                this.input.setTextColor(0xFFFF0000);
            }
        });
        this.input.setValue(Integer.toString(this.power));
        this.addWidget(this.input);
        this.setInitialFocus(this.input);
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        this.input.tick();
        this.minus.tick();
        this.plus.tick();
    }

    @Override
    public void resize(Minecraft mc, int pWidth, int pHeight) {
        String s = this.input.getValue();
        super.resize(mc, pWidth, pHeight);
        this.input.setValue(s);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            var mc = this.minecraft;
            if (mc != null) {
                var p = mc.player;
                if (p != null) {
                    p.closeContainer();
                }
            }
        }
        return this.input.keyPressed(keyCode, scanCode, modifiers)
                || this.input.canConsumeInput()
                || super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void render(PoseStack transform, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(transform);
        super.render(transform, mouseX, mouseY, partialTick);
        RenderSystem.disableBlend();
        this.input.render(transform, mouseX, mouseY, partialTick);
        this.renderTooltip(transform, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(PoseStack transform, int mouseX, int mouseY) {
        this.blit(transform, 125, 0, this.status == 0 ? 202 : 170, 0, 32, 44);
    }

    @Override
    protected void renderBg(PoseStack transform, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, BG_LOCATION);
        this.blit(transform, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
    }
}
