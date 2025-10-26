package com.sschoi.vodict.plugin;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public class Activator extends AbstractUIPlugin {
    public static final String PLUGIN_ID = "com.sschoi.vodict.plugin";
    private static Activator plugin;

    public Activator() {
        System.out.println("🚀 VO Dictionary Plugin Activator 생성됨");
        System.err.println("🚀 VO Dictionary Plugin Activator 생성됨 (stderr)");
    }

    @Override
    public void start(BundleContext context) throws Exception {
        System.out.println("🔄 VO Dictionary Plugin 시작 중...");
        System.err.println("🔄 VO Dictionary Plugin 시작 중... (stderr)");
        
        super.start(context);
        plugin = this;
        
        System.out.println("✅ VO Dictionary Plugin 시작됨 (Activator.start)");
        System.err.println("✅ VO Dictionary Plugin 시작됨 (stderr)");
        
        // 로그에도 기록
        getLog().log(new Status(IStatus.INFO, PLUGIN_ID, "VO Dictionary Plugin started successfully"));
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        System.out.println("🛑 VO Dictionary Plugin 종료 중...");
        plugin = null;
        super.stop(context);
        System.out.println("🛑 VO Dictionary Plugin 종료됨");
    }

    public static Activator getDefault() { return plugin; }
}
