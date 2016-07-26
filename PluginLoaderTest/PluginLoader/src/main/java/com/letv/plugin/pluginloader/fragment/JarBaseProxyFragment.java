
package com.letv.plugin.pluginloader.fragment;

import android.support.v4.app.Fragment;

import com.letv.plugin.pluginloader.loader.JarClassLoader;
import com.letv.plugin.pluginloader.loader.JarLoader;
import com.letv.plugin.pluginloader.loader.JarResOverrideInterface;
import com.letv.plugin.pluginloader.loader.JarResources;

/**
 * 主程序如需调用插件,则需要继承此类 主要功能:实现插件res资源的调用
 */
public class JarBaseProxyFragment extends Fragment implements JarResOverrideInterface {
    private static final String TAG = "JarBaseProxyFragment";
    private JarResources mJarResources;

    @Override
    public JarResources getOverrideResources() {
        return mJarResources;
    }

    @Override
    public void setOverrideResources(JarResources myres) {
        this.mJarResources = myres;
    }

    /**
     * @param isPlugin 是否切换到插件资源
     * @param jarname 插件名称 isPlugin==false时传null
     * @param jar_packagename 插件包名 isPlugin==false时传null
     */
    @Override
    public void setResourcePath(boolean isPlugin, String jarname,
            String jar_packagename) {
        if (isPlugin) {
            JarClassLoader jcl = JarLoader.getJarClassLoader(getActivity(), jarname,
                    jar_packagename);
            JarResources jres = JarResources.getResourceByCl(jcl, getActivity());
            setOverrideResources(jres);
        } else {
            setOverrideResources(null);
        }
    }
}
