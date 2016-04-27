package com.cricketcraft.ftbisland.util;

import com.cricketcraft.ftbisland.FTBIslands;
import com.google.gson.*;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.io.EOFException;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;

import cpw.mods.fml.common.registry.GameRegistry;

public class IslandCreator {
    public static HashMap<String, IslandPos> islandLocations = new HashMap<String, IslandPos>();
    public final String playerName;
    public final IslandPos pos;

    public IslandCreator() {
        playerName = null;
        pos = null;
    }

    public IslandCreator(String playerName, IslandPos pos) {
        this.playerName = playerName;
        this.pos = pos;
    }

    public static boolean createIsland(World world, String playerName, EntityPlayer player) {
        reloadIslands();
        IslandPos pos = FTBIslands.islandLoc.get(islandLocations.size() + 1);
        spawnIslandAt(world, pos.getX(), pos.getY(), pos.getZ(), playerName, (player != null ? player : null));
        return true;
    }

    private static final Item chickenStick = GameRegistry.findItem("excompressum", "chickenStick");
    public static boolean spawnIslandAt(World world, int x, int y, int z, String playerName, EntityPlayer player) {
        reloadIslands();
        if(!islandLocations.containsKey(playerName)) {
            for (int c = 0; c < 3; c++) {
                for (int d = 0; d < 3; d++) {
                    world.setBlock(x + c, y, z + d, Blocks.dirt);
                }
            }

            world.setBlock(x + 2, y + 1, z + 1, Blocks.chest);
            world.getBlock(x + 2, y + 1, z + 1).rotateBlock(world, x + 2, y + 1, z + 1, ForgeDirection.WEST);
            TileEntityChest chest = (TileEntityChest) world.getTileEntity(x + 2, y + 1, z + 1);
            chest.setInventorySlotContents(0, new ItemStack(Blocks.flowing_water, 1));
            chest.setInventorySlotContents(1, new ItemStack(Blocks.flowing_lava, 1));
            chest.setInventorySlotContents(2, new ItemStack(Items.dye, 64, 15));
            chest.setInventorySlotContents(3, new ItemStack(Items.dye, 64, 15));
            chest.setInventorySlotContents(4, new ItemStack(Items.apple, 16));
            chest.setInventorySlotContents(5, new ItemStack(Blocks.sapling, 8, 0));
            chest.setInventorySlotContents(6, new ItemStack(Items.spawn_egg, 2, 90));
            chest.setInventorySlotContents(7, new ItemStack(Items.spawn_egg, 2, 91));
            chest.setInventorySlotContents(8, new ItemStack(Items.spawn_egg, 2, 92));
            chest.setInventorySlotContents(9, new ItemStack(Items.spawn_egg, 2, 93));
            
            if (chickenStick != null)
                chest.setInventorySlotContents(10, new ItemStack(chickenStick, 1));

            if (islandLocations.size() != 0) {
                islandLocations.put(playerName, FTBIslands.islandLoc.get(islandLocations.size() + 1));
            } else {
                islandLocations.put(playerName, FTBIslands.islandLoc.get(1));
            }

            islandLocations.put(playerName, new IslandPos(x, y, z));
            try {
                FTBIslands.saveIslands(islandLocations);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (player != null)
                player.addChatMessage(new ChatComponentText(String.format("Created island named %s at %d, %d, %d", playerName, x, y, z)));
            else
                FTBIslands.logger.info(String.format("Created island named %s at %d %d %d", playerName, x, y, z));
            return true;
        } else {
            return false;
        }
    }

    protected static void reloadIslands() {
        try {
            islandLocations = FTBIslands.getIslands();
        } catch (EOFException e) {
            // silent catch
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    protected static void save() {
        try {
            FTBIslands.saveIslands(islandLocations);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static class IslandPos {
        private int x;
        private byte y;
        private int z;

        public IslandPos(int x, int y, int z) {
            this.x = x;
            this.y = (byte)(y-128);
            this.z = z;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y+128;
        }

        public int getZ() {
            return z;
        }

        public int hashCode() {
            return ((y * 31 + x) * 31 + z) * 17 + y;
        }
        
    }
}
