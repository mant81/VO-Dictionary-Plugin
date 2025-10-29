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
        
        // 극대화된 우선순위 설정 - Eclipse 기본 컴파일러 경고를 완전히 압도
        marker.setAttribute(IMarker.PRIORITY, IMarker.PRIORITY_HIGH);
        
        // VO Dictionary 전용 카테고리로 명확히 구분
        marker.setAttribute("org.eclipse.jdt.core.compiler.problem.category", "VO Dictionary");
        
        // 강제로 Problem 뷰에서 최상위에 오도록 설정
        marker.setAttribute("org.eclipse.jdt.core.compiler.problem.isError", severity == IMarker.SEVERITY_ERROR);
        marker.setAttribute("org.eclipse.jdt.core.compiler.problem.id", 99999); // 극도로 높은 ID로 최상위 우선순위
        
        // 추가 우선순위 속성들
        marker.setAttribute("org.eclipse.jdt.core.compiler.problem.sourceStart", 0);
        marker.setAttribute("org.eclipse.jdt.core.compiler.problem.sourceEnd", 0);
        
        // VO Dictionary 플러그인에서 생성된 마커임을 명시
        marker.setAttribute("com.sschoi.vodict.plugin.source", "VO Dictionary Validator");
        
        // Problems 뷰에서 제대로 표시되도록 추가 속성 설정
        marker.setAttribute("org.eclipse.jdt.core.compiler.problem.argument", message);
        marker.setAttribute("org.eclipse.jdt.core.compiler.problem.sourceId", "VO_DICTIONARY");
        
        // 추가 우선순위 강화 속성들
        marker.setAttribute("org.eclipse.jdt.core.compiler.problem.taskPriority", "HIGH");
        marker.setAttribute("org.eclipse.jdt.core.compiler.problem.taskSeverity", severity == IMarker.SEVERITY_ERROR ? "ERROR" : "INFO");
        
        // Problems 뷰 표시를 위한 추가 속성들
        marker.setAttribute(IMarker.CHAR_START, 0);
        marker.setAttribute(IMarker.CHAR_END, 0);
        marker.setAttribute(IMarker.USER_EDITABLE, false);
        
        System.out.println("🎯 VO Dictionary 마커 생성: " + message + " (극대화된 우선순위: HIGH, ID: 99999)");
    }

    /**
     * VO Dictionary 마커 제거
     */
    public static void clearMarkers(IFile file) throws CoreException {
        file.deleteMarkers("com.sschoi.vodict.plugin.voProblem", false, IResource.DEPTH_ZERO);
    }
}
