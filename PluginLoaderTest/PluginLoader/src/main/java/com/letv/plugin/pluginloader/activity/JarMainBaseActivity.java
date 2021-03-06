package com.letv.plugin.pluginloader.activity;


import android.app.Activity;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.content.res.Resources.Theme;

import com.letv.plugin.pluginloader.loader.JarClassLoader;
import com.letv.plugin.pluginloader.loader.JarLoader;
import com.letv.plugin.pluginloader.loader.JarResOverrideInterface;
import com.letv.plugin.pluginloader.loader.JarResources;

/**
 * 主程序如需调用插件,则需要继承此类
 * 主要功能:实现插件res资源的调用
 * @author chenlifeng1
 */
public class JarMainBaseActivity extends Activity implements JarResOverrideInterface {
	private static final String TAG = "JarMainBaseActivity";
	private JarResources jarResources;
	private AssetManager assetManager;
	private Resources resources;
	private Theme theme;
	
	@Override
	public AssetManager getAssets() {
		return assetManager == null ? super.getAssets() : assetManager;
	}

	@Override
	public Resources getResources() {
		return resources == null ? super.getResources() : resources;
	}

	@Override
	public Theme getTheme() {
		return theme == null ? super.getTheme() : theme;
	}

	@Override
	public JarResources getOverrideResources() {
		return jarResources;
	}
	
	@Override
	public void setOverrideResources(JarResources myres) {
		if (myres == null) {
			this.jarResources = null;
			this.resources = null;
			this.assetManager = null;
			this.theme = null;
		} else {
			this.jarResources = myres;
			this.resources = myres.getResources();
			this.assetManager = myres.getAssets();
			Theme t = myres.getResources().newTheme();
			t.setTo(getTheme());
			this.theme = t;
		}
	}

	/**
	 * @param isPlugin 是否切换到插件资源
	 * @param jarname 插件名称	isPlugin==false时传null
	 * @param jar_packagename 插件包名  isPlugin==false时传null
	 */
	@Override
	public void setResourcePath(boolean isPlugin, String jarname,
			String jar_packagename) {
		if(isPlugin){
			JarClassLoader jcl = JarLoader.getJarClassLoader(this, jarname, jar_packagename);
			JarResources jres = JarResources.getResourceByCl(jcl, this);
			setOverrideResources(jres);
		}else{
			setOverrideResources(null);
		}
	}
}
