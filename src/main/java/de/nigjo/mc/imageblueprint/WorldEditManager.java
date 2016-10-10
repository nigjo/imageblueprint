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

import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.UnknownDependencyException;

/**
 *
 * @author nigjo
 */
public class WorldEditManager {

  private static final String WORLD_EDIT_PLUGIN_NAME = "WorldEdit"; //NON-NLS
  private static final String REGIONS_SCHEMATIC_FORMAT_STRING = "%s.schematic"; //NON-NLS
  private WorldEdit worldedit;
  private WorldEditPlugin worldEditPlugin;

  public void load() {
    Plugin plugin = Bukkit.getServer().getPluginManager().
        getPlugin(WORLD_EDIT_PLUGIN_NAME);

    if (plugin == null || !(plugin instanceof WorldEditPlugin)) {
      throw new UnknownDependencyException(Utilities.getString(
          "WorldEditManager.not_found"));
    }
    else {
      worldEditPlugin = (WorldEditPlugin) plugin;
      worldedit = WorldEdit.getInstance();
    }
  }

  public void createSchematic(){

  }
}
