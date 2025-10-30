package dev.emi.emi.runtime;

import dev.emi.emi.api.EmiApi;
import dev.emi.emi.api.recipe.EmiRecipe;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;


public class EmiShareRecipe {

    private static Identifier lastSharedRecipe = null;

    public static void shareRecipe(PlayerEntity player, Identifier id, String senderDisplayName) {
        // Command is received 2x on a client, allow only first occurrence of this command
        // also works as primitive spam protection
        if (lastSharedRecipe != null && lastSharedRecipe.equals(id)) {
            return;
        }
        lastSharedRecipe = id;

        EmiRecipe recipe = EmiApi.getRecipeManager().getRecipe(id);
        if (recipe == null) {
            EmiLog.info("Could not create sharing message");
            return;
        }

        String itemDisplayName = "View Recipe"; // fallback display text
        if (!recipe.getOutputs().isEmpty()){
            itemDisplayName = recipe.getOutputs().get(0).getItemStack().getItem().getName().getString();
        }
        MutableText clickableId = Text.literal(String.format("[%s]", itemDisplayName));

        Style style = Style.EMPTY
                .withColor(Formatting.UNDERLINE)
                .withColor(Formatting.AQUA)
                .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/emi view recipe " + id))
                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.translatable("tooltip.emi.recipe_share_widget")));
        clickableId.setStyle(style);

        Text message = Text.translatable("chat.emi.recipe_share", senderDisplayName, clickableId);

        player.sendMessage(message, false);
    }
}
