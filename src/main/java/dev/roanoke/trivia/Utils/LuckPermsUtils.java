package dev.roanoke.trivia.Utils;

import dev.roanoke.trivia.Trivia;
import net.fabricmc.loader.api.FabricLoader;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

public class LuckPermsUtils
{
    private static final String MOD_ID_STRING = "luckperms";

    public static boolean isModLoaded()
    {
        return FabricLoader.getInstance().isModLoaded(MOD_ID_STRING);
    }

    public static boolean hasPermission(ServerCommandSource source, String permission) 
    {
        if (source.hasPermissionLevel(4)) return true;
        if (!isModLoaded()) return source.hasPermissionLevel(4);

        ServerPlayerEntity player;
        try
        {
            player = source.getPlayerOrThrow();
        }
        catch (Exception e)
        {
            Trivia.LOGGER.error("Failed to check player permissions: %s".formatted(e.getMessage()));
            return false;
        }

        LuckPerms api = LuckPermsProvider.get();

        User user = api.getUserManager().getUser(player.getUuid());
        if (user == null) return false;

        return user.getCachedData().getPermissionData().checkPermission(permission).asBoolean();
    }
}