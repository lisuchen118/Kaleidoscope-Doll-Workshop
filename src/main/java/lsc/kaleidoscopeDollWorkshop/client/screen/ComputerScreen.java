package lsc.kaleidoscopeDollWorkshop.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import lsc.kaleidoscopeDollWorkshop.menu.ComputerMenu;
import lsc.kaleidoscopeDollWorkshop.network.ModMessages;
import lsc.kaleidoscopeDollWorkshop.network.packet.PacketCraftDoll;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.lwjgl.glfw.GLFW;

public class ComputerScreen extends AbstractContainerScreen<ComputerMenu> {
    @SuppressWarnings("removal")
    private static final ResourceLocation TEXTURE = new ResourceLocation("kaleidoscope_doll_workshop", "textures/gui/computer.png");
    @SuppressWarnings("removal")
    private static final ResourceLocation OUTPUT_SLOT_TEXTURE = new ResourceLocation("kaleidoscope_doll_workshop", "textures/slot/computer_output_slot.png");

    private EditBox nameField;
    private Button craftButton;

    public ComputerScreen(ComputerMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.imageWidth = 176;
        this.imageHeight = 128;
        this.titleLabelX = 8;
        this.titleLabelY = 6;
    }

    @Override
    protected void init() {
        super.init();
        this.nameField = new EditBox(this.font, this.leftPos + 10, this.topPos + 22, 68, 12, Component.literal("Name"));
        this.nameField.setMaxLength(16);
        this.nameField.setBordered(false);
        this.addRenderableWidget(nameField);

        this.craftButton = Button.builder(Component.literal("✔"), b -> sendPacket())
                .bounds(this.leftPos + 80, this.topPos + 18, 16, 16)
                .build();
        this.addRenderableWidget(craftButton);
    }

    // 点击空白处时，如果焦点在输入框或按钮上则取消焦点
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.nameField.isFocused() && !this.nameField.isMouseOver(mouseX, mouseY)) {
            this.nameField.setFocused(false);
        }

        if (this.craftButton.isFocused() && !this.craftButton.isMouseOver(mouseX, mouseY)) {
            this.craftButton.setFocused(false);
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        // 仅绘制标题，隐藏背包标签
        guiGraphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 4210752, false);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics);

        if (this.nameField.isFocused()) {
            this.nameField.setTextColor(0x39ff56);
        } else {
            this.nameField.setTextColor(0xFFFFFF);
        }

        super.render(guiGraphics, mouseX, mouseY, partialTick);

        // 如果输入框未聚焦且为空，显示搜索提示文本
        if (!this.nameField.isFocused() && this.nameField.getValue().isEmpty()) {
            guiGraphics.drawString(this.font, Component.translatable("gui.kaleidoscope_doll_workshop.computer.search"),
                    this.nameField.getX(), this.nameField.getY(), 0xAAAAAA, false);
        }

        // 原料槽空置时的提示信息
        if (isHovering(108, 18, 16, 16, mouseX, mouseY)) {
            if (!this.menu.getSlot(0).hasItem()) {
                guiGraphics.renderTooltip(this.font, Component.translatable("tooltip.kaleidoscope_doll_workshop.computer.input"), mouseX, mouseY);
            }
        }

        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        guiGraphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);

        // 如果输出槽为空，绘制输出槽的背景图标
        if (!this.menu.getSlot(1).hasItem()) {
            guiGraphics.blit(OUTPUT_SLOT_TEXTURE, x + 151, y + 18, 0, 0, 16, 16, 16, 16);
        }
    }

    // 发送合成请求数据包
    private void sendPacket() {
        String name = nameField.getValue();
        if (name != null && !name.isEmpty()) {
            // 构造数据包时传入当前的Shift键状态，以决定是否进行批量合成
            ModMessages.sendToServer(new PacketCraftDoll(name, hasShiftDown()));
        }
    }

    @Override
    public void containerTick() {
        super.containerTick();
        this.nameField.tick();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.nameField.isFocused()) {
            if (keyCode == GLFW.GLFW_KEY_ESCAPE) return super.keyPressed(keyCode, scanCode, modifiers);
            if (this.minecraft.options.keyInventory.matches(keyCode, scanCode)) return false;
            if (this.nameField.keyPressed(keyCode, scanCode, modifiers)) return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}