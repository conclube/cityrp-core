package me.conclure.cityrp.common.command.abstraction;

import com.google.common.base.Preconditions;
import com.google.common.reflect.TypeToken;
import me.conclure.cityrp.common.command.Command;
import me.conclure.cityrp.common.command.CommandException;
import me.conclure.cityrp.common.command.CommandInfo;
import me.conclure.cityrp.common.sender.Sender;
import me.conclure.cityrp.common.utility.function.ThrowingConsumer;
import net.kyori.adventure.text.Component;

public abstract class AbstractCommand<S extends Sender<SS>,SS> implements Command<S,SS> {
    private final CommandInfo<S,SS> commandInfo;

    protected AbstractCommand(CommandInfo<S,SS> commandInfo) {
        Preconditions.checkNotNull(commandInfo);

        this.commandInfo = commandInfo;
    }

    static <S extends Sender<SS>,SS> void run0(
            AbstractCommand<S,SS> command,
            Sender<? extends SS> sender,
            String[] arguments,
            ThrowingConsumer<S, Exception> executor
    ) {
        Preconditions.checkNotNull(command);
        Preconditions.checkNotNull(sender);
        Preconditions.checkNotNull(arguments);
        Preconditions.checkNotNull(executor);

        boolean requiresPermission = command.commandInfo.requiresPermission();
        boolean senderHasNotPermission = !command.hasPermission(sender);
        boolean hasNotPermission = requiresPermission && senderHasNotPermission;

        if (hasNotPermission) {
            sender.sendMessage(Component.text("You don't have permission!"));
            return;
        }

        boolean notAcceptsSender = !command.acceptsSender(sender);
        if (notAcceptsSender) {
            sender.sendMessage(Component.text("Your sender type is disallowed for this command!"));
            return;
        }

        try {
            executor.accept((S) sender);
        } catch (Exception e) {
            throw new CommandException(e);
        }
    }

    @Override
    public boolean acceptsSender(Sender<? extends SS> sender) {
        Preconditions.checkNotNull(sender);

        return this.commandInfo.getSenderType().isSupertypeOf(sender.getClass());
    }

    @Override
    public boolean acceptsSenderType(Class<? extends Sender<? extends SS>> type) {
        Preconditions.checkNotNull(type);

        return this.commandInfo.getSenderType().isSupertypeOf(type);
    }

    @Override
    public boolean hasPermission(Sender<? extends SS> sender) {
        Preconditions.checkNotNull(sender);

        boolean notRequiresPermission = !this.commandInfo.requiresPermission();

        if (notRequiresPermission) {
            return true;
        }

        return sender.hasPermission(this.commandInfo.getPermission());
    }


    @Override
    public CommandInfo<S,SS> getInfo() {
        return this.commandInfo;
    }

}