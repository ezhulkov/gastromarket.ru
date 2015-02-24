import org.ohm.gastro.filter.UploadFilter

import javax.imageio.ImageIO

def file = new File("/Users/ezhulkov/Downloads/tvorog.jpg")
def image = ImageIO.read(file)

image = UploadFilter.resizeImage(image, 430, 310)
ImageIO.write(image, "jpeg", new File("/Users/ezhulkov/Downloads/tvorog_res.jpg"));