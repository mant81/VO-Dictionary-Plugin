package com.sschoi.vodict.plugin.quickfix;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.ui.text.java.*;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension2;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;

public class QuickFixProcessor implements IQuickFixProcessor {

    @Override
    public boolean hasCorrections(ICompilationUnit unit, int problemId) {
        return true;
    }

    @Override
    public IJavaCompletionProposal[] getCorrections(IInvocationContext context, IProblemLocation[] locations) {
        return new IJavaCompletionProposal[] {
            new IJavaCompletionProposal() {
                @Override
                public void apply(IDocument document) {
                    System.out.println("QuickFix 실행");
                }

                @Override
                public String getDisplayString() {
                    return "VO Dictionary Quick Fix 적용";
                }

                @Override
                public int getRelevance() {
                    return 10;
                }

                @Override
                public Image getImage() {
                    return null;
                }

                @Override
                public String getAdditionalProposalInfo() {
                    return "VO QuickFix 설명";
                }

                @Override
                public IContextInformation getContextInformation() {
                    return null;
                }

                // ✅ Eclipse 2025-09부터 추가된 필수 메서드
                @Override
                public Point getSelection(IDocument document) {
                    return null; // 수정 후 커서 위치 반환 (없으면 null)
                }
            }
        };
    }
}
