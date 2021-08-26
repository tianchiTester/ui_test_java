#!/usr/bin/groovy
def repositoryUrl = scm.userRemoteConfigs[0].url

pipeline {
    //agent { label 'Win10ProVM' }
    agent none
    
    environment {
        VERSION = '1.1.1.1'
        APP_NAME = 'OMEN_UI_AUTOMATION'
    }

	options
	{
      timeout(time: 10, unit: 'MINUTES')
	}

    stages{
        stage("BuildToTest"){
            agent { label 'Omen_ClientTest' }
            steps {
                echo "testing"
                // script {
                //     VERSION = VersionNumber(versionNumberString: '${BUILD_YEAR}.${BUILD_MONTH}.2.${BUILDS_THIS_MONTH}', skipFailedBuilds: true)
                // }

                //bat 'mvn clean package -DskipTests=true -DsuiteXmlFile=testng_ci_smoke.xml -DbrowserName=chrome -DbuildNumber=${BRANCH_NAME}-${VERSION}'
                bat "dir"
                bat "mvn install:install-file -Dfile=test-2.1.jar -DgroupId=Video -DartifactId=test -Dversion=2.1 -Dpackaging=jar"
                bat "mvn clean package -DskipTests=true -DsuiteXmlFile=TestSuite_SysMAT_Client.xml -DbuildNumber=${BRANCH_NAME}-${VERSION}"
                //sh('mvn install:install-file -Dfile=test-2.1.jar -DgroupId=Video -DartifactId=test -Dversion=2.1 -Dpackaging=jar')
                //sh('mvn clean package -DskipTests=true -DsuiteXmlFile=TestSuite_SysMAT_Client.xml -DbuildNumber=${BRANCH_NAME}-${VERSION}')
            }
        }
        stage("Publish"){
            when {
                expression {
                    return env.CHANGE_ID == null
                }
            }
            agent { label 'Omen_ClientTest' }
            steps {
                // script {
                //     VERSION = VersionNumber(versionNumberString: '${BUILD_YEAR}.${BUILD_MONTH}.2.${BUILDS_THIS_MONTH}', skipFailedBuilds: true)
                //     echo "${BRANCH_NAME}-${VERSION}"
                // }
                // setBuildName "${VERSION}"

                echo "Syncing to s3 bucket"
		bat "aws s3 cp target/${APP_NAME}.jar s3://psswqa-builds/${APP_NAME}/${BRANCH_NAME}/"
            }
        }
        stage("GITTAG") {
            agent { label 'Omen_ClientTest' }
            steps {
                withCredentials([usernamePassword(credentialsId: '548017e2-e8db-4e6d-90ba-ebe56afa1763', passwordVariable: 'github_password', usernameVariable: 'github_username')]) {
                        script {
                            def repourl = repositoryUrl.replaceAll("https://", "")
                            echo "Tagging Current build"
                            def git_command = "git clone https://" + github_username + ":" + github_password + "@" + repourl
                            def gitpush_command = "git push https://" + github_username + ":" + github_password + "@" + repourl + " " + "--tags"
                            git_command = git_command.replaceAll("[\n\r]", "")
                            echo "Cloing the OmenServices-UIAutomation Repo for git tagging"
                            bat "${git_command}"
                            bat "cd OmenServices-UIAutomation"
                            VERSION = VersionNumber(versionNumberString: '${BUILD_YEAR}.${BUILD_MONTH}.1.${BUILDS_THIS_MONTH}', skipFailedBuilds: false)
                            echo "${VERSION}"
                            setBuildName "${VERSION}"
                            bat "git tag '${branch_name}-${VERSION}' origin/${branch_name}"
                            bat "${gitpush_command}"
                            bat "cd .."
                          }
                }
                
            }
        post {
                    always {
                        archiveArtifacts "target/${APP_NAME}.jar"
                        deleteDir()
                    }
                }
        }
    }
}
