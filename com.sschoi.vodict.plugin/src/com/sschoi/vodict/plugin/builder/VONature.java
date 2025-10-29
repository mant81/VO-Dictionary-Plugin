package com.sschoi.vodict.plugin.builder;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.CoreException;

public class VONature implements IProjectNature {

    private IProject project;

    @Override
    public void configure() throws CoreException {
        IProjectDescription desc = project.getDescription();
        ICommand[] commands = desc.getBuildSpec();

        for (ICommand command : commands) {
            if (command.getBuilderName().equals(VOBuilder.BUILDER_ID))
                return;
        }

        ICommand newCommand = desc.newCommand();
        newCommand.setBuilderName(VOBuilder.BUILDER_ID);
        
        // VO Dictionary 빌더를 마지막에 실행되도록 설정 (Java 빌더 이후)
        ICommand[] newCommands = new ICommand[commands.length + 1];
        System.arraycopy(commands, 0, newCommands, 0, commands.length);
        newCommands[commands.length] = newCommand;
        
        desc.setBuildSpec(newCommands);
        project.setDescription(desc, null);
        
        System.out.println("🔧 VO Dictionary 빌더가 마지막 순서로 설정됨");
    }

    @Override
    public void deconfigure() throws CoreException {
        IProjectDescription desc = project.getDescription();
        ICommand[] commands = desc.getBuildSpec();

        for (int i = 0; i < commands.length; i++) {
            if (commands[i].getBuilderName().equals(VOBuilder.BUILDER_ID)) {
                ICommand[] newCommands = new ICommand[commands.length - 1];
                System.arraycopy(commands, 0, newCommands, 0, i);
                System.arraycopy(commands, i + 1, newCommands, i, commands.length - i - 1);
                desc.setBuildSpec(newCommands);
                project.setDescription(desc, null);
                return;
            }
        }
    }

    @Override
    public IProject getProject() {
        return project;
    }

    @Override
    public void setProject(IProject project) {
        this.project = project;
    }
}
