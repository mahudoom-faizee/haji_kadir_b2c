package winapp.hajikadir.customer.util;

import android.os.Environment;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.DecimalFormat;

/**
 * Created by user on 15-Jul-16.
 */
public class FileUtil implements Constants{

    public static String APP_LOG_DIR = "";
    static {
        init();
    }

    public static void init() {
        try {
            File app_dir = new File(Environment.getExternalStorageDirectory(),
                    APP_NAME);
            if (!app_dir.exists()) {
                app_dir.mkdirs();
                //L.debug("APP folder does not exists and created...");
            }

            APP_LOG_DIR = app_dir.getAbsolutePath();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String appendToFile(String file_path, String msg) {
        String sFilePath = null;
        try {

            sFilePath = file_path;
            File file = new File(sFilePath);

            // if file doesnt exists, then create it
            if (!file.exists()) {
                file.createNewFile();
            }

            PrintWriter out = new PrintWriter(new BufferedWriter(
                    new FileWriter(file.getAbsoluteFile(), true)));
            out.println(msg);
            out.close();
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        return sFilePath;
    }

    public static String writeToXMLFile(String file_path, String msg) {
        String sFilePath = null;
        try {

            sFilePath = file_path;
            File file = new File(sFilePath);

            // if file doesnt exists, then create it
            if (!file.exists()) {
                file.createNewFile();
            }

            PrintWriter out = new PrintWriter(new BufferedWriter(
                    new FileWriter(file.getAbsoluteFile(), true)));
            out.println(msg);
            out.close();
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        return sFilePath;
    }

    public static String writeToSDFile(String file_path, String msg) {
        String sFilePath = null;
        try {

            // L.debug("file_path -> " + file_path);

            sFilePath = file_path;

            File file = new File(sFilePath);

            file.createNewFile();

            // L.debug("outFileName " + sFilePath);

            OutputStream myOutput = new FileOutputStream(sFilePath);

            byte[] buffer = new byte[1024];
            int length;

            InputStream myInput = new DataInputStream(new ByteArrayInputStream(
                    msg.getBytes()));
            while ((length = myInput.read(buffer)) > 0) {
                myOutput.write(buffer, 0, length);
            }
            myInput.close();

            myOutput.flush();
            myOutput.close();
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        return sFilePath;
    }

    public static String readFile(String sFilePath) {
        String sFileContent = "";
        // Get the text file
        File file = new File(sFilePath);

        if (file.exists()) // check if file exist
        {
            // Read text from file
            StringBuilder text = new StringBuilder();

            try {
                BufferedReader br = new BufferedReader(new FileReader(file));
                String line;

                while ((line = br.readLine()) != null) {
                    text.append(line);
                }
            } catch (IOException e) {
            }
            // Set the text
            sFileContent = text.toString();
        } else {
            sFileContent = null;
        }

        return sFileContent;
    }

    public static void clearDirectory(File fileOrDirectory) {

        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                clearDirectory(child);
        if (fileOrDirectory.isFile())
            fileOrDirectory.delete();
    }

    public static void delete(String filepath) {
        File file = new File(filepath);
        if (file.exists())
            file.delete();
    }

    public static String readableFileSize(long size) {
        if (size <= 0)
            return "0";
        final String[] units = new String[] { "B", "KB", "MB", "GB", "TB" };
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size
                / Math.pow(1024, digitGroups))
                + " " + units[digitGroups];
    }
}
