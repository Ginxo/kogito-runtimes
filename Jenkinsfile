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
        stage('Downstream Build') {
            final SETTINGS_XML_ID = "9239af2e-46e3-4ba3-8dd6-1a814fc8a56d"
            configFileProvider([configFile(fileId: SETTINGS_XML_ID, variable: 'MAVEN_SETTINGS_XML_DOWNSTREAM')]) {
                def projectCollection = ['kogito-runtimes', 'kogito-apps', 'kogito-examples']
                treebuild.downstreamBuild(projectCollection, "${SETTINGS_XML_ID}", '-e -nsu validate -Psonarcloud-analysis', false)
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
