package com.tongbanjie.tarzan.common.extension;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Enumeration;
import java.util.Map;
import java.util.Objects;
import java.util.ServiceConfigurationError;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 〈扩展加载器〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019/3/18
 */
public final class ExtensionLoader<S> {

    private static final String PREFIX = "META-INF/extensions/";

    // The class or interface representing the service being loaded
    private final Class<S> service;

    // The class loader used to locate, load, and instantiate providers
    private final ClassLoader loader;

    // The access control context taken when the ServiceLoader is created
    private final AccessControlContext acc;

    // Cached providers, in instantiation order
    private Map<String, S> providers = new ConcurrentHashMap<String, S>();

    // cached extension name and extension className
    private ConcurrentMap<String, String> nameClassMap = new ConcurrentHashMap<String, String>();

    private LazyFinder lookupFinder;

    /**
     * Clear this loader's provider cache so that all providers will be
     * reloaded.
     *
     * <p> This method is intended for use in situations in which new providers
     * can be installed into a running Java virtual machine.
     */
    public void reload() {
        providers.clear();
        lookupFinder = new LazyFinder(service, loader);
    }

    private ExtensionLoader(Class<S> svc, ClassLoader cl) {
        service = Objects.requireNonNull(svc, "Service interface cannot be null");
        loader = (cl == null) ? ClassLoader.getSystemClassLoader() : cl;
        acc = (System.getSecurityManager() != null) ? AccessController.getContext() : null;
        reload();
    }

    private static void fail(Class<?> service, String msg, Throwable cause)
            throws ServiceConfigurationError {
        throw new ServiceConfigurationError(service.getName() + ": " + msg,
                cause);
    }

    private static void fail(Class<?> service, String msg)
            throws ServiceConfigurationError {
        throw new ServiceConfigurationError(service.getName() + ": " + msg);
    }

    private static void fail(Class<?> service, URL u, int line, String msg)
            throws ServiceConfigurationError {
        fail(service, u + ":" + line + ": " + msg);
    }

    // Parse a single line from the given configuration file, adding the name
    // on the line to the names list.
    //
    private int parseLine(Class<?> service, URL u, BufferedReader r, int lc)
            throws IOException, ServiceConfigurationError {
        String extensionLine = r.readLine();
        if (extensionLine == null) {
            return -1;
        }
        int ci = extensionLine.indexOf('#');
        if (ci >= 0) {
            extensionLine = extensionLine.substring(0, ci);
        }
        extensionLine = extensionLine.trim();
        if (extensionLine.length() != 0) {
            if ((extensionLine.indexOf(' ') >= 0) || (extensionLine.indexOf('\t') >= 0)) {
                fail(service, u, lc, "Illegal configuration-file syntax");
            }
            ExtensionPair extensionPair = ExtensionPair.build(service, extensionLine);

            String classString = extensionPair.getClassName();
            int cp = classString.codePointAt(0);
            if (!Character.isJavaIdentifierStart(cp)) {
                fail(service, u, lc, "Illegal provider-class name: " + classString);
            }
            int classLength = classString.length();
            for (int i = Character.charCount(cp); i < classLength; i += Character.charCount(cp)) {
                cp = classString.codePointAt(i);
                if (!Character.isJavaIdentifierPart(cp) && (cp != '.')) {
                    fail(service, u, lc, "Illegal provider-class name: " + classString);
                }
            }

            nameClassMap.putIfAbsent(extensionPair.getName(), extensionPair.getClassName());
        }
        return lc + 1;
    }

