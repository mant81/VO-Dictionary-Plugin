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
            if (command.getBuilderName().equals("com.sschoi.vodict.plugin.vobuilder"))
                return; // 이미 연결됨
        }

        ICommand newCommand = desc.newCommand();
        newCommand.setBuilderName("com.sschoi.vodict.plugin.vobuilder");

        ICommand[] newCommands = new ICommand[commands.length + 1];
        System.arraycopy(commands, 0, newCommands, 0, commands.length);
        newCommands[commands.length] = newCommand;

        desc.setBuildSpec(newCommands);
        project.setDescription(desc, null);
    }

    @Override
    public void deconfigure() throws CoreException {
        IProjectDescription desc = project.getDescription();
        ICommand[] commands = desc.getBuildSpec();

        int index = -1;
        for (int i = 0; i < commands.length; i++) {
            if (commands[i].getBuilderName().equals("com.sschoi.vodict.plugin.vobuilder")) {
                index = i;
                break;
            }
        }
        if (index != -1) {
            ICommand[] newCommands = new ICommand[commands.length - 1];
            System.arraycopy(commands, 0, newCommands, 0, index);
            System.arraycopy(commands, index + 1, newCommands, index, commands.length - index - 1);
            desc.setBuildSpec(newCommands);
            project.setDescription(desc, null);
        }
    }

    @Override
    public IProject getProject() { return project; }

    @Override
    public void setProject(IProject project) { this.project = project; }
}
