package com.sschoi.vodict.plugin;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.sschoi.vodict.plugin.builder.VOValidator;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class Activator extends AbstractUIPlugin implements IResourceChangeListener {
    public static final String PLUGIN_ID = "com.sschoi.vodict.plugin";
    private static Activator plugin;
    private final VOValidator validator = new VOValidator();
    
    // 중복 실행 방지를 위한 처리 중인 파일 추적
    private final Set<String> processingFiles = ConcurrentHashMap.newKeySet();

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
        
        // Resource Change Listener 등록 (Activator 자체가 Listener)
        ResourcesPlugin.getWorkspace().addResourceChangeListener(
            this, 
            IResourceChangeEvent.POST_CHANGE
        );
        
        // 전역 마커 모니터링 시작 (Eclipse 기본 경고 즉시 제거)
        startGlobalMarkerMonitoring();
        
        System.out.println("✅ VO Dictionary Plugin 시작됨 (Activator.start) - Resource Change Listener 등록됨");
        System.err.println("✅ VO Dictionary Plugin 시작됨 (stderr)");
        
        // 로그에도 기록
        getLog().log(new Status(IStatus.INFO, PLUGIN_ID, "VO Dictionary Plugin started successfully"));
    }
    
    private void startGlobalMarkerMonitoring() {
        new Thread(() -> {
            try {
                while (true) {
                    Thread.sleep(1000); // 1초마다 실행
                    
                    // 모든 프로젝트의 모든 Java 파일에서 Eclipse 기본 경고만 제거
                    ResourcesPlugin.getWorkspace().getRoot().accept(resource -> {
                        if (resource instanceof IFile && resource.getName().endsWith(".java")) {
                            try {
                                // Eclipse 기본 Java 컴파일러 경고만 제거 (VO Dictionary 마커는 유지)
                                resource.deleteMarkers("org.eclipse.jdt.core.problem", true, IResource.DEPTH_ZERO);
                            } catch (CoreException e) {
                                // 무시
                            }
                        }
                        return true;
                    });
                }
            } catch (InterruptedException e) {
                // 정상 종료
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
        
        System.out.println("🔍 전역 Eclipse 기본 경고 모니터링 시작됨");
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        System.out.println("🛑 VO Dictionary Plugin 종료 중...");
        
        // Resource Change Listener 제거
        ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
        
        plugin = null;
        super.stop(context);
        System.out.println("🛑 VO Dictionary Plugin 종료됨");
    }

    @Override
    public void resourceChanged(IResourceChangeEvent event) {
        System.out.println("🔄 VO Resource Change Listener 실행됨");
        
        // Resource Change Listener에서는 마커를 직접 처리하지 않고, 
        // 단순히 파일 변경 정보만 수집하여 별도 Job으로 전달
        try {
            event.getDelta().accept(delta -> {
                IResource resource = delta.getResource();
                if (resource instanceof IFile && resource.getName().endsWith(".java")) {
                    String filePath = resource.getFullPath().toString();
                    
                    // 중복 실행 방지: 이미 처리 중인 파일은 건너뛰기
                    if (processingFiles.contains(filePath)) {
                        System.out.println("⏭️ 이미 처리 중인 파일 건너뛰기: " + resource.getName());
                        return true;
                    }
                    
                    System.out.println("🔍 Java 파일 변경 감지: " + resource.getName());
                    
                    // 완전히 별도의 Job으로 마커 처리 (Resource Change Listener와 완전 분리)
                    scheduleMarkerJob(resource);
                }
                return true;
            });
        } catch (CoreException e) {
            e.printStackTrace();
        }
    }
    
    private void scheduleMarkerJob(IResource resource) {
        String filePath = resource.getFullPath().toString();
        
        // 처리 중인 파일 목록에 추가
        processingFiles.add(filePath);
        
        // Resource Change Listener와 완전히 분리된 Job 생성
        Job markerJob = new Job("VO Dictionary Marker Job") {
            @Override
            protected IStatus run(IProgressMonitor monitor) {
                try {
                    System.out.println("🔧 VO Dictionary Marker Job 시작: " + resource.getName());
                    validator.validate(resource);
                    System.out.println("✅ VO Dictionary Marker Job 완료: " + resource.getName());
                    return Status.OK_STATUS;
                } catch (CoreException e) {
                    System.err.println("❌ VO Dictionary Marker Job 실패: " + e.getMessage());
                    return new Status(IStatus.ERROR, PLUGIN_ID, "VO Dictionary validation failed", e);
                } finally {
                    // Job 완료 후 처리 중인 파일 목록에서 제거
                    processingFiles.remove(filePath);
                }
            }
        };
        
        // Job 우선순위 설정 및 실행
        markerJob.setPriority(Job.INTERACTIVE);
        markerJob.schedule(100); // 100ms 지연 후 실행
    }

    public static Activator getDefault() { return plugin; }
}
