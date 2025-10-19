package com.sschoi.vodict.plugin.quickfix;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.ui.text.java.*;
import org.eclipse.jdt.core.compiler.IProblem;

public class QuickFixProcessor implements IQuickFixProcessor {

    @Override
    public boolean hasCorrections(ICompilationUnit unit, int problemId) {
        return true;
    }

    @Override
    public IJavaCompletionProposal[] getCorrections(IInvocationContext context, IProblemLocation[] locations) {
        return new IJavaCompletionProposal[] {
            new DictionaryQuickFix("VO Dictionary Quick Fix 제안")
        };
    }
}
