package br.net.fabiozumbi12.redprotect;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Properties;
import java.util.SortedSet;
import java.util.TreeSet;

import org.spongepowered.api.entity.living.player.Player;

public class RPLang {
	
	static HashMap<String, String> BaseLang = new HashMap<String, String>();
	static HashMap<String, String> Lang = new HashMap<String, String>();
    static String pathLang;
    static File langFile;
    static String resLang;
	
	static SortedSet<String> helpStrings(){
		SortedSet<String> values = new TreeSet<String>();
		for (String help:Lang.keySet()){
			if (help.startsWith("cmdmanager.help.")){
				values.add(help.replace("cmdmanager.help.", ""));
			}
		}
		return values;
	}
	
	static void init() {
		pathLang = RedProtect.configDir + "lang" + RedProtect.cfgs.getString("language") + ".ini"; 
		langFile = new File(pathLang);
		resLang = "lang" + RedProtect.cfgs.getString("language") + ".ini";
						
		if (!langFile.exists()) {
			if (RedProtect.class.getResource(resLang) == null){		
				RedProtect.cfgs.setConfig("language", "EN-US");
				RedProtect.cfgs.save();
				resLang = "langEN-US.ini";
				pathLang = RedProtect.configDir + "langEN-US.ini";
			}
			
			try {
				InputStream isReader = RedProtect.class.getResourceAsStream(resLang);
				FileOutputStream fos = new FileOutputStream(langFile);
				while (isReader.available() > 0) {  // write contents of 'is' to 'fos'
			        fos.write(isReader.read());
			    }
			    fos.close();
			    isReader.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			//RedProtect.class.getResource(resLang);//create lang file
            RedProtect.logger.info("Created config file: " + pathLang);
        }
		
		loadLang();
		loadBaseLang();
		RedProtect.logger.info("Language file loaded - Using: "+ RedProtect.cfgs.getString("language"));	
	}
	
	static void loadBaseLang(){
	    BaseLang.clear();
	    Properties properties = new Properties();
	    try {
	    	InputStream fileInput = RedProtect.class.getResourceAsStream("langEN-US.ini");	      
	        Reader reader = new InputStreamReader(fileInput, "UTF-8");
	        properties.load(reader);
	    }
	    catch (Exception e)
	    {
	      e.printStackTrace();
	    }
	    for (Object key : properties.keySet()) {
	      if ((key instanceof String)) {
	    	  BaseLang.put((String)key, properties.getProperty((String)key));
	      }
	    }
	    updateLang();
	  }
	
	static void loadLang() {
		Lang.clear();
		Properties properties = new Properties();
		try {
			FileInputStream fileInput = new FileInputStream(pathLang);
			Reader reader = new InputStreamReader(fileInput, "UTF-8");
			properties.load(reader);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		for (Object key : properties.keySet()) {
			if (!(key instanceof String)) {
				continue;
			}			
			Lang.put((String) key, properties.getProperty((String) key));
		}		
		
		if (Lang.get("_lang.version") != null){
			int langv = Integer.parseInt(Lang.get("_lang.version").replace(".", ""));
			int rpv = Integer.parseInt(RedProtect.plugin.getVersion().get().replace(".", ""));
			if (langv < rpv || langv == 0){
				RedProtect.logger.warning("Your lang file is outdated. Probally need strings updates!");
				RedProtect.logger.warning("Lang file version: "+Lang.get("_lang.version"));
				Lang.put("_lang.version", RedProtect.plugin.getVersion().get());
			}
		}		
	}
	
	static void updateLang(){
	    for (String linha : BaseLang.keySet()) {	    	
	      if (!Lang.containsKey(linha)) {
	    	  Lang.put(linha, BaseLang.get(linha));
	      }
	    }
		if (!Lang.containsKey("_lang.version")){
			Lang.put("_lang.version", RedProtect.plugin.getVersion().get());
    	}
	    try {
	      Properties properties = new Properties()
	      {
	        private static final long serialVersionUID = 1L;	        
	        public synchronized Enumeration<Object> keys(){
	          return Collections.enumeration(new TreeSet<Object>(super.keySet()));
	        }
	      };
	      FileReader reader = new FileReader(pathLang);
	      BufferedReader bufferedReader = new BufferedReader(reader);
	      properties.load(bufferedReader);
	      bufferedReader.close();
	      reader.close();
	      properties.clear();
	      for (String key : Lang.keySet()) {
	        if ((key instanceof String)) {
	          properties.put(key, Lang.get(key));
	        }
	      }
	      properties.store(new OutputStreamWriter(new FileOutputStream(pathLang), "UTF-8"), null);
	    }
	    catch (Exception e)
	    {
	      e.printStackTrace();
	    }
	  }
	
	public static String get(String key){		
		String FMsg = "";

		if (Lang.get(key) == null){
			FMsg = "&c&oMissing language string for &4" + key;
		} else {
			FMsg = Lang.get(key);
		}
		
		return FMsg;
	}
	
	public static void sendMessage(Player p, String key){
		if (Lang.get(key) == null){
			p.sendMessage(RPUtil.toText(get("_redprotect.prefix")+" "+key));
		} else if (get(key).toString().equalsIgnoreCase("")){
			return;
		} else {
			p.sendMessage(RPUtil.toText(get("_redprotect.prefix")+" "+get(key)));
		}		
	}
	
	static String translBool(String bool){		
		return get("region."+bool).toString();
	}
	
	static String translBool(Boolean bool){		
		return get("region."+bool.toString()).toString();
	}
}
