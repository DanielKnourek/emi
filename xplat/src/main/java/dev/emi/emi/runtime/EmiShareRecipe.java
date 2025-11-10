package dev.emi.emi.runtime;

import com.google.common.collect.Lists;
import dev.emi.emi.api.EmiApi;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.config.SidebarType;
import dev.emi.emi.screen.EmiScreenManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Objects;

public class EmiShareRecipe {

    public static List<EmiFavorite> shareHistory = Lists.newArrayList();

    public static void shareRecipe(PlayerEntity player, Identifier id, String senderDisplayName) {

        EmiRecipe recipe = EmiApi.getRecipeManager().getRecipe(id);
        if (recipe == null) {
            EmiLog.error("Could not create sharing message. Could not find recipe.");
            return;
        }

        if(recipe.getOutputs().isEmpty() || recipe.getOutputs().get(0).isEmpty()){
            EmiLog.error("Could not create sharing message. Invalid Recipe.");
            return;
        }
        EmiFavorite sharedRecipe = new EmiFavorite(recipe.getOutputs().get(0).getEmiStacks().get(0), recipe);

        if(!shareHistory.isEmpty() && recipe.getId().equals(shareHistory.get(0).getRecipe().getId())){
            // Command is received 2x on a client, allow only first occurrence of this command
            // also works as primitive spam protection
            //TODO: find out why it is triggered 2 times
            return;
        }
        shareHistory.removeIf(e -> Objects.equals(e.getRecipe().getId(), sharedRecipe.getRecipe().getId()));
        shareHistory.add(0,sharedRecipe);
        EmiScreenManager.repopulatePanels(SidebarType.SHARE_HISTORY);

        String itemDisplayName = "View Recipe"; // fallback display text
        if (!recipe.getOutputs().isEmpty()){
            itemDisplayName = recipe.getOutputs().get(0).getItemStack().getItem().getName().getString();
        }
        MutableText clickableId = Text.literal(String.format("[%s]", itemDisplayName));

        Style style = Style.EMPTY
                .withColor(Formatting.UNDERLINE)
                .withColor(Formatting.AQUA)
                .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/emi view recipe " + id))
                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.translatable("tooltip.emi.recipe_share_chat")));
        clickableId.setStyle(style);

        Text message = Text.translatable("chat.emi.recipe_share", senderDisplayName, clickableId);

        player.sendMessage(message, false);
    }
}
