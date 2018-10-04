package com.purplepinemusic;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.purplepinemusic.helper.CommonHelper;
import com.purplepinemusic.helper.ConfigHelper;
import com.purplepinemusic.helper.ExcelHelper;
import com.purplepinemusic.helper.FileHelper;
import com.purplepinemusic.helper.SenderHelper;

public class Mailer extends CommonHelper {

    public static Logger log = LogManager.getLogger( Mailer.class );

    @SuppressWarnings("unchecked")
    public static void main(String[] args){

        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");

        long start = System.currentTimeMillis() , end = 0;

        log.info("########################################################################################");
        log.info("#s# mailer ## "+ fmt.format( new Date(start) ) +" ################################################\n");

        /*List<Map<String,Object>> rlist = new ArrayList<Map<String,Object>>();*/

        try{

            List<Map<String,Object>> clist = ConfigHelper.getEmailConfig();

            Map<String,Object> rmap = ExcelHelper.getSendMailtoLsit(clist);

            List<Map<String,Object>> tlisg = (List<Map<String,Object>>) rmap.get(R_ACCOUNT);

            Map<String,Object> fmap = (Map<String,Object>) rmap.get(R_CUSTOMER);;

            /*for( Map<String,Object> _tmap : tlisg ){

                String _excel = (String)_tmap.get( EXCEL_TARGET_NM );

                List<Map<String,Object>> _tmp = (List<Map<String,Object>>) fmap.get( _excel );

                log.debug("{} {}", _excel, _tmp.size() );

            }*/

            for( Map<String,Object> _tmap : tlisg ){

                List<Map<String,Object>> _rlist = SenderHelper.sendToEmail(_tmap, fmap);

                log.info("## 계정 정보 ############################\n" );
                log.info("## 메일서버 [{}] ", _tmap.get( C_SERVER ) );
                log.info("## 발송한도 [{}] ", _tmap.get( C_LIMIT  ) );
                log.info("## 발송자명 [{}] ", _tmap.get( C_NAME   ) );
                log.info("## 중복여부 [{}] ", _tmap.get( C_EXIST  ) );
                log.info("## 발송자ID [{}] ", _tmap.get( C_ID     ) );
                log.info("## 엑셀위치 [{}] ", _tmap.get( C_EXCEL  ) );
                log.info("## 첨부위치 [{}] \n", _tmap.get( C_ATTACH ) );
                log.info("## 결과 ###############################\n" );

                for( Map<String,Object> _emap : _rlist ){

                    log.info("## 수신자명 : {} ", _emap.get( E_NAME    ) );
                    log.info("## E-MAIL   : {} ", _emap.get( E_EMAIL   ) );
                    log.info("## 참조     : {} ", _emap.get( E_REF     ) );
                    log.info("## 숨은참조 : {} ", _emap.get( E_BCC     ) );
                    log.info("## 제목     : {} ", _emap.get( E_SUBJECT ) );
                    log.info("## 첨부     : {} ", _emap.get( E_ATTCH   ) );
                    log.info("## 메시지   : {} ", _emap.get( PROC_MSG ) );
                    log.info("## 처리일   : {} \n", _emap.get( PROC_DATE ) );

                }

                log.info("###################################\n" );

            }

            for( String _key : fmap.keySet() ){

                int _succ = 0, _none = 0, _fail = 0, _e1 = 0, _e2 = 0, _e3 = 0, _e4 = 0, _e5 = 0, _e6 = 0;

                List<Map<String,Object>> _list = (List<Map<String,Object>>)fmap.get(_key);

                for(Map<String,Object> m : _list){

                    String _procYn = (String)m.get( E_PROC_YN );

                    if( _procYn.equals("Y") ){
                        _succ++;
                    }else if( _procYn.equals("E1") ){
                        _e1++;
                    }else if( _procYn.equals("E2") ){
                        _e2++;
                    }else if( _procYn.equals("E3") ){
                        _e3++;
                    }else if( _procYn.equals("E4") ){
                        _e4++;
                    }else if( _procYn.equals("E5") ){
                        _e5++;
                    }else if( _procYn.equals("N") ){
                        _none++;
                    }else{
                        _fail++;
                    }

                }

                log.info("## 총 건수 [{}] 전송[{}] 미처리[{}] 초과[{}] 문자오류[{}] 잘못된 이메일[{}] 발송오류[{}] 첨부오류[{}] 그외[{}] 경로[{}]\n", _list.size(), _succ, _none, _e1, _e2, _e3, _e4, _e5, _fail , _key );

            }

            log.info("###################################\n" );



            for( Map<String,Object> _tmap : tlisg ){

                String _excel = (String)_tmap.get(C_EXCEL);

                FileHelper.compress( _excel );

            }

        }catch(IOException ie){

            ie.printStackTrace();
            log.error(ie);

        }catch(Exception e){

            e.printStackTrace();
            log.error(e);

        }

        end = System.currentTimeMillis();

        log.info("#e# "+ fmt.format( new Date(end) ) +" ### "+((end-start)/1000)/60+"m / "+(end-start)/1000+"s #############################################");
        log.info("########################################################################################\n");

    }

}
