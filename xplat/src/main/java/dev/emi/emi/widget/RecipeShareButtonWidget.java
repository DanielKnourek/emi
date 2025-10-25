package dev.emi.emi.widget;

import dev.emi.emi.EmiPort;
import dev.emi.emi.api.recipe.EmiRecipe;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.recipe.Recipe;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;

public class RecipeShareButtonWidget extends RecipeButtonWidget{

    public RecipeShareButtonWidget(int x, int y, EmiRecipe recipe) {
        super(x, y, 84, 0, recipe);
    }

    @Override
    public List<TooltipComponent> getTooltip(int mouseX, int mouseY) {
        return List.of(TooltipComponent.of(EmiPort.ordered(EmiPort.translatable("tooltip.emi.recipe_share"))));
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int button) {
        this.playButtonSound();

        Identifier id = recipe.getId();


        MinecraftClient client = MinecraftClient.getInstance();
        client.player.sendMessage(Text.literal("Sharing message"));
        client.player.sendMessage(Text.literal(id.toString()));

     return true;
    }
}
