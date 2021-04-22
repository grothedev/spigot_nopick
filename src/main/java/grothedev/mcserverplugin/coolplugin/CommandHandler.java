package grothedev.mcserverplugin.coolplugin;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.GameMode;

public class CommandHandler implements CommandExecutor{
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
        if (sender instanceof Player){
            Player p = (Player)sender;
            if (p.getGameMode() == GameMode.CREATIVE){
                p.setGameMode(GameMode.SURVIVAL);
            } else{
                p.setGameMode(GameMode.CREATIVE);
            }
        }
        return true;
    }
}