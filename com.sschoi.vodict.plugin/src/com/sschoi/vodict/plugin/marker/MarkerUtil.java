package com.sschoi.vodict.plugin.marker;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.CoreException;

public class MarkerUtil {

    /**
     * Java Editor에서 VO Dictionary 마커를 최상단에 표시
     */
    public static void addMarker(IFile file, String message, int lineNumber, int severity) throws CoreException {
        IMarker marker = file.createMarker("com.sschoi.vodict.plugin.voProblem"); // VO Dictionary 전용 마커
        marker.setAttribute(IMarker.MESSAGE, message);
        marker.setAttribute(IMarker.SEVERITY, severity); // 전달받은 severity 사용
        marker.setAttribute(IMarker.LINE_NUMBER, lineNumber);
        
        // 우선순위 최상위
        marker.setAttribute(IMarker.PRIORITY, IMarker.PRIORITY_HIGH);
        
        // Optional: 마커 카테고리
        marker.setAttribute("org.eclipse.jdt.core.compiler.problem.category", "VO Dictionary");
        
        // 강제로 Problem 뷰에서 상위에 오도록 ID 지정
        marker.setAttribute("org.eclipse.jdt.core.compiler.problem.isError", severity == IMarker.SEVERITY_ERROR);
        marker.setAttribute("org.eclipse.jdt.core.compiler.problem.id", 1000); // VO Dictionary 전용 ID
    }

    /**
     * VO Dictionary 마커 제거
     */
    public static void clearMarkers(IFile file) throws CoreException {
        file.deleteMarkers("com.sschoi.vodict.plugin.voProblem", false, IResource.DEPTH_ZERO);
    }
}
