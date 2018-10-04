package com.purplepinemusic.helper;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CommonHelper {

    /*
        -Droot.path="c:/mailing"
        -Dlog4j.configurationFile="file:///c:/mailing/resources/log4j2.xml
    */

    protected static final String R_ACCOUNT           = "account";
    protected static final String R_CUSTOMER          = "customer";

    /* JSON 설정 파일 속성 */
    protected static final String C_SERVER            = "server";
    protected static final String C_LIMIT             = "limit";
    protected static final String C_NAME              = "name";
    protected static final String C_EXIST             = "exist";
    protected static final String C_SENDER            = "sender";
    protected static final String C_ID                = "id";
    protected static final String C_PW                = "pw";
    protected static final String C_EXCEL             = "excel";
    protected static final String C_EXCELEXT          = "excelext";
    protected static final String C_ATTACH            = "attach";
    protected static final String C_ATTACHEXT         = "attachext";
    protected static final String C_DEBUG             = "debug";

    /* 이메일 발송 대상 객체 이름 및 갯수 속성  */
    protected static final String EXCEL_TARGET_NM     = "elist";
    protected static final String EXCEL_TARGET_LENGTH = "elength";

    protected static final String SEND_SUCCESS        = "success";
    protected static final String SEND_FAIL           = "fail";

    /* 엑셀 파일 속성 */
    protected static final String E_NAME              = "name";
    protected static final String E_EMAIL             = "email";
    protected static final String E_REF               = "ref";
    protected static final String E_BCC               = "bcc";
    protected static final String E_SUBJECT           = "subject";
    protected static final String E_CONTENT           = "content";
    protected static final String E_ATTCH             = "attch";

    /* 메일 발송 여부 구분자 */
    protected static final String E_PROC_YN           = "sendyn";

    /* 프로그램 루트 */
    protected static final String ROOT_PATH           = System.getProperty("root.path");

    /* 처리결과일자 및 메시지 */
    protected static final String PROC_DATE           = "procDate";
    protected static final String PROC_MSG            = "procMsg";

    protected static String locationdate(String fmt){
        return locationdate( fmt , new Date() );
    }

    protected static String locationdate(String fmt, Date date){
        SimpleDateFormat sfmt = new SimpleDateFormat( fmt );
        return sfmt.format( date );
    }

    protected static String locationdate(){
        return locationdate("yyyy-MM-dd HH:mm:ss.S");
    }

    protected static String yyyyMM(){
        return locationdate("yyyyMM");
    }

    protected static String yyyyMMddHHmmss(){
        return locationdate("yyyyMMddHHmmssSSS");
    }

}
