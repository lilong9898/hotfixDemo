package com.lilong.gradle.transform.maindex

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.FileUtils
import org.gradle.api.Project

import java.util.function.Consumer
import java.util.jar.JarEntry
import java.util.jar.JarFile

class CollectJarClassInfoTransform extends Transform{

    private Project project;

    /** 键：flavor + buildType，如"OppoDebug", 值：这个flavor + buildType对应的所有类的列表（包括build/intermediates/classes里的和jar包里的）*/
    HashMap<String, List<String>> allClassNamesMap

    CollectJarClassInfoTransform(Project project, HashMap<String, List<String>> allClassNamesMap) {
        this.project = project
        this.allClassNamesMap = allClassNamesMap
    }

    @Override
    String getName() {
        return "CollectJarClassInfoTransform";
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS;
    }

    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT;
    }

    @Override
    boolean isIncremental() {
        return false
    }

    @Override
    void transform(Context context, Collection<TransformInput> inputs, Collection<TransformInput> referencedInputs, TransformOutputProvider outputProvider, boolean isIncremental) throws IOException, TransformException, InterruptedException {

        List<String> allClassNamesFromJars = new ArrayList<>()
        allClassNamesMap.put(context.getPath().replace(project.path + ":transformClassesWithCollectJarClassInfoTransformFor", ""), allClassNamesFromJars)

        inputs.each { TransformInput input ->

            input.directoryInputs.each { DirectoryInput directoryInput ->

                    def classFileDirAbsolutePath = directoryInput.file.absolutePath;

//                    println "directoryInput : " + classFileDirAbsolutePath

                    def dest = outputProvider.getContentLocation(directoryInput.name, directoryInput.contentTypes, directoryInput.scopes, Format.DIRECTORY)
                    FileUtils.copyDirectory(directoryInput.file, dest)
            }

            input.jarInputs.each {JarInput jarInput ->

                def jarName = jarInput.name;

//                println "jarInput : " + jarInput.file.absolutePath
                List<String> classNames = getClasseNamesFromJar(jarInput.file.absolutePath)
                classNames.forEach(new Consumer<String>() {
                    @Override
                    void accept(String s) {
                        allClassNamesFromJars.add(s)
                    }
                })
                def md5Name = DigestUtils.md5Hex(jarInput.file.getAbsolutePath())
                if(jarName.endsWith(".jar")){
                    jarName = jarName.substring(0, jarName.length() - 4);
                }

                def dest = outputProvider.getContentLocation(jarName + md5Name, jarInput.contentTypes, jarInput.scopes, Format.JAR)
                FileUtils.copyFile(jarInput.file, dest);
            }
        }

    }

    /** 获取指定jar包中所有的类名，写到列表中*/
    List<String> getClasseNamesFromJar(String jarAbsPath) throws IOException {
        List<String> classes = new ArrayList<String>();
        JarFile jar = new JarFile(jarAbsPath);
        Enumeration<JarEntry> files = jar.entries();
        while (files.hasMoreElements()) {
            JarEntry jarEntry = files.nextElement();
            String name = jarEntry.getName();
            if (name.matches("^.*class\$")){
                classes.add(name);
            }
        }
        return classes;
    }
}