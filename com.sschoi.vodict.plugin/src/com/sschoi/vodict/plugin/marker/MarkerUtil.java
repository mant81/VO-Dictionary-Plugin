package com.sschoi.vodict.plugin.marker;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.CoreException;

public class MarkerUtil {

    private static final String MARKER_ID = "com.sschoi.vodict.plugin.marker";

    public static void addMarker(IFile file, String message, int lineNumber, int severity) throws CoreException {
        IMarker marker = file.createMarker(MARKER_ID);
        marker.setAttribute(IMarker.MESSAGE, message);
        marker.setAttribute(IMarker.SEVERITY, severity);
        marker.setAttribute(IMarker.LINE_NUMBER, lineNumber);
    }

    public static void clearMarkers(IFile file) throws CoreException {
        file.deleteMarkers(MARKER_ID, false, IResource.DEPTH_ZERO);
    }
}
