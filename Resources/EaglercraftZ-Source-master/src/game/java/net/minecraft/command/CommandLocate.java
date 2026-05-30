package net.minecraft.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureStart;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import net.minecraft.world.gen.structure.MapGenStructure;

import java.util.List;

public class CommandLocate extends CommandBase {

    @Override
    public String getCommandName() {
        return "locate";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/locate <structure> - Finds the nearest structure.";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length < 1) {
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Usage: /locate <structure>"));
            return;
        }

        String structureName = args[0].toLowerCase();
        World world = sender.getEntityWorld();
        //int x = (int) sender.getPlayerCoordinates().posX;
        //int z = (int) sender.getPlayerCoordinates().posZ;

        switch (structureName) {
            case "village":
                //locateStructure(sender, world, x, z, "Village");
                break;
            case "stronghold":
                //locateStructure(sender, world, x, z, "Stronghold");
                break;
            case "mineshaft":
                //locateStructure(sender, world, x, z, "Mineshaft");
                break;
            case "temple":
                //locateStructure(sender, world, x, z, "Temple");
                break;
            default:
                sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Unknown structure: " + structureName));
                break;
        }
    }

    private void locateStructure(ICommandSender sender, World world, int x, int z, String structureName) {
        //MapGenStructure structure = getStructureGenerator(world, structureName);
        //if (structure == null) {
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Structure not found: " + structureName));
            return;
        }

        //StructureStart start = structure.getClosestStrongholdPos(world, x, z);
        //if (start == null || start.getBoundingBox() == null) {
            //sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Could not locate " + structureName));
            //return;
        }

        //StructureBoundingBox box = start.getBoundingBox();
        //int centerX = (box.minX + box.maxX) / 2;
        //int centerZ = (box.minZ + box.maxZ) / 2;

        //sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "Located " + structureName + " at (" + centerX + ", " + centerZ + ")"));
    //}

    //private MapGenStructure getStructureGenerator(World world, String structureName) {
        //switch (structureName) {
            //case "Village":
                //return world.getMapGenVillage();
            //case "Stronghold":
                //return world.getMapGenStronghold();
            //case "Mineshaft":
                //return world.getMapGenMineshaft();
            //case "Temple":
                //return world.getMapGenScatteredFeature();
            //default:
                //return null;
        //}
    //}

    //@Override
    //public int getRequiredPermissionLevel() {
        //return 2; // Requires OP level 2
    //}
//}