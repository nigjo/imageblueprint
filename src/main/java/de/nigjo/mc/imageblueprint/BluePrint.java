/*
 * Copyright 2016 nigjo.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.nigjo.mc.imageblueprint;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

/**
 *
 * @author nigjo
 */
public class BluePrint {

  private final BufferedImage image;

  BluePrint(BufferedImage image) {
    this.image = new BufferedImage(image.getWidth(), image.getHeight(),
        BufferedImage.TYPE_INT_ARGB_PRE);
    this.image.createGraphics().drawImage(image, 0, 0, null);
    Logger.getLogger(BluePrint.class.getName()).log(Level.FINEST,
        "org : " + image.getColorModel().hasAlpha());
    Logger.getLogger(BluePrint.class.getName()).log(Level.FINEST,
        "copy: " + image.getColorModel().hasAlpha());
  }

  public int getWidth() {
    return image.getWidth();
  }

  public int getHeight() {
    return image.getHeight();
  }

  private void setBlockData(Block block, int rgb) {
    switch (rgb) {
      case 0xFFFFFFFF:
        block.setType(Material.WOOL);
        block.setData(DyeColor.WHITE.getWoolData());
        break;
      case 0xFFFF6A00:
        block.setType(Material.WOOL);
        block.setData(DyeColor.ORANGE.getWoolData());
        break;
      case 0xFFB200FF:
        block.setType(Material.WOOL);
        block.setData(DyeColor.MAGENTA.getWoolData());
        break;
      case 0xFF0094FF:
        block.setType(Material.WOOL);
        block.setData(DyeColor.LIGHT_BLUE.getWoolData());
        break;
      case 0xFFFFD800:
        block.setType(Material.WOOL);
        block.setData(DyeColor.YELLOW.getWoolData());
        break;
      case 0xFF4CFF00:
        block.setType(Material.WOOL);
        block.setData(DyeColor.LIME.getWoolData());
        break;
      case 0xFFFF006E:
        block.setType(Material.WOOL);
        block.setData(DyeColor.PINK.getWoolData());
        break;
      case 0xFF404040:
        block.setType(Material.WOOL);
        block.setData(DyeColor.GRAY.getWoolData());
        break;
      case 0xFF808080:
        block.setType(Material.WOOL);
        block.setData(DyeColor.SILVER.getWoolData());
        break;
      case 0xFF00FFFF:
        block.setType(Material.WOOL);
        block.setData(DyeColor.CYAN.getWoolData());
        break;
      case 0xFF7F3FB2:
        block.setType(Material.WOOL);
        block.setData(DyeColor.PURPLE.getWoolData());
        break;
      case 0xFF0026FF:
        block.setType(Material.WOOL);
        block.setData(DyeColor.BLUE.getWoolData());
        break;
      case 0xFF7F3300:
        block.setType(Material.WOOL);
        block.setData(DyeColor.BROWN.getWoolData());
        break;
      case 0xFF007F00:
        block.setType(Material.WOOL);
        block.setData(DyeColor.GREEN.getWoolData());
        break;
      case 0xFFFF0000:
        block.setType(Material.WOOL);
        block.setData(DyeColor.RED.getWoolData());
        break;
      case 0xFF000000:
        block.setType(Material.WOOL);
        block.setData(DyeColor.BLACK.getWoolData());
        break;
      default:
        if ((rgb & 0xFF000000) != 0xFF000000) {
          block.setType(Material.AIR);
        }
        else {
          DyeColor byColor = DyeColor.getByColor(Color.fromRGB(rgb & 0xFFFFFF));
          if (byColor != null) {
            block.setType(Material.WOOL);
            block.setData(byColor.getWoolData());
          }
          else {
            block.setType(Material.DIRT);
          }
        }
        break;
    }
  }

  private Material findMaterial(int dx, int dz) {
    int width = image.getWidth();
    if (width <= dx) {
      throw new IndexOutOfBoundsException("dx=" + dx + ",w=" + width);
    }
    int height = image.getHeight();
    if (height <= dz) {
      throw new IndexOutOfBoundsException("dz=" + dz + ",h=" + height);
    }
    int rgb = image.getRGB(dx, dz);
    return findMaterial(rgb);
  }

  private Material findMaterial(int rgb) {
    Material wool = Material.WOOL;
    switch (rgb) {
      case 0xFFFF0000:
        return wool;
      default:
        return Material.AIR;
    }
  }

  void paste(Player player) {
    Thread painter = new Thread(() -> pasteLoop(player), "imagepainter");
    painter.setDaemon(true);
    painter.start();
  }

  private void pasteLoop(Player player) {
    Location playerLocation = player.getLocation();
    World world = player.getWorld();
    final int x = playerLocation.getBlockX();
    final int y = playerLocation.getBlockY();
    final int z = playerLocation.getBlockZ();
    int maxx = getWidth();
    int maxz = getHeight();
    Logger.getLogger(CommandHandler.class.getName()).log(Level.FINEST,
        "maxx={0},maxz={1}", new Object[]{maxx, maxz});
    Logger.getLogger(BluePrint.class.getName()).log(Level.FINEST,
        "multiplied? {0}", image.isAlphaPremultiplied());
    if (!image.isAlphaPremultiplied() && image.getAlphaRaster() != null) {
      WritableRaster raster = image.getAlphaRaster();
      Logger.getLogger(BluePrint.class.getName()).log(Level.INFO,
          "extra alpha raster " + raster.getNumBands());
    }
    for (int dx = 0; dx < maxx; dx++) {
//      Logger.getLogger(CommandHandler.class.getName())
//          .log(Level.FINE, "row {0}", dx);
      final int col = dx;
      player.getServer().getScheduler().runTask(ImageBlueprintPlugin.getInstance(),
          () -> {
            for (int dz = 0; dz < maxz; dz++) {
              Block block = world.getBlockAt(x + col, y, z + dz);
              int rgb = image.getRGB(col, dz);
              setBlockData(block, rgb);
              //block.setType(findMaterial(rgb));
              for (int h = 1; h <= 10; h++) {
                world.getBlockAt(x + col, y + h, z + dz).setType(Material.AIR);
              }
            }
          });
    }
    //player.sendMessage(colorify("$9image painted"));
  }

}
