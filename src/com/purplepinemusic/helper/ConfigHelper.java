package com.purplepinemusic.helper;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class ConfigHelper extends CommonHelper {

    public static final Logger log = LogManager.getLogger( ConfigHelper.class );

    protected static final String RESOURCE_SUBFIX = "resources";
    protected static final String EMAIL_CONFIG_EXT = "mailing.config.json";

    /* resource 지정 디렉토리의 모든 Json 설정 파일을 읽어서 list<map> 형식으로 저장 */

    /*public static void main( String[] args ) {
        try {
            getJsonConfig();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }*/

    private static String findResources( String fnm ){

        String rpath = findResourcePath();

        log.info("# findResources path {}",rpath);

        List<String> rlist = findConfigFile(rpath, fnm);

        log.info("# findResources list {} \n",rlist);

        return rlist.get(0);

    }

    private static List<String> findResourcesList(){

        String rpath = findResourcePath();

        log.info("# findResourcesList path {}",rpath);

        List<String> rlist = findConfigFile(rpath);

        log.info("# findResourcesList list {}\n",rlist);

        return rlist;

    }

    /* 최초 선택되는 resource 디렉토리 경로 리턴 */
    private static String findResourcePath(){
        return findResourcePath(null);
    }

    private static String findResourcePath(String path){

        path = path == null ? ROOT_PATH : path;

        String _rPath = null;

        File _file = new File( path );

        File[] _flist = _file.listFiles();

        for( File _f : _flist ){

            if( _f.isDirectory() ){

                String _fnm = _f.getName();

                if( _fnm != null && _fnm.equalsIgnoreCase(RESOURCE_SUBFIX) ){
                    return _f.getAbsolutePath();
                }

                _rPath = findResourcePath( _f.getAbsolutePath() );

            }

            if( _rPath != null ){

                break;

            }

        }

        return _rPath;

    }

    /* 지정된 경로에서 환경설정 파일 목록 최득 */
    private static List<String> findConfigFile(String path){
        return findConfigFile(path, null);
    }

    private static List<String> findConfigFile(String path, String ext){

        List<String> _clist = new ArrayList<String>();

        File _file = new File(path);

        File[] _flist = _file.listFiles();

        for( File _f : _flist ){

            if( _f.isFile() ){

                String _fnm = _f.getName();

                if( _fnm != null && _fnm.indexOf(ext) != -1 ){

                    _clist.add( _f.getAbsolutePath() );

                }

            }

        }

        return _clist;

    }

    public static List<Map<String,Object>> getEmailConfig() throws IOException{
        return getJsonConfig( EMAIL_CONFIG_EXT );
    }

    private static List<Map<String,Object>> getJsonConfig(String fnm) throws IOException{

        Map<String, Object> _globalmap = new HashMap<String, Object>();

        List<Map<String,Object>> rlist = new ArrayList<Map<String,Object>>();

        String _jfile = findResources( fnm );

        JSONParser parser = new JSONParser();
        JSONObject jsonobj = null;

        try {
            jsonobj = (JSONObject) parser.parse( new FileReader(_jfile) );
        } catch ( IOException | ParseException e) {
            throw new IOException("JSON 설정 파일이 존재 하지 안거나 설정 형식이 올바르지 않습니다.\n"+_jfile+"\n"+e.getMessage());
        }

        _globalmap = objToMap( jsonobj ,  _globalmap );

        log.info("# 전역 정보 {} \n",_globalmap);

        JSONArray jsonary = (JSONArray) jsonobj.get("sender");

        for( int i = 0 ; i <  jsonary.size() ; i++ ){

            Map<String, Object> _map = new HashMap<String, Object>();

            JSONObject _jobj = (JSONObject)jsonary.get(i);

            _map = objToMap( _jobj , _map );

            if( _map.size() > 0 ){

                for( String _k : _globalmap.keySet() ){

                    if( _map.containsKey(_k) ){

                        if( _map.get(_k) == null || _map.get(_k).equals("") ){

                            _map.put(_k, _globalmap.get(_k));

                        }

                    }else{

                        _map.put(_k, _globalmap.get(_k));

                    }

                }

                rlist.add(_map);

            }

        }

        for( Map<String, Object> _lmap : rlist ){

            log.info("# 이메일 계정 정보 {} \n",_lmap);

        }

        return rlist;

    }

    private static Map<String,Object> objToMap( JSONObject obj, Map<String,Object> map ){

        for( Object _key : obj.keySet() ){

            String _skey = String.valueOf( _key );

            Object _obj = obj.get( _skey );

            if( _obj instanceof String ){

                String _value = String.valueOf( _obj );

                if( _value != null && !_value.equals("") ){

                    map.put( _skey , _value );

                }

            }

        }

        return map;

    }

}
