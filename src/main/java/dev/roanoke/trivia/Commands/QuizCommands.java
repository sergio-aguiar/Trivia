package dev.roanoke.trivia.Commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import dev.roanoke.trivia.Config;
import dev.roanoke.trivia.Quiz.QuizManager;
import dev.roanoke.trivia.Trivia;
import dev.roanoke.trivia.Utils.LuckPermsUtils;
import dev.roanoke.trivia.Utils.Messages;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class QuizCommands {
    public QuizCommands() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(
                literal("trivia")
                    .then(
                        literal("interval")
                            .requires(source -> LuckPermsUtils.hasPermission(source, "trivia.interval"))
                            .then(argument("intervalSeconds", IntegerArgumentType.integer(1, 999999)).executes(this::executeQuizInterval))
                    )
                    .then(
                        literal("start").requires(source -> LuckPermsUtils.hasPermission(source, "trivia.start"))
                            .executes(this::executeStartQuiz)
                    )
                    .then(
                        literal("reload").requires(source -> LuckPermsUtils.hasPermission(source, "trivia.reload"))
                            .executes(this::executeReloadQuiz)
                    )
                    .then(
                        literal("timeout").requires(source -> LuckPermsUtils.hasPermission(source, "trivia.timeout"))
                            .then(argument("timeoutSeconds", IntegerArgumentType.integer(1, 999999)).executes(this::executeQuizTimeout))
                    )
                    .then(
                        literal("answerbuffer").requires(source -> LuckPermsUtils.hasPermission(source, "trivia.answerbuffer"))
                            .then(argument("answerBuffer", IntegerArgumentType.integer(1, 999999)).executes(this::executeQuizAnswerBuffer))
                    )
                    .then(
                        literal("intervalbuffer").requires(source -> LuckPermsUtils.hasPermission(source, "trivia.intervalbuffer"))
                            .then(argument("intervalbuffer", IntegerArgumentType.integer(1, 999999)).executes(this::executeQuizIntervalBuffer))
                    )
        );
        });
    }

    private int executeQuizTimeout(CommandContext<ServerCommandSource> ctx) {
        Trivia.getInstance().config.setQuizTimeOut(ctx.getArgument("timeoutSeconds", Integer.class));
        ctx.getSource().sendMessage(Text.literal("Updated Quiz Timeout to " + ctx.getArgument("timeoutSeconds", Integer.class) + " seconds."));
        return 1;
    }

    private int executeQuizInterval(CommandContext<ServerCommandSource> ctx) {
        Trivia.getInstance().config.setQuizInterval(ctx.getArgument("intervalSeconds", Integer.class));
        ctx.getSource().sendMessage(Text.literal("Updated Quiz Interval to " + ctx.getArgument("intervalSeconds", Integer.class) + " seconds."));
        return 1;
    }

    private int executeQuizAnswerBuffer(CommandContext<ServerCommandSource> ctx) {
        Trivia.getInstance().config.setQuizAnswerBuffer(ctx.getArgument("answerBuffer", Integer.class));
        ctx.getSource().sendMessage(Text.literal("Updated Quiz answer buffer to " + ctx.getArgument("answerBuffer", Integer.class) + " seconds."));
        return 1;
    }

    private int executeQuizIntervalBuffer(CommandContext<ServerCommandSource> ctx) {
        Trivia.getInstance().config.setQuizAnswerBuffer(ctx.getArgument("intervalbuffer", Integer.class));
        ctx.getSource().sendMessage(Text.literal("Updated Quiz interval buffer to " + ctx.getArgument("intervalbuffer", Integer.class) + " seconds."));
        return 1;
    }

    private int executeStartQuiz(CommandContext<ServerCommandSource> ctx) {
        if (!Trivia.getInstance().quiz.quizInProgress()) {
            Trivia.getInstance().quizIntervalCounter = Trivia.getInstance().config.getQuizInterval() + 1;
        } else {
            Trivia.getInstance().quizIntervalCounter = 0;
            Trivia.getInstance().quizTimeOutCounter = 0;
            Trivia.getInstance().quiz.startQuiz(ctx.getSource().getServer());
        }
        return 1;
    }

    private int executeReloadQuiz(CommandContext<ServerCommandSource> ctx) {
        if (Trivia.getInstance().quiz.quizInProgress()) {
            Trivia.getInstance().quizIntervalCounter = 0;
        }
        Trivia.getInstance().quiz = new QuizManager();
        Trivia.getInstance().config = new Config();
        Trivia.messages = new Messages(FabricLoader.getInstance().getConfigDir().resolve("Trivia/messages.json"));
        return 1;
    }

}
