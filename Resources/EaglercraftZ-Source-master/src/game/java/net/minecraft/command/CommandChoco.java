package net.minecraft.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item;
import net.minecraft.init.Items;
import net.minecraft.util.ChatComponentText;

public class CommandChoco extends CommandBase {
    
    @Override
    public String getCommandName() {
        return "choco";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/choco - Gives you a choco";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (sender instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer)sender;
            ItemStack chocoMarshy = new ItemStack(Items.marshy_choco, 1, 0); // marshy choco
            player.inventory.addItemStackToInventory(chocoMarshy);
            player.addChatMessage(new ChatComponentText("§6Thanks for using the command heres your choco as I promised! §r-§l§b§lMarshy Choco"));
        } else {
            sender.addChatMessage(new ChatComponentText("§cOnly players can use this command!"));
        }
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0; // 0 means any player can use it
    }
}