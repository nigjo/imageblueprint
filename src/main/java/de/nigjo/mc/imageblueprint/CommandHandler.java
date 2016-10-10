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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author nigjo
 */
public class CommandHandler implements CommandExecutor {

  @Target(ElementType.FIELD)
  @Retention(RetentionPolicy.RUNTIME)
  private static @interface HelpCommand {

    String description() default "";

    String[] alias() default "";

    String method();
  }

  @HelpCommand(description = "this help",
      method = "cmdPrintHelp", alias = "?")
  private static final String HELP = "help";

  @HelpCommand(method = "cmdLoadImage")
  private static final String LOAD = "load";
  @HelpCommand(method = "cmdAsignBlock")
  private static final String ASIGN = "asign";

  //<editor-fold defaultstate="collapsed" desc="internal implementation">
  public CommandHandler() {
  }

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label,
      String[] args) {
    if (args.length == 0) {
      sender.sendMessage(colorify("type $2/ibp help$r for help"));
    }
    else {
      performCommand(sender, args);
    }
    return true;
  }

  private void performCommand(CommandSender sender, String... args) {

    Field field =
        Arrays.asList(CommandHandler.class.getDeclaredFields()).stream()
            .filter(f -> f.getAnnotation(HelpCommand.class) != null)
            .filter(f -> accept(f, args[0]))
            .findFirst().orElse(null);
    if (field != null) {
      HelpCommand command = field.getAnnotation(HelpCommand.class);
      try {
        Method cmdMethod = getClass().getDeclaredMethod(
            command.method(), CommandSender.class, String[].class);
        Object result = cmdMethod.invoke(this, sender, args);
        if (result instanceof String[]) {
          sender.sendMessage((String[]) result);
        }
        else if (result instanceof String) {
          sender.sendMessage((String) result);
        }
        else if (result instanceof List) {
          List<String> lines = (List<String>) result;
          sender.sendMessage(lines.toArray(new String[lines.size()]));
        }
      }
      catch (NoSuchMethodException | SecurityException |
          IllegalAccessException | IllegalArgumentException ex) {
        sender.sendMessage(ChatColor.RED + ex.toString());
        Logger.getLogger(CommandHandler.class.getName()).log(
            Level.SEVERE, null, ex);
      }
      catch (InvocationTargetException ex) {
        sender.sendMessage(ChatColor.RED + ex.getCause().toString());
        Logger.getLogger(CommandHandler.class.getName()).log(
            Level.SEVERE, null, ex.getCause());
      }
    }
    else {
      sender.sendMessage(colorify("$cunknown command $e/ibp ") + args[0]);
    }
  }

  private boolean accept(Field f, String cmd) {
    try {
      String key = (String) f.get(null);
      if (cmd.equalsIgnoreCase(key)) {
        return true;
      }
    }
    catch (IllegalArgumentException | IllegalAccessException ex) {
      Logger.getLogger(CommandHandler.class.getName()).log(Level.SEVERE, null, ex);
    }
    HelpCommand hc = f.getAnnotation(HelpCommand.class);
    for (String key : hc.alias()) {
      if (cmd.equalsIgnoreCase(key)) {
        return true;
      }
    }
    return false;
  }
  //</editor-fold>

  private String colorify(String message) {
    //<editor-fold defaultstate="collapsed" desc="internal implementation">
    String result = message;
    int index = -1;
    while (0 <= (index = result.indexOf("$", index))) {
      if (index + 1 >= result.length()) {
        break;
      }
      char next = result.charAt(index + 1);
      switch (next) {
        case '$':
          result = result.substring(0, index) +
              result.substring(index + 1);
          break;
        case 'k':
        case 'l':
        case 'm':
        case 'n':
        case 'o':
        case 'r':
          result = result.substring(0, index) +
              ChatColor.getByChar(next) +
              result.substring(index + 2);
          break;
        default:
          try {
            Integer.parseInt(Character.toString(next), 16);
            result = result.substring(0, index) +
                ChatColor.getByChar(next) +
                result.substring(index + 2);
          }
          catch (NumberFormatException ex) {
          }
          break;
      }
      index++;
    }
    return result;
    //</editor-fold>
  }

  private String cmdLoadImage(CommandSender sender, String... args) {
    if (args.length != 2) {
      return colorify("$9invalid syntax. $e/ibp load <filename>");
    }
    BluePrint bluePrint;
    try {
      bluePrint = ImageLoader.loadImage(args[1]);
    }
    catch (FileNotFoundException ex) {
      return colorify("$cunable to find blueprint $9") + ex.getMessage();
    }
    catch (IOException ex) {
      Logger.getLogger(CommandHandler.class.getName()).log(Level.SEVERE, null, ex);
      return colorify("$9") + ex.toString();
    }

    Player player = sender.getServer().getPlayer(sender.getName());
    bluePrint.paste(player);
//    r.run();
    return colorify("$9loading image of size: " +
        bluePrint.getWidth() + "x" + bluePrint.getHeight());
  }

  private String cmdAsignBlock(CommandSender sender, String... args) {
    return colorify("$9block asigned");
  }

  private List<String> cmdPrintHelp(CommandSender sender, String... args) {
    List<String> result = new ArrayList<>();
    Field[] fields = CommandHandler.class.getDeclaredFields();
    for (Field field : fields) {
      HelpCommand cmd = field.getAnnotation(HelpCommand.class);
      if (cmd != null) {
        field.setAccessible(true);
        String name;
        try {
          name = (String) field.get(null);
        }
        catch (IllegalArgumentException | IllegalAccessException ex) {
          name = field.getName();
        }
        String description = cmd.description();
        result.add(colorify("$e/" + name + " $f" + description));
      }
    }
    return result;
  }

}
