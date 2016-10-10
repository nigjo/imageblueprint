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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author nigjo
 */
public class ImageLoader {

  private ImageLoader() {
  }

  public static BluePrint loadImage(String filekey)
      throws FileNotFoundException, IOException {

    String filename = "blueprints/" + filekey + ".png";
    File canonical = getLoadFilename(filename);
    BufferedImage image = ImageIO.read(canonical);

    return new BluePrint(image);
  }

  private static File getLoadFilename(String filename) throws FileNotFoundException,
      IOException, IllegalArgumentException {
    File base = ImageBlueprintPlugin.getInstance().getDataFolder();
    File intermediate = new File(base, filename).getAbsoluteFile();
    String absolutePath = intermediate.getAbsolutePath().replace('\\', '/');
    if (absolutePath.contains("/../")) {
      throw new IllegalArgumentException("invalid path " + filename);
    }
    Logger.getLogger(ImageLoader.class.getName()).log(Level.FINE,
        "try to load file {0}", intermediate);
    if (!intermediate.exists()) {
      throw new FileNotFoundException(filename);
    }
    File canonical = intermediate.getCanonicalFile();
    Logger.getLogger(ImageLoader.class.getName()).log(Level.FINE,
        "resolved path to {0}", canonical);
    return canonical;
  }
}
