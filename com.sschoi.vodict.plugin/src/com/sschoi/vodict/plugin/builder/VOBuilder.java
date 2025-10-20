package com.sschoi.vodict.plugin.builder;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

public class VOBuilder extends IncrementalProjectBuilder {

    public static final String BUILDER_ID = "com.sschoi.vodict.plugin.vobuilder";

    @Override
    protected IProject[] build(int kind, java.util.Map<String, String> args, IProgressMonitor monitor)
            throws CoreException {

        getProject().deleteMarkers("com.sschoi.vodict.plugin.marker", true, IResource.DEPTH_INFINITE);

        // Example marker
        for (IResource res : getProject().members()) {
            if (res instanceof IFile file && file.getName().endsWith(".java")) {
                IMarker marker = file.createMarker("com.sschoi.vodict.plugin.marker");
                marker.setAttribute(IMarker.MESSAGE, "VO Dictionary 검사 결과: 샘플 경고");
                marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_WARNING);
            }
        }
        return null;
    }
}
