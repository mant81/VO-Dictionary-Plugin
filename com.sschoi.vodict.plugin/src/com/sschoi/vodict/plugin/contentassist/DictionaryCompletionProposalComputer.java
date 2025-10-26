package com.sschoi.vodict.plugin.contentassist;

import java.util.*;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.ui.text.java.*;
import org.eclipse.jface.text.*;
import org.eclipse.jface.text.contentassist.*;
import com.sschoi.vodict.plugin.util.DictionaryService;

public class DictionaryCompletionProposalComputer implements IJavaCompletionProposalComputer {

	public DictionaryCompletionProposalComputer() {
        System.out.println("[VO-DICT] 생성자 호출됨 ✅");
    }
	
    @Override
    public List<ICompletionProposal> computeCompletionProposals(ContentAssistInvocationContext context, IProgressMonitor monitor) {
    	System.out.println("[VO-DICT] computeCompletionProposals 실행됨 ✅");
    	
    	List<ICompletionProposal> proposals = new ArrayList<>();
        try {
            IDocument doc = context.getDocument();
            int offset = context.getInvocationOffset();

            // 현재 입력 중인 단어 추출
            int start = offset - 1;
            while (start >= 0 && Character.isJavaIdentifierPart(doc.getChar(start))) start--;
            start++;
            String prefix = doc.get(start, offset - start);

            if (prefix.length() < 2) return proposals; // 2글자 이상일 때만 제안

            // dictionary.json 에서 prefix 로 검색
            List<Map<String,String>> list = DictionaryService.searchPrefix(prefix);

            for (Map<String,String> entry : list) {
                String en = entry.get("en");
                String ko = entry.get("ko");

                proposals.add(new CompletionProposal(
                        en, // 교체할 텍스트
                        start,
                        offset - start,
                        en.length(),
                        null,
                        en + "  (" + ko + ")", // 표시 이름
                        null,
                        "VO 표준 단어: " + ko
                ));
            }
            System.out.println("Content Assist 호출됨! offset=" + context.getInvocationOffset());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return proposals;
    }

    @Override
    public List<IContextInformation> computeContextInformation(ContentAssistInvocationContext context, IProgressMonitor monitor) {
        return Collections.emptyList();
    }

    @Override
    public String getErrorMessage() { return null; }

    @Override
    public void sessionStarted() {}

    @Override
    public void sessionEnded() {}
}