    private String parse(Class<?> service, String serviceKey, URL u) throws ServiceConfigurationError {
        InputStream in = null;
        BufferedReader r = null;
        try {
            in = u.openStream();
            r = new BufferedReader(new InputStreamReader(in, "utf-8"));
            int lc = 1;
            while ((lc = parseLine(service, u, r, lc)) >= 0) {

            }
        } catch (IOException x) {
            fail(service, "Error reading configuration file", x);
        } finally {
            try {
                if (r != null) {
                    r.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException y) {
                fail(service, "Error closing configuration file", y);
            }
        }
        return nameClassMap.get(serviceKey);
    }

    private class LazyFinder {

        Class<S> service;
        ClassLoader loader;
        Enumeration<URL> configs = null;

        private LazyFinder(Class<S> service, ClassLoader loader) {
            this.service = service;
            this.loader = loader;
            init();
        }

        private void init(){
            try {
                String fullName = PREFIX + service.getName();
                if (loader == null) {
                    configs = ClassLoader.getSystemResources(fullName);
                } else {
                    configs = loader.getResources(fullName);
                }
            } catch (IOException x) {
                fail(service, "Error locating configuration files", x);
            }
        }

        public S find(final String name){
            //if exist in cache
            if(providers.containsKey(name)){
                return providers.get(name);
            }

            synchronized(this){
                if(providers.containsKey(name)){
                    return providers.get(name);
                }
                //lookup
                if (acc == null) {
                    return findService(name);
                } else {
                    PrivilegedAction<S> action = new PrivilegedAction<S>() {
                        @Override
                        public S run() { return findService(name); }
                    };
                    return AccessController.doPrivileged(action, acc);
                }
            }
        }

        private S findService(String name) {
            String className = nameClassMap.get(name);
            while (className == null && configs.hasMoreElements()){
                className = parse(service, name, configs.nextElement());
            }
            if(className == null){
                return null;
            }
            Class<?> clazz = null;
            try {
                clazz = Class.forName(className, false, loader);
            } catch (ClassNotFoundException x) {
                fail(service, "Provider " + className + " not found");
            }
            if (!service.isAssignableFrom(clazz)) {
                fail(service, "Provider " + className + " not a subtype");
            }
            try {
                S serviceObj = service.cast(clazz.newInstance());
                providers.put(name, serviceObj);
                return serviceObj;
            } catch (Throwable x) {
                fail(service, "Provider " + className + " could not be instantiated", x);
            }
            throw new Error();
        }

    }

    /**
     * lookup extension by name
     * @param name
     * @return
     */
    public S find(String name){
        return this.lookupFinder.find(name);
    }

    /**
     * get default extension that configured in SPI annotation
     * @return
     */
    public S get(){
        SPI spi = service.getAnnotation(SPI.class);
        if(spi == null){
            fail(service, "No annotation SPI configured.");
        }
        String extensionName = spi.value();
        if(extensionName == null || extensionName.length() == 0){
            fail(service, "No default extension name configured.");
        }
        return find(extensionName);
    }

    /**
     * Creates a new service loader for the given service type and class
     * loader.
     *
     * @param  <S> the class of the service type
     *
     * @param  service
     *         The interface or abstract class representing the service
     *
     * @param  loader
     *         The class loader to be used to load provider-configuration files
     *         and provider classes, or <tt>null</tt> if the system class
     *         loader (or, failing that, the bootstrap class loader) is to be
     *         used
     *
     * @return A new service loader
     */
    public static <S> ExtensionLoader<S> load(Class<S> service, ClassLoader loader) {
        return new ExtensionLoader<S>(service, loader);
    }

    /**
     * Creates a new service loader for the given service type, using the
     * current thread's {@linkplain java.lang.Thread#getContextClassLoader
     * context class loader}.
     *
     * @param  <S> the class of the service type
     *
     * @param  service
     *         The interface or abstract class representing the service
     *
     * @return A new service loader
     */
    public static <S> ExtensionLoader<S> load(Class<S> service) {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        return ExtensionLoader.load(service, cl);
    }

    /**
     * Returns a string describing this service.
     *
     * @return  A descriptive string
     */
    @Override
    public String toString() {
        return "ExtensionLoader[" + service.getName() + "]";
    }

}

