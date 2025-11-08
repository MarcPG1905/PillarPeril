package com.marcpg.pillarperil;

import com.marcpg.libpg.init.PaperLoaderPG;

public class PPLoader extends PaperLoaderPG {
    @Override
    public String kotlinVersion() {
        return "2.2.0";
    }

    @Override
    public void load() {
        addRepository("https://repo.xenondevs.xyz/releases/", "xenondevs");

        addDependency("kotlin.reflect.full.KCallables", "org.jetbrains.kotlin:kotlin-reflect:" + kotlinVersion());
    }
}
