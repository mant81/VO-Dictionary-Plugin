package com.sschoi.vodict.plugin.validator;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;
import com.sschoi.vodict.plugin.util.DictionaryService;
import java.io.*;
import java.util.regex.*;

public class VOValidator {

    private static final String MARKER_TYPE = "com.sschoi.vodict.plugin.voProblemMarker";
    private static final Pattern FIELD_PATTERN = Pattern.compile("\\bprivate\\s+\\w+\\s+(\\w+)\\s*;");

    public void validate(IFile file, IProgressMonitor monitor) throws CoreException {
        if (file == null || !file.exists()) return;

        file.deleteMarkers(MARKER_TYPE, true, IResource.DEPTH_ZERO);

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getContents(), file.getCharset()))) {
            String line;
            int lineNumber = 1;

            while ((line = reader.readLine()) != null) {
                Matcher matcher = FIELD_PATTERN.matcher(line);

                while (matcher.find()) {
                    String fieldName = matcher.group(1).trim().toLowerCase();
                    if (!DictionaryService.exists(fieldName)) {
                        addMarker(file, "VO 사전에 없는 단어: " + fieldName, lineNumber, IMarker.SEVERITY_ERROR);
                        System.out.println("❌ [" + fieldName + "] not found in dictionary");
                    } else {
                        System.out.println("✅ [" + fieldName + "] found in dictionary");
                    }
                }
                lineNumber++;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addMarker(IFile file, String message, int lineNumber, int severity) {
        try {
            IMarker marker = file.createMarker(MARKER_TYPE);
            marker.setAttribute(IMarker.MESSAGE, message);
            marker.setAttribute(IMarker.SEVERITY, severity);
            marker.setAttribute(IMarker.LINE_NUMBER, lineNumber);
            marker.setAttribute(IMarker.LOCATION, file.getName() + " : line " + lineNumber);
        } catch (CoreException e) {
            e.printStackTrace();
        }
    }
}
