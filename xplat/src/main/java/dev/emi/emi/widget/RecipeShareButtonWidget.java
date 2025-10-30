package dev.emi.emi.widget;

import dev.emi.emi.EmiPort;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.runtime.EmiLog;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;

public class RecipeShareButtonWidget extends RecipeButtonWidget{

    public boolean visible = true;

    public RecipeShareButtonWidget(int x, int y, EmiRecipe recipe) {
        super(x, y, 84, 0, recipe);

        if(recipe == null || recipe.getId() == null) {
            EmiLog.error("Unable to create recipe for [" + recipe.toString() + "]. Recipe handler not supported");
            this.visible = false;
        }

    }

    @Override
    public void render(DrawContext raw, int mouseX, int mouseY, float delta) {
        if(visible){
        super.render(raw, mouseX, mouseY, delta);
        }
    }

    @Override
    public List<TooltipComponent> getTooltip(int mouseX, int mouseY) {
        return List.of(TooltipComponent.of(EmiPort.ordered(EmiPort.translatable("tooltip.emi.recipe_share"))));
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int button) {
        this.playButtonSound();

        Identifier id = recipe.getId();
        if(id == null){
            EmiLog.error("Unable to create recipe for [" + recipe.toString() + "]. Recipe handler not supported");
            return false;
        }

        MinecraftClient client = MinecraftClient.getInstance();

        MutableText shareCommand = Text.literal("emi share recipe ");
        shareCommand.append(id.toString());

        client.player.networkHandler.sendChatCommand(shareCommand.getString());
        return true;
    }
}
