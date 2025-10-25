package com.sschoi.vodict.plugin.validator;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

public class VOFieldVisitor extends ASTVisitor {

    private final List<String> fieldNames = new ArrayList<>();

    @Override
    public boolean visit(FieldDeclaration node) {
        for (Object fragObj : node.fragments()) {
            if (fragObj instanceof VariableDeclarationFragment frag) {
                String name = frag.getName().getIdentifier();
                fieldNames.add(name);
            }
        }
        return super.visit(node);
    }

    /** 추출된 필드 이름 목록 반환 */
    public List<String> getFieldNames() {
        return fieldNames;
    }
}
