package com.sschoi.vodict.plugin.builder;

import java.util.Map;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;


public class VOBuilder extends IncrementalProjectBuilder {
    public static final String BUILDER_ID = "com.sschoi.vodict.plugin.vobuilder";

    @Override
    protected IProject[] build(int kind, Map<String, String> args, IProgressMonitor monitor) throws CoreException {
        VOValidator validator = new VOValidator();

        IResourceDelta delta = getDelta(getProject());
        if (delta == null) {
            getProject().accept(resource -> {
                validator.validate(resource);
                return true;
            });
        } else {
            delta.accept(delta1 -> {
                validator.validate(delta1.getResource());
                return true;
            });
        }
        return null;
    }

}
