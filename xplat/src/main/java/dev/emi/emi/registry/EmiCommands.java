package dev.emi.emi.registry;

import static net.minecraft.command.argument.IdentifierArgumentType.identifier;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

import dev.emi.emi.api.EmiApi;
import dev.emi.emi.api.recipe.EmiRecipe;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;

import dev.emi.emi.network.CommandS2CPacket;
import dev.emi.emi.network.EmiNetwork;
import net.minecraft.util.Formatting;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class EmiCommands {
	public static final byte VIEW_RECIPE = 0x01;
    public static final byte VIEW_TREE = 0x02;
	public static final byte TREE_GOAL = 0x11;
	public static final byte TREE_RESOLUTION = 0x12;

	public static void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(literal("emi")
			.requires(source -> source.hasPermissionLevel(0))
			.then(
				literal("view")
				.then(
					literal("recipe")
					.then(
						argument("id", identifier())
						.executes(context -> {
							send(context.getSource().getPlayer(), VIEW_RECIPE, context.getArgument("id", Identifier.class));
							return Command.SINGLE_SUCCESS;
						})
					)
				)
				.then(
					literal("tree")
					.executes(context -> {
						send(context.getSource().getPlayer(), VIEW_TREE, null);
						return Command.SINGLE_SUCCESS;
					})
				)
			)
			.then(
				literal("tree")
                .requires(source -> source.hasPermissionLevel(2))
				.then(
					literal("goal")
					.then(
						argument("id", identifier())
						.executes(context -> {
							send(context.getSource().getPlayer(), TREE_GOAL, context.getArgument("id", Identifier.class));
							return Command.SINGLE_SUCCESS;
						})
					)
				)
				.then(
					literal("resolution")
					.then(
						argument("id", identifier())
						.executes(context -> {
							send(context.getSource().getPlayer(), TREE_RESOLUTION, context.getArgument("id", Identifier.class));
							return Command.SINGLE_SUCCESS;
						})
					)
				)
			)
            .then(
                literal("share")
                .then(
                    literal("recipe")
                    .then(
                        argument("id", identifier())
                        .executes(context -> {
                            ServerPlayerEntity player = context.getSource().getPlayer();

                            Identifier id = context.getArgument("id", Identifier.class);

//                            EmiRecipe recipe = EmiRecipes.manager.getRecipe(id);
//                            if (recipe == null) {
////                                throw new Error("Recipe not found");
//                                return Command.SINGLE_SUCCESS;
//                            }

                            //TODO: change to Text.translatable
                            String itemDisplayName = "[View Recipe]"; //fallback text
                            if (id.toString() != null){
                                itemDisplayName = String.format("[%s]",
                                        id.toString().substring(id.toString().lastIndexOf('/') + 1)
                                );
                            }
//                            if(!recipe.getOutputs().isEmpty() && recipe.getOutputs().get(0) != null){
//                                itemDisplayName = String.format("[%s]",
//                                        recipe.getOutputs().get(0).getItemStack().getItem().getName().getString()
//                                );
//                            }

                            //TODO: resolve
//                          [14:57:58] [Thread-27/ERROR] [EMI/]: [EMI] Recipe emi:brewing/item/minecraft/turtle_master/minecraft/gunpowder/minecraft/potion/minecraft/splash_potion not present in recipe manager. Consider prefixing its path with '/' if it is synthetic.
                            MutableText clickableId = Text.literal(itemDisplayName);
                            Style style = Style.EMPTY
                                .withColor(Formatting.UNDERLINE)
                                .withColor(Formatting.AQUA)
                                .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/emi view recipe " + id))
                                //TODO: change to Text.translatable
                                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal("Click to view recipe")));
                            clickableId.setStyle(style);

                            Text message = Text.translatable("chat.emi.recipe_share", player.getDisplayName(), clickableId);

                            context.getSource().getServer().getPlayerManager().broadcast(message, false);
                            return Command.SINGLE_SUCCESS;
                        })
                    )
                )
            )
		);
	}

	private static void send(ServerPlayerEntity player, byte type, @Nullable Identifier id) {
		EmiNetwork.sendToClient(player, new CommandS2CPacket(type, id));
	}
}
