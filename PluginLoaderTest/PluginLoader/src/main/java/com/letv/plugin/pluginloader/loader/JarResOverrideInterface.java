package com.letv.plugin.pluginloader.loader;

public interface JarResOverrideInterface {
	void setOverrideResources(JarResources myres);
	JarResources getOverrideResources();
	void setResourcePath(boolean isPlugin, String jarname, String jar_packagename);
}
