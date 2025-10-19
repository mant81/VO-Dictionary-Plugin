package com.sschoi.vodict.plugin.validator;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.CoreException;

import com.sschoi.vodict.plugin.marker.MarkerUtil;
import com.sschoi.vodict.plugin.util.DictionaryService;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class DictionaryValidator implements IResourceVisitor, IResourceDeltaVisitor {

    private final DictionaryService dictionaryService = new DictionaryService();

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
                for (String word : dictionaryService.getAllWords()) {
                    if (line.contains(word)) {
                        Optional<String> meaning = dictionaryService.getKoreanMeaning(word);
                        String message = "'" + word + "' (" + meaning.orElse("?") + ") 은 표준 단어입니다.";
                        MarkerUtil.addMarker(file, message, lineNum, IMarker.SEVERITY_INFO);
                    }
                }
                lineNum++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
