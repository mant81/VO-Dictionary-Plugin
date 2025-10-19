package com.sschoi.vodict.plugin.builder;

import java.util.Map;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import com.sschoi.vodict.plugin.validator.DictionaryValidator;

public class VOBuilder extends IncrementalProjectBuilder {
    public static final String BUILDER_ID = "com.sschoi.vodict.plugin.vobuilder";

    @Override
    protected IProject[] build(int kind, Map<String, String> args, IProgressMonitor monitor) throws CoreException {
        IResourceDelta delta = getDelta(getProject());
        if (delta == null) {
            getProject().accept(new DictionaryValidator());
        } else {
            delta.accept(new DictionaryValidator());
        }
        return null;
    }
}
