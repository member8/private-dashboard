package com.purplepinemusic.helper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FileHelper extends CommonHelper {

    public static final Logger log = LogManager.getLogger( FileHelper.class );

    private static final String TARGET_PATH = ROOT_PATH.concat( "/backup/" ).concat( yyyyMM() ).concat( "/" );

    public static void compress(String path){

        File _d = new File(TARGET_PATH);

        if( !_d.isDirectory() ){
            _d.mkdirs();
            log.info("# 백업 디렉토리 생성 {} \n", _d.getAbsolutePath());
        }

        File file = new File(path);

        for( File _f : file.listFiles() ){

            if( _f.isFile() ){

                String _zfile = TARGET_PATH.concat( yyyyMMddHHmmss().concat("_").concat( _f.getName().concat(".zip") ) );

                FileOutputStream fops = null;
                ZipOutputStream outputStream = null;

                try{

                    fops = new FileOutputStream( _zfile );

                    outputStream = new ZipOutputStream( fops );

                    outputStream.putNextEntry(new ZipEntry(_f.getName()));

                    byte[] bytes = Files.readAllBytes(_f.toPath());

                    outputStream.write(bytes, 0, bytes.length);

                    outputStream.closeEntry();

                }catch(IOException e){

                    log.error("# 백업 파일 압축 오류 [{}]", e.getMessage());

                }finally{

                    try {
                        outputStream.close();
                        fops.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }

                log.info("# 백업 대상 파일 {}", _f.getAbsolutePath());
                log.info("# 백업 압축 파일 {} \n", _zfile);

            }

        }

    }

}
