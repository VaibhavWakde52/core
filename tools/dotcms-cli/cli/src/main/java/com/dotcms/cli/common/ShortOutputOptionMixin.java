package com.dotcms.cli.common;

import picocli.CommandLine;

public class ShortOutputOptionMixin {

    @CommandLine.Option(names = {"-sh","--short"},  description = "Pulled Content is shown in shorten format.", hidden = true)
    boolean shortOutput;

    public boolean isShortOutput() {
        return shortOutput;
    }

}
