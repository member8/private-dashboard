package com.purplepinemusic.helper;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.extractor.XSSFExcelExtractor;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelHelper extends CommonHelper {

    public static final Logger log = LogManager.getLogger( ExcelHelper.class );

    /*
     * 계정 limit 기준으로 메일 발송하도록 변경
     * */

    @SuppressWarnings("unchecked")
    public static Map<String,Object> getSendMailtoLsit( List<Map<String,Object>> tolist ) throws IOException, InvalidFormatException{

        String[] CELL_ARY = {E_NAME ,E_EMAIL ,E_REF ,E_BCC ,E_SUBJECT ,E_CONTENT ,E_ATTCH };

        /*
         * 결과 리턴용 map
         *
         * Map - Account List  - Map(0) > 메일 계정 정보
         *                       Map(1) > 메일 계정 정보
         *                       Map(2) > 메일 계정 정보
         *                       Map(n) > 메일 계정 정보
         *
         *     - customer List - Map(0) - excel List > 발송 대상 정보
         *                       Map(1) - excel List > 발송 대상 정보
         *                       Map(2) - excel List > 발송 대상 정보
         *                       Map(n) - excel List > 발송 대상 정보
         * */
        Map<String,Object> _rmap = new HashMap<String,Object>();

        /* 계정 정보 목록
         *
         * Account List  - Map(0) > 메일 계정 정보
         *                 Map(1) > 메일 계정 정보
         *                 Map(2) > 메일 계정 정보
         *                 Map(n) > 메일 계정 정보
         * * */
        List<Map<String,Object>> _rlist = new ArrayList<Map<String,Object>>();

        /* 메일 발송 대상 목록
         *
         * Customer List - Map(0) - excel List > 발송 대상 정보
         *                 Map(1) - excel List > 발송 대상 정보
         *                 Map(2) - excel List > 발송 대상 정보
         *                 Map(n) - excel List > 발송 대상 정보
         *
         * */
        Map<String,Object> _fmap = new HashMap<String,Object>();

        /* 중복 확인 변수
         * Map<String,Object>
         *     key    : 파일 경로
         *     value  : 파일의 메일 발송 대상 목록
         *              엑셀 갯수와 상관없이 대상 추출하여 List<Map<String,Object>> 에 담음
         * */

        Map<String, String> _pmap = new HashMap<String, String>()  /* 중복위치, 확장자 제외  */
                          , _emap = new HashMap<String, String>(); /* 중복 이메일 제외 */

        /* 전송 대상 목록 */
        for( Map<String,Object> _m : tolist ){

            log.info("## 계정 정보 이름 [{}] ID [{}]", _m.get("name"), _m.get("id") );

            List<Map<String,Object>> _tlist = new ArrayList<Map<String,Object>>();

            String _p = String.valueOf( _m.get( C_EXCEL    ) )
                  ,_e = String.valueOf( _m.get( C_EXCELEXT ) ).toUpperCase()
                  ,_t = String.valueOf( _m.get( C_EXIST    ) );

                  if( _e == null || _e.equalsIgnoreCase("null") ) _e = "XLSX";

            /* 중복 위치 제외 */
            if( _pmap.containsKey(_p) == false ){

                File _f = new File( _p );

                File[] _flist = _f.listFiles();

                /* 전송 대상 목록 기준 파일 */
                for( File _fobj : _flist ){

                    int _ftcnt = 0, _fecnt = 0; /* 파일 당 발송 대상 건수 */

                    /*
                     * excelext 기준 파일 확장 확인
                     * 기준 디렉토리의 모든 엑셀을 읽어 List<Map> 형식으로 저장
                     * */
                    if( _fobj.isFile() && _fobj.getName().toUpperCase().lastIndexOf( _e ) != -1 ){

                        try {

                            OPCPackage pkg = OPCPackage.open(new FileInputStream(_fobj));
                            XSSFWorkbook wb = new XSSFWorkbook(pkg);

                            XSSFExcelExtractor ee = new XSSFExcelExtractor(wb);
                            ee.setFormulasNotResults(true);
                            ee.setIncludeSheetNames(false);

                            for( int i = 0 ; i < wb.getNumberOfSheets(); i++ ){

                                XSSFSheet sheet = wb.getSheetAt(i);

                                for ( int r = 1 ; r < sheet.getLastRowNum() ; r++ ){

                                    String _tmail = "";

                                    Row row = sheet.getRow(r);

                                    Map<String,Object> _tomap = new LinkedHashMap<String,Object>();

                                    for( int c = 0 ; c < CELL_ARY.length ; c++ ){

                                        String _cvalue = row.getCell(c).getRichStringCellValue().getString();

                                        if( CELL_ARY[c].equals(E_EMAIL) ){
                                            _tmail = _cvalue;
                                        }

                                        if( CELL_ARY[c].equals(E_EMAIL)
                                         || CELL_ARY[c].equals(E_REF  )
                                         || CELL_ARY[c].equals(E_BCC  )
                                         || CELL_ARY[c].equals(E_ATTCH) ){
                                            _cvalue = _cvalue.replaceAll("\n", ",");
                                        }

                                        _tomap.put( CELL_ARY[c] , _cvalue);

                                    }

                                    /* 중복되는 이메일 제외
                                     * 또는 중복입력 가능 파라미터가 1 이면
                                     * */
                                    if( !_emap.containsKey(_tmail) || _t.equals("1") ){

                                        _tomap.put( E_PROC_YN , "N");

                                        _emap.put(_tmail, "1");

                                        _tlist.add(_tomap);

                                        _ftcnt++;

                                    }else{

                                        _fecnt++;

                                    }


                                }

                            }

                            ee.close();

                        }catch (IOException ie){
                            throw new IOException( ie );
                        }catch (InvalidFormatException fe) {
                            throw new InvalidFormatException(fe.getMessage());
                        }/*finally{

                            _tlist = new ArrayList<Map<String,Object>>();

                        }*/

                        log.info("## 발송 / 제외 [{}/{}] 누적 [{}] 파일 정보 [{}]", _ftcnt, _fecnt, _tlist.size() , _fobj.getAbsolutePath());

                    }

                    _pmap.put(_p, _e);

                }

               /*
                * 엑셀 파일 위치의 모든 발송(= 경로의 모든 엑셀 파일 ) 정보를 담은 List 를 key(경로) 기준으로 map에 담음
                * */
               _fmap.put(_p, _tlist);

               log.debug("#> 발송 총 갯수 [{}] 계정 발송한도 [{}] 대상 위치 [{}]\n", _tlist.size(), _m.get("limit"), _p);

            }

            _m.put( EXCEL_TARGET_NM , _p );
/*
            _m.put( EXCEL_TARGET_LENGTH, _tlist.size() );
*/
            Map<String, Object> _nm = new HashMap<String, Object>(_m);

            _rlist.add( _nm );

        }

        _rmap.put(R_ACCOUNT , _rlist);
        _rmap.put(R_CUSTOMER, _fmap );

        return _rmap;

    }

}
