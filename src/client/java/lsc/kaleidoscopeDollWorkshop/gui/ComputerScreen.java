package lsc.kaleidoscopeDollWorkshop.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import lsc.kaleidoscopeDollWorkshop.network.ModMessages;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public class ComputerScreen extends HandledScreen<ComputerScreenHandler> {
    private static final Identifier TEXTURE = new Identifier("kaleidoscope_doll_workshop", "textures/gui/computer.png");
    private static final Identifier OUTPUT_SLOT_TEXTURE = new Identifier("kaleidoscope_doll_workshop", "textures/slot/computer_output_slot.png");

    private TextFieldWidget nameField;
    private ButtonWidget craftButton;

    public ComputerScreen(ComputerScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.backgroundWidth = 176;
        this.backgroundHeight = 128;
        this.titleX = 8;
        this.titleY = 6;
    }

    @Override
    protected void init() {
        super.init();

        this.nameField = new TextFieldWidget(this.textRenderer, this.x + 10, this.y + 22, 68, 12, Text.literal("Player Name"));
        this.nameField.setMaxLength(16);
        this.nameField.setDrawsBackground(false);
        this.addDrawableChild(nameField);

        // 按钮点击事件：检测是否按下了 Shift 键，并将其状态传递给发送方法
        this.craftButton = ButtonWidget.builder(Text.literal("✔"), button -> {
            sendCraftPacket(Screen.hasShiftDown());
        }).dimensions(this.x + 80, this.y + 18, 16, 16).build();
        this.addDrawableChild(craftButton);
    }

    @Override
    protected void handledScreenTick() {
        super.handledScreenTick();
        this.nameField.tick();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        boolean handled = super.mouseClicked(mouseX, mouseY, button);
        if (!this.nameField.isMouseOver(mouseX, mouseY)) {
            this.nameField.setFocused(false);
        }
        if (!this.craftButton.isMouseOver(mouseX, mouseY) && !this.nameField.isMouseOver(mouseX, mouseY)) {
            this.setFocused(null);
        }
        return handled;
    }

    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        context.drawText(this.textRenderer, this.title, this.titleX, this.titleY, 4210752, false);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.nameField.isFocused()) {
            if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
                return super.keyPressed(keyCode, scanCode, modifiers);
            }
            if (this.client.options.inventoryKey.matchesKey(keyCode, scanCode)) {
                return this.nameField.keyPressed(keyCode, scanCode, modifiers);
            }
            if (this.nameField.keyPressed(keyCode, scanCode, modifiers)) {
                return true;
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private void sendCraftPacket(boolean craftAll) {
        String name = nameField.getText();
        if (name != null && !name.isEmpty()) {
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeString(name);
            buf.writeBoolean(craftAll); // 写入批量制作标志位
            ClientPlayNetworking.send(ModMessages.CRAFT_DOLL_ID, buf);
        }
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;
        context.drawTexture(TEXTURE, x, y, 0, 0, backgroundWidth, backgroundHeight);

        // 如果输出槽为空，绘制背景图标提示
        if (!this.handler.getSlot(1).hasStack()) {
            context.drawTexture(OUTPUT_SLOT_TEXTURE, x + 151, y + 18, 0, 0, 16, 16, 16, 16);
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context);

        // 高亮焦点
        if (this.nameField.isFocused()) {
            this.nameField.setEditableColor(0x39ff56);
        } else {
            this.nameField.setEditableColor(0xFFFFFF);
        }

        super.render(context, mouseX, mouseY, delta);

        // 占位符提示
        if (!this.nameField.isFocused() && this.nameField.getText().isEmpty()) {
            context.drawText(this.textRenderer, Text.translatable("gui.kaleidoscope_doll_workshop.computer.search"),
                    this.nameField.getX(), this.nameField.getY(), 0xAAAAAA, false);
        }

        drawInputSlotTooltip(context, mouseX, mouseY);
        drawMouseoverTooltip(context, mouseX, mouseY);
    }

    private void drawInputSlotTooltip(DrawContext context, int mouseX, int mouseY) {
        int inputSlotX = this.x + 108;
        int inputSlotY = this.y + 18;
        if (mouseX >= inputSlotX && mouseX < inputSlotX + 16 && mouseY >= inputSlotY && mouseY < inputSlotY + 16) {
            if (!this.handler.getSlot(0).hasStack()) {
                context.drawTooltip(this.textRenderer, Text.translatable("tooltip.kaleidoscope_doll_workshop.computer.input"), mouseX, mouseY);
            }
        }
    }
}