import org.apache.commons.io.IOUtils;
import org.codehaus.plexus.archiver.tar.TarArchiver;
import org.tukaani.xz.*;

import javax.swing.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;

public class StringTutorial {
    public static void main(String[] args) throws Exception{
        int window = 300;
        TarArchiver tarArchiver = new TarArchiver();
        tarArchiver.addDirectory(new File(args[0]));
        File destFile = new File(System.getProperty("user.dir"), UUID.randomUUID().toString()+".tar");
        tarArchiver.setDestFile(destFile);
        tarArchiver.createArchive();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        LZMA2Options filterOptions = new LZMA2Options();
        filterOptions.setPreset(9);
        try (XZOutputStream outputStream = new XZOutputStream(stream, filterOptions)){
            IOUtils.copy(new FileInputStream(destFile),outputStream);
        }
        JDialog dialog = new JDialog();
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        JLabel jLabel = new JLabel();
        dialog.setLocation(0,window*2);
        dialog.add(jLabel);
        dialog.setSize(window,window);
        dialog.pack();
        String src = Base64.getEncoder().encodeToString(stream.toByteArray());
        StringBuilder stringBuilder = new StringBuilder();
        int i=0;
        for(int idx=0; idx < src.length(); idx+=window, i++){
            String substring = src.substring(idx, idx + window > src.length() ? src.length() : idx + window);
            jLabel.setText(substring);
            dialog.pack();
            dialog.setVisible(true);
            System.out.println(i);
            Thread.sleep(TimeUnit.SECONDS.toMillis(2));
            stringBuilder.append(substring);
        }
        destFile.delete();
        System.out.println(stream.size());
        if(!stringBuilder.toString().equals(src)){
            throw new IllegalArgumentException();
        }
        System.exit(0);
    }
}
