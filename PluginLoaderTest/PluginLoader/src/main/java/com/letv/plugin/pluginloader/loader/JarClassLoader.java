package com.letv.plugin.pluginloader.loader;

import dalvik.system.DexClassLoader;

public class JarClassLoader extends DexClassLoader {
	public String mPackagename;
	public String mJarpath;
	
	public JarClassLoader(String packagename, String dexPath, String optimizedDirectory,
			String libraryPath, ClassLoader parent) {
		super(dexPath, optimizedDirectory, libraryPath, parent);
		this.mPackagename = packagename;
		this.mJarpath = dexPath;
	}
}
