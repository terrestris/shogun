/* SHOGun, https://terrestris.github.io/shogun/
 *
 * Copyright © 2020-present terrestris GmbH & Co. KG
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   https://www.apache.org/licenses/LICENSE-2.0.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.terrestris.shogun.lib.util;

import lombok.extern.log4j.Log4j2;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

@Log4j2
public class ImageFileUtil {

    public static Dimension getImageDimensions(MultipartFile uploadFile) throws IOException {
        File imgFile = FileUtil.convertToFile(uploadFile);

        try (ImageInputStream in = ImageIO.createImageInputStream(imgFile)) {
            final Iterator<ImageReader> readers = ImageIO.getImageReaders(in);
            if (readers.hasNext()) {
                ImageReader reader = readers.next();
                try {
                    reader.setInput(in);
                    return new Dimension(reader.getWidth(0), reader.getHeight(0));
                } finally {
                    reader.dispose();
                }
            }
        }

        return null;
    }

    public static BufferedImage toBufferedImage(Image img) {
        BufferedImage bImg = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

        Graphics2D bGr = bImg.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();

        return bImg;
    }

    public static byte[] getScaledImage(MultipartFile uploadFile, Dimension imageDimensions, int thumbnailSize) throws IOException {
        File imgFile = FileUtil.convertToFile(uploadFile);

        Image scaledImgFile;
        if (imageDimensions.height > imageDimensions.width) {
            scaledImgFile = ImageIO.read(imgFile).getScaledInstance(-1, thumbnailSize, BufferedImage.SCALE_SMOOTH);
        } else {
            scaledImgFile = ImageIO.read(imgFile).getScaledInstance(thumbnailSize, -1, BufferedImage.SCALE_SMOOTH);
        }

        BufferedImage img = ImageFileUtil.toBufferedImage(scaledImgFile);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ImageIO.write(img, "png", bos);

        return bos.toByteArray();
    }

}
