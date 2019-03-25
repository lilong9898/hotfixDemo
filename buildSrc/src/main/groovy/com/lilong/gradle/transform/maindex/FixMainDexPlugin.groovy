package com.lilong.gradle.transform.maindex

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task

class FixMainDexPlugin implements Plugin<Project>{

    private CollectJarClassInfoTransform collectJarClassInfoTransform;
    private FixMainDexTask fixMainDexTask;

    /** 键：flavor + buildType，如"OppoDebug", 值：这个flavor + buildType对应的所有类的列表（包括build/intermediates/classes里的和jar包里的）*/
    private HashMap<String, List<String>> allClassNamesMap = new HashMap<>();

    @Override
    void apply(Project project) {

        def fixMainDexExtension = project.extensions.create "fixMainDexExtension", FixMainDexExtension

        collectJarClassInfoTransform = new CollectJarClassInfoTransform(project, allClassNamesMap)
        project.android.registerTransform(collectJarClassInfoTransform)

        project.afterEvaluate {

            fixMainDexTask = project.tasks.create("fixMainDexTask", FixMainDexTask)
            fixMainDexTask.setAllClassNamesMap(allClassNamesMap)
            fixMainDexTask.setMode(fixMainDexExtension.mode)
            fixMainDexTask.setMainDexExcludedPackages(fixMainDexExtension.maindexExcludedPackages)
            // 将fixMainDexTask安排在transformClassesWithMultidexlist和transformClassesWithDex这两个内置的task之间
            project.tasks.all { Task task ->
                if(task.getName().contains("transformClassesWithDex")){
                    task.dependsOn fixMainDexTask
                }else if(task.getName().contains("transformClassesWithMultidexlist")){
                    fixMainDexTask.mustRunAfter task
                }
            }
        }
    }

}