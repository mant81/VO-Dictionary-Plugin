package com.sschoi.vodict.plugin.validator;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.CoreException;

import com.sschoi.vodict.plugin.marker.MarkerUtil;
import com.sschoi.vodict.plugin.util.DictionaryService;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DictionaryValidator implements IResourceVisitor, IResourceDeltaVisitor {

    // Java 필드명 패턴: 타입 필드명; 또는 타입 필드명 = 값;
    private static final Pattern FIELD_PATTERN = Pattern.compile("\\b(?:private|public|protected)?\\s+(?:static\\s+)?(?:final\\s+)?\\w+\\s+([a-zA-Z_][a-zA-Z0-9_]*)\\s*[;=]");

    @Override
    public boolean visit(IResource resource) throws CoreException {
        validate(resource);
        return true;
    }

    @Override
    public boolean visit(IResourceDelta delta) throws CoreException {
        validate(delta.getResource());
        return true;
    }

    private void validate(IResource resource) throws CoreException {
        if (!(resource instanceof IFile) || !resource.getName().endsWith(".java"))
            return;

        IFile file = (IFile) resource;
        MarkerUtil.clearMarkers(file);

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

                // Java 필드명 추출
                Matcher matcher = FIELD_PATTERN.matcher(line);
                while (matcher.find()) {
                    String fieldName = matcher.group(1);
                    if (fieldName != null && !fieldName.isEmpty()) {
                        validateFieldName(file, fieldName, lineNum);
                    }
                }
                lineNum++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void validateFieldName(IFile file, String fieldName, int lineNum) throws CoreException {
        if (DictionaryService.exists(fieldName)) {
            // 표준 단어 → ✅ INFO 마커
            Optional<String> meaning = DictionaryService.getKoreanMeaning(fieldName);
            String message = "✅ VO 표준 단어: '" + fieldName + "' (" + meaning.orElse("?") + ")";
            MarkerUtil.addMarker(file, message, lineNum, IMarker.SEVERITY_INFO);
            System.out.println("✅ VO Dictionary 마커 추가됨: " + message + " (line " + lineNum + ")");
        } else {
            // 비표준 단어 → ⚠️ ERROR 마커
            String message = "⚠️ VO 비표준 단어: '" + fieldName + "' - 사전에 등록되지 않음";
            MarkerUtil.addMarker(file, message, lineNum, IMarker.SEVERITY_ERROR);
            System.out.println("⚠️ VO Dictionary 마커 추가됨: " + message + " (line " + lineNum + ")");
        }
    }
}
