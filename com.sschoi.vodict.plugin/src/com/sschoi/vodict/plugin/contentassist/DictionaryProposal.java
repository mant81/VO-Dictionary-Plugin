package com.sschoi.vodict.plugin.contentassist;

import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.graphics.Point;

public class DictionaryProposal implements ICompletionProposal {

    private final String text;
    private final String ko;
    private final int start;
    private final int end;

    public DictionaryProposal(String text, String ko, int start, int end) {
        this.text = text;
        this.ko = ko;
        this.start = start;
        this.end = end;
    }

    @Override
    public void apply(IDocument document) {
        try {
            document.replace(start, end - start, text);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getAdditionalProposalInfo() {
        return "한글 뜻: " + ko;
    }

    @Override
    public String getDisplayString() {
        return text + " → " + ko;
    }

    @Override
    public org.eclipse.swt.graphics.Image getImage() {
        return null;
    }

    @Override
    public IContextInformation getContextInformation() {
        return null;
    }

    // ✅ Eclipse 2025-09에서 필수
    @Override
    public Point getSelection(IDocument document) {
        // 제안 적용 후 커서를 텍스트 끝으로 이동
        return new Point(start + text.length(), 0);
    }
}
