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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author nigjo
 */
public class ImageBlueprintPlugin extends JavaPlugin {

  private static ImageBlueprintPlugin instance;

  private WorldEditManager worldEditManager;
  private Logger ibpLogger;

  public ImageBlueprintPlugin() {
    initInstance();
  }

  private void initInstance() {
    if (instance != null) {
      throw new IllegalStateException("already initialized");
    }
    instance = this;

//    if (LogManager.getLogManager().getLogger(ImageBlueprintPlugin.class.getName()) == null) {
//      LogManager.getLogManager().addLogger(getLogger());
//    }

    ibpLogger = Logger.getLogger(ImageBlueprintPlugin.class.getPackage().getName());
    new ArrayList<>(Arrays.asList(ibpLogger.getHandlers())).stream().
        filter((h) -> h.getClass().getName().contains("IBPDebugHandler")).
        forEach((h) -> ibpLogger.removeHandler(h));

    ibpLogger.setLevel(Level.ALL);
    ConsoleHandler handler = new IBPDebugHandler();
    handler.setLevel(Level.ALL);
    handler.setFormatter(new Formatter() {
      @Override
      public String format(LogRecord record) {
        return "[IBP-" + record.getLevel().getName() + "] " +
            record.getSourceClassName()
                .substring(record.getSourceClassName().lastIndexOf('.') + 1) +
            ": " + formatMessage(record);
      }
    });
    ibpLogger.addHandler(handler);
  }

  public static ImageBlueprintPlugin getInstance() {
    return instance;
  }

  @Override
  public void onLoad() {
    super.onLoad(); //To change body of generated methods, choose Tools | Templates.
    Logger.getLogger(ImageBlueprintPlugin.class.getName()).log(Level.INFO, "loading");
    Logger.getLogger(ImageBlueprintPlugin.class.getName()).log(Level.FINEST,
        "loggername: {0}", getLogger().getName());
  }

  @Override
  public void onEnable() {
    super.onEnable(); //To change body of generated methods, choose Tools | Templates.
    worldEditManager = new WorldEditManager();

    worldEditManager.load();

    getCommand("imageblueprint").setExecutor(new CommandHandler());

    Logger.getLogger(ImageBlueprintPlugin.class.getName()).log(Level.FINEST,
        "debug mode enabled");
  }

  private class IBPDebugHandler extends ConsoleHandler {

    public IBPDebugHandler() {
    }

    @Override
    public boolean isLoggable(LogRecord record) {
      if (record.getLevel().intValue() >= Level.INFO.intValue()) {
        return false;
      }
      return super.isLoggable(record); //To change body of generated methods, choose Tools | Templates.
    }
  }

}
