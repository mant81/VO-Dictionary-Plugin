package com.sschoi.vodict.plugin;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import com.sschoi.vodict.plugin.validator.VOValidator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class Activator extends AbstractUIPlugin implements IResourceChangeListener {
    public static final String PLUGIN_ID = "com.sschoi.vodict.plugin";
    private static Activator plugin;
    private final VOValidator validator = new VOValidator();
    private final Set<String> processingFiles = ConcurrentHashMap.newKeySet();

    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
        ResourcesPlugin.getWorkspace().addResourceChangeListener(this, IResourceChangeEvent.POST_CHANGE);
        System.out.println("✅ VO Dictionary Plugin 시작됨");
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
        plugin = null;
        super.stop(context);
    }

    @Override
    public void resourceChanged(IResourceChangeEvent event) {
        try {
            event.getDelta().accept(delta -> {
                IResource resource = delta.getResource();
                if (resource instanceof IFile && resource.getName().endsWith(".java")) {
                    String path = resource.getFullPath().toString();
                    if (!processingFiles.contains(path)) {
                        processingFiles.add(path);
                        Job job = new Job("VO Validator Job") {
                            @Override
                            protected IStatus run(IProgressMonitor monitor) {
                                try {
                                    validator.validate((IFile) resource, monitor);
                                } catch (CoreException e) {
                                    e.printStackTrace();
                                } finally {
                                    processingFiles.remove(path);
                                }
                                return Status.OK_STATUS;
                            }
                        };
                        job.setPriority(Job.INTERACTIVE);
                        job.schedule(100);
                    }
                }
                return true;
            });
        } catch (CoreException e) {
            e.printStackTrace();
        }
    }

    public static Activator getDefault() { return plugin; }
}
