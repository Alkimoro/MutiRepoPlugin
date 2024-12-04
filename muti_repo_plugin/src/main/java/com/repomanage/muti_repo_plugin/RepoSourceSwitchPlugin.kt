package com.repomanage.muti_repo_plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.ProjectEvaluationListener
import org.gradle.api.ProjectState
import org.gradle.api.initialization.Settings
import org.gradle.internal.impldep.com.google.gson.Gson
import java.io.File
import java.io.FileReader

class RepoSourceSwitchPlugin : Plugin<Settings> {
    companion object {
        val FILE_NAME = "dependencies_source.json"
    }

    override fun apply(target: Settings) {
        val model = processJsonConfig(target)
        "源码切换插件开启状态：${model.enable}".print()
        if (!model.enable) return
        model.dependencies.forEach {
            if (it.useSources) {
                target.include(":${it.moduleName}")
                target.project(":${it.moduleName}").projectDir = File(it.modulePath)
            }
            "源码切换 Module:${it.moduleName}, Enable:${it.useSources}".print()
        }

        target.gradle.addProjectEvaluationListener(object : ProjectEvaluationListener {
            override fun beforeEvaluate(project: Project) { }

            override fun afterEvaluate(project: Project, state: ProjectState) {
                project.configurations.all {
                    it.resolutionStrategy.dependencySubstitution.apply {
                        model.dependencies.forEach { item ->
                            if (item.useSources) {
                                substitute(module(item.componentName))
                                    .using(project(":${item.moduleName}"))
                            }
                        }
                    }
                }
            }
        })

    }

    private fun processJsonConfig(settings: Settings): ConfigModel {
        var reader: FileReader? = null
        return try {
            val file = File(settings.rootDir.absolutePath + File.separator + FILE_NAME)
            "获取dependencies_source.json配置 path:${file.absolutePath}".print()
            if (!file.exists()) {
                "根目录dependencies_source.json配置不存在，自动跳过源码切换".print()
                ConfigModel()
            } else {
                reader = FileReader(file)
                val json = reader.readText()
                reader.close()
                "成功获取根目录dependencies_source.json配置，配置开始解析".print()
                Gson().fromJson(json, ConfigModel::class.java)
            }
        } catch (e: Exception) {
            e.message?.print()
            ConfigModel()
        } finally {
            reader?.close()
        }
    }

    fun String.print() {
        println(this)
    }

}