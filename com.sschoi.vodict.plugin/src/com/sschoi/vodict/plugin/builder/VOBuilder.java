package com.sschoi.vodict.plugin.builder;

import java.util.Map;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;
import com.sschoi.vodict.plugin.validator.VOValidator;

public class VOBuilder extends IncrementalProjectBuilder {

    public static final String BUILDER_ID = "com.sschoi.vodict.plugin.vobuilder";

    @Override
    protected IProject[] build(int kind, Map<String, String> args, IProgressMonitor monitor) throws CoreException {
        IResourceDelta delta = getDelta(getProject());

        if (delta == null || kind == FULL_BUILD) {
            fullBuild(monitor);
        } else {
            incrementalBuild(delta, monitor);
        }
        return null;
    }

    private void fullBuild(IProgressMonitor monitor) throws CoreException {
        getProject().accept(resource -> {
            if (resource instanceof IFile file && "java".equals(file.getFileExtension())) {
                new VOValidator().validate(file, monitor);
            }
            return true;
        });
    }

    private void incrementalBuild(IResourceDelta delta, IProgressMonitor monitor) throws CoreException {
        delta.accept(d -> {
            IResource res = d.getResource();
            if (res instanceof IFile file && "java".equals(file.getFileExtension())) {
                new VOValidator().validate(file, monitor);
            }
            return true;
        });
    }
}
