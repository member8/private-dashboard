package com.purplepinemusic.helper;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SenderHelper extends CommonHelper {

    public static final Logger log = LogManager.getLogger( SenderHelper.class );

    @SuppressWarnings("unchecked")
    public static List<Map<String,Object>> sendToEmail(Map<String,Object> tmap, Map<String,Object> cmap){

        List<Map<String,Object>> _tlist = (List<Map<String, Object>>) cmap.get( tmap.get( EXCEL_TARGET_NM ) );
        List<Map<String,Object>> _rlist = new ArrayList<Map<String,Object>>();

        int _fail = 0, _succ = 0;

        if( _tlist != null && _tlist.size() > 0 ){

            String _cserver    = String.valueOf( tmap.get( C_SERVER   ) )  /* EMAIL 서버 IP */
                  ,_cname      = String.valueOf( tmap.get( C_NAME     ) )  /* 이메일 발송자 명 */
                  ,_cattach    = String.valueOf( tmap.get( C_ATTACH   ) )  /* 첨부 파일 위치 */
                  ,_cid        = String.valueOf( tmap.get( C_ID       ) )  /* 계정 ID */
                  ,_cpw        = String.valueOf( tmap.get( C_PW       ) )  /* 계정 PW */
                  ,_debug      = String.valueOf( tmap.get( C_DEBUG    ) ); /* 이메일 디버그 */

            int _climit        = Integer.parseInt( String.valueOf( tmap.get( C_LIMIT ) ) ); /* 계청 최대 발송 이메일 갯수 */

            Properties mailp = new Properties();
            mailp.put("mail.smtp.host", _cserver);
            mailp.put("mail.smtp.auth", "true");
            mailp.put("mail.smtp.connectiontimeout", "5000");
            mailp.put("mail.smtp.timeout", "5000");
            if( _debug != null && !_debug.equals("") && !_debug.equals("null") ){
                mailp.put("mail.debug", _debug);
            }

            Session session = Session.getInstance(mailp, new javax.mail.Authenticator() {
                protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
                    return new javax.mail.PasswordAuthentication( _cid, _cpw );
                }
            });

            for( int i = 0 ; i < _tlist.size() ; i++ ){

                String procMsg = "";

                Map<String,Object> pmap = _tlist.get(i);

                String _name = "",_email = "",_ref = "",_bcc = "",_subject = "",_content = "",_attch = "",_sendyn = "", _procYn = "";

                try {

                    _name    = String.valueOf( pmap.get(E_NAME   ) );
                    _email   = String.valueOf( pmap.get(E_EMAIL  ) );
                    _ref     = String.valueOf( pmap.get(E_REF    ) );
                    _bcc     = String.valueOf( pmap.get(E_BCC    ) );
                    _subject = String.valueOf( pmap.get(E_SUBJECT) );
                    _content = String.valueOf( pmap.get(E_CONTENT) );
                    _attch   = String.valueOf( pmap.get(E_ATTCH  ) );
                    _sendyn  = String.valueOf( pmap.get(E_PROC_YN) );

                    if( _sendyn != null && _sendyn.equals("N") ){

                        if( i < _climit + 1 ){

                            MimeMessage mimemessage = new MimeMessage(session);

                            InternetAddress insertnetaddress = new InternetAddress(_cserver, _cname, "euc-kr");

                            mimemessage.setFrom(insertnetaddress);
                            mimemessage.setRecipients(javax.mail.Message.RecipientType.TO, InternetAddress.parse(_email));
                            mimemessage.setRecipients(javax.mail.Message.RecipientType.CC, InternetAddress.parse(_ref));
                            mimemessage.setRecipients(javax.mail.Message.RecipientType.BCC, InternetAddress.parse(_bcc));
                            mimemessage.setSubject   (MimeUtility.encodeText(_subject, "UTF-8", "B"));
                            mimemessage.setContent   (_content, "text/html; charset=UTF-8");
                            mimemessage.setSentDate  (new Date());

                            MimeBodyPart mbp = new MimeBodyPart();
                            MimeMultipart mmp = new MimeMultipart();

                            mbp.setText(_content, "utf-8", "html; charset=UTF-8");
                            mmp.addBodyPart(mbp);

                            if(_attch != null && !_attch.equals("")){

                                MimeBodyPart mimebodypart1 = new MimeBodyPart();
                                FileDataSource filedatasource = new FileDataSource( _cattach.concat( _attch ) );

                                mimebodypart1.setDataHandler(new DataHandler(filedatasource));

                                _attch = MimeUtility.encodeText(_attch, "UTF-8", "B");

                                mimebodypart1.setFileName(_attch);

                                mmp.addBodyPart(mimebodypart1);

                                mimemessage.setContent(mmp);

                            }

                            Transport.send(mimemessage);

                            _succ++;
                            _procYn = "Y";
                            procMsg = "발송 완료";

                        }else{

                            log.error("e# 발송 최대 갯수 초과 ({}/{}) {} {}", _climit, i, _name, _email);
                            procMsg = "발송 최대 갯수 초과 ("+_climit+"/"+i+")";
                            _procYn = "E1";
                            _fail++;

                        }

                    }

                } catch (UnsupportedEncodingException e) {

                    _fail++;
                    _procYn = "E2";
                    log.error("e# 지원하지 않는 형식 문자  {} {} {}", _name, _email, e);
                    procMsg = "지원하지 않는 형식의 문자 " + e.getMessage();

                } catch (AddressException e) {

                    _fail++;
                    _procYn = "E3";
                    log.error("e# 잘못 된 이메일 주소  {} {} {}", _name, _email, e);
                    procMsg = "잘못 된 이메일 주소 " + e.getMessage();

                } catch (MessagingException e) {

                    _fail++;
                    _procYn = "E4";
                    log.error("e# 이메일 발송 오류  {} {} {}", _name, _email, e);
                    procMsg = "이메일 발송 오류 " + e.getMessage();

                } catch (IOException e) {

                    _fail++;
                    _procYn = "E5";
                    log.error("e# 첨부 파일 없음  {} {} {}", _name, _email, e);
                    procMsg = "첨부 파일 없음" + e.getMessage();

                } catch (Exception e) {

                    _fail++;
                    _procYn = "E6";
                    log.error("e# 에러메시지 확인 필요  {} {} {}", _name, _email, e);
                    procMsg = "에러메시지 확인 필요. " + e.getMessage();
                }

                pmap.put(PROC_MSG , procMsg);
                pmap.put(PROC_DATE, locationdate("yyyy-MM-dd HH:mm:ss.S") );
                pmap.put(E_PROC_YN, _procYn);

                Map<String,Object> _m = new HashMap<String,Object>( pmap );

                _rlist.add(_m);

            }

         }
/*
        Map<String,Object> _rmap = new HashMap<String,Object>( tmap );

        _rmap.put(EXCEL_TARGET_NM , _rlist );
        _rmap.put(EXCEL_TARGET_LENGTH , _rlist.size() );
        _rmap.put(SEND_SUCCESS, _succ);
        _rmap.put(SEND_FAIL, _fail);
*/
        return _rlist;

    }

}
