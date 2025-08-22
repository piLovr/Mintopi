package com.github.pilovr.mintopi.starter.domain.common;

public abstract class Command {
    public abstract String getName();
    public abstract void run();
}
