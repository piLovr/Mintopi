package com.github.pilovr.mintopi.subscriber;

public class CommandTreeNode {
    private String name;
    private Command command;
    private Runnable runMethod;
    private CommandTreeNode parent;
    private CommandTreeNode[] children;
}
