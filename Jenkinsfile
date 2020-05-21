@Library('jenkins-pipeline-shared-libraries')_

pipeline {
    agent {
        label 'kie-rhel7 && kie-mem16g && !master'
    }
    tools {
        maven 'kie-maven-3.6.2'
        jdk 'kie-jdk11'
    }
    options {
        buildDiscarder logRotator(artifactDaysToKeepStr: '', artifactNumToKeepStr: '', daysToKeepStr: '', numToKeepStr: '10')
        timeout(time: 90, unit: 'MINUTES')
    }
    environment {
        SONARCLOUD_TOKEN = credentials('SONARCLOUD_TOKEN')
        MAVEN_OPTS = '-Xms512m -Xmx3g'
    }
    stages {
        stage('Initialize') {
            steps {
                sh 'printenv'
            }
        }
        stage('build sh script') {
            steps {
                script {
                    mailer.buildLogScriptPR()
                }
            }
        }
        stage('Build projects') {
            steps {
                script {
                    def file =  (JOB_NAME =~ /\/[a-z,A-Z\-]*\.downstream/).find() ? 'downstream.stages' :
                                (JOB_NAME =~ /\/[a-z,A-Z\-]*\.compile/).find() ? 'compile.stages' :
                                'upstream.stages'
                    if(fileExists("$WORKSPACE/${file}")) {
                        println "File ${file} exists, loading it."
                        load("$WORKSPACE/${file}")
                    } else {
                        dir("kogito-runtimes") {
                            def changeAuthor = env.CHANGE_AUTHOR ?: env.ghprbPullAuthorLogin
                            def changeBranch = env.CHANGE_BRANCH ?: env.ghprbSourceBranch
                            def changeTarget = env.CHANGE_TARGET ?: env.ghprbTargetBranch

                            println "File ${file} does not exist. Loading the one from kogito-runtimes project. Author [${changeAuthor}], branch [${changeBranch}]..."
                            githubscm.checkoutIfExists('kogito-runtimes', "${changeAuthor}", "${changeBranch}", 'kiegroup', "${changeTarget}")
                            println "Loading ${file} file..."
                            load("${file}")
                        }
                    }
                }
            }
        }
    }
    post {
        always {
            sh '$WORKSPACE/trace.sh'
            junit '**/target/surefire-reports/**/*.xml, **/target/failsafe-reports/**/*.xml'
            echo 'Archiving logs...'
            archiveArtifacts excludes: '**/target/checkstyle.log', artifacts: '**/*.maven.log,**/target/*.log', fingerprint: false, defaultExcludes: true, caseSensitive: true, allowEmptyArchive: true

            echo 'Archiving testStatusListener and screenshots artifacts...'
            archiveArtifacts artifacts: '**/target/testStatusListener*,**/target/screenshots/**', fingerprint: false, defaultExcludes: true, caseSensitive: true, allowEmptyArchive: true
        }
        failure {
            script {
                mailer.sendEmail_failedPR()
            }
        }
        unstable {
            script {
                mailer.sendEmail_unstablePR()
            }
        }
        fixed {
            script {
                mailer.sendEmail_fixedPR()
            }
        }
        cleanup {
            cleanWs()
        }
    }
}