package com.letv.plugin.pluginloader.service;

import android.app.Service;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.os.IBinder;

import com.letv.plugin.pluginloader.loader.JarClassLoader;
import com.letv.plugin.pluginloader.loader.JarLoader;
import com.letv.plugin.pluginloader.loader.JarResOverrideInterface;
import com.letv.plugin.pluginloader.loader.JarResources;

public class JarMainBaseService extends Service implements JarResOverrideInterface {
	private static final String TAG = "JarMainBaseService";
	private JarResources myResources;
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
		return myResources;
	}
	
	@Override
	public void setOverrideResources(JarResources myres) {
		if (myres == null) {
			this.myResources = null;
			this.resources = null;
			this.assetManager = null;
			this.theme = null;
		} else {
			this.myResources = myres;
			this.resources = myres.getResources();
			this.assetManager = myres.getAssets();
			Theme t = myres.getResources().newTheme();
			t.setTo(getTheme());
			this.theme = t;
		}
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void setResourcePath(boolean isPlugin, String jarname,
			String jar_packagename) {
		if(isPlugin){
			JarClassLoader jcl = JarLoader.getJarClassLoader(this, jarname, jar_packagename);
			JarResources jres = JarResources.getResourceByCl(jcl, getApplicationContext());
			setOverrideResources(jres);
		}else{
			setOverrideResources(null);
		}
	}
}
