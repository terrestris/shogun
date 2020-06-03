package de.terrestris.shogun.lib.util;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FilenameUtils;
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
        ImageIO.write(img, FilenameUtils.getExtension(uploadFile.getOriginalFilename()), bos);

        return bos.toByteArray();
    }

}
