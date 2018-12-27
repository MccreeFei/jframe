/**
 * 
 */
package jframe.core.util;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jframe.core.conf.VarHandler;

/**
 * <p>
 * Feature:
 * <li>属性分组,自定义组ID和默认组</li>
 * <li>属性值支持jframe配置变量${conf.key}</li>
 * <li>属性查询优先级,自定义ID->默认组</li>
 * </p>
 * 
 * @author dzh
 * @date Nov 17, 2014 4:52:33 PM
 * @since 1.0
 */
public class PropsConf {

    static final Logger LOG = LoggerFactory.getLogger(PropsConf.class);

    boolean init = false;

    public synchronized void init(String file) throws Exception {
        if (init) return;

        try {
            init(new FileInputStream(file));
        } catch (Exception e) {
            throw e;
        }
        init = true;
    }

    private Map<String, String> conf = Collections.emptyMap();

    public synchronized void init(InputStream is) throws Exception {
        if (is == null || init) return;

        conf = new HashMap<String, String>();
        try {
            Properties p = new Properties();
            p.load(is);

            for (Entry<Object, Object> e : p.entrySet()) {
                conf.put((String) e.getKey(), String.valueOf(e.getValue()).trim());
            }
        } catch (Exception e) {
            throw e;
        } finally {
            is.close();
        }
    }

    public synchronized void replace(VarHandler vh) {
        if (conf.isEmpty()) return;
        for (Entry<String, String> e : conf.entrySet()) {
            conf.put(e.getKey(), vh.replace(e.getValue()));
        }
    }

    public synchronized String[] getGroupIds() {
        String groups = conf.get("group.id");
        if (groups == null) return new String[0];
        return groups.split("\\s+");
    }

    /**
     * 
     * @param group
     * @param key
     * @return "" if not matched value or value
     */
    public synchronized String getConf(String group, String key) {
        if (conf.isEmpty()) return "";
        String val = null;
        if (group != null) {
            val = conf.get("@" + group + "." + key);
        }

        if (val == null) {
            val = conf.get(key);
        }
        return val == null ? "" : val;
    }

    /**
     * 
     * @param group
     * @param key
     * @param defVal
     * @return defVal if not matched value or value
     */
    public synchronized String getConf(String group, String key, String defVal) {
        if (conf.isEmpty()) return defVal;
        String val = null;
        if (group != null) {
            val = conf.get("@" + group + "." + key);
        }

        if (val == null) {
            val = conf.get(key);
        }
        return val == null ? defVal : val;
    }

    public synchronized int getConfInt(String group, String key, String defVal) {
        String val = getConf(group, key, defVal);
        if (val == null) return -1;
        return Integer.parseInt(val);
    }

    public synchronized boolean getConfBool(String group, String key, String defVal) {
        String val = getConf(group, key, defVal);
        if (val == null) return false;
        return Boolean.parseBoolean(val);
    }

    public synchronized long getConfLong(String group, String key, String defVal) {
        String val = getConf(group, key, defVal);
        if (val == null) return -1;
        return Long.parseLong(val);
    }

    public synchronized Properties clone2Properties() {
        Properties p = new Properties();
        if (conf.isEmpty()) return p;
        for (Entry<String, String> e : conf.entrySet()) {
            p.setProperty(e.getKey(), e.getValue());
        }
        return p;
    }

    public synchronized void clear() {
        conf.clear();
    }

    @Override
    public String toString() {
        return conf.toString();
    }
}
