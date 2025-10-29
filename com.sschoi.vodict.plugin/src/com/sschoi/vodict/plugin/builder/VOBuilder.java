package com.sschoi.vodict.plugin.builder;

import java.util.Map;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;


public class VOBuilder extends IncrementalProjectBuilder {
    public static final String BUILDER_ID = "com.sschoi.vodict.plugin.vobuilder";

    @Override
    protected IProject[] build(int kind, Map<String, String> args, IProgressMonitor monitor) throws CoreException {
        System.out.println("🔨 VOBuilder 실행됨 - kind: " + kind + " (FULL_BUILD=" + FULL_BUILD + ", INCREMENTAL_BUILD=" + INCREMENTAL_BUILD + ", AUTO_BUILD=" + AUTO_BUILD + ")");
        
        VOValidator validator = new VOValidator();

        IResourceDelta delta = getDelta(getProject());
        if (delta == null) {
            // 전체 빌드: 모든 파일 검사
            System.out.println("📁 전체 빌드 - 모든 Java 파일 검사");
            getProject().accept(resource -> {
                validator.validate(resource);
                return true;
            });
        } else {
            // 증분 빌드: 변경된 파일만 검사
            System.out.println("📝 증분 빌드 - 변경된 파일만 검사");
            delta.accept(delta1 -> {
                IResource resource = delta1.getResource();
                if (resource instanceof IFile && resource.getName().endsWith(".java")) {
                    System.out.println("🔍 검사할 파일: " + resource.getName());
                    validator.validate(resource);
                }
                return true;
            });
        }
        
        // 빌드 완료 후 지속적으로 Eclipse 기본 경고 모니터링 및 제거
        startMarkerCleanupTask();
        
        System.out.println("✅ VOBuilder 빌드 완료 - Eclipse 기본 경고 지속 모니터링 시작");
        return null;
    }

    private void startMarkerCleanupTask() {
        new Thread(() -> {
            try {
                // 더 강력한 지속적 제거 - 1초마다 20번 실행
                for (int i = 0; i < 20; i++) {
                    Thread.sleep(1000);
                    getProject().accept(resource -> {
                        if (resource instanceof IFile && resource.getName().endsWith(".java")) {
                            try {
                                // 모든 PROBLEM 마커 제거
                                resource.deleteMarkers(IMarker.PROBLEM, true, IResource.DEPTH_ZERO);
                                // 특정 Eclipse 컴파일러 경고도 제거
                                resource.deleteMarkers("org.eclipse.jdt.core.problem", true, IResource.DEPTH_ZERO);
                            } catch (CoreException e) {
                                e.printStackTrace();
                            }
                        }
                        return true;
                    });
                    System.out.println("🔄 Eclipse 기본 경고 지속 제거 중... (" + (i + 1) + "/20)");
                }
                System.out.println("✅ Eclipse 기본 경고 지속 제거 완료");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    protected void startupOnInitialize() {
        System.out.println("🚀 VOBuilder 초기화됨");
    }
}
