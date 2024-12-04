package com.repomanage.muti_repo_plugin

data class ConfigModel(
    val enable: Boolean = false,
    val dependencies: List<DependencyItem> = listOf(),
)

data class DependencyItem(
    val useSources: Boolean,
    val moduleName: String,
    val modulePath: String,
    val componentName: String,
)
