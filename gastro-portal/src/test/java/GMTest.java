import org.junit.Ignore;
import org.junit.Test;
import org.ohm.gastro.service.impl.ImageServiceImpl;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
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
    @Ignore
    public void testSphinx() throws SQLException {
        Connection con = null;
        String driver = "com.mysql.jdbc.Driver";
        String user = "root";
        String pass = "";
        ResultSet rs = null;
        ResultSetMetaData rsmd = null;

        try {
            Class.forName(driver);
        } catch (Exception e) {
            e.printStackTrace();
        }

        con = DriverManager.getConnection("jdbc:mysql://192.168.50.5:9306/data?characterEncoding=utf8&maxAllowedPacket=512000&relaxAutoCommit=true", user, pass);
        con.setAutoCommit(false);

        Statement s = con.createStatement();
        rs = s.executeQuery("select id,weight() from gastro_index where match('свадебный торт') and region='DEFAULT'");

        rsmd = rs.getMetaData();
        int cc = rsmd.getColumnCount();
        for (int i = 1; i <= cc; i++)
            System.out.println(rsmd.getColumnName(i));

        while (rs.next()) {
            System.out.println("Id: " + rs.getInt(1) + ", Weight: " + rs.getInt(2));
        }

        rs.close();
        con.close();

    }

    @Test
    @Ignore
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
    @Ignore
    public void testScalr() throws IOException {
        final BufferedImage image = ImageIO.read(getClass().getClassLoader().getResourceAsStream("4.jpg"));
//        /upload?file_type=PHOTO&object_id=3634&target_context=&s=1.6427380441522244&a=0&x=215&y=188&w=140&h=101
//        /upload?file_type=PHOTO&object_id=3641&target_context=&s=0.7384024541896362&a=0&x=187&y=255&w=140&h=101
//        0.6272572549271057&a=0&x=-47&y=-86&w=270&h=270
        ImageIO.write(ImageServiceImpl.resizeImage(image, 270, 270, "0.6272572549271057", "0", "-47", "-86", "270", "270"), "jpeg", new File("/Users/ezhulkov/Desktop/s/1.jpeg"));
//        ImageIO.write(ImageServiceImpl.resizeImage(image, 500, 500, "0.9205419161676651", "90", "97", "250", "210", "210"), "jpeg", new File("/Users/ezhulkov/Desktop/s/2.jpeg"));
//        ImageIO.write(ImageServiceImpl.resizeImage(image, 100, 100, "0.9205419161676651", "90", "97", "250", "210", "210"), "jpeg", new File("/Users/ezhulkov/Desktop/s/3.jpeg"));
//        ImageIO.write(ImageServiceImpl.resizeImage(image, 500, 334, "1.0", "180", "0", "0", "500", "334"), "jpeg", new File("/Users/ezhulkov/Desktop/s/r180.jpeg"));
//        ImageIO.write(ImageServiceImpl.resizeImage(image, 500, 334, "0.668", "270", "0", "0", "500", "334"), "jpeg", new File("/Users/ezhulkov/Desktop/s/r270.jpeg"));
//        ImageIO.write(ImageServiceImpl.resizeImage(image, 500, 334, "1.0", "360", "0", "0", "500", "334"), "jpeg", new File("/Users/ezhulkov/Desktop/s/r360.jpeg"));
//        ImageIO.write(ImageServiceImpl.resizeImage(image, 270, 270, null, null, null, null, null, null), "jpeg", new File("/Users/ezhulkov/Desktop/s/1.jpeg"));
//        ImageIO.write(ImageServiceImpl.resizeImage(image, 374, 270, null, null, null, null, null, null), "jpeg", new File("/Users/ezhulkov/Desktop/s/2.jpeg"));
//        ImageIO.write(ImageServiceImpl.resizeImage(image, 560, 404, null, null, null, null, null, null), "jpeg", new File("/Users/ezhulkov/Desktop/s/3.jpeg"));
//        ImageIO.write(ImageServiceImpl.resizeImage(image, 1000, 720, null, null, null, null, null, null), "jpeg", new File("/Users/ezhulkov/Desktop/s/4.jpeg"));
//        ImageIO.write(resizeImage(image, 100, 100), "jpeg", new File("/Users/ezhulkov/Desktop/s/1.jpeg"));
//        ImageIO.write(resizeImage(image, 400, 300), "jpeg", new File("/Users/ezhulkov/Desktop/s/2.jpeg"));
//        ImageIO.write(resizeImage(image, 300, 400), "jpeg", new File("/Users/ezhulkov/Desktop/s/3.jpeg"));
//        ImageIO.write(resizeImage(image, 1000, 720), "jpeg", new File("/Users/ezhulkov/Desktop/s/4.jpeg"));
//        ImageIO.write(resizeImage(image, 720, 1000), "jpeg", new File("/Users/ezhulkov/Desktop/s/5.jpeg"));
//        ImageIO.write(resizeImage(image, 720, 100), "jpeg", new File("/Users/ezhulkov/Desktop/s/6.jpeg"));
//        ImageIO.write(resizeImage(image, 300, 1000), "jpeg", new File("/Users/ezhulkov/Desktop/s/7.jpeg"));
//        ImageIO.write(resizeImage(image, 270, 270), "jpeg", new File("/Users/ezhulkov/Desktop/s/8.jpeg"));
    }


}

