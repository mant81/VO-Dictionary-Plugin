package com.sschoi.vodict.plugin.builder;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

import com.sschoi.vodict.plugin.marker.MarkerUtil;
import com.sschoi.vodict.plugin.util.DictionaryService;

public class VOValidator {

    // Java 필드명 패턴: 한 줄에 여러 필드도 처리 (예: private String name, age;)
    private static final Pattern FIELD_PATTERN = Pattern.compile("\\b(?:private|public|protected)?\\s+(?:static\\s+)?(?:final\\s+)?\\w+\\s+([a-zA-Z_][a-zA-Z0-9_]*)\\s*[;=,]");
    
    // 추가 패턴: 쉼표로 구분된 필드들 (예: name, age, address)
    private static final Pattern MULTIPLE_FIELDS_PATTERN = Pattern.compile("\\b(?:private|public|protected)?\\s+(?:static\\s+)?(?:final\\s+)?\\w+\\s+([a-zA-Z_][a-zA-Z0-9_]*(?:\\s*,\\s*[a-zA-Z_][a-zA-Z0-9_]*)*)\\s*;");

    public void validate(IResource resource) throws CoreException {
        System.out.println("🔍 VOValidator.validate() 호출됨 - 파일: " + resource.getName());
        
        if (!(resource instanceof IFile) || !resource.getName().endsWith(".java")) {
            System.out.println("⏭️ Java 파일이 아니므로 건너뜀: " + resource.getName());
            return;
        }

        IFile file = (IFile) resource;
        System.out.println("📝 Java 파일 검사 시작: " + file.getName());
        
        // 안전한 마커 제거 (Resource tree locked 오류 방지)
        safeDeleteMarkers(file);
        
        // VO Dictionary 마커만 추가
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getContents(), StandardCharsets.UTF_8))) {

            String line;
            int lineNum = 1;
            while ((line = reader.readLine()) != null) {
                // 주석이나 빈 줄은 건너뛰기
                if (line.trim().isEmpty() || line.trim().startsWith("//") || line.trim().startsWith("/*")) {
                    lineNum++;
                    continue;
                }

                // Java 필드명 추출 (한 줄에 여러 필드도 처리)
                extractAndValidateFields(file, line, lineNum);
                lineNum++;
            }
        } catch (Exception e) {
            System.err.println("❌ 파일 읽기 오류: " + e.getMessage());
            e.printStackTrace();
        }
        
        // 마커 생성 후 즉시 다시 한번 Eclipse 기본 경고 제거 (안전하게)
        safeDeleteMarkers(file);
        
        // 지연 실행으로 Eclipse 기본 경고 재제거 (더 강력하게)
        scheduleDelayedMarkerCleanup(file);
        
        System.out.println("✅ VOValidator.validate() 완료");
    }
    
    private void extractAndValidateFields(IFile file, String line, int lineNum) throws CoreException {
        // 1. 기본 패턴으로 필드 검사
        Matcher matcher = FIELD_PATTERN.matcher(line);
        while (matcher.find()) {
            String fieldName = matcher.group(1);
            if (fieldName != null && !fieldName.isEmpty()) {
                System.out.println("🔍 필드명 발견: " + fieldName + " (line " + lineNum + ")");
                validateFieldName(file, fieldName, lineNum);
            }
        }
        
        // 2. 한 줄에 여러 필드가 있는 경우 처리 (예: private String name, age;)
        Matcher multiMatcher = MULTIPLE_FIELDS_PATTERN.matcher(line);
        while (multiMatcher.find()) {
            String fieldsGroup = multiMatcher.group(1);
            if (fieldsGroup != null && !fieldsGroup.isEmpty()) {
                // 쉼표로 구분된 필드들을 각각 처리
                String[] fields = fieldsGroup.split("\\s*,\\s*");
                for (String field : fields) {
                    field = field.trim();
                    if (!field.isEmpty()) {
                        System.out.println("🔍 멀티 필드명 발견: " + field + " (line " + lineNum + ")");
                        validateFieldName(file, field, lineNum);
                    }
                }
            }
        }
    }
    
    private void safeDeleteMarkers(IFile file) {
        try {
            // Eclipse 기본 Java 컴파일러 경고만 제거 (VO Dictionary 마커는 유지)
            file.deleteMarkers("org.eclipse.jdt.core.problem", true, IResource.DEPTH_ZERO);
            System.out.println("🗑️ Eclipse 기본 Java 컴파일러 경고 제거 완료");
        } catch (CoreException e) {
            System.err.println("⚠️ 마커 제거 중 오류 (Resource tree locked): " + e.getMessage());
            // Resource tree locked 오류는 무시하고 계속 진행
        }
    }
    
    private void scheduleDelayedMarkerCleanup(IFile file) {
        new Thread(() -> {
            try {
                // 더 강력한 지속적 마커 제거
                for (int i = 0; i < 10; i++) {
                    Thread.sleep(200 + (i * 100)); // 점진적으로 대기 시간 증가
                    try {
                        // Eclipse 기본 Java 컴파일러 경고만 제거 (VO Dictionary 마커는 유지)
                        file.deleteMarkers("org.eclipse.jdt.core.problem", true, IResource.DEPTH_ZERO);
                        System.out.println("🔄 지속적 Eclipse 기본 경고 제거 " + (i + 1) + "/10 완료");
                    } catch (CoreException e) {
                        System.err.println("⚠️ 지속적 마커 제거 중 오류: " + e.getMessage());
                    }
                }
                System.out.println("🔄 Eclipse 기본 경고 완전 차단 완료");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void validateFieldName(IFile file, String fieldName, int lineNum) throws CoreException {
        if (DictionaryService.exists(fieldName)) {
            // 표준 단어 → ✅ INFO 마커
            Optional<String> meaning = DictionaryService.getKoreanMeaning(fieldName);
            String message = "✅ VO 표준 단어: '" + fieldName + "' (" + meaning.orElse("?") + ")";
            MarkerUtil.addMarker(file, message, lineNum, IMarker.SEVERITY_INFO);
            System.out.println("✅ VO Dictionary 마커 추가됨: " + message + " (line " + lineNum + ")");
        } else {
            // 비표준 단어 → ⚠️ ERROR 마커 (더 강력한 우선순위)
            String message = "⚠️ VO 비표준 단어: '" + fieldName + "' - 사전에 등록되지 않음";
            MarkerUtil.addMarker(file, message, lineNum, IMarker.SEVERITY_ERROR);
            System.out.println("⚠️ VO Dictionary 마커 추가됨: " + message + " (line " + lineNum + ")");
        }
        
        // 마커 생성 후 즉시 Eclipse 기본 경고 제거
        safeDeleteMarkers(file);
        
        // Problems 뷰 업데이트를 위한 강제 새로고침
        try {
            file.refreshLocal(IResource.DEPTH_ZERO, null);
        } catch (CoreException e) {
            System.err.println("⚠️ 파일 새로고침 중 오류: " + e.getMessage());
        }
    }
}
