package com.sschoi.vodict.plugin.quickfix;

import org.eclipse.jface.text.*;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposal;

public class DictionaryQuickFix implements IJavaCompletionProposal {

    private final String label;

    public DictionaryQuickFix(String label) {
        this.label = label;
    }

    @Override
    public void apply(IDocument document) {
        System.out.println("QuickFix 실행됨: " + label);
    }

    @Override
    public String getDisplayString() {
        return label;
    }

    @Override
    public int getRelevance() {
        return 10;
    }

    @Override
    public IContextInformation getContextInformation() {
        return null;
    }

    @Override
    public Image getImage() {
        return null;
    }

    @Override
    public String getAdditionalProposalInfo() {
        return "VO Dictionary Quick Fix 설명";
    }

    @Override
    public Point getSelection(IDocument document) {
        return null;
    }
}
