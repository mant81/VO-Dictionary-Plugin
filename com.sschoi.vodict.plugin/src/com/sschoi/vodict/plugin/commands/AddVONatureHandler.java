package com.sschoi.vodict.plugin.commands;

import org.eclipse.core.commands.*;
import org.eclipse.core.resources.*;
import org.eclipse.jface.viewers.*;
import org.eclipse.ui.handlers.HandlerUtil;

public class AddVONatureHandler extends AbstractHandler {
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        ISelection selection = HandlerUtil.getCurrentSelection(event);
        if (selection instanceof IStructuredSelection structured) {
            Object first = structured.getFirstElement();
            if (first instanceof IProject project) {
                try {
                    IProjectDescription desc = project.getDescription();
                    String[] prev = desc.getNatureIds();
                    for (String id : prev)
                        if (id.equals("com.sschoi.vodict.plugin.vonature"))
                            return null; // 이미 있음

                    String[] newNatures = new String[prev.length + 1];
                    System.arraycopy(prev, 0, newNatures, 0, prev.length);
                    newNatures[prev.length] = "com.sschoi.vodict.plugin.vonature";
                    desc.setNatureIds(newNatures);
                    project.setDescription(desc, null);

                    System.out.println("[VO-DICT] VO Nature added to: " + project.getName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}
