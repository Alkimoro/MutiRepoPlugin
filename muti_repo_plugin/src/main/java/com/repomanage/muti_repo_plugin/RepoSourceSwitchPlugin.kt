package com.repomanage.muti_repo_plugin

import org.gradle.api.Plugin
import org.gradle.api.Project

class RepoSourceSwitchPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        if (target.rootProject == target) {
            println("1111")
        }
        println("22222")
    }
}