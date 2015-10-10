import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;
import java.util.stream.Collectors;

/**
 * Created by ezhulkov on 18.07.15.
 */
public class GMTest {

    @Test
    public void testFJPool() {

        final RecursiveTask<List<String>> task1 = new RecursiveTask<List<String>>() {
            @Override
            protected List<String> compute() {
                return new LinkedList<String>() {{
                    add("m 1");
                    add("m 2");
                    add("m 3");
                }};
            }
        };

        final RecursiveTask<List<String>> task2 = new RecursiveTask<List<String>>() {
            @Override
            protected List<String> compute() {
                final List<RecursiveTask<String>> subTasks = new LinkedList<>();
                for (int i = 0; i < 10; i++) {
                    final int tn = i;
                    final RecursiveTask<String> subTask = new RecursiveTask<String>() {
                        @Override
                        protected String compute() {
                            return "s " + tn;
                        }
                    };
                    subTask.fork();
                    subTasks.add(subTask);
                }
                return subTasks.stream().map(ForkJoinTask::join).collect(Collectors.toList());
            }
        };

        System.out.println(new ForkJoinPool().invoke(task1));
        System.out.println(new ForkJoinPool().invoke(task2));

    }

    @Test
    public void testScalr() throws IOException {
        final BufferedImage image = ImageIO.read(getClass().getClassLoader().getResourceAsStream("2.jpg"));
        ImageIO.write(resizeImage(image, 5472, 3648), "jpeg", new File("/Users/ezhulkov/Desktop/s/orig.jpeg"));
        ImageIO.write(resizeImage(image, 100, 100), "jpeg", new File("/Users/ezhulkov/Desktop/s/1.jpeg"));
        ImageIO.write(resizeImage(image, 400, 300), "jpeg", new File("/Users/ezhulkov/Desktop/s/2.jpeg"));
        ImageIO.write(resizeImage(image, 300, 400), "jpeg", new File("/Users/ezhulkov/Desktop/s/3.jpeg"));
        ImageIO.write(resizeImage(image, 1000, 720), "jpeg", new File("/Users/ezhulkov/Desktop/s/4.jpeg"));
        ImageIO.write(resizeImage(image, 720, 1000), "jpeg", new File("/Users/ezhulkov/Desktop/s/5.jpeg"));
        ImageIO.write(resizeImage(image, 720, 100), "jpeg", new File("/Users/ezhulkov/Desktop/s/6.jpeg"));
        ImageIO.write(resizeImage(image, 300, 1000), "jpeg", new File("/Users/ezhulkov/Desktop/s/7.jpeg"));
        ImageIO.write(resizeImage(image, 270, 270), "jpeg", new File("/Users/ezhulkov/Desktop/s/8.jpeg"));
    }

    private BufferedImage resizeImage(final BufferedImage originalImage, final int width, final int height) {
        final float originalWidth = originalImage.getWidth();
        final float originalHeight = originalImage.getHeight();
        final float croppedWidth;
        final float croppedHeight;
        if (originalHeight < height && originalWidth < width) {
            croppedHeight = originalHeight;
            croppedWidth = originalWidth;
        } else if (originalHeight / height == originalWidth / width) {
            croppedHeight = originalHeight;
            croppedWidth = originalWidth;
        } else if ((float) width / height > originalWidth / originalHeight) {
            if (width > originalWidth) {
                croppedHeight = height;
                croppedWidth = originalWidth;
            } else {
                croppedHeight = Math.min(originalHeight, height * originalWidth / width);
                croppedWidth = originalWidth;
            }
        } else if ((float) width / height < originalWidth / originalHeight) {
            if (height > originalHeight) {
                croppedHeight = originalHeight;
                croppedWidth = width;
            } else {
                croppedHeight = originalHeight;
                croppedWidth = Math.min(originalWidth, width * originalHeight / height);
            }
        } else {
            croppedHeight = 0;
            croppedWidth = 0;
        }

        final BufferedImage croppedImage = originalImage.getSubimage((int) (originalWidth - croppedWidth) / 2,
                                                                     (int) (originalHeight - croppedHeight) / 2,
                                                                     (int) croppedWidth,
                                                                     (int) croppedHeight);
        final BufferedImage resizedImage = new BufferedImage(width, height, ColorSpace.TYPE_RGB);
        final Graphics2D g = resizedImage.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);
        g.drawImage(croppedImage,
                    (int) Math.max(0, (width - croppedWidth) / 2),
                    (int) Math.max(0, (height - croppedHeight) / 2),
                    (int) Math.min(croppedWidth, width),
                    (int) Math.min(croppedHeight, height),
                    null);
        g.setColor(Color.GRAY);
        g.setStroke(new BasicStroke(2));
        g.drawRect(0, 0, width - 2, height - 2);
        g.dispose();
        return resizedImage;
    }


}

