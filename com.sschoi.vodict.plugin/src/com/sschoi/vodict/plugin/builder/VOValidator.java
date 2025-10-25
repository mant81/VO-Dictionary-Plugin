package com.sschoi.vodict.plugin.builder;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

import com.sschoi.vodict.plugin.marker.MarkerUtil;
import com.sschoi.vodict.plugin.util.DictionaryService;

public class VOValidator {

    public void validate(IResource resource) throws CoreException {
        if (!(resource instanceof IFile) || !resource.getName().endsWith(".java"))
            return;

        IFile file = (IFile) resource;
        MarkerUtil.clearMarkers(file);

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getContents(), StandardCharsets.UTF_8))) {

            String line;
            int lineNum = 1;
            while ((line = reader.readLine()) != null) {
                // 단어 추출 (단순 ; 기준, 예: String acc;)
                String[] tokens = line.split("\\W+"); 
                for (String token : tokens) {
                    if (token.isBlank()) continue;
                    // 사전에 없는 단어만 WARNING
                    if (!DictionaryService.exists(token)) {
                        MarkerUtil.addMarker(file,
                                "VO 사전에 없는 필드명: " + token,
                                lineNum,
                                IMarker.SEVERITY_WARNING);
                        System.out.println("⚠ 마커 추가됨: VO 사전에 없는 필드명: " + token + " (line " + lineNum + ")");
                    }
                }
                lineNum++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
